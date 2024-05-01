package com.cc.flox.utils

import com.cc.flox.dataType.DataTypeClassLoader
import spock.lang.Specification


/**
 * @author cc
 * @date 2024/5/1
 */
class JavaCodeUtilsTest extends Specification {

    def "test"() {
        given:
        def code =
                """
package com.cc.flox.data.type;
public class Test { 
    public int value = 0; 
}
"""
        when:
        def className = JavaCodeUtils.getClassNameFromCode(code)
        def packageName = JavaCodeUtils.getPackageNameFromCode(code)
        byte[] res = JavaCodeUtils.codeToClass(code)

        then:
        className == "Test"
        packageName == "com.cc.flox.data.type"
        res.length != 0

    }
}
