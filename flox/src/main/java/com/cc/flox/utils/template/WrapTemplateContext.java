package com.cc.flox.utils.template;

/**
 * 代理的上下文
 *
 * @author cc
 * @date 2024/4/29
 */
public class WrapTemplateContext extends TemplateContext {

    /**
     * 代理
     */
    protected final TemplateContext delegate;

    public WrapTemplateContext(TemplateContext delegate) {
        super(null);
        this.delegate = delegate;
    }

    @Override
    public void bind(String key, Object value) {
        delegate.bind(key, value);
    }

    @Override
    public void append(String fragment) {
        delegate.append(fragment);
    }

    @Override
    public String getResult() {
        return delegate.getResult();
    }

    @Override
    public void setResult(String result) {
        delegate.setResult(result);
    }

    @Override
    public void addParameter(Object parameter) {
        delegate.addParameter(parameter);
    }

    @Override
    public int getUniqueIndex() {
        return delegate.getUniqueIndex();
    }
}
