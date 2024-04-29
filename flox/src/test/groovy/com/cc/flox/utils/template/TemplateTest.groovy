package com.cc.flox.utils.template

import org.springframework.core.io.DefaultResourceLoader
import spock.lang.Specification

/**
 * @author cc
 * @date 2024/4/29
 */
class TemplateTest extends Specification {

    def builder = new TemplateBuilder(new DefaultResourceLoader())

    def "test if"() {
        given:
        def target = builder.getTemplate(template)

        when:
        def result = target.process(binding as Map<String, Object>)

        then:
        res == result.getResult()
        param = result.getParameter()

        where:
        template                                                               | binding      | res                                     | param
        """select * from user where <if test='id != null'> id = #{id} </if>""" | ["id": "11"] | """select * from user where   id = ?  """ | [11]
    }
}
