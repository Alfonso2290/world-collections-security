package security.world_collections_security.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import security.world_collections_security.entity.sql.RequestAccess;
import security.world_collections_security.model.dto.RequestAccessDto;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestAccessMapper {

	RequestAccessMapper MAPPER = Mappers.getMapper(RequestAccessMapper.class);

	RequestAccess toRequestAccess(RequestAccessDto requestAccessDto);
}
