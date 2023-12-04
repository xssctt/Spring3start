package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket docket() {
        ApiInfo info = new ApiInfoBuilder()
                .contact(new Contact("你的名字", "https://www.bilibili.com", "javastudy111@163.com"))
                .title("图书管理系统 - 在线API接口文档")
                .description("这是一个图书管理系统的后端API文档，欢迎前端人员查阅！")
                .build();
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(info)
                .select()       //对项目中的所有API接口进行选择
                .apis(RequestHandlerSelectors.basePackage("ltd.newbee.mall.controller"))
                .build();
    }



}