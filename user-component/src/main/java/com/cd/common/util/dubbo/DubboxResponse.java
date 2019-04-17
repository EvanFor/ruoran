package com.cd.common.util.dubbo;

import java.io.Serializable;

public class DubboxResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    public static class Result implements Serializable {

        private static final long serialVersionUID = 2L;

        public int status = 200; // 伪HTTP状态码
        public String code = "ok"; // 接口提示码
        public String message = "OK";// 提示信息
    }

    public Result result = new Result();
    public Object data = null;

    public DubboxResponse setResult(int status, String message) {
        this.result.status = status;
        this.result.message = message;
        return this;
    }

    public DubboxResponse setResult(int status, String message, String code) {
        this.result.status = status;
        this.result.message = message;
        this.result.code = code;
        return this;
    }

    public DubboxResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public Result getResult() {
        return result;
    }

    public Object getData() {
        return data;
    }
}
