package com.litmonk.jsaspectj;

/**
 * Created by lu on 2016/12/23.
 */
public class ProxyBean {
    /**
     * 要加载的类名，包含路径，eg: com.litmonk.ProxyTest
     */
    private String proxyClass;

    /**
     * 要被代理的方法名
     */
    private String methodName;

    /**
     * 切面处理实现类，继承自IAroundAspectJHandler
     */
    private String aroundAspectJHandlerImplClass;

    public ProxyBean(String proxyClass, String methodName, String aroundAspectJHandlerImplClass) {
        this.proxyClass = proxyClass;
        this.methodName = methodName;
        this.aroundAspectJHandlerImplClass = aroundAspectJHandlerImplClass;
    }

    public String getProxyClass() {
        return proxyClass;
    }

    public void setProxyClass(String proxyClass) {
        this.proxyClass = proxyClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAroundAspectJHandlerImplClass() {
        return aroundAspectJHandlerImplClass;
    }

    public void setAroundAspectJHandlerImplClass(String aroundAspectJHandlerImplClass) {
        this.aroundAspectJHandlerImplClass = aroundAspectJHandlerImplClass;
    }
}
