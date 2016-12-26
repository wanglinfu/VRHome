package com.vrseen.vrstore.adapter.film;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.util.SharedPreferencesUtils;

import java.util.List;

/**
 * 剧集
 * Created by mll on 2016/5/25.
 */
public class FilmEpisodeAdapter extends BaseAdapter implements View.OnClickListener {

    private Context _context;
    private List<FilmDetailData.EpisodesData> _episodeData;
    private LayoutInflater layoutInflater;
    private int _selectEpisodeIndex = 0;

    public FilmEpisodeAdapter(Context context, List<FilmDetailData.EpisodesData> datas,int index) {
        layoutInflater = LayoutInflater.from(context);
        _context = context;
        _episodeData = datas;
        _selectEpisodeIndex = index;
    }

    public void refreshData( List<FilmDetailData.EpisodesData> datas,int index)
    {
        _episodeData.clear();
        _episodeData.addAll(datas);
        _selectEpisodeIndex = index;
        notifyDataSetChanged();
    }

    public void refreshData(int index){
        _selectEpisodeIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(_episodeData != null && _episodeData.size() > 0)
        {
            return _episodeData.size();
        }
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return _episodeData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.drama_count_layout_item, parent, false);
            holder.loadViews(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        if( _episodeData != null && _episodeData.size() > position)
        {
            holder.fill(_episodeData.get(position));
        }


        if(position == 0 && _selectEpisodeIndex == 0 ){
            holder.rl_drama.setBackgroundResource(R.drawable.btn_radius_blue_style);
        }

        if(_selectEpisodeIndex == position)
        {
            convertView.setBackgroundResource(R.drawable.btn_radius_blue_style);
            holder.episodeBtn.setTextColor(_context.getResources().getColor(R.color.white));

        }
        else
        {
            convertView.setBackgroundResource(R.drawable.btn_radius_white_style);
            holder.episodeBtn.setTextColor(_context.getResources().getColor(R.color.contentColor));
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder
    {
        TextView episodeBtn;
        RelativeLayout rl_drama;

        public void loadViews(View convertView)
        {
            episodeBtn = (TextView)convertView.findViewById(R.id.drama_count_button);
            rl_drama = (RelativeLayout)convertView.findViewById(R.id.rl_drama);
            //convertView.setSelected(true);
        }

        public void fill(FilmDetailData.EpisodesData episodesData)
        {
            episodeBtn.setText(String.format(_context.getString(R.string.film_episode_count),episodesData.getEpisode()));

        }
    }
}
