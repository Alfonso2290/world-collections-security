package security.world_collections_security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
						.path("/token/decrypt")
						.queryParam("token", authorization)
						.build())
				.accept(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
				.contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
				//.exchangeToMono(response -> response.bodyToMono(String.class));
				.retrieve().bodyToMono(String.class);
	}

	@Override
	public Mono<String> validateUser(String user, String password){
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
