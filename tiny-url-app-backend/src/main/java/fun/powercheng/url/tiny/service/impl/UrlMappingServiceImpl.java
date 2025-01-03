package fun.powercheng.url.tiny.service.impl;

import fun.powercheng.url.tiny.core.UrlShortener;
import fun.powercheng.url.tiny.core.UrlShortenerFactory;
import fun.powercheng.url.tiny.model.po.UrlMapping;
import fun.powercheng.url.tiny.repository.UrlMappingRepository;
import fun.powercheng.url.tiny.service.UrlMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Created by PowerCheng on 2024/12/28.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UrlMappingServiceImpl implements UrlMappingService {

    private final UrlMappingRepository urlMappingRepository;

    private final UrlShortenerFactory urlShortenerFactory;

    @Override
    public Mono<String> getUrlByShortCode(String shortCode) {
        return urlMappingRepository.findByShortCode(shortCode).map(UrlMapping::getOriginalUrl);
    }

    @Override
    public Mono<String> generateUrlMapping(String originalUrl) {
        return Mono.fromCallable(() -> {
            UrlShortener urlShortener = urlShortenerFactory.getUrlShortener();
            return urlShortener.shorten(originalUrl);
        }).doOnSuccess(shortCode -> {
            log.info("Generated shortCode: {}; Original URL is: {}", shortCode, originalUrl);
        }).flatMap(shortCode -> urlMappingRepository
                .save(UrlMapping.builder()
                        .originalUrl(originalUrl)
                        .shortCode(shortCode)
                        .build())
                .map(UrlMapping::getShortCode));

    }

}
