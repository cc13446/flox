package com.cc.flox.initializer;

import com.cc.flox.dataSource.DataSource;
import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.metaConfig.MetaConfig;
import com.cc.flox.metaConfig.MetaDataSourceConfig;
import com.cc.flox.utils.AssertUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 元数据库初始化器
 *
 * @author cc
 * @date 2024/4/6
 */
@Slf4j
@Order(1)
@Component
public class MetaDataSourceInitializer implements CommandLineRunner {

    /**
     * 元数据库中的数据表
     */
    private static final List<String> META_DATA_SOURCE_TABLE_LIST = List.of("data_source", "data_source_action");

    /**
     * 元数据库数据表初始化文件路径
     */
    private static final String META_DATA_SOURCE_TABLE_INTI_SQL_PATH = "classpath:meta/dataSource/initializer/";

    @Resource
    private MetaConfig metaConfig;

    @Resource
    private DataSourceManager dataSourceManager;

    @Resource
    private ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws IOException {
        // 连接到元数据库
        dataSourceManager.insert(metaConfig.getMetaDataSourceConfig().getDataSourceConfiguration());

        // 自动建立数据表
        DataSource ds = AssertUtils.assertNonNull(dataSourceManager.get(MetaDataSourceConfig.DATA_SOURCE_META_KEY), "Meta dataSource cannot be null");
        List<String> existedTableList = getExistedTableName(ds.getTemplate().getDatabaseClient());
        List<String> notExistedTableList = META_DATA_SOURCE_TABLE_LIST.stream()
                .filter(table -> !existedTableList.contains(table))
                .toList();

        for (String table : notExistedTableList) {
            String sql = getInitSql(table);
            ds.getTemplate().getDatabaseClient().sql(sql).then().block();
            log.info("Success init table [{}]", table);
        }

    }

    /**
     * @param tableName 数据表名
     * @return 初始化表 sql 语句
     */
    private String getInitSql(String tableName) throws IOException {
        org.springframework.core.io.Resource resource = resourceLoader.getResource(META_DATA_SOURCE_TABLE_INTI_SQL_PATH + tableName + ".sql");
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }

    /**
     * @param databaseClient client
     * @return 数据库中已存在的表
     */
    private List<String> getExistedTableName(DatabaseClient databaseClient) {
        return databaseClient.sql("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'")
                .map(result -> result.get("table_name", String.class))
                .all().collectList().block();
    }
}
