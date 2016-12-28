package com.litmonk.jsaspectj;

/**
 * 外观模式，通过集成接口来实现代理类工厂的加载
 * 实现类及路径必须为com.litmonk.jsapectj.ProxyClassFactoryBinder
 * Created by lu on 2016/12/23.
 */
public interface IProxyClassFactoryBinder {
    public IProxyClassFactory getProxyClassFactory();
}
