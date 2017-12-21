package com.lpjeremy.lifeservices.utils.http;

import com.lpjeremy.lifeservices.utils.http.base.BaseResult;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @desc:Retrofit格式定义http网络请求api
 * @date:2017/12/20 10:25
 * @auther:lp
 * @version:1.0
 */

public interface RetrofitApiInterfaces {

    @POST("login")
    Call<BaseResult> login(@Query("username") String phone,
                           @Query("password") String password);
}
