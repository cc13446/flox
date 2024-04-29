package com.cc.flox.utils.template.fragment.impl;

import com.cc.flox.utils.template.fragment.Fragment;

import java.util.Arrays;
import java.util.List;

/**
 * @author cc
 * @date 2024/4/29
 */
public class WhereFragment extends TrimFragment {

    private static final List<String> prefixList = Arrays.asList("AND ", "OR ", "AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");

    public WhereFragment(Fragment contents) {
        super(contents, "WHERE", null, prefixList, null);
    }

}
