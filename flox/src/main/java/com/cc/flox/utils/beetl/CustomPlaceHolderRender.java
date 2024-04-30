package com.cc.flox.utils.beetl;

import com.cc.flox.dataSource.PlaceHolderType;
import org.beetl.core.Context;
import org.beetl.core.statement.PlaceholderST;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 自定义渲染器
 *
 * @author cc
 * @date 2024/4/29
 */
public class CustomPlaceHolderRender implements PlaceholderST.Output {

    /**
     * 占位符类型
     */
    public final static String PLACE_HOLDER_TYPE_KEY = "placeHolder_type";

    /**
     * 参数
     */
    private final static ThreadLocal<List<Object>> PARAM = new ThreadLocal<>();

    @Override
    public void write(Context ctx, Object o) throws IOException {
        if (Objects.isNull(o)) {
            return;
        }
        if (Objects.isNull(getParam())) {
            reset();
        }
        PlaceHolderType placeHolderType = (PlaceHolderType) ctx.getGlobal(PLACE_HOLDER_TYPE_KEY);
        getParam().add(o);
        ctx.byteWriter.writeString(placeHolderType.getPlaceHolderFunc().apply(getParam().size()));
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
