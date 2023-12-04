package com.example.filter.Xss;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.StringEscapeUtils;


import java.io.IOException;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;


    public XssHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
    }
//使用StringEscapeUtils.escapeHtml4方法对字符串进行转义,这可以消除可能的XSS攻击代码
//getQueryString():过滤查询字符串
//getParameter(String name):过滤单个参数的值
//getParameterValues(String name):过滤某个参数的多个值
    @Override
    public String getQueryString() {
        return StringEscapeUtils.escapeHtml4(super.getQueryString());
    }

    @Override
    public String getParameter(String name) {
        return StringEscapeUtils.escapeHtml4(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapeValues = new String[length];
            for (int i = 0; i < length; i++) {
                // 防xss攻击和过滤前后空格
                escapeValues[i] = StringEscapeUtils.escapeHtml4(values[i]).trim();
            }
            return escapeValues;
        }
        return super.getParameterValues(name);
    }

}
