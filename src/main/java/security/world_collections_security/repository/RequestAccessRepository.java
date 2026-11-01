package security.world_collections_security.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import security.world_collections_security.entity.sql.RequestAccess;

@Repository
public interface RequestAccessRepository extends ReactiveCrudRepository<RequestAccess, Long> {
	Mono<RequestAccess> findByUser(String userName);
}
