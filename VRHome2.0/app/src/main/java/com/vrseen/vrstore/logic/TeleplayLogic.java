package com.vrseen.vrstore.logic;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.vrseen.vrhome.UnityCome;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.util.Constant;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by FX on 2016/4/25 14:37.
 * 描述: 管理当前在播放的影片详情
 */
public class TeleplayLogic {
    private static TeleplayLogic _instance;
    public static TeleplayLogic getInstance()
    {
        if(_instance == null)
        {
            _instance = new TeleplayLogic();
        }
        return _instance;
    }
    private Context _context;
    //电影详情
    private FilmDetailData _filmDetailData;

    //当前第几集
    private int _iEpisode = 0;

    /**
     * 通过id设置影片详情
     * @param id 134185
     * @param context
     */
    public void set_filmDetail(int id,Context context){
        Log.e("set_filmDetail",id + " "+"");
        if(id < 0)
            return;
        if(_filmDetailData == null || _filmDetailData.getData().getId() != id) {

            CommonRestClient.getInstance(_context).getFilmDetail(id, new AbstractRestClient.ResponseCallBack() {
                @Override
                public void onFailure(Response resp, Throwable e) {
                    Log.e("TeleplayLogic Fail",e.toString());
                    //CommonUtils.showResponseMessage(_context, resp, e, R.string.film_detail_error);
                }

                @Override
                public void onSuccess(Response resp) {
                    Log.e("TeleplayLogic Success","--");
                    _filmDetailData = (FilmDetailData) resp.getModel();
                }
            });
        }
        _context = context;
    }

    private  SyncHttpClient _syncHttpClient;

    public boolean getEpisodeUrlForU3d(int id,int episode_index) {
        Log.e("getEpisodeUrlForU3d","id="+id+" episode="+episode_index);
        if(_filmDetailData == null || id <= 0)
            return false;
        Log.e("getEpisodeUrlForU3d","id="+_filmDetailData.getData().getId());
        if(_filmDetailData.getData().getId() == id) {
            if(episode_index < _filmDetailData.getData().getEpisodes().size()) {
                _iEpisode = episode_index;
                if(_syncHttpClient == null)
                {
                    _syncHttpClient = new SyncHttpClient();
                    _syncHttpClient.addHeader("Accept", VRHomeConfig.SERVER_VERSION);
                }
                String token = (String) SharedPreferencesUtils.getParam(_context,
                        SPFConstant.KEY_USER_TOKEN, "");
                _syncHttpClient.addHeader("Authorization", token);
                Log.e("getEpisodeUrlForU3d","token="+token);
                _syncHttpClient.get(Constant.SERVER_DOMAIN_MAIN + CommonRestClient.URL_FILM_CHANNEL + "?video_id=" + _filmDetailData.getData().getEpisodes().get(episode_index).getId(),new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                    {
                        super.onSuccess(statusCode, headers, response);
                        if(response == null)
                        {
                            return;
                        }
                        try {
                            JSONObject data = response;
                            JSONArray dataArr = data.getJSONArray("data");
                            for (int i = 0; i <dataArr.length() ; i++) {
                                // FIXME: 2016/4/8 临时使用其中一个清晰度
                                JSONObject data2 = dataArr.getJSONObject(i);
                                String str2 = data2.getString("resource_id");
                                JSONObject data3 = new JSONObject(str2);
                                WaSuLogic.getVideoUrlForU3d(data3.getString("catId"),
                                        data3.getString("assetId"),
                                        data3.getInt("episode"), handler);
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });

                return true;
            }
            return false;
        }
        return false;
    }

    public FilmDetailData get_filmDetail() {
        return _filmDetailData;
    }

    public void set_filmDetail(FilmDetailData _filmDetail,Context context) {
        this._filmDetailData = _filmDetail;
        _context = context;
    }


    private Handler handler = new Handler(){

        public void handleMessage(Message msg) {
            Log.e("getEpisodeUrl","--2-"+msg.what);
            switch (msg.what){
                case FilmDetailActivity.GET_WASU_RESOURCE_URL_AUTO:
                    try {
                        String url = (String)msg.obj;
                        Log.e("url=",url);
                        Log.e("id=",_filmDetailData.getData().getId()+"");
                        Log.e("episode=",_iEpisode+"");

                        UnityCome.unitySendMessage("nextMovieUrl|" + url + "|" + _filmDetailData.getData().getId() + "|" + _iEpisode);

                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }

                    break;

            }
        };
    };

    public void clearData(){
        _filmDetailData = null;
    }
}
