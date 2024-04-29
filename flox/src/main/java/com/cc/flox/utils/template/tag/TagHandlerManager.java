package com.cc.flox.utils.template.tag;

import com.cc.flox.utils.template.tag.impl.*;

import java.util.Map;

/**
 * 标签处理器管理者
 *
 * @author cc
 * @date 2024/4/29
 */
public class TagHandlerManager {

    /**
     * handler map
     */
    public static final Map<String, TagHandler> TAG_HANDLER_MAP = Map.of(
            "trim", new TrimHandler(),
            "where", new WhereHandler(),
            "set", new SetHandler(),
            "foreach", new ForEachHandler(),
            "if", new IfHandler(),
            "choose", new ChooseHandler(),
            "when", new IfHandler(),
            "otherwise", new OtherwiseHandler()
    );

}
