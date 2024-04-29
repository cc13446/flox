package com.cc.flox.utils.template.tag.impl;

import com.cc.flox.utils.template.TemplateBuilder;
import com.cc.flox.utils.template.fragment.Fragment;
import com.cc.flox.utils.template.fragment.impl.ForeachFragment;
import com.cc.flox.utils.template.fragment.impl.MixedFragment;
import com.cc.flox.utils.template.tag.TagHandler;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author cc
 * @date 2024/4/29
 */
public class ForEachHandler implements TagHandler {
    public void handleNode(Node nodeToHandle, List<Fragment> targetContents) {
        List<Fragment> contents = TemplateBuilder.Builder.buildDynamicTag(nodeToHandle);
        MixedFragment mixedSqlFragment = new MixedFragment(contents);
        NamedNodeMap attributes = nodeToHandle.getAttributes();
        Node collectionAtt = attributes.getNamedItem("collection");

        if (Objects.isNull(collectionAtt)) {
            throw new RuntimeException(nodeToHandle.getNodeName() + " must has a collection attribute !");
        }

        String collection = collectionAtt.getTextContent();
        Node itemAtt = attributes.getNamedItem("item");
        String item = Optional.ofNullable(itemAtt).map(Node::getTextContent).orElse("item");

        Node indexAtt = attributes.getNamedItem("index");
        String index = Optional.ofNullable(indexAtt).map(Node::getTextContent).orElse("index");

        Node openAtt = attributes.getNamedItem("open");
        String open = Optional.ofNullable(openAtt).map(Node::getTextContent).orElse(null);

        Node closeAtt = attributes.getNamedItem("close");
        String close = Optional.ofNullable(closeAtt).map(Node::getTextContent).orElse(null);

        Node separatorAtt = attributes.getNamedItem("separator");
        String separator = Optional.ofNullable(separatorAtt).map(Node::getTextContent).orElse(null);

        ForeachFragment forEachSqlFragment = new ForeachFragment(mixedSqlFragment, collection, index, item, open, close, separator);
        targetContents.add(forEachSqlFragment);
    }
}