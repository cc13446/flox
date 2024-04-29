package com.cc.flox.utils.template;

import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import java.util.Map;
import java.util.Objects;

/**
 * 模板上下文
 *
 * @author cc
 * @date 2024/4/29
 */
public class TemplateContext {

    /**
     * 用户传入的占位符数据在上下文中的 key
     */
    private static final String BINDING_DATA = "_data";

    static {
        OgnlRuntime.setPropertyAccessor(Map.class, new ContextAccessor());
    }

    /**
     * ognl 属性交互器
     */
    @SuppressWarnings("unchecked")
    static class ContextAccessor implements PropertyAccessor {

        @Override
        public Object getProperty(Map context, Object target, Object name) {
            // 先在上下文中寻找
            Map<String, Object> map = (Map<String, Object>) target;
            Object result = map.get((String) name);
            if (Objects.nonNull(result)) {
                return result;
            }

            // 再在用户传入的属性数据中寻找
            map = (Map<String, Object>) map.get(BINDING_DATA);
            result = map.get((String) name);
            if (Objects.nonNull(result)) {
                return result;
            }

            return null;
        }

        @Override
        public void setProperty(Map context, Object target, Object name, Object value) {
            Map<String, Object> map = (Map<String, Object>) target;
            map.put(name.toString(), value);
        }

        public String getSourceAccessor(OgnlContext context, Object target, Object name) {
            return null;
        }

        public String getSourceSetter(OgnlContext context, Object target, Object name) {
            return null;
        }

    }
}
