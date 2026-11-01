package security.world_collections_security.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestAccessDto implements Serializable {
	private Long id;
	private String user;
	private String password;
	private String role;
	private String createdOn;
	private String status;
}
