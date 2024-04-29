package com.cc.flox.dataSource.template.impl;

import com.cc.flox.dataSource.template.TemplateRenderContext;
import com.cc.flox.dataSource.template.TemplateRenderExecutor;
import com.cc.flox.dataSource.template.TemplateType;
import com.cc.flox.utils.template.Template;
import com.cc.flox.utils.template.TemplateBuilder;
import com.cc.flox.utils.template.TemplateContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * 兼容 Mybatis 语法的 SQL 模板
 *
 * @author cc
 * @date 2024/4/28
 */
@Component
public class MybatisTemplateRenderExecutor implements TemplateRenderExecutor {

    /**
     * 模板构造器
     */
    private final TemplateBuilder templateBuilder;

    @Autowired
    public MybatisTemplateRenderExecutor(ResourceLoader resourceLoader) {
        this.templateBuilder = new TemplateBuilder(resourceLoader);
    }

    @Override
    public TemplateRenderContext invoke(TemplateRenderContext context) {
        Template template = templateBuilder.getTemplate(context.getAction().getSql());
        TemplateContext res = template.process(context.getParam());
        context.setRenderedSQL(res.getResult());
        context.setUseQuestionMark(false);
        context.setRenderedParam(res.getParameter());
        return context;
    }

    @Override
    public boolean match(TemplateRenderContext context) {
        return TemplateType.Mybatis.equals(context.getAction().getTemplateType());
    }
}
