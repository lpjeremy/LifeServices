package com.lpjeremy.lifeservices.activity.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @desc:所有activity的基类
 * @date:2017/12/14 15:33
 * @auther:lp
 * @version:1.0
 */

public abstract class BaseActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();
    protected Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        mContext = this;
        initView(savedInstanceState);
        loadData();
    }

    /**
     * 初始化界面
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 获取layout资源
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 加载数据
     */
    protected abstract void loadData();

    /**
     * 获取Android 控件
     *
     * @param resId
     * @param <T>
     * @return
     */
    protected <T extends View> T getViewById(int resId) {
        return (T) findViewById(resId);
    }

    /**
     * 获取Android 控件
     *
     * @param resId
     * @param <T>
     * @return
     */
    protected <T extends View> T getViewById(int resId, View parent) {
        if (parent != null)
            return (T) parent.findViewById(resId);
        else
            return null;
    }

    protected int getResColor(int resId) {
        return ContextCompat.getColor(mContext, resId);
    }

}
