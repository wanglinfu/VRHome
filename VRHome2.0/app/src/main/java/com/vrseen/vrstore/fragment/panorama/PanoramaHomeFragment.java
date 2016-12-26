package com.vrseen.vrstore.fragment.panorama;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.PageIndicator;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.panorama.PanoramaCollectionActivity;
import com.vrseen.vrstore.activity.panorama.PanoramaCollectionDetailActivity;
import com.vrseen.vrstore.activity.panorama.PanoramaDetailActivity;
import com.vrseen.vrstore.adapter.banner.BannerAdapter;
import com.vrseen.vrstore.adapter.home.HomeGridAdapter;
import com.vrseen.vrstore.fragment.BaseFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.model.bannel.Banner;
import com.vrseen.vrstore.model.Home.HomeData;
import com.vrseen.vrstore.model.panorama.PanoramaBannerListData;
import com.vrseen.vrstore.model.panorama.PanoramaCategoryData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.ExpGridView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景资源全部页面
 * 创建人：郝晓辉
 * 创建时间：2016/6/1 9:22
 * 修改人：郝晓辉
 * 修改时间：2016/6/1 9:22
 * 修改备注：
 */
public class PanoramaHomeFragment extends BaseFragment implements View.OnClickListener {

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

    public static final String KEY_MODEL_CATEGORY = "KEY_MODEL_CATEGORY";
    private View _thisFragment;
    private LayoutInflater _layoutInflater;
    private PanoramaCategoryData.Category _category;
    private in.srain.cube.image.ImageLoader _imageLoader;

    private int _page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.setPageName("PanoramaHomeFragment");
        super.onCreateView(inflater, container, savedInstanceState);
        _context = getActivity();
        _category = (PanoramaCategoryData.Category) getArguments().get(KEY_MODEL_CATEGORY);
        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_panorama_all, null);
            initView();
        } else {
            // 从parent删除
            ViewGroup parent = (ViewGroup) _thisFragment.getParent();
            if (parent != null) {
                parent.removeView(_thisFragment);
            }
        }
        return _thisFragment;
    }


    private Context _context;
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
    /**
     * 小编推荐布局类型2（规则的gridview）
     */
    private ExpGridView _recommendType2;

    /**
     * 全景资源title
     */
    private TextView _titleTextView2;
    /**
     * 全景资源gridview
     */
    private ExpGridView _gridview;
    /**
     * 全景资源主页面
     */
    private ScrollView _scrollView;
    private int _index = 0;
    private int _iListCache = 0;
    private int _itemGridHeight = 0;
    /**
     * 全景资源广告页下方圆点
     */
    private PageIndicator _bannerIndicator;

    private TextView _loadMoreTextView;

    private ProgressRelativeLayout _progressRelativeLayout;

    private void initView() {

        _progressRelativeLayout = (ProgressRelativeLayout) _thisFragment.findViewById(R.id.progress_layout);
        _progressRelativeLayout.showProgress();

        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(getContext());
        handler.setLoadingResources(R.drawable.jiazaiguanggao);
        handler.setErrorResources(R.drawable.jiazaishibai_guanggao);
        _imageLoader = ImageLoaderFactory.create(getContext(), handler);

        //滑动广告页
        View head1 = _thisFragment.findViewById(R.id.view_panorama_head);
        _bannerViewPager = (ViewPager) head1.findViewById(R.id.banner_view_pager);
        _bannerIndicator = (PageIndicator) head1.findViewById(R.id.indicator);

        requestPanoramaBanner();

        _loadMoreTextView = (TextView) _thisFragment.findViewById(R.id.panorama_title_more);
        _loadMoreTextView.setOnClickListener(this);

        /**
         * 小编推荐部分
         */
        _titleTextView = (TextView) _thisFragment.findViewById(R.id.panorama_title_1);
        _searchMoreTextView = (TextView) _thisFragment.findViewById(R.id.panorama_title_more);

        _recommendType1 = (LinearLayout) _thisFragment.findViewById(R.id.panorama_recommend_type1);
        _recommendType2 = (ExpGridView) _thisFragment.findViewById(R.id.panorama_recommend_type2);

        /**
         * 全景资源部分
         */
        _titleTextView2 = (TextView) _thisFragment.findViewById(R.id.panorama_title_3);

        _gridview = (ExpGridView) _thisFragment.findViewById(R.id.gridview);
        _gridview.setFocusable(false);
        _gridview.setVerticalSpacing(20);
        _gridview.setHorizontalSpacing(20);

        _gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        _scrollView = (ScrollView) _thisFragment.findViewById(R.id.scrollView);

        requestRecommends();


//        scrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                float y1=0;
//                float y2=1;
//
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN :
//                        y1=event.getY();
//                        break;
//                    case MotionEvent.ACTION_MOVE :
//
//                        break;
//
//                    default :
//                        break;
//                }
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    y2=event.getY();
//                    if(y2-y1>0){
//                        Toast.makeText(getContext(),y2+" "+y1,Toast.LENGTH_SHORT).show();
//                    }else
//
//                    if(y2-y1<0){
//                        Toast.makeText(getContext(),y2+" "+y1,Toast.LENGTH_SHORT).show();
//                    }
//                }
//                return false;
//            }
//        });

    }

    /**
     * 全景广告页
     */
    private PanoramaBannerListData panoramaBannerListData;

    /**
     * 请求广告数据
     */
    private void requestPanoramaBanner() {
        PanoramaRestClient.getInstance(getActivity()).getBannerData(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(getActivity(), resp, e, "获取数据失败");
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {


                JSONObject jo = (JSONObject) resp.getData();

                String str = jo.getString("heads");

                Gson g = new Gson();
                List<PanoramaBannerListData.PanoramaBanner> listPanoramaBannerData = g.fromJson(str, new TypeToken<List<PanoramaBannerListData.PanoramaBanner>>() {
                }.getType());

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

        _bannerAdapter = new BannerAdapter(_thisFragment.getContext(), _banners);
        _bannerViewPager.setAdapter(_bannerAdapter);
        _bannerViewPager.getParent().requestDisallowInterceptTouchEvent(true);
        _bannerIndicator.setViewPager(_bannerViewPager);
        _bannerAdapter.setOnItemClickListener(new BannerAdapter.OnItemClickListener() {
            @Override
            public void onClick(Banner b) {
                if (b.getType().equals("2")) {
                    Intent intent = new Intent(getActivity(), PanoramaDetailActivity.class);
                    intent.putExtra("panoramaId", b.getId());
                    getContext().startActivity(intent);
                } else if (b.getType().equals("4")) {
                    Intent intent = new Intent(getActivity(), PanoramaCollectionDetailActivity.class);
                    intent.putExtra("listid", b.getId());
                    getContext().startActivity(intent);
                }
            }
        });
    }

    /**
     * 请求动态栏数据
     */
    private void requestRecommends() {

        PanoramaRestClient.getInstance(getActivity()).getRecommendData(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(getActivity(), resp, e, "获取数据失败");
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

                JSONObject jo = (JSONObject) resp.getData();

                String str = jo.getString("data");

                Gson g = new Gson();

                List<HomeData.GroupData> listPanoramaRecommend = g.fromJson(str, new TypeToken<List<HomeData.GroupData>>() {
                }.getType());

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

            } else if (pr.getStyle() == 1) {

                initRecommendBottom(pr);

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

        for (int i = 0; i < recommendTops.length; i++) {
            final HomeData.GroupData.Item item = listItem.get(i);
            TextView tv = (TextView) recommendTops[i].findViewById(R.id.tv_title);
            CubeImageView iv_cover = (CubeImageView) recommendTops[i].findViewById(R.id.iv_cover);

            tv.setText(listItem.get(i).getTitle());
            iv_cover.loadImage(_imageLoader, listItem.get(i).getPic());

            recommendTops[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (panoramaRecommend.getType() == 4) {
                        Intent intent = new Intent(getContext(), PanoramaCollectionDetailActivity.class);
                        intent.putExtra("listid", item.getId());
                        getContext().startActivity(intent);
                    } else if (panoramaRecommend.getType() == 2) {
                        Intent intent = new Intent(getContext(), PanoramaDetailActivity.class);
                        intent.putExtra("panoramaId", item.getId());
                        getContext().startActivity(intent);
                    }
                }
            });

        }


    }

    /**
     * 动态下半部分
     *
     * @param panoramaRecommend
     */
    private void initRecommendBottom(HomeData.GroupData panoramaRecommend) {
        _titleTextView2.setText(panoramaRecommend.getTitle());

        _gridview.setNumColumns(2);

        HomeGridAdapter homeGridAdapter = new HomeGridAdapter(getActivity());
        homeGridAdapter.setGroupData(panoramaRecommend);
        _gridview.setAdapter(homeGridAdapter);

        _progressRelativeLayout.showContent();

    }


    @Override
    public void onClick(View v) {

        int vid = v.getId();
        switch (vid) {
            case R.id.panorama_title_more:

                Intent intent = new Intent(_thisFragment.getContext(), PanoramaCollectionActivity.class);
                startActivity(intent);

                break;
        }

    }

   /* @Override
    public void onResume() {
        super.onResume();
        _mHander.postDelayed(_bannerChange, VRHomeConfig.BANNER_CHANGE_INTERVAL);

    }

    @Override
    public void onPause() {
        super.onPause();
        _mHander.removeCallbacks(_bannerChange);
    }*/
   public void onResume() {
       super.onResume();
       _mHander.postDelayed(_bannerChange, VRHomeConfig.BANNER_CHANGE_INTERVAL);
       MobclickAgent.onResume(_context);       //统计时长
   }
    public void onPause() {
        super.onPause();
        _mHander.removeCallbacks(_bannerChange);
        MobclickAgent.onPause(_context);
    }

    public void refresh() {
        _category = (PanoramaCategoryData.Category) getArguments().get(KEY_MODEL_CATEGORY);
        requestRecommends();
    }


}
