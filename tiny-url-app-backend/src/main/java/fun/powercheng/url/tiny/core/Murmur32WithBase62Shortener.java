package fun.powercheng.url.tiny.core;

import com.google.common.hash.Hashing;
import fun.powercheng.url.tiny.model.bo.UrlShortenerResult;
import fun.powercheng.url.tiny.repository.UrlMappingRepository;
import fun.powercheng.url.tiny.service.BloomFilterService;
import fun.powercheng.url.tiny.util.Base62Util;
import fun.powercheng.url.tiny.util.CustomRedisTemplate;
import fun.powercheng.url.tiny.util.ProjectConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 对长网址直接做32位哈希，32位哈希转int，再进行 base62 转换
 * <p>
 * 整体逻辑
 * <p>
 * 1. 哈希 -> base62 -> 短链编码
 * <p>
 * 2. 短链是否存在于布隆过滤器
 * <p>
 * 2.1 若存在于布隆过滤器中：再判断是否存在redis中
 * <p>
 * 2.1.1 如果存在于redis，那么就代表生成过此短链，直接返回短链编码
 * <p>
 * 2.1.2 如果不存在redis，考虑到redis中key会过期的情况，就需要到数据库查询一下
 * （这里查库不会很频繁，因为但凡查询一次数据库，都会缓存值，而且走到这一步的一般是两种情况 一是相同长链key过期再次生成，二是哈希碰撞）
 * <p>
 * 2.1.2.1 如果存在数据库中，那么可以直接返回此短链编码，并进行缓存
 * <p>
 * 2.1.2.2 如果不存在于数据库中，那就代表是发生了哈希碰撞，产生了一个相同的哈希，加上固定后缀，回到第 1 步，重新编码判断
 * <p>
 * 2.2 若不存在，将短链添加到布隆过滤器，返回短链编码
 * <p>
 * Created by PowerCheng on 2024/12/29.
 */
@Slf4j
@Component("Murmur32WithBase62")
@RequiredArgsConstructor
public class Murmur32WithBase62Shortener implements UrlShortener {

    private final BloomFilterService bloomFilterService;

    private final CustomRedisTemplate customRedisTemplate;

    private final UrlMappingRepository urlMappingRepository;

    @Override
    public Mono<UrlShortenerResult> shorten(String url) {
        return Mono.fromCallable(() -> {
            // 生成短链
            int murmurHash = Hashing.murmur3_32_fixed().hashString(url, StandardCharsets.UTF_8).asInt();
            long murmurHashLong = murmurHash < 0 ? (murmurHash & 0xFFFFFFFFL) : murmurHash;
            return Base62Util.encode(murmurHashLong);
        }).flatMap(shortCode -> handleShortCode(url, shortCode));
    }

    private Mono<UrlShortenerResult> handleShortCode(String url, String shortCode) {
        // 判断是否生成过此短链 如果生成过 会存在布隆过滤器中
        return bloomFilterService.contains(ProjectConstants.BLOOM_FILTER_HASH, shortCode)
                .flatMap(isContains -> {
                    if (!isContains) {
                        // 布隆过滤器不存在，证明没有生成过该短链编码 加入到布隆过滤器中然后返回
                        return addBloomAndReturn(url, shortCode);
                    }
                    // 布隆过滤器存在并不完全代表哈希碰撞，也有可能是长连接的重复生成，需要先检查redis
                    return customRedisTemplate.getValue(shortCode)
                            .flatMap(res -> {
                                if (ProjectConstants.NOT_FOUND_404.equals(res)) {
                                    // 如果是 404 就是之前访问数据库为空的非法访问，需要再次重新编码
                                    return Mono.empty();
                                }
                                // key 存在 直接返回 无需重复生成
                                return Mono.just(UrlShortenerResult.builder()
                                        .shortCode(shortCode)
                                        .finalOriginalUrl(url)
                                        .cacheFlag(Boolean.TRUE)
                                        .build());
                            })
                            // 若 redis 中为空 先查库
                            .switchIfEmpty(Mono.defer(() -> queryDb(shortCode)))
                            // 库中也不存在 就是哈希碰撞 添加后缀重新生成短链
                            .switchIfEmpty(Mono.defer(() -> handleHashCollision(url, shortCode)));
                });
    }

    private Mono<UrlShortenerResult> queryDb(String shortCode) {
        return urlMappingRepository.findByShortCode(shortCode)
                // 查询到结果后先存 redis
                .flatMap(urlMapping -> {
                    log.debug("库中查询到数据了: {}", urlMapping.getShortCode());
                    return customRedisTemplate.saveValue(shortCode, urlMapping.getOriginalUrl());
                })
                // redis 存储完成之后返回结果
                .flatMap(originalUrl -> Mono.just(UrlShortenerResult.builder()
                        .shortCode(shortCode)
                        .finalOriginalUrl(originalUrl)
                        .cacheFlag(Boolean.TRUE)
                        .build()));
    }

    private Mono<UrlShortenerResult> handleHashCollision(String url, String shortCode) {
        return shorten(url + ProjectConstants.HASH_SUFFIX_VALUE).doOnNext(newHashRes -> {
            log.warn("发生哈希碰撞，已重新生成数据，原链接：{} 原短链值：{} 新短链值：{}", url, shortCode, newHashRes.getShortCode());
        });
    }

    private Mono<UrlShortenerResult> addBloomAndReturn(String url, String shortCode) {
        return bloomFilterService.add(ProjectConstants.BLOOM_FILTER_HASH, shortCode)
                .map(addSuccess -> {
                    if (addSuccess) {
                        log.debug("布隆过滤器添加成功！短链：{}", shortCode);
                    } else {
                        log.warn("布隆过滤器添加失败！请检查服务状态");
                    }
                    return UrlShortenerResult.builder()
                            .finalOriginalUrl(url)
                            .shortCode(shortCode)
                            .build();
                });
    }
}
