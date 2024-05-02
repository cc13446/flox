package com.cc.flox.meta.entity;

/**
 * 流程
 *
 * @author cc
 * @date 2024/4/27
 */
public record FloxEntity(

        String code,

        NodeEntity requestExtractor,

        NodeEntity subFlox,

        NodeEntity responseLoader) {
}
