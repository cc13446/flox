package com.cc.flox.config;

import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author cc
 * @date 2024/4/29
 */
@Configuration
public class BeetlConfig {

    @Bean
    public GroupTemplate groupTemplate() {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            org.beetl.core.Configuration cfg = org.beetl.core.Configuration.defaultConfiguration();
            cfg.setStatementStart("@");
            cfg.setStatementEnd(null);
            cfg.setPlaceholderStart("#");
            cfg.setPlaceholderEnd("#");
            cfg.setPlaceholderStart2("${");
            cfg.setPlaceholderEnd2("}");
            cfg.setErrorHandlerClass("org.beetl.core.ReThrowConsoleErrorHandler");
            cfg.setHtmlTagSupport(false);
            updateFnMap(cfg);
            return new GroupTemplate(resourceLoader, cfg);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * 更新fn map
     *
     * @param cfg Configuration
     */
    private static void updateFnMap(org.beetl.core.Configuration cfg) {
        Map<String, String> fnMap = Optional.ofNullable(cfg.getFnMap()).orElse(new HashMap<>());
        fnMap.put("join", "com.cc.flox.utils.beetl.fn.JoinFunction");
        fnMap.put("str", "com.cc.flox.utils.beetl.fn.StrFunction");
        cfg.setFnMap(fnMap);
    }
}
