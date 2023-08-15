package hu.webuni.userservice.security;

import io.lettuce.core.RedisConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    private final RedisTemplate<String, String> redisTemplate;


    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValueWithExpiration(String key, String value, long expirationSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, expirationSeconds, TimeUnit.SECONDS);
        } catch (RedisConnectionException re) {
            logger.error("An error occured while connecting to redis. message: {}", re.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getValueFromRedis(String redisKey) {
        return redisTemplate.opsForValue().get(redisKey);
    }

    public void deleteFromRedis(String redisKey) {
        redisTemplate.delete(redisKey);
    }

}
