package com.vrseen.vrhome;

import android.app.Activity;
import android.app.IZtevrManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.unity3d.player.UnityPlayer;
import com.vrseen.utilforunity.util.Thumbnailer;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.VRHomeApplication;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.logic.TeleplayLogic;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

public class UnityPlayerActivity extends Activity
{

	protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code

	private static int BRIGHTNESS = (int)(255f * 0.5f);

	private boolean flag = false;

	private Thumbnailer _localThumb = null;

	//统计
	private final String _pageName = "UnityPlayerActivity";

	private Handler _handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case VRHomeConfig.GET_LOCALRES_THUMB:
					UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", "localThumb|"+U3dMediaPlayerLogic.getInstance().getMyDownload());
					break;
			}

		}
	};

	// Setup activity layout
	@Override protected void onCreate (Bundle savedInstanceState)
	{

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		VRHomeApplication.getInstance().addActivity(this);
		Log.d("----------------", "---");

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

		mUnityPlayer = new UnityPlayer(this);
		setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();


	}

	public static void changeAppBrightness(Context context,int brightness) {
		if (context != null) {
			Window window = ((Activity)context).getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			if (brightness == -1) {
				lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;//恢复系统默认亮度
			} else {
				lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
			}
			window.setAttributes(lp);
		}

	}

	public void unityIsRuning(){
		flag = true;
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
		return U3dMediaPlayerLogic.getInstance().getFilePath(id,type,source,catId,assetId,index);
	}

	/**
	 * 播放影视文件地址
	 * @param
	 * @param tit idle 名称
	 * @param channel  如华数，联通
	 * @param screen  2d/3d(左右/上下)
	 * @parm resourceJson json数据
	 * @return
	 */

	public void toPlayMoiveForNoUrl(int id, String title,String channel, String screen,String panorama, String resourceJson) {
		U3dMediaPlayerLogic.getInstance().toPlayMoiveForNoUrl(this, id, title, channel, screen, panorama, resourceJson);
	}


	/**
	 * 获取是否为vip
	 * return  true 是，false 否
	 * created at 2016/5/20
	 */
	public boolean isVip()
	{
		return UserLogic.getInstance().isVIp();
	}

	/**
	 * 获取token
	 * created at 2016/5/20
	 */
	public String getToken()
	{
		String token = (String) SharedPreferencesUtils.getParam(UnityPlayerActivity.this,
				SPFConstant.KEY_USER_TOKEN, "");
		return token;
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
		U3dMediaPlayerLogic.getInstance().setSeekPosition(this, id, type, position, channel, episode);
	}


	/**
	 * u3d发起下载
	 *
	 * @param id
	 * @param type ,1为App，2为全景,3为影视
	 */
	public void toDownload(int id, int type,String url) {
		U3dMediaPlayerLogic.getInstance().toDownload(this, id, type, url);
	}

	/**
	 * 获取下载列表
	 * @return json
	 */
	public boolean getMyDownload() {

		_localThumb = new Thumbnailer(this,_handler,VRHomeConfig.GET_LOCALRES_THUMB);
		_localThumb.start();

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
		MainActivity.actionStart(this);
	}


	public void setZTEVRMode(int mode){
		if(Build.MODEL.contains("ZTE A2017")) {
			IZtevrManager vr = (IZtevrManager) getSystemService("ztevr");
			if (vr != null) {
				vr.vrenableVRMode(mode);
			}
		}else {
			//天机2VRmode就已经有亮度控制
			changeAppBrightness(this, mode == 1 ? BRIGHTNESS : -1);
		}
	}

	public void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent);
	}

	public void onStart()
	{
		super.onStart();
		//setZTEVRMode(1);
		Intent intent = getIntent();
		String param =  intent.getStringExtra("param");
		if(param != null)
			UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", param);
		else
			UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "AndroidReceive", "loadlevel|Splash");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// mUnityPlayer.quit();
		// this.finish();
		//FinishThis();
}

	// Quit Unity
	@Override protected void onDestroy ()
	{
		UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "stopVrseenDeviceService", "");
		setZTEVRMode(0);
		mUnityPlayer.quit();


		if(_localThumb != null) {
			_localThumb.stop();
			_localThumb = null;
		}

		_handler.removeCallbacksAndMessages(null);
		_handler = null;


		super.onDestroy();
	}

	// Pause Unity
	@Override protected void onPause()
	{
		UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "sVrseenDeviceService", "false");
				setZTEVRMode(0);
		mUnityPlayer.pause();
		if(_localThumb != null)
			_localThumb.pause();

		super.onPause();
		//finish();
		//zx_note 统计
		//MobclickAgent.onPageEnd(_pageName);
		//MobclickAgent.onPause(this);
	}

	// Resume Unity
	@Override protected void onResume()
	{
		UnityPlayer.UnitySendMessage("ReceiveFromPlatform", "sVrseenDeviceService", "true");
		setZTEVRMode(1);
		mUnityPlayer.resume();
		if(_localThumb != null)
			_localThumb.resume();

		super.onResume();
		//zx_note 统计
		//MobclickAgent.onPageStart(_pageName);
		//MobclickAgent.onResume(this);
	}

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     {
		//if (keyCode == KeyEvent.KEYCODE_BACK && flag == false) {
			//Log.e("onKeyDown =", keyCode + " ");
			//return false;
		//}

		return mUnityPlayer.injectEvent(event);
	}
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   {
		//if (keyCode == KeyEvent.KEYCODE_BACK && flag == false) {
			//Log.e("onKeyDown =", keyCode + " ");
			//CommonUtil.toast("当前在加载中，无法退出", this);
			//return false;
		//}

		return mUnityPlayer.injectEvent(event);
	}
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}
