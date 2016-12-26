package com.vrseen.vrstore.logic;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.model.app.AppDetailData;
import com.vrseen.vrstore.model.app.AppListData;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.DownloadUtils;
import com.vrseen.vrstore.util.FileUtils;
import com.vrseen.vrstore.util.ToastUtils;

import java.io.File;

public class AppLogic {

    public static final String intentFilter = "DownState";

    public static void startApp(Context context, String appPackage) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackage);
        context.startActivity(intent);
    }

    public static void installAPP(Context context, String url) {
        File file = new File(url);
        if (file == null) {
            ToastUtils.showShort(context, R.string.no_app);
            return;
        }
        //安装
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了
        context.startActivity(intent);
    }

    public static boolean isAppInstalled(Context context, String pakageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pakageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            return false;
        } else {

            return true;
        }
    }

    public static void updateDownBtnText(Button btn, AppListData.App app, String percent) {

        switch (app.getState()) {
            case STATE_NONE:
                btn.setText(R.string.download_xiazai);
//                btn.setBackgroundResource(R.color.lite_blue);
                break;
            case STATE_LOADING:
                //zx_note 走更新广播
                btn.setText(percent);
                break;
            case STATE_LOADFAILED:
                btn.setText(R.string.download_again);
                break;
            case STATE_LOADPAUSED:
                btn.setText(R.string.download_jixu);
                break;
            case STATE_INSTALLED:
                btn.setText(R.string.download_open);
                break;
            case STATE_LOADWAITING:
                btn.setText(R.string.download_wait);
                break;
            case STATE_NOINSTALLED:
                btn.setText(R.string.app_intall);
                break;
            case STATE_LOADSUCCESSED:
                btn.setText(R.string.app_intall);
                break;
        }

    }

    public static boolean handleDown(Context context, AppListData.App app) {
        if (app == null) {
            Log.e("AppLogic", "handleDown line: 195, app = null");
            return false;
        }

        DownloadInfo downinfo = DownloadUtils.getInstance().getDownloadVO(app.getId());
//
      if(downinfo!=null) {
          if (downinfo.getState() == HttpHandler.State.FAILURE) {
              app.setState(AppListData.App.State.STATE_LOADFAILED);
          }
      }

        switch (app.getState()) {
            case STATE_NONE:

                AppLogic.createNewDownload(context, app);

                break;
            case STATE_LOADING:

                if (downinfo == null) {
                    Log.e("AppLogic", "line 149: downinfo = null");
                    return false;
                }
                AppLogic.pauseDownload(context, app, downinfo);

                break;
            case STATE_LOADFAILED:

//                if(downinfo!=null && (new File(downinfo.getFileSavePath())).exists()){
//                    AppLogic.startApp(context, app.getScheme());
//                }else{
                AppLogic.resumeDownload(context, downinfo, app);
//                }
                break;
            case STATE_LOADPAUSED:
                if (downinfo == null) {
                    Log.e("AppLogic", "line 158: downinfo = null");
                    return false;
                }

                Intent intent = new Intent();
                intent.setAction(app.getScheme());
                intent.putExtra("CurPercent",context.getResources().getString(R.string.download_wait));
                context.sendBroadcast(intent);

                AppLogic.resumeDownload(context, downinfo, app);

                break;

            case STATE_INSTALLED:
                AppLogic.startApp(context, app.getScheme());
                break;
            case STATE_LOADWAITING:
                ToastUtils.showOnly(context, context.getString(R.string.app_download_wait), 2);
                break;
            case STATE_NOINSTALLED:
                if (downinfo == null) {
                    Log.e("AppLogic", "line 158: downinfo = null");
                    return false;
                }

                AppLogic.installAPP(context, downinfo.getFileSavePath());
                break;

        }

        return true;
    }

    public static boolean isVr(Context context){

        String curActivityName=CommonUtils.getCurActivityName(context);

        if (!curActivityName.isEmpty() && !curActivityName.contains("GoogleUnityActivity")) {

            return false;
        }
        return true;
    }

    public static boolean createNewDownload(final Context context, final AppListData.App app) {
        if (app == null) {
            Log.e("AppLogic", "createNewDownload line 173: app=null");
            return false;
        }

        final DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setResourceId(app.getId());
        downloadInfo.setAutoRename(true);
        downloadInfo.setAutoResume(true);
        downloadInfo.setScheme(app.getScheme());
        downloadInfo.setType(VRHomeConfig.DOWN_APP);

        String url = "";
        if (app instanceof AppDetailData.AppDetail)
            url = ((AppDetailData.AppDetail) app).getApp_platforms().get(0).getMax_version_platform().getLink();
        else {
            url = app.getQrtext();
        }

        if (url == null) {
            ToastUtils.show(context, "未找到下载地址", 1000);
            return false;
        }

        url = url.replace(" ", "%20");
        downloadInfo.setDownloadUrl(url);
        String name = app.getName();
//        name = name.replace(" ", "%20");
        downloadInfo.setFileName(name);

        String targetUrl = FileUtils.getFileName(downloadInfo.getDownloadUrl());
        targetUrl = targetUrl.replace("%20", "");
        targetUrl = VRHomeConfig.DEFAULT_SAVE_OTHERS_PATH + targetUrl;
        downloadInfo.setFileSavePath(targetUrl);
        downloadInfo.setThumbUrlForLocal(app.getImage());


        DownloadInfo downinfo = DownloadUtils.getInstance().getDownloadVO(app.getId());
        File file=new File(targetUrl);
        if(downinfo==null && file.exists()){
            file.delete();
        }

        Log.e("1111111111111", downloadInfo.getFileName());

        if (downloadInfo == null) {
            ToastUtils.showOnly(context, context.getString(R.string.app_download_get_error), 2);
            return false;
        }
        try {
            DownloadUtils.getInstance().addNewDownload(downloadInfo, new RequestCallBack<File>() {

                @Override
                public void onLoading(long total, long current, boolean isUploading) {

                    Log.e("2222222222222", app.getName());
                    app.setState(AppListData.App.State.STATE_LOADING);
                    //1.来个广播，通知下按钮更新
                    float progressValue = (float) current / (float) total;
                    sendBroadcast(context, progressValue, app.getScheme());
                }

                public void onSuccess(ResponseInfo<File> responseInfo) {

                    app.setState(AppListData.App.State.STATE_LOADSUCCESSED);

                    //zx_note_code
                    //1.判断当前是否在VR模式下，如果是，则在VR内给个提示框；否，则判断是否是普通全屏观影模式，是，则Toast提示，否，则对话框提示安装
                    //2.来个广播，通知下按钮更新
                    boolean isInVR = isVr(context);
                    if (isInVR) {

                    } else {
                        sendBroadcast(context, 1.0f, app.getScheme());
                        installAPP(context, downloadInfo.getFileSavePath());
                    }
                    //CommonUtils.installAPP(_context, downloadInfo.getFileSavePath());
                }

                public void onFailure(HttpException error, String msg) {

                    app.setState(AppListData.App.State.STATE_LOADFAILED);

                    //zx_note_code
                    //1.判断当前是否在VR模式下，如果是，则在VR内给个提示框；否，则判断是否是普通全屏观影模式，是，则Toast提示，否，则对话框提示安装
                    //2.来个广播，通知下按钮更新

                    boolean isInVR = isVr(context);
                    if (isInVR) {

                    } else {
                        sendBroadcast(context, -1.0f, app.getScheme());
                    }

                }


            });
        } catch (DbException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean pauseDownload(Context context, AppListData.App app, DownloadInfo info) {
        if (info == null || app == null) {
            Log.e("AppLogic", "pauseDownload line 234: downinfo=null || app=null");
            return false;
        }

        try {
            DownloadUtils.getInstance().stopDownload(info);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }

        app.setState(AppListData.App.State.STATE_LOADPAUSED);

        sendBroadcast(context, -1.0f, app.getScheme());

        return true;

    }

    public static boolean resumeDownload(final Context context, final DownloadInfo downinfo, final AppListData.App app) {
        if (downinfo == null || app == null) {
            Log.e("AppLogic", "resumedownload line 250: downinfo=null || app=null");
            return false;
        }

        try {
            DownloadUtils.getInstance().resumeDownload(downinfo, new RequestCallBack<File>() {
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    //app.setState(AppListData.App.State.STATE_LOADING);
                    //zx_note_code
                    //1.来个广播，通知下按钮更新

                    app.setState(AppListData.App.State.STATE_LOADING);

                    float progressValue = (float) current / (float) total;
                    sendBroadcast(context, progressValue, app.getScheme());

                }

                public void onSuccess(ResponseInfo<File> responseInfo) {
                    app.setState(AppListData.App.State.STATE_LOADSUCCESSED);

                    //zx_note_code
                    //1.判断当前是否在VR模式下，如果是，则在VR内给个提示框；否，则判断是否是普通全屏观影模式，是，则Toast提示，否，则对话框提示安装
                    //2.来个广播，通知下按钮更新
                    boolean isInVR = false;
                    if (isInVR) {

                    } else {
                        sendBroadcast(context, 1.0f, app.getScheme());
                        installAPP(context, downinfo.getFileSavePath());
                    }
                    //CommonUtils.installAPP(_context, downloadInfo.getFileSavePath());
                }

                public void onFailure(HttpException error, String msg) {
                    app.setState(AppListData.App.State.STATE_LOADFAILED);
                    //zx_note_code
                    //1.判断当前是否在VR模式下，如果是，则在VR内给个提示框；否，则判断是否是普通全屏观影模式，是，则Toast提示，否，则对话框提示安装
                    //2.来个广播，通知下按钮更新

                    boolean isInVR = false;
                    if (isInVR) {

                    } else {
                        sendBroadcast(context, -1.0f, app.getScheme());
                    }
                }

            });
        } catch (DbException e) {
            e.printStackTrace();
            return false;

        }

        return true;
    }

    private static void sendBroadcast(Context context, float curPercent, String scheme) {
        Intent intent = new Intent();

        intent.setAction(scheme);

        if (curPercent != -1.0f) {
            if (curPercent == 1.0f) {
                intent.putExtra("CurPercent", context.getResources().getString(R.string.app_intall));
            } else if (curPercent == -2.0f) {
                intent.putExtra("CurPercent", context.getResources().getString(R.string.download_wait));
            } else {
                intent.putExtra("CurPercent", String.valueOf((int) (curPercent * 100)) + "%");
            }
        }

        context.sendBroadcast(intent);
    }

}
