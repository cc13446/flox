package com.cc.flox.utils.template;

import com.cc.flox.utils.template.fragment.Fragment;
import com.cc.flox.utils.template.placeholder.PlaceHolderParser;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模板
 *
 * @author cc
 * @date 2024/4/28
 */
public class Template {

    public final static String OPEN_TOKEN = "#{";

    public final static String CLOSE_TOKEN = "}";

    /**
     * 根语句
     */
    private final Fragment root;

    public Template(Fragment root) {
        this.root = root;
    }

    /**
     * @param data 数据
     * @return 渲染结果
     */
    public TemplateContext process(Map<String, Object> data) {
        TemplateContext context = new TemplateContext(data);
        calculate(context);
        parseParameter(context);
        return context;
    }

    /**
     * 计算上下文，处理所有语句和$占位符
     *
     * @param context 上下文
     */
    private void calculate(TemplateContext context) {
        this.root.apply(context);
    }

    /**
     * 处理所有的#占位符，包括用户设置和计算时生成的
     *
     * @param context 上下文
     */
    private void parseParameter(TemplateContext context) {
        AtomicInteger index = new AtomicInteger(1);
        PlaceHolderParser parser = new PlaceHolderParser(OPEN_TOKEN, CLOSE_TOKEN, (p) -> {
            Object v = OgnlUtils.parseExpression(p, context.getBinding());
            if (Objects.isNull(v)) {
                throw new RuntimeException("Ognl cannot parse +[" + p + "] value");
            }
            context.addParameter(v);
            return "$" + index.getAndIncrement();
        });
        context.setResult(parser.parse(context.getResult()));
    }

}
