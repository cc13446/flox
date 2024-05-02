package com.cc.flox.meta.entity;

import com.cc.flox.api.endpoint.ApiMethod;

/**
 * EndPoint
 *
 * @author cc
 * @date 2024/4/27
 */
public record EndPointEntity(
        String code,

        String path,

        ApiMethod method,

        String floxCode) {
}
