package com.cc.flox.initializer.synchronizer;

import com.cc.flox.node.NodeManager;
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
@Order(12)
@Component
public class NodeSynchronizer implements CommandLineRunner {

    @Resource
    private NodeManager nodeManager;

    @Override
    public void run(String... args) {
        // 启动节点同步
        nodeManager.startSynchronize();
    }

}
