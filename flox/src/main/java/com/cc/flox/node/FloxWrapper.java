package com.cc.flox.node;

import com.cc.flox.api.endpoint.ApiExchange;
import com.cc.flox.api.error.ApiErrorHandler;
import com.cc.flox.dataSource.DataSourceManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * 包装 flox 数据流
 *
 * @author cc
 * @date 2024/5/19
 */
@Component
public class FloxWrapper {
    @Resource
    private ApiErrorHandler apiErrorHandler;

    @Resource
    private DataSourceManager dataSourceManager;

    /**
     * 包装
     *
     * @param floxSupplier floxSupplier
     * @return result
     */
    public Mono<Void> wrap(ApiExchange exchange, Supplier<Mono<Void>> floxSupplier) {
        try {
            Mono<Void> res = floxSupplier.get();
            res = dataSourceManager.wrapTransaction(exchange.getContext().getTaskId(), res);
            return res.onErrorResume((ex) -> apiErrorHandler.handle(exchange, ex));
        } catch (Exception e) {
            return apiErrorHandler.handle(exchange, e);
        }
    }
}
