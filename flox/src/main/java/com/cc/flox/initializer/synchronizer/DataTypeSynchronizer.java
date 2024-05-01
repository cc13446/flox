package com.cc.flox.initializer.synchronizer;

import com.cc.flox.dataType.DataTypeClassLoader;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 数据库同步器
 *
 * @author cc
 * @date 2024/4/6
 */
@Slf4j
@Order(11)
@Component
public class DataTypeSynchronizer implements CommandLineRunner {

    @Resource
    private DataTypeClassLoader dataTypeClassLoader;

    @Override
    public void run(String... args) {
        // 启动自定义数据类型同步
        dataTypeClassLoader.startSynchronize();
    }

}
