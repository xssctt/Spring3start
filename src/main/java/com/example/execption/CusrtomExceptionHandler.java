/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.example.execption;

import com.example.common.Result;
import com.example.until.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

/**
 * newbee-mall全局异常处理
 */
@RestControllerAdvice
public class CusrtomExceptionHandler {

    public static final Logger log = LoggerFactory.getLogger(CusrtomExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest req) {
        Result result = new Result();
        result.setCode("500");
        // 区分是否为自定义异常
        if (e instanceof CustomException) {
            CustomException newBeeMallException = (CustomException) e;
            result.setMessage(newBeeMallException.getMessage());
        } else {
            log.error(e.getMessage(), e);
            result.setMessage("未知异常");
        }

        //如果是AJAX请求,返回Result结果对象

        if (HttpUtil.isAjaxRequest(req)) {
            return result;
        }

        // //如果是普通请求,返回错误页面ModelAndView
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("url", req.getRequestURL());
        modelAndView.addObject("stackTrace", e.getStackTrace());
        modelAndView.addObject("author", "xss");
        modelAndView.addObject("ltd", "商城");
        modelAndView.setViewName("error/error");
        return modelAndView;
    }
}
