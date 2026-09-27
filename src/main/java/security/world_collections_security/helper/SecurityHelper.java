package security.world_collections_security.helper;

import org.springframework.http.HttpStatus;
import org.springframework.r2dbc.BadSqlGrammarException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

public class SecurityHelper {

	public static Mono<ServerResponse> errorHandler(Throwable error){
		if(error instanceof BadSqlGrammarException){
			BadSqlGrammarException exception = (BadSqlGrammarException) error;
			return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(buildMapMessage(exception.getSql(),
					"Database Error: " + exception.getMessage()));
		}

		if(error instanceof WebClientResponseException){
			WebClientResponseException exception = (WebClientResponseException) error;
			return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(buildMapMessage(
					exception.getStatusText() + "(" + exception.getStatusCode() + ")",
					"Invalid Token: " + error.getMessage()
			));
		}

		return Mono.error(error);
	}
	public static Map<String, Object> buildMapMessage(String status, String message){
		Map<String, Object> map = new HashMap<>();
		map.put("Status", status);
		map.put("Message", message);
		map.put("Timestamp", new Date());
		return map;
	}

	public static String hash(String value) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
		return HexFormat.of().formatHex(hash);
	}

	public static Boolean calculateTimeOfLiveToken(String dateTime, Integer timeLiveToken){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
		LocalDateTime localDateTimeNow = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);

		long differentDateTimeInSeconds = Duration.between(localDateTime, localDateTimeNow).toSeconds();
		if(timeLiveToken > differentDateTimeInSeconds){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
