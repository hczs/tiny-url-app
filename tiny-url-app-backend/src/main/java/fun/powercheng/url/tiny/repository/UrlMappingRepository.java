package fun.powercheng.url.tiny.repository;

import fun.powercheng.url.tiny.model.po.UrlMapping;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * Created by PowerCheng on 2024/12/28.
 */
public interface UrlMappingRepository extends ReactiveCrudRepository<UrlMapping, Long> {

    Mono<UrlMapping> findByShortCode(String shortCode);
}
