package com.vrseen.vrstore.fragment.panorama;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.panorama.Panorama1Activity;
import com.vrseen.vrstore.activity.panorama.PanoramaActivity;
import com.vrseen.vrstore.activity.panorama.PanoramaDetailActivity;
import com.vrseen.vrstore.adapter.panorama.PanoramaListAdapter;
import com.vrseen.vrstore.fragment.BaseFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.model.panorama.PanoramaCategoryData;
import com.vrseen.vrstore.model.panorama.PanoramaListData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.view.LoadMoreFooterView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;
import com.vrseen.vrstore.view.ScrollableHelper;
import com.vrseen.vrstore.view.ScrollableLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景资源列表界面
 * 创建人：郝晓辉
 * 创建时间：2016/6/2 9:23
 * 修改人：郝晓辉
 * 修改时间：2016/6/2 9:23
 * 修改备注：
 */
public class PanoramaListFragment extends BaseFragment implements View.OnClickListener {

    private PanoramaCategoryData.Category _category;
    public static final String KEY_MODEL_CATEGORY = "KEY_MODEL_CATEGORY";
    private View _thisFragment;

    private LayoutInflater _inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.setPageName("PanoramaListFragment");
        super.onCreateView(inflater, container, savedInstanceState);
        _category = (PanoramaCategoryData.Category) getArguments().get(KEY_MODEL_CATEGORY);
        _context = getActivity();
        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_panorama_list, null);
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

    /**
     * 请求数据页数
     */
    private int _page = 1;
    /**
     * 每页的数据数量
     */
    private int _limit = 10;
    /**
     * 0为视频，1为图片，2为3D，3为漫游，空为全部
     */
    private int _type = 1;
//    /**
//     * 排序 1-推荐，2-最新，3-热门,4-评分
//     */
//    private int sort=1;
//
    /**
     * 城市的ID
     */
    private int cityId = 0;
    /**
     * 分类id,如5A,古典园林，山水风光。。
     */
    private int cid = 0;
    /**
     * 集合id
     */
    private int listId = 1;

    private PanoramaListAdapter _panoramaListAdapter;

    private List<PanoramaListData.PanoramaList> _listPanorama;

    /**
     * gridview为空时显示的视图
     */
    private View _emptyView;

    private ProgressRelativeLayout _progressRelativeLayout;

    private LoadMoreGridViewContainer _loadMoreListViewContainer;
    private GridViewWithHeaderAndFooter _gridViewWithHeaderAndFooter;

    private Context _context;

    /**
     * 初始化界面
     */
    private void initView() {

        _context = getContext();

        _progressRelativeLayout = (ProgressRelativeLayout) _thisFragment.findViewById(R.id.progress_layout);

        _listPanorama = new ArrayList<>();

        _inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _emptyView = getActivity().getLayoutInflater().inflate(R.layout.no_data_layout, null);
        ((TextView) _emptyView.findViewById(R.id.tv_nodata)).setText("暂时没有数据");
        _emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // 获取view的引用
        _loadMoreListViewContainer = (LoadMoreGridViewContainer) _thisFragment.findViewById(R.id.load_more_grid_view_container);

        _gridViewWithHeaderAndFooter = (GridViewWithHeaderAndFooter) _thisFragment.findViewById(R.id.load_more_grid_view);

        _gridViewWithHeaderAndFooter.setNumColumns(2);
        _gridViewWithHeaderAndFooter.setHorizontalSpacing(10);
        _gridViewWithHeaderAndFooter.setVerticalSpacing(10);

        _gridViewWithHeaderAndFooter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), PanoramaDetailActivity.class);
                intent.putExtra("panoramaId", _listPanorama.get(position).getId());
                getContext().startActivity(intent);
            }
        });

        // 创建
        LoadMoreFooterView footerView = new LoadMoreFooterView(_loadMoreListViewContainer.getContext());

        // 设置
        _loadMoreListViewContainer.setLoadMoreView(footerView);
        _loadMoreListViewContainer.setLoadMoreUIHandler(footerView);

        _loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 请求下一页数据
                _page++;
                RequestParams reqpestParams = new RequestParams();

                if (_category.getId() != -1) {
                    reqpestParams.put("page", _page);
                    reqpestParams.put("limit", _limit);
                    if (!Panorama1Activity.getAllTypeId().equals("-1")) {
                        reqpestParams.put("type", Panorama1Activity.getAllTypeId());
                    }
                    reqpestParams.put("cityid", Panorama1Activity.getCityId());
                    reqpestParams.put("cid", _category.getId());
                    reqpestParams.put("channelid", 18);
                } else {
                    reqpestParams.put("page", _page);
                    reqpestParams.put("limit", _limit);
                    if (!Panorama1Activity.getAllTypeId().equals("-1")) {
                        reqpestParams.put("type", Panorama1Activity.getAllTypeId());
                    }
                    reqpestParams.put("cityid", Panorama1Activity.getCityId());
                    reqpestParams.put("channelid", 18);
                }
                requestList(reqpestParams, _context, false);

            }

        });

        RequestParams reqpestParams = new RequestParams();
        if (_category.getId() != -1) {
            reqpestParams.put("page", _page);
            reqpestParams.put("limit", _limit);
            if (!Panorama1Activity.getAllTypeId().equals("-1")) {
                reqpestParams.put("type", Panorama1Activity.getAllTypeId());
            }
            reqpestParams.put("cityid", PanoramaActivity.getCityId());
            reqpestParams.put("cid", _category.getId());
            reqpestParams.put("channelid", 18);
        } else {
            reqpestParams.put("page", _page);
            reqpestParams.put("limit", _limit);
            if (!Panorama1Activity.getAllTypeId().equals("-1")) {
                reqpestParams.put("type", Panorama1Activity.getAllTypeId());
            }
            reqpestParams.put("cityid", Panorama1Activity.getCityId());
            reqpestParams.put("channelid", 18);
        }
        requestList(reqpestParams, _context, false);

    }

    /**
     * 请求列表数据
     */
    private void requestList(RequestParams requestParams, Context context, final boolean isRefresh) {

        Log.e("xxxxxxxxxxxxx", "111111111111");

        PanoramaRestClient.getInstance(context).getListData(requestParams, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(getActivity(), resp, e, "获取数据失败");
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

                PanoramaListData _panoramaListData = (PanoramaListData) resp.getModel();


                List<PanoramaListData.PanoramaList> listPanorama = new ArrayList<>();

                if (_panoramaListData != null && _panoramaListData.getData() != null && _panoramaListData.getData().size() > 0) {

                    listPanorama = _panoramaListData.getData();

                }

                initList(listPanorama, isRefresh);
            }


        });

    }

    private void initList(List<PanoramaListData.PanoramaList> listPanorama, boolean isRefresh) {

        if (isRefresh) {
            _listPanorama.clear();
        }

        _listPanorama.addAll(listPanorama);
        ViewGroup v = (ViewGroup) _loadMoreListViewContainer.getParent();

        boolean hasMore = true;
        if (listPanorama.size() < 10) {
            hasMore = false;
        } else {
            hasMore = true;
        }

        if (_panoramaListAdapter == null) {
            _panoramaListAdapter = new PanoramaListAdapter(_context, _listPanorama);
            _gridViewWithHeaderAndFooter.setAdapter(_panoramaListAdapter);
            _loadMoreListViewContainer.setVisibility(View.VISIBLE);
            if (_listPanorama.isEmpty()) {
                _loadMoreListViewContainer.setVisibility(View.GONE);
                v.removeView(_emptyView);
                v.addView(_emptyView);
            }
        } else {
            _panoramaListAdapter.notifyDataSetChanged();
            if (_listPanorama.isEmpty()) {
                _loadMoreListViewContainer.setVisibility(View.GONE);
                v.removeView(_emptyView);
                v.addView(_emptyView);
            } else {
                _loadMoreListViewContainer.setVisibility(View.VISIBLE);
                v.removeView(_emptyView);
            }

        }

        _loadMoreListViewContainer.loadMoreFinish(listPanorama.isEmpty(), hasMore);
        _progressRelativeLayout.showContent();

        Panorama1Activity p = (Panorama1Activity) _context;
        if( p != null)
        {
            ScrollableLayout s = p.getScrollableLayout();
            if (s == null) {
                s = p.getScrollableLayout();
            }
            s.getHelper().setScrollableView(_gridViewWithHeaderAndFooter);
        }
    }


    @Override
    public void onClick(View v) {

    }

    /*   @Override
       public void onResume() {
           super.onResume();
       }*/
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        MobclickAgent.onResume(_context);       //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MobclickAgent.onPause(_context);
    }

    public void refresh(String type, Context context) {

        _progressRelativeLayout.showProgress();
        _gridViewWithHeaderAndFooter.smoothScrollToPosition(0);
        _category = (PanoramaCategoryData.Category) getArguments().get(KEY_MODEL_CATEGORY);
        RequestParams reqpestParams = new RequestParams();
        _page = 1;
        if (_category.getId() != -1) {
            reqpestParams.put("page", _page);
            reqpestParams.put("limit", _limit);
            if (!Panorama1Activity.getAllTypeId().equals("-1")) {
                reqpestParams.put("type", Panorama1Activity.getAllTypeId());
            }
            reqpestParams.put("cityid", Panorama1Activity.getCityId());
            reqpestParams.put("cid", _category.getId());
            reqpestParams.put("channelid", 18);
        } else {
            reqpestParams.put("page", _page);
            reqpestParams.put("limit", _limit);
            if (!Panorama1Activity.getAllTypeId().equals("-1")) {
                reqpestParams.put("type", Panorama1Activity.getAllTypeId());
            }
            reqpestParams.put("cityid", Panorama1Activity.getCityId());
            reqpestParams.put("channelid", 18);
        }
        requestList(reqpestParams, context, true);

    }


    public GridViewWithHeaderAndFooter getGridView() {
        if (_gridViewWithHeaderAndFooter != null) {
            return _gridViewWithHeaderAndFooter;
        }
        return null;

    }

}
