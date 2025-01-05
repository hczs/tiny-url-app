package fun.powercheng.url.tiny.service;

import org.redisson.api.RBloomFilter;
import reactor.core.publisher.Mono;

/**
 * Created by PowerCheng on 2025/1/4.
 */
public interface BloomFilterService {

    Mono<RBloomFilter<Object>> getBloomFilter(String bloomFilterName);

    /**
     * 向布隆过滤器中添加元素
     *
     * @param bloomFilterName 布隆过滤器名称
     * @param object          放入布隆过滤器中的元素
     * @return 成功返回 true 失败返回 false
     */
    Mono<Boolean> add(String bloomFilterName, Object object);

    /**
     * 判断布隆过滤器中是否包含指定元素
     *
     * @param bloomFilterName 布隆过滤器名称
     * @param object          指定元素
     * @return 包含返回 true 不包含返回 false
     */
    Mono<Boolean> contains(String bloomFilterName, Object object);
}
