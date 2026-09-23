package security.world_collections_security.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ServiceEnum {

	WORLD_CONTROL_COLLECTIONS("world-control-collections", "8081"),
	WORLD_COLLECTIONS_TOKEN("world-collections-token", "8082"),
	WORLD_COLLECTIONS_SECCURITY("world-collections-security", "8083");

	public final String name;
	public final String port;


	ServiceEnum(String name, String port) {
		this.name = name;
		this.port = port;
	}

	public static String getValuePortFromName(String name){
		return Arrays.stream(ServiceEnum.values())
				.filter(serviceEnum -> serviceEnum.getName().equals(name))
				.findFirst()
				.map(ServiceEnum::getPort)
				.orElse("");
	}

}


