package com.example.config;


import com.example.common.JacksonObjectMapper;
import com.example.interceptor.RepeatSubmitInterceptor;
import com.example.interceptor.Tokeninterceptor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*
     * @Description: 静态资源映射
     * @param registry
     * @Author: CoderMast https://www.codermast.com/
     */

    //token
    @Resource
    Tokeninterceptor tokeninterceptor;

    //重复提交
    @Resource
    RepeatSubmitInterceptor repeatSubmitInterceptor;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源开始映射...");
       // registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/"); // 后台
       // registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");     // 前台

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/static/uploads/")
                .addResourceLocations("classpath:/static/end/")
                .addResourceLocations("classpath:/uploads/")
                .addResourceLocations("classpath:/public/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }



    //注册token拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        InterceptorRegistration interceptor=registry.addInterceptor(tokeninterceptor);
//
//        //"/api/user/createuser",
//        // "/api/user/create",  login
//        // "/api/user/createuserp",  zhuce
//        // "/api/token/createtoken",
//        // "/api/sms/createsms"   duanxin
//
//        interceptor.addPathPatterns("/api/**")
//                .excludePathPatterns("/error","/api/user/create");

        registry.addInterceptor(tokeninterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/error","/api/user/create");
        registry.addInterceptor(repeatSubmitInterceptor)
                .addPathPatterns("/**");


    }


    //登录拦截器



    //跨域
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET","POST","PUT","DELETE","HEAD","OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }


    /**
     * @Description: 扩展mvc框架的消息转换器,主要的目的是将long转换成string，以便完整的记录long型的id
     * @param converters
     * @Author: CoderMast <a href="https://www.codermast.com/">...</a>
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("使用自定义消息转换器");
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        // 设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        // 将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0, messageConverter);
    }


}
