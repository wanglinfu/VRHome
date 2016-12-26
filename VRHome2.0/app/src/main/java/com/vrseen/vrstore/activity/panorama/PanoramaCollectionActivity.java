package com.vrseen.vrstore.activity.panorama;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.adapter.panorama.PanoramaCollectionAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.model.panorama.PanoramaCollectionData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.LoadMoreFooterView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.util.NetworkStatusManager;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;

/**
 * 项目名称：VRHome2.0
 * 类描述：
 * 创建人：admin
 * 创建时间：2016/6/2 14:25
 * 修改人：admin
 * 修改时间：2016/6/2 14:25
 * 修改备注：
 */
public class PanoramaCollectionActivity extends BaseActivity implements View.OnClickListener {

    private Context _context;
    //请求页数
    private int _page = 1;
    //每页请求数量
    private int _limit = 3;

    private PanoramaCollectionAdapter _panoramaCollectionAdapter;

    private LoadMoreListViewContainer _loadMoreListViewContainer;
    private ListView _collectionListView;

    private ImageView _view_back;

    private List<PanoramaCollectionData.PanoramaCollection> _listPanoramaCollection;

    private ProgressRelativeLayout _progressRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("PanoramaCollectionActivity");
        super.onCreate(savedInstanceState);

        _context = this;

        setContentView(R.layout.activity_panorama_collection);

        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);
        _progressRelativeLayout.showProgress();
        if(!NetworkStatusManager.getInstance(this).isNetworkConnectedHasMsg(false)){
            _progressRelativeLayout.showErrorText(getResources().getString(R.string.get_data_fail));

            return;
        }

        _listPanoramaCollection = new ArrayList<>();

        _view_back = (ImageView) findViewById(R.id.iv_back);
        _view_back.setOnClickListener(this);

        // 获取view的引用
        _loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_list_view_container);

        _collectionListView = (ListView) findViewById(R.id.lv_panorama_collection);

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
                requestCollection();

            }

        });

        _collectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });


        requestCollection();

    }

    private void requestCollection() {
        PanoramaRestClient.getInstance(_context).getCollectoinData(_page, _limit, new AbstractRestClient.ResponseCallBack() {
                    @Override
                    public void onFailure(Response resp, Throwable e) {
                        CommonUtils.showResponseMessage(_context, resp, e, getResources().getString(R.string.get_data_fail));
                    }

                    @Override
                    public void onSuccess(Response resp) throws JSONException {

                        PanoramaCollectionData panoramaCollectionData=(PanoramaCollectionData)resp.getModel();

                        List<PanoramaCollectionData.PanoramaCollection> listPanoramaCollection=null;

                        if(panoramaCollectionData!=null&&panoramaCollectionData.getData()!=null && panoramaCollectionData.getData().size()>=0){
                            listPanoramaCollection=panoramaCollectionData.getData();

                        }

                        _listPanoramaCollection.addAll(listPanoramaCollection);

                        if (_panoramaCollectionAdapter == null) {

                            _panoramaCollectionAdapter = new PanoramaCollectionAdapter(_context, listPanoramaCollection);
                            _collectionListView.setAdapter(_panoramaCollectionAdapter);

                        } else {
                            _panoramaCollectionAdapter.addData(listPanoramaCollection);
                        }

                        boolean hasMore=true;

                        if (listPanoramaCollection.size()<_limit) {
                            hasMore = false;
                        } else {
                            hasMore = true;
                        }

                        _loadMoreListViewContainer.loadMoreFinish(listPanoramaCollection.isEmpty(), hasMore);
                        _progressRelativeLayout.showContent();

                    }
                }

        );
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.iv_back) {
            finish();
        }

    }

}
