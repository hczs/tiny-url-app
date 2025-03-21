package fun.powercheng.url.tiny.core;

import com.github.yitter.idgen.YitIdHelper;
import fun.powercheng.url.tiny.model.bo.UrlShortenerResult;
import fun.powercheng.url.tiny.util.Base62Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 生成全局唯一ID，再对ID做base62转换
 * 无需考虑哈希碰撞问题
 * Created by PowerCheng on 2024/12/29.
 */
@Slf4j
@Component("UniqueIdWithBase62")
public class UniqueIdWithBase62Shortener implements UrlShortener {

    @Override
    public Mono<UrlShortenerResult> shorten(String url) {
        return Mono.fromCallable(() -> {
            long uniqueId = YitIdHelper.nextId();
            log.debug("UniqueIdWithBase62Shortener generated uniqueId: {}", uniqueId);
            return UrlShortenerResult.builder()
                    .finalOriginalUrl(url)
                    .shortCode(Base62Util.encode(uniqueId))
                    .build();
        });
    }

}
