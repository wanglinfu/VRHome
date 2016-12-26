package com.vrseen.vrstore.activity.panorama;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.db.DatabaseHelper;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.model.panorama.PanoramaCityData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.view.LetterListView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import in.srain.cube.util.NetworkStatusManager;


public class CitySelectActivity extends BaseActivity implements View.OnClickListener {
    private BaseAdapter adapter;
    private ResultListAdapter resultListAdapter;// 搜索结果adapter
    private ListView personList;// 总的listview
    private ListView resultList;// 搜索结果list
    private LetterListView letterListView; // A-Z listview
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private Handler handler;
    private ArrayList<PanoramaCityData> allCity_lists; // 所有城市列表
    private ArrayList<PanoramaCityData> city_lists;// 城市列表
    private ArrayList<PanoramaCityData> city_hot;// 热门城市
    private ArrayList<PanoramaCityData> city_result;// 搜索结果
    private ArrayList<PanoramaCityData> city_history;// 历史记录
    private EditText sh;// 搜索框
    private TextView tv_noresult;// 没有搜索结果

    private Context _context;

    private HotCityAdapter _hca;//热门城市
    private RecentlyCityAdapter rca;//历史记录

    private ImageView _backImageView;

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    ////
    private String currentCity = ""; // 用于保存定位到的城市
    private int locateProcess = 1; // 记录当前定位的状态 正在定位-定位成功-定位失败
    private boolean isNeedFresh;// 是否需要刷新

    private DatabaseHelper dbHelper;

//    private ProgressRelativeLayout _progressRelativeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        this.setPageName("CitySelectActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);

        _context = this;

//        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);
//        _progressRelativeLayout.showProgress();
//
//        if (!NetworkStatusManager.getInstance().isNetworkConnectedHasMsg(_context, false)) {
//            _progressRelativeLayout.showErrorText(getResources().getString(R.string.get_data_fail));
//            return;
//        }

        _backImageView = (ImageView) findViewById(R.id.iv_back);
        _backImageView.setOnClickListener(this);

        personList = (ListView) findViewById(R.id.list_view);
        allCity_lists = new ArrayList<>();
        city_hot = new ArrayList<>();
        city_result = new ArrayList<>();
        city_history = new ArrayList<>();
        resultList = (ListView) findViewById(R.id.search_result);
        sh = (EditText) findViewById(R.id.et_panorama_city_search);
        tv_noresult = (TextView) findViewById(R.id.tv_noresult);
        resultList.setVisibility(View.GONE);
        tv_noresult.setVisibility(View.GONE);

        dbHelper = new DatabaseHelper(this);

        sh.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString() == null || "".equals(s.toString())) {
                    letterListView.setVisibility(View.VISIBLE);
                    personList.setVisibility(View.VISIBLE);
                    resultList.setVisibility(View.GONE);
                    tv_noresult.setVisibility(View.GONE);
                } else {
                    city_result.clear();
                    letterListView.setVisibility(View.GONE);
                    personList.setVisibility(View.GONE);

                    getResultCityList(s.toString());

                    if (city_result.size() <= 0) {
                        tv_noresult.setVisibility(View.VISIBLE);
                        resultList.setVisibility(View.GONE);
                    } else {
                        tv_noresult.setVisibility(View.GONE);
                        resultList.setVisibility(View.VISIBLE);
                        resultListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        letterListView = (LetterListView) findViewById(R.id.letterListView);
        letterListView
                .setOnTouchingLetterChangedListener(new LetterListViewListener());


        alphaIndexer = new HashMap<>();
        handler = new Handler();
        isNeedFresh = true;

        personList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position >= 4) {

                    selectCity(allCity_lists.get(position));
                }
            }
        });

        locateProcess = 1;
        personList.setAdapter(adapter);

        resultListAdapter = new ResultListAdapter(this, city_result);
        resultList.setAdapter(resultListAdapter);
        resultList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                InsertCity(city_result.get(position));
                hisCityInit();
                rca.notifyDataSetChanged();
                selectCity(city_result.get(position));
            }
        });

        requestCityData();

        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        InitLocation();
        mLocationClient.start();
    }

    public void InsertCity(PanoramaCityData pcd) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from recentlycity where name = '"
                + pcd.getName() + "'", null);
        if (cursor.getCount() > 0) { //
            db.delete("recentlycity", "name = ?", new String[]{pcd.getName()});
        }
        ContentValues cv = new ContentValues();
        cv.put("cid", pcd.getId());
        cv.put("name", pcd.getName());
        cv.put("date", System.currentTimeMillis());
        db.insert("recentlycity", null, cv);
        db.close();
    }

    private void InitLocation() {
        // 设置定位参数
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(10000); // 10分钟扫描1次
        // 需要地址信息，设置为其他任何值（string类型，且不能为null）时，都表示无地址信息。
        option.setAddrType("all");
        // 设置是否返回POI的电话和地址等详细信息。默认值为false，即不返回POI的电话和地址信息。
//        option.setPoiExtraInfo(true);
        // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setProdName(getResources().getString(R.string.location_by_gps));
        // 禁用启用缓存定位数据
        option.disableCache(true);
        // 设置最多可返回的POI个数，默认值为3。由于POI查询比较耗费流量，设置最多返回的POI个数，以便节省流量。
//        option.setPoiNumber(3);
        // 设置定位方式的优先级。
        // 当gps可用，而且获取了定位结果时，不再发起网络请求，直接返回给用户坐标。这个选项适合希望得到准确坐标位置的用户。如果gps不可用，再发起网络请求，进行定位。
        option.setPriority(LocationClientOption.GpsFirst);
        mLocationClient.setLocOption(option);
    }

    private void cityInit(ArrayList<PanoramaCityData> list) {
        PanoramaCityData city = new PanoramaCityData(getResources().getString(R.string.location), "0", "-1"); // 当前定位城市
        allCity_lists.add(city);
        city = new PanoramaCityData(getResources().getString(R.string.recent), "1", "-1"); // 最近访问的城市
        allCity_lists.add(city);
        city = new PanoramaCityData(getResources().getString(R.string.hot), "2", "-1"); // 热门城市
        allCity_lists.add(city);
//        city = new PanoramaCityData("全部", "3", "-1"); // 全部城市
//        allCity_lists.add(city);

        city_lists = getCityList(list);

        allCity_lists.addAll(city_lists);

        hisCityInit();

        rca = new RecentlyCityAdapter(_context, city_history);

        _hca = new HotCityAdapter(_context, city_hot);

        setAdapter(allCity_lists, city_hot, city_history);

    }

    private void hisCityInit() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from recentlycity order by date desc limit 0, 3",
                null);
        if (!city_history.isEmpty()) {
            city_history.clear();
        }
        while (cursor.moveToNext()) {
            PanoramaCityData city = new PanoramaCityData();
            city.setId(cursor.getString(2));
            city.setName(cursor.getString(1));

            city_history.add(city);
        }
        cursor.close();
        db.close();
    }

    @SuppressWarnings("unchecked")
    private ArrayList<PanoramaCityData> getCityList(ArrayList<PanoramaCityData> listRequest) {

        ArrayList<PanoramaCityData> list = listRequest;

        if (list.size() <= 6) {
            city_hot = list;
        } else {
            for (int i = 0; i < 6; i++) {
                city_hot.add(list.get(i));
            }
        }

        // 将服务器返回的数据在此处赋予hotCityList以及city_lists
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from allcity");
        for (int i = 0; i < list.size(); i++) {
            PanoramaCityData city = list.get(i);
            ContentValues cv = new ContentValues();
            cv.put("cid", city.getId());
            cv.put("name", city.getName());
            cv.put("pinyin", city.getPinyin());
            db.insert("allcity", null, cv);
        }
        Collections.sort(list, comparator);
        return list;
    }

    private void requestCityData() {
        PanoramaRestClient.getInstance(_context).getCityData(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context, resp, e, getResources().getString(R.string.get_data_fail));
            }
            @Override
            public void onSuccess(Response resp) throws JSONException {
                JSONObject jo = (JSONObject) resp.getData();

                String str = jo.getString("data");

                Gson g = new Gson();

                ArrayList<PanoramaCityData> list = g.fromJson(str, new TypeToken<List<PanoramaCityData>>() {
                }.getType());

                cityInit(list);

            }
        }

        );
    }

    @SuppressWarnings("unchecked")
    private void getResultCityList(String keyword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from allcity where name like \"%"
                + keyword + "%\" or pinyin like \"%" + keyword + "%\"", null);
        PanoramaCityData city;
        Log.e("info", "length = " + cursor.getCount());
        while (cursor.moveToNext()) {
            city = new PanoramaCityData(cursor.getString(1), cursor.getString(3),
                    cursor.getString(2));
            city_result.add(city);
        }
        cursor.close();
        db.close();
        Collections.sort(city_result, comparator);
    }

    /**
     * a-z排序
     */
    @SuppressWarnings("rawtypes")
    Comparator comparator = new Comparator<PanoramaCityData>() {
        @Override
        public int compare(PanoramaCityData lhs, PanoramaCityData rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    private void setAdapter(ArrayList<PanoramaCityData> list, ArrayList<PanoramaCityData> hotList,
                            ArrayList<PanoramaCityData> hisCity) {
        adapter = new ListAdapter(this, list, hotList, hisCity);
        personList.setAdapter(adapter);
//        _progressRelativeLayout.showContent();
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation arg0) {
            Log.e("info", "city = " + arg0.getCity());
            if (!isNeedFresh) {
                return;
            }
            isNeedFresh = false;
            if (arg0.getCity() == null) {
                locateProcess = 3; // 定位失败
                personList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return;
            }
            currentCity = arg0.getCity().substring(0,
                    arg0.getCity().length() - 1);
            locateProcess = 2; // 定位成功
            personList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    private class ResultListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<PanoramaCityData> results = new ArrayList<PanoramaCityData>();

        public ResultListAdapter(Context context, ArrayList<PanoramaCityData> results) {
            inflater = LayoutInflater.from(context);
            this.results = results;
        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_panorama_city_list, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.name);
                viewHolder.id = (TextView) convertView.findViewById(R.id.id);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(results.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView name;
            TextView id;
        }
    }

    public class ListAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private ArrayList<PanoramaCityData> list;
        private ArrayList<PanoramaCityData> hotList;
        private ArrayList<PanoramaCityData> hisCity;
        final int VIEW_TYPE = 5;

        public ListAdapter(Context context, ArrayList<PanoramaCityData> list,
                           ArrayList<PanoramaCityData> hotList, ArrayList<PanoramaCityData> hisCity) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
            this.context = context;
            this.hotList = hotList;
            this.hisCity = hisCity;
            alphaIndexer = new HashMap<String, Integer>();
            sections = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                // 当前汉语拼音首字母
                String currentStr = list.get(i).getPinyin().substring(0, 1);
                // 上一个汉语拼音首字母，如果不存在为" "
                String previewStr = (i - 1) >= 0 ? list.get(i - 1).getPinyin().substring(0, 1)
                        : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = list.get(i).getPinyin().substring(0, 1);
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE;
        }

        @Override
        public int getItemViewType(int position) {
            return position < 4 ? position : 4;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView city;
            int viewType = getItemViewType(position);
            if (viewType == 0) { // 定位
                convertView = inflater.inflate(
                        R.layout.item_panorama_city_location, null);
                TextView locateHint = (TextView) convertView
                        .findViewById(R.id.tv_location_hint);
                city = (TextView) convertView
                        .findViewById(R.id.tv_location_city);
                city.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (locateProcess == 2) {
                            PanoramaCityData cityTemp = isLocationCityExist(currentCity);
                            if (cityTemp != null) {
                                selectCity(cityTemp);
                            } else {
                                ToastUtils.show(_context, getResources().getString(R.string.city_has_no_data_hint), 2000);
                            }

                        } else if (locateProcess == 3) {
                            locateProcess = 1;
                            personList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            mLocationClient.stop();
                            isNeedFresh = true;
                            InitLocation();
                            currentCity = "";
                            mLocationClient.start();
                        }
                    }
                });
                city.setText(currentCity);
                if (locateProcess == 1) { // 正在定位
                    locateHint.setText(getResources().getString(R.string.doing_location));
                    city.setVisibility(View.GONE);
                } else if (locateProcess == 2) { // 定位成功
                    locateHint.setText(getResources().getString(R.string.panorama_city_location_hint));
                    city.setVisibility(View.VISIBLE);
                    city.setText(currentCity);
                    mLocationClient.stop();
                } else if (locateProcess == 3) {
                    locateHint.setText(getResources().getString(R.string.location_fail));
                    city.setVisibility(View.VISIBLE);
                    city.setText(getResources().getString(R.string.select_retry));
                }
            } else if (viewType == 1) { // 最近访问城市
                convertView = inflater.inflate(
                        R.layout.item_panorama_city_recently, null);
                GridView rencentCity = (GridView) convertView
                        .findViewById(R.id.recent_city);
                rencentCity
                        .setAdapter(rca);
                rencentCity.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        PanoramaCityData panoramaCityData = hisCity.get(position);
                        InsertCity(panoramaCityData);
                        hisCityInit();
                        rca.notifyDataSetChanged();
                        selectCity(panoramaCityData);

                    }
                });
                TextView recentHint = (TextView) convertView
                        .findViewById(R.id.recentHint);
                recentHint.setText(getResources().getString(R.string.history_record));
            } else if (viewType == 2) {
                convertView = inflater.inflate(
                        R.layout.item_panorama_city_recently, null);
                final GridView hotCity = (GridView) convertView
                        .findViewById(R.id.recent_city);

                hotCity.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {


                        InsertCity(hotList.get(position));
                        hisCityInit();
                        _hca.notifyDataSetChanged();
                        selectCity(hotList.get(position));

                    }
                });
                hotCity.setAdapter(_hca);
                TextView hotHint = (TextView) convertView
                        .findViewById(R.id.recentHint);
                hotHint.setText(getResources().getString(R.string.panorama_city_hot_hint));
            } else
//            if (viewType == 3) {
//                convertView = inflater.inflate(R.layout.item_panorama_total_city, null);
//                convertView.setVisibility(View.GONE);
//            } else {
            {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_panorama_city_list, null);
                    holder = new ViewHolder();
                    holder.alpha = (TextView) convertView
                            .findViewById(R.id.alpha);
                    holder.name = (TextView) convertView
                            .findViewById(R.id.name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (position >= 1) {
                    holder.name.setText(list.get(position).getName());
                    String currentStr = list.get(position).getPinyin().substring(0, 1);
                    String previewStr = (position - 1) >= 0 ? list.get(
                            position - 1).getPinyin().substring(0, 1) : " ";
                    if (!previewStr.equals(currentStr)) {
                        holder.alpha.setVisibility(View.VISIBLE);
                        holder.alpha.setText(currentStr.substring(0, 1).toUpperCase());
                    } else {
                        holder.alpha.setVisibility(View.GONE);
                    }
                }
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }
    }

    private PanoramaCityData isLocationCityExist(String currentCity) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from allcity where name ='" + currentCity + "';", null);
        PanoramaCityData city = null;
        Log.e("info", "length = " + cursor.getCount());
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                city = new PanoramaCityData(cursor.getString(1), cursor.getString(3),
                        cursor.getString(2));
            }
        } else {
            city = null;
        }
        cursor.close();
        db.close();

        return city;

    }

    @Override
    protected void onStop() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        super.onStop();
    }

    class HotCityAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private ArrayList<PanoramaCityData> hotCitys;

        public HotCityAdapter(Context context, ArrayList<PanoramaCityData> hotCitys) {
            this.context = context;
            inflater = LayoutInflater.from(this.context);
            this.hotCitys = hotCitys;
        }

        @Override
        public int getCount() {
            return hotCitys.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_panorama_city, null);
            TextView city = (TextView) convertView
                    .findViewById(R.id.tv_city_item_name);
            city.setText(hotCitys.get(position).getName());
            return convertView;
        }
    }

    class RecentlyCityAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private ArrayList<PanoramaCityData> hisCitys;

        public RecentlyCityAdapter(Context context, ArrayList<PanoramaCityData> hisCitys) {
            this.context = context;
            inflater = LayoutInflater.from(this.context);
            this.hisCitys = hisCitys;
        }

        @Override
        public int getCount() {
            return hisCitys.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_panorama_city, null);
            TextView city = (TextView) convertView
                    .findViewById(R.id.tv_city_item_name);
            TextView id = (TextView) convertView
                    .findViewById(R.id.tv_city_item_id);
            city.setText(hisCitys.get(position).getName());
            id.setText(hisCitys.get(position).getId());
            return convertView;
        }
    }

    private boolean mReady;

    private boolean isScroll = false;

    private class LetterListViewListener implements
            LetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            isScroll = false;
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                personList.setSelection(position);
                // overlay.setText(s);
                // overlay.setVisibility(View.VISIBLE);
                // handler.removeCallbacks(overlayThread);
                // // 延迟一秒后执行，让overlay为不可见
                // handler.postDelayed(overlayThread, 1000);
            }
        }
    }

    @Override
    public void onClick(View v) {

        int vid = v.getId();

        switch (vid) {
            case R.id.iv_back:
                selectCity(new PanoramaCityData());

                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                selectCity(new PanoramaCityData());
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void selectCity(PanoramaCityData panoramaCityData) {
        Intent intent = new Intent(_context, Panorama1Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("city_id", panoramaCityData.getId() + "");
        bundle.putString("city_name", panoramaCityData.getName());
        intent.putExtras(bundle);
        setResult(0, intent);
        finish();
    }
}
