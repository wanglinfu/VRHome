package com.vrseen.vrstore.activity.panorama;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.adapter.panorama.PanoramaListAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.model.panorama.PanoramaDetailData;
import com.vrseen.vrstore.model.panorama.PanoramaListData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.DownloadUtils;
import com.vrseen.vrstore.util.FileUtils;
import com.vrseen.vrstore.view.ExpGridView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;
import in.srain.cube.util.NetworkStatusManager;

public class PanoramaDetailActivity extends BaseActivity implements View.OnClickListener {

    private Context _context;
    /**
     * 退出键
     */
    private ImageView _backImageView;
    /**
     * 标题栏
     */
    private TextView _titleTextView;

    /**
     * 全景名
     */
    private TextView _nameTextView;
    /**
     * 景点
     */
    private TextView _positionTextView;
    /**
     * 下载次数
     */
    private TextView _downloadTimesTextView;
    /**
     * 类型
     */
    private TextView _typeTextView;
    /**
     * 大小
     */
    private TextView _fileSizeTextView;

    /**
     * 维度
     */

    private TextView _metaTextView;

    /**
     * 时间
     */

    private TextView _timeTextView;

    /**
     * 介绍
     */

    private TextView _introductionTextView;
    /**
     * 页面全部的scrollview
     */
    private ScrollView _panoramaDetailScrollView;
    /**
     * 全景相关gridview
     */
    private ExpGridView _samePositionGridView;

    /**
     * 全景播放（图片）
     */
    private WebView _panoramaDetailPictureWebView;
    /**
     * 全景播放（视频）
     */
    private RelativeLayout _panoramaDetailVideoRelativeLayout;
    private CubeImageView _panoramaDetailVideoImageView;

    /**
     * 全景资源ID
     */
    private int panoramaId;
    /**
     * 全景相关请求页数
     */
    private int _page = 1;
    private static final int _limit = 4;

    private int index = 0;

    private int _itemGridHeight = 0;
    private int _iListCache = 0;

    private PanoramaListAdapter _panoramaListAdapter;

    private List<PanoramaListData.PanoramaList> _listPanorama;

    private in.srain.cube.image.ImageLoader imageLoader;
    /**
     * 下载按钮
     */
    private Button _downloadButton;

    private ProgressRelativeLayout _progressRelativeLayout;

    private ImageView _videoHintImageView;

    private TextView _relatedNoDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("PanoramaDetailActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama_detail);

        _context = this;

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        panoramaId = getIntent().getIntExtra("panoramaId", -1);

        initView();

    }

    @Override
    protected void initView() {

        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);
        _progressRelativeLayout.showProgress();

        if (!NetworkStatusManager.getInstance(this).isNetworkConnectedHasMsg( false)) {
            _progressRelativeLayout.showErrorText(getResources().getString(R.string.get_data_fail));
            return;
        }

        _listPanorama = new ArrayList<>();

        _relatedNoDataTextView = (TextView) findViewById(R.id.tv_panorama_related_nodaa);

        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(_context);
        imageLoader = ImageLoaderFactory.create(_context, handler);

        _backImageView = (ImageView) findViewById(R.id.iv_back);
        _backImageView.setOnClickListener(this);

        _downloadButton = (Button) findViewById(R.id.btn_panrama_detail_download);
        _downloadButton.setOnClickListener(this);

        _titleTextView = initTextView(R.id.tv_panorama_detail_title);
        _nameTextView = initTextView(R.id.tv_panorama_detail_name);
        _positionTextView = initTextView(R.id.tv_panorama_detail_position);
        _downloadTimesTextView = initTextView(R.id.tv_panorama_detail_download_times);
        _typeTextView = initTextView(R.id.tv_panorama_detail_type);
        _fileSizeTextView = initTextView(R.id.tv_panorama_detail_file_size);
        _metaTextView = initTextView(R.id.tv_panorama_detail_meta);
        _timeTextView = initTextView(R.id.tv_panorama_detail_time);
        _introductionTextView = initTextView(R.id.tv_panorama_detail_introduction);
        _videoHintImageView = (ImageView) findViewById(R.id.iv_panorama_detail_video_hint);

        _panoramaDetailPictureWebView = (WebView) findViewById(R.id.wv_panorama_detail_pic);
        _panoramaDetailVideoRelativeLayout = (RelativeLayout) findViewById(R.id.rl_panorama_detail_video);
        _panoramaDetailVideoImageView = (CubeImageView) findViewById(R.id.iv_panorama_detail_video);

        _panoramaDetailScrollView = (ScrollView) findViewById(R.id.sv_panorama_detail);
        _samePositionGridView = (ExpGridView) findViewById(R.id.gv_panorama_detail_same);
        _samePositionGridView.setHorizontalSpacing(30);

        _samePositionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(_context, PanoramaDetailActivity.class);
                intent.putExtra("panoramaId", _listPanorama.get(position).getId());
                _context.startActivity(intent);
                finish();

            }
        });

        _panoramaDetailScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        index++;
                        break;

                    default:
                        break;
                }
                if (event.getAction() == MotionEvent.ACTION_UP && index > 0) {
                    index = 0;
                    View view = ((ScrollView) v).getChildAt(0);
                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight() + _itemGridHeight * 2) {
                        //加载数据代码
                        if (_iListCache != _panoramaListAdapter.getCount()) {
                            _iListCache = _panoramaListAdapter.getCount();
                            _page++;
                            requestRelated();
                        }
                    }
                }
                return false;
            }
        });

        if (panoramaId != -1) {
            requestPanoramaDetailData(panoramaId);
//            requestRelated();
        }


    }

    private PanoramaDetailData.PanoramaDetail _panoramaDetail;

    private void requestPanoramaDetailData(final int panoramaId) {

        PanoramaRestClient.getInstance(_context).getPanoramaDetail(panoramaId, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

                PanoramaDetailData panoramaDetailData = (PanoramaDetailData) resp.getModel();

                _panoramaDetail = panoramaDetailData.getData();

                fillTextView(_titleTextView, _panoramaDetail.getName());
                fillTextView(_nameTextView, _panoramaDetail.getName());
                fillTextView(_positionTextView, _panoramaDetail.getPosition());
                fillTextView(_downloadTimesTextView, _panoramaDetail.getDownloads());
                String strPanoramaType = _panoramaDetail.getType();
                if (strPanoramaType.equals("0")) {
                    fillTextView(_typeTextView, _context.getResources().getString(R.string.panorama_alltype_video));

                    _panoramaDetailPictureWebView.setVisibility(View.GONE);
                    _panoramaDetailVideoRelativeLayout.setVisibility(View.VISIBLE);
//                    _panoramaDetailVideoImageView.loadImage(imageLoader, panoramaDetail.getThumbnail());
                    BitmapUtils bu = new BitmapUtils(_context);
                    bu.display(_panoramaDetailVideoImageView, _panoramaDetail.getThumbnail());
                    _panoramaDetailVideoImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (_panoramaDetail.getStoragepath().endsWith(".mp4")) {

                                DialogHelpUtils.getConfirmDialog(_context, getResources().getString(R.string.video_need_to_downloadhint), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {  //确定, 保存到sp中

                                        //继续下载
                                        handlerDown();

                                    }
                                }, null).show();

                            } else if (_panoramaDetail.getStoragepath().endsWith(".m3u8")) {
                                U3dMediaPlayerLogic.getInstance().startPlayPanorama(_context, _panoramaDetail, 1, false, VRHomeConfig.TYPE_PANORAMA);

                            }
                        }
                    });

                    if (_panoramaDetail.getStoragepath().endsWith(".mp4")) {
                        _videoHintImageView.setImageDrawable(_context.getResources().getDrawable(R.drawable.play));
                    } else if (_panoramaDetail.getStoragepath().endsWith(".m3u8")) {
                        _videoHintImageView.setImageDrawable(_context.getResources().getDrawable(R.drawable.play));
                    }

                } else if (strPanoramaType.equals("1")) {
                    fillTextView(_typeTextView, _context.getResources().getString(R.string.panorama_alltype_pic));

                    _panoramaDetailPictureWebView.setVisibility(View.GONE);
                    _panoramaDetailVideoRelativeLayout.setVisibility(View.VISIBLE);

                    BitmapUtils bu = new BitmapUtils(_context);
                    bu.display(_panoramaDetailVideoImageView, _panoramaDetail.getThumbnail());
//                    _videoHintImageView.setImageDrawable(_context.getResources().getDrawable(R.drawable.play));

                    _panoramaDetailVideoImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            U3dMediaPlayerLogic.getInstance().startPlayPanorama(_context, _panoramaDetail, 1, false, VRHomeConfig.TYPE_PANORAMA);


                        }
                    });


//                    //启用支持javascript
//                    WebSettings settings = _panoramaDetailPictureWebView.getSettings();
//                    settings.setJavaScriptEnabled(true);
//                    _panoramaDetailPictureWebView.setWebViewClient(new WebViewClientEmb());
//
//                    Map<String, String> m = new HashMap<>();
//                    m.put("pano", _panoramaDetail.getStoragepath());
//
//                    String strs = "http://beta.image.api.vrseen.net/360/?pano=" + _panoramaDetail.getStoragepath();
////                if(panoramaDetailData.getName().equals("雷峰塔"))
////                    _panoramaDetailPictureWebView.loadUrl("http://720yun.com/t/dcc2fmOOy4r?pano_id=521457");
////                else
//                    _panoramaDetailPictureWebView.loadUrl(
//                            "http://beta.image.api.vrseen.net/panoview?title=11&title2=xx&thumb=%27%27&filename="+_panoramaDetail.getStoragepath());
////                            "http://beta.image.api.vrseen.net/alidata1//media/thumb2/fa/ee/beixian7_S_P_2D.jpg");
////                                    + panoramaDetail.getStoragepath());
//
////                _panoramaDetailPictureWebView.loadUrl(strs);

                } else if (strPanoramaType.equals("2")) {
                    fillTextView(_typeTextView, getResources().getString(R.string.panorama_alltype_manyou));
                } else if (strPanoramaType.equals("3")) {
                    fillTextView(_typeTextView, getResources().getString(R.string.panorama_alltype_3d));
                }

                fillTextView(_fileSizeTextView, _panoramaDetail.getFilesize());
                fillTextView(_metaTextView, _panoramaDetail.getMeta());
                fillTextView(_timeTextView, _panoramaDetail.getShootingdate());
                fillTextView(_introductionTextView, _panoramaDetail.getDescription());

                _downloadInfo = DownloadUtils.getInstance().getDownloadVO(panoramaId);
                if (_downloadInfo != null) {
                    _state = _downloadInfo.getState();
                    showDownText();
                } else {
                    _downloadInfo = new DownloadInfo();
                    _downloadInfo.setResourceId(panoramaId);
                    _downloadInfo.setAutoRename(true);
                    _downloadInfo.setAutoResume(true);

                    if (_panoramaDetail.getType().equals("0")) {
                        _downloadInfo.setType(VRHomeConfig.VIDEO);
                    } else if (_panoramaDetail.getType().equals("1")) {
                        _downloadInfo.setType(VRHomeConfig.IMAGE);
                    }

                    //String url = appDetail.getApp_platforms().get(0).getMax_version_platform().getPath();
                    String url = _panoramaDetail.getStoragepath();
                    url = url.replace(" ", "%20");
                    _downloadInfo.setDownloadUrl(url);
                    String name = _panoramaDetail.getName();
                    name = name.replace(" ", "%20");
                    _downloadInfo.setFileName(name);
                    _downloadInfo.setThumbUrl(_panoramaDetail.getThumbnail());

                    String targetUrl = FileUtils.getFileName(_panoramaDetail.getName() + "." + _panoramaDetail.getFileformat().trim().toLowerCase());

                    if (_panoramaDetail.getType().equals("0")) {
                        targetUrl = VRHomeConfig.SAVE_PANO_VIDEO + targetUrl;
                    } else if (_panoramaDetail.getType().equals("1")) {
                        targetUrl = VRHomeConfig.SAVE_PANO_IMAGE + targetUrl;
                    }

                    _downloadInfo.setFileSavePath(targetUrl);
                    _downloadInfo.setViews(_panoramaDetail.getViews());
                }

                requestRelated();

            }


        });


    }


    public class WebViewClientEmb extends WebViewClient {

// 在WebView中而不是系统默认浏览器中显示页面

        @Override

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            return true;

        }

    }


    private void requestRelated() {

        PanoramaRestClient.getInstance(_context).getRelated(panoramaId, _page, _limit, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

                PanoramaListData _panoramaListData = (PanoramaListData) resp.getModel();

                List<PanoramaListData.PanoramaList> listPanorama = null;

                if (_panoramaListData != null && _panoramaListData.getData() != null && _panoramaListData.getData().size() >= 0) {
                    listPanorama = _panoramaListData.getData();
                }


                _listPanorama.addAll(listPanorama);

                if (_panoramaListAdapter == null) {
                    _panoramaListAdapter = new PanoramaListAdapter(_context, _listPanorama);
                    _samePositionGridView.setAdapter(_panoramaListAdapter);

                    View view = _panoramaListAdapter.getView(0, null, _samePositionGridView);
                    if (view != null) {
                        view.measure(0, 0);
                        _itemGridHeight = view.getMeasuredHeight();
                    }
                } else {
                    _panoramaListAdapter.addData(listPanorama);
                }

                if (listPanorama.size() <= 3) {
                    _relatedNoDataTextView.setVisibility(View.VISIBLE);
                } else {
                    _relatedNoDataTextView.setVisibility(View.GONE);
                }
                _progressRelativeLayout.showContent();

            }


        });


    }

    private TextView initTextView(int resourceId) {

        return (TextView) findViewById(resourceId);

    }

    private void fillTextView(TextView textView, String text) {
        textView.setText(text);
    }

    @Override
    public void onClick(View v) {

        int vid = v.getId();
        switch (vid) {
            case R.id.iv_back:

                finish();
                break;
            case R.id.btn_panrama_detail_download:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    boolean permissionCheck = selfPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    Log.e("ssssssssssssssssss",String.valueOf(permissionCheck));
                }
                if (_downloadButton.getText().toString().equals(_context.getResources().getString(R.string.download_open))) {
                    U3dMediaPlayerLogic.getInstance().startPlayPanorama(_context, _panoramaDetail, 1, false, VRHomeConfig.TYPE_PANORAMA);
                } else {
                    handlerDown();
                }
                break;
        }

    }

    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (_context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = _context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(_context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }

    /**
     * 下载状态
     */
    private HttpHandler.State _state = null;
    /**
     * 下载信息
     */
    private DownloadInfo _downloadInfo;

    private void handlerDown() {
        if (_state == null) {
            //下载
            try {
                DownloadUtils.getInstance().addNewDownload(_downloadInfo, new RequestCallBack<File>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.e("777777777777","onStart");
                    }

                    @Override
                    public void onCancelled() {
                        super.onCancelled();
                        Log.e("99999999999999","onCancelled");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        _state = HttpHandler.State.LOADING;
                        showDownText();
                        Log.e("88888888888888",_state.toString());
                    }

                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        _state = HttpHandler.State.SUCCESS;
                        showDownText();
                    }

                    public void onFailure(HttpException error, String msg) {
                        _state = HttpHandler.State.FAILURE;
                        showDownText();
                    }

                });
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else if (_state == HttpHandler.State.FAILURE || _state == HttpHandler.State.CANCELLED) {
            //重新下载
            try {
                DownloadUtils.getInstance().resumeDownload(_downloadInfo, new RequestCallBack<File>() {
                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        _state = HttpHandler.State.LOADING;
                        showDownText();
                    }

                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        _state = HttpHandler.State.SUCCESS;
                        showDownText();
                    }

                    public void onFailure(HttpException error, String msg) {
                        _state = HttpHandler.State.FAILURE;
                        showDownText();
                    }

                });
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else if (_state == HttpHandler.State.SUCCESS) {
            _downloadButton.setText(getString(R.string.download_open));
        } else if (_state == HttpHandler.State.LOADING) {
            try {
                DownloadUtils.getInstance().stopDownload(_downloadInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }

            _state = HttpHandler.State.CANCELLED;
            showDownText();
        }
    }

    private void showDownText() {
        if (_state == HttpHandler.State.SUCCESS) {
            _downloadButton.setText(getString(R.string.download_open));
        } else if (_state == HttpHandler.State.LOADING) {
            float current = (float) _downloadInfo.getProgress();
            float total = (float) _downloadInfo.getFileLength();
            float progressValue = (current / total) * 100;
            _downloadButton.setText((int) progressValue + "%");
        } else if (_state == HttpHandler.State.FAILURE) {
            _downloadButton.setText(getString(R.string.download_fauliar));
        } else if (_state == HttpHandler.State.CANCELLED) {
            _downloadButton.setText(getString(R.string.download_pause));
        }
    }

    public void onResume() {
        super.onResume();

        _downloadInfo = DownloadUtils.getInstance().getDownloadVO(panoramaId);
        if (_downloadInfo != null) {
            showDownText();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
