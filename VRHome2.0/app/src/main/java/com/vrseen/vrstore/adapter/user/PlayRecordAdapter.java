package com.vrseen.vrstore.adapter.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.model.user.PlayRecordInfo;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * 播放记录，个人中心
 * Created by mll on 16/5/13.
 */
public class PlayRecordAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context _context;
    private RelativeLayout rl_record;
    private List<PlayRecordInfo> _palyRecordList;
    private BitmapUtils bitmapUtils;
    private StringBuffer _stringBuffer;

    public PlayRecordAdapter(Context context, List<PlayRecordInfo> palyRecordList) {
        _context = context;
        layoutInflater = LayoutInflater.from(context);
        _palyRecordList = palyRecordList;
        bitmapUtils = new BitmapUtils(context);
        _stringBuffer = new StringBuffer();
    }

    @Override
    public int getCount() {
        return _palyRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return _palyRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.adapter_playrecord, parent, false);
            holder.tv_recordTitle = (TextView) convertView.findViewById(R.id.tv_record_title);
            holder.civ_playrecordImg = (CubeImageView) convertView.findViewById(R.id.civ_playrecordImg);
            holder.iv_vip = (ImageView) convertView.findViewById(R.id.iv_vip);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(_palyRecordList.get(position).getFee() == 1){
            holder.iv_vip.setVisibility(View.VISIBLE);
        }else {
            holder.iv_vip.setVisibility(View.INVISIBLE);
        }
        
        String history = (String) SharedPreferencesUtils.getParam(_context, "history", "");
        String imgUrl = _palyRecordList.get(position).getImage_url();
        if(imgUrl == null){
            holder.civ_playrecordImg.setImageResource(R.drawable.tp_yingshimoren);
            holder.tv_recordTitle.setText(_palyRecordList.get(position).getTitle());
        }else if(!history.contains(_palyRecordList.get(position).getImage_url())){
            bitmapUtils.display(holder.civ_playrecordImg, _palyRecordList.get(position).getImage_url());
            holder.tv_recordTitle.setText(_palyRecordList.get(position).getTitle());
            _stringBuffer.append(_palyRecordList.get(position).getImage_url());
        }

        SharedPreferencesUtils.setParam(_context, "history", _stringBuffer);
        return convertView;
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    class ViewHolder {
        ImageView iv_vip;
        CubeImageView civ_playrecordImg;
        TextView tv_recordTitle;
    }
}
