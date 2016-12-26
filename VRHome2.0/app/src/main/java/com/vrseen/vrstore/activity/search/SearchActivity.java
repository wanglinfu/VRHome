package com.vrseen.vrstore.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.adapter.search.SearchResultAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.SearchRestClient;
import com.vrseen.vrstore.model.app.AppListData;
import com.vrseen.vrstore.model.search.FilmSearchResultData;
import com.vrseen.vrstore.model.search.PanoramaSearchResultData;
import com.vrseen.vrstore.model.search.SearchHotKeywordData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.ExpGridView;
import com.vrseen.vrstore.view.LoadMoreFooterView;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import in.srain.cube.util.NetworkStatusManager;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;

/**
 * 项目名称：VRHome2.0
 * 类描述：搜索activity
 * 创建人：郝晓辉
 * 创建时间：2016/6/10 11:43
 * 修改人：郝晓辉
 * 修改时间：2016/6/10 11:43
 * 修改备注：
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private Context _context;

    /**
     * 关键字输入框
     */
    private EditText _searchKeywordEditText;
    /**
     * 关键字清空
     */
    private ImageView _searchKeywordClearImageView;
    /**
     * 取消搜索
     */
    private Button _searchCancelButton;
    /**
     * 搜索类型
     */
    private LinearLayout _searchTypeLinearLayout;
    /**
     * 搜索历史
     */
    private LinearLayout _searchHistoryLinearLayout;
    /**
     * 热门搜索
     */
    private LinearLayout _searchHotLinearLayout;
    /**
     * 清空历史记录
     */
    private TextView _searchHistoryClearTextView;
    /**
     * 搜索历史关键字
     */
    private ExpGridView _searchHistoryGridView;
    /**
     * 热门搜索关键字
     */
    private ExpGridView _searchHotGridView;
    /**
     * 由哪一个activity启动
     */
    private int _from = -1;
    /**
     * 类型列表
     */
    private List<String> _listType;
    /**
     * 类型textview列表
     */
    private List<TextView> _listTypeView;
    /**
     * 搜索历史关键字
     */
    private List<String> _listHistoryKeyword;
    /**
     * 热门搜索关键字
     */
    private List<String> _listHotKeyword;

    private ScrollView _searchScrollView;

    /**
     * 临时使用sharePreferences，以后使用数据库代替
     */
    private List<String> _listHistory = null;
    private SharedPreferences sharedPreferences;

    private int _page = 1;
    private int _limit = 10;
    /**
     * 搜索结果adapter
     */
    private SearchResultAdapter _searchResultAdapter;
    /**
     * 搜索历史adapter
     */
    private ArrayAdapter _arrayHistoryAdapter;

    /**
     * 关键字
     */
    private String _searchKeyword = "";

    private boolean _isTypeTitleExist = false;
    /**
     * listview为空时显示的视图
     */
    private View _emptyView;

    private ProgressRelativeLayout _progressRelativeLayout;

    private LoadMoreListViewContainer _loadMoreListViewContainer;
    private ListView _searchResultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setPageName("SearchActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        _context = this;

        _from = getIntent().getIntExtra("from", -1);

        if (_from == -1) {
            _isTypeTitleExist = true;
        }

        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);
        if(!NetworkStatusManager.getInstance(this).isNetworkConnectedHasMsg(false)){
            _progressRelativeLayout.showErrorText(getResources().getString(R.string.get_data_fail));

            return;
        }

        _searchKeywordEditText = (EditText) findViewById(R.id.et_search_title_keyword);
        _searchCancelButton = (Button) findViewById(R.id.btn_search_title_cancel);
        _searchKeywordClearImageView = (ImageView) findViewById(R.id.iv_search_keyword_clear);
        _searchTypeLinearLayout = (LinearLayout) findViewById(R.id.ll_search_type);
        _searchHistoryLinearLayout = (LinearLayout) findViewById(R.id.ll_search_history);
        _searchHistoryClearTextView = (TextView) findViewById(R.id.tv_search_history_clear);
        _searchHistoryGridView = (ExpGridView) findViewById(R.id.gv_search_history_keyword);
        _searchHotGridView = (ExpGridView) findViewById(R.id.gv_search_hot_keyword);
        _searchScrollView = (ScrollView) findViewById(R.id.sv_search);
        _loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_list_view_container);
        _searchResultListView = (ListView) findViewById(R.id.load_more_small_image_list_view);
        _loadMoreListViewContainer.setVisibility(View.GONE);

        _emptyView = getLayoutInflater().inflate(R.layout.no_data_layout, null);
        _emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        _searchHistoryClearTextView.setOnClickListener(this);
        _searchKeywordClearImageView.setOnClickListener(this);

        _searchKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    _searchKeyword = _searchKeywordEditText.getText().toString();

                    initSearchView();
                    if (!_searchKeyword.equals("")) {
                        requestSearchResult(_searchKeyword, true);
                    }
                    return true;
                }
                return false;
            }
        });

        _searchKeywordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String t = _searchKeywordEditText.getText().toString();
                String editable = _searchKeywordEditText.getText().toString();
                String str = StringFilter(editable.toString());
                if(!editable.equals(str)){
                    _searchKeywordEditText.setText(str);
                    _searchKeywordEditText.setSelection(str.length()); //光标置后
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

        _searchCancelButton.setOnClickListener(this);
        /**
         * 头部搜索类型栏是否显示
         */
        if (_isTypeTitleExist) {
            _searchTypeLinearLayout.setVisibility(View.VISIBLE);
            _listType = new ArrayList<>();
            _listType.add(getResources().getString(R.string.tab_video));
            _listType.add(getResources().getString(R.string.tab_apps));
            _listType.add(getResources().getString(R.string.panorama));
            initType();
        } else {
            _searchTypeLinearLayout.setVisibility(View.GONE);
        }

        initHistory();

        if (_from != -1) {
            requestHotKeyword(_from);
        }

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
                if (!_searchKeyword.equals("")) {
                    requestSearchResult(_searchKeyword, false);
                }

            }

        });

    }

    /**
     * 初始化搜索结果界面
     */
    private void initSearchView() {

        _progressRelativeLayout.showProgress();

        if (!_listHistory.contains(_searchKeyword)) {
            _listHistory.add(_searchKeyword);
        } else {
            _listHistory.remove(_searchKeyword);
            _listHistory.add(_searchKeyword);
        }
        String history = "";
        for (int i = 0; i < _listHistory.size(); i++) {
            if (i == 0) {
                history = history + _listHistory.get(i);
            } else {
                history = history + ";" + _listHistory.get(i);
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("history", history);
        editor.commit();

        _searchScrollView.setVisibility(View.GONE);
        _loadMoreListViewContainer.requestFocus();
        _loadMoreListViewContainer.setFocusableInTouchMode(true);
        _loadMoreListViewContainer.setFocusable(true);
        _loadMoreListViewContainer.setVisibility(View.VISIBLE);

    }

    /**
     * 初始化历史搜索历史界面
     */
    private void initHistory() {

        _listHistory = new ArrayList<>();

        sharedPreferences = _context.getSharedPreferences("keyword", Activity.MODE_PRIVATE);
        String history = sharedPreferences.getString("history", "");

        if (history.equals("")) {
            _searchHistoryClearTextView.setVisibility(View.GONE);
        } else {
            _searchHistoryClearTextView.setVisibility(View.VISIBLE);
        }

        String[] arrayHistory = history.split(";");


        if (arrayHistory != null && arrayHistory.length > 0) {

            if (arrayHistory.length > 10) {
                for (int i = arrayHistory.length - 1; i >= arrayHistory.length - 10; i--) {
                    if (!arrayHistory[i].equals("")) {
                        _listHistory.add(arrayHistory[i]);
                    }
                }
            } else {
                for (int i = arrayHistory.length - 1; i >= 0; i--) {
                    if (!arrayHistory[i].equals("")) {
                        _listHistory.add(arrayHistory[i]);
                    }
                }
            }

            _arrayHistoryAdapter = new ArrayAdapter(_context, R.layout.item_text, _listHistory);

            _searchHistoryGridView.setAdapter(_arrayHistoryAdapter);
            _searchHistoryLinearLayout.setVisibility(View.VISIBLE);

            _searchHistoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    _searchKeyword = _listHistory.get(position);

//                    _searchKeywordEditText.setText(_searchKeyword);
                    _searchKeywordEditText.setText(_searchKeyword);
                    initSearchView();
                    if (!_searchKeyword.equals("")) {
                        requestSearchResult(_searchKeyword, true);
                    }
                }
            });

        } else {
            _searchHistoryLinearLayout.setVisibility(View.GONE);
        }

    }

    /**
     * 请求热门关键词
     *
     * @param type 搜索类型
     */
    private void requestHotKeyword(int type) {

        SearchRestClient.getInstance(_context).getHotKeywords(type, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(SearchActivity.this, resp, e, getResources().getString(R.string.get_data_fail));

            }

            @Override
            public void onSuccess(Response resp) throws JSONException {

                SearchHotKeywordData searchHotKeywordData = (SearchHotKeywordData) resp.getModel();

                if (searchHotKeywordData != null && searchHotKeywordData.getData() != null && searchHotKeywordData.getData().size() > 0) {
                    List<SearchHotKeywordData.SearchHotKeyword> listKeyword = searchHotKeywordData.getData();

                    final List<String> keywords = new ArrayList<String>();
                    for (int i = 0; i < listKeyword.size(); i++) {
                        keywords.add(listKeyword.get(i).getSearch_key());
                    }

                    ArrayAdapter arrayAdapter = new ArrayAdapter(_context, R.layout.item_text, keywords);
                    _searchHotGridView.setAdapter(arrayAdapter);

                    _searchHotGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            _searchKeyword = keywords.get(position);

//                            _searchKeywordEditText.setText(_searchKeyword);
                            _searchKeywordEditText.setText(_searchKeyword);
                            initSearchView();
                            if (!_searchKeyword.equals("")) {
                                requestSearchResult(_searchKeyword, true);
                            }

                        }
                    });

                }

            }
        });

    }

    /**
     * 请求搜索结果
     *
     * @param keyword   关键词
     * @param isRefresh 是重新加载还是加载更多
     */
    private void requestSearchResult(String keyword, final boolean isRefresh) {

        SearchRestClient.getInstance(_context).getSearchResult(_from, _page, _limit, keyword, new AbstractRestClient.ResponseCallBack() {

            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(SearchActivity.this, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {
                switch (_from) {
                    case SearchRestClient.VIDEO_TAG:

                        FilmSearchResultData filmListData = (FilmSearchResultData) resp.getModel();
                        if (filmListData != null && filmListData.getData() != null && filmListData.getData().size() >= 0) {
                            List<FilmSearchResultData.FilmSearchResult> listFilm = filmListData.getData();


                            if (isRefresh) {
                                _searchResultAdapter = new SearchResultAdapter(_context, listFilm, null, null, SearchRestClient.VIDEO_TAG);
                                _searchResultListView.setAdapter(_searchResultAdapter);


                                if (filmListData.getData().isEmpty()) {
                                    _loadMoreListViewContainer.setVisibility(View.GONE);
                                    ((ViewGroup) _loadMoreListViewContainer.getParent()).removeView(_emptyView);
                                    ((ViewGroup) _loadMoreListViewContainer.getParent()).addView(_emptyView);
                                } else {
                                    _loadMoreListViewContainer.setVisibility(View.VISIBLE);
                                    ((ViewGroup) _loadMoreListViewContainer.getParent()).removeView(_emptyView);
                                }

                            } else {
                                _searchResultAdapter.addData(listFilm, null, null, SearchRestClient.VIDEO_TAG);
//                                if (listFilm.isEmpty()) {
//                                    ToastUtils.show(_context, "没有更多数据", 1000);
//                                }
                            }
                            boolean hasMore = true;
                            if (listFilm.size() < _limit) {
                                hasMore = false;
                            } else {
                                hasMore = true;
                            }
                            _loadMoreListViewContainer.loadMoreFinish(listFilm.isEmpty(), hasMore);
                        }

                        break;
                    case SearchRestClient.APPLICATION_TAG:

                        AppListData appListData = (AppListData) resp.getModel();

                        if (appListData != null && appListData.getData() != null && appListData.getData().size() >= 0) {
                            List<AppListData.App> listApp = appListData.getData();
                            if (isRefresh) {
                                _searchResultAdapter = new SearchResultAdapter(_context, null, listApp, null, SearchRestClient.APPLICATION_TAG);
                                _searchResultListView.setAdapter(_searchResultAdapter);


                                if (appListData.getData().isEmpty()) {
                                    _loadMoreListViewContainer.setVisibility(View.GONE);
                                    ((ViewGroup) _loadMoreListViewContainer.getParent()).removeView(_emptyView);
                                    ((ViewGroup) _loadMoreListViewContainer.getParent()).addView(_emptyView);
                                } else {
                                    _loadMoreListViewContainer.setVisibility(View.VISIBLE);
                                    ((ViewGroup) _loadMoreListViewContainer.getParent()).removeView(_emptyView);
                                }

                            } else {
                                _searchResultAdapter.addData(null, listApp, null, SearchRestClient.APPLICATION_TAG);
//                                if (listApp.isEmpty()) {
//                                    ToastUtils.show(_context, "没有更多数据", 1000);
//                                }
                            }

                            boolean hasMore = true;
                            if (listApp.size() < _limit) {
                                hasMore = false;
                            } else {
                                hasMore = true;
                            }
                            _loadMoreListViewContainer.loadMoreFinish(listApp.isEmpty(), hasMore);
                        }

                        break;
                    case SearchRestClient.PANORAMA_TAG:

                        PanoramaSearchResultData panoramaListData = (PanoramaSearchResultData) resp.getModel();

                        if (panoramaListData != null && panoramaListData.getData() != null && panoramaListData.getData().size() >= 0) {
                            List<PanoramaSearchResultData.PanoramaSearchResult> listPanorama = panoramaListData.getData();
                            if (isRefresh) {
                                _searchResultAdapter = new SearchResultAdapter(_context, null, null, listPanorama, SearchRestClient.PANORAMA_TAG);
                                _searchResultListView.setAdapter(_searchResultAdapter);


                                if (panoramaListData.getData().isEmpty()) {
                                    _loadMoreListViewContainer.setVisibility(View.GONE);
                                    ((ViewGroup) _loadMoreListViewContainer.getParent()).removeView(_emptyView);
                                    ((ViewGroup) _loadMoreListViewContainer.getParent()).addView(_emptyView);
                                } else {
                                    _loadMoreListViewContainer.setVisibility(View.VISIBLE);
                                    ((ViewGroup) _loadMoreListViewContainer.getParent()).removeView(_emptyView);
                                }

                            } else {
                                _searchResultAdapter.addData(null, null, listPanorama, SearchRestClient.PANORAMA_TAG);
//                                if (listPanorama.isEmpty()) {
//                                    ToastUtils.show(_context, "没有更多数据", 1000);
//                                }
                            }

                            boolean hasMore = true;
                            if (listPanorama.size() < _limit) {
                                hasMore = false;
                            } else {
                                hasMore = true;
                            }
                            _loadMoreListViewContainer.loadMoreFinish(listPanorama.isEmpty(), hasMore);
                        }

                        break;
                }


                _progressRelativeLayout.showContent();

            }
        });

    }


    /**
     * 初始化搜索类型
     */
    private void initType() {
        _listTypeView = new ArrayList<>();
        for (int i = 0; i < _listType.size(); i++) {
            TextView tv = new TextView(_context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50, 0, 0, 0);
            tv.setLayoutParams(layoutParams);
            tv.setTextSize(15);
            tv.setGravity(Gravity.CENTER);
            tv.setText(_listType.get(i));
            _searchTypeLinearLayout.addView(tv);
            _listTypeView.add(tv);
        }

        isViewSelected(_listTypeView, 0);
        _from = SearchRestClient.VIDEO_TAG;

        for (int i = 0; i < _listTypeView.size(); i++) {
            final int position = i;
            _listTypeView.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isViewSelected(_listTypeView, position);
                    _page = 1;
                    if (position == 0) {
                        _from = SearchRestClient.VIDEO_TAG;
                        if (_searchScrollView.getVisibility() == View.VISIBLE) {
                            requestHotKeyword(_from);
                        } else {
                            if (!_searchKeyword.equals("")) {
                                _progressRelativeLayout.showProgress();
                                requestSearchResult(_searchKeyword, true);
                            }
                        }
                    } else if (position == 1) {
                        _from = SearchRestClient.APPLICATION_TAG;
                        if (_searchScrollView.getVisibility() == View.VISIBLE) {
                            requestHotKeyword(_from);
                        } else {
                            if (!_searchKeyword.equals("")) {
                                _progressRelativeLayout.showProgress();
                                requestSearchResult(_searchKeyword, true);
                            }
                        }
                    } else if (position == 2) {
                        _from = SearchRestClient.PANORAMA_TAG;
                        if (_searchScrollView.getVisibility() == View.VISIBLE) {
                            requestHotKeyword(_from);
                        } else {
                            if (!_searchKeyword.equals("")) {
                                _progressRelativeLayout.showProgress();
                                requestSearchResult(_searchKeyword, true);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * item选择效果
     *
     * @param tvList   TextView的list
     * @param position 被点击的位置
     */
    private void isViewSelected(List<TextView> tvList, int position) {
        for (int i = 0; i < tvList.size(); i++) {
            if (i == position) {
                tvList.get(i).setTextColor(getResources().getColor(R.color.common_app_color));
            } else {
                tvList.get(i).setTextColor(getResources().getColor(R.color.contentColor));
            }
        }
    }

    public static String StringFilter(String str)throws PatternSyntaxException {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+_×÷|{}【】‘；\'：\"”“’。，、？]"; //要过滤掉的字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    @Override
    public void onClick(View v) {

        int vid = v.getId();
        switch (vid) {
            case R.id.btn_search_title_cancel:
                finish();
                break;
            case R.id.tv_search_history_clear:
                _listHistory.clear();
                _searchHistoryClearTextView.setVisibility(View.GONE);
                _arrayHistoryAdapter.notifyDataSetChanged();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                break;
            case R.id.iv_search_keyword_clear:
                _searchKeywordEditText.setText("");
                _loadMoreListViewContainer.setVisibility(View.GONE);
                _searchScrollView.setVisibility(View.VISIBLE);
                _searchResultListView.setAdapter(null);
                ((ViewGroup) _searchResultListView.getParent()).removeView(_emptyView);
                initHistory();
                requestHotKeyword(_from);
                break;
        }

    }

}
