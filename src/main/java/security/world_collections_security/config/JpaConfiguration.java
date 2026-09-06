package security.world_collections_security.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("security.world_collections_security.repository")
@EntityScan("security.world_collections_security.entity.sql")
public class JpaConfiguration {
}
