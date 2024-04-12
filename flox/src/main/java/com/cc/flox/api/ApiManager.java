package com.cc.flox.api;

import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.endpoint.ApiExchange;
import com.cc.flox.api.endpoint.ApiMethod;
import com.cc.flox.api.router.ApiRouter;
import com.cc.flox.api.router.impl.TrieApiRouter;
import com.cc.flox.utils.AssertUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 服务管理者
 *
 * @author cc
 * @date 2024/3/31
 */
@Component
public class ApiManager {

    /**
     * 每个 method 对应的的前缀路由树
     */
    private final Map<ApiMethod, ApiRouter> trieApiRouterMap = Map.copyOf(
            Arrays.stream(ApiMethod.values()).reduce(new HashMap<>(), (map, m) -> {
                map.put(m, new TrieApiRouter());
                return map;
            }, (map1, map2) -> {
                map1.putAll(map2);
                return map1;
            }));

    /**
     * 插入服务
     *
     * @param endPoint 服务
     * @return future
     */
    public Future<Void> insertHandler(ApiEndPoint endPoint) {
        return trieApiRouterMap.get(endPoint.method()).insertHandler(endPoint);
    }

    /**
     * 删除服务
     *
     * @param key key
     * @return future
     */
    public Future<Void> deleteHandler(String key, ApiMethod method) {
        return trieApiRouterMap.get(method).deleteHandler(key);
    }

    /**
     * 更新服务
     *
     * @param endPoint 服务
     * @return future
     */
    public Future<Void> updateHandler(ApiEndPoint endPoint) {
        return trieApiRouterMap.get(endPoint.method()).updateHandler(endPoint);
    }

    /**
     * 处理服务
     *
     * @param exchange exchange
     * @return result
     */
    public Mono<Void> handle(ApiExchange exchange) {
        ApiMethod method = ApiMethod.fromHttpMethod(exchange.getRequest().getMethod());
        AssertUtils.assertNonNull(method, "Unknown http method:" + exchange.getRequest().getMethod());
        return trieApiRouterMap.get(method).handle(exchange);
    }
}
