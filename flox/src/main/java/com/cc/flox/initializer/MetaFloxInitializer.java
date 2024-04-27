package com.cc.flox.initializer;

import com.cc.flox.api.ApiManager;
import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.endpoint.ApiMethod;
import com.cc.flox.domain.flox.FloxBuilder;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.subFlox.SubFlox;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.node.NodeManager;
import com.cc.flox.utils.AssertUtils;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.cc.flox.initializer.MetaRequestExtractorInitializer.META_REQUEST_EXTRACTOR_CODE_BODY_PARAMS;
import static com.cc.flox.initializer.MetaRequestExtractorInitializer.META_REQUEST_EXTRACTOR_CODE_QUERY_PARAMS;
import static com.cc.flox.initializer.MetaResponseLoaderInitializer.META_RESPONSE_LOADER_CODE_WRITE_JSON;
import static com.cc.flox.initializer.MetaSubFloxInitializer.META_SUB_FLOX_CODE_ECHO;
import static com.cc.flox.initializer.MetaSubFloxInitializer.META_SUB_FLOX_CODE_INSERT_DATA_SOURCE;


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
    }

    /**
     * @return echo end point
     */
    private ApiEndPoint getEchoEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_QUERY_PARAMS))
                .setSubFloxBuilder(() -> getSubFlox(META_SUB_FLOX_CODE_ECHO))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint("/echo", ApiMethod.GET, builder.builder());
    }

    /**
     * @return insert data source end point
     */
    private ApiEndPoint getInsertDataSourceEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_BODY_PARAMS))
                .setSubFloxBuilder(() -> getSubFlox(META_SUB_FLOX_CODE_INSERT_DATA_SOURCE))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint("/data-source/insert", ApiMethod.POST, builder.builder());
    }

    /**
     * @param code code
     * @return sub flox
     */
    private SubFlox getSubFlox(String code) {
        NodeEntity node = AssertUtils.assertNonNull(nodeManager.getMetaSubFlox(code), "Sub flox cannot be null, code is [" + code + "]");
        AssertUtils.assertTrue(node.nodeType() == NodeType.SUB_FLOX, "Flox only accept sub flox, but receive [" + node.nodeType().getCode() + "]");
        return (SubFlox) node.node();
    }
}
