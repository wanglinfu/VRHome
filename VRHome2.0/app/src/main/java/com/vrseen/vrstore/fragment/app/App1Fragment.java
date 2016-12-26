package com.vrseen.vrstore.fragment.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
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
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * 项目名称：VRHome2.0
 * 类描述：
 * 创建人：admin
 * 创建时间：2016/6/24 13:53
 * 修改人：admin
 * 修改时间：2016/6/24 13:53
 * 修改备注：
 */
public class App1Fragment extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private PtrClassicFrameLayout _ptrFrameLayout;
    private StoreHouseHeader _storeHouseHeader;
    private String _strLoadText = "VRSEEN";
    private ArrayList<LinearLayout> _typeLLArray = new ArrayList<>();
    private ViewPager _viewPager;
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
    private FragmentPagerItemAdapter _hotAppAdapater;
    private ArrayList<BaseCategoryFragment> _hotAppList = new ArrayList<>();
    private FragmentPagerItemAdapter _newAppAdapater;
    private ArrayList<BaseCategoryFragment> _newAppList = new ArrayList<>();

    private ProgressRelativeLayout _progressRelativeLayout;

    private Button _searchButton;

    private List<AppCategoryData.Category> _allCategoryList;
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        context = getContext();
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

        _allCategoryList = new ArrayList<>();

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

        _viewPager = (ViewPager) thisFragment.findViewById(R.id.app_viewpager);

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
        requestAppAdData();
        requestAppCategoryData();

    }

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
                AppBannerData appBannerData = (AppBannerData) resp.getModel();
                if (appBannerData != null) {
                    int lgh = appBannerData.getData().size();
                    for (int i = 0; i < lgh; i++) {
                        final AppBannerData.AppBanner app = appBannerData.getData().get(i);
                        if (i == 0) {
                            _cubeImageView1.loadImage(_imageLoader, app.getImage());
                            _cubeImageView1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppDetailActivity.actionStart(getActivity(), app.getApp_description_id());
                                }
                            });
                        }
                        if (i == 1) {
                            _cubeImageView2.loadImage(_imageLoader, app.getImage());
                            _cubeImageView2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppDetailActivity.actionStart(getActivity(), app.getApp_description_id());
                                }
                            });
                        }
                    }
                }
                _progressRelativeLayout.showContent();
            }
        });
    }

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

                List<AppCategoryData.Category> listCategory = new ArrayList<>();

                if (appCategoryData.getData() != null && appCategoryData.getData().size() > 0) {
                    listCategory = appCategoryData.getData();
                }

                initCategory(listCategory);

            }
        });

    }

    private List<AppCategoryData.Category> _rankList;

    private void initCategory(List<AppCategoryData.Category> list) {

        _allCategoryList.add(new AppCategoryData.Category(-1, "热门", 1));

        String[] rankApp = getActivity().getResources().getStringArray(R.array.app_rank);
        _rankList=new ArrayList<>();

        for (int i = 0; i < rankApp.length; i++) {
            AppCategoryData.Category category = new AppCategoryData.Category();
            category.setName(rankApp[i]);
            category.setId(-1);
            category.setLatest(i);
            _rankList.add(category);
        }
        _allCategoryList.addAll(_rankList);
        for(int i=0;i<list.size();i++){
            list.get(i).setLatest(0);
        }
        _allCategoryList.addAll(list);
        _allCategoryList.add(new AppCategoryData.Category(-1, "新品", 0));

        initViewPager();

    }

    private FragmentPagerItems.Creator _creator;

    private void initViewPager() {

        _creator = FragmentPagerItems.with(getActivity());

        for (AppCategoryData.Category category : _allCategoryList) {
                _creator.add(category.getName(), BaseAppCategoryFragment.class,
                        new Bundler().putSerializable(BaseAppCategoryFragment.KEY_APP_CATEGORY, category).get());
        }

        final FragmentPagerItemAdapter fragmentAdapter = new FragmentPagerItemAdapter(
                getActivity().getSupportFragmentManager(), _creator.create());

        _viewPager.setAdapter(fragmentAdapter);
        _viewPagerTab.setViewPager(_viewPager);

        switchFragment(0);

//        BaseAppCategoryFragment baseAppCategoryFragment=(BaseAppCategoryFragment)fragmentAdapter.getPage(0);
//        View view=baseAppCategoryFragment.getScrollableView();
//        if(view==null){
//            view=baseAppCategoryFragment.getScrollableView();
//        }else{
//            _scrollableLayout.getHelper().setScrollableView(view);
//        }
        _scrollableLayout.getHelper().setCurrentScrollableContainer((BaseAppCategoryFragment)fragmentAdapter.getPage(0));
        _viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position<_rankList.size()+1 && position>0){
                    _viewPagerTab.getTabAt(0).setVisibility(View.GONE);
                }
                BaseAppCategoryFragment baseAppCategoryFragment=(BaseAppCategoryFragment)fragmentAdapter.getPage(position);
                View view=baseAppCategoryFragment.getScrollableView();
                if(view==null){
                    view=baseAppCategoryFragment.getScrollableView();
                }else{
                    _scrollableLayout.getHelper().setScrollableView(view);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
    private class OnTypeChangeClickListener implements View.OnClickListener {

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
        MobclickAgent.onResume(context);       //统计时长

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MobclickAgent.onPause(context);

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {

        switchFragment(position);
    }

    private void switchFragment(int position) {

        switch (position){
            case 0:
                _viewPagerTab.getTabAt(0).setSelected(true);
                _viewPager.setCurrentItem(0);
                _viewPagerTab.setVisibility(View.GONE);
                break;
            case 1:
                _viewPagerTab.getTabAt(1).setSelected(true);
                _viewPager.setCurrentItem(1);
                _viewPagerTab.setVisibility(View.VISIBLE);
                break;
            case 2:
                _viewPagerTab.getTabAt(_rankList.size()+1).setSelected(true);
                _viewPager.setCurrentItem(_rankList.size()+1);
                _viewPagerTab.setVisibility(View.VISIBLE);
                break;
            case 3:
                _viewPagerTab.getTabAt(_allCategoryList.size()-1).setSelected(true);
                _viewPager.setCurrentItem(_allCategoryList.size()-1);
                _viewPagerTab.setVisibility(View.GONE);
                break;
        }

    }
}
