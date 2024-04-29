package com.cc.flox.utils.template.tag.impl;

import com.cc.flox.utils.template.fragment.Fragment;
import com.cc.flox.utils.template.fragment.impl.ChooseFragment;
import com.cc.flox.utils.template.tag.TagHandler;
import com.cc.flox.utils.template.tag.TagHandlerManager;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cc
 * @date 2024/4/29
 */
public class ChooseHandler implements TagHandler {
    @Override
    public void handleNode(Node nodeToHandle, List<Fragment> targetContents) {
        List<Fragment> whenSqlFragments = new ArrayList<>();
        List<Fragment> otherwiseSqlFragments = new ArrayList<>();
        handleWhenOtherwiseNodes(nodeToHandle, whenSqlFragments, otherwiseSqlFragments);
        Fragment defaultSqlFragment = getDefaultSqlFragment(otherwiseSqlFragments);
        ChooseFragment chooseSqlFragment = new ChooseFragment(defaultSqlFragment, whenSqlFragments);
        targetContents.add(chooseSqlFragment);
    }


    /**
     * @param chooseFragment   chooseFragment
     * @param ifFragments      ifFragments
     * @param defaultFragments defaultFragments
     */
    private void handleWhenOtherwiseNodes(Node chooseFragment, List<Fragment> ifFragments, List<Fragment> defaultFragments) {
        NodeList children = chooseFragment.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                TagHandler handler = TagHandlerManager.TAG_HANDLER_MAP.get(nodeName);
                if (handler instanceof IfHandler) {
                    handler.handleNode(child, ifFragments);
                } else if (handler instanceof OtherwiseHandler) {
                    handler.handleNode(child, defaultFragments);
                }
            }
        }
    }


    /**
     * @param defaultFragments defaultFragments
     * @return defaultFragment
     */
    private Fragment getDefaultSqlFragment(List<Fragment> defaultFragments) {
        Fragment defaultFragment = null;
        if (defaultFragments.size() == 1) {
            defaultFragment = defaultFragments.getFirst();
        } else if (defaultFragments.size() > 1) {
            throw new RuntimeException("Too many default (otherwise) elements in choose statement.");
        }
        return defaultFragment;
    }
}
