package com.vrseen.vrstore.adapter.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.find.LocalActivity;
import com.vrseen.vrstore.model.find.LocalResInfo;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;


public class FindLocalGridViewAdapter extends BaseAdapter {

    Context _context;
    ArrayList<LocalResInfo> _listLocalRes;
    String _tag;
    private BitmapUtils _bitmapUtils;

    public FindLocalGridViewAdapter(Context context, ArrayList<LocalResInfo> list, String tag){
        _context = context;
        _listLocalRes = list;
        _tag = tag;
        _bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public int getCount() {
        return _listLocalRes.size();
    }

    @Override
    public Object getItem(int position) {
        return _listLocalRes.get(position);
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
            view = LayoutInflater.from(_context).inflate(R.layout.item_locals_gridview, null);
            holder = new ViewHolder();
            holder._tvHolder = (TextView) view.findViewById(R.id.tv_fileName_forGridView);
            holder._civHolder = (CubeImageView) view.findViewById(R.id.civ_forgridview);
            holder._ivPlay = (ImageView) view.findViewById(R.id.iv_play);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }
        holder._tvHolder.setText(_listLocalRes.get(position).getFileName());
        _bitmapUtils.display(holder._civHolder,_listLocalRes.get(position).getThumbUrl());

        switch (_tag){
            case LocalActivity.LOCAL_IMAGE :
                holder._ivPlay.setVisibility(View.GONE);
                break;

            case LocalActivity.LOCAL_VIDEO :
                holder._ivPlay.setVisibility(View.VISIBLE);
                break;
        }

        return view;
    }

    private static class ViewHolder{
        CubeImageView _civHolder;
        TextView _tvHolder;
        ImageView _ivPlay;
    }
}
