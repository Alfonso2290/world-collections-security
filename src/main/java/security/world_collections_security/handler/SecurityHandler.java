package security.world_collections_security.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import security.world_collections_security.helper.SecurityHelper;
import security.world_collections_security.model.dto.UserAccessDto;
import security.world_collections_security.service.SecurityService;

import static security.world_collections_security.helper.Constants.*;

@Component
@RequiredArgsConstructor
public class SecurityHandler {

	private final SecurityService securityService;

	public Mono<ServerResponse> validateTokenAndRedirectRequest(ServerRequest request){
		ServerRequest.Headers headers = request.headers();
		String authorization = headers.firstHeader(HEADER_AUTHORIZATION);
		String[] accessToken = authorization.split(SPLIT_SPACE);

		return securityService.decryptToken(accessToken[1])
				.flatMap(responseTokenDecrypt -> {
					String[] authorizationDecrypt = responseTokenDecrypt.split(SPLIT_INTERMEDIATE_SCRIPT);
					UserAccessDto userAccessDto = new UserAccessDto(authorizationDecrypt[1], authorizationDecrypt[2], authorizationDecrypt[0]);
					return securityService.validateTokenUser(userAccessDto.getUserName(), userAccessDto.getPassword())
							.flatMap(responseRole -> {
								if (!responseRole.isEmpty()) {
									if (responseRole.equals(userAccessDto.getRole())) {
										return redirectRequest(request);
									}
								}
								return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(SecurityHelper.buildMapMessage(
										"Error", "The user does not have permissions"));
							});
				})
				.switchIfEmpty(ServerResponse.badRequest().build())
				.onErrorResume(SecurityHelper::errorHandler);
	}

	//Hacerlo más genérico: envié siempre el relativePath
	public Mono<ServerResponse> redirectRequest(ServerRequest request){
		String relativePath = request.path().substring("/security/".length());
		switch (relativePath) {
			case "user/validate/user" -> {
				return validateUser(request, relativePath);
			}
			case "others" -> {
				return ServerResponse.noContent().build();
			}
			default -> {
				return ServerResponse.notFound().build();
			}
		}
	}

	Mono<ServerResponse> validateUser(ServerRequest request, String pathService){
//		System.out.println("User Handler: " + request.queryParams().get("user").get(0));
//		System.out.println("Password Handler: " + request.queryParams().get("password").get(0));
		//TODO Los parametros deben ser genéricos, no dependiendo de un endpoint en especifico
		return securityService.validateUser(
						request.queryParams().get("user").get(0),
						request.queryParams().get("password").get(0),
						pathService)
				.flatMap(object -> {
					System.out.println("Object: " + object.toString()); // Poner en logger.info
					return ServerResponse.ok().bodyValue(object);
				})
				.onErrorResume(error -> {
					WebClientResponseException errorClient = (WebClientResponseException) error;
					if(errorClient.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
						return ServerResponse.status(HttpStatus.CONFLICT)
								.bodyValue(SecurityHelper.buildMapMessage(errorClient.getStatusCode().toString(), errorClient.getMessage()));
					}
					return Mono.error(errorClient);
				});
	}
}
