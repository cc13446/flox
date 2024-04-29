package com.cc.flox.utils.template.tag.impl;

import com.cc.flox.utils.template.TemplateBuilder;
import com.cc.flox.utils.template.fragment.Fragment;
import com.cc.flox.utils.template.fragment.impl.IfFragment;
import com.cc.flox.utils.template.fragment.impl.MixedFragment;
import com.cc.flox.utils.template.tag.TagHandler;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Objects;

/**
 * @author cc
 * @date 2024/4/29
 */
public class IfHandler implements TagHandler {

    @Override
    public void handleNode(Node nodeToHandle, List<Fragment> targetContents) {
        List<Fragment> contents = TemplateBuilder.Builder.buildDynamicTag(nodeToHandle);
        MixedFragment mixedSqlFragment = new MixedFragment(contents);

        NamedNodeMap attributes = nodeToHandle.getAttributes();

        Node testAtt = attributes.getNamedItem("test");
        if (Objects.isNull(testAtt)) {
            throw new RuntimeException(nodeToHandle.getNodeName() + " must has test attribute ! ");
        }

        String test = testAtt.getTextContent();
        IfFragment ifSqlFragment = new IfFragment(test, mixedSqlFragment);
        targetContents.add(ifSqlFragment);
    }
}
