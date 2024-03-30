package com.cc.flox.web.router.impl;

import com.cc.flox.web.exchange.HttpExchange;
import org.springframework.stereotype.Component;
import com.cc.flox.web.router.HttpRouter;
import reactor.core.publisher.Mono;

/**
 * http请求路由前缀树实现
 *
 * @author cc
 * @date 2024/3/30
 */
@Component("trieHttpRouter")
public class TrieHttpRouter implements HttpRouter {


    @Override
    public Mono<Void> handle(HttpExchange exchange) {
        return Mono.empty();
    }


}
