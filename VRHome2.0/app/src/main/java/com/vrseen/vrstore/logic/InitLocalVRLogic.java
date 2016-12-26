package com.vrseen.vrstore.logic;

import android.app.Activity;
import android.os.Handler;

/**
 * Created by FX on 2016/7/5 00:39.
 * 描述:
 */

public class InitLocalVRLogic implements Runnable {
    private Thread _thread;
    private Activity _localRelatedActivity;
    private Handler _handler;
    private boolean _bIsPause = false;
    private boolean _bIsStop = false;
    private int _iMsgType;

    public InitLocalVRLogic(Activity activity, Handler handler, int msgType) {
        this._localRelatedActivity = activity;
        this._handler = handler;
        this._iMsgType = msgType;
    }

    public void start() {
        if(this._thread == null || this._thread.getState() == Thread.State.TERMINATED) {
            this._thread = new Thread(this);
            this._thread.start();
        }

    }

    public void stop() {
        if(this._thread != null) {
            if(this._thread.getState() != Thread.State.TERMINATED) {
                this._thread.interrupt();
            }

            this._thread = null;
            this._bIsStop = true;
        }

    }

    public void pause() {
        if(this._thread != null && !this._bIsStop && this._thread.getState() == Thread.State.RUNNABLE) {
            this._bIsPause = true;
        }

    }

    public void resume() {
        if(this._thread != null && !this._bIsStop && this._thread.getState() == Thread.State.RUNNABLE) {
            this._bIsPause = false;
        }

    }

    public void run() {
        if(!this._bIsPause && !this._bIsStop) {
            FileLogic.getInstance().initByHandler(this._localRelatedActivity, this._handler, this._iMsgType);
        }

    }
}
