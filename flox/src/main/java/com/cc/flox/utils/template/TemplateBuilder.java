package com.cc.flox.utils.template;

import com.cc.flox.utils.XXHashUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

/**
 * Mybatis 模板配置
 *
 * @author cc
 * @date 2024/4/28
 */
@Slf4j
public class TemplateBuilder {

    /**
     * 模板缓存
     */
    private final ConcurrentHashMap<Long, FutureTask<Template>> templateCache;

    /**
     * 是否开启模板缓存
     */
    private final boolean cacheTemplate;

    public TemplateBuilder() {
        this(true);
    }

    public TemplateBuilder(boolean cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
        this.templateCache = new ConcurrentHashMap<>();
    }

    /**
     * @param content 模板内容
     * @return 模板
     */
    public Template getTemplate(final String content) {
        long hash = XXHashUtils.hash(content);
        if (cacheTemplate) {
            FutureTask<Template> f = templateCache.get(hash);
            if (Objects.isNull(f)) {
                FutureTask<Template> ft = new FutureTask<>(() -> createTemplate(content));
                f = templateCache.putIfAbsent(hash, ft);
                if (Objects.isNull(f)) {
                    ft.run();
                    f = ft;
                }
            }
            try {
                return f.get();
            } catch (Exception e) {
                templateCache.remove(hash);
                throw new RuntimeException(e);
            }
        }
        return createTemplate(content);
    }

    /**
     * @param content 模板内容
     * @return 模板
     */
    private Template createTemplate(String content) {
        return null;
    }
}
