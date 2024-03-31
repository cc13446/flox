package com.cc.flox.utils.trie

import com.cc.flox.utils.trie.command.TrieInsertCommand
import spock.lang.Specification


/**
 * 前缀树测试
 * @author cc
 * @date 2024/3/31
 */
class TrieTest extends Specification {

    def "Test trie" () {
        given:
        def target = new Trie() {
            @Override
            int getNodeLength() {
                return 26
            }

            @Override
            int getNodeIndex(char c) {
                return c - ('a' as Character)
            }
        }

        when:
        target.command(new TrieInsertCommand("key", "key")).get()

        then:
        target.get("key") == "key"

    }
}
