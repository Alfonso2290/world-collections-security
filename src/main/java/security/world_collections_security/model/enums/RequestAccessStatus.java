package security.world_collections_security.model.enums;

import lombok.Getter;

@Getter
public enum RequestAccessStatus {
	REQUEST("REQUEST"),
	APPROVED("APPROVED"),
	REJECTED("REJECTED"),
	COMPLETED("COMPLETED");

	public final String name;

	RequestAccessStatus(String name) {
		this.name = name;
	}
}
