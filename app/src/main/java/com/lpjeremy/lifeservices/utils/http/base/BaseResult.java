package com.lpjeremy.lifeservices.utils.http.base;

/**
 * @desc:网络请求返回默认结构
 * @date:2017/12/20 10:33
 * @auther:lp
 * @version:1.0
 */

public class BaseResult {
    private int status_code;
    private String message;

    public int getCode() {
        return status_code;
    }

    public String getMessage() {
        return message;
    }
}
