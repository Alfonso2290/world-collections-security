package security.world_collections_security.service.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SecurityServiceImpl {

	private final WebClient webClient;

	public SecurityServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	//Response Generico
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
	}
}
