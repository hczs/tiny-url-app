package fun.powercheng.url.tiny.init;

import fun.powercheng.url.tiny.config.TinyUrlAppConfig;
import fun.powercheng.url.tiny.enums.ShortenerTypeEnum;
import fun.powercheng.url.tiny.service.BloomFilterService;
import fun.powercheng.url.tiny.util.ProjectConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Created by PowerCheng on 2025/1/4.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BloomFilterInit implements ApplicationRunner {

    private final BloomFilterService bloomFilterService;

    private final TinyUrlAppConfig appConfig;

    private static final long initCapacity = 100_000L;

    private static final double falseProbability = 0.01;

    @Override
    public void run(ApplicationArguments args) {
        if (appConfig.getShortenerType() != ShortenerTypeEnum.MURMUR32_WITH_BASE62) {
            return;
        }
        // 初始化哈希碰撞布隆过滤器
        Mono<RBloomFilter<Object>> bloomFilterMono = bloomFilterService.getBloomFilter(ProjectConstants.BLOOM_FILTER_HASH);
        bloomFilterMono.subscribe(bloomFilter -> {
            bloomFilter.tryInit(initCapacity, falseProbability);
            log.info("布隆过滤器 {} 已完成初始化操作，初始容量：{} 误判率：{}", ProjectConstants.BLOOM_FILTER_HASH, initCapacity,
                    falseProbability);
        });
    }
}
