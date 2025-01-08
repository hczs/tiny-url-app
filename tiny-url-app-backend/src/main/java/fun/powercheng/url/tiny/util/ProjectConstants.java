package fun.powercheng.url.tiny.util;

import lombok.experimental.UtilityClass;

/**
 * Created by PowerCheng on 2025/1/4.
 */
@UtilityClass
public class ProjectConstants {

    public static final String REDIS_KEY_APP_PREFIX = "tiny:";

    public static final String NOT_FOUND_404 = "URL_IS_NOT_FOUND";

    /**
     * 哈希碰撞后添加的额外后缀
     */
    public static final String HASH_SUFFIX_VALUE = ",";

    /**
     * 判断哈希碰撞的布隆过滤器
     */
    public static final String BLOOM_FILTER_HASH = "bloomfilter:hash";

    /**
     * 为没有协议头的 url 添加上默认的 http 协议头
     */
    public static final String DEFAULT_HTTP_PROTOCOL = "http://";
}
