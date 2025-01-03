package fun.powercheng.url.tiny.core;

import fun.powercheng.url.tiny.config.TinyUrlAppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by PowerCheng on 2024/12/29.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UrlShortenerFactory {

    private final Map<String, UrlShortener> urlShortenerMap;

    private final TinyUrlAppConfig appConfig;

    public UrlShortener getUrlShortener() {
        log.debug("Current url shortener type is: {}", appConfig.getShortenerType());
        return urlShortenerMap.get(appConfig.getShortenerType().getTypeName());
    }
}
