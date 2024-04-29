package com.cc.flox.utils.template.fragment.impl;

import com.cc.flox.utils.template.TemplateContext;
import com.cc.flox.utils.template.fragment.Fragment;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * @author cc
 * @date 2024/4/29
 */
@AllArgsConstructor
public class ChooseFragment implements Fragment {

    /**
     * otherwise
     */
    private final Fragment defaultFragment;
    /**
     * when
     */
    private final List<Fragment> whenFragments;

    @Override
    public boolean apply(TemplateContext context) {
        for (Fragment sqlNode : whenFragments) {
            if (sqlNode.apply(context)) {
                // 命中这个 when
                return true;
            }
        }
        if (Objects.nonNull(defaultFragment)) {
            defaultFragment.apply(context);
            return true;
        }
        return false;
    }

}

