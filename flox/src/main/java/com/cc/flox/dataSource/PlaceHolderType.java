package com.cc.flox.dataSource;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

/**
 * 占位符类型类型
 *
 * @author cc
 * @date 2024/4/30
 */
@AllArgsConstructor
@Getter
public enum PlaceHolderType {

    QUESTION_MARK("question_mark", i -> "?"),

    DOLLAR("dollar", i -> "$" + i),
    ;

    /**
     * code
     */
    private final String code;

    /**
     * 通过下标计算出来的占位符
     */
    private final Function<Integer, String> placeHolderFunc;
}
