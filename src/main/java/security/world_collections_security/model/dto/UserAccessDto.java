package security.world_collections_security.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccessDto {

	private Long id;
	private String userName;
	private String password;
	private String role;
	private String validate;

	public UserAccessDto(String userName, String password, String role) {
		this.userName = userName;
		this.password = password;
		this.role = role;
	}

	public UserAccessDto() {
	}
}
