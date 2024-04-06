package com.cc.flox.meta;

import com.cc.flox.domain.builder.FloxBuilder;
import com.cc.flox.service.ServiceManager;
import com.cc.flox.web.endpoint.HttpEndPoint;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


/**
 * 元流程初始化器
 * @author cc
 * @date 2024/4/2
 */
@Order(2)
@Component
public class MetaFloxInitializer implements CommandLineRunner {
    @Resource
    private ServiceManager serviceManager;

    @Override
    public void run(String... args) throws Exception {
        FloxBuilder builder = new FloxBuilder().setRequestExtractorBuilder(() -> r ->
                Mono.just(r.getQueryParams().entrySet().stream()
                        .map(e -> e.getKey() + "=[" + e.getValue().stream().reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append).toString() + "]\n")
                        .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append).toString())
        ).setResponseLoaderBuilder(() -> (t, r) -> {
            r.setStatusCode(HttpStatus.OK);
            String res = (String) t;
            DataBuffer dataBuffer = r.bufferFactory().allocateBuffer(res.length());
            // 获得 OutputStream 的引用
            try (OutputStream outputStream = dataBuffer.asOutputStream()) {
                outputStream.write(res.getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
            return r.writeWith(Mono.just(dataBuffer));
        });
        HttpEndPoint echoEndPoint = new HttpEndPoint("/echo", builder.builder());
        serviceManager.insertHandler(echoEndPoint).get();
    }
}
