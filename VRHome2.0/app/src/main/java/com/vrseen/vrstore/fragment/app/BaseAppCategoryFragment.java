package com.vrseen.vrstore.fragment.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.activity.app.AppImageViewHolder;
import com.vrseen.vrstore.logic.AppLogic;
import com.vrseen.vrstore.model.app.AppCategoryData;
import com.vrseen.vrstore.model.app.AppListData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.ConfigDefaultImageUtils;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.DownloadUtils;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.view.LoadMoreFooterView;
import com.vrseen.vrstore.view.ScrollableLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.srain.cube.image.ImageLoader;
import in.srain.cube.util.NetworkStatusManager;
import in.srain.cube.views.list.ListViewDataAdapter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;

/**
 * app的基本fragment
 */

public class BaseAppCategoryFragment extends BaseCategoryFragment implements AbsListView.OnScrollListener {

    public static final String KEY_APP_CATEGORY = "KEY_APP_CATEGORY";
    private ListView _appListView;
    private View _view;
//    private TextView _noDataTextView;
    private ListViewDataAdapter<AppListData.App> _adapter;
    private List<AppListData.App> _appListArray;
    private int _character;
    private int _page = 1;
    private ImageLoader _imageLoader;
    private final int _limit = 15;
    private AppCategoryData.Category _category;
    private int _latest = 0;
    private int _count = 0;
    private View _thisFragment;

    private Context _context;

    private LoadMoreListViewContainer _loadMoreListViewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        _context = getActivity();

        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_common_listview, null);
            initView();
        } else {
            // 从parent删除
            ViewGroup parent = (ViewGroup) _thisFragment.getParent();
            if (parent != null) {
                parent.removeView(_thisFragment);
            }
        }

        mState = NetworkStatusManager.State.UNKNOWN;
        mReceiver = new ConnectivityBroadcastReceiver();
        startListening(_context);

        return _thisFragment;

    }

    private void initView() {
        _appListArray = new ArrayList<>();

        _appListView=(ListView) _thisFragment.findViewById(R.id.load_more_small_image_list_view);

        _category = (AppCategoryData.Category) getArguments().get(KEY_APP_CATEGORY);
        if (_category != null) {
            _character = _category.getId();
            _latest = _category.getLatest();
            setPageName("BaseAppCateFragment:"+_category.getName());
        }

        _view = _thisFragment;
//        _appListView = (ListView) _view.findViewById(R.id.common_listview);
//        _noDataTextView = (TextView) _view.findViewById(R.id.app_no_data);
//        _appListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        _appListView.setOnScrollListener(this);
        _imageLoader = ConfigDefaultImageUtils.getInstance().getAppImageLoader(getActivity().getApplicationContext());
        updateAppList(false);

        if (_adapter == null) {
            _adapter = new ListViewDataAdapter<>();
            _adapter.setViewHolderClass(this, AppImageViewHolder.class, _imageLoader);
        }

        _appListView.setAdapter(_adapter);

        _loadMoreListViewContainer = (LoadMoreListViewContainer) _thisFragment.findViewById(R.id.load_more_list_view_container);
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
                updateAppList(false);

            }

        });

//        _appListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == SCROLL_STATE_IDLE ||scrollState == SCROLL_STATE_FLING ||scrollState == SCROLL_STATE_TOUCH_SCROLL) {
//                    int lastPosition = view.getLastVisiblePosition() - 1;
//                    if(lastPosition<_adapter.getCount() - 2){
//                        lastPosition= view.getLastVisiblePosition() - 1;
//                    }
//                    if (_count != _adapter.getCount()) {
//                        _count = _adapter.getCount();
//
//                        Log.e("xxxxxxxxxxxxx",lastPosition+"    "+_adapter.getCount());
//                        if (lastPosition >= (_adapter.getCount() - 2)) {
//                            _page++;
//                            updateAppList(false);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
////                int lastPosition = view.getLastVisiblePosition() - 1;
////                if (_count != _adapter.getCount()) {
////                    _count = _adapter.getCount();
////                    Log.e("xxxxxxxxxxxxx", lastPosition + "    " + _adapter.getCount());
////                    if (lastPosition >= (_adapter.getCount() - 2)) {
////                        _page++;
////                        updateAppList(false);
////                    }
////                }
//            }
//        });

    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //temp
        return null;
    }

    //处理安装后的返回
    public void handlerInstall(Intent data) {
//		int id = 0;
//		if(data!= null)
//		{
//			id = data.getIntExtra("id",0);
//		}
//
//		ArrayList<App> list  = _adapter.getDataList();
//		for (int i = 0; i < list.size(); i++) {
//			App app = list.get(i);
//			int idValue = app.getId();
//			if(id > 0)
//			{
//				if(id == idValue )
//				{
//					initOneAppData(app);
//					return;
//				}
//			}
//			else
//			{
//				initOneAppData(app);
//			}
//		}
//
//		_adapter.notifyDataSetChanged();
    }

    private void initOneAppData(AppListData.App app) {
//		if (AppLogic.getInstance().isAppInstalled(getActivity(),app.getScheme()))
//		{
//			app.setIsDownLoad(true);
//		}else {
//			app.setIsDownLoad(false);
//		}
//		DownloadInfoVO downloadInfoVO = DownloadManagerLogic.getInstance().getDownloadVO(app.getId());
//		if( downloadInfoVO != null &&downloadInfoVO.getState() == HttpHandler.State.SUCCESS)
//		{
//			app.setIsLoaded(true);
//		}
//		else
//		{
//			app.setIsLoaded(false);
//		}
    }

    /**
     * 更新数据
     */
    private void updateAppList(final boolean isRefresh) {
        CommonRestClient.getInstance(getActivity()).getAppList(_page, _character, _latest, _limit, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(getActivity(), resp, e, R.string.app_get_list_error);
            }

            @Override
            public void onSuccess(Response resp) {
                AppListData appListData = (AppListData) resp.getModel();
                if (appListData != null && appListData.getData() != null && appListData.getData().size() > 0) {
                    _appListArray = appListData.getData();
                    if (isRefresh) {
                        _adapter.notifyDataSetChanged();
                    } else if (_appListArray.size() > 0) {
                        _adapter.getDataList().addAll(_appListArray);
                        _adapter.notifyDataSetChanged();
//                        _noDataTextView.setVisibility(View.GONE);
                        _appListView.setVisibility(View.VISIBLE);

                    }
                    boolean hasMore = true;
                    if (_appListArray.size() < _limit) {
                        hasMore = false;
                    } else {
                        hasMore = true;
                    }

                    _loadMoreListViewContainer.loadMoreFinish(_appListArray.isEmpty(), hasMore);
                }

            }
        });
    }

    @Override
    public View getScrollableView() {
//        if (_appListView != null)
        return _appListView;
//        return null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//		if(scrollState == SCROLL_STATE_IDLE)
//		{
//			int lastPosition = view.getLastVisiblePosition() - 1;
//			if(lastPosition >= (view.getCount() - 2)) {
//				_page++;
//				updateAppList();
//			}
//		}
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();

        _appListArray.clear();
        _appListArray = null;

        _adapter = null;
    }

    public void refresh(boolean refresh) {
        if (_adapter != null) {
            updateAppList(refresh);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAppList(true);
        MobclickAgent.onResume(_context);
        Log.e("YYYYYYYYYYYY","isResume"+_category.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(_context);
    }





    private boolean mListening;
    private NetworkStatusManager.State mState;
    private NetworkInfo mNetworkInfo;
    private NetworkInfo mOtherNetworkInfo;
    private String mReason;
    private boolean mIsFailOver;
    private boolean mIsWifi = false;
    private ConnectivityBroadcastReceiver mReceiver;

    private class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        private boolean isNetState = true;
        private Context mContext;

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            mContext = context;

            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || mListening == false) {

                return;
            }

            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (noConnectivity) {
                mState = NetworkStatusManager.State.NOT_CONNECTED;
            } else {
                mState = NetworkStatusManager.State.CONNECTED;
            }

            mNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            mOtherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            mReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            mIsFailOver = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            mIsWifi = NetworkStatusManager.checkIsWifi(_context);

            if (mNetworkInfo.isConnected() && mNetworkInfo.isAvailable())
            {
                // 连接成功后的业务逻辑
                // 具体的网络连接方式
                switch (mNetworkInfo.getType())
                {
                    case ConnectivityManager.TYPE_WIFI:
                        // 执行业务
                        if(isNetState == false)
                        {
                            isNetState = true;
                            //继续下载
                            resumeAllDownload();
                        }
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        // 执行业务
                        if(isNetState == false)
                        {
                            isNetState = true;
                            //如果当前正在下载，暂停下载，并弹出提示框，提醒用户是否继续下载
                            if(hasAnyDownload()){
                                stopAllDownload();
                                handleNoWifiState();
                            }
                        }
                        break;
                    default:
                        if(isNetState == true)
                        {
                            isNetState = false;
                            //如果正在下载，暂停下载
                            if(hasAnyDownload()){
                                stopAllDownload();
                            }
                        }
                        break;
                }
            }
            else
            {
                //沒有網絡連接
                isNetState = false;
                //如果正在下载，暂停下载
                if(hasAnyDownload()){
                    stopAllDownload();
                }
            }
        }
    }

    /**
     * 如果当前状态为正在下载，就暂停下载，并保存正在下载的条目的信息（ID），以便恢复下载；其他状态不处理
     */
    HashMap<String,Integer> map = new HashMap();
    private void stopAllDownload(){
        int k = 0;
        for(int i = 0; i < _adapter.getCount(); i++){
            AppListData.App av = _adapter.getItem(i);
            if(av.getState() == AppListData.App.State.STATE_LOADING){
                map.put("id" + k, av.getId());
                k++;
                AppLogic.pauseDownload(_context,av, DownloadUtils.getInstance().getDownloadVO(av.getId()));
            }
        }
    }

    private void resumeAllDownload(){
        for(int i = 0; i < _adapter.getCount(); i++){
            AppListData.App av = _adapter.getItem(i);
            for(int j = 0; j < map.size(); j++){
                if(av.getId() == map.get("id" + j)){
                    AppLogic.resumeDownload(_context,DownloadUtils.getInstance().getDownloadVO(av.getId()),av);
                }
            }
        }
        map.clear();
    }

    /**
     * 判断是否有应用正在下载，只要有一个正在下载，就返回TRUE
     */
    private boolean hasAnyDownload(){
        for(int i = 0; i < _adapter.getCount(); i++){
            AppListData.App av = _adapter.getItem(i);
            if(av.getState() == AppListData.App.State.STATE_LOADING){
                return true;
            }
        }
        return false;
    }

    private void handleNoWifiState(){ //无wifi，但有手机流量
        if( (boolean) SharedPreferencesUtils.getParam(_context.getApplicationContext(), SPFConstant.IS_NOWIFI_Download, false) ){  //非wifi可以播放

            //可以继续下载
            resumeAllDownload();

        }else { //弹框提醒
            DialogHelpUtils.getConfirmDialog(_context, _context.getString(R.string.app_no_wifi_download), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {  //确定, 保存到sp中
                    //继续下载
                    resumeAllDownload();
                    SharedPreferencesUtils.setParam(_context.getApplicationContext(), SPFConstant.IS_NOWIFI_Download, true);
                }
            }, null).show();
        }
    }

    public synchronized void startListening(Context context) {
        if (!mListening) {
            _context = context;

            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(mReceiver, filter);
            mListening = true;
        }
    }

    public synchronized void stopListening() {
        if (mListening) {
            _context.unregisterReceiver(mReceiver);
            _context = null;
            mNetworkInfo = null;
            mOtherNetworkInfo = null;
            mIsFailOver = false;
            mReason = null;
            mListening = false;
        }
    }
}
