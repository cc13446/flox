package com.cc.flox.utils.template;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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
    public static final String BINDING_DATA = "_data";

    /**
     * 占位符绑定
     */
    @Getter
    private final Map<String, Object> binding = new HashMap<>();

    /**
     * 结果
     */
    private StringBuilder result = new StringBuilder();

    /**
     * 参数
     */
    @Getter
    private final List<Object> parameter = new ArrayList<>(20);

    /**
     * 唯一下标
     */
    private int uniqueIndex = 0;

    public TemplateContext(Map<String, Object> data) {
        this.binding.put(BINDING_DATA, data);
    }

    /**
     * 绑定参数
     *
     * @param key   key
     * @param value value
     */
    public void bind(String key, Object value) {
        binding.put(key, value);
    }

    /**
     * @param fragment 语句
     */
    public void append(String fragment) {
        fragment = fragment.trim();
        if (StringUtils.isBlank(fragment)) {
            return;
        }
        result.append(fragment).append(" ");
    }

    /**
     * @return result
     */
    public String getResult() {
        return result.toString();
    }

    /**
     * @param result result
     */
    public void setResult(String result) {
        this.result = new StringBuilder(result);
    }

    /**
     * @param parameter 参数
     */
    public void addParameter(Object parameter) {
        this.parameter.add(parameter);
    }

    /**
     * @return 全局唯一下标
     */
    public int getUniqueIndex() {
        return ++uniqueIndex;
    }
}
