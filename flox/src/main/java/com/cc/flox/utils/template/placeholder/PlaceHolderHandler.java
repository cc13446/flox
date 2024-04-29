package com.cc.flox.utils.template.placeholder;

/**
 * 占位符处理器
 *
 * @author cc
 * @date 2024/4/29
 */
public interface PlaceHolderHandler {

    /**
     * @param placeHolder 占位符
     * @return output
     */
    String handleToken(String placeHolder);
}
