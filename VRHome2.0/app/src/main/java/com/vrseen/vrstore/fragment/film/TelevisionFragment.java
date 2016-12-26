package com.vrseen.vrstore.fragment.film;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.activity.film.FilmFilterActivity;
import com.vrseen.vrstore.activity.search.SearchActivity;
import com.vrseen.vrstore.adapter.film.FilmAdapter;
import com.vrseen.vrstore.fragment.BaseFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.SearchRestClient;
import com.vrseen.vrstore.model.Home.RecommendData;
import com.vrseen.vrstore.model.film.FilmCateroryData;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.film.FilmListData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.view.ExpGridView;
import com.vrseen.vrstore.view.LoadMoreFooterView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;
import in.srain.cube.util.NetworkStatusManager;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;

/**
 * Created by jiangs on 16/5/10.
 */
public class TelevisionFragment extends BaseFragment implements View.OnClickListener {
    public static final String KEY_MODEL_CATEGORY = "KEY_MODEL_CATEGORY";
    private View _thisFragment;
    private FilmCateroryData.Category _category;
    private in.srain.cube.image.ImageLoader _imageLoaderAD;
    private RelativeLayout _filterRelativel;
    private int _page = 1;
    private CubeImageView _recommendImage;
    private TextView _recommendNameTextView;
    private TextView _recommendDesTextView;
    private FilmAdapter _filmAdapter;
    private List<FilmListData.BaseFilm> baseFilmList;
    private ProgressRelativeLayout _progressLinearLayout;
    private EditText _searchEditText;

    private LoadMoreGridViewContainer _loadMoreListViewContainer;
    private GridViewWithHeaderAndFooter _gridViewWithHeaderAndFooter;

    private Context _context;

    private BitmapUtils _bitmapUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.setPageName("TelevisionFragment");
        super.onCreateView(inflater, container, savedInstanceState);
        _category = (FilmCateroryData.Category) getArguments().get(KEY_MODEL_CATEGORY);
        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_tev, null);
            initView();
        }

        _context = getContext();
        _bitmapUtils = new BitmapUtils(_context);

        return _thisFragment;

    }

    private void initView() {

        _progressLinearLayout = (ProgressRelativeLayout) _thisFragment.findViewById(R.id.progress_layout);
        _progressLinearLayout.showProgress();

        if(!NetworkStatusManager.getInstance(_context).isNetworkConnectedHasMsg(false)){
            _progressLinearLayout.showErrorText(getResources().getString(R.string.get_recommend_fail));
            return;
        }

        _searchEditText = (EditText) _thisFragment.findViewById(R.id.et_tev_search);
        _searchEditText.setOnClickListener(this);

        _filterRelativel = (RelativeLayout) _thisFragment.findViewById(R.id.rl_filter);

        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(getContext());
        handler.setLoadingResources(R.drawable.jiazaiguanggao);
        handler.setErrorResources(R.drawable.jiazaishibai_guanggao);
        _imageLoaderAD = ImageLoaderFactory.create(getContext(), handler);

        View head = LayoutInflater.from(getActivity()).inflate(R.layout.head_tev, null);

        head.findViewById(R.id.tv_viewMore).setVisibility(View.GONE);

        //_imageLoaderAD = ConfigDefaultImageUtils.getInstance().getADImageLoader(getActivity());

        _recommendImage = (CubeImageView) head.findViewById(R.id.iv_recommend);
        _recommendNameTextView = (TextView) head.findViewById(R.id.tv_recommend_name);
        _recommendDesTextView = (TextView) head.findViewById(R.id.tv_recommend_des);

        _filterRelativel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilmFilterActivity.actionStart(_thisFragment.getContext(), _category);
            }
        });

        // 获取view的引用
        _loadMoreListViewContainer = (LoadMoreGridViewContainer) _thisFragment.findViewById(R.id.load_more_grid_view_container);

        _gridViewWithHeaderAndFooter = (GridViewWithHeaderAndFooter) _thisFragment.findViewById(R.id.load_more_grid_view);

        _gridViewWithHeaderAndFooter.addHeaderView(head);

        _gridViewWithHeaderAndFooter.setNumColumns(3);
        _gridViewWithHeaderAndFooter.setHorizontalSpacing(10);
        _gridViewWithHeaderAndFooter.setVerticalSpacing(10);

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
                requestVideoList();

            }

        });

        requestRecommends();
    }

    private void requestRecommends() {
        if (_category == null)
            return;

        CommonRestClient.getInstance(getActivity()).getVideoRecommends(_category.getGenre_tag(), new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                String msg = getString(R.string.get_recommend_fail);
                CommonUtils.showResponseMessage(getActivity(), resp, e, msg);
                _progressLinearLayout.showErrorText(msg);
            }

            @Override
            public void onSuccess(Response resp) {
                RecommendData recommendData = (RecommendData) resp.getModel();
                if (recommendData != null && recommendData.getData() != null && recommendData.getData().size() > 0) {
                    final RecommendData.Recommend recommend = recommendData.getData().get(0);
                    if (recommend.getVideo() != null) {

                        //cube的bug,故使用bitmaputils
                        _bitmapUtils.display(_recommendImage,recommend.getVideo().getImage_url());
                       // _recommendImage.loadImage(_imageLoaderAD, recommend.getVideo().getImage_url());
                        _recommendNameTextView.setText(recommend.getVideo().getTitle());
                        _recommendDesTextView.setText(recommend.getVideo().getSubtitle());
                        _recommendImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FilmDetailData.PlayProgressData playProgressData = new FilmDetailData.PlayProgressData();
                                playProgressData.setId(recommend.getVideo().getId());
                                FilmDetailActivity.actionStart(_thisFragment.getContext(), playProgressData);
                            }
                        });

                    }
                }

                requestVideoList();
            }
        });

    }

    private void requestVideoList() {
        if (_category == null)
            return;

        CommonRestClient.getInstance(getActivity()).getVideoList(_category.getId() + "", _page, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(getActivity(), resp, e, getString(R.string.get_recommend_fail));
                _progressLinearLayout.showErrorText(getString(R.string.get_recommend_fail));
            }

            @Override
            public void onSuccess(Response resp) {
                _progressLinearLayout.showContent();
                FilmListData filmListData = (FilmListData) resp.getModel();
                if (filmListData != null) {
                    baseFilmList = filmListData.getData();

                    if (baseFilmList == null && baseFilmList.size() <= 0) {
                        ToastUtils.showOnly(getContext(), getString(R.string.no_last_data), 400);
                        return;
                    }

                    boolean hasMore=true;

                    if (baseFilmList.size()<10) {
                        hasMore = false;
                    } else {
                        hasMore = true;
                    }

                    if (_filmAdapter == null) {
                        _filmAdapter = new FilmAdapter(_context, baseFilmList);
                        _gridViewWithHeaderAndFooter.setAdapter(_filmAdapter);
                    } else {
                        _filmAdapter.addData(baseFilmList);
                    }
                    _loadMoreListViewContainer.loadMoreFinish(baseFilmList.isEmpty(), hasMore);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        int vid = v.getId();

        switch (vid) {
            case R.id.et_tev_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("from", SearchRestClient.VIDEO_TAG);
                getActivity().startActivity(intent);
                break;
        }

    }


    /*@Override
    public void onResume() {
        super.onResume();
    }*/
    public void onResume() {
        super.onResume();
        requestRecommends();
        MobclickAgent.onResume(_context);       //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(_context);
    }

}
