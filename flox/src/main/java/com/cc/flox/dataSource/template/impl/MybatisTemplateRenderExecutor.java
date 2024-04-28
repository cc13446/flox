package com.cc.flox.dataSource.template.impl;

import com.cc.flox.dataSource.template.TemplateRenderContext;
import com.cc.flox.dataSource.template.TemplateRenderExecutor;
import com.cc.flox.dataSource.template.TemplateType;
import org.springframework.stereotype.Component;

/** 兼容 Mybatis 语法的 SQL 模板
 * @author cc
 * @date 2024/4/28
 */
@Component
public class MybatisTemplateRenderExecutor implements TemplateRenderExecutor {
    @Override
    public TemplateRenderContext invoke(TemplateRenderContext context) {
        return null;
    }

    @Override
    public boolean match(TemplateRenderContext context) {
        return TemplateType.Mybatis.equals(context.getAction().getTemplateType());
    }
}
