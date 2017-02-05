package com.litmonk.jsaspectj;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import junit.framework.TestSuite;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by lu on 2016/12/29.
 */
public class ClassMethodProxyTest {
    @Test
    public void test_proxyClassMethodFromFactory_success() {
        ClassMethodProxy.proxyClassMethod("com.litmonk.jsaspectj.TestProxyImpl");

        String rStr = new TestProxyImpl().joinString("S1", "S2");

        assertEquals("Proxy_S1_S2", rStr);
    }

    @Test
    public void test_proxyClassMethod_success() throws NotFoundException, CannotCompileException {
        ClassMethodProxy.proxyClassMethod("com.litmonk.jsaspectj.TestProxyImpl1", "joinString",
                TestAroundAspectJHandleImpl.class.getName());

        String rStr = new TestProxyImpl1().joinString("S1", "S2");

        assertEquals("Proxy_S1_S2", rStr);
    }
}
