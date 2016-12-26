package com.vrseen.vrstore.activity.find;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.fragment.find.LocalFragment;
import com.vrseen.vrstore.logic.FileLogic;
import com.vrseen.vrstore.model.find.LocalResInfo;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalActivity extends BaseActivity {

    private SmartTabLayout _viewPagerTab;
    private ViewPager _viewPager;
    private Map<String,String> _tabTitle = new HashMap<>();
    private String[] _title;
    private Map<String,String> _sortedTitle;
    private List<Map<String, String>> _listSortedTitle = new ArrayList<>();
    private LinearLayout _nodataLayout;
    private TextView _tvNoData;
    private List<LocalResInfo> _list;
    private FragmentPagerItems.Creator _creator;
    private ProgressRelativeLayout _progressLinearLayout;

    public static final String LOCAL_VIDEO = "video";
    public static final String LOCAL_IMAGE = "image";

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,LocalActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("LocalActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_local);

        _progressLinearLayout = (ProgressRelativeLayout)findViewById(R.id.progress_layout);
        _progressLinearLayout.showProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileLogic.getInstance().init(LocalActivity.this);
                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.view_back).setOnClickListener(this);
        _viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        _viewPager = (ViewPager) findViewById(R.id.viewpager);
        _nodataLayout = (LinearLayout) findViewById(R.id.no_data_layout);
        _tvNoData = (TextView) findViewById(R.id.tv_nodata);
        _title = getResources().getStringArray(R.array.titles_array);
        initData();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    initView();
                    break;
                case 1:

                    _progressLinearLayout.showContent();

                    if(_list.size() <= 0)
                    {
                        _viewPagerTab.setVisibility(View.GONE);
                        _viewPager.setVisibility(View.GONE);
                        _nodataLayout.setVisibility(View.VISIBLE);
                        _tvNoData.setText(getString(R.string.find_local_nodata));
                    }
                    else
                    {
                        _viewPager.setVisibility(View.VISIBLE);
                        _viewPagerTab.setVisibility(View.VISIBLE);
                        _nodataLayout.setVisibility(View.GONE);
                    }


                    for (int i = 0; i < _list.size(); i++) {
                        LocalResInfo vo = _list.get(i);
                        int type = vo.getType();
                        switch ( type )
                        {
                            case VRHomeConfig.LOCAL_TYPE_PANO_VIDEO:
                                _tabTitle.put(String.valueOf(type),_title[0]);
                                break;
                            case VRHomeConfig.LOCAL_TYPE_PANO_IMAGE:
                                _tabTitle.put(String.valueOf(type),_title[1]);
                                break;
                            // FIXME: 2016/6/24 暂时不区分3d 上下左右（一律按左右播放的方式）
//                 case VRHomeConfig.LOCAL_TYPE_MOVIE_3D_TB:
//                     _tabTitle.put(String.valueOf(type),_title[2]);
//                     break;
//                 case VRHomeConfig.LOCAL_TYPE_MOVIE_3D_LR:
//                     _tabTitle.put(String.valueOf(type),_title[2]);
//                     break;
                            case VRHomeConfig.LOCAL_TYPE_MOVIE_3D:
                                _tabTitle.put(String.valueOf(type),_title[2]);
                                break;
                            case VRHomeConfig.LOCAL_TYPE_MOVIE_2D:
                                _tabTitle.put(String.valueOf(type),_title[3]);
                                break;
                            default:break;
                        }
                    }

                    //这里按标题排序
                    for(int i = 0; i < _title.length; i++){
                        for(Map.Entry<String, String> entry : _tabTitle.entrySet()) {

                            if(_title[i].equals(entry.getValue())){
                                _sortedTitle = new HashMap<>();
                                _sortedTitle.put("type", entry.getKey());
                                _sortedTitle.put("title", entry.getValue());
                                _listSortedTitle.add(_sortedTitle);
                            }

                        }
                    }

                    for(int i = 0; i < _listSortedTitle.size(); i++){
                        String type = _listSortedTitle.get(i).get("type");
                        String title = _listSortedTitle.get(i).get("title");

                        _creator.add(title, LocalFragment.class,
                                new Bundler().putSerializable(LocalFragment.LOCAL_TYPE, type).get());
                    }


                    FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                            getSupportFragmentManager(), _creator.create());

                    _viewPager.setAdapter(adapter);
                    _viewPagerTab.setViewPager(_viewPager);
                    break;
            }
        }
    };
    private void initData() {
         _creator = FragmentPagerItems.with(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                 _list = FileLogic.getInstance().localFileArrList;
                handler.sendEmptyMessage(1);
            }
        }).start();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.view_back :
                finish();
                break;
            default: break;
        }
    }
}
