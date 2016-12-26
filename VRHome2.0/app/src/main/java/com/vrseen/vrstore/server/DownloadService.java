package com.vrseen.vrstore.server;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.util.DownloadUtils;

import java.util.List;

/**
 * Author: wyouflf
 * Date: 13-11-10
 * Time: 上午1:04
 */
public class DownloadService extends Service {

    private static DownloadUtils DOWNLOAD_MANAGER;

    public static DownloadUtils getDownloadManager(Context appContext) {
        if (!DownloadService.isServiceRunning(appContext)) {
            Intent downloadSvr = new Intent("download.service.action");
            downloadSvr.setPackage("com.vrseen.vrstore.activity");
            try {
                appContext.startService(downloadSvr);
            }
            catch (NullPointerException e)
            {
                appContext = MainActivity.instance.getApplicationContext();
                appContext.startService(downloadSvr);
            }
        }
        if (DownloadService.DOWNLOAD_MANAGER == null) {
            DownloadService.DOWNLOAD_MANAGER = new DownloadUtils(appContext);
        }
        return DOWNLOAD_MANAGER;
    }

    public DownloadService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        if (DOWNLOAD_MANAGER != null) {
            try {
                DOWNLOAD_MANAGER.stopAllDownload();
                DOWNLOAD_MANAGER.backupDownloadInfoList();
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
        }
        super.onDestroy();
    }

    public static boolean isServiceRunning(Context context) {
        boolean isRunning = false;
        if(context == null ||context.getSystemService(Context.ACTIVITY_SERVICE) == null)
        {
            return isRunning;
        }

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(DownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
