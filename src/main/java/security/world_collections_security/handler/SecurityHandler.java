package security.world_collections_security.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.r2dbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import security.world_collections_security.model.dto.UserAccessDto;
import security.world_collections_security.service.SecurityService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SecurityHandler {

	private final SecurityService securityService;

	public Mono<ServerResponse> validateToken(ServerRequest request){
		ServerRequest.Headers headers = request.headers();
		String authorization = headers.firstHeader("Authorization");
		String[] accessToken = authorization.split(" ");

		Map<String, Object> map = new HashMap<>();

		//Refactorizar
		//customizar mejor mensajes de error
		return securityService.decryptToken(accessToken[1])
				.flatMap(responseTokenDecrypt -> {
					String[] authorizationDecrypt = responseTokenDecrypt.split("-");
					UserAccessDto userAccessDto = new UserAccessDto(authorizationDecrypt[1], authorizationDecrypt[2], authorizationDecrypt[0]);
					return securityService.validateUser(userAccessDto.getUserName(), userAccessDto.getPassword())
							.flatMap(responseRole -> {
								if (!responseRole.isEmpty()) {
									if (responseRole.equals(userAccessDto.getRole())) {
										map.put("Status", "Success");
										map.put("Message", "El usuario tiene permisos");
										map.put("Timestap", new Date());
										return ServerResponse.ok().bodyValue(map);
									}
								}
								map.put("Status", "Error");
								map.put("Message", "El usuario no tiene permisos");
								map.put("Timestap", new Date());
								return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(map);
							});
				})
				.switchIfEmpty(ServerResponse.badRequest().build())
				.onErrorResume(error -> {
					if(error instanceof BadSqlGrammarException){ //Poner una Exception general para todos los errores de BD
						BadSqlGrammarException exception = (BadSqlGrammarException) error;
						map.put("Status", exception.getSql());
						map.put("Message", "Error en BD: " + exception.getMessage());
						map.put("Timestap", new Date());
						map.put("Error", exception.getLocalizedMessage());
						return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(map);
					}

					if(error instanceof WebClientResponseException){
						WebClientResponseException exception = (WebClientResponseException) error;
						map.put("Status", exception.getStatusText() + "(" + exception.getStatusCode() + ")");
						map.put("Message", "Token invalido: " + error.getMessage());
						map.put("Timestap", new Date());
						return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(map);
						//return ServerResponse.badRequest().bodyValue(exception.getResponseBodyAsString());
					}

					return Mono.error(error);
				});
	}
}
