package fun.powercheng.url.tiny.service.impl;

import fun.powercheng.url.tiny.core.UrlShortenerFactory;
import fun.powercheng.url.tiny.model.bo.UrlShortenerResult;
import fun.powercheng.url.tiny.model.po.UrlMapping;
import fun.powercheng.url.tiny.model.vo.UrlShortenResponse;
import fun.powercheng.url.tiny.repository.UrlMappingRepository;
import fun.powercheng.url.tiny.service.UrlMappingService;
import fun.powercheng.url.tiny.util.CustomRedisTemplate;
import fun.powercheng.url.tiny.util.ProjectConstants;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import static fun.powercheng.url.tiny.util.ProjectConstants.DEFAULT_HTTP_PROTOCOL;
import static fun.powercheng.url.tiny.util.ProjectConstants.HASH_SUFFIX_VALUE;

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

    private static final String REGEX = "^(https?://)?([a-z0-9-]+\\.)+[a-z0-9]{2,}(/[^\\s]*)?$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    @Override
    public Mono<String> getUrlByShortCode(String shortCode) {
        return redisTemplate.getValue(shortCode)
                .doOnSuccess(res -> log.debug("短链缓存查询结果：shortCode: {} res: {}", shortCode, res))
                .switchIfEmpty(Mono.defer(() -> findOriginalUrlAndCache(shortCode)));
    }

    @Override
    public Mono<UrlShortenResponse> generateUrlMapping(String originalUrl) {

        if (!PATTERN.matcher(originalUrl).matches()) {
            return Mono.error(new IllegalArgumentException("请输入正确的 URL 格式"));
        }

        return addDefaultProtocolIfNeeded(originalUrl).flatMap(addedProtocolUrl ->
                urlShortenerFactory.getUrlShortener().shorten(addedProtocolUrl)
                        .doOnSuccess(genResult -> {
                            if (genResult.getCacheFlag()) {
                                log.info("Generated shortCode from cache or db! shortCode: {}; Original URL is: {}",
                                        genResult.getShortCode(), addedProtocolUrl);
                            } else {
                                log.info("Generated shortCode: {}; Original URL is: {}", genResult.getShortCode(), addedProtocolUrl);
                            }
                        })
                        .flatMap(genResult -> handleGenResult(addedProtocolUrl, genResult)));

    }


    private Mono<String> addDefaultProtocolIfNeeded(String url) {
        return Mono.defer(() -> {
            try {
                URI uri = new URI(url);
                if (uri.getScheme() == null) {
                    return Mono.just(DEFAULT_HTTP_PROTOCOL + url);
                }
                return Mono.just(url);
            } catch (URISyntaxException e) {
                return Mono.error(new IllegalArgumentException("无效的 URL 格式"));
            }
        });
    }

    private Mono<String> findOriginalUrlAndCache(String shortCode) {
        log.debug("短链查库：shortCode: {}", shortCode);
        return urlMappingRepository.findByShortCode(shortCode)
                .flatMap(urlMapping -> redisTemplate.saveValue(shortCode, urlMapping.getOriginalUrl()))
                .switchIfEmpty(Mono.defer(() -> redisTemplate.saveValue(shortCode, ProjectConstants.NOT_FOUND_404)));
    }

    private Mono<UrlShortenResponse> handleGenResult(String originalUrl, UrlShortenerResult genResult) {
        // 如果生成器已经判断过短编码在缓存中了，无需重复判断，直接返回短编码即可
        if (genResult.getCacheFlag()) {
            return Mono.just(UrlShortenResponse.builder()
                    .shortUrl(genResult.getShortCode())
                    .addedProtocolUrl(originalUrl)
                    .build());
        }
        // 不在缓存就认为短编码没生成过 对于唯一ID策略 每次都是全新的短编码 对于哈希策略 是已经处理过碰撞的短编码
        return saveAndCacheUrlMapping(genResult.getShortCode(), originalUrl)
                .onErrorResume(throwable -> {
                    if (throwable instanceof R2dbcDataIntegrityViolationException) {
                        log.warn("出现了布隆过滤器误判", throwable);
                        // 此时出现了布隆过滤器误判 / 获取短链生成器意外重复 导致数据库无法新增，所以需要重新生成url
                        return generateUrlMapping(genResult.getFinalOriginalUrl() + HASH_SUFFIX_VALUE);
                    } else {
                        log.error("新增短链异常", throwable);
                        throw new RuntimeException(throwable);
                    }
                });
    }

    private Mono<UrlShortenResponse> saveAndCacheUrlMapping(String shortCode, String originalUrl) {
        return urlMappingRepository
                .save(UrlMapping.builder()
                        .originalUrl(originalUrl)
                        .shortCode(shortCode)
                        .build())
                .map(urlMapping -> UrlShortenResponse.builder()
                        .addedProtocolUrl(originalUrl)
                        .shortUrl(shortCode)
                        .build())
                .onErrorStop()
                .flatMap(shortenResponse -> {
                    // 缓存刚生成的短链信息
                    return redisTemplate
                            .saveValue(shortenResponse.getShortUrl(), shortenResponse.getAddedProtocolUrl())
                            .doOnSuccess(res -> log.debug("已缓存 shortCode: {}; Original URL is: {}", shortCode, originalUrl))
                            .thenReturn(shortenResponse);
                });
    }

}
