package com.cc.flox.api.error;

import com.cc.flox.api.endpoint.ApiExchange;
import com.cc.flox.api.response.ApiResponseWrapper;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.node.NodeManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.cc.flox.initializer.meta.MetaResponseLoaderInitializer.META_RESPONSE_LOADER_CODE_WRITE_JSON;

/**
 * 全局错误处理
 *
 * @author cc
 * @date 2024/4/20
 */
@Component
@Slf4j
public class ApiErrorHandler {

    @Resource
    private NodeManager nodeManager;

    /**
     * 错误处理
     *
     * @param exchange exchange
     * @param ex       error
     * @return mono
     */
    public Mono<Void> handle(ApiExchange exchange, Throwable ex) {
        int code = 999;
        if (ex instanceof DataAccessException) {
            code = 500;
        } else if (ex instanceof RuntimeException) {
            code = 400;
        }
        NodeEntity responseLoader = nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON);
        log.error("Exec error : ", ex);
        return responseLoader.exec(Mono.just(ApiResponseWrapper.error(code, ex.getMessage())), Mono.just(exchange.getResponse())).mapNotNull(o -> null);
    }

}
