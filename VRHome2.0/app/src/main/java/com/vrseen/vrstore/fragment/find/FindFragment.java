package com.vrseen.vrstore.fragment.find;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.find.DownloadActivity;
import com.vrseen.vrstore.activity.find.LocalActivity;
import com.vrseen.vrstore.fragment.BaseFragment;

public class FindFragment extends BaseFragment implements View.OnClickListener {


    private View _thisFragment;
    public static FindFragment instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.setPageName("FindFragment");
        super.onCreateView(inflater, container, savedInstanceState);
        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_find, null);
            initView();
        } else {
            // 从parent删除
            ViewGroup parent = (ViewGroup) _thisFragment.getParent();
            if (parent != null) {
                parent.removeView(_thisFragment);
            }
        }
        instance = this;
        return _thisFragment;

    }

    private void initView() {
        _thisFragment.findViewById(R.id.ll_local).setOnClickListener(this);
        _thisFragment.findViewById(R.id.ll_download).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch ( v.getId() )
        {
            case R.id.ll_local :
                LocalActivity.actionStart(getActivity());
                break;
            case R.id.ll_download :
                DownloadActivity.actionStart(getActivity());
                break;
        }
    }
}
