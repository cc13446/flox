package com.cc.flox.api.error;

import com.cc.flox.api.endpoint.ApiExchange;
import com.cc.flox.api.response.ApiResponseWrapper;
import com.cc.flox.domain.loader.impl.DefaultResponseLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 全局错误处理
 *
 * @author cc
 * @date 2024/4/20
 */
@Component
@Slf4j
public class ApiErrorHandler {

    /**
     * 错误处理
     *
     * @param exchange exchange
     * @param ex          error
     * @return mono
     */
    public Mono<Void> handle(ApiExchange exchange, Throwable ex) {
        int code = 999;
        if (ex instanceof DataAccessException) {
            code = 500;
        } else if (ex instanceof RuntimeException) {
            code = 400;
        }
        DefaultResponseLoader responseLoader = new DefaultResponseLoader();
        log.error("Exec error : ", ex);
        return responseLoader.loader(Mono.just(ApiResponseWrapper.error(code, ex.getMessage())), Mono.just(exchange.getResponse()));
    }

}
