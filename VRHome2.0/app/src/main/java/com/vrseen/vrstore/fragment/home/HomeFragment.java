package com.vrseen.vrstore.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.PageIndicator;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.app.AppDetailActivity;
import com.vrseen.vrstore.activity.film.FilmActivity;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.activity.panorama.Panorama1Activity;
import com.vrseen.vrstore.activity.panorama.PanoramaDetailActivity;
import com.vrseen.vrstore.activity.search.SearchActivity;
import com.vrseen.vrstore.adapter.banner.BannerAdapter;
import com.vrseen.vrstore.adapter.home.HomeGridAdapter;
import com.vrseen.vrstore.fragment.BaseFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.model.Home.HomeAdData;
import com.vrseen.vrstore.model.Home.HomeData;
import com.vrseen.vrstore.model.bannel.Banner;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.server.UsbdeviceReciver;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.ExpGridView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;
import com.vrseen.vrstore.view.swipemenulistview.DownRefreshListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.util.NetworkStatusManager;


/**
 * Created by jiangs on 16/4/29.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = "HomeFragment";
    public static final int TYPE_MOVIE = 2;
    public static final int TYPE_QUJIN = 1;
    public static final int TYPE_APP = 0;
    private View _thisFragment;
    private Button _searchButton;
    private ViewPager _bannerViewPager;
    private BannerAdapter _bannerAdapter;
    private HomeData _homeData;
    private final List<Banner> _banners = new ArrayList<Banner>();
    private LayoutInflater _layoutInflater;
    private ProgressRelativeLayout _progressRelativeLayout;
  // private RelativeLayout _progressRelativeLayout;
    private PageIndicator _bannerIndicator;
    private final Handler _handler = new Handler();

    private Context _context;

    private boolean _initData = false;
    private DownRefreshListView downRefreshListView;
    private LinearLayout  linearLayout;
    private final Runnable bannerChange = new Runnable() {
        @Override
        public void run() {
            _handler.postDelayed(this, VRHomeConfig.BANNER_CHANGE_INTERVAL);
            if (_banners.size() > 1 && _bannerViewPager != null) {
                int i = (_bannerViewPager.getCurrentItem() + 1) % _banners.size();
                _bannerViewPager.setCurrentItem(i, true);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.setPageName("HomeFragment");
        super.onCreateView(inflater, container, savedInstanceState);
        _context=getContext();
        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_home, null);
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

    private void initView() {
        downRefreshListView = (DownRefreshListView)_thisFragment.findViewById(R.id.home_listview);
        downRefreshListView.setOnRefreshListener(new DownRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downRefreshListView.refreshFinish();
            }

            @Override
            public void onLoadMore() {
                downRefreshListView.loadMoreFinish();
            }
        });
        _layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        _progressRelativeLayout = (ProgressRelativeLayout) _thisFragment.findViewById(R.id.progress_layout);


        _searchButton = (Button) _thisFragment.findViewById(R.id.btn_search);

        _searchButton.setOnClickListener(this);

    }

    private void initData()
    {
        if(_initData == true)
        {
            return;
        }

        if(!NetworkStatusManager.getInstance(this.getContext()).isNetworkConnectedHasMsg(false))
        {
            _progressRelativeLayout.showErrorText(getActivity().getString(R.string.get_data_fail));
            Toast.makeText(_context,getActivity().getString(R.string.get_data_fail),Toast.LENGTH_SHORT).show();
            return;
        }

       _progressRelativeLayout.showProgress();

        CommonRestClient.getInstance(getActivity()).getUserToken(getActivity(), new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(getActivity(), resp, e, getActivity().getString(R.string.get_data_fail));
                _progressRelativeLayout.showErrorText(getActivity().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {
                _initData = true;
                CommonRestClient.getInstance(getActivity()).saveToken(resp);
                requestHomeAD();
                requestHomeData();
                if(VRHomeConfig.CUR_RELEASE_TYPE != VRHomeConfig.ReleaseType.ZTE)
                    UserLogic.getInstance().initLogin(getActivity());
                else
                    MainActivity.instance.bindAuthService();
                UsbdeviceReciver.getInstance().set_context(_context);
            }
        });
    }


    private void requestHomeAD() {
        CommonRestClient.getInstance(getActivity()).getHomeAD(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {

            }

            @Override
            public void onSuccess(Response resp) throws JSONException {
                HomeAdData list = (HomeAdData) resp.getModel();

                View head = LayoutInflater.from(getContext()).inflate(R.layout.view_home_head,null);
                linearLayout = (LinearLayout) head.findViewById(R.id.rl);
                _bannerViewPager = (ViewPager) head.findViewById(R.id.banner_view_pager);
                _bannerIndicator = (PageIndicator) head.findViewById(R.id.indicator);

                for (int i = 0; i < list.getData().size(); i++) {
                    HomeAdData.HomeAd HomeAd = list.getData().get(i);
                    Banner banner = new Banner();
                    banner.setId(Integer.valueOf(HomeAd.getResource_id()));
                    banner.setImagurl(HomeAd.getImage().replace(" ", "%20"));
                    banner.setType(HomeAd.getType());
                    _banners.add(banner);
                }

                //显示默认图片
                //在初始化ViewPager时，应先给Adapter初始化内容后再将该adapter传给ViewPager，
                _bannerAdapter = new BannerAdapter(_thisFragment.getContext(), _banners);
                _bannerViewPager.setAdapter(_bannerAdapter);
                _bannerIndicator.setViewPager(_bannerViewPager);


                _bannerAdapter.setOnItemClickListener(new BannerAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Banner b) {

                        int type = Integer.valueOf(b.getType()) - 1;
                        switch (type) {
                            case HomeGridAdapter.TYPE_APP:

                                AppDetailActivity.actionStart(getContext(), b.getId());
                                break;

                            case HomeGridAdapter.TYPE_MOVIE:

                                FilmDetailData.PlayProgressData data = new FilmDetailData.PlayProgressData();
                                data.setId(b.getId());
                                data.setProValue(0);
                                FilmDetailActivity.actionStart(getContext(), data);

                                break;
                            case HomeGridAdapter.TYPE_QUJIN:

                                Intent intent = new Intent(getContext(), PanoramaDetailActivity.class);
                                intent.putExtra("panoramaId", b.getId());
                                getContext().startActivity(intent);

                                break;
                            default:
                                break;
                        }
                    }
                });

                downRefreshListView.addRollView(head);
            }
        });

    }


    /**
     * 请求首页动态数据
     */
    private void requestHomeData() {
        CommonRestClient.getInstance(getActivity()).getHomeData(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
               _progressRelativeLayout.showErrorText(getActivity().getString(R.string.get_data_fail));
                CommonUtils.showResponseMessage(getActivity(), resp, e, R.string.get_data_fail);
            }

            @Override
            public void onSuccess(Response resp) {
                _homeData = (HomeData) resp.getModel();
                if (_homeData != null) {
                    initGroupData(_homeData);
                }
                _progressRelativeLayout.showContent();
            }
        });
    }

    /**
     * 初始化类别数据
     *
     * @param _homeData
     */
    private void initGroupData(HomeData _homeData) {
        List<HomeData.GroupData> list = _homeData.getData();

        if (VRHomeConfig.CUR_RELEASE_TYPE == VRHomeConfig.ReleaseType.ZTE) {
            int len = list.size();
            for (int i = 0; i < len; i++) {
                //中兴首页不要应用
                if (list.get(i).getStyle() == HomeGridAdapter.TYPE_APP) {
                    list.remove(i);
                    break;
                }
            }
        }
        MyAdapter ma = new MyAdapter(list, _context);
        downRefreshListView.setAdapter(ma);
    }

    private class MyAdapter extends BaseAdapter{

        private List<HomeData.GroupData> _list;
        private Context _context;

        public MyAdapter(List<HomeData.GroupData> _list,Context context) {
            this._list = _list;
            this._context=context;
        }

        @Override
        public int getCount() {
            return _list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = _layoutInflater.inflate(R.layout.item_group_home, parent, false);
                holder.initView(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (_list.size() > 0)
                holder.fill(_list.get(position),convertView);
            return convertView;
        }

        class ViewHolder {

            ExpGridView gridView;
            TextView tv_type;


            public void initView(View convertView) {
                gridView = (ExpGridView) convertView.findViewById(R.id.gridview);
                tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            }

            public void fill(HomeData.GroupData hg,View convertView){
                final int style = hg.getStyle();

                //查看更多
                convertView.findViewById(R.id.tv_viewMore).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (style) {
                            case HomeGridAdapter.TYPE_MOVIE:
                                FilmActivity.actionStart(getContext());
                                break;
                            case HomeGridAdapter.TYPE_QUJIN:
                                startActivity(new Intent(getContext(), Panorama1Activity.class));
                                break;
                            case HomeGridAdapter.TYPE_APP:
                                ((MainActivity) getContext()).onTabChange(getContext().getString(R.string.tab_apps), 2);
                                break;
                            default:
                                break;
                        }
                    }
                });

                gridView.setVerticalSpacing(20);
                gridView.setHorizontalSpacing(20);
                if (style == TYPE_MOVIE) {
                    gridView.setNumColumns(3);
                } else if (style == TYPE_QUJIN) {
                    gridView.setNumColumns(2);
                } else if (style == TYPE_APP) {
                    gridView.setNumColumns(4);
                    gridView.setVerticalSpacing(50);

                }
                tv_type.setText(hg.getTitle() + "");
                HomeGridAdapter homeGridAdapter = new HomeGridAdapter(getActivity());
                homeGridAdapter.setGroupData(hg);
                gridView.setAdapter(homeGridAdapter);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("from", -1);
                getActivity().startActivity(intent);
                break;

            default:
                break;
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        _handler.postDelayed(bannerChange, VRHomeConfig.BANNER_CHANGE_INTERVAL);
        initData();

    }


    @Override
    public void onPause() {
        super.onPause();
        _handler.removeCallbacks(bannerChange);

    }



}
