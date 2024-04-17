package com.cc.flox.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * 断言工具类
 *
 * @author cc
 * @date 2023/8/19
 */
public class AssertUtils {

    /**
     * 断言 object 不为 null
     *
     * @param object     object
     * @param errMessage 错误信息
     * @param <T>        T
     * @return object
     */
    public static <T> @NonNull T assertNonNull(T object, String errMessage) {
        if (Objects.isNull(object)) {
            throw new RuntimeException(errMessage);
        }
        return object;
    }

    /**
     * 断言字符串不为blank
     *
     * @param object     字符串
     * @param errMessage 错误信息
     * @return 字符串
     */
    public static String assertNonBlank(String object, String errMessage) {
        if (StringUtils.isBlank(object)) {
            throw new RuntimeException(errMessage);
        }
        return object;
    }

    /**
     * 断言boolean 为 true
     *
     * @param object     boolean
     * @param errMessage 错误信息
     */
    public static void assertTrue(boolean object, String errMessage) {
        if (!object) {
            throw new RuntimeException(errMessage);
        }
    }
}
