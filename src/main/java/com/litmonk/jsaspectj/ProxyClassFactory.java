package com.litmonk.jsaspectj;

import java.util.List;

/**
 * 代理加载工厂，紧提供获取能力，真正的代理加载工厂通过com.litmonk.jsaspectj.ProxyClassFactoryBinder获取
 * Created by lu on 2016/12/23.
 */
public final class ProxyClassFactory {
    public final static String PROXY_CLASS_FACTORY_BINDER_CLASS = "com.litmonk.jsaspectj.ProxyClassFactoryBinder";

    private static IProxyClassFactory proxyClassFactoryInterface = null;

    public static void init() {
        IProxyClassFactoryBinder proxyClassFactoryBinder = null;

        try {
            proxyClassFactoryBinder = (IProxyClassFactoryBinder)Thread.currentThread().getContextClassLoader()
                    .loadClass(PROXY_CLASS_FACTORY_BINDER_CLASS).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                proxyClassFactoryBinder = (IProxyClassFactoryBinder)Class.forName(PROXY_CLASS_FACTORY_BINDER_CLASS)
                        .newInstance();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        if (proxyClassFactoryBinder != null) {
            proxyClassFactoryInterface = proxyClassFactoryBinder.getProxyClassFactory();
        }
    }

    public static List<ProxyBean> getProxyBeanList() {
        return proxyClassFactoryInterface == null ? null : proxyClassFactoryInterface.getProxyBeanList();
    }
}
