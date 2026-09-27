package security.world_collections_security.entity.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class SecurityToken {
	@Id
	private String id;

	private String token;
	private String user;
	private String createdOn;
}
