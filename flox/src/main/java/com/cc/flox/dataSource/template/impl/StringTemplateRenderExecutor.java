package com.cc.flox.dataSource.template.impl;

import com.cc.flox.dataSource.template.TemplateType;
import com.cc.flox.dataSource.template.TemplateRenderContext;
import com.cc.flox.dataSource.template.TemplateRenderExecutor;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * String Template SQL 渲染执行器
 *
 * @author cc
 * @date 2024/4/12
 */
@Component
public class StringTemplateRenderExecutor implements TemplateRenderExecutor {

    @Override
    public TemplateRenderContext invoke(TemplateRenderContext context) {
        STGroup group = getSTGroup(context);
        ST st = group.getInstanceOf(context.getAction().getCode());
        for (Map.Entry<String, Object> entry : context.getParam().entrySet()) {
            st.add(entry.getKey(), entry.getValue());
        }
        context.setRenderedSQL(st.render());
        return context;
    }

    /**
     * @param context context
     * @return STGroup
     */
    private STGroup getSTGroup(TemplateRenderContext context) {
        STGroup sql = new STGroupString(context.getAction().getSql());
        AtomicInteger index = new AtomicInteger(1);
        context.setRenderedParam(new LinkedList<>());
        context.setUseQuestionMark(false);
        // 自定义属性渲染器
        AttributeRenderer attributeRenderer = (o, format, locale) -> {
            if ("p".equals(format)) {
                String key = "$" + index.getAndIncrement();
                context.getRenderedParam().add(o);
                return key;
            }
            return o.toString();
        };
        sql.registerRenderer(String.class, attributeRenderer);
        sql.registerRenderer(Number.class, attributeRenderer);
        sql.registerRenderer(Date.class, attributeRenderer);
        return sql;
    }

    @Override
    public boolean match(TemplateRenderContext templateRenderContext) {
        return TemplateType.StringTemplate.equals(templateRenderContext.getAction().getTemplateType());
    }
}
