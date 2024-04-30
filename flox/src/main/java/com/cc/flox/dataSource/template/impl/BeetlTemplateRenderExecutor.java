package com.cc.flox.dataSource.template.impl;

import com.cc.flox.dataSource.template.TemplateRenderContext;
import com.cc.flox.dataSource.template.TemplateRenderExecutor;
import com.cc.flox.dataSource.template.TemplateType;
import com.cc.flox.utils.beetl.CustomPlaceHolderRender;
import jakarta.annotation.Resource;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.statement.PlaceholderST;
import org.springframework.stereotype.Component;

/**
 * beetl sql 模板
 *
 * @author cc
 * @date 2024/4/29
 */
@Component
public class BeetlTemplateRenderExecutor implements TemplateRenderExecutor {

    private final static CustomPlaceHolderRender holderRender = new CustomPlaceHolderRender();

    static {
        PlaceholderST.output = holderRender;
    }

    @Resource
    private GroupTemplate groupTemplate;

    @Override
    public TemplateRenderContext invoke(TemplateRenderContext context) {
        holderRender.reset();
        Template template = groupTemplate.getTemplate(context.getAction().getSql());
        template.binding(context.getParam());
        template.getCtx().set(CustomPlaceHolderRender.PLACE_HOLDER_TYPE_KEY, context.getDataSourceType().getPlaceHolderType());
        context.setRenderedSQL(template.render());
        context.setRenderedParam(holderRender.getParam());
        return context;
    }

    @Override
    public boolean match(TemplateRenderContext templateRenderContext) {
        return TemplateType.Beetl.equals(templateRenderContext.getAction().getTemplateType());
    }
}
