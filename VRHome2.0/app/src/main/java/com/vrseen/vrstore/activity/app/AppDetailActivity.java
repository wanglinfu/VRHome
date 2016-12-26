package com.vrseen.vrstore.activity.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.PageIndicator;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.adapter.banner.BannerAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.logic.AppLogic;
import com.vrseen.vrstore.model.app.AppListData;
import com.vrseen.vrstore.model.bannel.Banner;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.model.app.AppDetailData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.ConfigDefaultImageUtils;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.DownloadUtils;
import com.vrseen.vrstore.util.FileUtils;
import com.vrseen.vrstore.util.LocalPicUtil;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.util.NetworkStatusManager;

public class AppDetailActivity extends BaseActivity {

    private Context _context;
    private ImageLoader _imageLoader;
    private Button _downBtn;
    private ViewPager _bannerViewPager;
    private BannerAdapter _bannerAdapter;
    private CubeImageView _cubeImage;
    private ImageView _appScoreImageView;
    private ProgressRelativeLayout _progressRelativeLayout;
    private DownloadInfo _downloadInfo;
    private final List<Banner> _banners = new ArrayList<Banner>();
    private int _appId;
    private final Handler _handler = new Handler();
    private HttpHandler.State _state = null;
    private AppDetailData.AppDetail _appDetail;

    private final Runnable bannerChange = new Runnable() {
        @Override
        public void run() {
            _handler.postDelayed(this, VRHomeConfig.BANNER_CHANGE_INTERVAL);
            if (_banners.size() > 1 && _bannerViewPager != null) {
                int i = (_bannerViewPager.getCurrentItem() + 1) % _banners.size();
                _bannerViewPager.setCurrentItem(i, true);
            }
        }
    };

    public static void actionStart(Context context, int id) {
        Intent intent = new Intent(context, AppDetailActivity.class);
        intent.putExtra("appId", id);
        CommonUtils.startActivityWithAnim(context, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("AppDetailActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        _context = this;

        mState = NetworkStatusManager.State.UNKNOWN;
        mReceiver = new ConnectivityBroadcastReceiver();
        startListening(_context);


        _appId = getIntent().getIntExtra("appId", 0);
        initView();

    }

    public void onResume() {
        super.onResume();
        _handler.postDelayed(bannerChange, VRHomeConfig.BANNER_CHANGE_INTERVAL);
        _downloadInfo = DownloadUtils.getInstance().getDownloadVO(_appId);
//        if (_downloadInfo != null) {
//            AppLogic.showDownText(_context,_downBtn,_downloadInfo,_appDetail.getScheme());
//        }
    }

    public void onPause() {
        super.onPause();
        _handler.removeCallbacks(bannerChange);
    }

    private MyBroadcast myBroadcast = null;

    private void requestGetDetail(final int appId) {

        CommonRestClient.getInstance(this).getAppDetail(appId, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e, R.string.app_get_list_error);
            }

            @Override
            public void onSuccess(Response resp) {

                AppDetailData data = (AppDetailData) resp.getModel();

                LocalPicUtil localPicUtil = new LocalPicUtil();

                DownloadInfo downloadInfo = DownloadUtils.getInstance().getDownloadVO(appId);
                AppDetailData.AppDetail appDetail = data.getData();
                if (downloadInfo != null) {
                    HttpHandler.State state = downloadInfo.getState();

                    if (state != null) {
                        switch (state) {
                            case LOADING:
                                appDetail.setState(AppListData.App.State.STATE_LOADING);
                                break;
                            case FAILURE:
                                appDetail.setState(AppListData.App.State.STATE_LOADFAILED);
                                break;
                            case SUCCESS:
                                appDetail.setState(AppListData.App.State.STATE_LOADSUCCESSED);

                                if (AppLogic.isAppInstalled(_context, appDetail.getScheme()))
                                    appDetail.setState(AppListData.App.State.STATE_INSTALLED);
                                else
                                    appDetail.setState(AppListData.App.State.STATE_NOINSTALLED);

                                break;
                            case CANCELLED:
                                appDetail.setState(AppListData.App.State.STATE_LOADPAUSED);
                                break;
                            case WAITING:
                                appDetail.setState(AppListData.App.State.STATE_LOADWAITING);
                                break;
                        }
                    }
                }else {
                    appDetail.setState(AppListData.App.State.STATE_NONE);
                }

                //名字
                ((TextView) findViewById(R.id.textview_name)).setText(appDetail.getName());
                ((TextView) findViewById(R.id.app_name)).setText(appDetail.getName());

                _appScoreImageView.setImageResource(localPicUtil.getStars(appDetail.getAvg_score()));
                _cubeImage.loadImage(_imageLoader, appDetail.getImage());
                //type
                ((TextView) findViewById(R.id.textview_type)).setText(Html.fromHtml(String.format(getString(R.string.app_type_data)
                        , appDetail.getApp_characters().get(0).getName())));
                //大小
                ((TextView) findViewById(R.id.textview_size)).setText(Html.fromHtml(String.format(getString(R.string.app_size_data)
                        , appDetail.getApp_platforms().get(0).getMax_version_platform().getSize())));
                //version
                ((TextView) findViewById(R.id.textview_version)).setText(Html.fromHtml(String.format(getString(R.string.app_version_data)
                        , appDetail.getMax_version().getVersion())));
                //下载
                ((TextView) findViewById(R.id.textview_down)).setText(Html.fromHtml(String.format(getString(R.string.app_down_data)
                        , appDetail.getDownload_count())));

                ((TextView) findViewById(R.id.app_detail_text)).setText(appDetail.getMax_version().getDescription());

                //广告
                List<AppDetailData.AppversionBanner> appversionBannerList = appDetail.getMax_version().getApp_images();
                if (appversionBannerList != null && appversionBannerList.size() > 0) {
                    for (int i = 0; i < appversionBannerList.size(); i++) {
                        Banner banner = new Banner();
                        banner.setImagurl(appversionBannerList.get(i).getImage());
                        _banners.add(banner);
                    }

                    _bannerAdapter = new BannerAdapter(_context, _banners);
                    _bannerViewPager.setAdapter(_bannerAdapter);
                    PageIndicator bannerIndicator = (PageIndicator) findViewById(R.id.indicator);
                    bannerIndicator.setViewPager(_bannerViewPager);
                    _bannerAdapter.setOnItemClickListener(new BannerAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(Banner b) {

                        }
                    });
                }
                _appDetail = appDetail;
                //刷新下载按钮文字
                AppLogic.updateDownBtnText(_downBtn, appDetail, null);

                myBroadcast = new MyBroadcast();
                IntentFilter filter = new IntentFilter();
//                filter.addAction(AppLogic.intentFilter);
                filter.addAction(appDetail.getScheme());
                _context.registerReceiver(myBroadcast, filter);

                _progressRelativeLayout.showContent();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();

        _cubeImage = (CubeImageView) findViewById(R.id.app_icon);
        _appScoreImageView = (ImageView) findViewById(R.id.app_score);
        _imageLoader = ConfigDefaultImageUtils.getInstance().getAppImageLoader(this);
        _bannerViewPager = (ViewPager) findViewById(R.id.banner_view_pager);
        _downBtn = (Button) findViewById(R.id.btn_down);
        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);
        _progressRelativeLayout.showProgress();

        if (!NetworkStatusManager.getInstance(this).isNetworkConnectedHasMsg(false)) {
            _progressRelativeLayout.showErrorText(getResources().getString(R.string.get_data_fail));
            return;
        }

        findViewById(R.id.view_back).setOnClickListener(this);
        _downBtn.setOnClickListener(this);
        requestGetDetail(getIntent().getIntExtra("appId", 0));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_back:
                this.finish();
                break;
            case R.id.btn_down:

                if (!NetworkStatusManager.getInstance(this).isNetworkConnectedHasMsg(false)) {
                    ToastUtils.show(_context,getResources().getString(R.string.network_exception),1000);
                    return;
                }else {
                    AppLogic.handleDown(_context, _appDetail);
                }
                break;
            default:
                break;
        }
    }

    private class MyBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String curPercent = intent.getStringExtra("CurPercent");
            if (_appDetail != null) {
                AppLogic.updateDownBtnText(_downBtn, _appDetail, curPercent);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myBroadcast != null) {
            unregisterReceiver(myBroadcast);
        }
        sendBroadcast((new Intent()).setAction("apprefresh"));

        stopListening();
    }


    private boolean mListening;
    private NetworkStatusManager.State mState;
    private NetworkInfo mNetworkInfo;
    private NetworkInfo mOtherNetworkInfo;
    private String mReason;
    private boolean mIsFailOver;
    private boolean mIsWifi = false;
    private ConnectivityBroadcastReceiver mReceiver;

    private class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        private boolean isNetState = true;
        private Context mContext;

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            mContext = context;

            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || mListening == false) {

                return;
            }

            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (noConnectivity) {
                mState = NetworkStatusManager.State.NOT_CONNECTED;
            } else {
                mState = NetworkStatusManager.State.CONNECTED;
            }

            mNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            mOtherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            mReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            mIsFailOver = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            mIsWifi = NetworkStatusManager.checkIsWifi(_context);

            if (mNetworkInfo.isConnected() && mNetworkInfo.isAvailable()) {
                // 连接成功后的业务逻辑
                // 具体的网络连接方式
                switch (mNetworkInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        Log.i("gzeyao", getResources().getString(R.string.wifi_connect));
                        // 执行业务
                        if (isNetState == false) {
                            isNetState = true;
                            ToastUtils.showShort(context, "" + context.getResources().getText(R.string.wifi_connect));

                        }
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        Log.i("gzeyao", getResources().getString(R.string.mobile_is_connecting));
                        // 执行业务
                        if (isNetState == false) {
                            isNetState = true;
                            ToastUtils.showShort(context, "" + context.getResources().getText(R.string.mobile_net_connect));

                            DownloadInfo downinfo = DownloadUtils.getInstance().getDownloadVO(_appDetail.getId());
                            if(downinfo!=null) {
                                HttpHandler.State nowState = downinfo.getState();
                                if (nowState.equals(HttpHandler.State.LOADING) || nowState.equals(HttpHandler.State.WAITING)) {
                                    AppLogic.pauseDownload(_context, _appDetail, downinfo);
                                }
                            }
                            handleNoWifiState();
                        }
                        break;
                    default:
                        Log.i("gzeyao", getResources().getString(R.string.no_connect));
                        if (isNetState == true) {
                            isNetState = false;
                            ToastUtils.showShort(context, "" + context.getResources().getText(R.string.network_exception));

                        }
                        break;
                }
            } else {   //沒有網絡連接
                ToastUtils.showShort(context, "" + context.getResources().getText(R.string.network_exception));
                isNetState = false;
                DownloadInfo downinfo = DownloadUtils.getInstance().getDownloadVO(_appDetail.getId());
                if(downinfo!=null) {
                    HttpHandler.State nowState = downinfo.getState();
                    if (nowState.equals(HttpHandler.State.LOADING) || nowState.equals(HttpHandler.State.WAITING)||nowState.equals(HttpHandler.State.FAILURE)) {
                        AppLogic.pauseDownload(_context, _appDetail, downinfo);
                    }
                }
            }
        }

    }

    private void handleNoWifiState() { //无wifi，但有手机流量
        if ((boolean) SharedPreferencesUtils.getParam(_context.getApplicationContext(), "IS_NOWIFI_Download", false)) {  //非wifi可以播放

            //可以继续下载

        } else { //弹框提醒
            DialogHelpUtils.getConfirmDialog(_context, getString(R.string.film_play_warn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {  //确定, 保存到sp中

                    //继续下载
                    AppLogic.handleDown(_context,_appDetail);
                    SharedPreferencesUtils.setParam(_context.getApplicationContext(), "IS_NOWIFI_Download", true);

                }
            }, null).show();
        }
    }

    public synchronized void startListening(Context context) {
        if (!mListening) {
            _context = context;

            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(mReceiver, filter);
            mListening = true;
        }
    }

    public synchronized void stopListening() {
        if (mListening) {
            _context.unregisterReceiver(mReceiver);
            _context = null;
            mNetworkInfo = null;
            mOtherNetworkInfo = null;
            mIsFailOver = false;
            mReason = null;
            mListening = false;
        }
    }

}
