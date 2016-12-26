package com.vrseen.vrhome;

import android.app.Activity;
import android.app.IZtevrManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.unity.GoogleUnityActivity;
import com.unity3d.player.UnityPlayer;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.VRHomeApplication;
import com.vrseen.vrstore.logic.TeleplayLogic;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;

import java.util.ArrayList;

/**
 * Created by FX on 2016/5/22 19:39.
 * 描述:
 */
public class UnityCome {
    private static Activity m_UnityActivity;


    private static int BRIGHTNESS = (int)(255f * 0.5f);

    public static boolean isInVRView = false;//因为SetUnityActivity中初始化Sensor,所以此值默认为true



    private static ArrayList<String> waitingSendMassge = new ArrayList<>();




    public void SetUnityActivity(Activity unityActivity) {
        this.m_UnityActivity = unityActivity;
        setWindowFormat();
        startVrseenDeviceService();
        //setZTEVRMode(1);
        U3dMediaPlayerLogic.getInstance().sendUnityVip();

        VRHomeApplication.getInstance().addActivity(unityActivity);
        sendWaitingMassage();
        //isInVRView = true;

    }

    private void setWindowFormat(){
        GoogleUnityActivity google = ((GoogleUnityActivity) m_UnityActivity);
        UnityPlayer unityPlayer = google.getUnityPlayer();
        if(unityPlayer != null) {
            Window window = unityPlayer.currentActivity.getWindow();
            //window.setFormat(PixelFormat.RGBX_8888);
        }
    }
    public void startVrseenDeviceService(){
        //UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "sVrseenDeviceService", "true");
    }

    public void destroyVrseenDeviceService(){
        //UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "stopVrseenDeviceService", "");
    }

    public int add(int a ,int b){
        return a + b;
    }

    public static int who(int a ,int b){
        return a + b;
    }

    public static void changeAppBrightness(Context context,int brightness) {
        if (context != null) {
            GoogleUnityActivity google = ((GoogleUnityActivity) m_UnityActivity);
            UnityPlayer unityPlayer = google.getUnityPlayer();
            if(unityPlayer != null){
                Window window = unityPlayer.currentActivity.getWindow();
                //Window window = ((Activity)context).getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                if (brightness == -1) {
                    lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;//恢复系统默认亮度
                } else {
                    lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
                }
                window.setAttributes(lp);
            }
        }

    }

    public void unityIsRuning(){

    }

    private void showLog(String str){
        UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", "log|" + str);
    }

    /**
     * 返回全景，影视文件地址
     * @param id
     * @param type  类型， 2，全景，3是影视
     * @param source  如华数，联通
     * @param catId         栏目id
     * @param assetId       资产id
     * @return
     */
    public String getFilePath(int id ,int type,int source,int catId,int assetId,int index)
    {
        return U3dMediaPlayerLogic.getInstance().getFilePath(id, type, source, catId, assetId, index);
    }

    /**
     * 播放影视文件地址
     * @param
     * @param title idle 名称
     * @param channel  如华数，联通
     * @param screen  2d/3d(左右/上下)
     * @parm resourceJson json数据
     * @return
     */

    public void toPlayMoiveForNoUrl(int id, String title,String channel, String screen,String panorama, String resourceJson) {
        U3dMediaPlayerLogic.getInstance().toPlayMoiveForNoUrl(m_UnityActivity, id, title, channel, screen, panorama, resourceJson);
    }

    /**
     *
     *续播
     * @param id 影视ID
     * @parem episode 第几集
     */
    public boolean toPlayNextMovieForNoUrl(int id,int episode_index) {
        Log.e("toPlayNextMovieForNoUrl", id + " = " + episode_index);
        return TeleplayLogic.getInstance().getEpisodeUrlForU3d(id,episode_index);
        //return false;
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
    public void setSeekPosition(int id ,int type,int position,int channel, int episode) {
        U3dMediaPlayerLogic.getInstance().setSeekPosition(m_UnityActivity, id, type, position, channel, episode);
    }


    /**
     * u3d发起下载
     *
     * @param id
     * @param type ,1为App，2为全景,3为影视
     */
    public void toDownload(int id, int type,String url) {
        U3dMediaPlayerLogic.getInstance().toDownload(m_UnityActivity, id, type, url);
    }

    /**
     * 获取下载列表
     * @return json
     */
    public boolean getMyDownload() {

        U3dMediaPlayerLogic.getInstance().initMyDownload(m_UnityActivity);

        return true;
    }

    /**
     * 获取昵称
     * @return
     */
    public String getUserNikename()
    {
        return U3dMediaPlayerLogic.getInstance().getUserNikename();
    }

    /**
     * 下载/暂停
     */
    public void changeDownloadState(int id ,int type)
    {
        U3dMediaPlayerLogic.getInstance().changeDownloadState(id, type);
    }

    public void FinishThis(){
        destroyVrseenDeviceService();
        //setZTEVRMode(0);

        MainActivity.actionStart(m_UnityActivity);

        GoogleUnityActivity google = ((GoogleUnityActivity) m_UnityActivity);
        UnityPlayer unityPlayer = google.getUnityPlayer();
        if(unityPlayer != null){
            unityPlayer.clearFocus();
        }
        //if(google != null){
            //google.finish();
        //}
        //isInVRView = false;
    }

    public static void exitVR(){
        //setZTEVRMode(0);
        MainActivity.actionStart(m_UnityActivity);
        //isInVRView = false;
    }

    public static void unitySendMessage(String msg)
    {
        if (msg != null) {
            if (m_UnityActivity == null) {
                waitingSendMassge.add(msg);
            } else {
                //if(!isInVRView){
                    //setZTEVRMode(1);
                    //UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "sVrseenDeviceService", "true");
                //}
                //isInVRView = true;
                UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", msg);
            }
        }
        //else
        //UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", "loadlevel|Splash");
    }

    private static void sendWaitingMassage(){
        int size = waitingSendMassge.size();
        if(size > 0){

            for(int i = 0;i < size;i++){
                UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive",waitingSendMassge.get(i));
            }

            waitingSendMassge.clear();
        }
    }



    public static void setZTEVRMode(int mode){
        if(Build.MODEL.contains("ZTE A2017")) {
            IZtevrManager vr = (IZtevrManager) m_UnityActivity.getSystemService("ztevr");
            if (vr != null) {
                vr.vrenableVRMode(mode);
            }
        }else {
            //天机2VRmode就已经有亮度控制
            //changeAppBrightness(m_UnityActivity, mode == 1 ? BRIGHTNESS : -1);
        }
    }

    public static Activity getUnityActivity(){
        return m_UnityActivity;
    }

    public static void onPause(boolean isPause){
        isInVRView = !isPause;
        if(isPause){

        }else{

        }
    }


    public static void VRRequestFocus(){
        if(m_UnityActivity != null) {
            GoogleUnityActivity google = ((GoogleUnityActivity) m_UnityActivity);
            UnityPlayer unityPlayer = google.getUnityPlayer();
            if (unityPlayer != null) {
                unityPlayer.requestFocus();
            }
        }
    }

}
