package com.cc.flox.utils.template;

import ognl.*;

import java.io.StringReader;
import java.util.Map;
import java.util.Objects;

public class OgnlUtils {

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
            map = (Map<String, Object>) map.get(TemplateContext.BINDING_DATA);
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

    /**
     * @param expression expression
     * @param binding    binding
     * @return 表达式计算结果
     * @throws ParseException exception
     * @throws OgnlException  exception
     */
    public static Object parseExpression(String expression, Map<String, Object> binding) throws ParseException, OgnlException {
        return Ognl.getValue(new OgnlParser(new StringReader(expression)).topLevelExpression(), binding);
    }
}
