package com.cc.flox.utils.template.tag.impl;

import com.cc.flox.utils.template.TemplateBuilder;
import com.cc.flox.utils.template.fragment.Fragment;
import com.cc.flox.utils.template.fragment.impl.MixedFragment;
import com.cc.flox.utils.template.fragment.impl.TrimFragment;
import com.cc.flox.utils.template.tag.TagHandler;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Optional;

/**
 * @author cc
 * @date 2024/4/29
 */
public class TrimHandler implements TagHandler {

    @Override
    public void handleNode(Node nodeToHandle, List<Fragment> targetContents) {
        List<Fragment> contents = TemplateBuilder.Builder.buildDynamicTag(nodeToHandle);
        MixedFragment mixedFragment = new MixedFragment(contents);
        NamedNodeMap attributes = nodeToHandle.getAttributes();

        Node prefixAtt = attributes.getNamedItem("prefix");
        String prefix = Optional.ofNullable(prefixAtt).map(Node::getTextContent).orElse(null);

        Node prefixOverridesAtt = attributes.getNamedItem("prefixOverrides");
        String prefixOverrides = Optional.ofNullable(prefixOverridesAtt).map(Node::getTextContent).orElse(null);

        Node suffixAtt = attributes.getNamedItem("suffix");
        String suffix = Optional.ofNullable(suffixAtt).map(Node::getTextContent).orElse(null);

        Node suffixOverridesAtt = attributes.getNamedItem("suffixOverrides");
        String suffixOverrides = Optional.ofNullable(suffixOverridesAtt).map(Node::getTextContent).orElse(null);

        TrimFragment trim = new TrimFragment(mixedFragment, prefix, suffix, prefixOverrides, suffixOverrides);
        targetContents.add(trim);
    }
}
