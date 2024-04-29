package com.cc.flox.utils.template.fragment;

import com.cc.flox.utils.template.TemplateContext;

/**
 * 语句
 *
 * @author cc
 * @date 2024/4/29
 */
public interface Fragment {

    /**
     * @param context 上下文
     * @return 是否将此语句应用到上下文中
     */
    boolean apply(TemplateContext context);

}
