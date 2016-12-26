package com.vrseen.vrstore.http;

import org.json.JSONObject;

/**
 * Created by jiangs on 16/4/28.
 */
public class Response {
    public static final String KEY_STATUS_CODE = "status_code";
    public static final int SUCCESS_STATUS_CODE = 200; //正确返回时的code
    private Object model;// data处理后的对象
    private Object data;// json对象或者json数组
    private String message;
    private int status_code;


    public static Response fromJson(int statusCode, JSONObject json) {
        Response r = new Response();
        if (statusCode == SUCCESS_STATUS_CODE) {
            if (json != null)
                r.data = json;

        } else {
            if (json != null) {
                r.message = json.optString("message", "");
                r.status_code = statusCode;
            }
        }


        return r;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }
}
