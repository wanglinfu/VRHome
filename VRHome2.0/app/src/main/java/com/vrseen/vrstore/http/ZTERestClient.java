package com.vrseen.vrstore.http;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.util.Constant;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

/**
 * 中兴logic
 * Created by mll on 2016/3/30.
 */
public class ZTERestClient  {
    private static final String GET_TOKEN_BY_ZTE = "/oauth/access_token_zhongxing";
    private static final String GET_INFO_BY_ZTE = "/api/me/info";//"/api/me/zhongxinginfo";
    private static ZTERestClient instance = null;


    protected SyncHttpClient client = new SyncHttpClient(false, 80, 443);
    private Context appCtx;
    public static ZTERestClient getInstance(Context context) {
        if (instance == null) {
            instance = new ZTERestClient();
            instance.appCtx = context.getApplicationContext();
        }
        //每次getInstance都设置一下值，否则用户切换账号会有问题
        String token = (String) SharedPreferencesUtils.getParam(context,
                SPFConstant.KEY_USER_TOKEN, "");
        instance.client.addHeader("Authorization", token);
        instance.client.setTimeout(10000);
        instance.client.addHeader("Accept", VRHomeConfig.SERVER_VERSION);
        return instance;
    }


    //获取token
    public void requestZTEToken(String zteToken,JsonHttpResponseHandler responseCallBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("token", zteToken);


        client.post(appCtx,Constant.SERVER_DOMAIN_MAIN + GET_TOKEN_BY_ZTE, requestParams, responseCallBack);
       // doRequest(METHOD_POST, GET_TOKEN_BY_ZTE, requestParams, responseCallBack);
    }

    public void getMyInfo(JsonHttpResponseHandler responseCallBack)
    {
        RequestParams requestParams = new RequestParams();
        client.get(appCtx,Constant.SERVER_DOMAIN_MAIN + GET_INFO_BY_ZTE, requestParams, responseCallBack);

    }

}
