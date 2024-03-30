package com.cc.flox.utils.trie.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 前缀树命令
 * @author cc
 * @date 2024/3/30
 */
@AllArgsConstructor
@Getter
public abstract class TrieCommand<T> {

    /**
     * key
     */
    private String key;

}
