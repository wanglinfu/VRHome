package com.vrseen.vrstore.activity.panorama;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.adapter.panorama.PanoramaCollectionAdapter;
import com.vrseen.vrstore.adapter.panorama.PanoramaListAdapter;
import com.vrseen.vrstore.adapter.panorama.PanoramaRelatedAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.model.panorama.PanoramaCollectionData;
import com.vrseen.vrstore.model.panorama.PanoramaCollectionDetailData;
import com.vrseen.vrstore.model.panorama.PanoramaListData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.ExpGridView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;
import in.srain.cube.util.NetworkStatusManager;

/**
 * 项目名称：VRHome2.0
 * 类描述：专辑详情页
 * 创建人：郝晓辉
 * 创建时间：2016/6/7 13:23
 * 修改人：郝晓辉
 * 修改时间：2016/6/7 13:23
 * 1
 */
public class PanoramaCollectionDetailActivity extends BaseActivity implements View.OnClickListener {

    private Context _context;
    /**
     * 标题栏
     */
    private TextView _titleTextView;
    /**
     * 图片
     */
    private CubeImageView _imageImageView;
    /**
     * 简介
     */
    private TextView _instroductionTextView;
    /**
     * 名称
     */
    private TextView _positionTextVieww;
    /**
     * 类型
     */
    private TextView _typeTextView;
    /**
     * 维度
     */
    private TextView _metaTextView;
    /**
     * 大小
     */
    private TextView _sizeTextView;
    /**
     * 时间
     */
    private TextView _timeTextView;


    /**
     * 返回键
     */
    private View _backImageView;
    /**
     * 列表scrollview
     */
    private ScrollView _collectionDetailScrollView;
    /**
     * 列表gridview
     */
    private ExpGridView _collectionDetailGridView;
    /**
     * 页数
     */
    private int _page = 1;
    /**
     * 每页数量
     */
    private int _limit = 4;
    /**
     * 专辑id
     */
    private String _listid = "";
    /**
     * 全景列表
     */
//    private PanoramaRelatedAdapter _panoramaCollectionAdapter;

    private int _index = 0;
    private int _itemGridHeight = 0;
    private int _iListCache = 0;

    private ProgressRelativeLayout _progressRelativeLayout;

    private in.srain.cube.image.ImageLoader imageLoader;

    private TextView _relatedNoDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("PanoramaCollectionDetailActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_panorama_collection_detail);

        _context = this;

        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(_context);
        handler.setLoadingResources(R.drawable.jiazaiguanggao);
        handler.setErrorResources(R.drawable.jiazaishibai_guanggao);
        imageLoader = ImageLoaderFactory.create(_context, handler);

        initView();

    }

    @Override
    protected void initView() {
        super.initView();

        _relatedNoDataTextView = (TextView) findViewById(R.id.tv_panorama_related_nodaa);

        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);
        _progressRelativeLayout.showProgress();

        if (!NetworkStatusManager.getInstance(this).isNetworkConnectedHasMsg(false)) {
            _progressRelativeLayout.showErrorText(getResources().getString(R.string.get_data_fail));

            return;
        }

        _listid = getIntent().getStringExtra("listid");

        _listPanorama = new ArrayList<>();

        _titleTextView = (TextView) findViewById(R.id.tv_panorama_collection_detail_title);
        _imageImageView = (CubeImageView) findViewById(R.id.iv_panorama_collection_detail_video);
        _instroductionTextView = (TextView) findViewById(R.id.tv_panorama_collection_detail_introduction);
        _positionTextVieww = (TextView) findViewById(R.id.tv_panorama_collection_detail_position);
        _typeTextView = (TextView) findViewById(R.id.tv_panorama_collection_detail_type);
        _metaTextView = (TextView) findViewById(R.id.tv_panorama_collection_detail_meta);
        _sizeTextView = (TextView) findViewById(R.id.tv_panorama_collection_detail_file_size);
        _timeTextView = (TextView) findViewById(R.id.tv_panorama_collection_detail_time);

        _backImageView = findViewById(R.id.view_back);
        _backImageView.setOnClickListener(this);

        _collectionDetailScrollView = (ScrollView) findViewById(R.id.sv_panorama_collection_detail);
        _collectionDetailGridView = (ExpGridView) findViewById(R.id.gv_panorama_collection_detail);

        _collectionDetailGridView.setNumColumns(2);

        _collectionDetailGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });

//        _collectionDetailScrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        _index++;
//                        break;
//
//                    default:
//                        break;
//                }
//                if (event.getAction() == MotionEvent.ACTION_UP && _index > 0) {
//                    _index = 0;
//                    View view = ((ScrollView) v).getChildAt(0);
//                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight() + _itemGridHeight * 2) {
//                        //加载数据代码
//                        if (_iListCache != _panoramaListAdapter.getCount()) {
//                            _iListCache = _panoramaListAdapter.getCount();
//                            _page++;
//
//                            RequestParams reqpestParams = new RequestParams();
//
//                            reqpestParams.put("page", _page);
//                            reqpestParams.put("limit", _limit);
//                            reqpestParams.put("listid", _listid);
//
//                            requestList(reqpestParams);
//                        }
//                    }
//                }
//                return false;
//            }
//        });

        requestCollectionDetail();

    }

    private String _panoramaListJson = "";

    private void requestCollectionDetail() {

        PanoramaRestClient.getInstance(_context).getCollectionDetail(_listid, new AbstractRestClient.ResponseCallBack() {

            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

                JSONObject jo = new JSONObject(resp.getData().toString());
                JSONObject jo1 = jo.getJSONObject("data");
                JSONArray ja = jo1.getJSONArray("posts");
                _panoramaListJson = ja.toString();

                PanoramaCollectionDetailData panoramaCollectionDetailData = (PanoramaCollectionDetailData) resp.getModel();

                PanoramaCollectionDetailData.PanoramaCollectionDetail panoramaCollectionDetai = null;

                if (panoramaCollectionDetailData != null && panoramaCollectionDetailData.getData() != null) {
                    panoramaCollectionDetai = panoramaCollectionDetailData.getData();
                }

                initDetail(panoramaCollectionDetai);

            }
        });

    }

    private void initDetail(PanoramaCollectionDetailData.PanoramaCollectionDetail panoramaCollectionDetai) {

        _titleTextView.setText(panoramaCollectionDetai.getTitle());
        _imageImageView.loadImage(imageLoader, panoramaCollectionDetai.getThumbnail());
        _timeTextView.setText(panoramaCollectionDetai.getShootingdate());
        _instroductionTextView.setText(panoramaCollectionDetai.getDescription());

        if (panoramaCollectionDetai.getMeta() != null && !panoramaCollectionDetai.getMeta().equals("")) {
            _metaTextView.setText(panoramaCollectionDetai.getMeta().split("\"")[1]);
        } else {
            _metaTextView.setText(panoramaCollectionDetai.getMeta());
        }

        _sizeTextView.setText(panoramaCollectionDetai.getFilesize());
        _positionTextVieww.setText(panoramaCollectionDetai.getShootinglocation());
        if (panoramaCollectionDetai.getType().equals("0")) {
            _typeTextView.setText(getResources().getString(R.string.panorama_alltype_pic));
        } else if (panoramaCollectionDetai.getType().equals("1")) {
            _typeTextView.setText(getResources().getString(R.string.panorama_alltype_video));
        }

        _imageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                U3dMediaPlayerLogic.getInstance().startPlayPanorama(_context, _panoramaDetail, 1, false, VRHomeConfig.TYPE_PANORAMA);

                U3dMediaPlayerLogic.getInstance().startPlayPanoramaCollection(_context, _panoramaListJson, 1, VRHomeConfig.TYPE_PANORAMA);
            }
        });


//        RequestParams reqpestParams = new RequestParams();
//
//        reqpestParams.put("page", _page);
//        reqpestParams.put("limit", _limit);
//        reqpestParams.put("listid", _listid);
//
//        requestList(reqpestParams);

        List<PanoramaListData.PanoramaList> listPanorama = new ArrayList<>();

        if (panoramaCollectionDetai.getPosts() != null && panoramaCollectionDetai.getPosts().size() >= 0) {
            listPanorama = panoramaCollectionDetai.getPosts();
        }

        _listPanorama.addAll(listPanorama);

        if (_panoramaListAdapter == null) {
            _panoramaListAdapter = new PanoramaListAdapter(_context, _listPanorama);
            _collectionDetailGridView.setAdapter(_panoramaListAdapter);

            View view = _panoramaListAdapter.getView(0, null, _collectionDetailGridView);
            if (view != null) {
                view.measure(0, 0);
                _itemGridHeight = view.getMeasuredHeight();
            }
        } else {
            _panoramaListAdapter.addData(listPanorama);
        }

        if (listPanorama.size() <= 3) {
            _relatedNoDataTextView.setVisibility(View.VISIBLE);
        } else {
            _relatedNoDataTextView.setVisibility(View.GONE);
        }
        _progressRelativeLayout.showContent();


    }

    private PanoramaListAdapter _panoramaListAdapter;

    private List<PanoramaListData.PanoramaList> _listPanorama;

    /**
     * 请求列表数据
     */
    private void requestList(RequestParams requestParams) {

        Log.e("xxxxxxxxxxxxx", "111111111111");

        PanoramaRestClient.getInstance(_context).getListData(requestParams, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

                PanoramaListData _panoramaListData = (PanoramaListData) resp.getModel();

                List<PanoramaListData.PanoramaList> listPanorama = null;

                if (_panoramaListData != null && _panoramaListData.getData() != null && _panoramaListData.getData().size() >= 0) {
                    listPanorama = _panoramaListData.getData();
                }

//                PanoramaCollectionData panoramaCollectionData=(PanoramaCollectionData)resp.getModel();
//
//                List<PanoramaCollectionData.PanoramaCollection> pnoramaCollection=new ArrayList<>();
//
//                if(panoramaCollectionData!=null&&panoramaCollectionData.getData()!=null&&panoramaCollectionData.getData().size()>=0){
//                    pnoramaCollection=panoramaCollectionData.getData();
//                }

//                initList(pnoramaCollection);
                _listPanorama.addAll(listPanorama);

                if (_panoramaListAdapter == null) {
                    _panoramaListAdapter = new PanoramaListAdapter(_context, _listPanorama);
                    _collectionDetailGridView.setAdapter(_panoramaListAdapter);

                    View view = _panoramaListAdapter.getView(0, null, _collectionDetailGridView);
                    if (view != null) {
                        view.measure(0, 0);
                        _itemGridHeight = view.getMeasuredHeight();
                    }
                } else {
                    _panoramaListAdapter.addData(listPanorama);
                }

                if (listPanorama.size() <= 3) {
                    _relatedNoDataTextView.setVisibility(View.VISIBLE);
                } else {
                    _relatedNoDataTextView.setVisibility(View.GONE);
                }
                _progressRelativeLayout.showContent();
            }


        });

    }

//    private void initList(List<PanoramaCollectionData.PanoramaCollection> listCollection) {
//
//        _collectionDetailGridView.setNumColumns(2);
//        _collectionDetailGridView.setHorizontalSpacing(10);
//
//        if(_panoramaCollectionAdapter==null) {
//
//            _panoramaCollectionAdapter=new PanoramaRelatedAdapter(_context,listCollection);
//            _collectionDetailGridView.setAdapter(_panoramaCollectionAdapter);
//
//            View view = _panoramaCollectionAdapter.getView(0, null, _collectionDetailGridView);
//            if (view != null) {
//                view.measure(0, 0);
//                _itemGridHeight = view.getMeasuredHeight();
//            }
//
//        }else {
//            _panoramaCollectionAdapter.addData(listCollection);
//        }
//
//        _progressRelativeLayout.showContent();
//
//    }


    @Override
    public void onClick(View v) {

        int vid = v.getId();
        if (vid == R.id.view_back) {
            finish();
        }

    }

}
