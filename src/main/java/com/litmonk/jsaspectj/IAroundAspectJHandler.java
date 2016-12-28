package com.litmonk.jsaspectj;

import java.lang.reflect.Method;

/**
 * 环绕切面接口
 * Created by lu on 2016/12/23.
 */
public interface IAroundAspectJHandler {
    /**
     * 代理，对被环绕切面方法的调用将触发该操作
     * @param obj 要进行切面的对象
     * @param method 被环绕切面的方法
     * @param args 被环绕切面的方法的参数
     * @return 被环绕切面的方法执行后的返回值
     * @throws Throwable
     */
    public Object invoke(Object obj, Method method, Object[] args) throws Throwable;
}
