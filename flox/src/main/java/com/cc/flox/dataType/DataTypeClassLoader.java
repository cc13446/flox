package com.cc.flox.dataType;

import com.cc.flox.meta.Constant;
import com.cc.flox.node.NodeManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.cc.flox.initializer.meta.MetaSubFloxInitializer.META_SUB_FLOX_CODE_SELECT_DATA_TYPE;
import static com.cc.flox.utils.FormatUtils.YYYY_MM_DD_HH_MM_SS;

/**
 * 带有用户定义的数据类型的类加载器
 *
 * @author cc
 * @date 2024/5/1
 */
@Slf4j
@Component
public class DataTypeClassLoader {
    /**
     * 数据类型字节码 map
     */
    private final Map<String, byte[]> classMap = new ConcurrentHashMap<>();

    /**
     * 是否启动
     */
    private final AtomicBoolean hasStart = new AtomicBoolean(false);

    /**
     * 更新时间
     */
    private final AtomicReference<OffsetDateTime> updateTime = new AtomicReference<>(OffsetDateTime.MIN);

    @Resource
    private NodeManager nodeManager;

    /**
     * 开始同步
     */
    public void startSynchronize() {
        if (hasStart.compareAndSet(false, true)) {
            doSynchronize();
        }
    }

    /**
     * 同步数据源
     */
    @SuppressWarnings("unchecked")
    private void doSynchronize() {
        log.info("Start synchronize data type, {}", updateTime.get().format(YYYY_MM_DD_HH_MM_SS));
        nodeManager.getMetaSubFlox(META_SUB_FLOX_CODE_SELECT_DATA_TYPE).exec(Mono.just(Map.of(Constant.UPDATE_TIME, updateTime.get()))).subscribe(l -> {
            try {
                List<Map<String, Object>> nodes = (List<Map<String, Object>>) l;
                Map<String, String> map = nodes.stream().collect(Collectors.toMap(m -> m.get(Constant.PATH).toString(), m -> m.get(Constant.CONTENT).toString()));
                for (Map.Entry<String, String> entry : map.entrySet()) {

                }
                this.updateTime.updateAndGet(t -> nodes.stream()
                        .filter(m -> Objects.nonNull(m.get(Constant.UPDATE_TIME)))
                        .map(m -> (OffsetDateTime) m.get(Constant.UPDATE_TIME))
                        .max(OffsetDateTime::compareTo).orElse(t));
            } catch (Exception e) {
                log.error("Synchronize data type error : ", e);
            } finally {
                Mono.delay(Duration.ofSeconds(10)).subscribe(i -> doSynchronize());
            }
        });
    }

}
