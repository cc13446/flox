package com.cc.flox.executor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 执行器调度者
 *
 * @author cc
 * @date 2024/4/12
 */
@Component
public class ExecutorInvoker implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * @param clazz    执行器类
     * @param param    参数
     * @param <Result> 结果类型
     * @param <Param>  参数类型
     * @param <T>      执行器类型
     * @return 结果
     */
    public <T extends Executor<Param, Result>, Param, Result> Result invoke(Class<T> clazz, Param param) {
        Map<String, T> executors = applicationContext.getBeansOfType(clazz);
        for (Map.Entry<String, T> entry : executors.entrySet()) {
            if (entry.getValue().match(param)) {
                return entry.getValue().invoke(param);
            }
        }
        throw new RuntimeException("Unknown executor for param : " + param);
    }
}
