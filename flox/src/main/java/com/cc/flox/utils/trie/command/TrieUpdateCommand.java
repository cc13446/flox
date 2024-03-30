package com.cc.flox.utils.trie.command;

import lombok.Getter;

import java.util.function.Function;

/**
 * 前缀树更新命令
 *
 * @author cc
 * @date 2024/3/30
 */
@Getter
public class TrieUpdateCommand<T> extends TrieCommand<T> {

    /**
     * 更新逻辑
     */
    private final Function<T, T> update;

    public TrieUpdateCommand(String key, Function<T, T> update) {
        super(key);
        this.update = update;
    }
}
