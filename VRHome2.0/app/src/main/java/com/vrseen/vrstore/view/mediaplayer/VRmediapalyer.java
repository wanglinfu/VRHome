package com.vrseen.vrstore.view.mediaplayer;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.lidroid.xutils.util.LogUtils;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.wasu.util.NetUtils;

import java.io.IOException;

/**
 * Created by John on 16/1/19.
 */
public class VRmediapalyer implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener {

    public MediaPlayer      mediaPlayer;
    public boolean          mIsOccurError = false;

    private Activity        _activity;
    private Display         _currentDisplay;
    private SurfaceHolder   _surfaceHolder;
    private SeekBar         _seekProgress;
    private ProgressBar     _loadingBar;
    private Handler         _handler;
    private SurfaceView     _surfaceView;
    private Callback        _callback;

    private int             _iVideoWidth;
    private int             _iVideoHeight;
    private boolean         _isReadyMedia = false;
    private boolean         _bIsAutoPlay = false;
    private String          _strMediaUrl = "";
    private int             _iFilmDuration = 0;
    private String          TAG = "VRMediaplayer";
    private int             _iCurDuration = 0;

    public int getCurDuration() {
        return _iCurDuration;
    }

    public void setCurDuration(int curDuration) {
        this._iCurDuration = curDuration;
    }

    public boolean isPrepared()
    {
        return _isReadyMedia;
    }

    public VRmediapalyer(Activity activity, SurfaceView surfaceView, SeekBar seekBar,
                         ProgressBar progressBar, Handler handler)
    {

        mediaPlayer = new MediaPlayer();

        this._activity = activity;

        this._currentDisplay = activity.getWindowManager().getDefaultDisplay();

        this._seekProgress = seekBar;

        this._surfaceView = surfaceView;

        this._loadingBar = progressBar;

        _handler = handler;

        _surfaceHolder=surfaceView.getHolder();

        _surfaceHolder.addCallback(this);

        _surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //_timer.schedule(timerTask, 0, 1000);

        _seekProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());


    }

    public void resetMediaPlayer()
    {
        if(mediaPlayer == null) return;

        if(!_isReadyMedia || mIsOccurError) {
            _iCurDuration = mediaPlayer.getCurrentPosition();

            mediaPlayer.reset();
            _isReadyMedia = false;
        }
        else
        {
            mediaPlayer.seekTo(0);
            _seekProgress.setProgress(0);
            pause();
        }

        mIsOccurError = false;

    }

    public void reset()
    {
        if(mediaPlayer == null) return;

        if(_handler != null)
            _handler.removeCallbacksAndMessages(null);

        mediaPlayer.reset();

        _isReadyMedia = false;
    }

    public void play()
    {
        if(!NetUtils.isNetConnected(_activity.getBaseContext()))
            return;

        if(!_isReadyMedia ) return;

        if (mediaPlayer != null) {

            mediaPlayer.start();

            if(_handler != null)
                _handler.sendEmptyMessage(FilmDetailActivity.UPDATE_TIME);

           _callback.isPlaying(true);

        }
    }

    public void playUrl(boolean isAutoPlay, String url,boolean isNewFilm)
    {
        if(!NetUtils.isNetConnected(_activity.getBaseContext()))
            return;

        try {
            if (url != null) {

                if(mediaPlayer == null)
                    mediaPlayer = new MediaPlayer();

                _strMediaUrl = url;

                _bIsAutoPlay = isAutoPlay;

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.reset();

                mediaPlayer.setDataSource(url);

                mediaPlayer.prepareAsync();

                _loadingBar.setVisibility(View.VISIBLE);

                mIsOccurError = false;

                if(isNewFilm)
                    _iCurDuration = 0;

                _isReadyMedia = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        } catch (SecurityException e)
        {
            e.printStackTrace();
        } catch (IllegalStateException e)
        {
            e.printStackTrace();
        }

    }

    public void pause()
    {
        if (mediaPlayer == null) return;

        if(_isReadyMedia && !mIsOccurError) {
            mediaPlayer.pause();
        }
        else
            mediaPlayer.reset();

        _callback.isPlaying(false);

    }

    public void stop()
    {
        if (mediaPlayer == null) return;

        if(!_isReadyMedia || mIsOccurError)
            mediaPlayer.reset();
        else
            mediaPlayer.stop();

        mediaPlayer.release();

        mediaPlayer = null;

        mIsOccurError = false;

        _callback.isPlaying(false);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (mediaPlayer != null)
        {
            try {
                mediaPlayer.setDisplay(_surfaceHolder);
            }
            catch (IllegalArgumentException e)
            {
                final Surface surface = holder.getSurface();
                if ( surface == null ) return;

                final boolean invalidSurface = ! surface.isValid();
                if ( invalidSurface  ) return;

                mediaPlayer.setDisplay(holder);
                LogUtils.e("surfaceCreated IllegalArgumentException error");
            }

            mediaPlayer.setOnBufferingUpdateListener(this);

            mediaPlayer.setOnPreparedListener(this);

            mediaPlayer.setOnVideoSizeChangedListener(this);

            mediaPlayer.setOnSeekCompleteListener(this);

            mediaPlayer.setOnErrorListener(this);

            mediaPlayer.setOnCompletionListener(this);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

        if(mediaPlayer == null) return;

        if(!_isReadyMedia  || mIsOccurError) return;//|| !mp.isPlaying()

        // FIXME: 2016/7/4 该方法是为了解决拖动到最后无法退出播放影视的bug
//        if(percent >= 100)
//        {
//            _callback.isPlayComplete(true);
//            resetMediaPlayer();
//        }

        _seekProgress.setSecondaryProgress(percent);

    }

    public void updateSeekBar()
    {
        if(mediaPlayer == null) return;

        if(!_isReadyMedia || !mediaPlayer.isPlaying() ) return;//|| mIsOccurError

        int currentProgress = 0;

        if(mediaPlayer.getDuration() != 0)
            currentProgress = _seekProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();

        _seekProgress.setProgress(currentProgress);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if(mediaPlayer == null || mIsOccurError) return;

        _callback.isPlayComplete(true);
        resetMediaPlayer();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        if(mediaPlayer == null) return;

        LogUtils.d("``````````````````````````onPrepared");

        _isReadyMedia = true;

        _iFilmDuration = mediaPlayer.getDuration();

        if (_loadingBar.getVisibility() == View.VISIBLE)
        {
            _loadingBar.setVisibility(View.GONE);
        }

        if(_bIsAutoPlay) {

            if(_iCurDuration < _iFilmDuration)
                mediaPlayer.seekTo(_iCurDuration);
            else
                mediaPlayer.seekTo(0);

            mediaPlayer.start();
            if(_handler != null)
                _handler.sendEmptyMessage(FilmDetailActivity.UPDATE_TIME);
        }

        _iVideoWidth = mediaPlayer.getVideoWidth();

        _iVideoHeight = mediaPlayer.getVideoHeight();

        if (_iVideoHeight != 0 && _iVideoWidth != 0)
        {
            _callback.isLoadComplete(true);

        }

    }

    /**
     * 播放状态监听
     * @param callback
     */
    public void setOnPlayerStartListener(Callback callback)
    {
        this._callback = callback;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

        if (mediaPlayer != null) {

            _loadingBar.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        mIsOccurError = true;

        resetMediaPlayer();

        _handler.removeCallbacksAndMessages(null);

        if(what != 1)
            _handler.sendEmptyMessage(FilmDetailActivity.MEDIAPLAYER_UNKNOW_ERROR);

        LogUtils.e("mediaplayer error:" + "what:" + what + "extra:" + extra + "url:" + _strMediaUrl);

        String msgWhat = "";
        switch (what) {
            case -1004:
                msgWhat = "MEDIA_ERROR_IO";
                break;
            case -1007:
                msgWhat = "MEDIA_ERROR_MALFORMED";
                break;
            case 200:
                msgWhat = "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK";
                break;
            case 100:
                msgWhat =  "MEDIA_ERROR_SERVER_DIED";
                break;
            case -110:
                msgWhat = "MEDIA_ERROR_TIMED_OUT";
                break;
            case 1:
                msgWhat = "MEDIA_ERROR_UNKNOWN";
                break;
            case -1010:
                msgWhat ="MEDIA_ERROR_UNSUPPORTED";
                break;
        }

        String msgExtra = "";

        switch (extra) {
            case 800:
                msgExtra =  "MEDIA_INFO_BAD_INTERLEAVING";
                break;
            case 702:
                msgExtra = "MEDIA_INFO_BUFFERING_END";
                break;
            case 701:
                msgExtra =  "MEDIA_INFO_METADATA_UPDATE";
                break;
            case 802:
                msgExtra =  "MEDIA_INFO_METADATA_UPDATE";
                break;
            case 801:
                msgExtra = "MEDIA_INFO_NOT_SEEKABLE";
                break;
            case 1:
                msgExtra =  "MEDIA_INFO_UNKNOWN";
                break;
            case 3:
                msgExtra = "MEDIA_INFO_VIDEO_RENDERING_START";
                break;
            case 700:
                msgExtra =  "MEDIA_INFO_VIDEO_TRACK_LAGGING";
                break;
        }

        if(StringUtils.isNotBlank(msgWhat))
        {
            ToastUtils.showShort(_activity, "error:" + msgWhat);
            LogUtils.e("error what:" + msgWhat);
        }

        if(StringUtils.isNotBlank(msgExtra))
        {
            ToastUtils.showShort(_activity, "error:" + msgExtra);
            LogUtils.e("error extra:" + msgExtra);
        }

        return true;
    }

    public interface Callback
    {
        void isLoadComplete(boolean flag);
        void isPlaying(boolean flag);
        void isPlayComplete(boolean flag);

    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener{

        int progress = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(!_isReadyMedia || mediaPlayer == null) return;//

            this.progress = progress * _iFilmDuration / _seekProgress.getMax();

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if(!_isReadyMedia || mediaPlayer == null || mIsOccurError) return;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            if(!_isReadyMedia || mediaPlayer == null ) return;//|| mIsOccurError

            mediaPlayer.seekTo(progress);

            if(!mIsOccurError)
                _loadingBar.setVisibility(View.VISIBLE);
            else
                _loadingBar.setVisibility(View.GONE);

        }
    }
}
