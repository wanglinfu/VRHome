package com.vrseen.vrstore.activity.film;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.adapter.film.FilmAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.model.film.FilmCateroryData;
import com.vrseen.vrstore.model.film.FilmFilterCateryData;
import com.vrseen.vrstore.model.film.FilmListData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.view.ExpGridView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;
import com.vrseen.vrstore.view.ScrollableLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 影视过滤
 * Created by mll on 2016/6/2.
 */
public class FilmFilterActivity extends BaseActivity implements View.OnClickListener {

    private static final String CATEGORY_DATA = "categoryData";
    private FilmCateroryData.Category _category;
    private Context _context;
    private LinearLayout _filterLL;
    private LayoutInflater _layoutInflater;
    private ExpGridView _filterGridView;
    private FilmAdapter _filmAdapter;
    private ScrollableLayout _scrollableLayout;

    private int index = 0;
    private int _itemGridHeight = 0;
    private int _iListCache = 0;
    private int _page = 1;
    private int _sort = 1;
    private String _strGener;

    private List<FilmListData.BaseFilm> _baseFilmList;
    private ScrollView _scrollView;
    private ProgressRelativeLayout _progressRelativeLayout;

    private Map<String, FilmFilterCateryData.FilmFilterCatery> _dataMap = new HashMap<>(); //所有的数据集合

    private Map<String, List<TextView>> _otherTextViewList = new HashMap<>(); //其他的textview（动态）
    private List<TextView> _textViewRankList = new ArrayList<>();//好评等textview（静态）

    private LinearLayout _selectedItemLinearLayout;
    private TextView _selectedItemTextView;
    private int _menuLinearLayout = -1;

    private Map<String, FilmFilterCateryData.FilmFilterCatery> _selectedItemMap;
    private String _strSeletedItem = "";

    private ImageView _menuPllImageView;

    private View _emptyView;

    public static void actionStart(Context context, FilmCateroryData.Category category) {
        Intent intent = new Intent(context, FilmFilterActivity.class);
        intent.putExtra(CATEGORY_DATA, category);
        CommonUtils.startActivityWithAnim(context, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("FilmFilterActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_film_filter);
        _category = (FilmCateroryData.Category) getIntent().getSerializableExtra(CATEGORY_DATA);
        _context = this;
        _strGener = String.valueOf(_category.getId());
//        _strSeletedItem = _category.getGenre_name();
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        _menuPllImageView=(ImageView)findViewById(R.id.iv_selected_item);
        _menuPllImageView.setOnClickListener(this);

        _emptyView = getLayoutInflater().inflate(R.layout.no_data_layout, null);
        _emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        _selectedItemMap = new HashMap<>();

        _selectedItemLinearLayout = (LinearLayout) findViewById(R.id.ll_selected_item);
        _selectedItemTextView = (TextView) findViewById(R.id.tv_selected_item);
        _selectedItemTextView.setVisibility(View.GONE);
        _menuPllImageView.setVisibility(View.GONE);
        _selectedItemLinearLayout.setVisibility(View.GONE);

        _scrollView = (ScrollView) findViewById(R.id.scroll_film_filter);
        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);
        _progressRelativeLayout.showProgress();

        _filterGridView = (ExpGridView) findViewById(R.id.gridview);
        _filterGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        _filterGridView.setEmptyView(findViewById(R.id.no_data_layout));

        _scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        index++;
                        break;
                    default:
                        break;
                }
                if (event.getAction() == MotionEvent.ACTION_UP && index > 0) {
                    index = 0;
                    View view = ((ScrollView) v).getChildAt(0);
                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight() + _itemGridHeight * 2) {
                        //加载数据代码
                        if (_iListCache != _filmAdapter.getCount()) {
                            _iListCache = _filmAdapter.getCount();
                            _page++;
                            getData();
                        }
                    }


                }
                return false;
            }
        });

        _filterLL = (LinearLayout) findViewById(R.id.ll_filter);
        _layoutInflater = LayoutInflater.from(this);

        ((TextView) findViewById(R.id.tv_title)).setText(_category.getGenre_name());

        _textViewRankList.add((TextView) findViewById(R.id.tv_good_rank));
        _textViewRankList.add((TextView) findViewById(R.id.tv_hot_rank));
        _textViewRankList.add((TextView) findViewById(R.id.tv_new_rank));

        _strSeletedItem=_textViewRankList.get(0).getText().toString().trim();
//        _selectedItemTextView.setText(_strSeletedItem);

        for (int i = 0; i < _textViewRankList.size(); i++) {
            TextView textview = _textViewRankList.get(i);
            textview.setOnClickListener(new FilterSortClickListener(i));
        }

        findViewById(R.id.ll_viewback).setOnClickListener(this);
        requestGetCateory();
    }

    /**
     * 获取筛选数据
     * 参数/返回
     * created at 2016/6/6
     */
    private void requestGetCateory() {

        CommonRestClient.getInstance(_context).getFilmFilter(String.valueOf(_category.getId()), new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {

                CommonUtils.showResponseMessage(_context, resp, e, R.string.film_filter_fail);

            }

            @Override
            public void onSuccess(Response resp) {

                _dataMap.clear();
                FilmFilterCateryData filmCateroryData = (FilmFilterCateryData) resp.getModel();

                //添加全部
                for (int i = 0; i < filmCateroryData.getData().size(); i++) {
                    String key = String.valueOf(i + 1);
                    List<FilmCateroryData.Category> list1 = filmCateroryData.getData().get(i).getGenres();
                    FilmCateroryData.Category category = new FilmCateroryData.Category();
                    category.setGenre_name(getString(R.string.film_all_film));
                    list1.add(0, category);

                    _dataMap.put(key, filmCateroryData.getData().get(i));

                    _strSeletedItem = _strSeletedItem + " . " + getString(R.string.film_all_film);
                    _selectedItemTextView.setText(_strSeletedItem);

                }

                //生成UI
                LinearLayout linearLayout;
                for (Map.Entry<String, FilmFilterCateryData.FilmFilterCatery> enter : _dataMap.entrySet()) {
                    String key = enter.getKey();
                    FilmFilterCateryData.FilmFilterCatery category = enter.getValue();
                    View screenView = _layoutInflater.inflate(R.layout.item_film_filter, _filterLL, false);
                    linearLayout = (LinearLayout) screenView.findViewById(R.id.ll_typeData);

                    //单个key对应的集合
                    List<TextView> textViewList = new ArrayList<>();
                    for (int i = 0; i < category.getGenres().size(); i++) {

                        View view = _layoutInflater.inflate(R.layout.item_film_filter_text,
                                (ViewGroup) screenView, false);

                        TextView textView = (TextView) view.findViewById(R.id.tv_filter);
                        textView.setText(category.getGenres().get(i).getGenre_name());
                        textView.setTextSize(15);
                        textView.setTag("" + i);
                        if (i == 0) {
                            textView.setTextColor(getResources().getColor(R.color.lite_blue));
                        } else {
                            textView.setTextColor(getResources().getColor(R.color.black));
                        }

                        textView.setOnClickListener(new FilterTypeClickListener(key));
                        linearLayout.addView(view);
                        textViewList.add(textView);
                    }
                    _otherTextViewList.put(key, textViewList);

                    _filterLL.addView(screenView);
                }
                getData();
            }
        });
    }


    //处理好评，热门等生成_sort
    private class FilterSortClickListener implements View.OnClickListener {
        private int _postion;

        public FilterSortClickListener(int postion) {
            _postion = postion;
        }

        @Override
        public void onClick(View v) {
            for (int i = 0; i < _textViewRankList.size(); i++) {
                if (_postion == i) {
                    _textViewRankList.get(i).setTextColor(getResources().getColor(R.color.lite_blue));
                    _sort = i + 1;

                    String[] str=_strSeletedItem.split("\\.");
                    str[0]=_textViewRankList.get(_postion).getText().toString().trim();
                    _strSeletedItem="";
                    for(int j=0;j<str.length;j++){
                        if(!str[j].equals("")){
                            if(j==str.length-1){
                                _strSeletedItem=_strSeletedItem+str[j].trim();
                            }else{
                                _strSeletedItem=_strSeletedItem+str[j].trim()+" . ";
                            }
                        }
                    }
//
                    _selectedItemTextView.setText(_strSeletedItem);

                } else {
                    _textViewRankList.get(i).setTextColor(getResources().getColor(R.color.black));
                }

            }

            if (_filmAdapter != null) {
                _filmAdapter.refreshData(new ArrayList<FilmListData.BaseFilm>());
            }
            _page = 1;
            _progressRelativeLayout.showProgress();
            getData();
        }
    }

    //处理各种分类,生成gener
    private class FilterTypeClickListener implements View.OnClickListener {
        private String _key;

        public FilterTypeClickListener(String type) {
            _key = type;
        }

        @Override
        public void onClick(View v) {

            FilmFilterCateryData.FilmFilterCatery filmFilterCatery = _dataMap.get(_key);
            List<TextView> textViewList = _otherTextViewList.get(_key);
            int position = Integer.valueOf(v.getTag().toString());

            for (int i = 0; i < filmFilterCatery.getGenres().size(); i++) {
                if (i == position) {
                    textViewList.get(i).setTextColor(getResources().getColor(R.color.lite_blue));

                    FilmFilterCateryData.FilmFilterCatery categoryTemp = new FilmFilterCateryData.FilmFilterCatery();
                    categoryTemp.setId(filmFilterCatery.getGenres().get(i).getId());
                    categoryTemp.setGenre_name(filmFilterCatery.getGenres().get(i).getGenre_name());

                    if (_selectedItemMap.containsKey(_key)) {
                        _selectedItemMap.remove(_key);
                    }

                    _selectedItemMap.put(_key, categoryTemp);

                    _strGener = String.valueOf(_category.getId());

                    String[] arr = _strSeletedItem.split("\\.");

                    for (String key : _selectedItemMap.keySet()) {
                        if (_selectedItemMap.get(key).getId() != 0) {
                            _strGener = _strGener + "," + _selectedItemMap.get(key).getId();
                        }
                        if (_selectedItemMap.get(key).getId() != 0) {
                            arr[Integer.valueOf(key)] = _selectedItemMap.get(key).getGenre_name();
                        } else {
                            arr[Integer.valueOf(key)] = getResources().getString(R.string.film_all_film);
                        }
                    }
                    String s = "";
                    for (int j = 0; j < arr.length; j++) {
                        if(!arr[j].equals("")) {
                            if (j == arr.length - 1) {
                                s = s + arr[j].trim();
                            } else {
                                s = s + arr[j].trim() + " . ";
                            }
                        }
                    }
                    _selectedItemTextView.setText(s);

                } else {
                    textViewList.get(i).setTextColor(getResources().getColor(R.color.black));
                }
            }
            //重新生成数据
            if (_filmAdapter != null) {
                _filmAdapter.refreshData(new ArrayList<FilmListData.BaseFilm>());
            }
            _page = 1;
            _progressRelativeLayout.showProgress();
            getData();
        }
    }

    //获取筛选的数据
    private void getData() {

        CommonRestClient.getInstance(_context).getVideoList(_strGener, _page, _sort, 12, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {

                CommonUtils.showResponseMessage(_context, resp, e, R.string.film_filter_data_fail);
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {


                FilmListData filmListData = (FilmListData) resp.getModel();
                if (filmListData != null && filmListData.getData().size() > 0) {
                    _baseFilmList = filmListData.getData();
                    if (_baseFilmList == null && _baseFilmList.size() <= 0) {
                        if (_filmAdapter.getCount() > 0) {
                            ToastUtils.showOnly(_context, getString(R.string.no_last_data), 350);
                        }
                        return;
                    }

                    if (_filmAdapter == null) {
                        _filmAdapter = new FilmAdapter(_context, _baseFilmList);
                        _filterGridView.setAdapter(_filmAdapter);

                        View view = _filmAdapter.getView(0, null, _filterGridView);
                        if (view != null) {
                            view.measure(0, 0);
                            _itemGridHeight = view.getMeasuredHeight();
                        }
                    } else {
                        _filmAdapter.addData(_baseFilmList);
                        if (_baseFilmList.size() <= 0) {
                            ((ViewGroup) _filterGridView.getParent()).removeView(_emptyView);
                            ((ViewGroup) _filterGridView.getParent()).addView(_emptyView);
                        } else {
                            ((ViewGroup) _filterGridView.getParent()).removeView(_emptyView);
                        }
                    }
                } else {
                    if (_filmAdapter.getCount() <= 0) {
                        ((ViewGroup) _filterGridView.getParent()).removeView(_emptyView);
                        ((ViewGroup) _filterGridView.getParent()).addView(_emptyView);
                    } else {
                        ((ViewGroup) _filterGridView.getParent()).removeView(_emptyView);
                    }
                }
//                _scrollView.smoothScrollTo(0, 0);
                _progressRelativeLayout.showContent();

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_viewback:

                this.finish();
                break;
            case R.id.iv_selected_item:
                _scrollView.smoothScrollTo(0,0);
                _selectedItemTextView.setVisibility(View.GONE);
                _menuPllImageView.setVisibility(View.GONE);
                _selectedItemLinearLayout.setAlpha(0);
                _selectedItemLinearLayout.setVisibility(View.GONE);
                break;
            default:
                break;

        }

    }

    /**
     * y1手指按下
     * y2手指抬起
     */
    float y1 = 0;
    float y2 = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        float distance = getResources().getDimension(R.dimen.menu_disappear_distance);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y1 = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:


                y2 = ev.getY();
                if (y2 - y1 > distance) {
                    _selectedItemLinearLayout.setAlpha(0);
                    _selectedItemTextView.setVisibility(View.GONE);
                    _menuPllImageView.setVisibility(View.GONE);
                    _selectedItemLinearLayout.setVisibility(View.GONE);
                } else if (y2 - y1 < -distance) {
                    if (_scrollView.getScrollY() != 0) {
                        _selectedItemLinearLayout.setAlpha(1);
                        _selectedItemTextView.setVisibility(View.VISIBLE);
                        _menuPllImageView.setVisibility(View.VISIBLE);
                        _selectedItemLinearLayout.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                y2 = ev.getY();
                if (y2 - y1 > distance) {
                    _selectedItemLinearLayout.setAlpha(0);
                    _selectedItemTextView.setVisibility(View.GONE);
                    _menuPllImageView.setVisibility(View.GONE);
                    _selectedItemLinearLayout.setVisibility(View.GONE);
                } else if (y2 - y1 < -distance) {
                    if (_scrollView.getScrollY() != 0) {
                        _selectedItemLinearLayout.setAlpha(1);
                        _selectedItemTextView.setVisibility(View.VISIBLE);
                        _menuPllImageView.setVisibility(View.VISIBLE);
                        _selectedItemLinearLayout.setVisibility(View.VISIBLE);
                    }
                }
                y1 = 0;
                y2 = 0;
                break;

        }

        return super.dispatchTouchEvent(ev);
    }

}
