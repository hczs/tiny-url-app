package fun.powercheng.url.tiny;

import fun.powercheng.url.tiny.util.CustomRedisTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
class TinyUrlAppBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private CustomRedisTemplate redisTemplate;

    @Test
    void testRedisTemplate() {
        String testKey = "a";
        String testValue = "1";
        redisTemplate.saveValue(testKey, testValue, Duration.ofHours(1)).subscribe(res -> {
            redisTemplate.getValue(testKey).subscribe(getRes -> {
                Assertions.assertEquals(getRes, res);
                redisTemplate.deleteValue(testKey).subscribe(Assertions::assertTrue);
            });
        });
    }

}
