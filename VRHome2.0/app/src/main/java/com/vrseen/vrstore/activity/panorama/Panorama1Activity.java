package com.vrseen.vrstore.activity.panorama;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.viewpagerindicator.PageIndicator;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.activity.search.SearchActivity;
import com.vrseen.vrstore.adapter.banner.BannerAdapter;
import com.vrseen.vrstore.adapter.panorama.PanoramaAllTypeAdapter;
import com.vrseen.vrstore.fragment.panorama.PanoramaHomeFragment;
import com.vrseen.vrstore.fragment.panorama.PanoramaListFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.SearchRestClient;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.model.Home.HomeData;
import com.vrseen.vrstore.model.bannel.Banner;
import com.vrseen.vrstore.model.panorama.PanoramaAllTypeData;
import com.vrseen.vrstore.model.panorama.PanoramaBannerListData;
import com.vrseen.vrstore.model.panorama.PanoramaCategoryData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.ProgressRelativeLayout;
import com.vrseen.vrstore.view.ScrollableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.util.NetworkStatusManager;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

public class Panorama1Activity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener,View.OnTouchListener {

    public static final String KEY_MODEL_CATEGORY = "KEY_MODEL_CATEGORY";
    /**
     * 广告滑动控制
     */
    private final Handler _mHander = new Handler();
    private final Runnable _bannerChange = new Runnable() {
        @Override
        public void run() {
            _mHander.postDelayed(this, VRHomeConfig.BANNER_CHANGE_INTERVAL);
            if (_banners.size() > 1 && _bannerViewPager != null) {
                int i = (_bannerViewPager.getCurrentItem() + 1) % _banners.size();
                _bannerViewPager.setCurrentItem(i, true);
            }
        }
    };

    private LayoutInflater _layoutInflater;
    private PanoramaCategoryData.Category _category;
    private in.srain.cube.image.ImageLoader _imageLoader;

    private int _page = 1;

    /**
     * 城市选择request（startActivityForResult）
     */
    private static final int CITY_SELECT = 0;

    /**
     * 全景资源广告页下方圆点
     */
    private PageIndicator _bannerIndicator;

    private TextView _loadMoreTextView;
    /**
     * 进入页面时的loading进度条
     */
    private ProgressRelativeLayout _progressRelativeLayout;

    /**
     * 全景资源title
     */
    private TextView _titleTextView2;

    /**
     * 广告栏viewpager
     */
    private ViewPager _bannerViewPager;
    /**
     * 广告栏adapter
     */
    private BannerAdapter _bannerAdapter;
    /**
     * 广告栏图片列表
     */
    private final List<Banner> _banners = new ArrayList<Banner>();

    /**
     * 小编推荐标题
     */
    private TextView _titleTextView;

    /**
     * 查看更多按钮
     */
    private TextView _searchMoreTextView;

    /**
     * 小编推荐布局类型1（上2下1）
     */
    private LinearLayout _recommendType1;

    private Context _context;
    /**
     * 搜索按钮（右上角）
     */
    private Button _searchButton;

    /**
     * 返回键
     */
    private View _view_back;
    /**
     * 标签栏
     */
    private SmartTabLayout _viewPagerTab;
    /**
     * 滑动控件
     */
    private ScrollableLayout _scrollableLayout;

    private PtrClassicFrameLayout _ptrFrameLayout;
    private StoreHouseHeader _storeHouseHeader;
    private String _strLoadText = "VRSEEN";
    private ViewPager _viewPager;

    /**
     * 当前城市
     */
    private static String _currentCity = "杭州";
    private static String _currentCityId = "19";
    /**
     * 全景分类
     */
    private PanoramaCategoryData _panoramaCateroryData;
    private List<PanoramaCategoryData.Category> _categories;

    /**
     * 城市选择
     */
    private LinearLayout _citySelectLinearLayout;
    private TextView _cityTextView;
    /**
     * 列表fragment
     */
    private FragmentPagerItemAdapter _fragmentAdapter;

    private int _viewPagerIndex = 0;

    private ImageButton float_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("PanoramaActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama_1);

        _context = this;

        _categories = new ArrayList<>();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenwidth = dm.widthPixels;
        screenheight = dm.heightPixels - 130;
//        toFork = AnimationUtils.loadAnimation(this, R.anim.rotate_to_fork);
//        toNormal = AnimationUtils.loadAnimation(this, R.anim.rotate_to_normal);

        _types = new ArrayList<>();
        _types.add(new PanoramaAllTypeData("-1", getResources().getString(R.string.film_all_film)));

        _citySelectLinearLayout = (LinearLayout) findViewById(R.id.ll_panorama_city);
        _cityTextView = (TextView) findViewById(R.id.tv_panorama_city);
        _citySelectLinearLayout.setOnClickListener(this);
        _cityTextView.setText(_currentCity);

        initHeadView();

        initContentView();
        initAllType();
        _searchButton = (Button) findViewById(R.id.btn_search);
        _searchButton.setOnClickListener(this);
        _view_back = findViewById(R.id.view_back);
        _view_back.setOnClickListener(this);

        float_button = (ImageButton) findViewById(R.id.float_button);
        float_button.setOnClickListener(this);
        float_button.setOnTouchListener(this);

    }


    /**
     * 请求分类数据
     * @param currentCityId 当前城市ID
     */
    private void requestPanoramaCategory(String currentCityId) {
        PanoramaRestClient.getInstance(this).getPanoramaCategory(currentCityId, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(Panorama1Activity.this, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) {

                _panoramaCateroryData = (PanoramaCategoryData) resp.getModel();

                if (_panoramaCateroryData != null && _panoramaCateroryData.getData() != null && _panoramaCateroryData.getData().size() > 0) {

                    _categories.clear();

                    _categories.add(new PanoramaCategoryData.Category(-1, getResources().getString(R.string.film_all_film)));

                    _categories.addAll(_panoramaCateroryData.getData());

                    initFragment();

                }

            }
        });
    }

    /***
     * 初始化下方列表页
     */
    private void initFragment() {

        FragmentPagerItems.Creator creator = FragmentPagerItems.with(Panorama1Activity.this);

        for (PanoramaCategoryData.Category category : _categories) {

//            if(category.getCate().equals("全部")){
//                String allId="";
//                for(int i=0;i<_categories.size();i++){
//                    if(_categories.get(i).getId()!=-1) {
//                        if (i == _categories.size() - 1) {
//                            allId = allId + _categories.get(i).getId();
//                        } else {
//                            allId = allId + _categories.get(i).getId() + ",";
//                        }
//                    }
//                }
//                category.setAllId(allId);
//            }

            creator.add(category.getCate(), PanoramaListFragment.class,
                    new Bundler().putSerializable(PanoramaListFragment.KEY_MODEL_CATEGORY, category).get());

        }

        _fragmentAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), creator.create());

        _viewPager.setAdapter(_fragmentAdapter);
        _viewPagerTab.setViewPager(_viewPager);

        _viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                _viewPagerIndex = position;
                PanoramaListFragment p = (PanoramaListFragment) _fragmentAdapter.getPage(_viewPagerIndex);
                GridViewWithHeaderAndFooter g = p.getGridView();
                if (g == null) {
                    g = p.getGridView();
                }
                _scrollableLayout.getHelper().setScrollableView(g);
                p.refresh(_currentAllTypeId, _context);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 初始化标签栏及列表页控件
     */
    private void initContentView() {
        _viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        _scrollableLayout = (ScrollableLayout) findViewById(R.id.app_scrollview);
        _ptrFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.store_app_ptr_frame);
        if (_storeHouseHeader == null) {
            _storeHouseHeader = new StoreHouseHeader(_context);
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

        _viewPager = (ViewPager) findViewById(R.id.app_viewpager);

        requestPanoramaCategory(_currentCityId);

    }

    private LinearLayout _headerLinearLayout;

    /**
     * 初始化头部数据（广告栏、三个专辑推荐）
     */
    private void initHeadView() {

        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);
        _progressRelativeLayout.showProgress();

        if (!NetworkStatusManager.getInstance(_context).isNetworkConnectedHasMsg( false)) {
            _progressRelativeLayout.showErrorText(getResources().getString(R.string.get_data_fail));
            return;
        }

        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(_context);
        handler.setLoadingResources(R.drawable.jiazaiguanggao);
        handler.setErrorResources(R.drawable.jiazaishibai_guanggao);
        _imageLoader = ImageLoaderFactory.create(_context, handler);

        //滑动广告页
        View head1 = findViewById(R.id.view_panorama_head);
        _bannerViewPager = (ViewPager) head1.findViewById(R.id.banner_view_pager);
        _bannerIndicator = (PageIndicator) head1.findViewById(R.id.indicator);
        requestPanoramaBanner();

        /**
         * 小编推荐部分
         */
        _loadMoreTextView = (TextView) findViewById(R.id.panorama_title_more);
        _loadMoreTextView.setOnClickListener(this);
        _titleTextView = (TextView) findViewById(R.id.panorama_title_1);
        _searchMoreTextView = (TextView) findViewById(R.id.panorama_title_more);
        _recommendType1 = (LinearLayout) findViewById(R.id.panorama_recommend_type1);

        _headerLinearLayout = (LinearLayout) findViewById(R.id.ll_header);

        requestRecommends();

    }

    /**
     * 请求广告数据
     */
    private void requestPanoramaBanner() {
        PanoramaRestClient.getInstance(_context).getBannerData(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

                PanoramaBannerListData panoramaBannerListData = (PanoramaBannerListData) resp.getModel();

                List<PanoramaBannerListData.PanoramaBanner> listPanoramaBannerData = new ArrayList<>();
                if (panoramaBannerListData.getData() != null && panoramaBannerListData.getData().size() > 0) {
                    listPanoramaBannerData = panoramaBannerListData.getData();
                }

                initBanner(listPanoramaBannerData);

            }
        });
    }

    /**
     * 加载广告数据
     *
     * @param listPanoramaBannerData 广告数据
     */
    private void initBanner(final List<PanoramaBannerListData.PanoramaBanner> listPanoramaBannerData) {

        for (int i = 0; i < listPanoramaBannerData.size(); i++) {
            PanoramaBannerListData.PanoramaBanner pbd = listPanoramaBannerData.get(i);
            if (pbd.getGroup().equals("HEAD")) {
                Banner b = new Banner();
                b.setImagurl(pbd.getImage());
                b.setId(pbd.getResource().getId());
                b.setType(pbd.getType());
                _banners.add(b);

            }
        }

        _bannerAdapter = new BannerAdapter(_context, _banners);
        _bannerViewPager.setAdapter(_bannerAdapter);
        _bannerViewPager.getParent().requestDisallowInterceptTouchEvent(true);
        _bannerIndicator.setViewPager(_bannerViewPager);
        _bannerAdapter.setOnItemClickListener(new BannerAdapter.OnItemClickListener() {
            @Override
            public void onClick(Banner b) {
                if (b.getType().equals("2")) {
                    Intent intent = new Intent(_context, PanoramaDetailActivity.class);
                    intent.putExtra("panoramaId", b.getId());
                    _context.startActivity(intent);
                } else if (b.getType().equals("4")) {
                    Intent intent = new Intent(_context, PanoramaCollectionDetailActivity.class);
                    intent.putExtra("listid", b.getId());
                    startActivity(intent);
                }
            }
        });
        _progressRelativeLayout.showContent();
    }

    /**
     * 请求动态栏数据
     */
    private void requestRecommends() {

        PanoramaRestClient.getInstance(_context).getRecommendData(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e,getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {
                HomeData homeData = (HomeData) resp.getModel();

                List<HomeData.GroupData> listPanoramaRecommend = new ArrayList<>();
                if (homeData.getData() != null && homeData.getData().size() > 0) {
                    listPanoramaRecommend = homeData.getData();
                }

                initRecommend(listPanoramaRecommend);
            }
        });
    }

    /**
     * 加载动态栏数据
     *
     * @param listPanoramaRecommend 动态栏数据
     */

    private void initRecommend(List<HomeData.GroupData> listPanoramaRecommend) {
        for (int i = 0; i < listPanoramaRecommend.size(); i++) {
            HomeData.GroupData pr = listPanoramaRecommend.get(i);
            if (pr.getStyle() == 3) {
                initRecommendTop(pr);
            }
        }
    }

    /**
     * 动态上半部分（小编推荐）
     *
     * @param panoramaRecommend
     */
    private void initRecommendTop(final HomeData.GroupData panoramaRecommend) {
        _titleTextView.setText(panoramaRecommend.getTitle());
        final List<HomeData.GroupData.Item> listItem = panoramaRecommend.getItems();
        View recommendTop1 = _recommendType1.findViewById(R.id.panorama_recommend1);
        View recommendTop2 = _recommendType1.findViewById(R.id.panorama_recommend2);
        View recommendTop3 = _recommendType1.findViewById(R.id.panorama_recommend3);
        View[] recommendTops = new View[]{recommendTop1, recommendTop2, recommendTop3};

        for (int i = 0; i < listItem.size(); i++) {
            final HomeData.GroupData.Item item = listItem.get(i);
            TextView tv = (TextView) recommendTops[i].findViewById(R.id.tv_title);
            CubeImageView iv_cover = (CubeImageView) recommendTops[i].findViewById(R.id.iv_cover);
            tv.setText(listItem.get(i).getTitle());
            BitmapUtils bu=new BitmapUtils(_context);
            bu.display(iv_cover,listItem.get(i).getPic());
            recommendTops[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (panoramaRecommend.getType() == 4) {
                        Intent intent = new Intent(_context, PanoramaCollectionDetailActivity.class);
                        intent.putExtra("listid", String.valueOf(item.getId()));
                        startActivity(intent);
//                    } else if (panoramaRecommend.getType() == 2) {
//                        Intent intent = new Intent(_context, PanoramaDetailActivity.class);
//                        intent.putExtra("panoramaId", item.getId());
//                        startActivity(intent);
//                    }
                }
            });
            _progressRelativeLayout.showContent();
        }
    }

    //判断改变悬浮按钮旋转动画
    private boolean turned = false;
    private boolean ismoving = false;
    private boolean movedLater = false;//true=刚刚移动过
    //获取手机的宽高
    private int screenwidth;
    private int screenheight;
    private int lastX;
    private int lastY;

    @Override
    public void onClick(View v) {

        int vid = v.getId();
        switch (vid) {
            case R.id.panorama_title_more:

                Intent intent = new Intent(_context, PanoramaCollectionActivity.class);
                startActivity(intent);

                break;
            case R.id.btn_search:
                Intent intent1 = new Intent(_context, SearchActivity.class);
                intent1.putExtra("from", SearchRestClient.PANORAMA_TAG);
                startActivity(intent1);
                break;
            case R.id.view_back:
                finish();
                break;
            case R.id.ll_panorama_city:
                Intent intent2 = new Intent(this, CitySelectActivity.class);
                startActivityForResult(intent2, CITY_SELECT);
                break;
            case R.id.float_button:
                if (!ismoving) {
                    if (movedLater) {
                        movedLater = false;
                        return;
                    }
                    //float_button.startAnimation(toFork);
                    U3dMediaPlayerLogic.getInstance().comeinVR(this,"loadlevel|Scene_Home|5");
//
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CITY_SELECT:

                if (resultCode == 0) {
                    String citySelect = data.getExtras().getString("city_name");
                    if (citySelect != null) {
                        _currentCity = data.getExtras().getString("city_name");
                        _currentCityId = data.getExtras().getString("city_id");
                        _cityTextView.setText(_currentCity);
                        startActivity(new Intent(_context, Panorama1Activity.class));
                        finish();
//                        requestPanoramaCategory(_currentCityId);

                    }
                }

                break;
        }
    }

    public static String getCityId() {
        return _currentCityId;
    }//获取城市ID

    /**
     * 获取全部类型，列表fragment中需要即使获取类型数据
     * @return  全部类型ID
     */
    public static String getAllTypeId() {
        return _currentAllTypeId;
    }//获取

    public void onResume() {
        super.onResume();
        _mHander.postDelayed(_bannerChange, VRHomeConfig.BANNER_CHANGE_INTERVAL);

    }
    public void onPause() {
        super.onPause();

        _mHander.removeCallbacks(_bannerChange);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 返回可滑动的scrollablelayout
     * @return  activity中初始化的scrollablelayout
     */
    public ScrollableLayout getScrollableLayout() {
        if (_scrollableLayout != null)
            return _scrollableLayout;
        return null;
    }

    private static String _currentAllTypeId = "-1";
    private GridView _panoramaAllTypeGridView;
    private List<PanoramaAllTypeData> _types;

    /**
     * 初始化全部类型（视频、图片等）
     */
    private void initAllType() {
        _panoramaAllTypeGridView = (GridView) findViewById(R.id.gv_panorama_all_type);
        _panoramaAllTypeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemSelect(_panoramaAllTypeGridView, position);
                int index = _viewPager.getCurrentItem();
                _currentAllTypeId = _types.get(position).getApi_tag();
                Fragment fragment = _fragmentAdapter.getPage(index);//获取当前页的fragment
                if (fragment instanceof PanoramaHomeFragment) {
                } else if (fragment instanceof PanoramaListFragment) {
                    ((PanoramaListFragment) fragment).refresh(_currentAllTypeId, _context);
                }
            }
        });

        requestPanoramaAllType(_currentCityId);

    }

    /**
     * 设置item选中状态
     * @param gridView
     * @param position  选中的位置
     */
    private void itemSelect(GridView gridView, int position) {
        int count = gridView.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            if (i == position) {
                ((TextView) gridView.getChildAt(i).findViewById(R.id.tv_city_item_name)).setTextColor(getResources().getColor(R.color.common_app_color));
            } else {
                ((TextView) gridView.getChildAt(i).findViewById(R.id.tv_city_item_name)).setTextColor(getResources().getColor(R.color.contentColor));
            }
        }
    }

    /**
     * 请求全部类型数据
     * @param currentCityId 当前城市id
     */
    private void requestPanoramaAllType(String currentCityId) {
        PanoramaRestClient.getInstance(this).getPanoramaAllType(currentCityId, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {


                JSONObject jo = (JSONObject) resp.getData();

                String str = jo.getString("data");

                Gson g = new Gson();

                List<PanoramaAllTypeData> types = g.fromJson(str, new TypeToken<List<PanoramaAllTypeData>>() {
                }.getType());
                _types.addAll(types);

                PanoramaAllTypeAdapter panoramaAllTypeAdapter = new PanoramaAllTypeAdapter(_context, _types);
                _panoramaAllTypeGridView.setNumColumns(_types.size());
                _panoramaAllTypeGridView.setAdapter(panoramaAllTypeAdapter);

            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //获取控件一开始的位置
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                ismoving = false;
                break;

            case MotionEvent.ACTION_MOVE:
                int det = 10;//小于此偏移量都当没有移动
                //获取移动的距离
                int dx = (int) (event.getRawX() - lastX);
                int dy = (int) (event.getRawY() - lastY);
                if (Math.abs(dx) < det && Math.abs(dy) < det) {//当只是移动没有点击时，无需变换悬浮按钮的状态
                    //如果移动时正好时叉号状态，变回原样
                    ismoving = false;
                } else {
                    movedLater = true;
                    ismoving = true;
                    if (turned) {
                        turned = false;
                        //float_button.startAnimation(toNormal);
                    }
                }
                //getLeft()方法得到的是控件坐标距离父控件原点(左上角，坐标（0，0）)的x轴距离，
                //getRight()是控件右边距离父控件原点的x轴距离，同理，getTop和getButtom是距离的y轴距离。
                int left = v.getLeft() + dx;
                int right = v.getRight() + dx;
                int top = v.getTop() + dy;
                int bottom = v.getBottom() + dy;

                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }

                if (right > screenwidth) {
                    right = screenwidth;
                    left = right - v.getWidth();
                }

                if (top < 0) {
                    top = 0;
                    bottom = top + v.getHeight();
                }

                if (bottom > screenheight) {
                    bottom = screenheight;
                    top = bottom - v.getHeight();
                }

                //到达边界后不能再移动
                v.layout(left, top, right, bottom);
                //Log.i("@@@@@@", "position:" + left + ", " + top + ", " + right + ", " + bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //Log.i("lastx ,lasty", "lastx:"+lastX+"lasty:"+lastY);
                break;

            case MotionEvent.ACTION_UP:

                int detUp = 10;//小于此偏移量都当没有移动
                //获取移动的距离
                int dxUp = (int) (event.getRawX() - lastX);
                int dyUp = (int) (event.getRawY() - lastY);

                int leftUp = v.getLeft() + dxUp;
                int rightUp = v.getRight() + dxUp;
                int topUp = v.getTop() + dyUp;
                int bottomUp = v.getBottom() + dyUp;

//                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v,"translationX", 0);

                if (leftUp <= screenwidth / 2) {

                    ObjectAnimator.ofFloat(v, "translationX",leftUp, 0f).setDuration(300).start();
                    leftUp = 0;
                    rightUp = leftUp + v.getWidth();

                }

                if (rightUp > screenwidth / 2) {

                    rightUp = screenwidth;
                    leftUp = rightUp - v.getWidth();
                    ObjectAnimator.ofFloat(v, "translationX",-360f, 0f).setDuration(300).start();

                }

                if (topUp < 0) {
                    topUp = 0;
                    bottomUp = topUp + v.getHeight();
                }

                if (bottomUp > screenheight) {
                    bottomUp = screenheight;
                    topUp = bottomUp - v.getHeight();
                }
//                if (leftUp <= screenwidth / 2) {
//                    animation = new TranslateAnimation(leftUp, 0, 0, 0);
//                } else {
//
//                    animation = new TranslateAnimation(leftUp, screenwidth, 0, 0);
//                }
//
//                animation.setDuration(500);
//                v.startAnimation(animation);
                v.layout(leftUp, topUp, rightUp, bottomUp);
                //Log.i("@@@@@@", "position:" + left + ", " + top + ", " + right + ", " + bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //Log.i("lastx ,lasty", "lastx:"+lastX+"lasty:"+lastY);

                break;
        }

        return false;
    }
}
