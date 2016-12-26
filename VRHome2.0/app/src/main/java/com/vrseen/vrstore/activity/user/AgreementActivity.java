package com.vrseen.vrstore.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.util.CommonUtils;

/**
 *
 * Created by mll on 2016/5/6.
 */
public class AgreementActivity extends BaseActivity {

    public static int LICENSE = 1;//软件许可及服务协议
    public static int AGREEMENT= 2;//隐私协议

    private String webUrl = "";

    public static void actionStart(Context context,int type)
    {
        Intent intent = new Intent(context,AgreementActivity.class);
        intent.putExtra("type",type);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setPageName("AgreementActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agreement);

        int type = getIntent().getIntExtra("type",0);
        if(type == LICENSE)
        {
            webUrl = "file:///android_asset/fuwu.html";
        }
        else if(type == AGREEMENT)
        {
            webUrl = "file:///android_asset/yinsi.html";
        }

        initView();
    }


    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.view_back).setOnClickListener(this);

        WebView webView = (WebView) findViewById(R.id.webview);
        //允许JavaScript执行
        webView.getSettings().setJavaScriptEnabled(true);
        //找到Html文件，也可以用网络上的文件
        webView.loadUrl(webUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.view_back:
                this.finish();
                break;
            default:break;
        }
    }

}
