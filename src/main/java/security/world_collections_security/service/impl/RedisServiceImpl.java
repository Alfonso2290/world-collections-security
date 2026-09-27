package security.world_collections_security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import security.world_collections_security.service.RedisService;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

	@Value("${application.time-to-live.redis}")
	private Integer timeToLiveRedis;

	private final StringRedisTemplate redisTemplate;

	@Override
	public void save(String key, String value) {

		redisTemplate.opsForValue().set(
				key, value, Duration.ofMinutes(timeToLiveRedis));
	}

	@Override
	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public void delete(String key) {
		redisTemplate.delete(key);
	}
}
