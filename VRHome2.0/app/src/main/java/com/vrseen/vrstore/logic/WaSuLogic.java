package com.vrseen.vrstore.logic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.util.LogUtils;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.util.ToastUtils;
import com.wasu.authsdk.AuthListener;
import com.wasu.module.log.WLog;
import com.wasu.vr.WasuVRSdk;

/**获取华数资源
 * Created by John on 16/1/25.
 */
public class WaSuLogic {

    public static String videoUrl;
    private static Context _context = null;

    /**
     * 初始化华数sdk
     * @param context
     */
    public static void initWaSu(Context context)
    {
        _context = context;
        WasuVRSdk.getInstance().init(context);
        WasuVRSdk.getInstance().register(new AuthListener() {

            @Override
            public void result(int ret, String extra, Object retData) {
                WLog.i("WasuVR", "register ret=" + ret + ",extra=" + extra);
            }
        });
    }

    /**
     * 注册华数
     */
    public static void registerWaSu()
    {

        WasuVRSdk.getInstance().register(new AuthListener() {
            @Override
            public void result(int i, String s, Object o) {

                LogUtils.e("register num:" + i + "\b" + "extra:" + s);
            }
        });
    }


    /**
     * u3d获取视频播放地址
     * @param catId         栏目id
     * @param assetId       资产id
     * @param indext        剧集
     */
    public static void getVideoUrlForU3d(final String catId,
                                   final String assetId,
                                   final int indext,
                                   final Handler handler)
    {
        WasuVRSdk.getInstance().geturl(catId, assetId, indext,
                1300000L, new WasuVRSdk.WasuVRCallback() {
                    @Override
                    public void result(int i, String s) {
                        videoUrl = s;
                        Message message = handler.obtainMessage();
                        message.what = FilmDetailActivity.GET_WASU_RESOURCE_URL_AUTO;
                        if (videoUrl != null) {
                            message.obj = videoUrl;
                        }
                        if (i == 3) {
                            message.obj = 3;
                        }
                        handler.sendMessage(message);
                    }
                });
    }

    /**
     * 获取视频播放地址
     * @param catId         栏目id
     * @param assetId       资产id
     * @param indext        剧集
     */
    public static void getVideoUrl( final boolean isAutoPlay,
                                    String catId,
                                    String assetId,
                                    int indext,
                                    final Handler handler)
    {
//        catId = "231357";
//        assetId = "2598632";
//        indext = -1;
        Long rate = 1300000L;

        WasuVRSdk.getInstance().geturl(catId, assetId, indext,
                rate, new WasuVRSdk.WasuVRCallback() {
            @Override
            public void result(int i, String s) {
                videoUrl = s;
                Message message = handler.obtainMessage();
                if (!isAutoPlay) {
                    message.what = FilmDetailActivity.GET_WASU_RESOURCE_URL;
                }else{
                    message.what = FilmDetailActivity.GET_WASU_RESOURCE_URL_AUTO;
                }
                //videoUrl = "http://hlsglsb.wasu.tv/hsy800.m3u8?action=hls&Contentid=6299912";
                if (videoUrl != null) {
                    message.obj = videoUrl;
                }
                if (i == 3) {
                    message.obj = 3;
                }
                LogUtils.d("videoUrl:" + videoUrl);
                if(i == -1)
                {
                    if(_context != null && _context.getApplicationContext()!= null)
                    {
                        ToastUtils.showShort(_context.getApplicationContext(),s);
                    }
                    return;
                }

                handler.sendMessage(message);
                //LogUtils.e("资源catId:"+catId+"资源assetId:"+assetId);
                //LogUtils.e("获取华数资源地址状态:"+i+"\b"+"获取到的地址:"+s);
            }
        });

    }
}
