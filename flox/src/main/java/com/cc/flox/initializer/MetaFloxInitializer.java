package com.cc.flox.initializer;

import com.cc.flox.api.ApiManager;
import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.endpoint.ApiMethod;
import com.cc.flox.domain.flox.FloxBuilder;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.node.NodeManager;
import com.cc.flox.utils.AssertUtils;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.cc.flox.initializer.MetaRequestExtractorInitializer.*;
import static com.cc.flox.initializer.MetaResponseLoaderInitializer.META_RESPONSE_LOADER_CODE_WRITE_JSON;
import static com.cc.flox.initializer.MetaSubFloxInitializer.*;


/**
 * 元流程初始化器
 *
 * @author cc
 * @date 2024/4/2
 */
@Order(5)
@Component
public class MetaFloxInitializer implements CommandLineRunner {

    @Resource
    private ApiManager apiManager;

    @Resource
    private NodeManager nodeManager;

    @Override
    public void run(String... args) throws Exception {
        apiManager.insertHandler(getEchoEndPoint()).get();
        apiManager.insertHandler(getInsertDataSourceEndPoint()).get();
        apiManager.insertHandler(getSelectDataSourceEndPoint()).get();
        apiManager.insertHandler(getInsertDataSourceActionEndPoint()).get();
        apiManager.insertHandler(getSelectDataSourceActionEndPoint()).get();
    }

    /**
     * @return echo end point
     */
    private ApiEndPoint getEchoEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_QUERY_PARAMS_TO_MAP))
                .setSubFloxBuilder(() -> getSubFlox(META_SUB_FLOX_CODE_ECHO))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint("/echo", ApiMethod.GET, builder.builder());
    }

    /**
     * @return insert data source end point
     */
    private ApiEndPoint getInsertDataSourceEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_BODY_PARAMS_TO_LIST_MAP))
                .setSubFloxBuilder(() -> getSubFlox(META_SUB_FLOX_CODE_INSERT_DATA_SOURCE))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint("/data-source/insert", ApiMethod.POST, builder.builder());
    }

    /**
     * @return select data source end point
     */
    private ApiEndPoint getSelectDataSourceEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_QUERY_PARAMS_TO_MAP))
                .setSubFloxBuilder(() -> getSubFlox(META_SUB_FLOX_CODE_SELECT_DATA_SOURCE))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint("/data-source/select", ApiMethod.GET, builder.builder());
    }

    /**
     * @return select data source action end point
     */
    private ApiEndPoint getInsertDataSourceActionEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_BODY_PARAMS_TO_LIST_MAP))
                .setSubFloxBuilder(() -> getSubFlox(META_SUB_FLOX_CODE_INSERT_DATA_SOURCE_ACTION))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint("/data-source/action/insert", ApiMethod.POST, builder.builder());
    }

    /**
     * @return select data source action end point
     */
    private ApiEndPoint getSelectDataSourceActionEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_QUERY_PARAMS_TO_MAP))
                .setSubFloxBuilder(() -> getSubFlox(META_SUB_FLOX_CODE_SELECT_DATA_SOURCE_ACTION))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint("/data-source/action/select", ApiMethod.GET, builder.builder());
    }

    /**
     * @param code code
     * @return sub flox
     */
    private NodeEntity getSubFlox(String code) {
        NodeEntity node = AssertUtils.assertNonNull(nodeManager.getMetaSubFlox(code), "SubFlox cannot be null, code is [" + code + "]");
        AssertUtils.assertTrue(node.nodeType() == NodeType.SUB_FLOX, "Flox only accept subFlox, but receive [" + node.nodeType().getCode() + "]");
        return node;
    }
}
