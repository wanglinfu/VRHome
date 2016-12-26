package com.vrseen.vrstore.activity.find;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.adapter.find.FindPagerAdapter;
import com.vrseen.vrstore.fragment.find.DownloadAppFragment;
import com.vrseen.vrstore.fragment.find.DownloadPanoFragment;
import com.vrseen.vrstore.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends BaseActivity {

    private Context _context = null;

    private ViewPager _viewPager;
    private RadioGroup _radioGroup;
    private RadioButton _rbApp;
    private RadioButton _rbPano;

    public static final String DOWNLOAD_APP  = "MyDownloadApp";
    public static final String DOWNLOAD_PANO = "MyDownloadPano";

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,DownloadActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        this.setPageName("DownloadActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download);

        _context = this;
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.view_back).setOnClickListener(this);
        findViewById(R.id.tv_deleteAll).setOnClickListener(this);
        _rbApp = (RadioButton) findViewById(R.id.rb_app);
        _rbPano = (RadioButton) findViewById(R.id.rb_pano);
        _radioGroup = (RadioGroup) findViewById(R.id.rg_downloadInfo);
        _radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_app){
                    _viewPager.setCurrentItem(0);
                }else if(checkedId == R.id.rb_pano){
                    _viewPager.setCurrentItem(1);
                }
            }
        });

        List<Fragment> list = new ArrayList<>();
        DownloadAppFragment appfragment = new DownloadAppFragment();
        list.add(appfragment);
        DownloadPanoFragment panoFragment = new DownloadPanoFragment();
        list.add(panoFragment);

        _viewPager = (ViewPager) findViewById(R.id.vp_content);
        _viewPager.setAdapter(new FindPagerAdapter(getSupportFragmentManager(),list));

        _viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    _rbApp.setChecked(true);
                    _rbPano.setChecked(false);
                }else if(position == 1){
                    _rbApp.setChecked(false);
                    _rbPano.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        _rbApp.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (v.getId())
        {
            case R.id.view_back :
                finish();
                break;

            default: break;
        }
    }
}
