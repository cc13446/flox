package com.cc.flox.utils.template.fragment.impl;

import com.cc.flox.utils.template.TemplateContext;
import com.cc.flox.utils.template.fragment.Fragment;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author cc
 * @date 2024/4/20
 */
@AllArgsConstructor
public class MixedFragment implements Fragment {

    /**
     * 语句内容
     */
    private final List<Fragment> contents;

    @Override
    public boolean apply(TemplateContext context) {
        boolean res = false;
        for (Fragment f : contents) {
            res = res || f.apply(context);
        }
        return res;
    }

}
