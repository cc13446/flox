package com.cc.flox.initializer.meta;

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

import static com.cc.flox.initializer.meta.MetaRequestExtractorInitializer.*;
import static com.cc.flox.initializer.meta.MetaResponseLoaderInitializer.META_RESPONSE_LOADER_CODE_WRITE_JSON;
import static com.cc.flox.initializer.meta.MetaSubFloxInitializer.*;


/**
 * 元流程初始化器
 *
 * @author cc
 * @date 2024/4/2
 */
@Order(6)
@Component
public class MetaFloxInitializer implements CommandLineRunner {

    @Resource
    private ApiManager apiManager;

    @Resource
    private NodeManager nodeManager;

    @Override
    public void run(String... args) throws Exception {
        apiManager.insertHandler(getEchoEndPoint()).get();
        apiManager.insertHandler(getBaseInsertEndPoint(META_SUB_FLOX_CODE_INSERT_DATA_SOURCE, "/data-source/insert")).get();
        apiManager.insertHandler(getBaseUpdateEndPoint(META_SUB_FLOX_CODE_UPDATE_DATA_SOURCE, "/data-source/update")).get();
        apiManager.insertHandler(getBaseSelectEndPoint(META_SUB_FLOX_CODE_SELECT_DATA_SOURCE, "/data-source/select")).get();
        apiManager.insertHandler(getBaseInsertEndPoint(META_SUB_FLOX_CODE_INSERT_DATA_SOURCE_ACTION, "/data-source/action/insert")).get();
        apiManager.insertHandler(getBaseUpdateEndPoint(META_SUB_FLOX_CODE_UPDATE_DATA_SOURCE_ACTION, "/data-source/action/update")).get();
        apiManager.insertHandler(getBaseSelectEndPoint(META_SUB_FLOX_CODE_SELECT_DATA_SOURCE_ACTION, "/data-source/action/select")).get();
        apiManager.insertHandler(getBaseInsertEndPoint(META_SUB_FLOX_CODE_INSERT_DATA_TYPE, "/data-type/insert")).get();
        apiManager.insertHandler(getBaseSelectEndPoint(META_SUB_FLOX_CODE_SELECT_DATA_TYPE, "/data-type/select")).get();
        apiManager.insertHandler(getBaseInsertEndPoint(META_SUB_FLOX_CODE_INSERT_NODE, "/node/insert")).get();
        apiManager.insertHandler(getBaseUpdateEndPoint(META_SUB_FLOX_CODE_UPDATE_NODE, "/node/update")).get();
        apiManager.insertHandler(getBaseSelectEndPoint(META_SUB_FLOX_CODE_SELECT_NODE, "/node/select")).get();
        apiManager.insertHandler(getBaseInsertEndPoint(META_SUB_FLOX_CODE_INSERT_NODE_RELATION, "/node/relation/insert")).get();
        apiManager.insertHandler(getBaseUpdateEndPoint(META_SUB_FLOX_CODE_UPDATE_NODE_RELATION, "/node/relation/update")).get();
        apiManager.insertHandler(getBaseSelectEndPoint(META_SUB_FLOX_CODE_SELECT_NODE_RELATION, "/node/relation/select")).get();
        apiManager.insertHandler(getBaseInsertEndPoint(META_SUB_FLOX_CODE_INSERT_FLOX, "/flox/insert")).get();
        apiManager.insertHandler(getBaseUpdateEndPoint(META_SUB_FLOX_CODE_UPDATE_FLOX, "/flox/update")).get();
        apiManager.insertHandler(getBaseSelectEndPoint(META_SUB_FLOX_CODE_SELECT_FLOX, "/flox/select")).get();
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
     * @param subFloxCode 子流程code
     * @param path        路径
     * @return base insert end point
     */
    private ApiEndPoint getBaseInsertEndPoint(String subFloxCode, String path) {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_BODY_PARAMS_TO_LIST_MAP))
                .setSubFloxBuilder(() -> getSubFlox(subFloxCode))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint(path, ApiMethod.POST, builder.builder());
    }

    /**
     * @param subFloxCode 子流程code
     * @param path        路径
     * @return base update end point
     */
    private ApiEndPoint getBaseUpdateEndPoint(String subFloxCode, String path) {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_BODY_PARAMS_TO_MAP))
                .setSubFloxBuilder(() -> getSubFlox(subFloxCode))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint(path, ApiMethod.POST, builder.builder());
    }

    /**
     * @param subFloxCode 子流程code
     * @param path        路径
     * @return base select end point
     */
    private ApiEndPoint getBaseSelectEndPoint(String subFloxCode, String path) {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> nodeManager.getRequestExtract(META_REQUEST_EXTRACTOR_CODE_QUERY_PARAMS_TO_MAP))
                .setSubFloxBuilder(() -> getSubFlox(subFloxCode))
                .setResponseLoaderBuilder(() -> nodeManager.getResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON));
        return new ApiEndPoint(path, ApiMethod.GET, builder.builder());
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
