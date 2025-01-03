package fun.powercheng.url.tiny.config;

import fun.powercheng.url.tiny.enums.ShortenerTypeEnum;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by PowerCheng on 2024/12/29.
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "tiny-url-app")
public class TinyUrlAppConfig {

    private ShortenerTypeEnum shortenerType;

    private Short workerId;

    @PostConstruct
    public void init() {

        if (shortenerType == null) {
            log.warn("检测到未配置缩短策略 [tiny-url-app.shortener-type]，将使用默认的 UNIQUE_ID_WITH_BASE62 网址缩短策略");
            shortenerType = ShortenerTypeEnum.UNIQUE_ID_WITH_BASE62;
        }

        if (shortenerType == ShortenerTypeEnum.UNIQUE_ID_WITH_BASE62 && workerId == null) {
            log.error("使用 UNIQUE_ID_WITH_BASE62 策略时，请配置 [tiny-url-app.worker-id]");
        }
    }
}
