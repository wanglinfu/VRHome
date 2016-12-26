package com.vrseen.vrstore.http;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.model.Home.HomeData;
import com.vrseen.vrstore.model.panorama.PanoramaBannerListData;
import com.vrseen.vrstore.model.panorama.PanoramaCategoryData;
import com.vrseen.vrstore.model.panorama.PanoramaCollectionData;
import com.vrseen.vrstore.model.panorama.PanoramaCollectionDetailData;
import com.vrseen.vrstore.model.panorama.PanoramaDetailData;
import com.vrseen.vrstore.model.panorama.PanoramaListData;
import com.vrseen.vrstore.util.Constant;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景资源请求数据
 * 创建人：郝晓辉
 * 创建时间：2016/6/1 10:36
 * 修改人：郝晓辉
 * 修改时间：2016/6/1 10:36
 * 修改备注：
 */
public class PanoramaRestClient extends AbstractRestClient {

    //获取游客token接口
    private static final String URL_PANORAMA_TOKEN = "/oauth/access_token";

    //获取全景界面广告数据
    private static final String URL_PANORAMA_BANNER = "/api/content/recommends";
    //获取全景界面动态部分数据
    private static final String URL_PANORAMA_RECOMMEND = "/api/content/dynamic";
    //获取全景界面类型数据
    private static final String URL_PANORAMA_CATEGORY = "/api/content/characters";
    //获取全景界面全部类型数据
    private static final String URL_PANORAMA_TYPE = "/api/content/types";
    //获取列表页数据
    private static final String URL_PANORAMA_LIST = "/api/content/find";
    //获取城市列表
    private static final String URL_PANORAMA_CITY = "/api/content/cities";
    //获取专辑列表
    private static final String URL_PANORAMA_COLLECTION = "/api/contentlist/find";
    //获取全景详情
    public static final String URL_PANORAMA_DETAIL = "/api/content/detail";
    //获取全景详情相关
    private static final String URL_PANORAMA_RELATED = "/api/content/relateds";
    //获取全景全部分类
    private static final String URL_PANORAMA_ALL_TYPE = "/api/content/types";
//获取全景专辑详情
    private static final String URL_PANORAMA_COLLECTION_DETAIL="/api/contentlist/detail";
    //获取全景专辑相关
    private static final String URL_PANORAMA_COLLECTION_RELATEDS="/api/contentlist/relateds";

    private static PanoramaRestClient instance;

    private PanoramaRestClient() {
        super();
    }

    private Context appCtx;

    public static PanoramaRestClient getInstance(Context context) {
        if (instance == null) {
            instance = new PanoramaRestClient();
            instance.appCtx = context.getApplicationContext();
        }
        //每次getInstance都设置一下值，否则用户切换账号会有问题
        String token = (String) SharedPreferencesUtils.getParam(context,
                SPFConstant.KEY_USER_TOKEN, "");
        instance.client.addHeader("Authorization", token);
        instance.client.addHeader("Accept", VRHomeConfig.SERVER_VERSION);
        instance.client.setTimeout(30000);
        return instance;
    }


    /**
     * 获取广告数据
     *
     * @param callback
     */
    public void getBannerData(ResponseCallBack callback) {

        requestObject(appCtx, METHOD_GET, URL_PANORAMA_BANNER, null, PanoramaBannerListData.class, callback);
//        doRequest(METHOD_GET, URL_PANORAMA_BANNER, null, callback);
    }

    /**
     * 获取全景分类数据
     *
     * @param callback
     */

    public void getPanoramaCategory(String currentCityId,ResponseCallBack callback) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("cityid", currentCityId);
        requestObject(appCtx, METHOD_GET, URL_PANORAMA_CATEGORY, requestParams, PanoramaCategoryData.class, callback);
    }

    /**
     * 获取全景全部分类
     *
     * @param callback
     */
    public void getPanoramaAllType(String currentCityId,ResponseCallBack callback) {
//        requestObject(appCtx, METHOD_GET, URL_PANORAMA_ALL_TYPE, null, PanoramaCategoryData.class, callback);
        RequestParams requestParams = new RequestParams();
        requestParams.put("cityid", currentCityId);
        doRequest(METHOD_GET, URL_PANORAMA_ALL_TYPE, requestParams, callback);
    }

    /**
     * 获取推荐数据
     *
     * @param callback
     */
    public void getRecommendData(ResponseCallBack callback) {

//        doRequest(METHOD_GET, URL_PANORAMA_RECOMMEND, null, callback);

        requestObject(appCtx, METHOD_GET, URL_PANORAMA_RECOMMEND, null, HomeData.class, callback);
    }

    /**
     * 获取列表数据
     *
     * @param callback
     */
    public void getListData(RequestParams requestParams, ResponseCallBack callback) {

//        doRequest(METHOD_POST, URL_PANORAMA_LIST, requestParams, callback);
        requestObject(appCtx, METHOD_POST, URL_PANORAMA_LIST, requestParams, PanoramaListData.class, callback);

    }


    /**
     * 获取城市
     *
     * @param callback
     */
    public void getCityData(ResponseCallBack callback) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("page", 1);
        requestParams.put("limit", 99);
        doRequest(METHOD_GET, URL_PANORAMA_CITY, requestParams, callback);

    }

    /**
     * 获取专辑数据
     *
     * @param page     页数
     * @param limit    每页数量
     * @param callback
     */
    public void getCollectoinData(int page, int limit, ResponseCallBack callback) {
        RequestParams requestParams = new RequestParams();

        requestParams.put("page", page);
        requestParams.put("limit", limit);
        requestObject(appCtx, METHOD_POST, URL_PANORAMA_COLLECTION, requestParams, PanoramaCollectionData.class, callback);
//        doRequest(METHOD_POST, URL_PANORAMA_COLLECTION, requestParams, callback);
    }

    /**
     * 获取全景详情
     *
     * @param panoramaId 全景资源ID
     * @param callBack
     */

    public void getPanoramaDetail(int panoramaId, ResponseCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", panoramaId);
        requestObject(appCtx, METHOD_GET, URL_PANORAMA_DETAIL, requestParams, PanoramaDetailData.class, callBack);
//        doRequest(METHOD_GET,URL_PANORAMA_DETAIL,requestParams,callBack);
    }

    /**
     * 获取全景相关
     * @param panoramaId    全景id
     * @param page  页数
     * @param limit 每页数量
     * @param callback
     */
    public void getRelated(int panoramaId, int page, int limit, ResponseCallBack callback) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("id", panoramaId);
        requestParams.put("page", page);
        requestParams.put("limit", limit);
//        requestObjectList(appCtx,METHOD_GET,URL_PANORAMA_RELATED,requestParams, PanoramaDetailData.class,callback);
        requestObject(appCtx, METHOD_GET, URL_PANORAMA_RELATED, requestParams, PanoramaListData.class, callback);
//        doRequest(METHOD_GET, URL_PANORAMA_RELATED, requestParams, callback);

    }

    /**
     * 获取专辑详情
     * @param collectionId
     * @param callBack
     */
    public void getCollectionDetail(String collectionId,ResponseCallBack callBack){

        RequestParams requestParams = new RequestParams();
        requestParams.put("id", collectionId);

        requestObject(appCtx, METHOD_GET, URL_PANORAMA_COLLECTION_DETAIL, requestParams, PanoramaCollectionDetailData.class, callBack);

    }

    public void getCollectionRelateds(RequestParams requestParams,ResponseCallBack callBack){

        requestObject(appCtx, METHOD_GET, URL_PANORAMA_COLLECTION_RELATEDS, requestParams, PanoramaListData.class, callBack);

    }

    @Override
    protected String getBaseUrl() {
        return Constant.SERVER_DOMAIN_MAIN;
    }
}
