package fun.powercheng.url.tiny.core;

import fun.powercheng.url.tiny.model.bo.UrlShortenerResult;
import reactor.core.publisher.Mono;

/**
 * Created by PowerCheng on 2024/12/29.
 */
public interface UrlShortener {

    Mono<UrlShortenerResult> shorten(String url);
}
