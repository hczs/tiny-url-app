package fun.powercheng.url.tiny.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by PowerCheng on 2024/12/29.
 */
@AllArgsConstructor
@Getter
public enum ShortenerTypeEnum {

    /**
     * 哈希 + base62 转换
     * Murmur32WithBase62
     */
    MURMUR32_WITH_BASE62("Murmur32WithBase62"),

    /**
     * 唯一ID + base62 转换
     * UniqueIdWithBase62
     */
    UNIQUE_ID_WITH_BASE62("UniqueIdWithBase62");

    private final String typeName;
}
