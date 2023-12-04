package com.example.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class JsonResult<T> {
    private String code;
    private String message;
    private T data;

    public static final String CODE_SUCCESS = "SUCCESS";
    public static final String CODE_ERROR = "ERROR";

    //成功, 没有数据
    public JsonResult() {
        this.data = null;
        this.code = CODE_SUCCESS;
        this.message = "";
    }

    //成功，有数据
    public JsonResult(T data) {
        this.data = data;
        this.message = "";
        this.code = CODE_SUCCESS;
    }

    //成功或失败，决于status，不携带数据
    public JsonResult(boolean status, String message) {
        this.data = null;
        this.message = message;
        this.code = status ? CODE_SUCCESS : CODE_ERROR;
    }

    public JsonResult(String code, String message) {
        this.data = null;
        this.message = message;
        this.code = code;
    }

    public JsonResult(String code, String message, T data) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public JsonResult(boolean status, String message, T data) {
        this.data = data;
        this.message = message;
        this.code = status ? CODE_SUCCESS : CODE_ERROR;
    }

    @JsonIgnore //json忽略此字段
    public boolean isSuccess() {
        return code.equals(CODE_SUCCESS);
    }
}
