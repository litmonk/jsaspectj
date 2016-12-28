package com.litmonk.jsaspectj;

import java.util.List;

/**
 * 代理类加载工厂
 * Created by lu on 2016/12/23.
 */
public interface IProxyClassFactory {
    public List<ProxyBean> getProxyBeanList();
}
