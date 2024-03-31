package com.cc.flox.utils.trie.command;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;

/**
 * 前缀树命令
 * @author cc
 * @date 2024/3/30
 */
@Getter
public abstract class TrieCommand<T> {

    /**
     * key
     */
    private final String key;

    /**
     * future
     */
    private final CompletableFuture<Void> future = new CompletableFuture<>();

    public TrieCommand(String key) {
        this.key = key;
    }
}
