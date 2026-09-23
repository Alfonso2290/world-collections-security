package security.world_collections_security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import security.world_collections_security.repository.UserAccessRepository;
import security.world_collections_security.service.SecurityService;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

	private final WebClient client;
	private final UserAccessRepository userAccessRepository;

	@Override
	public Mono<String> decryptToken(String authorization) {
		return client.post()
				.uri(uriBuilder -> uriBuilder
						.scheme("http")
						.host("world.local") //TODO cambiar a world-collections-token-service-private cuando pruebe desde kubernetes
						.port(8082)
						.path("/token/decrypt")
						.queryParam("token", authorization)
						.build())
				.accept(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
				.contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
				.retrieve().bodyToMono(String.class);
	}

	@Override
	public Mono<String> validateTokenUser(String user, String password){
		return userAccessRepository.findFirstByUserNameAndPassword(user, password)
				.flatMap(userAccess -> {
					if(userAccess.getUserName().equals(user) && userAccess.getPassword().equals(password)) {
						return Mono.just(userAccess.getRole());
					}
					else {
						return Mono.empty();
					}
				});
	}

	//TODO Ver logica para dinamizar el puerto, ya que cada microservicio en mi kubernetes va con puerto distinto
	@Override
	public Mono<Object> redirectRequest(ServerRequest request, String pathService, MultiValueMap<String, String> queryParams) {
		return	request.bodyToMono(String.class)
					.defaultIfEmpty("")
					.flatMap(body-> {
						WebClient.RequestBodySpec requestBodySpec = client.method(request.method())
						.uri(uriBuilder -> {
							queryParams.forEach(uriBuilder::queryParam);
							return uriBuilder
								.scheme("http")
								.host("localhost") //TODO cambiar a world-control-collections-service-private cuando pruebe desde kubernetes
								.port(8083) //TODO pendiente de dinamizar //cambiar a 8081 cuando pruebe desde kubernetes
								.path("/" + pathService)
								.build();
						})
						.headers(headers -> headers.addAll(request.headers().asHttpHeaders()));
						if(body.isEmpty()){
							return requestBodySpec
									.retrieve()
									.bodyToMono(Object.class);
							}
						return requestBodySpec
							.bodyValue(body)
							.accept(MediaType.APPLICATION_JSON)
							.retrieve()
							.bodyToMono(Object.class);
					});
	}

	/*//Response Generico
	public Mono<ResponseEntity<byte[]>> redirect(
			String url, //path-variable
			HttpMethod method,//POST, GET
			HttpHeaders headers,
			byte[] body) {

		return webClient
				.method(method)
				.uri(url)
				.headers(httpHeaders -> {
					httpHeaders.addAll(headers);
				})
				.bodyValue(body != null ? body : new byte[0])
				.exchangeToMono(response ->
						response.bodyToMono(byte[].class)
								.defaultIfEmpty(new byte[0])
								.map(responseBody -> {

									HttpHeaders responseHeaders =
											new HttpHeaders();

									responseHeaders.putAll(
											response.headers()
													.asHttpHeaders()
									);

									return ResponseEntity
											.status(response.statusCode())
											.headers(responseHeaders)
											.body(responseBody);
								})
				);
	}*/
}
