package com.cc.flox.utils;

import net.jpountz.xxhash.StreamingXXHash64;
import net.jpountz.xxhash.XXHashFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author cc
 * @date 2024/4/28
 */
public class XXHashUtils {

    private static final XXHashFactory factory = XXHashFactory.fastestInstance();

    private static final long seed = 0x0747b28c9747b28cL;

    /**
     * @param s 字符串
     * @return 64 位 hash
     */
    public static long hash(String s) {
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        try (StreamingXXHash64 hash = factory.newStreamingHash64(seed)) {
            hash.update(data, 0, data.length);
            return hash.getValue();
        }
    }

}
