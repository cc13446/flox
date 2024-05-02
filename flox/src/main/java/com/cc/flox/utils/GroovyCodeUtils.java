package com.cc.flox.utils;

import com.cc.flox.dataType.DataTypeClassLoader;
import groovy.lang.GroovyClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cc
 * @date 2024/5/2
 */
@Component
public class GroovyCodeUtils {

    /**
     * groovy 类加载器
     */
    private final GroovyClassLoader classLoader;

    @Autowired
    public GroovyCodeUtils(DataTypeClassLoader classLoader) {
        this.classLoader = new GroovyClassLoader(classLoader);
    }

    /**
     * @param code  代码
     * @param clazz 实例类型
     * @param <T>   T
     * @return 实例
     */
    public <T> T getGroovyObject(String code, Class<T> clazz) {
        try {
            Class<?> groovyClass = classLoader.parseClass(code);
            Object object = groovyClass.getDeclaredConstructor().newInstance();
            if (clazz.isAssignableFrom(object.getClass())) {
                return clazz.cast(object);
            }
        } catch (Exception e) {
            throw new RuntimeException("Compile groovy code error : ", e);
        }
        return null;
    }
}
