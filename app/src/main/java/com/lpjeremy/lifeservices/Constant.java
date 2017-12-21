package com.lpjeremy.lifeservices;

/**
 * @desc:项目中用到的常量配置类
 * @date:2017/12/19 16:59
 * @auther:lp
 * @version:1.0
 */
public final class Constant {
    /**
     * 项目本地配置数据
     */
    public static final class Config {
        /**
         * 打印tag
         */
        public static final String TAG = "LifeServices";
        /**
         * 是否是debug模式
         */
        public static final boolean DEBUG = BuildConfig.DEBUG;

        public static final int TIMEOUT = 10000;


    }

    /**
     * url地址配置
     */
    public static final class Url {
        public static final String URL_ROOT = (Config.DEBUG ? "http://106.14.62.12:97" : "https://dibudapi.dibugroup.net") + "/";
    }

    /**
     * 存储地址配置
     */
    public static final class Path {

    }
}
