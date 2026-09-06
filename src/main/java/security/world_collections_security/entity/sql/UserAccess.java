package security.world_collections_security.entity.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UserAccess", schema = "dbo")
@Getter
@Setter
public class UserAccess {
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "[user]")
	private String user;
	private String password;
	private String role;

}
