package com.vrseen.vrstore.activity.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.adapter.user.PlayRecordDetailAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.user.FilmListDatas;
import com.vrseen.vrstore.model.user.PlayRecordInfo;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.view.LoadMoreFooterView;
import com.vrseen.vrstore.view.swipemenulistview.SwipeMenu;
import com.vrseen.vrstore.view.swipemenulistview.SwipeMenuCreator;
import com.vrseen.vrstore.view.swipemenulistview.SwipeMenuItem;
import com.vrseen.vrstore.view.swipemenulistview.SwipeMenuListView;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;

/**
 * 播放记录
 * Created by mll on 2016/5/13.
 */
public class MyPlayRecordActivity extends BaseActivity {

    private PlayRecordDetailAdapter _playRecordDetailAdapter;
    private SwipeMenuListView _listView;
    private TextView _editdeleteTextView;
    private TextView _nodataTextView;
    private Button _btnDelete;
    private List<PlayRecordInfo> _palyRecordList;
    private List<PlayRecordInfo> _totalPalyRecordList;
    private List<PlayRecordInfo> _removePalyRecordList;
    public static String STATE_DELETE = "delete";
    public static String STATE_NODELETE = "noDelete";
    private boolean isDeleting = false;
    private LinearLayout _nodataLayout;
    private List<FilmListDatas.Data.Data1> _data1List;

    private int _page=1;
    private int _limit=15;
    private LoadMoreListViewContainer _loadMoreListViewContainer;

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,MyPlayRecordActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setPageName("MyPlayRecordActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_record);
        _totalPalyRecordList = new ArrayList<PlayRecordInfo>();
        _palyRecordList = (ArrayList<PlayRecordInfo>) getIntent().getExtras().getSerializable("data");
        _totalPalyRecordList.addAll(_palyRecordList);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();

        // 获取view的引用
        _loadMoreListViewContainer = (LoadMoreListViewContainer)findViewById(R.id.load_more_grid_view_container);

        _listView = (SwipeMenuListView) findViewById(R.id.listView_record);

        // 创建
        LoadMoreFooterView footerView = new LoadMoreFooterView(_loadMoreListViewContainer.getContext());

        // 设置
        _loadMoreListViewContainer.setLoadMoreView(footerView);
        _loadMoreListViewContainer.setLoadMoreUIHandler(footerView);

        _loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 请求下一页数据
                if(!isDeleting){
                    _page++;
                    requestPlayRecordList();
                }
            }

        });

        _editdeleteTextView = (TextView)findViewById(R.id.tv_edit_or_delete);
        _btnDelete = (Button)findViewById(R.id.btn_delete);

        findViewById(R.id.view_back).setOnClickListener(this);
        _editdeleteTextView.setOnClickListener(this);
        _btnDelete.setOnClickListener(this);
        _nodataLayout = (LinearLayout) findViewById(R.id.no_data_layout);
        _nodataTextView = (TextView) findViewById(R.id.tv_nodata);
        _nodataTextView.setText(getString(R.string.palyrecord_nodata));

        if(_totalPalyRecordList.size() <= 0){
            _nodataLayout.setVisibility(View.VISIBLE);
            _loadMoreListViewContainer.setVisibility(View.GONE);
        }else {
            _nodataLayout.setVisibility(View.GONE);
            _loadMoreListViewContainer.setVisibility(View.VISIBLE);
        }


        boolean hasMore=true;

        if (_palyRecordList.size()<15) {
            hasMore = false;
        } else {
            hasMore = true;
        }

        _playRecordDetailAdapter =  new PlayRecordDetailAdapter(MyPlayRecordActivity.this, _totalPalyRecordList);
        _listView.setAdapter(_playRecordDetailAdapter);

        _loadMoreListViewContainer.loadMoreFinish(_palyRecordList.isEmpty(), hasMore);
    }

    private void requestPlayRecordList() {
        UserRestClient.getInstance(MyPlayRecordActivity.this).getPlayHistroys(_page, _limit, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {


            }

            @Override
            public void onSuccess(Response resp) {
                Object json = resp.getData();
                FilmListDatas filmListDatas = new Gson().fromJson(json.toString(), FilmListDatas.class);

                _data1List = filmListDatas.getData().getData();
                if (_data1List != null) {
                    _palyRecordList.clear();
                    for(int i = 0; i < _data1List.size(); i++){

                        if(_data1List.get(i).getType() == 2){
                            PlayRecordInfo playRecordInfo = new PlayRecordInfo();

                            playRecordInfo.setId(_data1List.get(i).getId());
                            playRecordInfo.setResource_id(_data1List.get(i).getResource_id());
                            playRecordInfo.setCreated_at(_data1List.get(i).getCreated_at());
                            playRecordInfo.setUser_id(_data1List.get(i).getUser_id());
                            playRecordInfo.setLast_tick(_data1List.get(i).getLast_tick());
                            playRecordInfo.setLast_channel_id(_data1List.get(i).getLast_channel_id());
                            playRecordInfo.setLast_episode_id(_data1List.get(i).getLast_episode_id());
                            playRecordInfo.setType(_data1List.get(i).getType());
                            playRecordInfo.setLast_episode_text(_data1List.get(i).getLast_episode_text());

                            playRecordInfo.setEpisode(_data1List.get(i).getResource().getEpisode());
                            playRecordInfo.setFee(_data1List.get(i).getResource().getFee());
                            playRecordInfo.setImage_url(_data1List.get(i).getResource().getImage_url());
                            playRecordInfo.setSubtitle(_data1List.get(i).getResource().getSubtitle());
                            playRecordInfo.setTag(_data1List.get(i).getResource().getTag());
                            playRecordInfo.setTitle(_data1List.get(i).getResource().getTitle());
                            _palyRecordList.add(playRecordInfo);
                        }

                    }

                    _totalPalyRecordList.addAll(_palyRecordList);
                    if (_totalPalyRecordList == null || _totalPalyRecordList.size() <= 0) {
//                        _listView.setEmptyView(findViewById(R.id.no_data_layout));
                        _nodataLayout.setVisibility(View.VISIBLE);
                        _loadMoreListViewContainer.setVisibility(View.GONE);
                        return;
                    }


                    boolean hasMore = true;

                    if (_palyRecordList.size()<15) {
                        hasMore = false;
                    } else {
                        hasMore = true;
                    }

                    if (_playRecordDetailAdapter == null) {
                        _playRecordDetailAdapter =  new PlayRecordDetailAdapter(MyPlayRecordActivity.this, _totalPalyRecordList);
                        _listView.setAdapter(_playRecordDetailAdapter);
                    } else {
                        _playRecordDetailAdapter.resetData(_totalPalyRecordList);
                    }

                    _loadMoreListViewContainer.loadMoreFinish(_palyRecordList.isEmpty(), hasMore);
                }
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void initData()
    {
//        _playRecordDetailAdapter =  new PlayRecordDetailAdapter(this, _palyRecordList);
//        _listView.setAdapter(_playRecordDetailAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                deleteItem.setTitle(getResources().getString(R.string.delete));
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        _listView.setMenuCreator(creator);

        // step 2. listener item click event
        _listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                UserRestClient.getInstance(MyPlayRecordActivity.this).deleteRecordForFilm(_totalPalyRecordList.get(position).getId(), new AbstractRestClient.ResponseCallBack(){
                    @Override
                    public void onFailure(Response resp, Throwable e) {
                        ToastUtils.showShort(MyPlayRecordActivity.this, getResources().getString(R.string.video_record_delete_fail));
                    }

                    @Override
                    public void onSuccess(Response resp) throws JSONException {
                        ToastUtils.showShort(MyPlayRecordActivity.this, getResources().getString(R.string.video_record_delete_success));
                        _totalPalyRecordList.remove(position);
                        if(_totalPalyRecordList.size() <= 0){
                            requestPlayRecordList();
                        }else {
                            _playRecordDetailAdapter.resetData(_totalPalyRecordList);
                        }
                    }
                });
                return false;
            }
        });

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果当前状态是删除，就把状态改为非删除，，如果状态是非删除，就继续播放视频
                if(_totalPalyRecordList.get(position).getIsDelete().equals(MyPlayRecordActivity.STATE_DELETE)){
                    _totalPalyRecordList.get(position).setIsDelete(MyPlayRecordActivity.STATE_NODELETE);
                    _playRecordDetailAdapter.notifyDataSetChanged();
                }else if(_totalPalyRecordList.get(position).getIsDelete().equals(MyPlayRecordActivity.STATE_NODELETE)){
                    if(isDeleting){
                        _totalPalyRecordList.get(position).setIsDelete(MyPlayRecordActivity.STATE_DELETE);
                        _playRecordDetailAdapter.notifyDataSetChanged();
                    }else {
//                        ToastUtils.showShort(MyPlayRecordActivity.this,"播放视频");
                        _position = position;
                        FilmDetailData.PlayProgressData playProgressData = new FilmDetailData.PlayProgressData();
                        playProgressData.setId(_totalPalyRecordList.get(position).getResource_id());
                        playProgressData.setProValue(Integer.valueOf(_totalPalyRecordList.get(position).getLast_tick()));
                        playProgressData.setEpisodeID(_totalPalyRecordList.get(position).getLast_episode_id());

                        Intent intent = new Intent(MyPlayRecordActivity.this, FilmDetailActivity.class);
                        intent.putExtra(FilmDetailActivity.PLAY_PROGRESS_VALUE, playProgressData);
                        startActivityForResult(intent, 0);
                    }
                }
            }
        });

    }

    private int _position;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            int playProgress = data.getIntExtra("PLAYTO",0);

            PlayRecordInfo playRecordInfo =  _totalPalyRecordList.get(_position);
            playRecordInfo.setLast_tick(String.valueOf(playProgress));
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);
            playRecordInfo.setCreated_at(dateString);
            _totalPalyRecordList.remove(_position);
            _totalPalyRecordList.add(0,playRecordInfo);
            _playRecordDetailAdapter.resetData(_totalPalyRecordList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.view_back:
                this.finish();
                break;
            case R.id.tv_edit_or_delete:
                //如果是edit，就改为delete，并修改所有的listview条目为删除状态，设置全局状态为正在删除
                //如果是delete，查看是否有选中的．若有就删除,,若没有就恢复为编辑状态，
                switch (_editdeleteTextView.getText().toString()){
                    case "编辑":
                        isDeleting = true;
                        _editdeleteTextView.setText(getResources().getString(R.string.delete));
                        for(int i = 0; i < _totalPalyRecordList.size(); i++){
                            _totalPalyRecordList.get(i).setIsDelete(MyPlayRecordActivity.STATE_DELETE);
                        }
                        _playRecordDetailAdapter.resetData(_totalPalyRecordList);
                        break;
                    case "删除":
                        _removePalyRecordList = new ArrayList<PlayRecordInfo>();
                        for(int i = 0; i < _totalPalyRecordList.size(); i++){
                            if(_totalPalyRecordList.get(i).getIsDelete().equals(MyPlayRecordActivity.STATE_DELETE)){
                                _removePalyRecordList.add(_totalPalyRecordList.get(i));
                            }
                        }

                        if(_removePalyRecordList.size()>0){
                            //删除集合中的所有条目
                            DialogHelpUtils.getConfirmDialog(MyPlayRecordActivity.this, getResources().getString(R.string.sure_to_delete_hint) + _removePalyRecordList.size() + getResources().getString(R.string.times_record), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isDeleting = false;
                                    for(int i = 0; i < _removePalyRecordList.size(); i++){
                                        UserRestClient.getInstance(MyPlayRecordActivity.this).deleteRecordForFilm(_removePalyRecordList.get(i).getId(), new AbstractRestClient.ResponseCallBack(){
                                            @Override
                                            public void onFailure(Response resp, Throwable e) {

                                            }

                                            @Override
                                            public void onSuccess(Response resp) throws JSONException {

                                            }
                                        });
                                    }
                                    _totalPalyRecordList.removeAll(_removePalyRecordList);
                                    _playRecordDetailAdapter.resetData(_totalPalyRecordList);
                                    _editdeleteTextView.setText(getResources().getString(R.string.edit));
                                    if (_totalPalyRecordList.size() <= 0) {
                                        //加载新数据，如果加载回来的数据为空，就没有数据了
                                        requestPlayRecordList();

                                    }
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isDeleting = false;
                                    _editdeleteTextView.setText(getResources().getString(R.string.edit));
                                    for(int i = 0; i < _totalPalyRecordList.size(); i++){
                                        _totalPalyRecordList.get(i).setIsDelete(MyPlayRecordActivity.STATE_NODELETE);
                                    }
                                    _playRecordDetailAdapter.notifyDataSetChanged();
                                }
                            }).show();
                        }else {
                            _editdeleteTextView.setText(getResources().getString(R.string.edit));
                            isDeleting = false;
                        }
                        break;
                }
                break;
            case R.id.btn_delete:
               //删除

                break;
            default :
                break;
        }
    }
}
