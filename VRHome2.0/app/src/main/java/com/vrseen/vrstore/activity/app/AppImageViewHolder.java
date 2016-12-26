package com.vrseen.vrstore.activity.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.utilforunity.bean.DownloadInfoVO;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.logic.AppLogic;
import com.vrseen.vrstore.model.app.AppListData;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.DownloadUtils;
import com.vrseen.vrstore.util.FileUtils;
import com.vrseen.vrstore.util.LocalPicUtil;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.wasu.util.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.util.CLog;
import in.srain.cube.util.NetworkStatusManager;
import in.srain.cube.views.list.ViewHolderBase;

public class AppImageViewHolder extends ViewHolderBase<AppListData.App> {

    private CubeImageView _imageView;
    private ImageLoader _imageLoader;
    private TextView _appNameTextView;
    private TextView _appSizeTextView;
    private Button _appDownLoadBtn;
    private ImageView _appScoreImageView;
    private RelativeLayout _appLayout;
    private Context _context;
    private LocalPicUtil _starPicUtil;
    private TextView _typeNameTextView;

    private AppListData.App _appvo;

    public AppImageViewHolder(ImageLoader imageLoader) {
        _imageLoader = imageLoader;//  VrHomeLogic.getInstance().getAppImageLoader(context);
        _context = MainActivity.instance;
        _starPicUtil = new LocalPicUtil();
    }

    @Override
    public View createView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.app_list_item, null);
        _imageView = (CubeImageView) v.findViewById(R.id.app_icon);
        _imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        _appNameTextView = (TextView) v.findViewById(R.id.app_name);
        _appSizeTextView = (TextView) v.findViewById(R.id.app_size);
        _appDownLoadBtn = (Button) v.findViewById(R.id.app_download);
        _appScoreImageView = (ImageView) v.findViewById(R.id.app_score);
        _appLayout = (RelativeLayout) v.findViewById(R.id.app_layout);
        _typeNameTextView = (TextView) v.findViewById(R.id.app_typename);
        return v;
    }

    @Override
    public void setItemData(int position, View convertView) {
        // _fil_imageView.setTag("img"+position);
        //_filmNameTextView.setTag("text"+position);
    }

    private int _position;

    @Override
    public void showData(int position, final AppListData.App appvo) {

        _position = position;

        DownloadInfo downloadInfo = DownloadUtils.getInstance().getDownloadVO(appvo.getId());

        if (downloadInfo != null) {
            HttpHandler.State state = downloadInfo.getState();

            if (state != null) {
                switch (state) {
                    case LOADING:
                        appvo.setState(AppListData.App.State.STATE_LOADING);
                        break;
                    case FAILURE:
                        appvo.setState(AppListData.App.State.STATE_LOADFAILED);
                        break;
                    case SUCCESS:
                        appvo.setState(AppListData.App.State.STATE_LOADSUCCESSED);

                        if (AppLogic.isAppInstalled(_context, appvo.getScheme()))
                            appvo.setState(AppListData.App.State.STATE_INSTALLED);
                        else
                            appvo.setState(AppListData.App.State.STATE_NOINSTALLED);

                        break;
                    case CANCELLED:
                        appvo.setState(AppListData.App.State.STATE_LOADPAUSED);
                        break;
                    case WAITING:
                        appvo.setState(AppListData.App.State.STATE_LOADWAITING);
                        break;
                }
            }
        } else {
            appvo.setState(AppListData.App.State.STATE_NONE);
        }

        _appvo = appvo;

        CLog.d("showData", "position:" + position + ",url:" + appvo.getImage());

        _imageView.loadImage(_imageLoader, appvo.getImage());

        final int id = appvo.getId();
        _appLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailActivity.actionStart(_context, id);
            }
        });

        _appNameTextView.setText(appvo.getName());
        _appScoreImageView.setImageResource(_starPicUtil.getStars(appvo.getAvg_score()));

        String browseText = String.format(_context.getString(R.string.app_browse), appvo.getBrowse_count());
        _appSizeTextView.setText(browseText);
        if (appvo.getApp_characters().size() > 0) {
            AppListData.AppCharacters characterses = appvo.getApp_characters().get(0);
            _typeNameTextView.setText(characterses.getName());
        }

        IntentFilter ss = new IntentFilter();
        ss.addAction(appvo.getScheme());
        _context.registerReceiver(new MyBroadcastReciver(), ss);

        AppLogic.updateDownBtnText(_appDownLoadBtn, appvo, null);
        _appDownLoadBtn.setTag(position);
        _appDownLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkStatusManager.getInstance(_context).isNetworkConnectedHasMsg( false)) {
                    ToastUtils.show(_context,"网络错误",1000);
                    return;
                }else {
                    AppLogic.handleDown(_context, appvo);
                }
            }
        });
    }


    private class MyBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            DownloadInfo downloadInfo = DownloadUtils.getInstance().getDownloadVO(_appvo.getId());

            if (downloadInfo != null) {
                HttpHandler.State state = downloadInfo.getState();

                if (state != null) {
                    switch (state) {
                        case LOADING:
                            _appvo.setState(AppListData.App.State.STATE_LOADING);
                            break;
                        case FAILURE:
                            _appvo.setState(AppListData.App.State.STATE_LOADFAILED);
                            break;
                        case SUCCESS:
                            _appvo.setState(AppListData.App.State.STATE_LOADSUCCESSED);

                            if (AppLogic.isAppInstalled(_context, _appvo.getScheme()))
                                _appvo.setState(AppListData.App.State.STATE_INSTALLED);
                            else
                                _appvo.setState(AppListData.App.State.STATE_NOINSTALLED);

                            break;
                        case CANCELLED:
                            _appvo.setState(AppListData.App.State.STATE_LOADPAUSED);
                            break;
                        case WAITING:
                            _appvo.setState(AppListData.App.State.STATE_LOADWAITING);
                            break;
                    }
                }
            } else {
                _appvo.setState(AppListData.App.State.STATE_NONE);
            }

            String curPercent = intent.getStringExtra("CurPercent");
            if ((int) _appDownLoadBtn.getTag() == _position) {
                AppLogic.updateDownBtnText(_appDownLoadBtn, _appvo, curPercent);
            }


            if (curPercent != null && curPercent.equals("安装")) {
                CommonRestClient.getInstance(_context).downloadTimes(String.valueOf(_appvo.getId()), new AbstractRestClient.ResponseCallBack() {
                    @Override
                    public void onFailure(Response resp, Throwable e) {
                        CommonUtils.showResponseMessage(_context, resp, e, R.string.app_get_list_error);
                    }

                    @Override
                    public void onSuccess(Response resp) throws JSONException {

                    }
                });
            }
        }
    }

}
