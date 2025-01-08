package fun.powercheng.url.tiny.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by PowerCheng on 2024/12/29.
 */
@Builder
@Data
public class UrlShortenResponse {

    private String shortUrl;

    /**
     * 添加过协议头的原始URL（如果原url没有协议头的话）
     */
    private String addedProtocolUrl;

    private String message;
}
