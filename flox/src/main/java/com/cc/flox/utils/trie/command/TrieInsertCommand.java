package com.cc.flox.utils.trie.command;

import lombok.Getter;

/**
 * 前缀树插入命令
 *
 * @author cc
 * @date 2024/3/30
 */
@Getter
public class TrieInsertCommand<T> extends TrieCommand<T> {

    /**
     * 插入值
     */
    private final T value;

    public TrieInsertCommand(String key, T value) {
        super(key);
        this.value = value;
    }
}
