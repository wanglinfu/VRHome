package com.vrseen.vrstore.adapter.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.model.find.DownloadInfo;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;


public class FindDownloadPanoAdapter extends BaseAdapter {

    Context _context;
    ArrayList<DownloadInfo> _list;
    public FindDownloadPanoAdapter(Context _context, ArrayList<DownloadInfo> _list){
        this._context = _context;
        this._list = _list;
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
        View view;
        ViewHolder holder;

        if(null == convertView){
            view = LayoutInflater.from(_context).inflate(R.layout.item_download_pano, null);
            holder = new ViewHolder();
            holder._ivHolder = (CubeImageView) view.findViewById(R.id.iv_download_pano);
            holder._tvHolder = (TextView) view.findViewById(R.id.tv_download_pano);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        holder._tvHolder.setText(_context.getString(R.string.panorama));
        BitmapUtils bitmapUtils = new BitmapUtils(_context);
        bitmapUtils.display(holder._ivHolder, _list.get(position).getThumbUrl());

        return view;
    }

    private static class ViewHolder{
        CubeImageView _ivHolder;
        TextView _tvHolder;
    }
}
