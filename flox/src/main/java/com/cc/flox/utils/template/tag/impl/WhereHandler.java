package com.cc.flox.utils.template.tag.impl;

import com.cc.flox.utils.template.TemplateBuilder;
import com.cc.flox.utils.template.fragment.Fragment;
import com.cc.flox.utils.template.fragment.impl.MixedFragment;
import com.cc.flox.utils.template.fragment.impl.WhereFragment;
import com.cc.flox.utils.template.tag.TagHandler;
import org.w3c.dom.Node;

import java.util.List;

/**
 * @author cc
 * @date 2024/4/29
 */
public class WhereHandler implements TagHandler {

    @Override
    public void handleNode(Node nodeToHandle, List<Fragment> targetContents) {
        List<Fragment> contents = TemplateBuilder.Builder.buildDynamicTag(nodeToHandle);
        MixedFragment mixedSqlFragment = new MixedFragment(contents);
        WhereFragment where = new WhereFragment(mixedSqlFragment);
        targetContents.add(where);
    }
}
