package com.vrseen.vrstore;

import android.app.Activity;
import android.os.Bundle;

import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;

public class ComeInVRActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_come_in_vr);
    }

    private void ComeInVR(){
        Bundle bundle = this.getIntent().getExtras();
        final String msg = bundle.getString("msg");

        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                U3dMediaPlayerLogic.getInstance().startSceneVR(ComeInVRActivity.this,msg);
            }
        }, 100);
    }

    public void onResume() {
        super.onResume();
        ComeInVR();
    }
    public void onPause() {
        super.onPause();
    }

}
