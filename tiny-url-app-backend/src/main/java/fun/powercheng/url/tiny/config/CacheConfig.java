package fun.powercheng.url.tiny.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.naming.ConfigurationException;
import java.time.temporal.ChronoUnit;

/**
 * Created by PowerCheng on 2025/1/4.
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "tiny-url-app.cache-config")
@Configuration
public class CacheConfig {

    /**
     * 缓存时长
     */
    private Long cacheDuration;

    /**
     * 缓存时长单位
     */
    private ChronoUnit durationUnit;

    @PostConstruct
    public void init() throws ConfigurationException {
        if (cacheDuration == null && durationUnit == null) {
            log.warn("检测到未配置 [tiny-url-app.cache-config.cache-duration] 和 [tiny-url-app.cache-config.duration-unit] " +
                    " 将使用默认值 7 days");
            cacheDuration = 7L;
            durationUnit = ChronoUnit.DAYS;
        } else if (cacheDuration == null || durationUnit == null) {
            log.error("[tiny-url-app.cache-config.cache-duration] 和 [tiny-url-app.cache-config.duration-unit] 两个配置项" +
                    "必须同时设置");
            throw new ConfigurationException("请配置完整 tiny-url-app.cache-config");
        } else {
            log.info("当前缓存默认过期时间为 {} {}", cacheDuration, durationUnit);
        }
    }
}
