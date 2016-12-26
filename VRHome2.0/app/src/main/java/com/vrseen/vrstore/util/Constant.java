package com.vrseen.vrstore.util;

import com.vrseen.vrstore.BuildConfig;

import java.util.List;

/**
 * Created by jiangs on 16/4/28.
 */
public class Constant {
    private static String TAG = "Constant";
    //主服务器
    public static final String SERVER_DOMAIN_MAIN;
    public static final String IMAGE_DOMAIN_MAIN;

    /**
     * 生产环境  0
     * 测试环境  !0
     */
    public final static int ENV_PROD = 0;
    public static final int env = BuildConfig.ENV;

    static {
        if (ENV_PROD == Constant.env) {
            SERVER_DOMAIN_MAIN = "http://vrseenhome.api.vrseen.net";
            IMAGE_DOMAIN_MAIN = "http://image.api.vrseen.net";
        } else {
            SERVER_DOMAIN_MAIN = "http://beta.vrseenhome.api.vrseen.net";
            IMAGE_DOMAIN_MAIN = "http://beta.image.api.vrseen.net";
        }
    }



    //跳转请求
    public static final String KEY_JUMP_REQ_CODE = "JUMP_REQ";
    //到登陆界面
    public static final int JUMP_REQ_LOGIN = 1;
}
