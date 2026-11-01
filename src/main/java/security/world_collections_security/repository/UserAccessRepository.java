package security.world_collections_security.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import security.world_collections_security.entity.sql.UserAccess;

@Repository
public interface UserAccessRepository extends ReactiveCrudRepository<UserAccess, Long> {

	Mono<UserAccess> findFirstByUserNameAndPassword(String user, String password);
}
