package security.world_collections_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

	@Bean
	public CorsWebFilter corsWebFilter() {

		CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOrigins(List.of("http://world.local"));
		config.setAllowedMethods(List.of(
				"GET",
				"POST",
				"PUT",
				"DELETE",
				"OPTIONS"
		));
		config.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source =
				new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", config);

		return new CorsWebFilter(source);
	}
}
