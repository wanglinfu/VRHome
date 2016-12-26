package com.vrseen.vrstore.fragment.find;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.find.LocalActivity;
import com.vrseen.vrstore.adapter.find.FindListViewAdapter;
import com.vrseen.vrstore.fragment.BaseFragment;
import com.vrseen.vrstore.logic.FileLogic;
import com.vrseen.vrstore.model.find.LocalResInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 项目名称：VRHome2.0
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/6/4 18:45
 * 修改人：Administrator
 * 修改时间：2016/6/4 18:45
 * 修改备注：
 */
public class LocalFragment extends BaseFragment implements View.OnClickListener {
    public static String LOCAL_TYPE = "localtype";
    private View _thisFragment;
    private Context _context;
    private List<LocalResInfo> _listLocalInfo = new ArrayList<LocalResInfo>();  //每个分类的全部数据
    public static ListView _localDataListView;
    public static int _type = -1;
    private ArrayList<HashMap> _listToListView = new ArrayList<HashMap>();
    //今天的
    private ArrayList<LocalResInfo> _listToday = new ArrayList<LocalResInfo>();
    //上周
    private ArrayList<LocalResInfo> _listLastWeek = new ArrayList<LocalResInfo>();
    //上月
    private ArrayList<LocalResInfo> _listLastMonth = new ArrayList<LocalResInfo>();
    //更早
    private ArrayList<LocalResInfo> _listLongAgo = new ArrayList<LocalResInfo>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.setPageName("LocalFragment");
        super.onCreateView(inflater, container, savedInstanceState);

        _context = getActivity();
        String str = (String) getArguments().get(LOCAL_TYPE);
        _type = Integer.valueOf(str);

        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_locals, null);
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
        _listLocalInfo.clear();
        _localDataListView = (ListView) _thisFragment.findViewById(R.id.lv_360_local);
        List<LocalResInfo> list = FileLogic.getInstance().localFileArrList;

        if (list.size() <= 0)
        {
            View emptyView= _thisFragment.findViewById(R.id.no_data_layout);
            _localDataListView.setEmptyView(emptyView);
            return;
        }


        for (int i = 0; i < list.size(); i++) {
            LocalResInfo vo = list.get(i);
            int type = vo.getType();
            if (type == _type) {
                _listLocalInfo.add(vo);
            }
        }

        handleData();   //根据时间封装不同数据


        //图片的显示样式和视频不一样，单独区分
        if (_type == VRHomeConfig.LOCAL_TYPE_PANO_IMAGE) {
            _localDataListView.setAdapter(new FindListViewAdapter(getActivity(), _listToListView, LocalActivity.LOCAL_IMAGE) {
                @Override
                public void refresh() {

                }
            });
        } else {
            _localDataListView.setAdapter(new FindListViewAdapter(getActivity(), _listToListView, LocalActivity.LOCAL_VIDEO) {
                @Override
                public void refresh() {

                }
            });
        }

    }


    private ArrayList<HashMap> handleData() {
        //遍历全部数据，根据时间放到不同arraylist集合中，然后把时间和集合封装到hashmap中，最后放到一个ArrayList中返回
        for (int i = 0; i < _listLocalInfo.size(); i++) {
            long lastModifiedTime = _listLocalInfo.get(i).getLastModifiedTime();
            long nowTime = System.currentTimeMillis();
            long DTime = nowTime - lastModifiedTime;
            if (DTime < 24 * 60 * 60 * 1000) {  //今天
                _listToday.add(_listLocalInfo.get(i));
            } else if (DTime < 7 * 24 * 60 * 60 * 1000) {    //上周
                _listLastWeek.add(_listLocalInfo.get(i));
            } else if (DTime < 30 * 24 * 60 * 60 * 1000) {    //上月
                _listLastMonth.add(_listLocalInfo.get(i));
            } else {    //更早
                _listLongAgo.add(_listLocalInfo.get(i));
            }
        }
        //如果集合中有数据，就放到hashmap中
        if (_listToday.size() > 0) {
            HashMap todayHashMap = new HashMap();
            todayHashMap.put("time", getString(R.string.find_local_today));
            todayHashMap.put("data", _listToday);
            _listToListView.add(todayHashMap);
        }
        if (_listLastWeek.size() > 0) {
            HashMap lastWeekHashMap = new HashMap();
            lastWeekHashMap.put("time", getString(R.string.find_local_lastweek));
            lastWeekHashMap.put("data", _listLastWeek);
            _listToListView.add(lastWeekHashMap);
        }
        if (_listLastMonth.size() > 0) {
            HashMap lastMonthHashMap = new HashMap();
            lastMonthHashMap.put("time", getString(R.string.find_local_lastmonth));
            lastMonthHashMap.put("data", _listLastMonth);
            _listToListView.add(lastMonthHashMap);
        }
        if (_listLongAgo.size() > 0) {
            HashMap longAgoHashMap = new HashMap();
            longAgoHashMap.put("time", getString(R.string.find_local_longago));
            longAgoHashMap.put("data", _listLongAgo);
            _listToListView.add(longAgoHashMap);
        }
        return _listToListView;
    }

    @Override
    public void onClick(View v) {

    }
}
