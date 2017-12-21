package com.lpjeremy.lifeservices.utils.http;

import com.lpjeremy.lifeservices.utils.http.base.BaseResult;
import com.lpjeremy.lifeservices.utils.http.base.HttpRequestCallBack;
import com.lpjeremy.lifeservices.utils.http.base.RetrofitApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @desc:网络请求api
 * @date:2017/12/20 10:15
 * @auther:lp
 * @version:1.0
 */

public class HttpRequestApi implements HttpApi {
    private static HttpRequestApi requestApi;

    public static HttpRequestApi getInstance() {
        if (requestApi == null) {
            synchronized (HttpRequestApi.class) {
                if (requestApi == null) {
                    requestApi = new HttpRequestApi();
                }
            }
        }
        return requestApi;
    }

    @Override
    public void login(String phone, String password, final HttpRequestCallBack<BaseResult> callBack) {
        RetrofitApiInterfaces apiInterfaces = RetrofitApi.getInstance().create(RetrofitApiInterfaces.class);
        Call<BaseResult> call = apiInterfaces.login(phone, password);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (callBack != null) {
                    if (response == null) {
                        callBack.onFail(new Throwable(""));
                    }else {
                        callBack.onComplete(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                if (callBack != null)
                    callBack.onFail(t);
            }
        });
    }
}
