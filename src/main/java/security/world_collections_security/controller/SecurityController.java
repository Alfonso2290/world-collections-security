package security.world_collections_security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/*@RestController
@RequestMapping("/security")
@RequiredArgsConstructor*/
public class SecurityController {

	/*@PostMapping("/enrout/{path-backend}")
	public Mono<ResponseEntity<byte[]>> request(
			@PathVariable("path-backend") String backend, //aca seria todo el path completo ejemplo "user/validate/user"
			ServerHttpRequest request,
			@RequestBody(required = false) byte[] body) {

		HttpHeaders headers = request.getHeaders();

		String token = headers.getFirst(HttpHeaders.AUTHORIZATION);

		// 1. Validar token
		// 2. Obtener usuario / role
		// 3. Determinar backend
		// 4. Reenviar petición

		return null;
	}*/
}
