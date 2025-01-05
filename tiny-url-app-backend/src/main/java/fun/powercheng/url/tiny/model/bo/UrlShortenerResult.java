package fun.powercheng.url.tiny.model.bo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by PowerCheng on 2025/1/5.
 */
@Builder
@Data
public class UrlShortenerResult {

    /**
     * 最终用于生成 shortCode 的长连接
     * 主要考虑哈希碰撞会向url中添加后缀的场景
     */
    private String finalOriginalUrl;

    /**
     * 短链编码
     */
    private String shortCode;

    /**
     * 该 shortCode 是否存在于缓存
     * 默认不存在缓存 除非是短编码生成器中进行了缓存判断，可以设置这个值 防止业务中重复判断
     */
    @Builder.Default
    private Boolean cacheFlag = Boolean.FALSE;
}
