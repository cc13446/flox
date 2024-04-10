package com.cc.flox.api;

import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.router.ApiRouter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * 服务管理者
 *
 * @author cc
 * @date 2024/3/31
 */
@Component
public class ApiManager {

    @Resource
    private ApiRouter trieApiRouter;

    /**
     * 插入服务
     *
     * @param endPoint 服务
     * @return future
     */
    public Future<Void> insertHandler(ApiEndPoint endPoint) {
        return trieApiRouter.insertHandler(endPoint);
    }

    /**
     * 删除服务
     *
     * @param key key
     * @return future
     */
    public Future<Void> deleteHandler(String key) {
        return trieApiRouter.deleteHandler(key);
    }

    /**
     * 更新服务
     *
     * @param endPoint 服务
     * @return future
     */
    public Future<Void> updateHandler(ApiEndPoint endPoint) {
        return trieApiRouter.updateHandler(endPoint);
    }
}
