package security.world_collections_security.service;

import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public interface SecurityService {
	//Mono<ResponseEntity<byte[]>> redirect(String url,HttpMethod method,HttpHeaders headers,byte[] body);

	Mono<String> decryptToken(String authorization);

	Mono<String> validateTokenUser(String user, String password);

	Mono<Object> redirectRequest(ServerRequest request, String pathService, MultiValueMap<String, String> queryParams);
}
