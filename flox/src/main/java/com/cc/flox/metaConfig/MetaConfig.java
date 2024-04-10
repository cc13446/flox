package com.cc.flox.metaConfig;

import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * 元配置
 * @author cc
 * @date 2024/4/6
 */
@Getter
@Configuration
public class MetaConfig {

    @Resource
    private MetaDataSourceConfig metaDataSourceConfig;
}
