package com.cc.flox.dataSource.template.impl

import com.cc.flox.config.BeetlConfig
import com.cc.flox.dataSource.DataSourceType
import com.cc.flox.dataSource.action.Action
import com.cc.flox.dataSource.template.TemplateRenderContext
import com.cc.flox.dataSource.template.TemplateType
import spock.lang.Specification

/**
 * @author cc
 * @date 2024/4/30
 */
class BeetlTemplateRenderExecutorTest extends Specification {
    def "test"() {
        given:
        def groupTemplate = new BeetlConfig().groupTemplate()
        def target = new BeetlTemplateRenderExecutor(groupTemplate: groupTemplate)
        def context = new TemplateRenderContext(new Action("test", TemplateType.Beetl, sql), binding, DataSourceType.Mysql)

        when:
        context = target.invoke(context)

        then:
        res == context.getRenderedSQL()
        param.size() == context.getRenderedParam().size()
        for (int i = 0; i < param.size(); i++) {
            param.get(i) == context.getRenderedParam().get(i)
        }

        where:
        sql                                                                                                                                            | binding                    | res                                                                        | param
        """SELECT * FROM USER WHERE\n1=1\n@if(!isEmpty(name)){\nand `name` = #name#\n@}\n@if(!isEmpty(age)){\nand `age` = #age#\n@}"""                 | ["name": "cc", "age": 18]  | """SELECT * FROM USER WHERE\n1=1\nand `name` = ?\nand `age` = ?\n"""       | ["cc", 18]
        """SELECT * FROM USER WHERE\n1=1\n@if(!isEmpty(name)){\nand `name` in ( #join(name)# )\n@}\n@if(!isEmpty(age)){\nand `age` = #str(age)#\n@}""" | ["name": "cc", "age": 18]  | """SELECT * FROM USER WHERE\n1=1\nand `name` in ( ? )\nand `age` = 18\n""" | ["cc"]
        """SELECT * FROM USER WHERE\n1=1\n@if(!isEmpty(name)){\nand `name` in ( #join(name)# )\n@}"""                                                  | ["name": ["cc", "bb"]]     | """SELECT * FROM USER WHERE\n1=1\nand `name` in ( ?,? )\n"""               | ["cc", "bb"]
        """SELECT * FROM USER WHERE\n1=1\n@if(!isEmpty(name)){\nand `name` in ( #join(name,",",false)# )\n@}"""                                        | ["name": ["'cc'", "'bb'"]] | """SELECT * FROM USER WHERE\n1=1\nand `name` in ( 'cc','bb' )\n"""         | []

    }
}
