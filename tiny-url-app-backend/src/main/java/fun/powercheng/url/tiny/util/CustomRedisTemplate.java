package fun.powercheng.url.tiny.util;

import fun.powercheng.url.tiny.config.CacheConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static fun.powercheng.url.tiny.util.ProjectConstants.REDIS_KEY_APP_PREFIX;

/**
 * Created by PowerCheng on 2025/1/2.
 */
@Component
@RequiredArgsConstructor
public class CustomRedisTemplate {

    private final ReactiveStringRedisTemplate redisTemplate;

    private final CacheConfig cacheConfig;

    private Duration configDuration;

    @PostConstruct
    public void init() {
        configDuration = Duration.of(cacheConfig.getCacheDuration(), cacheConfig.getDurationUnit());
    }

    /**
     * 缓存 key 返回 value，使用默认的 fun.powercheng.url.tiny.config.CacheConfig 配置的过期时长
     *
     * @param key   redis key
     * @param value redis value
     * @return 刚刚缓存的 value
     */
    public Mono<String> saveValue(String key, String value) {
        return redisTemplate.opsForValue().set(REDIS_KEY_APP_PREFIX + key, value, configDuration)
                .thenReturn(value);
    }

    public Mono<String> saveValue(String key, String value, Duration duration) {
        return redisTemplate.opsForValue().set(REDIS_KEY_APP_PREFIX + key, value, duration)
                .thenReturn(value);
    }

    /**
     * 从 redis 中根据 key 获取 value
     * 每次获取都会自动为key自动续期 续期时间使用配置的 cache duration
     *
     * @param key key 值
     * @return 值
     */
    public Mono<String> getValue(String key) {
        return redisTemplate.opsForValue().getAndExpire(REDIS_KEY_APP_PREFIX + key, configDuration);
    }

    public Mono<Boolean> deleteValue(String key) {
        return redisTemplate.opsForValue().delete(REDIS_KEY_APP_PREFIX + key);
    }
}
