package com.vrseen.vrstore.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.unity.GoogleUnityActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.unity3d.player.UnityPlayer;
import com.vrseen.vrhome.UnityCome;
import com.vrseen.vrstore.ComeInVRActivity;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.app.AppDetailActivity;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.activity.panorama.PanoramaCollectionDetailActivity;
import com.vrseen.vrstore.activity.panorama.PanoramaDetailActivity;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.model.find.LocalResInfo;
import com.vrseen.vrstore.model.panorama.PanoramaDetailData;
import com.vrseen.vrstore.util.DownloadUtils;
import com.vrseen.vrstore.util.FileUtils;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.cache.DiskFileUtils;
import in.srain.cube.util.Encrypt;


/**
 * 项目名称：VRHome
 * 类描述：
 * 创建人：mll
 * 创建时间：2016/1/15 10:34
 * 修改人：mll
 * 修改时间：2016/1/15 10:34
 * 修改备注：
 */
public class U3dMediaPlayerLogic  {
    private static U3dMediaPlayerLogic _instance;


    public static U3dMediaPlayerLogic getInstance() {
        if (_instance == null) {
            _instance = new U3dMediaPlayerLogic();
        }
        return _instance;
    }

    private String _param = "";
    private Bundle _bundle = new Bundle();
    private Intent _intent = new Intent();

    public Context mContext;
    //VRHomeConfig文件下面
    //    //VR模式
    //    public static final int TYPE_VR = 1;//VR模式
    //    public static final int TYPE_NO_VR  = 0;//非VR模式
    //
    //    //维度
    //    public static final String TYPE_2D  = "2D";
    //    public static final String TYPE_3D  = "3D";
    //
    //    //渲染类型
    //    public static final String TYPE_RENDER_LR  = "LR";//左右
    //    public static final String TYPE_RENDER_TB  = "TB";//上下

    //	1）当播放全景图片或视频时，参数为：loadlevel|Pano|[资源url]|VR模式1或非VR模式0,如：
    //	"loadlevel|Pano|http://120.26.217.87/Uploads/media/siping/sipingdemo.mp4|1"

    /**
     * 播放全全景图片或视频
     *
     * @param context
     * @param vrMode  VR模式1或非VR模式0
     */
    public void startPlayPanorama(Context context, PanoramaDetailData.PanoramaDetail panoramaDetailData, int vrMode, int sceneId) {

        mContext = context;
        DownloadUtils.initDownload(mContext);
        String url ;

         url = getMediaUrlU3d(panoramaDetailData);
        if(url == null || url.isEmpty()) {
            LogUtils.e("startPlayPanorama url = null from u3dmediaplayerlogic line 100");
            return;
        }

        String panoramaUrl = "loadlevel|Pano|" + url + "|" + vrMode + "|" + sceneId;

        comeinVR(context,panoramaUrl);

        //添加播放记录
        UserRestClient.getInstance(context).addPlayPanoramaRecord(panoramaDetailData.getId(), 10);

        if(context instanceof PanoramaDetailActivity)
        {
            ((PanoramaDetailActivity) context).finish();
        }else if(context instanceof FilmDetailActivity)
        {
            ((FilmDetailActivity) context).finish();
        }
//        else if(context instanceof MyDownloadActivity)
//        {
//            ((MyDownloadActivity) context).finish();
//        }
    }

    public void startPlayPanoramaCollection(Context context, String json, int vrMode, int sceneId) {

        mContext = context;
        DownloadUtils.initDownload(mContext);

        if(json == null || json.isEmpty()) {
            LogUtils.e("startPlayPanorama url = null from u3dmediaplayerlogic line 100");
            return;
        }

        String panoramaUrl = "loadlevel|Pano|" + json + "|" + vrMode + "|" + sceneId;

        comeinVR(context,panoramaUrl);

//        //添加播放记录
//        UserRestClient.getInstance(context).addPlayPanoramaRecord(panoramaDetailData.getId(), 10);

        if(context instanceof PanoramaDetailActivity)
        {
            ((PanoramaDetailActivity) context).finish();
        }else if(context instanceof FilmDetailActivity)
        {
            ((FilmDetailActivity) context).finish();
        }else if(context instanceof PanoramaCollectionDetailActivity){
            ((PanoramaCollectionDetailActivity) context).finish();
        }
//        else if(context instanceof MyDownloadActivity)
//        {
//            ((MyDownloadActivity) context).finish();
//        }
    }

    /**
     * 播放全全景图片或视频，是否添加记录
     *
     * @param context
     * @param vrMode  VR模式1或非VR模式0
     * param   addRecord ，是否添加记录
     */
    public void startPlayPanorama(Context context, PanoramaDetailData.PanoramaDetail vo, int vrMode,boolean addRecord, int sceneId) {
        mContext = context;
        DownloadUtils.initDownload(mContext);

        String url ;
        url = getMediaUrlU3d(vo);
        if(url == null || url.isEmpty())
            return ;

        String panoramaUrl = "loadlevel|Pano|" + url + "|" + vrMode + "|" + sceneId;
        comeinVR(context,panoramaUrl);
        if(addRecord == true )
        {
            //添加播放记录
            UserRestClient.getInstance(context).addPlayPanoramaRecord(vo.getId(), 10);
        }

        if(context instanceof PanoramaDetailActivity)
        {
            ((PanoramaDetailActivity) context).finish();
        }else if(context instanceof FilmDetailActivity)
        {
            ((FilmDetailActivity) context).finish();
        }
//        else if(context instanceof MyDownloadActivity)
//        {
//            ((MyDownloadActivity) context).finish();
//        }
    }

    //	2)当播放影片[电影]时，参数为:loadlevel|Cinema|影片url|2D或3D|如果是3D,则有此参数，值为LR[左右]或TB[上下],也可能不写，默认为左右,如下:
    //	“loadlevel|Cinema|http://101.69.250.174/88888888/16/20151015/269052417/269052417.ts|3D|LR";

    /**
     * 播放影片[电影]
     *
     * @param context
     * @param url           影片url
     * @param dimensionType 维度类型 2D或3D
     * @param renderType    渲染类型 如果是3D,则有此参数，值为LR[左右]或TB[上下]
     */
    public void startPlayFilm(Context context, String url, String dimensionType
            , String renderType,String title,int position,int movieid, int espsode) {

        if(url == null || url.isEmpty() ||
                dimensionType == null || dimensionType.isEmpty()
                ||title == null || title.isEmpty()) {
            LogUtils.e("startPlayFilm url = null from u3dmediaplayerlogic line 175");
            return;
        }

        url = getMediaUrl(url);

        if(url == null || url.isEmpty())
            return ;

        mContext = context;

        DownloadUtils.initDownload(mContext);

        String filmUrl;
        if(movieid == 0)
            filmUrl = "loadlevel|Cinema|" + url + "|" + dimensionType + "|" + renderType + "|" + title + "|" + position;
        else
            filmUrl = "loadlevel|Cinema|" + url + "|" + dimensionType + "|" + renderType + "|" + title + "|" + position +"|-1|"+movieid+"|"+espsode;
        //去了if,unity内部发起的播放就不成功了
        if(context instanceof GoogleUnityActivity){
            UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", filmUrl);
         }else {
            comeinVR(context,filmUrl);
        }

        if(context instanceof FilmDetailActivity)
        {
            ((FilmDetailActivity) context).finish();
        }
//        else if(context instanceof MyDownloadActivity)
//        {
//            ((MyDownloadActivity) context).finish();
//        }
    }

    /**
     * 播放影片[电影]
     *
     * @param context
     * @param url           影片url
     * @param dimensionType 维度类型 2D
     */
    public void startPlayFilm(Context context, String url, String dimensionType
            ,String title,int position,int movieid, int espsode) {

        if(url == null || url.isEmpty() ||
                dimensionType == null || dimensionType.isEmpty()
                ||title == null || title.isEmpty()) {
            LogUtils.e("startPlayFilm url = null from u3dmediaplayerlogic line 220");
            return;
        }

        mContext = context;
        DownloadUtils.initDownload(mContext);
        String filmUrl;
        if(movieid == 0) {
            filmUrl = "loadlevel|Cinema|" + url + "|" + dimensionType + "|" + "空缺|" + title + "|" + position;
        }else if(movieid < 0)
            filmUrl = "loadlevel|Cinema|" + url + "|" + dimensionType+ "|" +"空缺|"+ title + "|"+position +"|"+movieid;
        else
            filmUrl = "loadlevel|Cinema|" + url + "|" + dimensionType+ "|" +"空缺|"+ title + "|"+position +"|-1|"+movieid+"|"+espsode;
        comeinVR(context,filmUrl);
        if(context instanceof FilmDetailActivity)
        {
            ((FilmDetailActivity) context).finish();
        }
//        else if(context instanceof MyDownloadActivity)
//        {
//            ((MyDownloadActivity) context).finish();
//        }
    }


    public void comeinVR(Context context,String msg){
        Intent intent = new Intent();
        intent.setClass(context, ComeInVRActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("msg",msg);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进去VR
     *
     * @param context
     */
    public void startSceneVR(Context context,String msg) {
        mContext = context;
        DownloadUtils.initDownload(mContext);
        _intent.setClass(context, com.google.unity.GoogleUnityActivity.class);
        _intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(_intent);
        if(msg == null || msg.isEmpty()){
            int tab = 0;
            if(MainActivity.instance != null)
                tab = MainActivity.instance.getCurrentTab();
            switch (tab){
                case 0:
                    msg = null;//不需要定位到VR某个位置(之前在哪退出的就进到哪里)
                    break;
                case 1://定位到首页
                    UnityCome.unitySendMessage("loadlevel|Scene_Home");
                    break;
                case 2:
                    msg = "loadlevel|Scene_Home|7";//定位到应用模块
                    break;
                case 3:
                case 4:
                    msg = "loadlevel|Scene_Home|9";//定位到发现模块
                    break;
            }
        }
        if(msg != null && !msg.isEmpty())
            UnityCome.unitySendMessage(msg);//loadlevel|Scene_Home|
        UnityCome.VRRequestFocus();
    }



    //播放的url
    public String getMediaUrlU3d(PanoramaDetailData.PanoramaDetail vo) {
        if(vo == null)
            return "";
        String url = vo.getStoragepath();
        //用不同的服务器,如测试服务器和正式服务器,就可以引起同一个资源找不到,因为服务器地址不一样了
        //HttpHandler.State state = DownloadManagerLogic.getInstance().getDownLoadState(url);
        //if (state != null && state.equals(HttpHandler.State.SUCCESS)) {
            // 下载成功
        //    String getSdUrl = DownloadManagerLogic.getInstance().getSdUrl(url);
        //    if (getSdUrl != null) {
                //转化为本地视频
        //        return "file:///"+getSdUrl;
        //    }
        //}

        DownloadInfo _downloadInfoVO = DownloadUtils.getInstance().getDownloadVO(vo.getId());
        if(_downloadInfoVO != null && _downloadInfoVO.getState().equals(HttpHandler.State.SUCCESS)){
            return "file:///"+_downloadInfoVO.getFileSavePath();
        }
        return url;
    }

    //播放的url
    public String getMediaUrl(String url) {
        if(url == null || url.isEmpty())
            return "";

        HttpHandler.State state = DownloadUtils.getInstance().getDownLoadState(url);
        if (state != null && state.equals(HttpHandler.State.SUCCESS)) {
            // 下载成功
            String getSdUrl = DownloadUtils.getInstance().getSdUrl(url);
            if (getSdUrl != null) {
                //转化为本地视频
                return getSdUrl;
            }
        }
        return url;
    }

    /**
     * u3d发起下载
     *
     * @param id
     * @param type ,1为App，2为全景,3为影视
     */
    public void toDownload(Context context,int id, int type,String url)
    {
        if(url == null || url.isEmpty())
            return ;

        if(DownloadUtils.getInstance().checkInDowning(url))
        {
            //正在下载中
            if(DownloadUtils.getInstance().getDownLoadState(url) == HttpHandler.State.SUCCESS)
            {
                ToastUtils.showShort(context,R.string.download_complete);
            }
            else
            {
                ToastUtils.showShort(context,R.string.downlaoding);
            }
            return;
        }

        if(type == VRHomeConfig.TYPE_PANORAMA)
        {
            //下载全景
            downPanoramaData(context,id);
        }
        else if(type == VRHomeConfig.TYPE_APP)
        {
            //若是下载app，发起跳转到url
//            Intent intent = new Intent(context,AppDownLoadActivity.class);
//            intent.putExtra("url", url);
//            context.startActivity(intent);
            AppDetailActivity.actionStart(context,id);
        }

        //UnitySendMessage("","","progress|ID|type|curlen|len|0/1[==1下载中，==0下载完成]")通知Unity
    }


    protected HttpUtils mHttpUtils      = new HttpUtils(10000);

    private void downPanoramaData(final Context context,int id)
    {
        //获取详细信息
        String url = PanoramaRestClient.URL_PANORAMA_DETAIL;
        RequestParams params = new RequestParams();
        String token = (String) SharedPreferencesUtils.getParam(context,
                SPFConstant.KEY_USER_TOKEN, "");
        String strAuthorization = token;
        params.addHeader("Accept", VRHomeConfig.SERVER_VERSION);
        params.addHeader("Authorization", strAuthorization);
        params.addBodyParameter("id",String.valueOf(id));

        final DownloadInfo downloadInfo = new DownloadInfo();
        mHttpUtils.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo != null) {
                    try {
                        JSONObject jsonObjectAll = new JSONObject(responseInfo.result);
                        if(jsonObjectAll.has("data"))
                        {
                            JSONObject jsonObject = jsonObjectAll.getJSONObject("data");
                            downloadInfo.setResourceId(jsonObject.getInt("id"));
                            downloadInfo.setAutoRename(true);
                            downloadInfo.setAutoResume(true);

                            if(jsonObject.has("name"))
                            {
                                String name = jsonObject.getString("name");
                                name = name.replace(" ", "%20");
                                downloadInfo.setFileName(name);
                            }

                            if(jsonObject.has("storagepath")) {
                                String url = jsonObject.getString("storagepath");
                                url = url.replace(" ", "%20");
                                downloadInfo.setDownloadUrl(url);
                            }

                            if(jsonObject.has("thumbnail"))
                            {
                                String thumbnail = jsonObject.getString("thumbnail");
                                thumbnail = thumbnail.replace(" ", "%20");
                                downloadInfo.setThumbUrlForLocal(thumbnail);
                            }

                            if(jsonObject.has("type"))
                            {
                                String type = jsonObject.getString("type");
                                String targetUrl = FileUtils.getFileName(downloadInfo.getDownloadUrl());

                                if (type.equals("0")) {
                                    downloadInfo.setType(VRHomeConfig.VIDEO);
                                    targetUrl = VRHomeConfig.SAVE_PANO_VIDEO + targetUrl;
                                } else if (type.equals("1")) {
                                    downloadInfo.setType(VRHomeConfig.IMAGE);
                                    targetUrl = VRHomeConfig.SAVE_PANO_IMAGE + targetUrl;
                                }
                                downloadInfo.setFileSavePath(targetUrl);
                            }

                            try {
                                DownloadUtils.getInstance().addNewDownload(downloadInfo,  new RequestCallBack<File>() {
                                    @Override
                                    public void onLoading(long total, long current, boolean isUploading) {
                                        float progressValue = ((float)current/(float)total)*100;
                                        LogUtils.e("当前进度：" + progressValue + "%");
                                        _param = "progress|"+downloadInfo.getId()+"|"+ downloadInfo.getType()+"|"+current+"|" +total ;
                                        String params = _param +"|1";
                                        UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", params);
                                        super.onLoading(total, current, isUploading);
                                    }

                                    public void onSuccess(ResponseInfo<File> responseInfo) {
                                        String params = _param +"|0";
                                        UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", params);
                                    }

                                    public void onFailure(HttpException error, String msg) {

                                    }
                                });
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                HttpException error2 = error;
                ToastUtils.showShort(context,R.string.panorama_detail_fail);
            }
        });
    }

    /**
     * 获取下载列表
     * @return json
     */
    public String getMyDownload() {

        JSONArray jsonArray = new JSONArray();
        JSONObject allJson = new JSONObject();

        //添加本地的数据
        ArrayList<LocalResInfo> voList = FileLogic.getInstance().localFileArrList;
        for(int j = 0;j< voList.size();j++)
        {
            LocalResInfo vo = voList.get(j);
            jsonArray.put(getJsonDataForLocal(VRHomeConfig.SAVE_THUMBS, vo,false));
        }

        try {
            allJson.put("dataLocal",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //添加下载的数据

        jsonArray = new JSONArray();
        List<DownloadInfo> downloadInfoVOList = DownloadUtils.getInstance().getlist();
        for (int i = 0; i < downloadInfoVOList.size(); i++) {
            DownloadInfo vo = downloadInfoVOList.get(i);
            if(vo.getState() == HttpHandler.State.SUCCESS)
            {
                jsonArray.put(getJsonDataForDown(DiskFileUtils.LocalCacheDir + "/", vo, true));
            }
        }

        try {
            allJson.put("dataDownload",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allJson.toString();
        //return [json数据]; {data:[{},{}]} //{[id,type,name,thumb,path,state[==0下载完成|==1下载中],curlen[当前已经下载量],len[总量]]}
    }

    /**
     * 处理下载
     * 参数/返回
     * created at 2016/6/10
     */
    private JSONObject getJsonDataForDown(String path, DownloadInfo vo, boolean isNoEx )
    {
        JSONObject jsonObject  = new JSONObject();
        try {
            jsonObject.put("ID",vo.getId());
            jsonObject.put("name",vo.getFileName());
            jsonObject.put("type",vo.getType());

            String thumburl = vo.getThumbUrl();
            if(path != null && thumburl != null) {
                if(isNoEx) {
                    thumburl = path + Encrypt.md5(thumburl);
                }else
					thumburl = path + FileUtils.getFileName(thumburl);
            }

            jsonObject.put("thumb",thumburl);
            jsonObject.put("path",vo.getFileSavePath());
            if(vo.getState() == HttpHandler.State.SUCCESS)
            {
                jsonObject.put("state",0);
            }
            else
            {
                jsonObject.put("state",1);
            }
            jsonObject.put("curlen",vo.getProgress());
            jsonObject.put("len",vo.getFileLength());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 处理本地
     * 参数/返回
     * created at 2016/6/10
     */
    private JSONObject getJsonDataForLocal(String path, LocalResInfo vo, boolean isNoEx )
    {
        JSONObject jsonObject  = new JSONObject();
        try {
            jsonObject.put("ID",vo.getId());
            jsonObject.put("name",vo.getFileName());
            if(vo.getType() != VRHomeConfig.LOCAL_TYPE_MOVIE_3D )
            {
                jsonObject.put("type",vo.getType());
            }
            else
            {
                //3d
                jsonObject.put("type",vo.getSmallType());
            }

            String thumburl = vo.getThumbUrl();
            if(path != null && thumburl != null) {
                if(isNoEx) {
                    thumburl = path + Encrypt.md5(thumburl);
                }else
                    thumburl = path + FileUtils.getFileName(thumburl);
            }

            jsonObject.put("thumb",thumburl);
            jsonObject.put("path",vo.getFileSavePath());
            jsonObject.put("state",1);
            jsonObject.put("curlen",100);
           // jsonObject.put("len",vo.getFileLength());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 获取昵称
     * @return
     */
    public String getUserNikename()
    {
       return UserLogic.getInstance().getUserInfo().getName();
        //return "";
    }

    /**
     * 返回全景，影视文件地址
     * @param id
     * @param type  类型， 2，全景，3是影视
     * @param source  如华数，联通
     * @param catId         栏目id
     * @param assetId       资产id
     * @param indext        剧集
     * @return
     */

    private int _movieId = 0;
    private String _dimensionType ="";
    private String _renderType = "";
    private Context _context;
    private String _title;

    private Handler _handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what) {
                case FilmDetailActivity.GET_WASU_RESOURCE_URL_AUTO:
                    String url = (String)msg.obj;
                    if(_dimensionType.equals("panorama")){
                        //全景
                        String panoramaUrl = "loadlevel|Pano|" + url + "|" + 1+"|8";
                        if(_context instanceof GoogleUnityActivity) {
                            UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", panoramaUrl);
                        }else {
                            comeinVR(_context,panoramaUrl);
                        }
                    }else
                        startPlayFilm(_context, url, _dimensionType, _renderType,_title,0,0,0);
                    TeleplayLogic.getInstance().set_filmDetail(_movieId,_context);
                    break;
                case VRHomeConfig.GET_LOCALRES_THUMB:
                    UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", "localThumb|" + U3dMediaPlayerLogic.getInstance().getMyDownload());
                    break;
                default:break;
            }
        }
    };

    private InitLocalVRLogic _localThumb = null;


    public void initMyDownload(Activity activity) {

        _localThumb = new InitLocalVRLogic(activity,_handler,VRHomeConfig.GET_LOCALRES_THUMB);
        _localThumb.start();
    }

    public void toPlayMoiveForNoUrl(final Context context,int id,String title, String channel, String screen,String panorama, String resourceJson)
    {
        _context = context;
        _title = title;
        _movieId = id;
        _dimensionType = (StringUtils.isBlank(screen) || screen.equals("2D"))? "2D" : "3D";
        _dimensionType = StringUtils.isBlank(panorama) ? _dimensionType : "panorama";
        _renderType = (screen.equals("左右")) ? "LR" : "TB";
        String catId = "";
        String assetId = "";
        String episode = "";

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(resourceJson);
            catId = jsonObject.getString("catId");
            assetId = jsonObject.getString("assetId");
            episode = jsonObject.getString("episode");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WaSuLogic.getVideoUrlForU3d(catId, assetId, Integer.parseInt(episode), _handler);
    }


    public String getFilePath(int id ,int type,int source,int catId,int assetId,int index)
    {
        DownloadInfo vo = DownloadUtils.getInstance().getDownloadVO(id);
        if(vo == null)
        {
            return "";
        }
        String url = "";
        switch (type)
        {
            case VRHomeConfig.TYPE_PANORAMA:
                 url = getMediaUrl( vo.getDownloadUrl());
                 break;
            case VRHomeConfig.TYPE_VIDEO:
                //需要添加handler？
                // FIXME: 2016/1/26
                 WaSuLogic.getVideoUrlForU3d(String.valueOf(catId),String.valueOf(assetId),index, _handler);
                break;
            default: break;
        }

        return url;
    }

    /**
     * 更新进度
     * @param id   电影或电视剧的ID
     * @param type 类型，2是全景，3是影视
     * @param position 上次播放到的秒数,默认为0
     * @param channel  为电视剧,则为播放当前集的ID,否则为0,默认为0
     * @param episode 上次播放的channel的ID,默认为0
     * @return
     */
    public void setSeekPosition(Context context,int id ,int type,int position,int channel,int episode )
    {
        if(type == VRHomeConfig.TYPE_PANORAMA)
        {
            UserRestClient.getInstance(context).addPlayPanoramaRecord(id,position);
        }
        else if(type == VRHomeConfig.TYPE_VIDEO)
        {
            UserRestClient.getInstance(context).addPlayRecordForFilm(id,position,episode,channel);
        }
    }

    /**
     * 下载/暂停
     */
    public void changeDownloadState(int id ,int type)
    {
        List<DownloadInfo> downloadInfoVOList = DownloadUtils.getInstance().getlist();
        for (int i = 0; i < downloadInfoVOList.size(); i++) {
            final DownloadInfo vo = downloadInfoVOList.get(i);
            if(type == vo.getType() && vo.getId() == id)
            {
                if(vo.getState() == HttpHandler.State.LOADING)
                {
                    //暂停
                    try {
                        DownloadUtils.getInstance().stopDownload(vo);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                else if(vo.getState() != HttpHandler.State.SUCCESS)
                {
                    //下载
                    try {
                        DownloadUtils.getInstance().resumeDownload(vo, new RequestCallBack<File>() {
                           @Override
                           public void onLoading(long total, long current, boolean isUploading) {
                               float progressValue = ((float)current/(float)total)*100;
                               LogUtils.e("当前进度：" + progressValue + "%");
                               _param = "progress|"+vo.getId()+"|"+vo.getType()+"|"+current+"|" +total ;
                               String params = _param +"|1";
                               UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", params);
                               super.onLoading(total, current, isUploading);
                           }

                           public void onSuccess(ResponseInfo<File> responseInfo) {
                               String params = _param +"|0";
                               UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", params);
                           }

                           public void onFailure(HttpException error, String msg) {

                           }
                       });
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void sendUnityVip(){
        if(UnityCome.getUnityActivity() != null){
            UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "setVip", String.valueOf(UserLogic.getInstance().getVip()));
        }

    }


    public void VRToast(String msg){
        UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "showToast", msg);
    }
}