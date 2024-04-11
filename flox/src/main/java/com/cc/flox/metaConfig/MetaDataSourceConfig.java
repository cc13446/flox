package com.cc.flox.metaConfig;

import com.cc.flox.dataSource.DataSourceConfiguration;
import com.cc.flox.dataSource.DataSourceType;
import com.cc.flox.dataSource.action.Action;
import com.cc.flox.dataSource.action.ActionType;
import com.cc.flox.utils.AssertUtils;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * 元数据库配置
 *
 * @author cc
 * @date 2024/4/6
 */
@Getter
@Configuration
public class MetaDataSourceConfig {
    /**
     * 元数据库 key
     */
    public static final String META_DATA_SOURCE_KEY = "META_DATA_SOURCE";

    /**
     * 元数据库动作路径
     */
    private static final String META_DATA_SOURCE_ACTION_PATH = "classpath:meta/dataSource/action/";

    /**
     * 数据源 url
     */
    @Value("${meta.dataSource.url}")
    private String url;

    /**
     * 用户名
     */
    @Value("${meta.dataSource.username}")
    private String username;

    /**
     * 密码
     */
    @Value("${meta.dataSource.password}")
    private String password;

    /**
     * 连接池初始连接数
     */
    @Value("${meta.dataSource.pool.initSize:5}")
    private int initSize;

    /**
     * 连接池最大连接数
     */
    @Value("${meta.dataSource.pool.maxSize:20}")
    private int maxSize;

    /**
     * 连接池连接最大空闲时间
     */
    @Value("${meta.dataSource.pool.maxIdle:60}")
    private int maxIdle;

    @Resource
    private ResourceLoader resourceLoader;

    /**
     * @return 元数据库配置对象
     */
    public DataSourceConfiguration getDataSourceConfiguration() throws IOException {
        AssertUtils.assertNonBlank(url, "Meta dataSource [url] cannot be null");

        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(url);
        String dataSourceType = (String) checkAndGetValue(options, "driver");
        return new DataSourceConfiguration(
                META_DATA_SOURCE_KEY,
                url,
                StringUtils.isNoneBlank(username) ? username : (String) checkAndGetValue(options, "username"),
                StringUtils.isNoneBlank(password) ? password : (String) checkAndGetValue(options, "password"),
                AssertUtils.assertNonNull(DataSourceType.fromCode(dataSourceType), "Unknown dataSource type : " + dataSourceType),
                initSize,
                maxSize,
                maxIdle,
                getMetaDataSourceAction()
        );
    }

    /**
     * @return 元数据库动作
     */
    private Map<String, Action> getMetaDataSourceAction() throws IOException {
        File folder = resourceLoader.getResource(META_DATA_SOURCE_ACTION_PATH).getFile();
        if (!folder.isDirectory()) {
            throw new RuntimeException(META_DATA_SOURCE_ACTION_PATH + " must be directory!");
        }
        File[] actionFiles = folder.listFiles();
        AssertUtils.assertNonNull(actionFiles, "Meta data source actions must not be null");
        Map<String, Action> res = HashMap.newHashMap(actionFiles.length);
        for (File file : actionFiles) {
            if (file.isDirectory() || file.getName().startsWith(".")) {
                continue;
            }
            String fileName = file.getName();
            String[] nameSplit = fileName.split("\\.");
            if (nameSplit.length != 2) {
                throw new RuntimeException("Meta data source action file name invalid:" + fileName);
            }
            String actionCode = nameSplit[0];
            ActionType actionType = ActionType.fromCode(nameSplit[1]);
            AssertUtils.assertNonNull(actionType, "Unknown action type:" + nameSplit[1]);
            String actionSql = Files.readString(file.toPath());
            res.put(actionCode, new Action(actionCode, actionType,
                    AssertUtils.assertNonBlank(actionSql, "Meta data source action sql cannot be blank")));
        }

        return res;
    }

    /**
     * 检查并获取值
     *
     * @param options ConnectionFactoryOptions
     * @param name    option name
     * @return value
     */
    private Object checkAndGetValue(ConnectionFactoryOptions options, String name) {
        String errorMessage = "Meta dataSource [" + name + "] cannot be null";
        return AssertUtils.assertNonNull(options.getValue(Option.valueOf(name)), errorMessage);
    }

}
