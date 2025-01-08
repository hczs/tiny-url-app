package fun.powercheng.url.tiny.service;

import fun.powercheng.url.tiny.model.vo.UrlShortenResponse;
import reactor.core.publisher.Mono;

/**
 * Created by PowerCheng on 2024/12/28.
 */
public interface UrlMappingService {

    /**
     * 根据缩短后的网址编码找到原始网址
     *
     * @param shortCode 短网址编码
     * @return 原始网址
     */
    Mono<String> getUrlByShortCode(String shortCode);

    /**
     * 根据原始 URL 生成 urlMapping 记录，并返回短网址编码
     *
     * @param originalUrl 原始网址
     * @return 短网址编码
     */
    Mono<UrlShortenResponse> generateUrlMapping(String originalUrl);

}
