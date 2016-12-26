package com.vrseen.vrstore.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebViewClient;

import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.onlineconfig.OnlineConfigLog;
import com.umeng.onlineconfig.UmengOnlineConfigureListener;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.vrseen.vrstore.BuildConfig;
import com.vrseen.vrstore.R;

import org.json.JSONObject;

/**
 * 友盟
 * Created by mll on 2016/5/6.
 */
public class UmengUtils {

    private static UmengUtils _instance = null;

    public static UmengUtils getInstance()
    {
        if(_instance == null)
        {
            _instance = new UmengUtils();
        }
        return _instance;
    }

    private String version_last = "";

    public void initUmeng(Context context)
    {
        //初始化自动更新
        OnlineConfigAgent.getInstance().updateOnlineConfig(context);
        OnlineConfigAgent.getInstance().setDebugMode(BuildConfig.DEBUG);

        //初始化统计
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        MobclickAgent.openActivityDurationTrack(false);
        //MobclickAgent.setAutoLocation(true);
        MobclickAgent.setSessionContinueMillis(1000);
        MobclickAgent.enableEncrypt(true);//加密
        initVrhomeConfig(context);
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    private void initVrhomeConfig(Context context)
    {
       // MobclickAgent.startWithConfigure(
               // new MobclickAgent.UMAnalyticsConfig(context, "a572c5fd67e58ec622001898", "Umeng", MobclickAgent.EScenarioType.E_UM_NORMAL));
    }

    private void initZTEConfig()
    {

    }

    /**
     * 自动更新
     * 参数/返回
     * created at 2016/3/25
     */
    public void UpdateVersionAuto(final Context context)
    {
        //检测是否要强制更新
        String upgradeMode = OnlineConfigAgent.getInstance().getConfigParams(context, "upgrade_mode");
        version_last = OnlineConfigAgent.getInstance().getConfigParams(context, "version_last");
        OnlineConfigAgent.getInstance().setOnlineConfigListener(configureListener);

        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(context);

        if(!TextUtils.isEmpty(upgradeMode))
        {
            //判断是否强制升级
            if(isNeedForceUpdate(context,upgradeMode))
            {
                UmengUpdateAgent.forceUpdate(context);//这行如果是强制更新就一定加上
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int i, UpdateResponse updateResponse) {

                    }
                });

                UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
                    @Override
                    public void onClick(int status) {

                        switch (status) {
                            case 5:
                                break;
                            default:
                                ToastUtils.showShort(context,R.string.umeng_update_error);
                                Activity ac = (Activity)context;
                                ac.finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(0);
                                break;
                        }
                    }
                });
            }
        }
    }

    UmengOnlineConfigureListener configureListener = new UmengOnlineConfigureListener() {
        @Override
        public void onDataReceived(JSONObject json) {
            // TODO Auto-generated method stub
            OnlineConfigLog.d("OnlineConfig", "json=" + json);
        }
    };


    public boolean isNeedForceUpdate(Context context,String allowMinVersion)
    {
        String localVersion = CommonUtils.getVersionName(context);
        String[] allowArr = allowMinVersion.split("\\.");
        String[] currentArr = localVersion.split("\\.");
        for (int i = 0; i < 3 ; i++) {
            if(allowArr[i] == null)
            {
                allowArr[i] ="0";
            }
            if(currentArr[i] == null)
            {
                currentArr[i] ="0";
            }
        }
        //默认3位
        if(Integer.parseInt(allowArr[0]) > Integer.parseInt(currentArr[0]))
        {
            //需强制
            return true;
        }
        else if (Integer.parseInt(allowArr[0]) < Integer.parseInt(currentArr[0]))
        {
            return false;
        }
        if(Integer.parseInt(allowArr[1]) > Integer.parseInt(currentArr[1]))
        {
            //需强制
            return true;
        }
        else if(Integer.parseInt(allowArr[1]) < Integer.parseInt(currentArr[1]))
        {
            return false;
        }
        if(Integer.parseInt(allowArr[2]) >= Integer.parseInt(currentArr[2]))
        {
            //需强制
            return true;
        }

        return false;
    }

    /**
     * 手动更新
     * 参数/返回
     * created at 2016/3/25
     */
    public void UpdateVersionManual(Context context)
    {
        if(context == null) return;

        if(version_last.isEmpty() )
        {
            ToastUtils.showShort(context,R.string.new_best);
            return;
        }

        if(!version_last.equals(CommonUtils.getVersionName(context))) {
            UmengUpdateAgent.setDefault();
            UmengUpdateAgent.setUpdateOnlyWifi(false);
            UmengUpdateAgent.forceUpdate(context);
        }else{
            ToastUtils.showShort(context,R.string.new_best);
        }
    }
}
