package fun.powercheng.url.tiny.service.impl;

import fun.powercheng.url.tiny.core.UrlShortener;
import fun.powercheng.url.tiny.core.UrlShortenerFactory;
import fun.powercheng.url.tiny.model.po.UrlMapping;
import fun.powercheng.url.tiny.repository.UrlMappingRepository;
import fun.powercheng.url.tiny.service.UrlMappingService;
import fun.powercheng.url.tiny.util.CustomRedisTemplate;
import fun.powercheng.url.tiny.util.ProjectConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Created by PowerCheng on 2024/12/28.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UrlMappingServiceImpl implements UrlMappingService {

    private final UrlMappingRepository urlMappingRepository;

    private final UrlShortenerFactory urlShortenerFactory;

    private final CustomRedisTemplate redisTemplate;

    @Override
    public Mono<String> getUrlByShortCode(String shortCode) {
        return redisTemplate.getValue(shortCode)
                .doOnSuccess(res -> log.debug("短链缓存查询结果：shortCode: {} res: {}", shortCode, res))
                .flatMap(res ->
                        Optional.ofNullable(res)
                                .map(Mono::just)
                                .orElseGet(() -> findOriginalUrlAndCache(shortCode)));
    }

    @Override
    public Mono<String> generateUrlMapping(String originalUrl) {
        return Mono.fromCallable(() -> {
                    UrlShortener urlShortener = urlShortenerFactory.getUrlShortener();
                    return urlShortener.shorten(originalUrl);
                })
                .doOnSuccess(shortCode -> {
                    log.info("Generated shortCode: {}; Original URL is: {}", shortCode, originalUrl);
                })
                .flatMap(shortCode -> saveAndCacheUrlMapping(shortCode, originalUrl));
    }

    private Mono<String> saveAndCacheUrlMapping(String shortCode, String originalUrl) {
        return urlMappingRepository
                .save(UrlMapping.builder()
                        .originalUrl(originalUrl)
                        .shortCode(shortCode)
                        .build())
                .flatMap(urlMapping -> {
                    // 缓存刚生成的短链信息
                    return redisTemplate
                            .saveValue(urlMapping.getShortCode(), urlMapping.getOriginalUrl())
                            .doOnSuccess(res -> {
                                log.debug("已缓存 shortCode: {}; Original URL is: {}", shortCode, originalUrl);
                            })
                            .thenReturn(shortCode);
                });
    }

    private Mono<String> findOriginalUrlAndCache(String shortCode) {
        log.debug("短链查库：shortCode: {}", shortCode);
        return urlMappingRepository.findByShortCode(shortCode)
                .flatMap(urlMapping -> Optional.ofNullable(urlMapping)
                        .map(mapping -> {
                            // 如果存在就缓存然后返回
                            return redisTemplate.saveValue(shortCode, mapping.getOriginalUrl());
                        })
                        .orElseGet(() -> {
                            // 不存在的话 缓存异常请求到404 防止重复查库
                            return redisTemplate.saveValue(shortCode, ProjectConstants.NOT_FOUND_404);
                        })
                );
    }

}
