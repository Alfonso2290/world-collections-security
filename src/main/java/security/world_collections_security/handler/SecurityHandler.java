package security.world_collections_security.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
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

	public Mono<ServerResponse> validateToken(ServerRequest request){
		ServerRequest.Headers headers = request.headers();
		String authorization = headers.firstHeader(HEADER_AUTHORIZATION);
		String[] accessToken = authorization.split(SPLIT_SPACE);

		return securityService.decryptToken(accessToken[1])
				.flatMap(responseTokenDecrypt -> {
					String[] authorizationDecrypt = responseTokenDecrypt.split(SPLIT_INTERMEDIATE_SCRIPT);
					UserAccessDto userAccessDto = new UserAccessDto(authorizationDecrypt[1], authorizationDecrypt[2], authorizationDecrypt[0]);
					return securityService.validateUser(userAccessDto.getUserName(), userAccessDto.getPassword())
							.flatMap(responseRole -> {
								if (!responseRole.isEmpty()) {
									if (responseRole.equals(userAccessDto.getRole())) {
										return ServerResponse.ok().bodyValue(SecurityHelper.buildMapMessage(
												"Success", "The user have permissions"));
									}
								}
								return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(SecurityHelper.buildMapMessage(
										"Error", "The user does not have permissions"));
							});
				})
				.switchIfEmpty(ServerResponse.badRequest().build())
				.onErrorResume(SecurityHelper::errorHandler);
	}
}
