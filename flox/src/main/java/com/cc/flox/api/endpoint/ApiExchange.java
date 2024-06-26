package com.cc.flox.api.endpoint;

import com.cc.flox.domain.node.NodeExecContext;
import lombok.Data;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

/** HTTP 交换数据类
 * @author cc
 * @date 2024/3/30
 */
@Data
public class ApiExchange {

    /**
     * 执行环境
     */
    private NodeExecContext context;

    /**
     * HTTP 请求
     */
    private ServerHttpRequest request;

    /**
     * HTTP 响应
     */
    private ServerHttpResponse response;

}
