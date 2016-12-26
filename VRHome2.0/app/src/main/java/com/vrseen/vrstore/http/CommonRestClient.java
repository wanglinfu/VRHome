package com.vrseen.vrstore.http;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.model.Home.HomeAdData;
import com.vrseen.vrstore.model.app.AppBannerData;
import com.vrseen.vrstore.model.app.AppCategoryData;
import com.vrseen.vrstore.model.app.AppDetailData;
import com.vrseen.vrstore.model.app.AppListData;
import com.vrseen.vrstore.model.channel.ChannelData;
import com.vrseen.vrstore.model.film.FilmAssetData;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.Home.HomeData;
import com.vrseen.vrstore.model.Home.RecommendData;
import com.vrseen.vrstore.model.film.FilmCateroryData;
import com.vrseen.vrstore.model.film.FilmFilterCateryData;
import com.vrseen.vrstore.model.film.FilmListData;
import com.vrseen.vrstore.model.film.FilmRelateData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.Constant;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

import org.json.JSONObject;

/**
 * Created by jiangs on 16/4/29.
 * 普通请求类
 */
public class CommonRestClient extends AbstractRestClient {

    //获取游客token接口
    private static final String URL_USER_TOKEN = "/oauth/access_token";
    //首页数据接口
    private static final String URL_HOME_DATA = "/api/dynamic";
    //广告
    private static final String URL_HOME_AD = "/api/recommends?platform=2";

    //频道首页接口
    private static final String URL_CHANNEL_DATA = "/api/channels";
    //影视主分类接口
    private static final String URL_VIDEO_CATEGORY = "/api/video/genres/main";
    //小编推荐
    private static final String URL_VIDEO_RECOMMEDS = "/api/video/recommends";
    private static final String URL_VIDEO_LIST = "/api/video/find";
    private static final String URL_FILM_DETAIL = "/api/video/detail";
    private static final String URL_RELATE_FILM = "/api/video/relateds";
    public static final String URL_FILM_CHANNEL = "/api/video/channels";
    private static final String URL_FILM_FILTER = "/api/video/genres/sub";

    private static CommonRestClient instance;

    private CommonRestClient() {
        super();
    }

    private Context appCtx;

    public static CommonRestClient getInstance(Context context) {
        if (instance == null) {
            instance = new CommonRestClient();
            Log.e("CommonRestClient","1");
            instance.appCtx = context.getApplicationContext();
            Log.e("CommonRestClient","2");
        }
        //每次getInstance都设置一下值，否则用户切换账号会有问题
        String token = (String) SharedPreferencesUtils.getParam(instance.appCtx,
                SPFConstant.KEY_USER_TOKEN, "");
        instance.client.addHeader("Authorization", token);
        instance.client.addHeader("Accept", VRHomeConfig.SERVER_VERSION);
        instance.client.setTimeout(30000);
        Log.e("CommonRestClient","3"+" "+token);
        return instance;
    }


    /**
     * 获取token
     *
     * @param responseCallBack
     */
    public void getUserToken(Context context, ResponseCallBack responseCallBack) {
        RequestParams rps = new RequestParams();
        rps.put("grant_type", "client_credentials");
        rps.put("client_id", "56402755e7078");
        rps.put("client_secret", "af37a7123784aec850a182fbc6b4ff92");
        rps.put("scope", "common");
        doRequest(METHOD_POST, URL_USER_TOKEN, rps, responseCallBack);
    }

    /**
     * 获取token
     *
     * @param context
     */
    public void getUserToken(final Context context) {
        RequestParams rps = new RequestParams();
        rps.put("grant_type", "client_credentials");
        rps.put("client_id", "56402755e7078");
        rps.put("client_secret", "af37a7123784aec850a182fbc6b4ff92");
        rps.put("scope", "common");
        doRequest(METHOD_POST, URL_USER_TOKEN, rps, new ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(context, resp, e, "网络异常，获取token失败");
            }

            @Override
            public void onSuccess(Response resp) {
                saveToken(resp);
            }
        });
    }

    public void saveToken(Response resp) {
        JSONObject jsonObject = (JSONObject) resp.getData();
        saveToken(jsonObject);
    }

    public void saveToken(JSONObject jsonObject) {
        if (jsonObject != null) {
            //保存token
            String accessToken = jsonObject.optString("access_token", "");
            String tokenType = jsonObject.optString("token_type", "");
            String authorization = tokenType + "\40" + accessToken;
            SharedPreferencesUtils.setParam(instance.appCtx, SPFConstant.KEY_USER_TOKEN, authorization);
            //保存refreshToken
            String refreshToken = jsonObject.optString("refresh_token", "");
            String refreshAuthorization = tokenType + "\40" + refreshToken;
            SharedPreferencesUtils.setParam(instance.appCtx, SPFConstant.KEY_USER_REFRESH_TOKEN, refreshAuthorization);
        }
    }

    /**
     * 0
     * 获取首页数据
     *
     * @param callback
     */
    public void getHomeData(ResponseCallBack callback) {
        RequestParams rps = new RequestParams();
        rps.put("platform", "2");
        requestObject(appCtx, METHOD_GET, URL_HOME_DATA, rps, HomeData.class, callback);
    }

    /**
     * 获取首页广告信息
     *
     * @param callback created at 2016/6/10
     */
    public void getHomeAD(ResponseCallBack callback) {
        requestObject(appCtx, METHOD_GET, URL_HOME_AD, null, HomeAdData.class, callback);
    }

    /**
     * 获取渠道号
     *
     * @param callback
     */
    public void getChannels(ResponseCallBack callback) {
        requestObject(appCtx, METHOD_GET, URL_CHANNEL_DATA, null, ChannelData.class, callback);
    }


    public void getVideoCategory(ResponseCallBack callback) {
        requestObject(appCtx, METHOD_GET, URL_VIDEO_CATEGORY, null, FilmCateroryData.class, callback);
    }


    public void getVideoRecommends(String tag, ResponseCallBack callback) {
        RequestParams rps = new RequestParams();
        rps.put("tag", "VR_HOME_" + tag);
        requestObject(appCtx, METHOD_GET, URL_VIDEO_RECOMMEDS, rps, RecommendData.class, callback);
    }

    public void getVideoList(String genre, int page, ResponseCallBack callback) {
        RequestParams rps = new RequestParams();
        rps.put("genre", genre);
        rps.put("sort", 2);
        rps.put("page", page);
        rps.put("limit", 12);
        requestObject(appCtx, METHOD_POST, URL_VIDEO_LIST, rps, FilmListData.class, callback);
    }

    /**
     * 获取影视的数据
     * 参数/返回
     * created at 2016/6/7
     */
    public void getVideoList(String genre, int page, int sort, int limit, ResponseCallBack callback) {
        RequestParams rps = new RequestParams();
        rps.put("genre", genre);
        rps.put("sort", sort);
        rps.put("page", page);
        rps.put("limit", limit);
        requestObject(appCtx, METHOD_POST, URL_VIDEO_LIST, rps, FilmListData.class, callback);
    }

    @Override
    protected String getBaseUrl() {
        return Constant.SERVER_DOMAIN_MAIN;
    }

    /***********************
     * 应用
     *************************/

    private static final String URL_APP_AD_DATA = "/api/app/recommends?platform=2";
    private static final String URL_GET_APP_CHARTERS = "/api/app/characters";
    private static final String URL_GET_APP_LIST = "/api/find";
    private static final String URL_GET_APP_DETAIL = "/api/detail";

    /**
     * 获取app广告
     * created at 2016/5/16
     */
    public void getAppAdData(ResponseCallBack responseCallBack) {
        requestObject(appCtx, METHOD_GET, URL_APP_AD_DATA, null, AppBannerData.class, responseCallBack);
    }

    /**
     * 获得ａｐｐ分类
     * created at 2016/5/16
     */
    public void getAppCharacters(ResponseCallBack responseCallBack) {

        RequestParams params = new RequestParams();
        params.put("platform",2);

        requestObject(appCtx, METHOD_GET, URL_GET_APP_CHARTERS, params, AppCategoryData.class, responseCallBack);
    }

    /**
     * 获得ａｐｐ列表
     * created at 2016/5/16
     */
    public void getAppList(int page, int characters, int latest, int limit, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        if (characters >= 0) {
            params.add("characters", String.valueOf(characters));
        }
        params.add("page", String.valueOf(page));
        params.add("limit", "" + limit);
        params.add("latest", "" + latest);
        params.add("platform", String.valueOf(2)); //2代表的是android

        requestObject(appCtx, METHOD_POST, URL_GET_APP_LIST, params, AppListData.class, responseCallBack);
    }

    /**
     * 获得ａｐｐ详情数据
     * created at 2016/5/16
     */
    public void getAppDetail(int appId, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        params.add("id", String.valueOf(appId));
        params.add("limit", "1");

        requestObject(appCtx, METHOD_POST, URL_GET_APP_DETAIL, params, AppDetailData.class, responseCallBack);
    }

    //影视
    public void getFilmDetail(int filmId, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        params.add("id", String.valueOf(filmId));
        requestObject(appCtx, METHOD_GET, URL_FILM_DETAIL, params, FilmDetailData.class, responseCallBack);

    }

    public void test() {
        doRequest(METHOD_GET, "http://121.40.76.197/list.json", null, new ResponseCallBack() {

            @Override
            public void onFailure(Response resp, Throwable e) {
                Response response = resp;
            }

            @Override
            public void onSuccess(Response resp) {

                Response response = resp;
            }
        });
    }

    //影视相关
    public void getRelateFilm(final int filmId, ResponseCallBack responseCallBack) {
        String url = URL_RELATE_FILM + "?id=" + filmId + "&limit=3&page=1";
        requestObject(appCtx, METHOD_GET, url, null, FilmRelateData.class, responseCallBack);
    }

    //获取影视频道
    public void getFilmChannelsData(int id, ResponseCallBack responseCallBack) {
        Log.e("getFilmChannelsData","id="+id);
        String url = URL_FILM_CHANNEL + "?video_id=" + String.valueOf(id);
        requestObject(appCtx, METHOD_GET, url, null, FilmAssetData.class, responseCallBack);
    }

    //获取影视过滤的分类
    public void getFilmFilter(String superid, ResponseCallBack responseCallBack) {
        RequestParams params = new RequestParams();
        params.add("superid", superid);

        requestObject(appCtx, METHOD_GET, URL_FILM_FILTER, params, FilmFilterCateryData.class, responseCallBack);
    }

    private static final  String URL_APP_DOWNLOAD_TIMES="/api/app/download";

    public void downloadTimes(String appId, ResponseCallBack responseCallBack){
        RequestParams params = new RequestParams();
        params.add("id", appId);

        doRequest(METHOD_POST, URL_APP_DOWNLOAD_TIMES, params, responseCallBack);

    }

}
