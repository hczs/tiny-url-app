package fun.powercheng.url.tiny.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Created by PowerCheng on 2025/1/2.
 */
@Component
@RequiredArgsConstructor
public class CustomRedisTemplate {

    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<String> saveValue(String key, String value, Duration duration) {
        return redisTemplate.opsForValue().set(key, value, duration)
                .thenReturn(value);
    }

    public Mono<String> getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> deleteValue(String key) {
        return redisTemplate.opsForValue().delete(key);
    }
}
