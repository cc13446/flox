package com.cc.flox.utils.template.fragment.impl;

import com.cc.flox.utils.template.fragment.Fragment;

/**
 * @author cc
 * @date 2024/4/29
 */
public class WhenFragment extends IfFragment {

    public WhenFragment(Fragment content, String test) {
        super(test, content);
    }
}
