package com.litmonk.jsaspectj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lu on 2016/12/29.
 */
public class ProxyClassFactoryBinder implements IProxyClassFactoryBinder {
    public IProxyClassFactory getProxyClassFactory() {
        return new IProxyClassFactory() {
            public List<ProxyBean> getProxyBeanList() {
                List<ProxyBean> proxyBeanList = new ArrayList<ProxyBean>();
                proxyBeanList.add(new ProxyBean("com.litmonk.jsaspectj.TestProxyImpl", "joinString",
                        TestAroundAspectJHandleImpl.class.getName()));
                return proxyBeanList;
            }
        };
    }
}
