package com.vrseen.vrstore.fragment.find;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.find.DownloadActivity;
import com.vrseen.vrstore.adapter.find.FindListViewAdapter;
import com.vrseen.vrstore.fragment.BaseFragment;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.util.DownloadUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DownloadAppFragment extends BaseFragment implements View.OnClickListener {


    private View _thisFragment;
    private Context context;
    //所有的下载的应用数据
    private ArrayList<DownloadInfo> _listAppInfo = new ArrayList<DownloadInfo>();
    //给listview的数据,HashMap里面保存时间和要传递给gridview的数据集合
    private ArrayList<HashMap> _listToListView = new ArrayList<HashMap>();
    //最近下载
    private ArrayList<DownloadInfo> _listRecent = new ArrayList<DownloadInfo>();
    //以前下载
    private ArrayList<DownloadInfo> _listPrevious = new ArrayList<DownloadInfo>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.setPageName("DownloadAppFragment");
        super.onCreateView(inflater, container, savedInstanceState);
        context = getContext();
        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_downloads, null);
            initData();
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

    private void initData() {
        //获取app的下载数据
        _listAppInfo.clear();
        List<DownloadInfo> downloadInfoList = DownloadUtils.getInstance().getlist();

        for (int i = 0; i < downloadInfoList.size(); i++) {
            DownloadInfo vo = downloadInfoList.get(i);
            if(vo.getType() == VRHomeConfig.LOCAL_TYPE_APP)
            {
                _listAppInfo.add(vo);
            }
        }
    }
    public static ListView downloadAppListView;
    public static View emptyView;
    private void initView() {

        downloadAppListView = (ListView) _thisFragment.findViewById(R.id.lv_myDownload);
        emptyView = _thisFragment.findViewById(R.id.ll_download_nodata);
        if(_listAppInfo.size() <= 0)
        {
            downloadAppListView.setEmptyView(emptyView);
            return;
        }

        handleAppData();
        downloadAppListView.setAdapter(new FindListViewAdapter(getActivity(), _listToListView, DownloadActivity.DOWNLOAD_APP) {
            @Override
            public void refresh() {

            }
        });
    }

    private ArrayList<HashMap> handleAppData() {
        //遍历全部数据，根据时间放到不同arraylist集合中，然后把时间和集合封装到hashmap中，最后放到一个ArrayList中返回
        for(int i = 0; i < _listAppInfo.size(); i++){
            long lastModifiedTime = _listAppInfo.get(i).getLastModifiedTime();
            long nowTime = System.currentTimeMillis();
            long DTime = nowTime - lastModifiedTime;
            if(DTime < 2*24*60*60*1000){  //最近下载，2天内的
                _listRecent.add(_listAppInfo.get(i));
            }else {    //以前下载
                _listPrevious.add(_listAppInfo.get(i));
            }
        }
        //如果集合中有数据，就放到hashmap中
        if(_listRecent.size() > 0){
            HashMap todayHashMap = new HashMap();
            todayHashMap.put("time", getString(R.string.find_download_recent));
            todayHashMap.put("data", _listRecent);
            _listToListView.add(todayHashMap);
        }

        if(_listPrevious.size() > 0){
            HashMap longAgoHashMap = new HashMap();
            longAgoHashMap.put("time", getString(R.string.find_download_previous));
            longAgoHashMap.put("data", _listPrevious);
            _listToListView.add(longAgoHashMap);
        }
        return _listToListView;
    }


    @Override
    public void onClick(View v) {
        switch ( v.getId() )
        {

        }
    }
}
