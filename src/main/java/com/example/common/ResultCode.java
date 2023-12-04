package com.example.common;

public enum ResultCode {


    SUCCESS("success",null),
    ERROR("-1","系统异常"),
    PARAM_ERROR("1001","参数异常"),
    USER_EXIST_ERROR("2001","账户已存在"),
    USER_ACCOUNT_ERROR("2002","账户或密码错误"),
    USER_NOT_EXIST_ERROR("2003","用户未找到"),
    ORDER_PAY_ERROR("3001","库存不足");

    public String code;
    public String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
