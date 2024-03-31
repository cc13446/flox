package com.cc.flox.utils.trie;

import com.cc.flox.utils.trie.command.TrieCommand;
import com.cc.flox.utils.trie.command.TrieDeleteCommand;
import com.cc.flox.utils.trie.command.TrieInsertCommand;
import com.cc.flox.utils.trie.command.TrieUpdateCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 前缀树
 *
 * @author cc
 * @date 2024/3/30
 */
@Slf4j
public abstract class Trie<T> {

    /**
     * 修改前缀树队列
     */
    private final BlockingQueue<TrieCommand<T>> commandQueue = new LinkedBlockingQueue<>();

    /**
     * 执行命令线程
     */
    private final Thread commandThread;

    /**
     * 是否启动
     */
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * 前缀树节点
     */
    private final TrieNode<T> root;

    public Trie() {
        this.root = new TrieNode<>(getNodeLength());

        this.commandThread = new Thread(() -> {
            while (true) {
                try {
                    this.doCommand();
                } catch (Exception e) {
                    log.error("Do trie command error : ", e);
                }
            }
        });
        this.commandThread.setDaemon(true);
    }

    /**
     * 获取key对应的值
     *
     * @param key key
     * @return value
     */
    public T get(String key) {
        return root.get(key, 0, this);
    }

    /**
     * 修改前缀树
     *
     * @param command command
     */
    public Future<Void> command(TrieCommand<T> command) {
        if (!started.get() && started.compareAndSet(false, true)) {
            this.commandThread.start();
        }
        if (!commandQueue.offer(command)) {
            command.getFuture().completeExceptionally(new RuntimeException("Command Queue offer fail."));
        }
        return command.getFuture();
    }

    /**
     * @return node 长度
     */
    public abstract int getNodeLength();

    /**
     * @return char 对应的node下标
     */
    public abstract int getNodeIndex(char c);

    /**
     * 处理 Command
     */
    private void doCommand() throws InterruptedException {
        TrieCommand<T> command = commandQueue.take();
        switch (command) {
            case TrieDeleteCommand<T> deleteCommand -> this.root.delete(deleteCommand,0, this);
            case TrieInsertCommand<T> insertCommand -> this.root.insert(insertCommand,0, this);
            case TrieUpdateCommand<T> updateCommand -> this.root.update(updateCommand, 0, this);
            default -> log.error("Unknown command type [{}]", command.getClass().getCanonicalName());
        }
        command.getFuture().complete(null);
    }
}
