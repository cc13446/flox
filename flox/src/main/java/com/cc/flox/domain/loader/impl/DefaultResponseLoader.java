package com.cc.flox.domain.loader.impl;

import com.cc.flox.domain.loader.ResponseLoader;
import com.cc.flox.result.Response;
import com.cc.flox.utils.GsonUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author cc
 * @date 2024/4/20
 */
public class DefaultResponseLoader implements ResponseLoader<Object> {
    @Override
    public Mono<Void> loader(Mono<Object> source, Mono<ServerHttpResponse> destination) {
        return Mono.zip(source, destination).publishOn(Schedulers.boundedElastic()).handle((t, sink) -> {
            t.getT2().setStatusCode(HttpStatus.OK);
            String res = GsonUtils.INS.toJson(Response.success(t.getT1()));
            DataBuffer dataBuffer = t.getT2().bufferFactory().allocateBuffer(res.length());
            try (OutputStream outputStream = dataBuffer.asOutputStream()) {
                outputStream.write(res.getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                sink.error(new RuntimeException(ioException));
                return;
            }
            t.getT2().writeWith(Mono.just(dataBuffer)).block();
            sink.complete();
        });
    }
}
