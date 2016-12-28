package com.litmonk.jsaspectj;

import java.lang.reflect.Method;

/**
 * Created by lu on 2016/12/29.
 */
public class TestAroundAspectJHandleImpl implements IAroundAspectJHandler {
    public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
        return "Proxy_" + method.invoke(obj, args);
    }
}
