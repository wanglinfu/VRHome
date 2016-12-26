package com.vrseen.vrstore.adapter.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.model.find.DownloadInfo;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;


public class FindDownloadAppAdapter extends BaseAdapter {

    Context _context;
    ArrayList<DownloadInfo> _list;
    private ImageLoader _imageLoader;
    public FindDownloadAppAdapter(Context context, ArrayList<DownloadInfo> list){
        _context = context;
        _list = list;
        _imageLoader = ImageLoaderFactory.create(_context);
    }

    public void  refreshData( ArrayList<DownloadInfo> list)
    {
        _list = list;
        notifyDataSetChanged();
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
            view = LayoutInflater.from(_context).inflate(R.layout.item_download_app, null);
            holder = new ViewHolder();
            holder._ivHolder = (CubeImageView) view.findViewById(R.id.iv_download_app);
            holder._tvHolder = (TextView) view.findViewById(R.id.tv_download_app);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        holder._ivHolder.loadImage(_imageLoader,_list.get(position).getThumbUrl());

        holder._tvHolder.setText(_context.getString(R.string.tab_apps));
//        BitmapUtils bitmapUtils = new BitmapUtils(_context);
//        bitmapUtils.display(holder._ivHolder, _list.get(position).getThumbUrl());

        return view;
    }

    private static class ViewHolder{
        CubeImageView _ivHolder;
        TextView _tvHolder;
    }
}
