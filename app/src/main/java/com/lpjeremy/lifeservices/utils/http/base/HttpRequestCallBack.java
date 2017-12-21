package com.lpjeremy.lifeservices.utils.http.base;

/**
 * @desc:http请求回调
 * @date:2017/12/20 10:19
 * @auther:lp
 * @version:1.0
 */

public interface HttpRequestCallBack<T> {
    void onComplete(T result);
    void onFail(Throwable throwable);
}
