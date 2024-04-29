package com.cc.flox.utils.template.fragment.impl;

import com.cc.flox.utils.template.TemplateContext;
import com.cc.flox.utils.template.WrapTemplateContext;
import com.cc.flox.utils.template.fragment.Fragment;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author cc
 * @date 2024/4/29
 */
@AllArgsConstructor
public class TrimFragment implements Fragment {

    private final Fragment content;
    private final String prefix;
    private final String suffix;
    private final List<String> prefixesToOverride;
    private final List<String> suffixesToOverride;

    public TrimFragment(Fragment content, String prefix, String suffix, String prefixesToOverride, String suffixesToOverride) {
        this(content, prefix, suffix,
                StringUtils.isBlank(prefixesToOverride) ? null : Arrays.asList(prefixesToOverride.split("\\|")),
                StringUtils.isBlank(suffixesToOverride) ? null : Arrays.asList(suffixesToOverride.split("\\|")));
    }

    @Override
    public boolean apply(TemplateContext context) {
        FilteredContent fContext = new FilteredContent(context);
        content.apply(fContext);
        fContext.applyAll();
        return false;
    }

    /**
     * append 时进行过滤的上下文
     */
    private class FilteredContent extends WrapTemplateContext {

        private boolean trim = false;

        private StringBuilder wrapper = new StringBuilder();

        public FilteredContent(TemplateContext delegate) {
            super(delegate);
        }

        @Override
        public void append(String fragment) {
            wrapper.append(fragment).append(" ");
        }

        /**
         * 写入结果
         */
        public void applyAll() {
            if (trim) {
                delegate.append(wrapper.toString());
                return;
            }
            wrapper = new StringBuilder(wrapper.toString().trim());
            if (!wrapper.isEmpty()) {
                applyPrefix();
                applySuffix();
            }
            delegate.append(wrapper.toString());
            trim = true;
        }

        /**
         * 处理后缀
         */
        private void applySuffix() {
            if (!CollectionUtils.isEmpty(suffixesToOverride)) {
                for (String toRemove : suffixesToOverride) {
                    if (wrapper.toString().endsWith(toRemove)) {
                        int start = wrapper.length() - toRemove.length();
                        int end = wrapper.length();
                        wrapper.delete(start, end);
                        break;
                    }
                }
            }
            if (StringUtils.isNoneBlank(suffix)) {
                this.append(suffix);
            }
        }

        /**
         * 处理前缀
         */
        private void applyPrefix() {
            if (!CollectionUtils.isEmpty(prefixesToOverride)) {
                for (String toRemove : prefixesToOverride) {
                    if (wrapper.toString().startsWith(toRemove.toUpperCase())) {
                        wrapper.delete(0, toRemove.length());
                        break;
                    }
                }
            }
            if (StringUtils.isNoneBlank(prefix)) {
                wrapper.insert(0, prefix + " ");
            }
        }
    }
}
