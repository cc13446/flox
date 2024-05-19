package com.cc.flox.domain.node;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * node 执行上下文
 *
 * @author cc
 * @date 2024/5/19
 */
@Getter
public class NodeExecContext {

    /**
     * 任务ID
     */
    private final String taskId;

    /**
     * 开启事务
     */
    @Setter
    private boolean transaction;

    /**
     * 属性
     */
    @Setter
    private Map<String, Object> attribute;

    public NodeExecContext(String taskId) {
        this.taskId = taskId;
    }
}
