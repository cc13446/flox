package com.cc.flox.meta.entity;

import com.cc.flox.dataSource.DataSource;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author cc
 * @date 2024/5/1
 */
public record DataSourcesEntity(
        List<DataSource> dataSources,
        OffsetDateTime updateTime) {

}
