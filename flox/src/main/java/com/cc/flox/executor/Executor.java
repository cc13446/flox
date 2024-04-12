package com.cc.flox.executor;

/**
 * 执行器
 *
 * @author cc
 * @date 2024/4/12
 */
public interface Executor<Param, Result> {

    /**
     * @param param 参数
     * @return 结果
     */
    Result invoke(Param param);

    /**
     * @param param 参数
     * @return 是否匹配
     */
    boolean match(Param param);

}
