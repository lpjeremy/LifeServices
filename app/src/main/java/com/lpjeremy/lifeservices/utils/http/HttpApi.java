package com.lpjeremy.lifeservices.utils.http;

import com.lpjeremy.lifeservices.utils.http.base.BaseResult;
import com.lpjeremy.lifeservices.utils.http.base.HttpRequestCallBack;

/**
 * @desc:http 请求API接口定义
 * @date:2017/12/20 10:17
 * @auther:lp
 * @version:1.0
 */

public interface HttpApi {
    /**
     * 登录
     *
     * @param phone
     * @param password
     * @param callBack
     */
    void login(String phone, String password, HttpRequestCallBack<BaseResult> callBack);
}
