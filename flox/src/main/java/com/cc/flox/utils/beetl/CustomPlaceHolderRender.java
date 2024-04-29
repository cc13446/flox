package com.cc.flox.utils.beetl;

import org.beetl.core.Context;
import org.beetl.core.statement.PlaceholderST;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 自定义渲染器
 *
 * @author cc
 * @date 2024/4/29
 */
public class CustomPlaceHolderRender implements PlaceholderST.Output {

    /**
     * 参数
     */
    private final static ThreadLocal<List<Object>> PARAM = new ThreadLocal<>();

    @Override
    public void write(Context ctx, Object o) throws IOException {
        if (CollectionUtils.isEmpty(getParam())) {
            reset();
        }
        ctx.byteWriter.writeString("?");
        getParam().add(o);
    }

    /**
     * 重置参数
     */
    public void reset() {
        PARAM.set(new LinkedList<>());
    }

    /**
     * @return 参数
     */
    public List<Object> getParam() {
        return PARAM.get();
    }
}
