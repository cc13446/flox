package com.cc.flox.api.router.impl;

import com.cc.flox.utils.trie.Trie;
import com.cc.flox.utils.trie.command.TrieDeleteCommand;
import com.cc.flox.utils.trie.command.TrieInsertCommand;
import com.cc.flox.utils.trie.command.TrieUpdateCommand;
import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.endpoint.ApiExchange;
import org.springframework.stereotype.Component;
import com.cc.flox.api.router.ApiRouter;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.Future;

/**
 * http请求路由前缀树实现
 *
 * @author cc
 * @date 2024/3/30
 */
@Component("trieApiRouter")
public class TrieApiRouter implements ApiRouter {

    /**
     * 路由前缀树
     */
    static class ApiEndPointTrie extends Trie<ApiEndPoint> {
        @Override
        public int getNodeLength() {
            return 55;
        }

        @Override
        public int getNodeIndex(char c) {
            if (c >= 'a' && c <= 'z') {
                return c - 'a';
            } else if (c >= 'A' && c <= 'Z') {
                return c - 'A' + 26;
            } else if (c == '-') {
                return 52;
            } else if (c == '_') {
                return 53;
            } else if (c == '/') {
                return 54;
            }
            throw new RuntimeException("Unknown char [" + c + "] in path");
        }
    }

    /**
     * 路由前缀树
     */
    private final ApiEndPointTrie endPointTrie = new ApiEndPointTrie();

    @Override
    public Mono<Void> handle(ApiExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        ApiEndPoint endPoint = endPointTrie.get(path);
        if (Objects.isNull(endPoint)) {
            return Mono.error(new RuntimeException("Miss router path [" + path + "]"));
        }
        return endPoint.handler(exchange);
    }

    @Override
    public Future<Void> insertHandler(ApiEndPoint endPoint) {
        return endPointTrie.command(new TrieInsertCommand<>(endPoint.getPath(), endPoint));
    }

    @Override
    public Future<Void> deleteHandler(String key) {
        return endPointTrie.command(new TrieDeleteCommand<>(key));
    }

    @Override
    public Future<Void> updateHandler(ApiEndPoint endPoint) {
        return endPointTrie.command(new TrieUpdateCommand<>(endPoint.getPath(), e -> endPoint));
    }
}
