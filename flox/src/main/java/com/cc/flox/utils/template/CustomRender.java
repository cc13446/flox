package com.cc.flox.utils.template;

/**
 * @author cc
 * @date 2024/4/29
 */
@FunctionalInterface
public interface CustomRender {
    /**
     * @param index 下标
     * @param v     v
     * @return 字符串
     */
    String render(Integer index, Object v);
}
