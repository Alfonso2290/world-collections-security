package security.world_collections_security.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import security.world_collections_security.entity.sql.UserAccess;


public interface UserAccessRepository extends ReactiveCrudRepository<UserAccess, Long> {

	Mono<UserAccess> findFirstByUserNameAndPassword(String user, String password);
}
