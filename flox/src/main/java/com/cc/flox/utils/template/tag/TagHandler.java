package com.cc.flox.utils.template.tag;

import com.cc.flox.utils.template.fragment.Fragment;
import org.w3c.dom.Node;

import java.util.List;

/**
 * xml 标签处理器
 *
 * @author cc
 * @date 2024/4/29
 */
public interface TagHandler {

    /**
     * @param node           xml node
     * @param targetContents 目标语句
     */
    void handleNode(Node node, List<Fragment> targetContents);
}
