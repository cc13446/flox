package com.cc.flox.meta.entity;

import com.cc.flox.domain.extractor.RequestExtractor;
import com.cc.flox.domain.loader.ResponseLoader;

/**
 * 流程
 *
 * @author cc
 * @date 2024/4/27
 */
public record FloxEntity(
        RequestExtractor<Object> requestExtractor,

        String subFloxCode,

        ResponseLoader<Object> responseLoader) {
}
