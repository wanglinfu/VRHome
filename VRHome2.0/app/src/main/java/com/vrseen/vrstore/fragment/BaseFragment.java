package com.vrseen.vrstore.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.util.StringUtils;

/**
 * Created by jiangs on 16/4/29.
 */
public class BaseFragment extends Fragment {

    private Context mContext;
    private String _pageName;

    public String getPageName() {
        return _pageName;
    }

    public void setPageName(String pageName) {
        this._pageName = pageName;
    }

    public Context getContext() {
        if (mContext != null) {
            return mContext;
        } else if (getActivity() != null) {
            return getActivity();
        } else if (getView() != null) {
            return getView().getContext();
        } else {
            throw new IllegalStateException("couldn't find context.");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
