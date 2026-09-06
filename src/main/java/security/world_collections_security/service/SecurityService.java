package security.world_collections_security.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface SecurityService {
	Mono<ResponseEntity<byte[]>> redirect(String url,HttpMethod method,HttpHeaders headers,byte[] body);
}
