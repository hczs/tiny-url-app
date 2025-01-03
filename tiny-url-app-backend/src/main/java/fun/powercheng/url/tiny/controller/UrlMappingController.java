package fun.powercheng.url.tiny.controller;

import fun.powercheng.url.tiny.model.dto.UrlShortenRequest;
import fun.powercheng.url.tiny.model.vo.UrlShortenResponse;
import fun.powercheng.url.tiny.service.UrlMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Created by PowerCheng on 2024/12/28.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlMappingController {

    private final UrlMappingService urlMappingService;

    @PostMapping("/data/shorten")
    public Mono<ResponseEntity<UrlShortenResponse>> shorten(@RequestBody UrlShortenRequest urlShortenRequest) {
        return urlMappingService.generateUrlMapping(urlShortenRequest.getLongUrl())
                .map(shortUrl -> ResponseEntity.status(HttpStatus.OK)
                        .body(UrlShortenResponse.builder().shortUrl(shortUrl).build()));
    }

    @GetMapping("/{shortCode}")
    public Mono<Void> getUrl(@PathVariable String shortCode, ServerWebExchange exchange) {
        return urlMappingService.getUrlByShortCode(shortCode)
                .flatMap(originalUrl -> {
                    exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                    exchange.getResponse().getHeaders().setLocation(URI.create(originalUrl));
                    return exchange.getResponse().setComplete();
                });
    }
}
