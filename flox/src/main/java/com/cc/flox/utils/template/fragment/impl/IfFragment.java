package com.cc.flox.utils.template.fragment.impl;

import com.cc.flox.utils.template.OgnlUtils;
import com.cc.flox.utils.template.TemplateContext;
import com.cc.flox.utils.template.fragment.Fragment;
import lombok.AllArgsConstructor;

/**
 * @author cc
 * @date 2024/4/29
 */
@AllArgsConstructor
public class IfFragment implements Fragment {

    private final String test;

    private final Fragment content;

    @Override
    public boolean apply(TemplateContext context) {
        if (OgnlUtils.evaluateBoolean(test, context.getBinding())) {
            this.content.apply(context);
            return true;
        }
        return false;
    }
}

