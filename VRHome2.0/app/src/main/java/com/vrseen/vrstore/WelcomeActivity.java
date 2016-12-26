package com.vrseen.vrstore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.logic.FileLogic;
import com.vrseen.vrstore.util.Constant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

/**
 * Created by jiangs on 16/4/27.
 */
public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        CheckCurReleaseType();

        setContentView(R.layout.activity_welcome);
        changeBgImage();
        //processIntent(getIntent());
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    /**
     * 处理新来的intent
     *
     * @param intent
     */
    private void processIntent(Intent intent) {
        int target = intent.getIntExtra(Constant.KEY_JUMP_REQ_CODE, -1);
        if (target == Constant.JUMP_REQ_LOGIN) {
            //到登陆页
            //这里不用处理，会自动到登录页
        } else {
            // 退出程序
            if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
                finish();
            }
        }
    }

    protected void initApp() {
        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();

            }
        }, 500);
    }

    private void init()
    {
        SharedPreferencesUtils.setParam(getApplicationContext(), "IS_NOWIFI_PLAY", false);
        SharedPreferencesUtils.setParam(getApplicationContext(), "IS_NOWIFI_Download", false);
        VRHomeConfig.setSavePath(FileLogic.getInstance().getLocalFilePath(this.getPackageName()));
        FileLogic.getInstance().createLocalFile(this);
    }

    /**
     * 根据包名判断当前版本
     */
    private void CheckCurReleaseType(){
        String packname = this.getPackageName();
        switch (packname){
            case "com.vrseen.zvr":
                VRHomeConfig.CUR_RELEASE_TYPE = VRHomeConfig.ReleaseType.ZTE;
                break;
            default:
                VRHomeConfig.CUR_RELEASE_TYPE = VRHomeConfig.ReleaseType.OFFICIAL;
                break;
        }
    }

    private void changeBgImage(){
        if(VRHomeConfig.CUR_RELEASE_TYPE == VRHomeConfig.ReleaseType.ZTE) {
            ImageView bgImage = (ImageView)this.findViewById(R.id.iv_welcome_logo);
            bgImage.setBackground(getResources().getDrawable(R.drawable.welcome_logo_zte));
        }
    }


    /**
     * 进入主界面
     */
    protected void startMainActivity() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }



   /* @Override
    protected void onResume() {
        super.onResume();
        initApp();
    }*/

    public void onResume() {
        super.onResume();
        initApp();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
