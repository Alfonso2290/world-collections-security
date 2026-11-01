package security.world_collections_security.entity.sql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "RequestAccess", schema = "dbo")
public class RequestAccess {
	@Id
	private Long id;
	private String user;
	private String password;
	private String role;
	private String createdOn;
	private String status;
}
