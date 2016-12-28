package com.litmonk.jsaspectj;

/**
 * Created by lu on 2016/12/29.
 */
public class TestProxyImpl {
    public String joinString(String s1, String s2) {
        return new StringBuilder(s1).append("_").append(s2).toString();
    }
}
