package com.vrseen.vrstore.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.util.CommonUtils;

/**
 * 关于我们
 * Created by mll on 2016/5/5.
 */
public class AboutUsActivity extends BaseActivity {

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,AboutUsActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setPageName("AboutUsActivity");
        super.onCreate(savedInstanceState);
        if(VRHomeConfig.CUR_RELEASE_TYPE == VRHomeConfig.ReleaseType.ZTE)
            setContentView(R.layout.activity_aboutus_zte);
        else
            setContentView(R.layout.activity_aboutus);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        TextView textView = (TextView) findViewById(R.id.versionText);

        textView.setText(CommonUtils.getVersionName(this));

        findViewById(R.id.view_back).setOnClickListener(this);

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
