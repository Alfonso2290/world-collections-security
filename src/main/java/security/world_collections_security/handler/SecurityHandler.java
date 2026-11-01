package security.world_collections_security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import security.world_collections_security.entity.redis.SecurityToken;
import security.world_collections_security.entity.sql.RequestAccess;
import security.world_collections_security.helper.SecurityHelper;
import security.world_collections_security.model.dto.RequestAccessDto;
import security.world_collections_security.model.dto.UserAccessDto;
import security.world_collections_security.service.RedisService;
import security.world_collections_security.service.RequestAccessService;
import security.world_collections_security.service.SecurityService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static security.world_collections_security.helper.Constants.*;

@Component
@RequiredArgsConstructor
public class SecurityHandler {

	@Value("${application.time-to-live.access-token}")
	private Integer timeToLiveAccessToken;

	@Value("${application.name.url.login}")
	private String nameUrlLogin;

	@Value("${application.name.url.create-account}")
	private String nameUrlCreateAccount;

	private final SecurityService securityService;
	private final RedisService redisService;
	private final ObjectMapper objectMapper;
	private final RequestAccessService requestAccessService;

	public static final Object OBJECT_EMPTY = new Object();

	public Mono<ServerResponse> validateTokenAndRedirectRequest(ServerRequest request){
		ServerRequest.Headers headers = request.headers();
		String authorization = headers.firstHeader(HEADER_AUTHORIZATION);
		String[] accessToken = authorization.split(SPLIT_SPACE);
		String pathRelative = request.path().substring("/security/".length());

		return securityService.decryptToken(accessToken[1])
				.flatMap(responseTokenDecrypt -> {
					String[] authorizationDecrypt = responseTokenDecrypt.split(SPLIT_INTERMEDIATE_SCRIPT);
					UserAccessDto userAccessDto = new UserAccessDto(authorizationDecrypt[1], authorizationDecrypt[2], authorizationDecrypt[0]);
					if(!((nameUrlLogin.equals(pathRelative) && request.method().equals(HttpMethod.GET)) || (nameUrlCreateAccount.equals(pathRelative) && request.method().equals(HttpMethod.POST)) )){
						return validationAuthorized(userAccessDto.getUserName(), accessToken[1]);
					}
					return Mono.just(nameUrlCreateAccount.equals(pathRelative) && request.method().equals(HttpMethod.POST))
							.flatMap(isPathCreateAccount -> {
								if(Boolean.TRUE.equals(isPathCreateAccount)){
									return requestAccessService.searchRequestAccess(userAccessDto.getUserName())
											.switchIfEmpty(requestAccessService.saveRequestAccess(buildRequestAccessDto(userAccessDto)));
									//Pendiente: Si es true no deberia ir a validateTokenUser : solo debe redirecionar independientemente si registra o no en RequestAccess
								}
								return Mono.just(new RequestAccess());
							})
							.flatMap(responseRequestAccess -> this.validateTokenUser(userAccessDto, request, accessToken, pathRelative));

				})
				.switchIfEmpty(ServerResponse.badRequest().build())
				.onErrorResume(SecurityHelper::errorHandler);
	}

	private Mono<ServerResponse> validateTokenUser(UserAccessDto userAccessDto, ServerRequest request, String[] accessToken, String pathRelative ){
		return securityService.validateTokenUser(userAccessDto.getUserName(), userAccessDto.getPassword())
				.flatMap(responseRole -> saveRedisAndRedirect(request, userAccessDto, responseRole,accessToken ,pathRelative));
	}

	private RequestAccessDto buildRequestAccessDto(UserAccessDto userAccessDto){
		RequestAccessDto requestAccessDto = new RequestAccessDto();
		requestAccessDto.setCreatedOn(null); // Pendiente actualizar
		requestAccessDto.setPassword(userAccessDto.getPassword());
		requestAccessDto.setRole(userAccessDto.getRole());
		requestAccessDto.setStatus("REQUEST"); // Pendiente actualizar: ponerlo en Util
		requestAccessDto.setUser(userAccessDto.getUserName());
		return requestAccessDto;
	}

	private Mono<ServerResponse> validationAuthorized(String userName, String accessToken){
		try {
			String key = SecurityHelper.hash(userName.concat(accessToken));
			String responseRedis = redisService.get(key);
			if(responseRedis.isEmpty()){
				return ServerResponse.status(HttpStatus.UNAUTHORIZED)
						.bodyValue(SecurityHelper.buildMapMessage(HttpStatus.UNAUTHORIZED.value() + "", "User UnAuthorized"));
			}
			SecurityToken redisEntity = objectMapper.readValue(responseRedis, SecurityToken.class);
			if(!SecurityHelper.calculateTimeOfLiveToken(redisEntity.getCreatedOn(), timeToLiveAccessToken)){
				return ServerResponse.status(HttpStatus.UNAUTHORIZED)
						.bodyValue(SecurityHelper.buildMapMessage(HttpStatus.UNAUTHORIZED.value() + "", "User UnAuthorized"));
			}
		} catch (Exception e) {
			return ServerResponse.status(HttpStatus.CONFLICT).
					bodyValue(SecurityHelper.buildMapMessage(HttpStatus.CONFLICT.getReasonPhrase(), "Error in Hash or conversion json"));
		}
		return Mono.empty();
	}

	public Mono<ServerResponse> saveRedisAndRedirect(ServerRequest request, UserAccessDto userAccessDto, String responseRole,String[] accessToken, String pathRelative){
		if (!responseRole.isEmpty()) {
			if (responseRole.equals(userAccessDto.getRole())) {
				String user = userAccessDto.getUserName();
				String token = accessToken[1];
				Boolean flagLogin = nameUrlLogin.equals(pathRelative) && request.method().equals(HttpMethod.GET);
				return this.saveRedis(user, token, flagLogin).flatMap(responseSaveRedis -> {
					if(responseSaveRedis.isEmpty()){
						return ServerResponse.status(HttpStatus.CONFLICT)
								.bodyValue(SecurityHelper.buildMapMessage(HttpStatus.CONFLICT.getReasonPhrase(), "Failed in save Redis"));
					}
					return this.redirectRequest(request,pathRelative);
				});
			}
		}
		return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(SecurityHelper.buildMapMessage(
				"Error", "The user does not have permissions"));
	}

	//TODO Nunca llega a este onErrorResume -> arreglar
	public Mono<ServerResponse> redirectRequest(ServerRequest request, String relativePath){
		return securityService.redirectRequest(request, relativePath, request.queryParams(), OBJECT_EMPTY)
				.flatMap(object -> {
					if(object == OBJECT_EMPTY) {
						return ServerResponse.ok().build();
					}
					return ServerResponse.ok().bodyValue(object);
				})
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(error -> {
					WebClientResponseException errorClient = (WebClientResponseException) error;
					if(errorClient.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
						return ServerResponse.status(HttpStatus.CONFLICT)
								.bodyValue(SecurityHelper.buildMapMessage(errorClient.getStatusCode().toString(), errorClient.getMessage()));
					}
					return Mono.error(errorClient);
				});
	}

	public Mono<String> saveRedis(String user, String accessToken, Boolean flag){
		return Mono.just(new SecurityToken())
			.flatMap(entityRedis -> {
				try {
					String json = "default";
					if(Boolean.TRUE.equals(flag)) {
						entityRedis.setUser(user);
						entityRedis.setToken(accessToken);
						entityRedis.setCreatedOn(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
						entityRedis.setId(UUID.randomUUID().toString());

						String key = SecurityHelper.hash(entityRedis.getUser().concat(entityRedis.getToken()));
						json = objectMapper.writeValueAsString(entityRedis);

						redisService.save(key, json);
					}
					return Mono.just(json);
				} catch (Exception e) {
					return Mono.empty();
				}
			});
	}
}
