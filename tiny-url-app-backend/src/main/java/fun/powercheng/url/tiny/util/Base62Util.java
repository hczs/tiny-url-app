package fun.powercheng.url.tiny.util;

import lombok.experimental.UtilityClass;

/**
 * Created by PowerCheng on 2024/12/29.
 */
@UtilityClass
public class Base62Util {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int BASE62_RADIX = BASE62_ALPHABET.length();

    public static String encode(long input) {
        StringBuilder result = new StringBuilder();
        while (input > 0) {
            int digit = (int) (input % BASE62_RADIX);
            result.insert(0, BASE62_ALPHABET.charAt(digit));
            input /= BASE62_RADIX;
        }
        return result.isEmpty() ? "0" : result.toString();
    }
}
