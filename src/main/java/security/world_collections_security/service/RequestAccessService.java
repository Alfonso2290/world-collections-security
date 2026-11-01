package security.world_collections_security.service;

import reactor.core.publisher.Mono;
import security.world_collections_security.entity.sql.RequestAccess;
import security.world_collections_security.model.dto.RequestAccessDto;

public interface RequestAccessService {
	Mono<RequestAccess> searchRequestAccess(String userName);

	Mono<RequestAccess> saveRequestAccess(RequestAccessDto requestAccessDto);
}
