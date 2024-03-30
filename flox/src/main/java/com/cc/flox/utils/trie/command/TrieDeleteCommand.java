package com.cc.flox.utils.trie.command;

/**
 * 前缀树删除命令
 * @author cc
 * @date 2024/3/30
 */
public class TrieDeleteCommand<T> extends TrieCommand<T> {

    public TrieDeleteCommand(String key) {
        super(key);
    }
}
