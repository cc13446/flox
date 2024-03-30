package com.cc.flox.config;

import com.cc.flox.web.DispatchHttpHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;

/**
 * web flux 配置对象
 * @author cc
 * @date 2024/3/30
 */
@Configuration
public class WebFluxConfig {

    @Bean
    public HttpHandler httpHandler() {
        return new DispatchHttpHandler();
    }

}
