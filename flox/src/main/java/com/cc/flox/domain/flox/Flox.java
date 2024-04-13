package com.cc.flox.domain.flox;

import com.cc.flox.domain.extractor.RequestExtractor;
import com.cc.flox.domain.loader.ResponseLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程
 *
 * @author cc
 * @date 2024/3/31
 */
@AllArgsConstructor
@Getter
public class Flox {

    /**
     * HTTP请求提取器
     */
    private RequestExtractor<Object> requestExtractor;

    /**
     * HTTP响应加载器
     */
    private ResponseLoader<Object> responseLoader;

}
