package com.vrseen.vrstore.adapter.find;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.find.DownloadActivity;
import com.vrseen.vrstore.fragment.find.DownloadAppFragment;
import com.vrseen.vrstore.fragment.find.DownloadPanoFragment;
import com.vrseen.vrstore.logic.AppLogic;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.model.panorama.PanoramaDetailData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.ConfigDefaultImageUtils;
import com.vrseen.vrstore.util.DownloadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;


public class FindDownloadGridViewAdapter extends BaseAdapter {

    private   Context _context;
    private  ArrayList<DownloadInfo> _list;
    private  BitmapUtils _bitmapUtils;
    private  String _tag;
    private ImageLoader _imageLoader;

    public FindDownloadGridViewAdapter(Context context, ArrayList<DownloadInfo> list, String tag) {
        _context = context;
        _list = list;
        _tag = tag;
        _bitmapUtils = new BitmapUtils(_context);
        _bitmapUtils.configDefaultLoadFailedImage(R.drawable.jiazaishibai_yingyong);//加载失败图片
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public Object getItem(int position) {
        return _list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void handlerDown(final ViewHolder holder, final DownloadInfo downloadInfo)
    {
       final HttpHandler.State state = downloadInfo.getState();
       if(state == HttpHandler.State.FAILURE || state == HttpHandler.State.CANCELLED)
        {
            //重新下载
            try {
                DownloadUtils.getInstance().resumeDownload(downloadInfo, new RequestCallBack<File>() {
                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        downloadInfo.setState(HttpHandler.State.LOADING);
                        showDownText(holder,downloadInfo);
                    }

                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        downloadInfo.setState(HttpHandler.State.SUCCESS);
                        showDownText(holder,downloadInfo);
                        //如果是应用就安装
                        if(_tag.equals(DownloadActivity.DOWNLOAD_APP)){
                            AppLogic.installAPP(_context,downloadInfo.getFileSavePath());
                        }
                    }

                    public void onFailure(HttpException error, String msg) {
                        downloadInfo.setState(HttpHandler.State.FAILURE);
                        showDownText(holder,downloadInfo);
                    }

                });
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        else if(state == HttpHandler.State.SUCCESS)
        {
            switch (_tag) {
                case DownloadActivity.DOWNLOAD_APP:

                    if(AppLogic.isAppInstalled(_context,downloadInfo.getScheme()))
                    {
                        AppLogic.startApp(_context,downloadInfo.getScheme());
                        return;
                    }
                    //安装
                    AppLogic.installAPP(_context,downloadInfo.getFileSavePath());
                    break;
                case DownloadActivity.DOWNLOAD_PANO:
                    PanoramaDetailData.PanoramaDetail panoramaDetail = new PanoramaDetailData.PanoramaDetail();
                    panoramaDetail.setStoragepath(downloadInfo.getFileSavePath());
                    panoramaDetail.setId((int) downloadInfo.getId());
                    U3dMediaPlayerLogic.getInstance().startPlayPanorama(_context,panoramaDetail, VRHomeConfig.TYPE_VR,false,VRHomeConfig.VR_MYDOWNLOAD_ID);
                    break;
            }

        }
        else if(state == HttpHandler.State.LOADING)
        {
            downloadInfo.setState(HttpHandler.State.CANCELLED);
            try {
                DownloadUtils.getInstance().stopDownload(downloadInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }

            showDownText(holder,downloadInfo);
        }
    }

    private void showDownText(ViewHolder holder,DownloadInfo downloadInfo)
    {
        switch (_tag) {
            case DownloadActivity.DOWNLOAD_APP:
                if(AppLogic.isAppInstalled(_context,downloadInfo.getScheme()))
                {
                    holder._downloadProgressTextView.setVisibility(View.GONE);
                    holder._downloadProgressTextView.setText(_context.getString(R.string.app_intall));
                }
                break;
            case DownloadActivity.DOWNLOAD_PANO:
                break;
        }

        holder._filePictureCubeImageView.setAlpha(1.0f);

        if(downloadInfo.getState() == HttpHandler.State.SUCCESS)
        {
            holder._downloadProgressTextView.setVisibility(View.GONE);
            holder._saveDownloadImageView.setVisibility(View.GONE);
            //移除定时器
            holder._handler.removeCallbacks(holder._runnable);
        }
        else if(downloadInfo.getState() == HttpHandler.State.LOADING)
        {
            holder._filePictureCubeImageView.setAlpha(0.5f);
            holder._downloadProgressTextView.setVisibility(View.VISIBLE);
            holder._saveDownloadImageView.setVisibility(View.GONE);
            float current = (float)downloadInfo.getProgress();
            float total = (float)downloadInfo.getFileLength();
            float progressValue = (current/total)*100;
            holder._downloadProgressTextView.setText((int)progressValue + "%");
        }
        else if(downloadInfo.getState() == HttpHandler.State.FAILURE)
        {
            holder._filePictureCubeImageView.setAlpha(0.5f);
            holder._downloadProgressTextView.setText(_context.getString(R.string.download_fauliar));
        }
        else if(downloadInfo.getState() == HttpHandler.State.CANCELLED)
        {
            holder._filePictureCubeImageView.setAlpha(0.5f);
            holder._saveDownloadImageView.setVisibility(View.VISIBLE);
            holder._downloadProgressTextView.setVisibility(View.GONE);
        }
    }

    private ArrayList<DownloadInfo> _listAppInfo = new ArrayList<DownloadInfo>();
    private ArrayList<HashMap> _listToListView = new ArrayList<HashMap>();
    //最近下载
    private ArrayList<DownloadInfo> _listRecent = new ArrayList<DownloadInfo>();
    //以前下载
    private ArrayList<DownloadInfo> _listPrevious = new ArrayList<DownloadInfo>();


    private ArrayList<DownloadInfo> _listPanoInfo = new ArrayList<DownloadInfo>();
    private ArrayList<DownloadInfo> _listToday = new ArrayList<DownloadInfo>();
    //上周
    private ArrayList<DownloadInfo> _listLastWeek = new ArrayList<DownloadInfo>();
    //上月
    private ArrayList<DownloadInfo> _listLastMonth = new ArrayList<DownloadInfo>();
    //更早
    private ArrayList<DownloadInfo> _listLongAgo = new ArrayList<DownloadInfo>();

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = null;
        final ViewHolder holder;
        final DownloadInfo downloadInfo = _list.get(position);

        if (null == convertView) {
            view = LayoutInflater.from(_context).inflate(R.layout.item_download, null);
            holder = new ViewHolder();
            holder._filePictureCubeImageView = (CubeImageView) view.findViewById(R.id.civ_filePicture);
            holder._typeImageView = (ImageView) view.findViewById(R.id.iv_type);
            holder._liulanImageView = (ImageView) view.findViewById(R.id.iv_liulan);
            holder._countTextView = (TextView) view.findViewById(R.id.tv_count);
            holder._fileNameTextView = (TextView) view.findViewById(R.id.tv_fileName_forGridView);
            holder._downloadProgressTextView = (TextView) view.findViewById(R.id.tv_download_progress);
            holder._saveDownloadImageView = (ImageView) view.findViewById(R.id.iv_save_download);
            holder._downloaditemFrameLayout = (FrameLayout) view.findViewById(R.id.fl_download_item);
            holder._handler = new Handler();
            holder._runnable = new Runnable() {
                @Override
                public void run() {
                    holder._handler.postDelayed( holder._runnable,1000);
                    //显示进度等
                    showDownText(holder,downloadInfo);
                }
            };

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder._handler.postDelayed(holder._runnable, 1000);

        holder._downloaditemFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击下载
                handlerDown(holder,downloadInfo);
            }
        });

        holder._downloaditemFrameLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeDownloadFile();
                return false;
            }

            private void removeDownloadFile() {
                AlertDialog.Builder builder = new AlertDialog.Builder(_context);

                builder.setMessage(_context.getString(R.string.delete_download));
                builder.setTitle(_context.getString(R.string.notice));

                builder.setPositiveButton(_context.getString(R.string.config), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            DownloadUtils.getInstance().removeDownload(_list.get(position));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                        //下面是刷新界面
                        List<DownloadInfo> downloadInfoList = DownloadUtils.getInstance().getlist();

                        for (int i = 0; i < downloadInfoList.size(); i++) {
                            DownloadInfo vo = downloadInfoList.get(i);
                            if(vo.getType() == VRHomeConfig.LOCAL_TYPE_APP)
                            {
                                _listAppInfo.add(vo);
                            }
                            if (vo.getType() == VRHomeConfig.VIDEO || vo.getType() == VRHomeConfig.IMAGE) {
                                _listPanoInfo.add(vo);
                            }
                        }
                        switch (_tag){
                            case DownloadActivity.DOWNLOAD_APP:
                                ListView downloadAppListView = DownloadAppFragment.downloadAppListView;
                                if(_listAppInfo.size() <= 0)
                                {
                                    View emptyView = DownloadAppFragment.emptyView;

                                    downloadAppListView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                }else {
                                    handleAppData();
                                    downloadAppListView.setAdapter(new FindListViewAdapter(_context, _listToListView, DownloadActivity.DOWNLOAD_APP) {
                                        @Override
                                        public void refresh() {

                                        }
                                    });
                                }
                                break;
                            case DownloadActivity.DOWNLOAD_PANO:
                                ListView downloadPanoListView = DownloadPanoFragment.downloadPanoListView;
                                if(_listPanoInfo.size() <= 0)
                                {
                                    View emptyView = DownloadAppFragment.emptyView;

                                    downloadPanoListView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                }else {
                                    handlePanoData();
                                    downloadPanoListView.setAdapter(new FindListViewAdapter(_context, _listToListView, DownloadActivity.DOWNLOAD_PANO) {
                                        @Override
                                        public void refresh() {

                                        }
                                    });
                                }
                                break;
                        }

                    }

                });

                builder.setNegativeButton(_context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();

            }

        });

        switch (_tag) {
            case DownloadActivity.DOWNLOAD_APP:
                ViewGroup.LayoutParams layoutParams =  holder._filePictureCubeImageView.getLayoutParams();
                layoutParams.height = (int)_context.getResources().getDimension(R.dimen.app_icon);
                layoutParams.width = layoutParams.height;
                holder._filePictureCubeImageView.setLayoutParams(layoutParams);
                if(_imageLoader == null)
                {
                    _imageLoader = ConfigDefaultImageUtils.getInstance().getAppImageLoader(_context);
                }
                holder._filePictureCubeImageView.loadImage(_imageLoader,_list.get(position).getThumbUrl());
                break;
            case DownloadActivity.DOWNLOAD_PANO:
                holder._typeImageView.setVisibility(View.VISIBLE);
                holder._liulanImageView.setVisibility(View.VISIBLE);
                holder._countTextView.setVisibility(View.VISIBLE);
                if(_imageLoader == null)
                {
                    _imageLoader = ConfigDefaultImageUtils.getInstance().getPanoramaImageLoader(_context);
                }
                holder._filePictureCubeImageView.loadImage(_imageLoader,_list.get(position).getThumbUrl());
                //这里现在先设置一个假数量，
                holder._countTextView.setText(_list.get(position).getViews());

                if (_list.get(position).getType() == VRHomeConfig.VIDEO) {
                    holder._typeImageView.setImageResource(R.drawable.icon_shiping);
                } else if (_list.get(position).getType() == VRHomeConfig.IMAGE) {
                    holder._typeImageView.setImageResource(R.drawable.icon_tupian);
                }

                break;
        }

        holder._fileNameTextView.setText(_list.get(position).getFileName());
        return view;
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
            todayHashMap.put("time", _context.getString(R.string.find_download_recent));
            todayHashMap.put("data",_listRecent);
            _listToListView.add(todayHashMap);
        }

        if(_listPrevious.size() > 0){
            HashMap longAgoHashMap = new HashMap();
            longAgoHashMap.put("time",_context.getString(R.string.find_download_previous));
            longAgoHashMap.put("data",_listPrevious);
            _listToListView.add(longAgoHashMap);
        }
        return _listToListView;
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
            todayHashMap.put("time", _context.getString(R.string.find_local_today));
            todayHashMap.put("data",_listToday);
            _listToListView.add(todayHashMap);
        }
        if(_listLastWeek.size() > 0){
            HashMap lastWeekHashMap = new HashMap();
            lastWeekHashMap.put("time",_context.getString(R.string.find_local_lastweek));
            lastWeekHashMap.put("data",_listLastWeek);
            _listToListView.add(lastWeekHashMap);
        }
        if(_listLastMonth.size() > 0){
            HashMap lastMonthHashMap = new HashMap();
            lastMonthHashMap.put("time",_context.getString(R.string.find_local_lastmonth));
            lastMonthHashMap.put("data",_listLastMonth);
            _listToListView.add(lastMonthHashMap);
        }
        if(_listLongAgo.size() > 0){
            HashMap longAgoHashMap = new HashMap();
            longAgoHashMap.put("time",_context.getString(R.string.find_local_longago));
            longAgoHashMap.put("data",_listLongAgo);
            _listToListView.add(longAgoHashMap);
        }
        return _listToListView;
    }

    class ViewHolder {
        FrameLayout _downloaditemFrameLayout;
        CubeImageView _filePictureCubeImageView;
        ImageView _typeImageView;
        ImageView _liulanImageView;
        TextView _countTextView;
        TextView _fileNameTextView;
        TextView _downloadProgressTextView;
        ImageView _saveDownloadImageView;
        Handler _handler;
        Runnable _runnable;
    }

}
