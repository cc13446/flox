package com.cc.flox.domain.node;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 节点类型
 *
 * @author cc
 * @date 2024/4/14
 */
@AllArgsConstructor
@Getter
public enum NodeType {

    SUB_FLOX("subFlox"),

    EXTRACT("extract"),

    REQUEST_EXTRACT("requestExtract"),

    LOADER("loader"),

    RESPONSE_LOADER("responseLoader"),

    DATA_SOURCE_LOADER("dataSourceLoader"),

    TRANSFORMER("transformer"),

    BI_TRANSFORMER("biTransformer"),

    TRI_TRANSFORMER("triTransformer");

    /**
     * code
     */
    private final String code;

}
