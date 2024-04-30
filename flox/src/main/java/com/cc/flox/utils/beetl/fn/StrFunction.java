package com.cc.flox.utils.beetl.fn;

import org.beetl.core.Context;
import org.beetl.core.Function;

import java.util.Objects;

/**
 * @author cc
 * @date 2024/4/30
 */
public class StrFunction implements Function {

    @Override
    public Object call(Object[] paras, Context ctx) {
        try {
            Object temp = paras[0];
            if (Objects.isNull(temp)) {
                return null;
            }
            ctx.byteWriter.writeString(temp.toString());
        } catch (Exception e) {
            throw new RuntimeException("Beetl join error : ", e);
        }
        return null;
    }
}


