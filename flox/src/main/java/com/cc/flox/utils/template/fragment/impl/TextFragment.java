package com.cc.flox.utils.template.fragment.impl;

import com.cc.flox.utils.template.OgnlUtils;
import com.cc.flox.utils.template.TemplateContext;
import com.cc.flox.utils.template.fragment.Fragment;
import com.cc.flox.utils.template.placeholder.PlaceHolderParser;
import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * @author cc
 * @date 2024/4/29
 */
@AllArgsConstructor
public class TextFragment implements Fragment {

    /**
     * 文本内容
     */
    private final String content;

    @Override
    public boolean apply(final TemplateContext context) {

        PlaceHolderParser parser = new PlaceHolderParser("${", "}", (p) -> {
            Object v = OgnlUtils.parseExpression(p, context.getBinding());
            if (Objects.isNull(v)) {
                return "";
            }
            return v.toString();
        });
        context.append(parser.parse(content));
        return true;
    }
}

