package com.vrseen.vrstore.fragment.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.view.ScrollableHelper;

import in.srain.cube.app.CubeFragment;

/**
 * 分类对应的fragment
 * Created by mll on 2016/5/16.
 */
public abstract class BaseCategoryFragment extends CubeFragment implements ScrollableHelper.ScrollableContainer {

    private String _pageName;

    public String getPageName() {
        return _pageName;
    }

    public void setPageName(String pageName) {
        this._pageName = pageName;
    }

    public void onResume() {
        super.onResume();
        if(StringUtils.isNotBlank(_pageName))
        {
            MobclickAgent.onPageStart(_pageName); //统计页面，"MainScreen"为页面名称，可自定义
        }
    }

    public void onPause() {
        super.onPause();
        if(StringUtils.isNotBlank(_pageName))
        {
            MobclickAgent.onPageEnd(_pageName); //统计页面，"MainScreen"为页面名称，可自定义
        }
    }

}
