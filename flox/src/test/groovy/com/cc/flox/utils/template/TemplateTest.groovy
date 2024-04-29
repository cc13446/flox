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
        def target = builder.getTemplate(template)

        when:
        def result = target.process(binding as Map<String, Object>)

        then:
        res == result.getResult()
        param.size() == result.getParameter().size()
        for (int i = 0; i < param.size(); i++) {
            param.get(i) == result.getParameter().get(i)
        }

        where:
        template                                                                                                                                                                           | binding                        | res                                                       | param
        """<script>select * from user where <if test='id != null'> id = #{id} </if></script>"""                                                                                            | ["id": "11"]                   | """select * from user where id = ? """                    | [11]
        """<script>select * from user <where> <if test='id != null'> and id = #{id} </if> <if test='name != null' > and name = #{name}</if></where></script>"""                            | ["id": "11", "name": "cc"]     | """select * from user WHERE id = ? and name = ? """       | [11, "cc"]
        """<script>update user <set> <if test='id != null'> id = #{id}, </if><if test='name != null'> name = #{name}, </if></set></script>"""                                              | ["id": "11", "name": "cc"]     | """update user SET id = ?, name = ? """                   | [11, "cc"]
        """<script>select * from user <where><choose><when test='id!= null'> and id = #{id} </when><when test='name!= null'> and name = #{name} </when></choose></where></script>"""       | ["id": "11", "name": "cc"]     | """select * from user WHERE id = ? """                    | [11]
        """<script>select * from user <where> id in <foreach item="item" index="index" collection="list" open="(" separator="," close=")"> #{item}_#{index} </foreach></where></script>""" | ["list": ["11", "name", "cc"]] | """select * from user WHERE id in ( ?_? , ?_? , ?_? ) """ | [11, 0, "name", 1, "cc", 2]
    }
}
