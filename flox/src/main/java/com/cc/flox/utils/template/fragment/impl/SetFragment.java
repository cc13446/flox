package com.cc.flox.utils.template.fragment.impl;

import com.cc.flox.utils.template.fragment.Fragment;

import java.util.List;

/**
 * @author cc
 * @date 2024/4/29
 */
public class SetFragment extends TrimFragment {

    private static final List<String> suffixList = List.of(",");

    public SetFragment(Fragment contents) {
        super(contents, "SET", null, null, suffixList);
    }

}