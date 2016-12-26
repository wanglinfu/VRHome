package com.vrseen.vrstore.adapter.find;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.find.DownloadActivity;
import com.vrseen.vrstore.activity.find.LocalActivity;
import com.vrseen.vrstore.fragment.find.LocalFragment;
import com.vrseen.vrstore.logic.FileLogic;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.model.find.LocalResInfo;
import com.vrseen.vrstore.model.panorama.PanoramaDetailData;
import com.vrseen.vrstore.view.ExpGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class FindListViewAdapter extends BaseAdapter {

    Context _context;
    ArrayList<HashMap> _list;
    String _tag;

    public FindListViewAdapter(Context context, ArrayList<HashMap> list, String tag){
        _context = context;
        _list = list;
        _tag = tag;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        final ViewHolder holder;

        if(null == convertView){
            view = LayoutInflater.from(_context).inflate(R.layout.item_locals, null);
            holder = new ViewHolder();
            holder._tvHolder = (TextView) view.findViewById(R.id.tv_local_items);
            holder._egvHolder = (ExpGridView) view.findViewById(R.id.gv_local_items);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        holder._tvHolder.setText( (String) _list.get(position).get("time") );

        switch (_tag){
            case LocalActivity.LOCAL_IMAGE :
                final ArrayList<LocalResInfo> _listImageToGridView = (ArrayList<LocalResInfo>) _list.get(position).get("data");

                FindLocalGridViewAdapter findLocalImageAdapter = new FindLocalGridViewAdapter(_context, _listImageToGridView, LocalActivity.LOCAL_IMAGE);
                holder._egvHolder.setAdapter(findLocalImageAdapter);

                holder._egvHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //播放全景图片
                        PanoramaDetailData.PanoramaDetail panoramaDetail = new PanoramaDetailData.PanoramaDetail();
                        panoramaDetail.setStoragepath(_listImageToGridView.get(position).getFileSavePath());
                        panoramaDetail.setId((int) _listImageToGridView.get(position).getId());
                        U3dMediaPlayerLogic.getInstance().startPlayPanorama(_context,panoramaDetail, VRHomeConfig.TYPE_VR,false,VRHomeConfig.VR_MYDOWNLOAD_ID);
                    }
                });

                removeLocalFile(holder, _listImageToGridView, findLocalImageAdapter, position);

                break;

            case LocalActivity.LOCAL_VIDEO :
                final ArrayList<LocalResInfo> _listVideoToGridView = (ArrayList<LocalResInfo>) _list.get(position).get("data");

                FindLocalGridViewAdapter findLocalVideoAdapter = new FindLocalGridViewAdapter(_context, _listVideoToGridView, LocalActivity.LOCAL_VIDEO);
                holder._egvHolder.setAdapter(findLocalVideoAdapter);
                //点击条目，播放视频
                holder._egvHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        LocalResInfo localResInfo = _listVideoToGridView.get(0);
                        switch (localResInfo.getType()){
                            case VRHomeConfig.LOCAL_TYPE_PANO_VIDEO:
                                //播放全景视频
                                PanoramaDetailData.PanoramaDetail panoramaDetail = new PanoramaDetailData.PanoramaDetail();
                                panoramaDetail.setStoragepath(_listVideoToGridView.get(position).getFileSavePath());
                                panoramaDetail.setId((int) _listVideoToGridView.get(position).getId());
                                U3dMediaPlayerLogic.getInstance().startPlayPanorama(_context,panoramaDetail, VRHomeConfig.TYPE_VR,false,0);
                                break;
//                            case VRHomeConfig.LOCAL_TYPE_MOVIE_3D_TB:
//                                //// FIXME: 2016/6/24 默认播放3d左右
//                                //播放3D上下屏电影
//                                U3dMediaPlayerLogic.getInstance().startPlayFilm(_context, _listVideoToGridView.get(position).getFileSavePath(),
//                                        VRHomeConfig.TYPE_3D,VRHomeConfig.TYPE_RENDER_TB, _listVideoToGridView.get(position).getFileName(),0,0,0);
//                                break;
                            case VRHomeConfig.LOCAL_TYPE_MOVIE_3D:
                                //播放3D左右屏电影
                                if(localResInfo.getSmallType() == VRHomeConfig.LOCAL_TYPE_MOVIE_3D_TB)
                                {
                                    //上下
                                    U3dMediaPlayerLogic.getInstance().startPlayFilm(_context, _listVideoToGridView.get(position).getFileSavePath(),
                                        VRHomeConfig.TYPE_3D,VRHomeConfig.TYPE_RENDER_TB, _listVideoToGridView.get(position).getFileName(),0,0,0);
                                }
                                else
                                {
                                    //左右
                                    U3dMediaPlayerLogic.getInstance().startPlayFilm(_context, _listVideoToGridView.get(position).getFileSavePath(),
                                            VRHomeConfig.TYPE_3D,VRHomeConfig.TYPE_RENDER_LR, _listVideoToGridView.get(position).getFileName(),0,0,0);
                                }

                                break;
                            case VRHomeConfig.LOCAL_TYPE_MOVIE_2D:
                                //播放2D电影
                                U3dMediaPlayerLogic.getInstance().startPlayFilm(_context, _listVideoToGridView.get(position).getFileSavePath(),
                                        VRHomeConfig.TYPE_2D, _listVideoToGridView.get(position).getFileName(),0,0,0);

                                //添加播放记录
                                break;
                        }
                    }
                });

                removeLocalFile(holder, _listVideoToGridView, findLocalVideoAdapter, position);
                break;

            case  DownloadActivity.DOWNLOAD_APP :
                ArrayList<DownloadInfo> _listAppToGridView = (ArrayList<DownloadInfo>) _list.get(position).get("data");
                FindDownloadGridViewAdapter findDownloadAppAdapter = new FindDownloadGridViewAdapter(_context, _listAppToGridView, DownloadActivity.DOWNLOAD_APP);
                holder._egvHolder.setAdapter(findDownloadAppAdapter);
                holder._egvHolder.setNumColumns(4);
                break;

            case  DownloadActivity.DOWNLOAD_PANO :
                ArrayList<DownloadInfo> _listPanoToGridView = (ArrayList<DownloadInfo>) _list.get(position).get("data");
                FindDownloadGridViewAdapter findDownloadPanoAdapter = new FindDownloadGridViewAdapter(_context, _listPanoToGridView, DownloadActivity.DOWNLOAD_PANO);
                holder._egvHolder.setAdapter(findDownloadPanoAdapter);
                break;
        }

        return view;

    }

    private void removeLocalFile(ViewHolder holder, final ArrayList<LocalResInfo> arrayList, final FindLocalGridViewAdapter findLocalGridViewAdapter, final int listPosition) {
        holder._egvHolder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(_context);

                builder.setMessage(_context.getString(R.string.delete_download));
                builder.setTitle(_context.getString(R.string.notice));

                builder.setPositiveButton(_context.getString(R.string.config), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FileLogic.getInstance().removeFile(arrayList.get(position));
                        arrayList.remove(position);

                        if(arrayList.size() <= 0){
                            _list.remove(listPosition);

                            if(_list.size() <= 0){
                                refresh();
                            }
                        }

                        notifyDataSetChanged();

                    }
                });

                builder.setNegativeButton(_context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();

                return true;
            }
        });
    }

    public abstract void refresh();

    private static class ViewHolder{
        TextView _tvHolder;
        ExpGridView _egvHolder;
    }
}
