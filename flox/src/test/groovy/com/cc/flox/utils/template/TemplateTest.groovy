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
        def target = builder.getTemplate("""select * from user where <if test='id != null'> id = #{id} </if>""")
        def map = ["id":"11"]

        when:
        def res = target.process(map as Map<String, Object>)
        println(res.getResult())
        println(res.getParameter())

        then:
        true
    }
}
