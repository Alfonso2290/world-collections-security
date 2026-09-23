package security.world_collections_security.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import security.world_collections_security.handler.SecurityHandler;

@Configuration
public class RouterFunctionConfig {

	@Bean
	RouterFunction<ServerResponse> routerConfig(SecurityHandler handler){
		return RouterFunctions.route(
				RequestPredicates.GET("/security/**"), handler::validateTokenAndRedirectRequest)
				.andRoute(RequestPredicates.POST("/security/**"), handler::validateTokenAndRedirectRequest)
				.andRoute(RequestPredicates.PUT("/security/**"), handler::validateTokenAndRedirectRequest)
				.andRoute(RequestPredicates.DELETE("/security/**"), handler::validateTokenAndRedirectRequest);
	}
}
