package com.cc.flox.utils.template.fragment.impl;

import com.cc.flox.utils.template.OgnlUtils;
import com.cc.flox.utils.template.Template;
import com.cc.flox.utils.template.TemplateContext;
import com.cc.flox.utils.template.WrapTemplateContext;
import com.cc.flox.utils.template.fragment.Fragment;
import com.cc.flox.utils.template.placeholder.PlaceHolderParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author cc
 * @date 2024/4/29
 */
@AllArgsConstructor
public class ForeachFragment implements Fragment {

    public static final String ITEM_PREFIX = "__foreach__";

    /**
     * 将对象下标化
     *
     * @param item item
     * @param i    下标
     * @return 下标化字符串
     */
    private static String itemizeItem(String item, int i) {
        return ITEM_PREFIX + item + "__" + i;
    }

    private final Fragment content;
    private final String collection;
    private final String open;
    private final String close;
    private final String separator;
    private final String item;
    private final String index;

    @Override
    public boolean apply(TemplateContext context) {
        applyOpen(context);

        final Iterable<?> iterable = OgnlUtils.evaluateIterable(collection, context.getBinding());
        boolean first = true;
        int i = 0;
        for (Object o : iterable) {
            PrefixedContext prefixedContext;
            if (first) {
                prefixedContext = new PrefixedContext(context, "");
            } else if (StringUtils.isNoneBlank(separator)) {
                prefixedContext = new PrefixedContext(context, separator);
            } else {
                prefixedContext = new PrefixedContext(context, "");
            }
            int uniqueIndex = prefixedContext.getUniqueIndex();
            if (o instanceof Map.Entry) {
                @SuppressWarnings("unchecked")
                Map.Entry<Object, Object> mapEntry = (Map.Entry<Object, Object>) o;
                applyIndex(prefixedContext, mapEntry.getKey(), uniqueIndex);
                applyItem(prefixedContext, mapEntry.getValue(), uniqueIndex);
            } else {
                applyIndex(prefixedContext, i, uniqueIndex);
                applyItem(prefixedContext, o, uniqueIndex);
            }
            content.apply(new TransformedContext(prefixedContext, index, item, uniqueIndex));

            first = first && !prefixedContext.isPrefixApplied();
            i++;
        }

        applyClose(context);
        // 清理环境
        if (StringUtils.isNoneBlank(index)) {
            context.getBinding().remove(index);
        }
        if (StringUtils.isNoneBlank(item)) {
            context.getBinding().remove(item);
        }
        return true;
    }

    /**
     * @param context 上下文
     */
    private void applyOpen(TemplateContext context) {
        if (StringUtils.isNoneBlank(open)) {
            context.append(open);
        }
    }

    /**
     * @param context 上下文
     * @param o       值
     * @param i       下标
     */
    private void applyIndex(TemplateContext context, Object o, int i) {
        if (StringUtils.isNoneBlank(index)) {
            context.bind(index, o);
            context.bind(itemizeItem(index, i), o);
        }
    }

    /**
     * @param context 上下文
     * @param o       值
     * @param i       下标
     */
    private void applyItem(TemplateContext context, Object o, int i) {
        if (StringUtils.isNoneBlank(item)) {
            context.bind(item, o);
            context.bind(itemizeItem(item, i), o);
        }
    }

    /**
     * @param context 上下文
     */
    private void applyClose(TemplateContext context) {
        if (StringUtils.isNoneBlank(close)) {
            context.append(close);
        }
    }

    /**
     * append 带转化的上下文
     */
    private static class TransformedContext extends WrapTemplateContext {
        private final int uniqueIndex;
        private final String index;
        private final String item;

        public TransformedContext(TemplateContext delegate, String index, String item, int i) {
            super(delegate);
            this.uniqueIndex = i;
            this.index = index;
            this.item = item;
        }

        @Override
        public void append(String fragment) {
            PlaceHolderParser parser = new PlaceHolderParser(Template.OPEN_TOKEN, Template.CLOSE_TOKEN, (content) -> {
                String newContent = content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", itemizeItem(item, uniqueIndex));
                if (StringUtils.isNoneBlank(index) && newContent.equals(content)) {
                    newContent = content.replaceFirst("^\\s*" + index + "(?![^.,:\\s])", itemizeItem(index, uniqueIndex));
                }
                return Template.OPEN_TOKEN + newContent + Template.CLOSE_TOKEN;
            });
            delegate.append(parser.parse(fragment));
        }
    }

    /**
     * append 带前缀的上下文
     */
    private static class PrefixedContext extends WrapTemplateContext {

        /**
         * 前缀已经写入
         */
        private final String prefix;

        @Getter
        private boolean prefixApplied;

        public PrefixedContext(TemplateContext delegate, String prefix) {
            super(delegate);
            this.prefix = prefix;
            this.prefixApplied = false;
        }

        @Override
        public void append(String fragment) {
            if (!prefixApplied && StringUtils.isNoneBlank(fragment)) {
                delegate.append(prefix);
                prefixApplied = true;
            }
            delegate.append(fragment);
        }
    }
}
