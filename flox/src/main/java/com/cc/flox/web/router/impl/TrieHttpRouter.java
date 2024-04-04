package com.cc.flox.web.router.impl;

import com.cc.flox.utils.trie.Trie;
import com.cc.flox.utils.trie.command.TrieDeleteCommand;
import com.cc.flox.utils.trie.command.TrieInsertCommand;
import com.cc.flox.utils.trie.command.TrieUpdateCommand;
import com.cc.flox.web.endpoint.HttpEndPoint;
import com.cc.flox.web.endpoint.HttpExchange;
import org.springframework.stereotype.Component;
import com.cc.flox.web.router.HttpRouter;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.Future;

/**
 * http请求路由前缀树实现
 *
 * @author cc
 * @date 2024/3/30
 */
@Component("trieHttpRouter")
public class TrieHttpRouter implements HttpRouter {

    /**
     * 路由前缀树
     */
    static class HttpEndPointTrie extends Trie<HttpEndPoint> {
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
    private final HttpEndPointTrie endPointTrie = new HttpEndPointTrie();

    @Override
    public Mono<Void> handle(HttpExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        HttpEndPoint endPoint = endPointTrie.get(path);
        if (Objects.isNull(endPoint)) {
            return Mono.error(new RuntimeException("Miss router path [" + path + "]"));
        }
        return endPoint.handler(exchange);
    }

    @Override
    public Future<Void> insertHandler(HttpEndPoint endPoint) {
        return endPointTrie.command(new TrieInsertCommand<>(endPoint.getPath(), endPoint));
    }

    @Override
    public Future<Void> deleteHandler(String key) {
        return endPointTrie.command(new TrieDeleteCommand<>(key));
    }

    @Override
    public Future<Void> updateHandler(HttpEndPoint endPoint) {
        return endPointTrie.command(new TrieUpdateCommand<>(endPoint.getPath(), e -> endPoint));
    }
}
