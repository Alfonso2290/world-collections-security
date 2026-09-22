package security.world_collections_security.entity.sql;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "UserAccess", schema = "dbo")
@Getter
@Setter
public class UserAccess {
	@Id
	private Long id;
	@Column("username")
	private String userName;
	private String password;
	private String role;

}
