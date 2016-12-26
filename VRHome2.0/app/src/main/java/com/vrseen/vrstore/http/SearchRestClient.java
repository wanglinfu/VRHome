package com.vrseen.vrstore.http;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.vrseen.vrstore.model.search.SearchHotKeywordData;
import com.vrseen.vrstore.model.app.AppListData;
import com.vrseen.vrstore.model.search.FilmSearchResultData;
import com.vrseen.vrstore.model.search.PanoramaSearchResultData;
import com.vrseen.vrstore.util.Constant;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

/**
 * 项目名称：VRHome2.0
 * 类描述：搜索页请求数据
 * 创建人：郝晓辉
 * 创建时间：2016/6/10 14:36
 * 修改人：郝晓辉
 * 修改时间：2016/6/10 14:36
 * 修改备注：
 */
public class SearchRestClient extends AbstractRestClient {

    public static final int VIDEO_TAG=0;
    public static final int APPLICATION_TAG=1;
    public static final int PANORAMA_TAG=2;

    /**
     * 影视热门关键词
     */
    private static final String URL_SEARCH_VIDEO_HOT_KEYWORD="/api/video/hotkeys";


    /**
     * 影视搜索
     */
    private static final String URL_SEARCH_VIDEO="/api/video/search";

    /**
     * 应用热门关键词
     */
    private static final String URL_SEARCH_APPLICATION_HOT_KEYWORD="/api/app/hotkeys";

    /**
     * 应用搜索
     */
    private static final String URL_SEARCH_APPLICATION="/api/app/search";

    /**
     * 全景热门关键词
     */
    private static final String URL_SEARCH_PANORAMA_HOT_KEYWORD="/api/content/hotkeys";

    /**
     * 全景搜索
     */
    private static final String URL_SEARCH_PANORAMA="/api/content/search";

    private static SearchRestClient instance;

    private SearchRestClient() {
        super();
    }

    private Context appCtx;

    public static SearchRestClient getInstance(Context context) {
        if (instance == null) {
            instance = new SearchRestClient();
            instance.appCtx = context.getApplicationContext();
        }
        //每次getInstance都设置一下值，否则用户切换账号会有问题
        String token = (String) SharedPreferencesUtils.getParam(context,
                SPFConstant.KEY_USER_TOKEN, "");
        instance.client.addHeader("Authorization", token);
        instance.client.addHeader("Accept", "application/x.vr.v2+json");
        instance.client.setTimeout(30000);
        return instance;
    }

    /**
     * 获取热门关键词
     * @param callBack
     */
    public void getHotKeywords(int type,ResponseCallBack callBack){

        RequestParams requestParams=new RequestParams();
        requestParams.put("limit",10);
        requestParams.put("page",1);

        String url="";
        if(type==PANORAMA_TAG){
            url=URL_SEARCH_PANORAMA_HOT_KEYWORD;
        }else if(type==VIDEO_TAG){
            url=URL_SEARCH_VIDEO_HOT_KEYWORD;
        }else if(type==APPLICATION_TAG){
            url=URL_SEARCH_APPLICATION_HOT_KEYWORD;
        }

        requestObject(appCtx,METHOD_GET,url,requestParams, SearchHotKeywordData.class,callBack);

    }

    /**
     * 获取热门关键词
     * @param callBack
     */
    public void getSearchResult (int type,int page,int limit,String keyword,ResponseCallBack callBack){

        RequestParams requestParams=new RequestParams();
        requestParams.put("limit",limit);
        requestParams.put("page",page);
        requestParams.put("keyword",keyword);

        String url="";
        if(type==PANORAMA_TAG){
            url=URL_SEARCH_PANORAMA;
            requestObject(appCtx,METHOD_POST,url,requestParams, PanoramaSearchResultData.class,callBack);
        }else if(type==VIDEO_TAG){
            url=URL_SEARCH_VIDEO;
            requestObject(appCtx,METHOD_POST,url,requestParams, FilmSearchResultData.class,callBack);
        }else if(type==APPLICATION_TAG){
            url=URL_SEARCH_APPLICATION;
            requestObject(appCtx,METHOD_POST,url,requestParams, AppListData.class,callBack);
        }

    }


    @Override
    protected String getBaseUrl() {
        return Constant.SERVER_DOMAIN_MAIN;
    }
}
