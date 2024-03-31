package com.cc.flox.utils.trie;

import com.cc.flox.utils.trie.command.TrieDeleteCommand;
import com.cc.flox.utils.trie.command.TrieInsertCommand;
import com.cc.flox.utils.trie.command.TrieUpdateCommand;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 前缀树节点
 *
 * @author cc
 * @date 2024/3/30
 */
public class TrieNode<T> {

    /**
     * 下级节点
     */
    private final TrieNode<T>[] nodes;

    /**
     * 本节点的子节点数量
     */
    private final AtomicInteger size = new AtomicInteger(0);

    /**
     * 值
     */
    private volatile T value;

    @SuppressWarnings("unchecked")
    public TrieNode(int nodeLength) {
        this.nodes = (TrieNode<T>[]) Array.newInstance(TrieNode.class, nodeLength);
    }

    /**
     * 获取key对应的值
     *
     * @param key   key
     * @param depth 深度
     * @param trie  trie
     * @return value 如果不存在则为 null
     */
    public T get(String key, int depth, Trie<T> trie) {
        if (depth == key.length()) {
            return value;
        }

        TrieNode<T> node = getChildNode(key, depth, trie);
        if (Objects.isNull(node)) {
            return null;
        }
        return node.get(key, depth + 1, trie);
    }

    /**
     * 更新
     *
     * @param command command
     * @param depth   深度
     * @param trie    trie
     */
    public void update(TrieUpdateCommand<T> command, int depth, Trie<T> trie) {
        if (command.getKey().length() == depth) {
            T newValue = command.getUpdate().apply(this.value);
            if (Objects.isNull(newValue)) {
                throw new RuntimeException("Update trie error: value is null, please use delete command");
            }
            this.value = newValue;
            return;
        }
        TrieNode<T> node = getChildNode(command.getKey(), depth, trie);
        if (Objects.isNull(node)) {
            throw new RuntimeException("Update trie error: key [" + command.getKey() + "] is not exist");
        }
        node.update(command, depth + 1, trie);
    }

    /**
     * 插入
     *
     * @param command command
     * @param depth   深度
     * @param trie    trie
     */
    public void insert(TrieInsertCommand<T> command, int depth, Trie<T> trie) {
        if (command.getKey().length() == depth) {
            this.value = command.getValue();
            return;
        }
        int index = getChildNodeIndex(command.getKey(), depth, trie);
        if (Objects.isNull(this.nodes[index])) {
            this.nodes[index] = new TrieNode<>(trie.getNodeLength());
            this.size.incrementAndGet();
        }
        this.nodes[index].insert(command, depth + 1, trie);
    }

    /**
     * 删除
     *
     * @param command command
     * @param depth   深度
     * @param trie    trie
     * @return 此节点是否为空
     */
    public boolean delete(TrieDeleteCommand<T> command, int depth, Trie<T> trie) {
        if (command.getKey().length() == depth) {
            this.value = null;
            return this.size.get() == 0;
        }
        int index = getChildNodeIndex(command.getKey(), depth, trie);
        if (Objects.isNull(this.nodes[index])) {
            throw new RuntimeException("Delete trie error: key [" + command.getKey() + "] is not exist");
        }
        boolean childIsNull = this.nodes[index].delete(command, depth + 1, trie);
        if (childIsNull) {
            this.nodes[index] = null;
            return Objects.isNull(this.value) && this.size.decrementAndGet() == 0;
        } else {
            return false;
        }
    }

    /**
     * @param key   key
     * @param depth depth
     * @param trie  trie
     * @return 下一级 Node
     */
    private TrieNode<T> getChildNode(String key, int depth, Trie<T> trie) {
        int index = getChildNodeIndex(key, depth, trie);
        return this.nodes[index];
    }

    /**
     * @param key   key
     * @param depth depth
     * @param trie  trie
     * @return node index
     */
    private int getChildNodeIndex(String key, int depth, Trie<T> trie) {
        return trie.getNodeIndex(key.charAt(depth));
    }
}
