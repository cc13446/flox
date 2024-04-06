package com.cc.flox.meta;

import com.cc.flox.dao.DataSourceManager;
import com.cc.flox.meta.config.MetaConfig;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 元数据库初始化器
 *
 * @author cc
 * @date 2024/4/6
 */
@Order(1)
@Component
public class MetaDataSourceInitializer implements CommandLineRunner {

    @Resource
    private MetaConfig metaConfig;

    @Resource
    private DataSourceManager dataSourceManager;

    @Override
    public void run(String... args) {
        dataSourceManager.insert(metaConfig.getMetaDataSourceConfig().getDataSourceConfiguration());

    }
}
