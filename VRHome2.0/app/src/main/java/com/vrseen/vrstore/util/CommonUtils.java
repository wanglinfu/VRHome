/**
 *
 */
package com.vrseen.vrstore.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.WelcomeActivity;
import com.vrseen.vrstore.http.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.conn.HttpHostConnectException;

/**
 * Created by jiangs on 16/4/27.
 */
public class CommonUtils {
    private static String TAG = "CommonUtils";

    private static long lastClickTime;
    private static int lastClickViewId;
    private static final int KEY_PREVENT_TS = -20000;
    private static String CHENIU_APPKEY;

    /**
     * 获取androidManifest中的CHENIU_APPKEY
     *
     * @return CHENIU_APPKEY
     */
    public static String getCheNiuAppKey(Context context) {
        if (StringUtils.isBlank(CHENIU_APPKEY)) {
            ApplicationInfo info;
            try {
                info = context.getPackageManager().getApplicationInfo(
                        context.getPackageName(), PackageManager.GET_META_DATA);
                CHENIU_APPKEY = info.metaData.getString("CHENIU_APPKEY");
                Log.d(TAG, "CHENIU_APPKEY=" + CHENIU_APPKEY);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "getCheNiuAppKey failed.", e);
            }
        }
        return CHENIU_APPKEY;
    }


    /**
     * 判断是否异常的双击（300毫秒内不能点击不同控件，500毫秒内不能点击相同控件）
     *
     * @return
     */
    public static boolean isFastDoubleClick(View v) {
        long now = System.currentTimeMillis();
        //检查是否被阻止点击
        if (v.getTag(KEY_PREVENT_TS) != null && v.getTag(KEY_PREVENT_TS) instanceof Long) {
            if ((Long) v.getTag(KEY_PREVENT_TS) > now) {
                Log.d(TAG, "Click prevented before " + v.getTag(KEY_PREVENT_TS));
                return true;
            }
        }
        long interval = now - lastClickTime;
        if (lastClickViewId == v.getId() && interval < 500) {
            Log.d(TAG, "isFastDoubleClick:same view");
            return true;
        } else if (interval < 300) {
            Log.d(TAG, "isFastDoubleClick:not same view");
            return true;
        }
        lastClickViewId = v.getId();
        lastClickTime = now;
        return false;
    }

    /**
     * 阻止点击事件若干毫秒，在使用isFastDoubleClick检查是会判断
     *
     * @param view
     * @param ts
     */
    public static void preventClick(View view, long ts) {
        view.setTag(KEY_PREVENT_TS, System.currentTimeMillis() + ts);
    }

    /**
     * 退出程序
     *
     * @param activity
     */
    public static void exitApp(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 重启应用
     *
     * @param context
     */
    public static void triggerRebirth(Context context) {
        triggerRebirth(context, getRestartIntent(context));
    }


    public static Bitmap getBitMap(String urlstring)
    {
        try {
            //建立网络连接
            URL imageURl=new URL(urlstring);
            URLConnection con=imageURl.openConnection();
            con.connect();
            InputStream in=con.getInputStream();
            Bitmap bitmap= BitmapFactory.decodeStream(in);

            return bitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  null;
    }


    /**
     * Call to restart the application process using the specified intent.
     * <p/>
     * Behavior of the current process after invoking this method is undefined.
     */
    private static final String KEY_RESTART_INTENT = "phoenix_restart_intent";

    private static void triggerRebirth(Context context, Intent nextIntent) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // In case we are called with non-Activity context.
        intent.putExtra(KEY_RESTART_INTENT, nextIntent);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
        Runtime.getRuntime().exit(0); // Kill kill kill!
    }

    private static Intent getRestartIntent(Context context) {
        Intent defaultIntent = new Intent(Intent.ACTION_MAIN, null);
        defaultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        defaultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        String packageName = context.getPackageName();
        PackageManager packageManager = context.getPackageManager();
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(defaultIntent, 0)) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo.packageName.equals(packageName)) {
                defaultIntent.setComponent(new ComponentName(packageName, activityInfo.name));
                return defaultIntent;
            }
        }

        throw new IllegalStateException("Unable to determine default activity for "
                + packageName
                + ". Does an activity specify the DEFAULT category in its intent filter?");
    }

    /**
     * 判断服务是否正在运行
     *
     * @param mContext
     * @param clazz
     * @return
     */
    public static boolean isServiceRunning(Context mContext, Class clazz) {
        boolean isRunning = false;
        final String appPackage = mContext.getPackageName();
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo service : serviceList) {
            if (service.service.getPackageName().equals(appPackage)
                    && service.service.getClassName().equals(clazz.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 链接清空按钮和输入框
     *
     * @param editText
     * @param clearBtn
     */
    public static void connectEditTextAndClearButton(final EditText editText, final View clearBtn) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    clearBtn.setVisibility(View.VISIBLE);
                } else {
                    clearBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
        if (editText.getText().length() == 0) {
            clearBtn.setVisibility(View.INVISIBLE);
        } else {
            clearBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * 获取适应屏幕的七牛图片参数
     *
     * @param context
     * @return
     */
    public static String getQiniuParamFitScreen(Context context) {
        int screenWidth = DensityUtil.getScreenWidth(context);
        int screenHeight = DensityUtil.getScreenWidth(context);
        return "?imageView/2/w/" + screenWidth + "/h/" + screenHeight + "&";
    }

    /**
     * 判断是否wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        boolean result = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnected()) {
            result = true;
        } else {
            Log.d(TAG, "wifi not connected.");
        }
        return result;
    }


    /**
     * 是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

 /*   *//**
     * 友盟自定义统计
     * @param context
     * @param map
     * @param eventId
     *//*

    public static void StaticClickMethod(Context context,Map<String,String> map,String eventId){
        MobclickAgent.onEvent(context, eventId, map);

    }*/

    /**
     * 获取网关IP
     *
     * @param context
     * @return
     */
    public static String getGatewayIp(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wm.getDhcpInfo();
        WifiInfo wifiinfo = wm.getConnectionInfo();
        //ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
        Log.d(TAG, "Wifi info----->" + Formatter.formatIpAddress(wifiinfo.getIpAddress()));
        Log.d(TAG, "DHCP info gateway----->" + Formatter.formatIpAddress(dhcpInfo.gateway));
        Log.d(TAG, "DHCP info netmask----->" + Formatter.formatIpAddress(dhcpInfo.netmask));
        return Formatter.formatIpAddress(dhcpInfo.gateway);
    }


    /**
     * 显示服务器返回的消息
     *
     * @param context        context
     * @param resp           服务器返回的Response
     * @param throwable      异常信息
     * @param defaultMessage 默认消息
     */
    public static void showResponseMessage(Context context, Response resp, Throwable throwable, String defaultMessage) {
        if (context == null) {
            return;
        }
        if (resp != null && !StringUtils.isBlank(resp.getMessage())) {
            ToastUtils.show(context, resp.getMessage(), Toast.LENGTH_SHORT, ToastUtils.TYPE_ERROR);
        } else {
            StringBuilder msg = new StringBuilder();
            //添加默认信息
            if (defaultMessage != null && defaultMessage.length() > 0) {
                msg.append(defaultMessage);
            }
            //添加错误信息
            if (throwable != null &&
                    (throwable instanceof org.apache.http.conn.ConnectTimeoutException
                            || throwable instanceof SocketTimeoutException
                            || throwable instanceof HttpHostConnectException
                            || (throwable.getMessage() != null && throwable.getMessage().contains("UnknownHostException")))) {
                if (msg.length() > 0) {
                    msg.append(": ");
                }


                msg.append(context.getString(R.string.network_exception));
            }
            //判断是否需要提示
            if (msg.length() > 0) {
                ToastUtils.show(context, msg.toString(), Toast.LENGTH_SHORT, ToastUtils.TYPE_ERROR);
            }
        }
    }

    /**
     * 显示服务器返回的消息
     *
     * @param context             context
     * @param resp                服务器返回的Response
     * @param throwable           异常信息
     * @param defaultMessageResId 默认消息资源ID
     */
    public static void showResponseMessage(Context context, Response resp, Throwable throwable, int defaultMessageResId) {
        if (context == null) {
            return;
        }
        showResponseMessage(context, resp, throwable, context.getResources().getString(defaultMessageResId));
    }


    public static void showServerError(Context context) {
        showServerError(context, null);
    }

    public static void showServerError(Context context, final String message) {
        String msg = message;
        if (StringUtils.isBlank(msg)) {
            //TODO
//            msg = context.getString(R.string.server_error);
        }
        ToastUtils.show(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String result = "ERROR";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            result = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int result = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            result = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取设备id(imei)
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String id = "";
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            id = tm.getDeviceId();
            if (TextUtils.isEmpty(id)) {
                id = android.provider.Settings.Secure.getString(
                        context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
        } catch (Throwable e) {
            //可能会由于没有权限啥的。。。
            Log.d(TAG, "getDeviceId error.", e);
        }
        return id;
    }

    /**
     * 内容格式化成银行卡的格式
     *
     * @param editText
     */
    public static void addFourDigitCardFormatWatcher(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String original = s.toString();
                String formatted = StringUtils.formatBankCardNo(s.toString());
                if (!original.equals(formatted)) {
                    int sec = editText.getSelectionStart();
                    editText.setText(formatted);
                    //保持光标在原本位置;如果光标前面是空格，变长：光标向后移动1位，变短：向前移动
                    if (sec > 0 && formatted.length() > sec && formatted.charAt(sec - 1) == ' ') {
                        if (formatted.length() > original.length()) {
                            sec++;
                        } else {
                            sec--;
                        }
                    }
                    editText.setSelection(Math.min(formatted.length(), sec));
                }
            }
        });
    }

    /**
     * 打开新的activity，加上动画
     *
     * @param context   需要为Activity时才有动画效果
     * @param intent
     * @param enterAnim
     */
    public static void startActivityWithAnim(final Context context, final Intent intent, int enterAnim) {
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(enterAnim, android.R.anim.fade_out);
        }
    }

    /**
     * 打开新的activity，自右向左打开
     *
     * @param context 需要为Activity时才有动画效果
     * @param intent
     */
    public static void startActivityWithAnim(final Context context, final Intent intent) {
        context.startActivity(intent);
        if (context instanceof Activity) {

            ((Activity) context).overridePendingTransition(R.anim.slide_right_in, android.R.anim.fade_out);
        }
    }

    /**
     * 打开新的activity，自右向左打开
     *
     * @param context 需要为Activity时才有动画效果
     * @param intent
     */
    public static void startActivityForResultWithAnim(final Context context, final Intent intent, final int reqCode) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, reqCode);

            activity.overridePendingTransition(R.anim.slide_right_in, android.R.anim.fade_out);
        }
    }

    /**
     * 关闭activity，加上动画
     *
     * @param activity
     * @param exitAnim
     */
    public static void finishActivityWithAnim(final Activity activity, int exitAnim) {
        activity.finish();
        // 退出动画
        activity.overridePendingTransition(android.R.anim.fade_in, exitAnim);
    }


    /**
     * 点击空白处,关闭输入法软键盘
     */
    public static void onHideSoftInput(Activity activity) {
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 检查应用是否有界面已经打开（即使已经切到主界面后，也会认为打开）
     *
     * @param context
     * @return
     */
    public static boolean appIsLanuched(Context context) {
        if (isBackground(context)) {
            return true;
        }
        final String appPackage = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(appPackage) &&
                    info.baseActivity.getPackageName().equals(appPackage)) {
                isAppRunning = true;
                Log.i("app status :前台", "top :" + info.topActivity.getPackageName() + " base : " + info.baseActivity.getPackageName());
                //find it, break
                break;
            }
        }
        return isAppRunning;
    }

    @Deprecated
    private static boolean isBackground(Context context) {
        final String appPackage = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(appPackage)) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("app status :后台", appProcess.processName);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 打开应用. 应用在前台不处理,在后台就直接在前台展示当前界面, 未开启则重新启动
     */
    public static void openApplicationFromBackground(Context context) {
        Log.v(TAG, "openApplicationFromBackground: begin");
        Intent intent;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (!list.isEmpty() && list.get(0).topActivity.getPackageName().equals(context.getPackageName())) {
            //此时应用正在前台, 不作处理
            return;
        }
        Log.v(TAG, "openApplicationFromBackground: app not run foreground");
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(context.getPackageName())) {
                intent = new Intent();
                intent.setComponent(info.topActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                return;
            }
        }
        Log.v(TAG, "openApplicationFromBackground: app not running , will start");
        intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(intent);

    }

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static String getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd.HHmmss");
        String dateString = formatter.format(currentTime);
//        ParsePosition pos = new ParsePosition(8);
//        Date currentTime_2 = formatter.parse(dateString, pos);
        return dateString;
    }


    // ** randomWord 产生任意长度随机字母数字组合
    // ** randomFlag-是否任意长度 min-任意长度最小位[固定位数] max-任意长度最大位
    //** xuanfeng 2014-08-28

    public static String randomWord(Boolean randomFlag, int min,int max)
    {
        String str = "";
        int range = min;
        String[] arr = {"0","1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
//
//        // 随机产生
        if(randomFlag){
            range = (int)Math.round(Math.random() * (max-min)) + min;
        }


        for(int i=0; i<range; i++){
            int pos = (int)Math.round(Math.random() * (arr.length-1));
            str += arr[pos];
        }
        return str;
    }

    public static String getSign(Context context,String value)
    {
        String pwd = "QB4E0E";
        String sign = "";
        try {
            InputStream inputStream = context.getAssets().open("zVR.pfx");
            PrivateKey priKey = ReadPFX.GetPvkformPfx(inputStream,pwd);
            sign = ReadPFX.generateSHA1withRSASigature(priKey, value);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sign;
    }


    /**
     * 获取当前activity
     */
    public static String getCurActivityName(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(am == null)
            return null;
        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
        if(runningTasks == null || runningTasks.size() == 0)
            return null;
        ActivityManager.RunningTaskInfo rti = runningTasks.get(0);
        if(rti == null)
            return null;
        ComponentName component = rti.topActivity;
        if(component == null)
            return null;
        return component.getClassName();
    }

}
