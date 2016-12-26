package com.vrseen.vrstore.fragment.find;


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

public class DownloadPanoFragment extends BaseFragment implements View.OnClickListener {


    private View _thisFragment;

    //所有的下载的全景数据
    private ArrayList<DownloadInfo> _listPanoInfo = new ArrayList<DownloadInfo>();
    //给listview的数据,HashMap里面保存时间和要传递给gridview的数据集合
    private ArrayList<HashMap> _listToListView = new ArrayList<HashMap>();
    //今天的
    private ArrayList<DownloadInfo> _listToday = new ArrayList<DownloadInfo>();
    //上周
    private ArrayList<DownloadInfo> _listLastWeek = new ArrayList<DownloadInfo>();
    //上月
    private ArrayList<DownloadInfo> _listLastMonth = new ArrayList<DownloadInfo>();
    //更早
    private ArrayList<DownloadInfo> _listLongAgo = new ArrayList<DownloadInfo>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.setPageName("DownloadPanoFragment");
        super.onCreateView(inflater, container, savedInstanceState);
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
        _listPanoInfo.clear();
        List<DownloadInfo> downloadInfoVOList = DownloadUtils.getInstance().getlist();
        for (int i = 0; i < downloadInfoVOList.size(); i++) {
            DownloadInfo vo = downloadInfoVOList.get(i);
            if (vo.getType() == VRHomeConfig.VIDEO || vo.getType() == VRHomeConfig.IMAGE) {
                _listPanoInfo.add(vo);
            }
        }
    }

    public static ListView downloadPanoListView;
    public static View emptyView;
    private void initView() {

        downloadPanoListView = (ListView) _thisFragment.findViewById(R.id.lv_myDownload);
        emptyView = _thisFragment.findViewById(R.id.ll_download_nodata);
        if(_listPanoInfo.size() <= 0)
        {
            downloadPanoListView.setEmptyView(emptyView);
            return;
        }
        handlePanoData();
        downloadPanoListView.setAdapter(new FindListViewAdapter(getActivity(), _listToListView, DownloadActivity.DOWNLOAD_PANO) {
            @Override
            public void refresh() {

            }
        });
    }

    private ArrayList<HashMap> handlePanoData() {
        //遍历全部数据，根据时间放到不同arraylist集合中，然后把时间和集合封装到hashmap中，最后放到一个ArrayList中返回
        for(int i = 0; i < _listPanoInfo.size(); i++){
            long lastModifiedTime = _listPanoInfo.get(i).getLastModifiedTime();
            long nowTime = System.currentTimeMillis();
            long DTime = nowTime - lastModifiedTime;
            if(DTime < 24*60*60*1000){  //今天
                _listToday.add(_listPanoInfo.get(i));
            }else if(DTime < 7*24*60*60*1000){    //上周
                _listLastWeek.add(_listPanoInfo.get(i));
            }else if(DTime < 30*24*60*60*1000){    //上月
                _listLastMonth.add(_listPanoInfo.get(i));
            }else {    //更早
                _listLongAgo.add(_listPanoInfo.get(i));
            }
        }
        //如果集合中有数据，就放到hashmap中
        if(_listToday.size() > 0){
            HashMap todayHashMap = new HashMap();
            todayHashMap.put("time",getString(R.string.find_local_today));
            todayHashMap.put("data",_listToday);
            _listToListView.add(todayHashMap);
        }
        if(_listLastWeek.size() > 0){
            HashMap lastWeekHashMap = new HashMap();
            lastWeekHashMap.put("time",getString(R.string.find_local_lastweek));
            lastWeekHashMap.put("data",_listLastWeek);
            _listToListView.add(lastWeekHashMap);
        }
        if(_listLastMonth.size() > 0){
            HashMap lastMonthHashMap = new HashMap();
            lastMonthHashMap.put("time",getString(R.string.find_local_lastmonth));
            lastMonthHashMap.put("data",_listLastMonth);
            _listToListView.add(lastMonthHashMap);
        }
        if(_listLongAgo.size() > 0){
            HashMap longAgoHashMap = new HashMap();
            longAgoHashMap.put("time",getString(R.string.find_local_longago));
            longAgoHashMap.put("data",_listLongAgo);
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
