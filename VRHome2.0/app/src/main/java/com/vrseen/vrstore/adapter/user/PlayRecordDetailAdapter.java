package com.vrseen.vrstore.adapter.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.user.MyPlayRecordActivity;
import com.vrseen.vrstore.model.user.PlayRecordInfo;
import com.vrseen.vrstore.util.DateUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.view.swipemenulistview.BaseSwipListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.image.CubeImageView;

/**
 * 播放记录，可编辑，可删除
 * Created by mll on 16/5/13.
 */
public class PlayRecordDetailAdapter extends BaseSwipListAdapter {
    private LayoutInflater layoutInflater;
    private Context _context;
    private RelativeLayout rl_movie;
    private Map<Integer,Boolean> isCheckMap = new HashMap<Integer, Boolean>();
    private List<PlayRecordInfo> _palyRecordList;
    private BitmapUtils bitmapUtils;

    public PlayRecordDetailAdapter(Context context, List<PlayRecordInfo> palyRecordList) {
        _context = context;
        _palyRecordList = new ArrayList<PlayRecordInfo>();
        _palyRecordList.addAll(palyRecordList);
        layoutInflater = LayoutInflater.from(context);
        bitmapUtils = new BitmapUtils(context);
    }

    public void resetData(List<PlayRecordInfo> list){
        _palyRecordList = list;
        notifyDataSetChanged();
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
    public boolean getSwipEnableByPosition(int position) {
//        if(position % 2 == 0){
//            return false;
//        }
        return true;
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
            convertView = layoutInflater.inflate(R.layout.adapter_playrecord_detail, parent, false);
            holder.recordTitleTextView = (TextView) convertView.findViewById(R.id.tv_record_title);
            holder.playrecordCubeImageView = (CubeImageView) convertView.findViewById(R.id.civ_playrecordImg);
            holder.showDeleteRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_delete);
            holder.timeTextView = (TextView) convertView.findViewById(R.id.tv_time);
            holder.seeToTextView = (TextView) convertView.findViewById(R.id.tv_seeTo);
            holder.record_episodeTextView = (TextView) convertView.findViewById(R.id.tv_record_episode);
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

        bitmapUtils.display(holder.playrecordCubeImageView, _palyRecordList.get(position).getImage_url());
        holder.recordTitleTextView.setText(_palyRecordList.get(position).getTitle());
        int last_tick = Integer.valueOf(_palyRecordList.get(position).getLast_tick());
        int time = last_tick/(60*1000) + 1;
        holder.seeToTextView.setText(_context.getString(R.string.film_play_to) + time + _context.getString(R.string.film_play_minutes));

        String createdTime = _palyRecordList.get(position).getCreated_at();
        int yearPalyRecord = Integer.valueOf(createdTime.substring(0,4));
        int monthPalyRecord = Integer.valueOf(createdTime.substring(5,7));
        int datePalyRecord = Integer.valueOf(createdTime.substring(8,10));
        int yearCurrent = DateUtils.getYear();
        int monthCurrent = DateUtils.getMonth();
        int dateCurrent = DateUtils.getDate();
        if(yearCurrent == yearPalyRecord && monthCurrent == monthPalyRecord){   //同年同月
            if(dateCurrent == datePalyRecord){
                holder.timeTextView.setText(_context.getString(R.string.find_local_today));
            }else if(dateCurrent - datePalyRecord == 1){
                holder.timeTextView.setText(_context.getString(R.string.find_local_yesterday));
            }else if(dateCurrent - datePalyRecord == 2){
                holder.timeTextView.setText(_context.getString(R.string.find_local_twodayago));
            }else {
                holder.timeTextView.setText(createdTime.substring(0,10));
            }
        }else {
            holder.timeTextView.setText(createdTime.substring(0,10));
        }


        if(_palyRecordList.get(position).getIsDelete().equals(MyPlayRecordActivity.STATE_DELETE)){
            holder.showDeleteRelativeLayout.setVisibility(View.VISIBLE);
        }else{
            holder.showDeleteRelativeLayout.setVisibility(View.GONE);
        }

        //集数有问题
        String episode = _palyRecordList.get(position).getLast_episode_text();
        holder.record_episodeTextView.setText("");

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
        TextView recordTitleTextView;
        CubeImageView playrecordCubeImageView;
        RelativeLayout showDeleteRelativeLayout;
        TextView timeTextView;
        TextView seeToTextView;
        TextView record_episodeTextView;
    }
}
