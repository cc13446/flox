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

/**
 * String Template SQL 渲染执行器
 *
 * @author cc
 * @date 2024/4/12
 */
@Component
public class StringTemplateRenderExecutor implements TemplateRenderExecutor {

    /**
     * string template 渲染 sql 时需要的上下文
     */
    static final class StringTemplateRenderContext {
        /**
         * 参数下标
         */
        int paramIndex = 1;

        Map<String, Object> paramMap = new HashMap<>();

    }

    /**
     * SQL 渲染时的上下文
     */
    private final ThreadLocal<StringTemplateRenderContext> CONTEXT = new ThreadLocal<>();

    @Override
    public TemplateRenderContext invoke(TemplateRenderContext context) {
        CONTEXT.set(new StringTemplateRenderContext());
        STGroup group = getSTGroup(context);
        ST st = group.getInstanceOf(context.getAction().getCode());
        for (Map.Entry<String, Object> entry : context.getParam().entrySet()) {
            st.add(entry.getKey(), entry.getValue());
        }
        context.setRenderedParam(Map.copyOf(CONTEXT.get().paramMap));
        context.setRenderedSQL(st.render());
        return null;
    }

    /**
     * @param context context
     * @return STGroup
     */
    private STGroup getSTGroup(TemplateRenderContext context) {
        STGroup sql = new STGroupString(context.getAction().getSql());

        // 自定义属性渲染器
        AttributeRenderer attributeRenderer = (o, s, locale) -> {
            StringTemplateRenderContext stringTemplateRenderContext = CONTEXT.get();
            String key = "$" + stringTemplateRenderContext.paramIndex;
            stringTemplateRenderContext.paramMap.put(key, o);
            return key;
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
