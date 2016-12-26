package com.vrseen.vrstore;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.fragment.app.AppFragment;
import com.vrseen.vrstore.fragment.channel.ChannelFragment;
import com.vrseen.vrstore.fragment.find.FindFragment;
import com.vrseen.vrstore.fragment.home.HomeFragment;
import com.vrseen.vrstore.fragment.user.MineFragment;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.ZTERestClient;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.logic.WaSuLogic;
import com.vrseen.vrstore.model.user.UserInfo;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.DownloadUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.util.UmengUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zx.AuthComp.IMyService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.ShareSDK;
import cz.msebera.android.httpclient.Header;
import in.srain.cube.util.NetworkStatusManager;


/**
 * Created by jiangs on 16/4/27.
 */
public class MainActivity extends BaseActivity implements View.OnTouchListener {

    private final String TAG = "MainActivity";
    public final static String COMMAND_PROTOCOL = "protocol";
    private final String CURRENT_TAB = "CURRENT_TAB";
    private FragmentTabHost mTabHost;
    private LayoutInflater layoutInflater;
    private static boolean _isExit = false;
    private static boolean _hasTask = false;
    private Timer _tExit = new Timer();
    private MainTimerTask _task;



    public static MainActivity instance;

    //判断改变悬浮按钮旋转动画
    private boolean turned = false;
    private boolean ismoving = false;
    private boolean movedLater = false;//true=刚刚移动过
    //获取手机的宽高
    private int screenwidth;
    private int screenheight;
    private int lastX;
    private int lastY;

    private Animation toFork;
    private Animation toNormal;

    private ImageButton float_button;
    private Context mContext;
    private final String mPageName = "AnalyticsHome";

    // Fragment界面
    private Class<?> fragmentArray[] = {HomeFragment.class,
            ChannelFragment.class,
            AppFragment.class,
            FindFragment.class,
            MineFragment.class};
    // Tab按钮图片
    private int mTabImage[] = {R.drawable.tab_home_btn,
            R.drawable.tab_channel_btn,
            R.drawable.tab_app_btn,
            R.drawable.tab_film_btn,
            R.drawable.tab_mine_btn};

    private int mTabText[] = {R.string.tab_home, R.string.tab_full_scene, R.string.tab_apps, R.string.tab_find, R.string.tab_mine};

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        CommonUtils.startActivityWithAnim(context, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initView();
        NetworkStatusManager.init(this);
        init();
        instance = this;

    }

    private void init() {
        UmengUtils.getInstance().initUmeng(this);
        ShareSDK.initSDK(this);
        DownloadUtils.initDownload(this);
        WaSuLogic.initWaSu(getApplicationContext());
    }

    protected void initView() {
        super.initView();

        float_button = (ImageButton) findViewById(R.id.float_button);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenwidth = dm.widthPixels;
        screenheight = dm.heightPixels - 130;
        toFork = AnimationUtils.loadAnimation(this, R.anim.rotate_to_fork);
        toNormal = AnimationUtils.loadAnimation(this, R.anim.rotate_to_normal);

        float_button.setOnClickListener(this);
        float_button.setOnTouchListener(this);

        layoutInflater = LayoutInflater.from(this);
        tabHostInit();
    }

    private void tabHostInit() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        // 得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(
                    getResources().getString(mTabText[i])).setIndicator(
                    getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
    }

    public void onTabChange(String tabId, int index) {
        if (mTabHost != null) {
            // mTabHost.onTabChanged(tabId);
            mTabHost.setCurrentTab(index);

        }
    }

    public int getCurrentTab(){
        if (mTabHost != null) {
            return mTabHost.getCurrentTab();
        }
        return 0;
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.item_main_tab, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mTabImage[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTabText[index]);

        RelativeLayout rl_tabitem = (RelativeLayout) view.findViewById(R.id.rl_tabitem);
        return view;
    }


    public void initNeedNetWork(){
        if(NetworkStatusManager.getInstance(this).isNetworkConnectedHasMsg(false))
        {
            init();
        }

    }

    //再按一次退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (_isExit == false) {
                _isExit = true;
                ToastUtils.showShort(this, R.string.confirm_out);
                if (_task != null)
                    _task.cancel();                // 将原任务从队列中移除
                _task = new MainTimerTask();          // 新建一个任务
                _tExit.schedule(_task, 2000);

            } else {
                //zx_note 统计
                MobclickAgent.onKillProcess(mContext);
                VRHomeApplication.getInstance().exit(); //调用退出方法
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
        return false;
    }

    class MainTimerTask extends TimerTask {

        public void run() {
            _isExit = false;
        }
    }



    @Override
    public void onClick(View v) {

        int vid = v.getId();
        switch (vid) {
            case R.id.float_button:
                if (!ismoving) {
                    if (movedLater) {
                        movedLater = false;
                        return;
                    }
                    //float_button.startAnimation(toFork);
                    U3dMediaPlayerLogic.getInstance().comeinVR(this,null);

//
                }
                break;
        }

    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //获取控件一开始的位置
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                ismoving = false;
                break;

            case MotionEvent.ACTION_MOVE:
                int det = 10;//小于此偏移量都当没有移动
                //获取移动的距离
                int dx = (int) (event.getRawX() - lastX);
                int dy = (int) (event.getRawY() - lastY);
                if (Math.abs(dx) < det && Math.abs(dy) < det) {//当只是移动没有点击时，无需变换悬浮按钮的状态
                    //如果移动时正好时叉号状态，变回原样
                    ismoving = false;
                } else {
                    movedLater = true;
                    ismoving = true;
                    if (turned) {
                        turned = false;
                        //float_button.startAnimation(toNormal);
                    }
                }
                //getLeft()方法得到的是控件坐标距离父控件原点(左上角，坐标（0，0）)的x轴距离，
                //getRight()是控件右边距离父控件原点的x轴距离，同理，getTop和getButtom是距离的y轴距离。
                int left = v.getLeft() + dx;
                int right = v.getRight() + dx;
                int top = v.getTop() + dy;
                int bottom = v.getBottom() + dy;

                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }

                if (right > screenwidth) {
                    right = screenwidth;
                    left = right - v.getWidth();
                }

                if (top < 0) {
                    top = 0;
                    bottom = top + v.getHeight();
                }

                if (bottom > screenheight) {
                    bottom = screenheight;
                    top = bottom - v.getHeight();
                }

                //到达边界后不能再移动
                v.layout(left, top, right, bottom);
                //Log.i("@@@@@@", "position:" + left + ", " + top + ", " + right + ", " + bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //Log.i("lastx ,lasty", "lastx:"+lastX+"lasty:"+lastY);
                break;

            case MotionEvent.ACTION_UP:

                int detUp = 10;//小于此偏移量都当没有移动
                //获取移动的距离
                int dxUp = (int) (event.getRawX() - lastX);
                int dyUp = (int) (event.getRawY() - lastY);

                int leftUp = v.getLeft() + dxUp;
                int rightUp = v.getRight() + dxUp;
                int topUp = v.getTop() + dyUp;
                int bottomUp = v.getBottom() + dyUp;

//                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v,"translationX", 0);

                if (leftUp <= screenwidth / 2) {

                    ObjectAnimator.ofFloat(v, "translationX",leftUp, 0f).setDuration(300).start();
                    leftUp = 0;
                    rightUp = leftUp + v.getWidth();

                }

                if (rightUp > screenwidth / 2) {

                    rightUp = screenwidth;
                    leftUp = rightUp - v.getWidth();
                    ObjectAnimator.ofFloat(v, "translationX",-360f, 0f).setDuration(300).start();

                }

                if (topUp < 0) {
                    topUp = 0;
                    bottomUp = topUp + v.getHeight();
                }

                if (bottomUp > screenheight) {
                    bottomUp = screenheight;
                    topUp = bottomUp - v.getHeight();
                }
//                if (leftUp <= screenwidth / 2) {
//                    animation = new TranslateAnimation(leftUp, 0, 0, 0);
//                } else {
//
//                    animation = new TranslateAnimation(leftUp, screenwidth, 0, 0);
//                }
//
//                animation.setDuration(500);
//                v.startAnimation(animation);
                v.layout(leftUp, topUp, rightUp, bottomUp);
                //Log.i("@@@@@@", "position:" + left + ", " + top + ", " + right + ", " + bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //Log.i("lastx ,lasty", "lastx:"+lastX+"lasty:"+lastY);

                break;
        }

        return false;
    }
    public void onResume() {



        super.onResume();
        MobclickAgent.onPageStart( mPageName); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);



    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }

    public void onDestroy() {
        super.onDestroy();
        if(myService != null)
        {
            this.unbindService(serviceConnection);
        }
        NetworkStatusManager.getInstance(this).stopListening();
    }


    //----------------------------以下中兴帐号-----------------------
    private IMyService myService =  null;

    public boolean bindAuthService(){
        //联接中兴帐号系统
        boolean ret = false;
        if(myService == null){
            Log.w(TAG, "try bindService");
            Intent intent = new Intent("org.zx.AuthComp.IMyService");
            intent.setPackage("org.zx.AuthComp");
            ret = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        return ret;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_CANCELED && data == null)
        {
            LogUtils.e(getResources().getString(R.string.account_update_hin));
        }
        switch ( requestCode )
        {
            case VRHomeConfig.ZTE_REQUEST_ADD_ACCOUNT:
                //添加账号
                if(resultCode == Activity.RESULT_OK && data != null)
                {
                    if(data.getIntExtra("add_account_result",1) == 0)
                    {
                        //添加账号成功
                        LogUtils.e(getResources().getString(R.string.account_add_success));
                        getZteToken();
                    }
                    else if(data.getIntExtra("add_account_result",1) == 1)
                    {
                        //添加账号失败
                        LogUtils.e(getResources().getString(R.string.zte_account_add_fail));
                    }
                }
                else
                {
                    try {
                        String str = myService.getUser();
                        if(str != null && !str.isEmpty())
                        {
                            getZteToken();
                            return;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case VRHomeConfig.ZTE_REQUEST_ACCOUNT_MANAGER:
                //账号管理
                //退出账户
                if(resultCode == Activity.RESULT_OK && data != null)
                {
                    getZteToken();
                    if(data.getIntExtra("modify_info_result",1) == 0)
                    {
                        //修改用户信息成功时
                    }
                    else if(data.getIntExtra("modify_info_result",1) == 1)
                    {
                        //修改用户信息失败时
                    }
                    else if(data.getIntExtra("switch_user_result",1) == 0)
                    {
                        //切换用户成功

                    }
                    else if(data.getIntExtra("switch_user_result",1) == 1)
                    {
                        //切换用户失败
                    }
                    else if(data.getIntExtra("sign_out_result",1) == 0)
                    {
                        //注销用户成功

                    }
                    else if(data.getIntExtra("sign_out_result",1) == 1)
                    {
                        //注销用户失败
                        //退出
                        //UserLogic.getInstance().loginOut(this);
                    }

//                     账号组件发起的结果回调时：resultCode=Activity.RESULT_OK && data!=null ：
//                     修改用户信息成功时：data.getIntExtra(“modify_info_result”,1)==0
//                     修改用户密码成功时：data.getIntExtra(“modify_pwd_result”,1)==0
//                     切换用户成功时：data.getIntExtra(“switch_user_result”,1)==0
//                     注销用户成功时：data.getIntExtra(“sign_out_result”,1)==0
//                     失败时，上述值=1,应用根据这些状态做自己的业务逻辑处理
                }
                break;
            default:
                break;
        }
        try {
            String str = myService.getUser();
            //为空的时候，处理退出
            if(str == null || str.isEmpty())
            {
                if(UserLogic.getInstance().checkLoginSuc())
                {
                    UserLogic.getInstance().loginOut(this);
                }
            }else {
                if(UserLogic.getInstance().checkLoginSuc())
                    UserLogic.getInstance().getUserInfo().set_avatarBitmap(myService.getUserImage2());

                MineFragment.instance.updateInfo();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void getZteToken()
    {
        //生成32位的随机数作为id
        //final String nonce = CommonUtil.getRandom32Code();
        final String nonce = CommonUtils.randomWord(false, 32,32);
        final String timestamp = CommonUtils.getNowDate();
        final String str = "appid="+VRHomeConfig.VR_HOME_ID+"&nonce="+nonce+"&timestamp="+timestamp;

        Map<String,String> map = new HashMap<String,String>();
        map.put("appid",VRHomeConfig.VR_HOME_ID);
        map.put("nonce",nonce);
        map.put("timestamp", timestamp);

        String[] keys = map.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        StringBuilder query = new StringBuilder();
        String strUnSign = "";
        boolean hasParam = false;

        for (String key : keys) {
            String value = map.get(key);
            if (!key.isEmpty() && !value.isEmpty())
            {
                if(!hasParam )
                {
                    strUnSign += key +"="+value;
                    hasParam = true;
                }else
                {
                    strUnSign += "&"+key +"="+value;
                }
                // query.append(key).append(value);
            }
        }

        final String sign = CommonUtils.getSign(MainActivity.this, strUnSign);

        new Thread(){
            @Override
            public void run(){
                try {
                    String token =  myService.getTokenByApp(VRHomeConfig.VR_HOME_ID, nonce, timestamp, sign);
                    ZTERestClient.getInstance(MainActivity.this).requestZTEToken(token, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            if (response != null) {
                                CommonRestClient.getInstance(MainActivity.this).saveToken(response);
                                getZTEUserInfo();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            CommonUtils.showResponseMessage(MainActivity.this, null, null, R.string.film_cancel_collect_fail);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getZTEUserInfo(){
        //获取个人数据

        ZTERestClient.getInstance(MainActivity.this).getMyInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (response != null) {
                    Gson _gson = new Gson();
                    UserLogic.getInstance().setUserInfo(_gson.fromJson(response.optJSONObject("data").toString(), UserInfo.class));
                    try {
                        Bitmap _bitmap = myService.getUserImage2();
                        UserLogic.getInstance().getUserInfo().set_avatarBitmap(_bitmap);

                        String user = myService.getUser();
                        if(user != null && !user.isEmpty() && user.contains("ext_3rdacc")){
                            UserInfo userInfo = UserLogic.getInstance().getUserInfo();

                            JSONObject jsonObject = new JSONObject(user);
                            jsonObject = jsonObject.getJSONObject("extenInfo");
                            JSONArray jsonArray = jsonObject.getJSONArray("ext_3rdacc");
                            if(jsonArray.length() > 0) {
                                jsonObject = jsonArray.getJSONObject(0);
                                userInfo.setAvatar(jsonObject.getString("fig_url"));
                                userInfo.setExtType(jsonObject.getString("type"));
                            }
                        }
                        Log.e("zte user =",user);
                    }catch (Exception e){

                    }
                    if(MineFragment.instance != null)
                        MineFragment.instance.handler.sendEmptyMessage(2);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                CommonUtils.showResponseMessage(MainActivity.this, null, null, R.string.film_cancel_collect_fail);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }




    /**
     * 初始化
     *
     */
    public void initZTE()
    {
        Log.e("initZte","fx--------------");
        if(myService == null){
            CommonUtils.showResponseMessage(MainActivity.this,null,null,R.string.zte_error);
            return;
        }

        //if(UserLogic.getInstance().checkLoginSuc())
        try {
            if(UserLogic.getInstance().checkLoginSuc())
            {
                //启动账号管理
                Bundle bundle = myService.startAccountManagerActivity();
                Intent intent = bundle.getParcelable("intent");
                intent.putExtra("invoker", VRHomeConfig.VR_HOME_ID);//此VrHomeId可以自行定义但需通知帐号小组）
                startActivityForResult(intent, VRHomeConfig.ZTE_REQUEST_ACCOUNT_MANAGER);
            }
            else
            {
                Bundle bundle = myService.startAddAccountActivity();
                Intent intent = bundle.getParcelable("intent");
                intent.putExtra("invoker", VRHomeConfig.VR_HOME_ID);//此VrHomeId可以自行定义但需通知帐号小组）
                startActivityForResult(intent, VRHomeConfig.ZTE_REQUEST_ADD_ACCOUNT);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection(){
        @Override
        public void onServiceDisconnected(ComponentName name){
            Log.e(TAG, "onServiceDisconnected");
            myService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            Log.e(TAG, "onServiceConnected");
            myService = IMyService.Stub.asInterface(service);
            //fx 2016-5-9 自动登录中兴帐号
            try {
                String strUser = myService.getUser();
                if (strUser != null) {

                    initZTE();
                }
            }catch (Exception e){

            }
        }
    };

    //----------------------------以上中兴帐号-----------------------

}
