package com.lpjeremy.lifeservices.utils.http.base;

import android.util.Log;

import com.lpjeremy.lifeservices.Constant;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @desc:Retrofit基础api类
 * @date:2017/12/19 17:18
 * @auther:lp
 * @version:1.0
 */

public class RetrofitApi {
    private static RetrofitApi retrofitApi;

    private Retrofit retrofit;

    public static RetrofitApi getInstance() {
        if (retrofitApi == null) {
            synchronized (RetrofitApi.class) {
                if (retrofitApi == null) {
                    retrofitApi = new RetrofitApi();
                }
            }
        }
        return retrofitApi;
    }

    public RetrofitApi() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.Url.URL_ROOT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build();
    }

    private OkHttpClient createOkHttpClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e(Constant.Config.TAG + "http data:", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("APP_VERSION", "1.0")//通过addHeader添加请求头
                                .addHeader("CHANNEL", "ANDROID")
                                .build();
                        return chain.proceed(request);
                    }

                }).connectTimeout(Constant.Config.TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(Constant.Config.TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(Constant.Config.TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        return httpClient;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
