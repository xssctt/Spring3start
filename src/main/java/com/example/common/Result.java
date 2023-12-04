package com.example.common;

public class Result<T>{
    private String code;
    private String message;
    private T Data;

    public Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.Data = data;
    }

    public Result() {
    }

    public Result(T data) {
        this.Data = data;
    }

    /**
     * 不带参数的返回  成功
     * @return
     */
    public static Result success(){
        Result result=new Result<>();
        result.setCode(ResultCode.SUCCESS.code);
        result.setMessage(ResultCode.SUCCESS.message);
        return result;
    }

    /**
     * 带参数的返回 成功
     * @return
     */
    public static <T> Result <T> success(T data){
        Result<T> result=new Result<>(data);
        result.setCode(ResultCode.SUCCESS.code);
        result.setMessage(ResultCode.SUCCESS.message);
        return result;
    }

    /**
     * 不带参数的返回  失败
     * @return
     */
    public static Result error(){
        Result result=new Result<>();
        result.setCode(ResultCode.ERROR.code);
        result.setMessage(ResultCode.ERROR.message);
        return result;
    }


    /**
     * 带参数的返回  失败
     * @return
     */
    public static Result error(String code,String message){
        Result result=new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", Data=" + Data +
                '}';
    }
}
