package com.vrseen.vrstore.fragment.panorama;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.panorama.PanoramaActivity;
import com.vrseen.vrstore.activity.panorama.PanoramaDetailActivity;
import com.vrseen.vrstore.adapter.panorama.PanoramaListAdapter;
import com.vrseen.vrstore.fragment.app.BaseCategoryFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.model.panorama.PanoramaCategoryData;
import com.vrseen.vrstore.model.panorama.PanoramaListData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.LoadMoreFooterView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景列表页
 * 创建人：郝晓辉
 * 创建时间：2016/6/21 16:50
 * 修改人：郝晓辉
 * 修改时间：2016/6/21 16:50
 * 修改备注：
 */
public class BasePanoramaCategoryFragment extends BaseCategoryFragment implements AbsListView.OnScrollListener{

    private PanoramaCategoryData.Category _category;
    public static final String KEY_MODEL_CATEGORY = "KEY_MODEL_CATEGORY";
    private View _thisFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _context = getActivity();
        _category = (PanoramaCategoryData.Category) getArguments().get(KEY_MODEL_CATEGORY);
        _thisFragment = inflater.inflate(R.layout.fragment_panorama_list_1, null);
        initView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(_context);       //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(_context);
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

    private Context _context;

    private GridView _gridView;

    private int _latest = 0;
    private int _count = 0;

    /**
     * 初始化界面
     */
    private void initView() {

        _context = getContext();

        _progressRelativeLayout = (ProgressRelativeLayout) _thisFragment.findViewById(R.id.progress_layout);

        _listPanorama = new ArrayList<>();

        _gridView=(GridView)_thisFragment.findViewById(R.id.gv_panorama_list);

        _emptyView = getActivity().getLayoutInflater().inflate(R.layout.no_data_layout, null);
        _emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        _gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    int lastPosition = view.getLastVisiblePosition() - 1;
                    if (_count != _panoramaListAdapter.getCount()) {
                        _count = _panoramaListAdapter.getCount();
                        if (lastPosition >= (view.getCount() - 2)) {
                            _page++;
                            RequestParams reqpestParams = new RequestParams();

                            reqpestParams.put("page", _page);
                            reqpestParams.put("limit", _limit);
                            if (!PanoramaActivity.getAllTypeId().equals("-1")) {
                                reqpestParams.put("type", PanoramaActivity.getAllTypeId());
                            }
                            reqpestParams.put("cityid", PanoramaActivity.getCityId());
                            reqpestParams.put("cid", _category.getId());

                            requestList(reqpestParams, _context, false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        RequestParams reqpestParams = new RequestParams();

        reqpestParams.put("page", _page);
        reqpestParams.put("limit", _limit);
        if (!PanoramaActivity.getAllTypeId().equals("-1")) {
            reqpestParams.put("type", PanoramaActivity.getAllTypeId());
        }
        reqpestParams.put("cityid", PanoramaActivity.getCityId());
        reqpestParams.put("cid", _category.getId());

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


                List<PanoramaListData.PanoramaList> listPanorama=new ArrayList<>();

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
        ViewGroup v = (ViewGroup) _gridView.getParent();

        if (_panoramaListAdapter == null) {
            _panoramaListAdapter = new PanoramaListAdapter(_context, _listPanorama);
            _gridView.setAdapter(_panoramaListAdapter);
            _gridView.setVisibility(View.VISIBLE);
            if (_listPanorama.isEmpty()) {
                _gridView.setVisibility(View.GONE);
                v.removeView(_emptyView);
                v.addView(_emptyView);
            }
        } else {
            _panoramaListAdapter.notifyDataSetChanged();
            if (_listPanorama.isEmpty()) {
                _gridView.setVisibility(View.GONE);
                v.removeView(_emptyView);
                v.addView(_emptyView);
            } else {
                _gridView.setVisibility(View.VISIBLE);
                v.removeView(_emptyView);
            }

        }
        _progressRelativeLayout.showContent();

    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public View getScrollableView() {
        return _gridView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _listPanorama.clear();
        _listPanorama=null;
        _panoramaListAdapter=null;
    }
}
