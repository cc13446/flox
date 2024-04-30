package com.cc.flox.utils.beetl.fn;

import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.GeneralLoopStatus;
import org.beetl.core.ILoopStatus;
import org.beetl.core.statement.PlaceholderST;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cc
 * @date 2024/4/29
 */
public class JoinFunction implements Function {

    /**
     * 单值可以按照逗号区分
     */
    private static final String SINGLE_SPLIT = ",";

    /**
     * @param o 参数
     * @return 是否是单值
     */
    private static boolean isSingle(Object o) {
        return !(o instanceof Map || o instanceof Iterable || o instanceof Iterator || o.getClass().isArray() || o instanceof Enumeration);
    }

    /**
     * @param o o
     * @return 参数是字符串，而且含有分隔符
     */
    private static boolean isStringWithSplit(Object o) {
        return (o instanceof String) && ((String) o).contains(SINGLE_SPLIT);
    }

    /**
     * @param o           value
     * @param placeHolder 是否为sql占位符
     * @param ctx         上下文
     * @throws IOException exception
     */
    private static void output(Object o, boolean placeHolder, Context ctx) throws IOException {
        if (placeHolder) {
            PlaceholderST.output.write(ctx, o);
        } else {
            ctx.byteWriter.writeString(o.toString());
        }
    }

    /**
     * @param it      可迭代数组
     * @param joinStr join字符串
     */
    private static void join(ILoopStatus it, String joinStr, boolean placeHolder, Context ctx) throws IOException {
        while (it.hasNext()) {
            Object o = it.next();
            if (!it.isFirst()) {
                output(joinStr, false, ctx);
            }
            output(o, placeHolder, ctx);
        }
    }

    @Override
    public Object call(Object[] paras, Context ctx) {
        try {
            Object temp = paras[0];
            if (Objects.isNull(temp)) {
                return null;
            }
            String joinStr = ",";
            if (paras.length >= 2) {
                joinStr = (String) paras[1];
            }

            boolean placeHolder = true;
            if (paras.length >= 3) {
                placeHolder = Boolean.parseBoolean(paras[2].toString());
            }

            // 参数为单值 &&
            // 参数不是字符串 || 参数是字符串不包含分隔符
            if (isSingle(temp) && !isStringWithSplit(temp)) {
                output(temp, placeHolder, ctx);
                return null;
            }
            if (isStringWithSplit(temp)) {
                temp = Arrays.stream(((String) temp).split(SINGLE_SPLIT)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            }
            ILoopStatus it = GeneralLoopStatus.getIteratorStatus(temp);
            join(it, joinStr, placeHolder, ctx);
        } catch (Exception e) {
            throw new RuntimeException("Beetl join error : ", e);
        }
        return null;
    }
}

