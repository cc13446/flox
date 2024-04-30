package com.cc.flox.meta.entity;

/**
 * 流程
 *
 * @author cc
 * @date 2024/4/27
 */
public record FloxEntity(
        NodeEntity requestExtractor,

        NodeEntity subFloxCode,

        NodeEntity responseLoader) {
}
