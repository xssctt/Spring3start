package com.example.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.example.annotion.RepeatSubmit;
import com.example.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 重复提交拦截器
 *
 * 在preHandle方法中拦截请求,获取请求参数及当前时间
 *
 * 从session中获取上一次请求的数据,包括参数、时间等
 *
 * 判断两次请求的参数是否相同,时间间隔是否在设定阈值内
 *
 * 如果判断为重复提交,返回错误结果,阻止请求
 * 如果不是重复提交,更新session中的数据,包括新参数和时间
 * 最后放行请求
 * 这样通过记录请求参数和时间,判断两次请求时间间隔,即可有效防止重复提交表单。
 *
 * 使用注意:
 *
 * 需要在请求方法上添加@RepeatSubmit注解,表明需要验证
 * 可以通过修改REPEAT_TIME_INTERVAL来调整时间间隔门槛
 * 需要排除验证的接口,避免所有请求都被验证
 */
@Component
public class RepeatSubmitInterceptor implements HandlerInterceptor {

    /**
     * 请求承诺书
     */
    private static final String REPEAT_PARAMETERS = "repeatParameters";
    /**
     * 请求时间
     */
    private static final String REPEAT_TIME = "repeatTime";
    /**
     * 请求间隔小于10s才处理
     */
    private static final int REPEAT_TIME_INTERVAL = 10;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
        if (o instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) o;
            RepeatSubmit repeatSubmit = handlerMethod.getMethodAnnotation(RepeatSubmit.class);

            //判断如果repeatSubmit为空,说明该方法没有配置重复提交验证,直接返回true放行。
            if (repeatSubmit == null) {
                return true;
            }
//这几行代码是获取本次请求的数据,以备后续进行重复提交判断:
//将本次请求的所有参数,转换为JSON字符串,作为本次请求的唯一标识。
//创建一个Map,用于保存本次请求的数据。
//在Map中以常量REPEAT_PARAMETERS为key,保存本次请求的参数JSON字符串。
//获取当前系统时间的毫秒时间戳,作为本次请求的时间标识。
//在Map中以常量REPEAT_TIME为key,保存本次请求的时间戳。
//这样我们就获取到了本次请求的唯一标识(参数字符串)和时间标识,用于后续的重复提交对比:
//参数字符串相同,表示请求参数没有变化
//时间戳间隔小于阈值,表示两次请求在短时间内发生
            String parameterMap = JSONObject.toJSONString(request.getParameterMap());
            Map<String, Object> nowData = new HashMap<>(8);
            nowData.put(REPEAT_PARAMETERS, parameterMap);
            long nowTime = System.currentTimeMillis();
            nowData.put(REPEAT_TIME, nowTime);


            String requestURI = request.getRequestURI();

            HttpSession session = request.getSession();
            Object repeatData = session.getAttribute("repeatData");
            if (repeatData != null) {
                //从session或上次请求方法时间

                Map<String, Object> sessionData = (Map<String, Object>) repeatData;
                if (sessionData.containsKey(requestURI)) {
                    Map<String, Object> oldData = (Map<String, Object>) sessionData.get(requestURI);
                    long oldTime = (Long) oldData.get(REPEAT_TIME);
                    String oldParameterMap = (String) oldData.get(REPEAT_PARAMETERS);
                    if (parameterMap.equals(oldParameterMap) && (nowTime - oldTime) / 1000 < REPEAT_TIME_INTERVAL) {
                        Result result = new Result();
                        result.setMessage("请勿重复点击");
                        try {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("utf-8");
                            response.getWriter().print(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
            }
            //为空  第一次提交 放入session
            Map<String, Object> newSessionData = new HashMap<>();
            newSessionData.put(requestURI, nowData);
            session.setAttribute("repeatData", newSessionData);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
