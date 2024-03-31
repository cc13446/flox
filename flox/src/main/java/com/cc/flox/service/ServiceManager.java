package com.cc.flox.service;

import com.cc.flox.web.endpoint.HttpEndPoint;
import com.cc.flox.web.router.HttpRouter;
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
public class ServiceManager {

    @Resource
    private HttpRouter httpRouter;

    /**
     * 插入服务
     *
     * @param endPoint 服务
     * @return future
     */
    public Future<Void> insertHandler(HttpEndPoint endPoint) {
        return httpRouter.insertHandler(endPoint);
    }

    /**
     * 删除服务
     *
     * @param key key
     * @return future
     */
    public Future<Void> deleteHandler(String key) {
        return httpRouter.deleteHandler(key);
    }

    /**
     * 更新服务
     *
     * @param endPoint 服务
     * @return future
     */
    public Future<Void> updateHandler(HttpEndPoint endPoint) {
        return httpRouter.updateHandler(endPoint);
    }
}
