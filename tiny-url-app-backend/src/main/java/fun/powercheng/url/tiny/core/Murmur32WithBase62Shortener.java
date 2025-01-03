package fun.powercheng.url.tiny.core;

import com.google.common.hash.Hashing;
import fun.powercheng.url.tiny.util.Base62Util;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 对长网址直接做32位哈希，32位哈希转int，再进行 base62 转换
 * TODO 要考虑哈希碰撞
 * Created by PowerCheng on 2024/12/29.
 */
@Component("Murmur32WithBase62")
public class Murmur32WithBase62Shortener implements UrlShortener {

    @Override
    public String shorten(String url) {
        int murmurHash = Hashing.murmur3_32_fixed().hashString(url, StandardCharsets.UTF_8).asInt();
        long murmurHashLong = murmurHash < 0 ? (murmurHash & 0xFFFFFFFFL) : murmurHash;
        return Base62Util.encode(murmurHashLong);
    }
}
