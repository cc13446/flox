package com.cc.flox.domain.flox.node;

import java.util.List;

/**
 * 流程节点
 *
 * @author cc
 * @date 2024/4/13
 */
public interface FloxNode {

    /**
     * @return node code
     */
    String getNodeCode();

    /**
     * @return 前置 node codes
     */
    List<String> getPreNodeCodes();

    /**
     * @return groovy 代码
     */
    String getGroovy();

    /**
     * @return node 类型
     */
    FloxNodeType getType();

}
