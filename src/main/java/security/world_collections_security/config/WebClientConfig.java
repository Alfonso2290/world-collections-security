package security.world_collections_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	/*@Bean
	public WebClient webClient() {
		return WebClient.builder().build();
	}*/

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder.build();
	}

	/*@Bean
	public WebClient webClient() {
		return WebClient.create("http://world.local:8082");
		//return WebClient.create("http://localhost:8082");
	}*/
}