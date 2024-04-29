package com.cc.flox.utils.template;

import ognl.*;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
     */
    public static Object parseExpression(String expression, Map<String, Object> binding) {
        try {
            return Ognl.getValue(new OgnlParser(new StringReader(expression)).topLevelExpression(), binding);
        } catch (ParseException | OgnlException e) {
            throw new RuntimeException("Unknown ognl express [" + expression + "] ", e);
        }
    }

    /**
     * @param expression 表达式
     * @param binding    参数
     * @return 表达式的 bool 结果
     */
    public static boolean evaluateBoolean(String expression, Map<String, Object> binding) {
        Object value = parseExpression(expression, binding);
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof Number)
            return !new BigDecimal(String.valueOf(value)).equals(BigDecimal.ZERO);
        return value != null;
    }

    /**
     * @param expression 表达式
     * @param binding    参数
     * @return 表达式的迭代结果
     */
    public static Iterable<?> evaluateIterable(String expression, Map<String, Object> binding) {
        Object value = parseExpression(expression, binding);
        if (Objects.isNull(value)) {
            throw new RuntimeException("The expression [" + expression + "] evaluated to a null value.");
        }
        if (value instanceof Iterable) {
            return (Iterable<?>) value;
        }
        if (value.getClass().isArray()) {
            int size = Array.getLength(value);
            List<Object> answer = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Object o = Array.get(value, i);
                answer.add(o);
            }
            return answer;
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value).entrySet();
        }
        throw new RuntimeException("Error evaluating expression [" + expression + "].  Return value [" + value + "] was not iterable.");
    }
}
