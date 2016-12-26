package com.vrseen.vrstore.activity.film;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.activity.user.ExChangeActivity;
import com.vrseen.vrstore.activity.user.LoginActivity;
import com.vrseen.vrstore.adapter.film.FilmAdapter;
import com.vrseen.vrstore.adapter.film.FilmEpisodeAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.logic.TeleplayLogic;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.logic.WaSuLogic;
import com.vrseen.vrstore.model.film.FilmAssetData;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.film.FilmRelateData;
import com.vrseen.vrstore.model.panorama.PanoramaCityData;
import com.vrseen.vrstore.model.panorama.PanoramaDetailData;
import com.vrseen.vrstore.model.user.UserInfo;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.DateUtils;
import com.vrseen.vrstore.util.DensityUtil;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.view.ExpGridView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;
import com.vrseen.vrstore.view.mediaplayer.LightnessController;
import com.vrseen.vrstore.view.mediaplayer.VRmediapalyer;
import com.vrseen.vrstore.view.mediaplayer.VolumnController;
import com.wasu.util.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import in.srain.cube.util.NetworkStatusManager;

/**
 * 影视详情
 * Created by mll on 2016/5/24.
 */
public class FilmDetailActivity extends BaseActivity implements VRmediapalyer.Callback {

    public static final int GET_WASU_RESOURCE_URL_AUTO = 0x111118;
    public static final int GET_WASU_RESOURCE_URL = 0x111123;
    public static final int CHANGE_ORIENTATION = 0x200001;         //更变手机方向
    public static final int UPDATE_TIME = 0x200002;         //更新视频播放时间
    public static final int AUTO_PLAY = 0x200003;         //开始播放
    public static final int HIDE_UI = 0x200004;          //隐藏
    public static final int INIT_TIME = 0x200005;
    public static final int CONTINUE_PLAY = 0x200006;         //继续播放
    public static final int READY_PLAY = 0X200007;         //准备播放
    public static final int RETURN_FILM_DETAIL = 0X200008;         //获取电影详情
    public static final int MEDIAPLAYER_UNKNOW_ERROR = 0X200009;            //播放器发生未知错误
    public static final int HIDE_MEDIA_ERROR = 0X200010;
    public static final int MEDIAPLAYER_LOADING = 0X200011;
    public static final int NO_NETWORK = 0X200012;

    public static final int DELAY_TIME = 3000;
    public static final int UPDATE_DELAY_TIME = 1000;
    public static String PLAY_PROGRESS_VALUE = "PlayProgressData";
    private int _fee = 0;
    private FilmDetailData.FilmAsset _filmAsset = new FilmDetailData.FilmAsset();
    private boolean _isPlaying;
    private boolean _bIsPrepared = false;
    private FilmDetailData.PlayProgressData _playProgressData = null; //播放记录
    private Context _context = null;
    private FilmDetailData _filmDetailData = null;
    private FilmAdapter _relateFilmAdapter = null;
    private GridView _relateFilmGridView = null;

    private ExpGridView _episodeGridView = null;
    private FilmEpisodeAdapter _episodeAdapter;

    private SurfaceView _surfaceView = null;
    private SeekBar _seekBar = null;
    private ProgressBar _progressBar = null;
    private VRmediapalyer _player = null;
    private AudioManager _audioManager;
    private VolumnController _volumnController;
    private VolumnController _lightController;
    private boolean _isFullScreen = false;
    private int _currentFilmId = 0;
    private TextView _mediaplayerErrorText;
    private TextView _currentTimeTextView;
    private ImageView _play2ImageView;
    private ImageView _playImageView;
    private ImageView _fullscreenImageView;
    //    private RelativeLayout _titleLayout;                       //头部布局
    private LinearLayout _vrLinearLayout;
    private LinearLayout _playbuttonLayout;
    private TextView _totalTimeTextView;
    private boolean _isAutoPlay = false;
    private RelativeLayout _playBottomLayout;      //播放底部控件
    private int _ithreshold;
    private float _fWidth;
    private float _fHeight;
    private ScrollView _scrollView;
    private RelativeLayout _playLayout;          //播放布局
    private ImageView _collectImageView;         //收藏按钮
    private ImageView _vipButton;
    private boolean _isCollected;
    private ProgressRelativeLayout _progressRelativeLayout;

    private RelativeLayout _rl_backView;
    private SensorManager _sensorManager;
    private ViewGroup.MarginLayoutParams _mp;
    private boolean _isLockNow;
    private LinearLayout _lockScreenLinearLayout;
    private ImageView _lockImageView;
    private TextView _lockTextView;
    private LinearLayout _nodata;

    private RelativeLayout _vipRelativeLayout;
    private int _orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private Sensor _sensor;

    private TextView _moreTextview;

    //打开对应影视的播放
    public static void actionStart(Context context, FilmDetailData.PlayProgressData data) {
        Intent intent = new Intent(context, FilmDetailActivity.class);
        intent.putExtra(PLAY_PROGRESS_VALUE, data);
        CommonUtils.startActivityWithAnim(context, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("FilmDetailActivity");
        super.onCreate(savedInstanceState);

        //保持常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.film_detail_layout);
        _context = this;
        _isFirst = true;
        _mp = new ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.film_no_full_screen));
        _sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _sensorManager.registerListener(listener, _sensor, SensorManager.SENSOR_DELAY_NORMAL);

        mState = NetworkStatusManager.State.UNKNOWN;
        mReceiver = new ConnectivityBroadcastReceiver();
        startListening(_context);
        initView();

    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            _lockScreenLinearLayout.setVisibility(View.GONE);
        }
    };


    private void hideLockLayout() {
        handler.postDelayed(runnable, 3000);
    }


    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float xValue = Math.abs(event.values[0]);
            float yValue = Math.abs(event.values[1]);
            float zValue = Math.abs(event.values[2]);
            if (xValue > 5 && xValue > yValue && zValue < 8) {

                if (_orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    return;
                }

                //切换为横屏
                if (!_isFullScreen) {
                    if (_lockScreenLinearLayout.getVisibility() != View.VISIBLE) {
                        _lockScreenLinearLayout.setVisibility(View.VISIBLE);
                        handler.removeCallbacks(runnable);
                    }

                    if (!_isLockNow) {
                        fullScreen();
                        _lockImageView.setVisibility(View.INVISIBLE);
                        _lockTextView.setText(getResources().getString(R.string.click_lock));
                    } else {
                        _lockImageView.setVisibility(View.VISIBLE);
                        _lockTextView.setText(getResources().getString(R.string.click_unlock));
                    }

                    hideLockLayout();
                }

            }

            if (yValue > 5 && yValue > xValue && zValue < 8) {

                if (_orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    return;
                }

                //切换为竖屏
                if (_isFullScreen) {
                    _lockScreenLinearLayout.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(runnable);

                    if (!_isLockNow) {
                        noFullScreen();
                        _lockImageView.setVisibility(View.INVISIBLE);
                        _lockTextView.setText(R.string.click_lock);
                    } else {
                        _lockImageView.setVisibility(View.VISIBLE);
                        _lockTextView.setText(R.string.click_unlock);
                    }

                    hideLockLayout();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void onNewIntent(Intent intent) {

        addFilmRecord(false);

        resetMediaPlayer(true);

        super.onNewIntent(intent);
        setIntent(intent);

        _isFirst = true;
        getFilmDetail(true);
    }

    protected void initView() {

        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);

        _moreTextview = (TextView) findViewById(R.id.textview_more_drama);
        _vipButton = (ImageView) findViewById(R.id.imageview_becomeVip);
        _vipRelativeLayout = (RelativeLayout) findViewById(R.id.rl_vip);

        _collectImageView = (ImageView) findViewById(R.id.play_collect);
        _playLayout = (RelativeLayout) findViewById(R.id.rl_play);
        _scrollView = (ScrollView) findViewById(R.id.film_scrollview);
//        _titleLayout = (RelativeLayout) findViewById(R.id.title_bar);
        _fullscreenImageView = (ImageView) findViewById(R.id.play_fullscreen);
        _play2ImageView = (ImageView) findViewById(R.id.play_button);
        _playImageView = (ImageView) findViewById(R.id.film_play);
        _relateFilmGridView = (GridView) findViewById(R.id.relate_film_gridview);
        _episodeGridView = (ExpGridView) findViewById(R.id.gridview_drama);
        _linearLayout = (LinearLayout) findViewById(R.id.film_drama_layout);
        _mediaplayerErrorText = (TextView) findViewById(R.id.mediaplayer_error_text);
        _currentTimeTextView = (TextView) findViewById(R.id.current_time);
        _vrLinearLayout = (LinearLayout) findViewById(R.id.vr_layout);
        _playbuttonLayout = (LinearLayout) findViewById(R.id.film_play_layout);
        _totalTimeTextView = (TextView) findViewById(R.id.total_time);
        _surfaceView = (SurfaceView) findViewById(R.id.film_surfaceview);
        _playBottomLayout = (RelativeLayout) findViewById(R.id.play_bottom_layout);
        _playBottomLayout.setOnClickListener(this);
        _seekBar = (SeekBar) findViewById(R.id.film_seekbar);
        _seekBar.setOnClickListener(this);
        _progressBar = (ProgressBar) findViewById(R.id.film_progressBar);

        _rl_backView = (RelativeLayout) findViewById(R.id.view_back);
        _rl_backView.setOnClickListener(this);
        findViewById(R.id.textview_more_introduce).setOnClickListener(this);
        findViewById(R.id.textview_more_drama).setOnClickListener(this);
        _playImageView.setOnClickListener(this);
        _fullscreenImageView.setOnClickListener(this);
        _surfaceView.setOnClickListener(this);
        _vrLinearLayout.setOnClickListener(this);
        _collectImageView.setOnClickListener(this);
        _play2ImageView.setOnClickListener(this);
        _vipButton.setOnClickListener(this);
        _lockScreenLinearLayout = (LinearLayout) findViewById(R.id.ll_lock_screen);
        _lockScreenLinearLayout.setVisibility(View.GONE);
        _lockScreenLinearLayout.setOnClickListener(this);
        _lockImageView = (ImageView) findViewById(R.id.iv_lock);
        _lockTextView = (TextView) findViewById(R.id.tv_lock);
        _nodata = (LinearLayout) findViewById(R.id.nodata);


        //hideLockLayout();

        initMediaplayer();
        getFilmDetail(true);
    }

    private void initMediaplayer() {
        //初始化播放器
        _player = new VRmediapalyer(this, _surfaceView, _seekBar, _progressBar, handler);
        _player.setOnPlayerStartListener(this);
        //  初始化audioManager
        _audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //初始化声音控制
        _volumnController = new VolumnController(this, R.drawable.ling, getString(R.string.film_detail_ling));
        _lightController = new VolumnController(this, R.drawable.sunny, getString(R.string.film_detail_light));
        //获取屏幕宽高
        _fWidth = DensityUtil.getWidthInPx(this);
        _fHeight = DensityUtil.getHeightInPx(this);
        _ithreshold = DensityUtil.dip2px(this, 18);

    }

    private int _episode=1;

    private void getFilmDetail(boolean isAutoPlay) {
        _isAutoPlay = isAutoPlay;
        Intent intent = getIntent();

        _playProgressData = (FilmDetailData.PlayProgressData) intent.getSerializableExtra(PLAY_PROGRESS_VALUE);

        int id = _playProgressData.getId();
        _episode = _playProgressData.getEpisodeID();
        if (id > 0) {
            //获取影视详情
            CommonRestClient.getInstance(_context).getFilmDetail(id, new AbstractRestClient.ResponseCallBack() {
                @Override
                public void onFailure(Response resp, Throwable e) {
                    CommonUtils.showResponseMessage(_context, resp, e, R.string.film_detail_error);
                }

                @Override
                public void onSuccess(Response resp) {
                    _filmDetailData = (FilmDetailData) resp.getModel();


                    if (_filmDetailData == null || _filmDetailData.getData() == null) {
                        return;
                    }

                    if (_filmDetailData.getData().getFee() == 1) {
                        UserInfo userInfo = UserLogic.getInstance().getUserInfo();
                        if (userInfo != null) {   //登录状态
                            if (userInfo.getIs_vip() == 1) {
                                _vipRelativeLayout.setVisibility(View.GONE);
                            } else {
                                _vipRelativeLayout.setVisibility(View.VISIBLE);
                                _isAutoPlay = false;
                                _play2ImageView.setClickable(false);
                                _fullscreenImageView.setClickable(false);

                                if (_sensorManager != null) {
                                    _sensorManager.unregisterListener(listener);
                                }
                            }
                        } else { //未登录，播放的处理和登录状态一样
                            _vipRelativeLayout.setVisibility(View.VISIBLE);
                            _isAutoPlay = false;
                            _play2ImageView.setClickable(false);
                            _fullscreenImageView.setClickable(false);

                            if (_sensorManager != null) {
                                _sensorManager.unregisterListener(listener);
                            }
                        }

                    } else { //非付费电影
                        _vipRelativeLayout.setVisibility(View.GONE);
                        _isAutoPlay = true;
                        _play2ImageView.setClickable(true);
                        _fullscreenImageView.setClickable(true);
                        _sensorManager.registerListener(listener, _sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    }

                    //获取相关
                    getRelateFilm();
                    //设置模式
                    if (_filmDetailData.getData().getScreen().equals(getResources().getString(R.string.left_and_right))) {
                        _filmDetailData.getData().setScreen("LR");
                    } else if (_filmDetailData.getData().getScreen().equals(getResources().getString(R.string.top_and_down))) {
                        _filmDetailData.getData().setScreen("TB");
                    } else if (TextUtils.isEmpty(_filmDetailData.getData().getScreen())) {
                        _filmDetailData.getData().setScreen("2D");
                    }


//                    ((TextView) findViewById(R.id.textview_name)).setText(_filmDetailData.getData().getTitle());
                    ((TextView) findViewById(R.id.play_count)).setText(_filmDetailData.getData().getPlays() + getResources().getString(R.string.app_count));
                    ((TextView) findViewById(R.id.film_name)).setText(_filmDetailData.getData().getTitle());
                    //主演
                    if (StringUtils.isNotBlank(_filmDetailData.getData().getActors())) {
                        (findViewById(R.id.ll_actor)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.play_actor)).setText(_filmDetailData.getData().getActors());
                    } else {
                        (findViewById(R.id.ll_actor)).setVisibility(View.GONE);
                    }

                    //简介
                    if (StringUtils.isNotBlank(_filmDetailData.getData().getPlot())) {
                        (findViewById(R.id.introduction_layout)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.play_introduction)).setText(_filmDetailData.getData().getPlot());
                    } else {
                        (findViewById(R.id.introduction_layout)).setVisibility(View.GONE);
                    }

                    //更新到多少集
                    if (StringUtils.isNotBlank(_filmDetailData.getData().getEpisode())) {
                        (findViewById(R.id.ll_update)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.play_total_count)).setText(_filmDetailData.getData().getEpisode());
                    } else {
                        (findViewById(R.id.ll_update)).setVisibility(View.GONE);
                    }

                    if (_filmDetailData.getData().getIs_collected() == 0) {
                        _collectImageView.setImageResource(R.drawable.shoucang);
                        _isCollected = false;
                    } else {
                        _collectImageView.setImageResource(R.drawable.shoucang_1);
                        _isCollected = true;
                    }
                    showMoreDrama();
                    _isFirst = false;
                    getChannes(_filmDetailData.getData().getRefer_id());

                    //_progressRelativeLayout.showContent();
                }
            });
        }
    }

    //获取相关的影视
    private void getRelateFilm() {
        CommonRestClient.getInstance(_context).getRelateFilm(_filmDetailData.getData().getId(), new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e, R.string.film_relate_error);
            }

            @Override
            public void onSuccess(Response resp) {
                FilmRelateData filmRalateData = (FilmRelateData) resp.getModel();
                if (filmRalateData.getData() == null || filmRalateData.getData().size() <= 0) {
                    _relateFilmGridView.setVisibility(View.GONE);
                    _nodata.setVisibility(View.VISIBLE);
                    return;
                }
                if (_relateFilmAdapter == null) {
                    _relateFilmAdapter = new FilmAdapter(_context, filmRalateData.getData());
                    _relateFilmGridView.setAdapter(_relateFilmAdapter);
                } else {
                    _relateFilmAdapter.refreshData(filmRalateData.getData());
                }
            }
        });
    }

    private void resetMediaPlayer(boolean isFreshBtn) {

        _player.reset();

        if(isFreshBtn)
            _moreTextview.setText(getString(R.string.film_detail_close));

        _currentTimeTextView.setText("00:00:00" + "\40");
        _orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        _isLockNow = false;
    }

    private void addFilmRecord(boolean isReleaseMp) {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            if (isReleaseMp)
                handler = null;
        }

        if (_player.mediaPlayer != null) {

            if (_player.isPrepared() && _player.mediaPlayer.getCurrentPosition() > 0)//!_player.mIsOccurError &&
            {
                if (UserLogic.getInstance().checkLoginSuc()) {
                    //添加播放记录
                    UserRestClient.getInstance(FilmDetailActivity.this).addPlayRecordForFilm(_playProgressData.getId(),
                            _player.mediaPlayer.getCurrentPosition(),
                            _filmDetailData.getData().getEpisodes().size() >= 0 ? _index + 1 : 0, 0);
                }
            }

            if (isReleaseMp)
                _player.stop();
        }
    }

    @Override
    protected void onDestroy() {

        addFilmRecord(true);

        if (_sensorManager != null) {
            _sensorManager.unregisterListener(listener);
        }
        stopListening();
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (_isFullScreen) {

                noFullScreen();

            } else {
                _bIsBack = true;

                Intent intent = new Intent();
                intent.putExtra("PLAYTO", _player.mediaPlayer.getCurrentPosition());
                setResult(RESULT_OK, intent);

                this.finish();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void isLoadComplete(boolean flag) {
        if (_bIsBack) return;
        if (flag) {
            _bIsPrepared = true;
            if (handler != null) {
                handler.sendEmptyMessage(INIT_TIME);
            }
        }
    }

    @Override
    public void isPlaying(boolean flag) {
        if (flag) {
            _playImageView.setImageResource(R.drawable.zhanting1);
            _play2ImageView.setImageResource(R.drawable.zhanting1);
        } else {
            _playImageView.setImageResource(R.drawable.baofang);
            _play2ImageView.setImageResource(R.drawable.baofang);
        }
        _isPlaying = flag;
    }


    private int _index = 0;

    @Override
    public void isPlayComplete(boolean flag) {
        if (!flag || _bIsBack) return;
        ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.film_mediaplayer_bottom));
        List<FilmDetailData.EpisodesData> episodesDataList = _filmDetailData.getData().getEpisodes();
        if (_index < episodesDataList.size() - 1) {
            _index++;
            _episodeAdapter.refreshData(_index);
            ToastUtils.showShort(getApplicationContext(), R.string.film_continue_play);
            getChannes(episodesDataList.get(_index).getId());
            _isAutoPlay = true;
        } else {
            _index = 0;
            //如果在全屏模式下，剧集是最后一集，跳出全屏
            if (_isFullScreen) {
                _isFullScreen = false;
                _fullscreenImageView.setImageResource(R.drawable.fullscreen);
                _surfaceView.setOnTouchListener(null);
//                _titleLayout.setVisibility(View.VISIBLE);
                _rl_backView.setVisibility(View.VISIBLE);
                _scrollView.setVisibility(View.VISIBLE);
                mp.setMargins(0, (int) getResources().getDimension(R.dimen.film_full_screen2), 0, 0);
                RelativeLayout.LayoutParams _bottomParam = new RelativeLayout.LayoutParams(mp);
                _playBottomLayout.setLayoutParams(_bottomParam);
                _playBottomLayout.setVisibility(View.VISIBLE);
                _playLayout.setLayoutParams(new RelativeLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                        (int) getResources().getDimension(R.dimen.film_full_screen2)));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                _playbuttonLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 显示或隐藏
     */
    private void showorhide() {
        if (_vrLinearLayout.getVisibility() == View.VISIBLE) {
            _vrLinearLayout.setVisibility(View.GONE);
            _playbuttonLayout.setVisibility(View.GONE);
            if (_isFullScreen) {
                _playBottomLayout.setVisibility(View.GONE);
            }

        } else {
            _vrLinearLayout.setVisibility(View.VISIBLE);
            _playbuttonLayout.setVisibility(View.VISIBLE);
            if (_isFullScreen) {
                _playBottomLayout.setVisibility(View.VISIBLE);
            }
            if (handler != null) {
                handler.removeMessages(HIDE_UI);
                handler.sendEmptyMessageDelayed(HIDE_UI, DELAY_TIME);
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_lock_screen:
                if (_lockImageView.getVisibility() == View.VISIBLE) {
                    _lockImageView.setVisibility(View.INVISIBLE);
                    _lockTextView.setText(R.string.click_lock);
                    //解锁状态,屏幕可以旋转
                    _isLockNow = false;
                } else {
                    _lockImageView.setVisibility(View.VISIBLE);
                    _lockTextView.setText(R.string.click_unlock);
                    //锁定状态，屏幕不能旋转
                    _isLockNow = true;
                }
                handler.removeCallbacks(runnable);
                hideLockLayout();
                break;

            case R.id.view_back:
                _bIsBack = true;

                Intent intent = new Intent();
                intent.putExtra("PLAYTO", _player.mediaPlayer.getCurrentPosition());
                setResult(RESULT_OK, intent);

                finish();
                break;

            case R.id.textview_more_introduce:
                //展开详细内容简介
                TextView textView = (TextView) findViewById(R.id.play_introduction);
                TextView textView1 = (TextView) findViewById(R.id.textview_more_introduce);
                if (textView.getMaxLines() == 1) {
                    textView.setMaxLines(20);
                    textView1.setText(getString(R.string.film_detail_close));
                } else {
                    textView.setMaxLines(1);
                    textView1.setText(getString(R.string.film_detail_open));
                }
                break;
            case R.id.textview_more_drama:
                showMoreDrama();
                break;

            case R.id.film_surfaceview:
                showorhide();
                break;

            case R.id.film_seekbar:
                _playBottomLayout.setVisibility(View.VISIBLE);
                if (handler != null) {
                    handler.removeMessages(HIDE_UI);
                    handler.sendEmptyMessageDelayed(HIDE_UI, DELAY_TIME);
                }
                break;

            case R.id.play_bottom_layout:
                _playBottomLayout.setVisibility(View.VISIBLE);
                if (handler != null) {
                    handler.removeMessages(HIDE_UI);
                    handler.sendEmptyMessageDelayed(HIDE_UI, DELAY_TIME);
                }
                break;

            case R.id.film_play:
                _playBottomLayout.setVisibility(View.VISIBLE);
                if (handler != null) {
                    handler.removeMessages(HIDE_UI);
                    handler.sendEmptyMessageDelayed(HIDE_UI, DELAY_TIME);
                }

                if (isVipPlay() == false) {
                    return;
                }

                if (!_bIsPrepared) {
                    handler.sendEmptyMessageDelayed(MEDIAPLAYER_LOADING, DELAY_TIME);
                    //zx_note  此处高级处理方法为添加待自动播放消息，加载完成后自动播放
                    return;
                }
                if (handler.hasMessages(UPDATE_TIME))
                    handler.removeMessages(UPDATE_TIME);

                if (_isPlaying) {
                    pausePlay();
                } else {
                    resumePlay();
                }

                break;

            case R.id.play_button:
                _playBottomLayout.setVisibility(View.VISIBLE);
                if (handler != null) {
                    handler.removeMessages(HIDE_UI);
                    handler.sendEmptyMessageDelayed(HIDE_UI, DELAY_TIME);
                }

                if (isVipPlay() == false) {
                    return;
                }
                if (_player.mIsOccurError) {
                    //Toast.makeText(getApplicationContext(),R.string.film_error,Toast.LENGTH_SHORT).show();
                    //zx_add 添加用户发聩窗口，把问题片源做个追踪
                    //.......
                    return;
                }
                if (!_bIsPrepared) {
                    handler.sendEmptyMessageDelayed(MEDIAPLAYER_LOADING, DELAY_TIME);
                    //Toast.makeText(getApplicationContext(),R.string.loading,Toast.LENGTH_SHORT).show();
                    //zx_note  此处高级处理方法为添加待自动播放消息，加载完成后自动播放
                    return;
                }

                _progressBar.setVisibility(View.GONE);

                if (handler.hasMessages(UPDATE_TIME))
                    handler.removeMessages(UPDATE_TIME);

                if (_isPlaying) {
                    pausePlay();
                } else {
                    resumePlay();
                }

                break;

            case R.id.play_fullscreen:
                //全屏监听

                if (!_isFullScreen) {
                    _isLockNow = true;
                    _fullscreenImageView.setImageResource(R.drawable.fullscreen_1);
                    fullScreen();

                } else {

                    // _isLockNow = false;
                    noFullScreen();
                }
                break;

            case R.id.play_collect:
                _playBottomLayout.setVisibility(View.VISIBLE);
                if (handler != null) {
                    handler.removeMessages(HIDE_UI);
                    handler.sendEmptyMessageDelayed(HIDE_UI, DELAY_TIME);
                }

                //判断是否登录
                if (UserLogic.getInstance().checkLoginSucAndHasMsg(getApplicationContext())) {
                    //TODO 收藏
                    _currentFilmId = _playProgressData.getId();
                    if (!_isCollected) {
                        UserRestClient.getInstance(FilmDetailActivity.this).addCollectFilm(String.valueOf(_currentFilmId), new AbstractRestClient.ResponseCallBack() {
                            @Override
                            public void onFailure(Response resp, Throwable e) {
                                CommonUtils.showResponseMessage(FilmDetailActivity.this, resp, e, R.string.film_collect_fail);
                            }

                            @Override
                            public void onSuccess(Response resp) {
                                String videojson = resp.toString();
                                Log.e("Log", videojson);
                                _collectImageView.setImageResource(R.drawable.shoucang_1);
                                _isCollected = true;
                                ToastUtils.showShort(_context, R.string.film_collect_suc);
                            }
                        });

                    } else {

                        UserRestClient.getInstance(FilmDetailActivity.this).cancelCollectFilm(String.valueOf(_currentFilmId), new AbstractRestClient.ResponseCallBack() {
                            @Override
                            public void onFailure(Response resp, Throwable e) {
                                CommonUtils.showResponseMessage(FilmDetailActivity.this, resp, e, R.string.film_cancel_collect_fail);
                            }

                            @Override
                            public void onSuccess(Response resp) {
                                _collectImageView.setImageResource(R.drawable.shoucang);
                                _isCollected = false;
                                ToastUtils.showShort(_context, R.string.film_cancel_collect_suc);
                            }
                        });
                    }
                } else {
                    _collectImageView.setClickable(false);
                    return;
                }
                break;

            case R.id.vr_layout:

                ///播放vr
                if (_strFilmURL != null) {
                    String dimensionType;
                    if (!StringUtils.isBlank(_filmDetailData.getData().getPanorama())) {
                        //播放全景
                        PanoramaDetailData.PanoramaDetail vo = new PanoramaDetailData.PanoramaDetail();
                        vo.setId(_filmDetailData.getData().getId());
                        vo.setStoragepath(_strFilmURL);
                        U3dMediaPlayerLogic.getInstance().startPlayPanorama(_context, vo, VRHomeConfig.TYPE_VR, VRHomeConfig.VR_CINEMA_ID);
                        this.finish();
                        return;
                    } else {
                        int position = 0;
                        if (_player != null && _player.mediaPlayer != null && _player.isPrepared())
                            position = _player.mediaPlayer.getCurrentPosition();
                        TeleplayLogic.getInstance().set_filmDetail(_filmDetailData, this);
                        if (_filmDetailData.getData().getScreen().equals("2D")) {

                            dimensionType = VRHomeConfig.TYPE_2D;

                            U3dMediaPlayerLogic.getInstance().startPlayFilm(this, _strFilmURL,
                                    dimensionType, _filmDetailData.getData().getTitle(), position, _filmDetailData.getData().getId(), _selectEpisodeIndex);
                        } else {

                            dimensionType = VRHomeConfig.TYPE_3D;

                            U3dMediaPlayerLogic.getInstance().startPlayFilm(this, _strFilmURL, dimensionType,
                                    _filmDetailData.getData().getScreen(), _filmDetailData.getData().getTitle()
                                    , position, _filmDetailData.getData().getId(), _selectEpisodeIndex);
                        }

                    }

                } else {

                    ToastUtils.showShort(_context, R.string.film_detail_error);
                    return;
                }

                break;
            case R.id.imageview_becomeVip:
                toGetVip();
                break;
            default:
                break;
        }
    }

    private void pausePlay() {
        _isPlaying = false;
        _playImageView.setImageResource(R.drawable.baofang);
        _play2ImageView.setImageResource(R.drawable.baofang);
        _player.pause();
    }

    private void resumePlay() {

        _isPlaying = true;

        if (_player.isPrepared() && !_player.mIsOccurError)
            _player.play();
        else
            _player.playUrl(true, _strFilmURL, false);

        _playImageView.setImageResource(R.drawable.zhanting1);
        _play2ImageView.setImageResource(R.drawable.zhanting1);
    }

    private LinearLayout _linearLayout;
    private int _selectEpisodeIndex = 0;
    private boolean _isFirst = true;

    Comparator comparator = new Comparator<FilmDetailData.EpisodesData>() {
        @Override
        public int compare(FilmDetailData.EpisodesData lhs, FilmDetailData.EpisodesData rhs) {
            String a = lhs.getEpisode();
            String b = rhs.getEpisode();
            int flag = b.compareTo(a);
            return flag;
        }
    };

    //显示更多的剧集
    private void showMoreDrama() {
        _selectEpisodeIndex = _episode - 1;

        if (_filmDetailData == null || _filmDetailData.getData() == null)
            return;

        if (_filmDetailData.getData().getEpisodes().size() > 0) {
            _linearLayout.setVisibility(View.VISIBLE);
            //剧情更新到第几集
            List<FilmDetailData.EpisodesData> list1 = _filmDetailData.getData().getEpisodes();

            String main=_filmDetailData.getData().getGenres().getMain();
            if(main.equals(getString(R.string.type_video_dianshiju))){
                Collections.sort(list1, comparator);
            }


            int length = list1.size();
            if (length > 5) {
                length = 5;
            }
            List<FilmDetailData.EpisodesData> list = new ArrayList<>();


            if (_isFirst == true) {
                if (_moreTextview.getText().toString().equals(getString(R.string.film_detail_close))) {
                    _moreTextview.setText(getString(R.string.film_detail_open));
                    for (int i = 0; i < length; i++) {
                        list.add(list1.get(i));
                    }
                }
            } else {
                if (_moreTextview.getText().toString().equals(getString(R.string.film_detail_close))) {
                    _moreTextview.setText(getString(R.string.film_detail_open));
                    for (int i = 0; i < length; i++) {
                        list.add(list1.get(i));
                    }
                } else {
                    _moreTextview.setText(getString(R.string.film_detail_close));
                    list = list1;
                }
            }

            if (_episodeAdapter == null) {
                _episodeAdapter = new FilmEpisodeAdapter(_context, list, _selectEpisodeIndex);
                _episodeGridView.setAdapter(_episodeAdapter);
            } else {
                _episodeAdapter.refreshData(list, _selectEpisodeIndex);
            }

            _episodeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //获取集数
                    for (int i = 0; i < parent.getCount(); i++) {
                        View view1 = parent.getChildAt(i);
                        view1.setBackgroundResource(R.drawable.btn_radius_white_style);
                        ((TextView)((RelativeLayout)view1).getChildAt(0)).setTextColor(getResources().getColor(R.color.contentColor));
                    }
                    view.setBackgroundResource(R.drawable.btn_radius_blue_style);
                    ((TextView)((RelativeLayout)view).getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                    if (_selectEpisodeIndex != position) {
                        _selectEpisodeIndex = position;
                        _index = _selectEpisodeIndex;
                        _episode = position + 1;
                        _isAutoPlay = true;

                        //zx_note 刷新播放记录
//                            Intent intent = new Intent();
//                            intent.putExtra("PLAYTO", _player.mediaPlayer.getCurrentPosition());
//                            setResult(RESULT_OK, intent);

                        //添加播放记录
                        addFilmRecord(false);

                        resetMediaPlayer(false);
                        //获取剧集
                        getChannes(_filmDetailData.getData().getEpisodes().get(position).getId());

                    }
                }
            });
        } else {
            findViewById(R.id.film_drama_layout).setVisibility(View.INVISIBLE);
        }
    }

    private boolean _bIsBack = false;//是否按back键
    private long _lFilmTime;
    private String _strFilmURL;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            if (_bIsBack) return;
            switch (msg.what) {

                case NO_NETWORK:
                    _mediaplayerErrorText.setVisibility(View.VISIBLE);
                    _mediaplayerErrorText.setText(R.string.get_network_res_error);
                    //zx_note
                    //...添加《点击重试》按钮显示，点击后playurl,暂时关闭

                    break;
                case MEDIAPLAYER_LOADING:
                    _mediaplayerErrorText.setVisibility(View.VISIBLE);
                    _mediaplayerErrorText.setText(R.string.loading);
                    handler.sendEmptyMessage(HIDE_UI);
                    handler.sendEmptyMessageDelayed(HIDE_MEDIA_ERROR, DELAY_TIME);
                    break;
                case MEDIAPLAYER_UNKNOW_ERROR:
                    _mediaplayerErrorText.setVisibility(View.VISIBLE);
                    _mediaplayerErrorText.setText(R.string.play_format_error);
                    handler.sendEmptyMessage(HIDE_UI);
                    handler.sendEmptyMessageDelayed(HIDE_MEDIA_ERROR, DELAY_TIME);
                    break;
                case HIDE_MEDIA_ERROR:
                    _mediaplayerErrorText.setVisibility(View.GONE);
                    break;

                case CHANGE_ORIENTATION:
                    break;
                case UPDATE_TIME:
                    if (_player.mIsOccurError || _player.mediaPlayer == null || handler == null)
                        return;

                    if (!NetUtils.isNetConnected(_context)) {
                        _player.pause();
                        handler.removeMessages(UPDATE_TIME);
                        return;
                    }
                    _player.updateSeekBar();
                    if (_player.mediaPlayer.getCurrentPosition() >= 0) {
                        _currentTimeTextView.setText
                                (DateUtils.getTimeOfHHMMSS(_player.mediaPlayer.getCurrentPosition()) + "\40");

                        handler.sendEmptyMessageDelayed(UPDATE_TIME, UPDATE_DELAY_TIME);
                    }

                    break;

                case INIT_TIME:
                    _mediaplayerErrorText.setVisibility(View.GONE);
                    _vrLinearLayout.setVisibility(View.VISIBLE);
                    _playbuttonLayout.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(HIDE_UI, DELAY_TIME);

                    if (_player.mIsOccurError || _player.mediaPlayer == null) return;

                    _lFilmTime = _player.mediaPlayer.getDuration();

                    _currentTimeTextView.setText
                            (DateUtils.getTimeOfHHMMSS(_player.mediaPlayer.getCurrentPosition()) + "\40");

                    _totalTimeTextView.setText("/" + "\40" + DateUtils.getTimeOfHHMMSS(_lFilmTime));

                    //播放记录续播  zx_note，后续改成在prepared时，seekto，现在playurl少个参数
                    int value = _playProgressData.getProValue();
                    if (value > 0) {
                        _player.mediaPlayer.seekTo(value);
                        _progressBar.setProgress(value / _player.mediaPlayer.getDuration());
                        _playProgressData.setProValue(0);
                    }

                    break;

                case READY_PLAY:
                    toAutoPlayFilm(false);
                    break;
                case AUTO_PLAY:

                    if (NetworkStatusManager.checkIsWifi(_context)) { //wifi环境

                        _mediaplayerErrorText.setVisibility(View.GONE);

                        toAutoPlayFilm(true);

                    } else { //非wifi,
                        handleNoWifiState(false);
                    }

                    break;
                case GET_WASU_RESOURCE_URL_AUTO:
                    try {
                        if (msg.obj != null && !(String.valueOf(msg.obj)).equals("3")) {

                            _strFilmURL = String.valueOf(msg.obj);
                            _player.playUrl(true, _strFilmURL, true);
                            _playbuttonLayout.setVisibility(View.GONE);
                            _playImageView.setImageResource(R.drawable.zhanting1);
                            _play2ImageView.setImageResource(R.drawable.zhanting1);
                        } else {
                            handler.sendEmptyMessage(MEDIAPLAYER_UNKNOW_ERROR);
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case GET_WASU_RESOURCE_URL:
                    try {
                        if (msg.obj != null && !(String.valueOf(msg.obj)).equals("3")) {
                            _strFilmURL = String.valueOf(msg.obj);
                            _player.playUrl(false, String.valueOf(msg.obj), true);
                        } else {
                            handler.sendEmptyMessage(MEDIAPLAYER_UNKNOW_ERROR);

                        }
                        _playbuttonLayout.setVisibility(View.GONE);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }

                    break;

                case HIDE_UI:
                    _vrLinearLayout.setVisibility(View.GONE);
                    _playbuttonLayout.setVisibility(View.GONE);
                    if (_isFullScreen) {

                        _playBottomLayout.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    };

    private void toAutoPlayFilm(boolean isAutoPlay) {

        if (isVipPlay() == false) {
            return;
        }
        WaSuLogic.getVideoUrl(isAutoPlay, _filmAsset.getCatId(),
                _filmAsset.getAssetId(), _filmAsset.getEpisode(), handler);
    }

    private boolean isVipPlay() {
        if (_fee > 0) {
            if (UserLogic.getInstance().isVIp()) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    private void toGetVip() {
        FilmDetailActivity.this.finish();
        if (UserLogic.getInstance().checkLoginSuc()) {
            ExChangeActivity.actionStart(this);
        } else {
            LoginActivity.actionStart(this);
        }
    }

    private void getChannes(int episodeId) {
        _currentFilmId = episodeId;
        CommonRestClient.getInstance(this).getFilmChannelsData(episodeId, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {

            }

            @Override
            public void onSuccess(Response resp) {
                FilmAssetData filmAssetsArr = (FilmAssetData) resp.getModel();

                if (filmAssetsArr.getData().size() > 0) {

                    FilmDetailData.FilmChannelAsset filmChannelAsset = filmAssetsArr.getData().get(0);
                    String result = filmChannelAsset.getResource_id();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        _filmAsset.setAssetId(jsonObject.getString("assetId"));
                        _filmAsset.setCatId(jsonObject.getString("catId"));
                        _filmAsset.setEpisode(jsonObject.getInt("episode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (handler != null) {
                    if (_isAutoPlay)
                        handler.sendEmptyMessage(AUTO_PLAY);
                    else
                        handler.sendEmptyMessage(READY_PLAY);
                }
            }
        });
    }

    private String formatTime(long time) {

        SimpleDateFormat formatter = new SimpleDateFormat(" HH:mm:ss ");
        return formatter.format(new Date(time));
    }

    /**
     * 全屏
     */
    private void fullScreen() {
        _isFullScreen = true;
        if (_playBottomLayout.getVisibility() == View.VISIBLE) {
            _playBottomLayout.setVisibility(View.GONE);
        }
        _surfaceView.setOnTouchListener(OnScreenOnTouchListener);
//                    _titleLayout.setVisibility(View.GONE);
        _rl_backView.setVisibility(View.GONE);
        _scrollView.setVisibility(View.GONE);
        _fWidth = DensityUtil.getWidthInPx(this);
        _fHeight = DensityUtil.getHeightInPx(this);

        int dpH11 = DensityUtil.getHeightInDp(this);
        int dpW = DensityUtil.getWidthInDp(this);

        int height = dpH11 - (int) getResources().getDimension(R.dimen.film_mediaplayer_bottom);
        _mp.setMargins(0, height, 0, 0);

        _playLayout.setLayoutParams(new RelativeLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT));


//                         RelativeLayout.LayoutParams _bottomParam = new RelativeLayout.LayoutParams(
//                                 ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                         _bottomParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
//

        RelativeLayout.LayoutParams _bottomParam = (RelativeLayout.LayoutParams) _playBottomLayout.getLayoutParams();

        _bottomParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        _playBottomLayout.setLayoutParams(_bottomParam);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        _orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    /**
     * 非全屏
     */
    private void noFullScreen() {

        ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.film_mediaplayer_bottom));

        _isFullScreen = false;

        _fullscreenImageView.setImageResource(R.drawable.fullscreen);

        _surfaceView.setOnTouchListener(null);

//                _titleLayout.setVisibility(View.VISIBLE);
        _rl_backView.setVisibility(View.VISIBLE);
        _scrollView.setVisibility(View.VISIBLE);

        mp.setMargins(0, (int) getResources().getDimension(R.dimen.film_full_screen2), 0, 0);

        RelativeLayout.LayoutParams _bottomParam = new RelativeLayout.LayoutParams(mp);

        _playBottomLayout.setLayoutParams(_bottomParam);
        _playBottomLayout.setVisibility(View.VISIBLE);

        _playLayout.setLayoutParams(new RelativeLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.film_full_screen2)));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        _orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    /**
     * 播放后退
     *
     * @param delataX
     */
    private void backward(float delataX) {
        if (_player.mediaPlayer == null) return;
        int current = _player.mediaPlayer.getCurrentPosition();
        int backTime = (int) (delataX / _fWidth * _player.mediaPlayer.getDuration());
        int currentTime = current - backTime;
        _player.mediaPlayer.seekTo(currentTime);
        _seekBar.setProgress(currentTime * 100 / _player.mediaPlayer.getDuration());
        _currentTimeTextView.setText(formatTime(currentTime) + "\40");
    }

    /**
     * 播放前进
     *
     * @param delataX
     */
    private void forward(float delataX) {
        if (_player.mediaPlayer == null) return;
        int current = _player.mediaPlayer.getCurrentPosition();
        int backTime = (int) (delataX / _fWidth * _player.mediaPlayer.getDuration());
        int currentTime = current + backTime;
        _player.mediaPlayer.seekTo(currentTime);
        _seekBar.setProgress(currentTime * 100 / _player.mediaPlayer.getDuration());
        _currentTimeTextView.setText(formatTime(currentTime) + "\40");
    }

    /**
     * 降低音量
     *
     * @param delatY
     */
    private void volumeDown(float delatY) {
        int max = _audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int down = (int) (delatY / _fHeight * max * 3);
        int volume = Math.max(current - down, 0);
        _audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        _volumnController.show(transformatVolume);
    }

    /**
     * 增强音量
     *
     * @param delatY
     */
    private void volumeUp(float delatY) {
        int max = _audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int up = (int) (delatY / _fHeight * max * 3);
        int volume = Math.min(current + up, max);
        _audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;

        _volumnController.show(transformatVolume);

    }

    /**
     * 降低亮度
     *
     * @param delatY
     */
    private void lightDown(float delatY) {
        int current = LightnessController.getLightness(this);
        int down = (int) (delatY / _fHeight * 255 * 3);
        int light = Math.max(current - down, 0);

        LightnessController.setLightness(this, light);

        int transformatLight = light * 100 / 255;
        _lightController.show(transformatLight);
    }

    /**
     * 增强亮度
     *
     * @param delatY
     */
    private void lightUp(float delatY) {
        int current = LightnessController.getLightness(this);
        int up = (int) (delatY / _fHeight * 255 * 3);
        int light = Math.min(current + up, 255);

        LightnessController.setLightness(this, light);
        int transformatLight = light * 100 / 255;
        _lightController.show(transformatLight);
    }

    private float _fLastMotionX;
    private float _fLastMotionY;
    private int _istartX;
    private int _istartY;
    private boolean isClick = true;

    private View.OnTouchListener OnScreenOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            _playBottomLayout.setVisibility(View.VISIBLE);
            if (handler != null) {
                handler.removeMessages(HIDE_UI);
                handler.sendEmptyMessageDelayed(HIDE_UI, DELAY_TIME);
            }

            final float x = event.getX();
            final float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    _fLastMotionX = x;
                    _fLastMotionY = y;
                    _istartX = (int) x;
                    _istartY = (int) y;
                    break;

                case MotionEvent.ACTION_MOVE:

                    float deltaX = x - _fLastMotionX;
                    float deltaY = y - _fLastMotionY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);

                    boolean isAdjustAudio = false;
                    if (absDeltaX > _ithreshold && absDeltaY > _ithreshold) {
                        if (absDeltaX < absDeltaY) {
                            isAdjustAudio = true;
                        } else {
                            isAdjustAudio = false;
                        }
                    } else if (absDeltaX < _ithreshold && absDeltaY > _ithreshold) {
                        isAdjustAudio = true;
                    } else if (absDeltaX > _ithreshold && absDeltaY < _ithreshold) {
                        isAdjustAudio = false;
                    } else {
                        return true;
                    }

                    if (isAdjustAudio) {
                        if (x < _fWidth / 2) {
                            if (deltaY > 0) {
                                lightDown(absDeltaY);
                            } else if (deltaY < 0) {
                                lightUp(absDeltaY);
                            }
                        } else {
                            if (deltaY > 0) {
                                volumeDown(absDeltaY);
                            } else if (deltaY < 0) {
                                volumeUp(absDeltaY);
                            }
                        }
                    } else {
                        //暂时不需要屏幕左右滑动
                        if (deltaX > 0) {
                            forward(absDeltaX / 10);
                        } else if (deltaX < 0) {
                            backward(absDeltaX / 10);
                        }
                    }

                    _fLastMotionX = x;
                    _fLastMotionY = y;

                    break;

                case MotionEvent.ACTION_UP:

                    if (Math.abs(x - _istartX) > _ithreshold
                            || Math.abs(y - _istartY) > _ithreshold) {

                        isClick = false;
                    }
                    _fLastMotionX = 0;
                    _fLastMotionY = 0;
                    _istartX = 0;

                    if (isClick) {
                        showorhide();
                    }
                    isClick = true;
                    break;
            }

            return true;
        }
    };

   /* @Override
    protected void onPause() {
        super.onPause();

        if (_player.mediaPlayer != null && _player.mediaPlayer.isPlaying()) {

            _player.mediaPlayer.setOnBufferingUpdateListener(null);

            _player.pause();

        }
        //zx_note 统计
        //MobclickAgent.onPageEnd(_pageName);
        //MobclickAgent.onPause(this);
    }*/

    /*  @Override
      public void onResume() {
          super.onResume();
          //zx_note 统计
          //MobclickAgent.onPageStart(_pageName);
          // MobclickAgent.onResume(this);
      }*/
    public void onResume() {
        super.onResume();
        if (_sensorManager != null) {
            _sensorManager.registerListener(listener, _sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause() {
        super.onPause();

        if (_player.mediaPlayer != null && _player.mediaPlayer.isPlaying()) {

            _player.mediaPlayer.setOnBufferingUpdateListener(null);

            _player.pause();

        }
        if (_sensorManager != null) {
            _sensorManager.unregisterListener(listener);
        }
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

            if (NetUtils.isNetConnected(context)) {
                // 连接成功后的业务逻辑
                // 具体的网络连接方式
                switch (mNetworkInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        Log.i("gzeyao", getResources().getString(R.string.wifi_is_not_connected));
                        // 执行业务
                        if (isNetState == false) {
                            isNetState = true;
                            ToastUtils.showShort(context, "" + context.getResources().getText(R.string.wifi_connect));
                            resumePlay();
                            handler.sendEmptyMessage(HIDE_MEDIA_ERROR);
                        }
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        Log.i("gzeyao", getResources().getString(R.string.mobile_is_connecting));
                        // 执行业务
                        if (isNetState == false) {
                            isNetState = true;
                            ToastUtils.showShort(context, "" + context.getResources().getText(R.string.mobile_net_connect));
                            handleNoWifiState(true);
                        }
                        break;
                    default:
                        Log.i("gzeyao", getResources().getString(R.string.no_connect));
                        if (isNetState == true) {
                            isNetState = false;
                            ToastUtils.showShort(context, "" + context.getResources().getText(R.string.network_exception));
                            pausePlay();
                        }
                        break;
                }
            } else {   //沒有網絡連接
                pausePlay();
                handler.sendEmptyMessage(NO_NETWORK);
                ToastUtils.showShort(context, "" + context.getResources().getText(R.string.network_exception));
                isNetState = false;
            }
        }

    }

    public synchronized void startListening(Context context) {
        if (!mListening) {
//            _context = context;

            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(mReceiver, filter);
            mListening = true;
        }
    }

    public synchronized void stopListening() {
        if (mListening) {
            _context.unregisterReceiver(mReceiver);
//            _context = null;
            mNetworkInfo = null;
            mOtherNetworkInfo = null;
            mIsFailOver = false;
            mReason = null;
            mListening = false;
        }
    }

    /**
     * 如果设置了设置了可以播放，就直接播放，否则，弹出提示框
     *
     * @param isResumePlay 判断是继续播放还是从头播放 ,false从头播放，true继续播放
     */
    private void handleNoWifiState(final boolean isResumePlay) { //无wifi，但有手机流量
        if ((boolean) SharedPreferencesUtils.getParam(_context.getApplicationContext(), "IS_NOWIFI_PLAY", false)) {  //非wifi可以播放

            if (isResumePlay) {
                resumePlay();
            } else {
                _mediaplayerErrorText.setVisibility(View.GONE);
                toAutoPlayFilm(true);
            }

        } else { //弹框提醒
            DialogHelpUtils.getConfirmDialog(_context, getString(R.string.film_play_warn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {  //确定, 保存到sp中

                    if (isResumePlay) {
                        resumePlay();
                    } else {
                        _mediaplayerErrorText.setVisibility(View.GONE);
                        toAutoPlayFilm(true);
                    }

                    SharedPreferencesUtils.setParam(_context.getApplicationContext(), "IS_NOWIFI_PLAY", true);

                }
            }, null).show();
        }
    }

}
