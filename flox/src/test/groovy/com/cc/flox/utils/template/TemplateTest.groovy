package com.cc.flox.utils.template

import org.springframework.core.io.DefaultResourceLoader
import spock.lang.Specification

/**
 * @author cc
 * @date 2024/4/29
 */
class TemplateTest extends Specification {

    def builder = new TemplateBuilder(new DefaultResourceLoader())

    def "test"() {
        given:
        def head = """<?xml version = "1.0" ?><!DOCTYPE root SYSTEM "script-1.0.dtd">"""
        def target = builder.getTemplate(head + template)

        when:
        def result = target.process(binding as Map<String, Object>)

        then:
        res == result.getResult()
        param.size() == result.getParameter().size()
        for (int i = 0; i < param.size(); i++) {
            param.get(i) == result.getParameter().get(i)
        }

        where:
        template                                                                                                                                                                       | binding                        | res                                                                   | param
        """<root>select * from user where <if test='id != null'> id = #{id} </if></root>"""                                                                                            | ["id": "11"]                   | "select * from user where id = \$1 "                                  | [11]
        """<root>select * from user <where> <if test='id != null'> and id = #{id} </if> <if test='name != null' > and name = #{name}</if></where></root>"""                            | ["id": "11", "name": "cc"]     | """select * from user WHERE id = \$1 and name = \$2 """               | [11, "cc"]
        """<root>update user <set> <if test='id != null'> id = #{id}, </if><if test='name != null'> name = #{name}, </if></set></root>"""                                              | ["id": "11", "name": "cc"]     | """update user SET id = \$1, name = \$2 """                           | [11, "cc"]
        """<root>select * from user <where><choose><when test='id!= null'> and id = #{id} </when><when test='name!= null'> and name = #{name} </when></choose></where></root>"""       | ["id": "11", "name": "cc"]     | """select * from user WHERE id = \$1 """                              | [11]
        """<root>select * from user <where> id in <foreach item="item" index="index" collection="list" open="(" separator="," close=")"> #{item}_#{index} </foreach></where></root>""" | ["list": ["11", "name", "cc"]] | """select * from user WHERE id in ( \$1_\$2 , \$3_\$4 , \$5_\$6 ) """ | [11, 0, "name", 1, "cc", 2]
    }
}
