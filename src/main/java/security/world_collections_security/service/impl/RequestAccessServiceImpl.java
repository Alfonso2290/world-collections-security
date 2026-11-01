package security.world_collections_security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import security.world_collections_security.entity.sql.RequestAccess;
import security.world_collections_security.mapper.RequestAccessMapper;
import security.world_collections_security.model.dto.RequestAccessDto;
import security.world_collections_security.repository.RequestAccessRepository;
import security.world_collections_security.service.RequestAccessService;

@Service
@RequiredArgsConstructor
public class RequestAccessServiceImpl implements RequestAccessService {

	private final RequestAccessRepository requestAccessRepository;


	@Override
	public Mono<RequestAccess> searchRequestAccess(String userName) {
		return requestAccessRepository.findByUser(userName);
	}

	@Override
	public Mono<RequestAccess> saveRequestAccess(RequestAccessDto requestAccessDto) {
		return requestAccessRepository.save(RequestAccessMapper.MAPPER.toRequestAccess(requestAccessDto));
	}
}
