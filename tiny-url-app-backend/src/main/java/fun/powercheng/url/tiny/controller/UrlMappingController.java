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

import static fun.powercheng.url.tiny.util.ProjectConstants.NOT_FOUND_404;

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
                .map(shortenResponse -> ResponseEntity.status(HttpStatus.OK).body(shortenResponse))
                .onErrorResume(error -> {
                    if (error instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(UrlShortenResponse.builder().message(error.getMessage()).build())
                        );
                    }
                    return Mono.error(error);
                })
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/{shortCode}")
    public Mono<Void> getUrl(@PathVariable String shortCode, ServerWebExchange exchange) {
        return urlMappingService.getUrlByShortCode(shortCode)
                .flatMap(originalUrl -> {
                    if (NOT_FOUND_404.equals(originalUrl)) {
                        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                        return exchange.getResponse().setComplete();
                    }
                    exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                    exchange.getResponse().getHeaders().setLocation(URI.create(originalUrl));
                    return exchange.getResponse().setComplete();
                });
    }
}
