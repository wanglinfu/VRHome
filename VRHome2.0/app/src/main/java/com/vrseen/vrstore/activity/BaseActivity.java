package com.vrseen.vrstore.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.VRHomeApplication;
import com.vrseen.vrstore.util.StringUtils;


/**
 * Created by jiangs on 16/4/29.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    private String _pageName;

    public String getPageName() {
        return _pageName;
    }

    public void setPageName(String pageName) {
        this._pageName = pageName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VRHomeApplication.getInstance().addActivity(this);
    }

    protected void initView()
    {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        VRHomeApplication.getInstance().deleteActivity(this);
    }

    public void onResume() {
        super.onResume();

        if(StringUtils.isNotBlank(_pageName))
        {
            MobclickAgent.onPageStart(_pageName);
        }
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();

        if(StringUtils.isNotBlank(_pageName))
        {
            MobclickAgent.onPageEnd(_pageName);
        }
        MobclickAgent.onResume(this);
    }
}
