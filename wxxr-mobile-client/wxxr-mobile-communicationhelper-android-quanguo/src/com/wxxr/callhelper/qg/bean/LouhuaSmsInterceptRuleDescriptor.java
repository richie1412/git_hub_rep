package com.wxxr.callhelper.qg.bean;

import java.util.List;

/**
 * ���������ʾ���ʵ����
 * @author cuizaixi
 *
 */
public class LouhuaSmsInterceptRuleDescriptor {
    private String name;
    private  String pattern;

    public String[] getPatterns() {
        if (pattern!=null){
            return pattern.split("or");
        }
        return null;
    }
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public LouhuaSmsInterceptRuleDescriptor() {
        super();
    }

    public LouhuaSmsInterceptRuleDescriptor(String name, String pattern) {
        super();
        this.name = name;
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "LouhuaSmsInterceptRuleDescriptor [name=" + name + ", pattern=" + pattern + "]";
    }



}
