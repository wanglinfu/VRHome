package com.vrseen.vrstore.http;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.model.user.UserInfo;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.Constant;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 用户
 * Created by mll on 2016/5/6.
 */
public class UserRestClient extends AbstractRestClient {

    //意见反馈
    private static final String SEND_FEED_BACK_URL = "/api/feedback";
    //激活码
    public static final String GET_MOBILE_JIHUO_CODE = "/api/mobilecode";
    //注册
    public static final String REGISTER_ACC_URL = "/api/signup";
    //token
    public static final String LOGIN_ACC_URL = "/oauth/access_token";
    //用户信息
    public static final String GET_MY_INFO_URL = "/api/me/info";
    //修改密码
    public static final String FIND_PWD_URL = "/api/changepassword";
    //兑换会员
    public static final String EXCHANGE_VIP = "/api/me/exchange";
    //验证用户名是否存在
    public static final String MOBILE_CHECK = "/api/mobilecheck";
    //获取第三方的token
    public static final String GET_THIRD_TOKEN_URL = "/oauth/access_token_other";
    //添加收藏影视
    public static final String ADD_COLLECT_FILM_URL = "/api/video/collect";
    //取消收藏影视
    public static final String CANCEL_COLLECT_FILM_URL = "/api/video/uncollect";
    //获取收藏记录
    public static final String GET_COLLECT_FILM_RECORD_URL = "/api/me/collections";
    //增添全景视频播放记录ַ
    public static final String ADD_PANORAMA_PLAY_RECORD = "/api/content/play";
    //删除全景视频播放记录ַ
    public static final String DELETE_PANORAMA_PLAY_RECORD = "api/content/playhistory";
    //增添影视播放记录
    public static final String ADD_VIDEO_PLAY_RECORD = "/api/video/play";
    //删除影视播放记录
    public static final String DELETE_VIDEO_PLAY_RECORD = "/api/video/playhistory";
    //获取我的播放记录
    public static final String GET_PLAY_RECORD_HISTORY = "/api/me/playhistories";
    //上传头像的地址
    public static final String UPLOADURL = "/upload2";

    //注册的激活码
    public static final int REGISTER_TYPE = 1;
    //忘记密码的激活码
    public static final int REGISTER_FORGET_PWD = 2;
    //会员启动的激活码
    public static final int REGISTER_HUI_YUAN = 3;

    private static UserRestClient instance;

    private UserRestClient() {
        super();
    }

    private Context appCtx;

    public static UserRestClient getInstance(Context context) {
        if (instance == null) {
            instance = new UserRestClient();
            instance.appCtx = context.getApplicationContext();
        }
        //每次getInstance都设置一下值，否则用户切换账号会有问题
        String token = (String) SharedPreferencesUtils.getParam(context,
                SPFConstant.KEY_USER_TOKEN, "");
        instance.client.addHeader("Authorization", token);
        instance.client.setTimeout(30000);
        instance.client.addHeader("Accept", VRHomeConfig.SERVER_VERSION);
        return instance;
    }

    //提交
    public void submitAdvice(String content, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        params.add("title", "");
        params.add("content", content);
        doRequest(METHOD_POST, SEND_FEED_BACK_URL, params, responseCallBack);
    }

    /**
     * 获取激活码
     * 参数/返回
     * created at 2016/5/9
     */
    public void requestGetCode(String tel, int type, ResponseCallBack responseCallBack) {
        String url = GET_MOBILE_JIHUO_CODE;
        RequestParams params = new RequestParams();
        params.add("mobile", tel);
        params.add("type", String.valueOf(type));
        doRequest(METHOD_GET, url, params, responseCallBack);
    }

    /**
     * 注册账户
     * 参数/返回
     * created at 2016/5/9
     */
    public void registerAcc(String phoneName, String pwd, String code, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        params.add("name", String.valueOf(phoneName));
        params.add("mobile", String.valueOf(phoneName));
        params.add("password", String.valueOf(pwd));
        params.add("password_confirmation", String.valueOf(pwd));
        params.add("code", String.valueOf(code));

        doRequest(METHOD_POST, REGISTER_ACC_URL, params, responseCallBack);
    }

    /**
     * 找回密码
     * 参数/返回
     * created at 2016/5/9
     */
    public void findPwdByPhone(String phoneName, String pwd, String code, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        String url = FIND_PWD_URL + "?account=" + String.valueOf(phoneName) + "&password=" + String.valueOf(pwd)
                + "&code=" + code;
        doRequest(METHOD_GET, url, params, responseCallBack);
    }

    /**
     * 手机登陆
     * 参数/返回
     * created at 2016/5/9
     */
    public void loginByPhone(String phoneName, String pwd, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        params.add("client_id", "56402755e7078");
        params.add("client_secret", "af37a7123784aec850a182fbc6b4ff92");
        params.add("grant_type", "password");
        params.add("scope", "common,strict");
        params.add("username", phoneName);
        params.add("password", pwd);
        doRequest(METHOD_POST, LOGIN_ACC_URL, params, responseCallBack);
    }


    /**
     * 手机登陆
     * 参数/返回
     * created at 2016/5/9
     */
    public void loginByThird(String platName, RequestCallBack responseCallBack) {
        Platform platform = ShareSDK.getPlatform(platName);
        if (platform == null) {
            return;
        }
        String username = platform.getDb().getUserId();
        String avatar = platform.getDb().getUserIcon();
        String nickname = platform.getDb().getUserName();
        int type = 1;
        if (platName.equals(QQ.NAME)) {
            type = 1;
        } else if (platName.equals(SinaWeibo.NAME)) {
            type = 3;
        } else if (platName.equals(Wechat.NAME)) {
            type = 2;
        }

        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("username", username);
        params.addBodyParameter("name", nickname);
        params.addBodyParameter("type", String.valueOf(type));
        params.addBodyParameter("avatar", avatar);

        //doRequest(METHOD_POST, GET_THIRD_TOKEN_URL, params, responseCallBack);

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.SERVER_DOMAIN_MAIN + GET_THIRD_TOKEN_URL, params, responseCallBack);
    }

    /**
     * 手机登陆
     * 参数/返回
     * created at 2016/5/9
     */
    public void loginByThird2(String platName, ResponseCallBack responseCallBack) {
        Platform platform = ShareSDK.getPlatform(platName);
        if (platform == null) {
            return;
        }
        String username = platform.getDb().getUserId();
        String avatar = platform.getDb().getUserIcon();
        String nickname = platform.getDb().getUserName();
        int type = 1;
        if (platName.equals(QQ.NAME)) {
            type = 1;
        } else if (platName.equals(SinaWeibo.NAME)) {
            type = 3;
        } else if (platName.equals(Wechat.NAME)) {
            type = 2;
        }

        RequestParams params = new RequestParams();
        params.add("username", username);
        params.add("name", nickname);
        params.add("type", String.valueOf(type));
        params.add("avatar", avatar);

        doRequest(METHOD_POST, GET_THIRD_TOKEN_URL, params, responseCallBack);
    }

    /**
     * 获取用户信息
     * 参数/返回
     * created at 2016/5/9
     */
    public void getUserInfo(Context context, ResponseCallBack responseCallBack) {
        RequestParams requestParams = new RequestParams();
        requestObject(context, METHOD_GET, GET_MY_INFO_URL, requestParams, UserInfo.class, responseCallBack);
    }

    /**
     * 注册会员
     * 参数/返回
     * created at 2016/5/10
     */
    public void registerVIP(String code, ResponseCallBack responseCallBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("code", code);
        doRequest(METHOD_POST, EXCHANGE_VIP, requestParams, responseCallBack);
    }

    /**
     * 验证手机是否有效
     * 参数/返回
     * created at 2016/5/11
     */
    public void mobileCheck(String mobile, ResponseCallBack responseCallBack) {
        String url = MOBILE_CHECK + "?mobile=" + mobile;
        doRequest(METHOD_GET, url, new RequestParams(), responseCallBack);
    }

    //添加收藏
    public void addCollectFilm(String filmId, ResponseCallBack responseCallBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", filmId);
        doRequest(METHOD_POST, ADD_COLLECT_FILM_URL, requestParams, responseCallBack);
    }

    //取消收藏
    public void cancelCollectFilm(String filmId, ResponseCallBack responseCallBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", filmId);
        doRequest(METHOD_POST, CANCEL_COLLECT_FILM_URL, requestParams, responseCallBack);
    }

    //获取收藏列表
    public void getFilmsCollect(int page, int limit, ResponseCallBack responseCallBack) {
        RequestParams rps = new RequestParams();
        rps.put("limit", limit);
        rps.put("page", page);
//        requestObject(appCtx, METHOD_GET, GET_COLLECT_FILM_RECORD_URL, rps, FilmListData.class, responseCallBack);
        doRequest(METHOD_GET, GET_COLLECT_FILM_RECORD_URL, rps, responseCallBack);
    }


    //获取播放记录
    public void addPlayPanoramaRecord(int id, int last_tick) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", String.valueOf(last_tick));

        doRequest(METHOD_POST, ADD_PANORAMA_PLAY_RECORD, requestParams, new ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(appCtx, resp, e, R.string.mine_add_record_fail);
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

            }
        });
    }

    /**
     * 删除全景播放记录
     */
    public void deleteRecordforPanorama(final int id, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        params.add("id", String.valueOf(id));
        doRequest(METHOD_DELETE, DELETE_PANORAMA_PLAY_RECORD, params, responseCallBack);
    }

    /**
     * 添加影视播放记录
     */
    public void addPlayRecordForFilm(int id, int last_tick, int last_episode, int last_channel) {
        RequestParams params = new RequestParams();

        params.put("id", String.valueOf(id));
        params.put("last_tick", String.valueOf(last_tick));
        params.put("last_episode", last_episode);
        params.put("last_channel", last_channel);

        doRequest(METHOD_POST, ADD_VIDEO_PLAY_RECORD, params, new ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(appCtx, resp, e, R.string.mine_add_record_fail);
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

            }
        });
    }

    /**
     * 删除影视播放记录
     */
    public void deleteRecordForFilm(final int id, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        params.add("id", String.valueOf(id));

        doRequest(METHOD_DELETE, DELETE_VIDEO_PLAY_RECORD, params, responseCallBack);
    }

    /**
     * 获取播放记录
     */
    public void getPlayHistroys(int page, int limit, ResponseCallBack responseCallBack) {
        RequestParams rps = new RequestParams();
        rps.put("limit", limit);
        rps.put("page", page);
        doRequest(METHOD_GET, GET_PLAY_RECORD_HISTORY, rps, responseCallBack);
    }

    /**
     * 上传头像到服务器
     * @param path
     * @param responseCallBack
     */
    public void uploadAvatar(String path, StringResponseCallback responseCallBack) {

        RequestParams rps = new RequestParams();
        try {
            rps.put("data", new File(path));
            File file = new File(path);
            Log.e("cccc", file.length() + "");
            doRequest(METHOD_POST, Constant.IMAGE_DOMAIN_MAIN + UPLOADURL, rps, responseCallBack);
            //client.post(appCtx,Constant.IMAGE_DOMAIN_MAIN + UPLOADURL,rps,responseCallBack);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改用户头像
     * @param path
     * @param responseCallBack
     */
    public void updateAvatar(String path, AsyncHttpResponseHandler responseHandler){
        RequestParams rps = new RequestParams();
        put(path, null, responseHandler);
    }

    @Override
    protected String getBaseUrl() {
        return Constant.SERVER_DOMAIN_MAIN;
    }
}
