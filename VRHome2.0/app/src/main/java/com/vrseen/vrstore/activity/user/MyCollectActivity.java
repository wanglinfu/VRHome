package com.vrseen.vrstore.activity.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.adapter.film.FilmAdapter;
import com.vrseen.vrstore.adapter.user.MyCollectAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.film.FilmListData;
import com.vrseen.vrstore.model.user.FilmListDatas;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.ToastUtils;
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
 * Created by mll on 2016/5/4.
 */
public class MyCollectActivity extends BaseActivity {

    private Context _context = null;
    private GridViewWithHeaderAndFooter _gridViewWithHeaderAndFooter;


    private List<FilmListDatas.Data.Data1> data1List;
    private List<FilmListData.BaseFilm> baseFilmList;
    private List<FilmListData.BaseFilm> _totalBaseFilmList;

    private MyCollectAdapter _myCollectAdapter;
    private LoadMoreGridViewContainer _loadMoreListViewContainer;
    private ProgressRelativeLayout _progressLinearLayout;
    private LinearLayout _nodataLayout;
    private int _page=1;
    private int _limit=15;

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,MyCollectActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setPageName("MyCollectActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_collect);
        _context = this;
        _totalBaseFilmList = new ArrayList<FilmListData.BaseFilm>();
        initView();
    }


    @Override
    protected void initView() {
        super.initView();
        _progressLinearLayout = (ProgressRelativeLayout)findViewById(R.id.progress_layout);
        _progressLinearLayout.showProgress();
        // 获取view的引用
        _loadMoreListViewContainer = (LoadMoreGridViewContainer)findViewById(R.id.load_more_grid_view_container);

        _gridViewWithHeaderAndFooter = (GridViewWithHeaderAndFooter)findViewById(R.id.gridview_collect);

        _gridViewWithHeaderAndFooter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilmDetailData.PlayProgressData playProgressData = new FilmDetailData.PlayProgressData();
                playProgressData.setId(_totalBaseFilmList.get(position).getId());

                for (int i = 0; i <data1List.size() ; i++) {
                    if (data1List.get(position).getId() ==_totalBaseFilmList.get(position).getId() ){
                        playProgressData.setEpisodeID(data1List.get(position).getLast_episode_id());
                        playProgressData.setProValue(Integer.valueOf(data1List.get(position).getLast_tick()));

                    }
                    
                }
                
                FilmDetailActivity.actionStart(_context,playProgressData);
            }
        });

        _gridViewWithHeaderAndFooter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                DialogHelpUtils.getConfirmDialog(_context, getResources().getString(R.string.config_delete) + baseFilmList.get(position).getTitle() +"？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserRestClient.getInstance(_context).cancelCollectFilm(String.valueOf(baseFilmList.get(position).getId()), new AbstractRestClient.ResponseCallBack() {
                            @Override
                            public void onFailure(Response resp, Throwable e) {
                                ToastUtils.showShort(_context,"删除失败");
                            }

                            @Override
                            public void onSuccess(Response resp) throws JSONException {
                                _totalBaseFilmList.remove(position);
                                _myCollectAdapter.refreshData(_totalBaseFilmList);
                                ToastUtils.showShort(_context,"删除成功");
                                if(_totalBaseFilmList.size() <= 0){
                                    requestFilmsCollectList();
                                }
                            }
                        });
                    }
                }, null).show();

                return false;
            }
        });

        _nodataLayout = (LinearLayout) findViewById(R.id.no_data_layout);
        TextView tv_nodata = (TextView) findViewById(R.id.tv_nodata);
        tv_nodata.setText("暂无收藏记录！");
        _nodataLayout.setVisibility(View.GONE);


        _gridViewWithHeaderAndFooter.setNumColumns(3);
        _gridViewWithHeaderAndFooter.setHorizontalSpacing(15);
        _gridViewWithHeaderAndFooter.setVerticalSpacing(15);

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
                requestFilmsCollectList();
            }

        });

        requestFilmsCollectList();

        findViewById(R.id.view_back).setOnClickListener(this);

    }

    private void requestFilmsCollectList() {
        UserRestClient.getInstance(this).getFilmsCollect(_page,_limit,new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                String msg = getString(R.string.film_collect_get_error);
                CommonUtils.showResponseMessage(MyCollectActivity.this, resp, e, msg);
                _progressLinearLayout.showErrorText(msg);
            }

            @Override
            public void onSuccess(Response resp) {
                Object json = resp.getData();
                FilmListDatas filmListDatas = new Gson().fromJson(json.toString(), FilmListDatas.class);
                data1List = filmListDatas.getData().getData();
                if (data1List != null) {
                    baseFilmList = new ArrayList<FilmListData.BaseFilm>();
                    for(int i = 0; i < data1List.size(); i++){
                        FilmListData.BaseFilm baseFilm = new FilmListData.BaseFilm();
                        baseFilm.setEpisode(data1List.get(i).getResource().getEpisode());
                        baseFilm.setFee(data1List.get(i).getResource().getFee());
                        baseFilm.setId(data1List.get(i).getResource().getId());
                        baseFilm.setImage_url(data1List.get(i).getResource().getImage_url());
                        baseFilm.setSubtitle(data1List.get(i).getResource().getSubtitle());
                        baseFilm.setTag(data1List.get(i).getResource().getTag());
                        baseFilm.setTitle(data1List.get(i).getResource().getTitle());
                        baseFilmList.add(baseFilm);
                    }

                    _totalBaseFilmList.addAll(baseFilmList);

                    if (_totalBaseFilmList == null || _totalBaseFilmList.size() <= 0) {

                        _loadMoreListViewContainer.setVisibility(View.GONE);
                        _nodataLayout.setVisibility(View.VISIBLE);
                        _progressLinearLayout.showContent();
                        return;
                    }


                    boolean hasMore=true;

                    if (baseFilmList.size()<15) {
                        hasMore = false;
                    } else {
                        hasMore = true;
                    }

                    if (_myCollectAdapter == null) {
                        _myCollectAdapter = new MyCollectAdapter(_context, _totalBaseFilmList);
                        _gridViewWithHeaderAndFooter.setAdapter(_myCollectAdapter);
                    } else {
                        _myCollectAdapter.refreshData(_totalBaseFilmList);
                    }

                    _loadMoreListViewContainer.loadMoreFinish(baseFilmList.isEmpty(), hasMore);
                }

                _progressLinearLayout.showContent();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.view_back:
                this.finish();
                break;
            default:break;
        }
    }


}
