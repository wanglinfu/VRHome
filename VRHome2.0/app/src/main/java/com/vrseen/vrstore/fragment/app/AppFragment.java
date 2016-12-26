package com.vrseen.vrstore.fragment.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.app.AppDetailActivity;
import com.vrseen.vrstore.activity.search.SearchActivity;
import com.vrseen.vrstore.adapter.app.CommonFragmentAdapter;
import com.vrseen.vrstore.fragment.BaseFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.SearchRestClient;
import com.vrseen.vrstore.model.app.AppBannerData;
import com.vrseen.vrstore.model.app.AppCategoryData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.ConfigDefaultImageUtils;
import com.vrseen.vrstore.view.ProgressRelativeLayout;
import com.vrseen.vrstore.view.ScrollableLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.util.NetworkStatusManager;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;


public class AppFragment extends BaseFragment implements OnPageChangeListener, View.OnClickListener {

    private PtrClassicFrameLayout _ptrFrameLayout;
    private StoreHouseHeader _storeHouseHeader;
    private String _strLoadText = "VRSEEN";
    //四个应用的分类
    private ArrayList<LinearLayout> _typeLLArray = new ArrayList<>();

    // FIXME: 2016/7/1 为解决单个viewpager setadapter的bug 临时使用4个viewpager  mll
    private ViewPager _viewPager;
    private ViewPager _viewPager2;
    private ViewPager _viewPager3;
    private ViewPager _viewPager4;
    private ScrollableLayout _scrollableLayout;
    private View thisFragment;
    private MainActivity mContext;
    private CubeImageView _cubeImageView1 = null;
    private CubeImageView _cubeImageView2 = null;
    private ImageLoader _imageLoader;
    private SmartTabLayout _viewPagerTab;
    private View _viewLine;
    private FragmentPagerItemAdapter _typeAppAdapter;
    private FragmentPagerItemAdapter _rankAppAdapter;
    private CommonFragmentAdapter _hotAppAdapater;

    private ArrayList<BaseCategoryFragment> _hotAppList = new ArrayList<>();

    private FragmentPagerItemAdapter _newAppAdapater;
    private ArrayList<BaseCategoryFragment> _newAppList = new ArrayList<>();

    private ProgressRelativeLayout _progressRelativeLayout;

    private Button _searchButton;

    private Context _context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.setPageName("AppFragment");
        super.onCreateView(inflater, container, savedInstanceState);
        _context = getContext();
        if (thisFragment == null) {
            thisFragment = inflater.inflate(R.layout.fragment_app, null);
            initView();
        } else {
            // 从parent删除
            ViewGroup parent = (ViewGroup) thisFragment.getParent();
            if (parent != null) {
                parent.removeView(thisFragment);
            }
        }
        mContext = (MainActivity) getContext();
        _imageLoader = ConfigDefaultImageUtils.getInstance().getADImageLoader(getContext());
        return thisFragment;
    }

    /**
     * 初始化普通控件
     */
    private void initView() {

        _searchButton = (Button) thisFragment.findViewById(R.id.btn_search);
        _searchButton.setOnClickListener(this);

        _progressRelativeLayout = (ProgressRelativeLayout) thisFragment.findViewById(R.id.progress_layout);
        _progressRelativeLayout.showProgress();

        _viewLine = thisFragment.findViewById(R.id.viewLine);
        _viewPagerTab = (SmartTabLayout) thisFragment.findViewById(R.id.viewpagertab);
        _cubeImageView1 = (CubeImageView) thisFragment.findViewById(R.id.img_appBaner1);
        _cubeImageView2 = (CubeImageView) thisFragment.findViewById(R.id.img_appBaner2);
        _scrollableLayout = (ScrollableLayout) thisFragment.findViewById(R.id.app_scrollview);
        _ptrFrameLayout = (PtrClassicFrameLayout) thisFragment.findViewById(R.id.store_app_ptr_frame);
        if (_storeHouseHeader == null) {
            _storeHouseHeader = new StoreHouseHeader(getActivity());
            _storeHouseHeader.setPadding(0, LocalDisplay.dp2px(15), 0, 0);
        }

        _storeHouseHeader.initWithString(_strLoadText);

        _ptrFrameLayout.setDurationToCloseHeader(1000);
        _ptrFrameLayout.setHeaderView(_storeHouseHeader);
        _ptrFrameLayout.addPtrUIHandler(_storeHouseHeader);
        _ptrFrameLayout.setEnabledNextPtrAtOnce(true);
        _ptrFrameLayout.setLastUpdateTimeRelateObject(this);
        _ptrFrameLayout.disableWhenHorizontalMove(true);
        _ptrFrameLayout.setKeepHeaderWhenRefresh(true);
        _ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                if (_scrollableLayout.isCanPullToRefresh()) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }
                return false;
            }
        });

        _viewPager = (ViewPager) thisFragment.findViewById(R.id.app_viewpager1);
        _viewPager2 = (ViewPager) thisFragment.findViewById(R.id.app_viewpager2);
        _viewPager3 = (ViewPager) thisFragment.findViewById(R.id.app_viewpager3);
        _viewPager4 = (ViewPager) thisFragment.findViewById(R.id.app_viewpager4);

        _typeLLArray.add((LinearLayout) thisFragment.findViewById(R.id.ll_hotApp));
        _typeLLArray.add((LinearLayout) thisFragment.findViewById(R.id.ll_rankApp));
        _typeLLArray.add((LinearLayout) thisFragment.findViewById(R.id.ll_typeApp));
        _typeLLArray.add((LinearLayout) thisFragment.findViewById(R.id.ll_newApp));
        for (int i = 0; i < _typeLLArray.size(); i++) {
            _typeLLArray.get(i).setOnClickListener(new OnTypeChangeClickListener(i));
        }
//		_viewPager.setAdapter(new CommonFragmentAdapter(getFragmentManager(),_fragmentListArray));
//		_viewPager.setOnPageChangeListener(this);
//		_viewPager.setCurrentItem(0);
        //_scrollableLayout.getHelper().setCurrentScrollableContainer(_fragmentListArray.get(0));
        //获取app广告数据


//        myBroadcast=new MyBroadcast();
//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction("apprefresh");
//        getContext().registerReceiver(myBroadcast,intentFilter);
        initData();
    }

    private boolean _initData = false;

    private void initData() {

        if (_initData == true) {
            switchFragment(_position);
            return;
        }

        if (!NetworkStatusManager.getInstance(this.getContext()).isNetworkConnectedHasMsg(false)) {
            _progressRelativeLayout.showErrorText(getActivity().getString(R.string.get_data_fail));
            return;
        }

        requestAppAdData();
        _listCategory = new ArrayList<>();
    }

//    private MyBroadcast myBroadcast;

    private void requestAppAdData() {
        CommonRestClient.getInstance(getContext()).getAppAdData(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {

                String msg = getString(R.string.app_get_ad_error);
                CommonUtils.showResponseMessage(getActivity(), resp, e, R.string.app_get_ad_error);
                _progressRelativeLayout.showErrorText(msg);
            }

            @Override
            public void onSuccess(Response resp) {
                _initData = true;
                AppBannerData appBannerData = (AppBannerData) resp.getModel();
                if (appBannerData != null) {
                    int lgh = appBannerData.getData().size();
                    for (int i = 0; i < lgh; i++) {
                        final AppBannerData.AppBanner app = appBannerData.getData().get(i);
                        if (i == 0) {
                            _cubeImageView1.loadImage(_imageLoader, app.getImage());
                            _cubeImageView1.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppDetailActivity.actionStart(getActivity(), app.getApp_description_id());
                                }
                            });
                        }
                        if (i == 1) {
                            _cubeImageView2.loadImage(_imageLoader, app.getImage());
                            _cubeImageView2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppDetailActivity.actionStart(getActivity(), app.getApp_description_id());
                                }
                            });
                        }
                    }
                }
                requestAppCategoryData();
            }
        });
    }

    /**
     * 控件点击事件
     *
     * @param v
     */

    @Override
    public void onClick(View v) {

        int vid = v.getId();

        switch (vid) {
            case R.id.btn_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("from", SearchRestClient.APPLICATION_TAG);
                getActivity().startActivity(intent);
                break;
        }

    }

    /**
     * type onclick listener
     *
     * @author John
     */
    private class OnTypeChangeClickListener implements OnClickListener {

        int iPosition;

        public OnTypeChangeClickListener(int positon) {

            this.iPosition = positon;

        }

        @Override
        public void onClick(View v) {

            switchFragment(iPosition);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getContext().unregisterReceiver(myBroadcast);

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {

//        switchFragment(position);
    }

    private static int _position = 0;//当前被选中的button（热门 排行 分类 新品）
    private int _indexRankPage = 0;//当前被选中的page（排行）
    private int _indexTypePage = 0;//当前被选中的page（分类）

    /**
     * 切换fragment
     *
     * @param position
     */
    private void switchFragment(int position) {
        _position = position;
        _viewPager.setVisibility(View.GONE);
        _viewPager2.setVisibility(View.GONE);
        _viewPager3.setVisibility(View.GONE);
        _viewPager4.setVisibility(View.GONE);

        try {
            switch (position) {
                case 0:
                    _viewLine.setVisibility(View.VISIBLE);
                    _viewPager.setVisibility(View.VISIBLE);
                    _viewPagerTab.setVisibility(View.GONE);
                    if (_hotAppAdapater == null) {
                        BaseAppCategoryFragment fragment = new BaseAppCategoryFragment();
                        AppCategoryData.Category categroy = new AppCategoryData.Category();
                        categroy.setLatest(1);
                        categroy.setName(getString(R.string.app_hot));
                        categroy.setId(-1);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BaseAppCategoryFragment.KEY_APP_CATEGORY, categroy);
                        fragment.setArguments(bundle);
                        _hotAppList.add(fragment);
                        _hotAppAdapater = new CommonFragmentAdapter(this.getChildFragmentManager(), _hotAppList);
                        _viewPager.setAdapter(_hotAppAdapater);
                    } else {
                        //_viewPager.setAdapter(_hotAppAdapater);
                    }

                    _scrollableLayout.getHelper().setCurrentScrollableContainer(_hotAppList.get(0));

                    break;
                case 1:
                    _viewPager2.setVisibility(View.VISIBLE);
                    _viewLine.setVisibility(View.GONE);
                    _viewPagerTab.setVisibility(View.VISIBLE);

                    if (_rankAppAdapter == null) {
                        FragmentPagerItems.Creator creator = FragmentPagerItems.with(getActivity());
                        String[] rankApp = getActivity().getResources().getStringArray(R.array.app_rank);
                        List<AppCategoryData.Category> categoriesList = new ArrayList<>();
                        for (int i = 0; i < rankApp.length; i++) {
                            AppCategoryData.Category category = new AppCategoryData.Category();
                            category.setId(i);
                            category.setName(rankApp[i]);
                            category.setLatest(i);
                            categoriesList.add(category);
                        }

                        for (AppCategoryData.Category category : categoriesList) {
                            creator.add(category.getName(), BaseAppCategoryFragment.class,
                                    new Bundler().putSerializable(BaseAppCategoryFragment.KEY_APP_CATEGORY, category).get());
                        }
                        _rankAppAdapter = new FragmentPagerItemAdapter(
                                this.getChildFragmentManager(), creator.create());
                        _viewPager2.setAdapter(_rankAppAdapter);
                        _viewPagerTab.setViewPager(_viewPager2);
                    } else {
                        //_viewPager.setAdapter(_rankAppAdapter);
                        _viewPagerTab.setViewPager(_viewPager2);
                    }

                    _viewPager2.setCurrentItem(_indexRankPage);
                    _scrollableLayout.getHelper().setCurrentScrollableContainer(
                            (BaseAppCategoryFragment) _rankAppAdapter.getPage(_indexRankPage));
                    _viewPagerTab.setOnPageChangeListener(new OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            _indexRankPage = position;
                            _scrollableLayout.getHelper().setCurrentScrollableContainer(
                                    (BaseAppCategoryFragment) _rankAppAdapter.getPage(position));
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    break;
                case 2:
                    _viewLine.setVisibility(View.GONE);
                    _viewPager3.setVisibility(View.VISIBLE);
                    _viewPagerTab.setVisibility(View.VISIBLE);
                    if (_typeAppAdapter == null) {
                        FragmentPagerItems.Creator creator = FragmentPagerItems.with(getActivity());
                        List<AppCategoryData.Category> categories = _listCategory;
                        for (AppCategoryData.Category category : categories) {
                            creator.add(category.getName(), BaseAppCategoryFragment.class,
                                    new Bundler().putSerializable(BaseAppCategoryFragment.KEY_APP_CATEGORY, category).get());
                        }
                        _typeAppAdapter = new FragmentPagerItemAdapter(
                                this.getChildFragmentManager(), creator.create());
                        _viewPager3.setAdapter(_typeAppAdapter);
                        _viewPagerTab.setViewPager(_viewPager3);
                    } else {
                        //_viewPager3.setAdapter(_typeAppAdapter);
                        _viewPagerTab.setViewPager(_viewPager3);
                    }

                    _viewPager3.setCurrentItem(_indexTypePage);
                    _scrollableLayout.getHelper().setCurrentScrollableContainer(
                            (BaseAppCategoryFragment) _typeAppAdapter.getPage(_indexTypePage));
                    _viewPagerTab.setOnPageChangeListener(new OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            _indexTypePage = position;
                            _scrollableLayout.getHelper().setCurrentScrollableContainer(
                                    (BaseAppCategoryFragment) _typeAppAdapter.getPage(position));
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                    break;
                case 3:
                    _viewLine.setVisibility(View.VISIBLE);
                    _viewPager4.setVisibility(View.VISIBLE);
                    _viewPagerTab.setVisibility(View.GONE);
                    if (_newAppAdapater == null) {
                        FragmentPagerItems.Creator creator = FragmentPagerItems.with(getActivity());
                        AppCategoryData.Category categroy = new AppCategoryData.Category();
                        categroy.setLatest(0);//最新
                        categroy.setName(getString(R.string.app_new));
                        categroy.setId(-1);
                        List<AppCategoryData.Category> categories = new ArrayList<>();
                        categories.add(categroy);

                        creator.add(categroy.getName(), BaseAppCategoryFragment.class,
                                new Bundler().putSerializable(BaseAppCategoryFragment.KEY_APP_CATEGORY, categroy).get());

                        _newAppAdapater = new FragmentPagerItemAdapter(
                                this.getChildFragmentManager(), creator.create());
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable(BaseAppCategoryFragment.KEY_APP_CATEGORY, categroy);
//                        fragment.setArguments(bundle);
//                        _newAppList.add(fragment);
//                        _newAppAdapater = new CommonFragmentAdapter(getFragmentManager(), _newAppList);
                        _viewPager4.setAdapter(_newAppAdapater);
                        _viewPagerTab.setViewPager(_viewPager4);

                    } else {
//                    _viewPager.setAdapter(_newAppAdapater);
//                    _scrollableLayout.getHelper().setCurrentScrollableContainer(_newAppList.get(0));
//                        _viewPager.setAdapter(_newAppAdapater);
//                        _viewPagerTab.setViewPager(_viewPager);
                    }

                    //BaseAppCategoryFragment bacf1 = (BaseAppCategoryFragment) _newAppAdapater.getPage(0);
//                    View v1 = bacf1.getScrollableView();
//                    if (v1 == null) {
//                        v1 = bacf1.getScrollableView();
//                    }
//                    _scrollableLayout.getHelper().setScrollableView(v1);
                    _scrollableLayout.getHelper().setCurrentScrollableContainer(
                            (BaseAppCategoryFragment) _newAppAdapater.getPage(0));

                    break;
                default:
                    break;
            }

            for (int i = 0; i < _typeLLArray.size(); i++) {
                if (position == i) {
                    _typeLLArray.get(i).setSelected(true);
                } else {
                    _typeLLArray.get(i).setSelected(false);
                }
            }
        } catch (IndexOutOfBoundsException e) {

        }
    }

    public ScrollableLayout getScrollableLayout() {
        if (_scrollableLayout != null) {
            return _scrollableLayout;
        }
        return null;
    }


    List<AppCategoryData.Category> _listCategory;

    private void requestAppCategoryData() {

        CommonRestClient.getInstance(getContext()).getAppCharacters(new AbstractRestClient.ResponseCallBack() {

            @Override
            public void onFailure(Response resp, Throwable e) {
                String msg = getString(R.string.app_get_ad_error);
                CommonUtils.showResponseMessage(getActivity(), resp, e, R.string.app_get_ad_error);
                _progressRelativeLayout.showErrorText(msg);
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

                AppCategoryData appCategoryData = (AppCategoryData) resp.getModel();

                _listCategory = new ArrayList<>();

                if (appCategoryData.getData() != null && appCategoryData.getData().size() > 0) {
                    _listCategory = appCategoryData.getData();
                }

                for (int i = 0; i < _listCategory.size(); i++) {
                    _listCategory.get(i).setLatest(0);
                }

                _progressRelativeLayout.showContent();
                switchFragment(_position);

            }
        });

    }

//    private class MyBroadcast extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            switch (_position) {
//                case 0:
//                    ((BaseAppCategoryFragment)_hotAppList.get(0)).refresh();
//                    break;
//                case 1:
//                    ((BaseAppCategoryFragment)_rankAppAdapter.getPage(_indexPage)).refresh();
//                    break;
//                case 2:
//                    ((BaseAppCategoryFragment)_typeAppAdapter.getPage(_indexPage)).refresh();
//                    break;
//                case 3:
//                    ((BaseAppCategoryFragment)_newAppList.get(0)).refresh();
//                    break;
//            }
//
//        }
//    }

}
