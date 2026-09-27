package security.world_collections_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WorldCollectionsSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorldCollectionsSecurityApplication.class, args);
	}

}
