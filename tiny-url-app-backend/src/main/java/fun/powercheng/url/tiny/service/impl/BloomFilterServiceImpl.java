package fun.powercheng.url.tiny.service.impl;

import fun.powercheng.url.tiny.service.BloomFilterService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Created by PowerCheng on 2025/1/4.
 */
@Service
@RequiredArgsConstructor
public class BloomFilterServiceImpl implements BloomFilterService {

    private final RedissonClient redissonClient;

    @Override
    public Mono<RBloomFilter<Object>> getBloomFilter(String bloomFilterName) {
        return Mono.fromCallable(() -> redissonClient.getBloomFilter(bloomFilterName));
    }

    @Override
    public Mono<Boolean> add(String bloomFilterName, Object object) {
        return Mono.create(sink -> {
            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomFilterName);
            RFuture<Boolean> future = bloomFilter.addAsync(object);
            future.whenComplete((res, e) -> {
                if (e != null) {
                    sink.error(e);
                } else {
                    sink.success(res);
                }
            });
        });
    }

    @Override
    public Mono<Boolean> contains(String bloomFilterName, Object object) {
        return Mono.create(sink -> {
            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomFilterName);
            RFuture<Boolean> future = bloomFilter.containsAsync(object);
            future.whenComplete((res, e) -> {
                if (e != null) {
                    sink.error(e);
                } else {
                    sink.success(res);
                }
            });
        });
    }
}
