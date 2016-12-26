package com.vrseen.vrstore.adapter.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.film.FilmActivity;
import com.vrseen.vrstore.model.channel.Channel;
import com.vrseen.vrstore.model.Home.HomeData;
import com.vrseen.vrstore.util.ConfigDefaultImageUtils;

import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * app首页adapter
 */
public class AppAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater _layoutInflater;
    private HomeData data;
    private Context _context;
    private List<Channel> _channels;
    private in.srain.cube.image.ImageLoader _imageLoader;

    public AppAdapter(Context context, List<Channel> channels) {
        //_layoutInflater = LayoutInflater.from(context);
        _context = context;
        _channels = channels;
        _imageLoader = ConfigDefaultImageUtils.getInstance().getAppImageLoader(context.getApplicationContext());
    }


    @Override
    public int getCount() {
        if (_channels != null && _channels.size() > 0)
            return _channels.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return _channels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
           // convertView = _layoutInflater.inflate(R.layout.item_channel, parent, false);
//                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView = View.inflate(_context, R.layout.item_channel, null);
            holder.loadViews(convertView);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fill(_channels.get(position));
        convertView.setOnClickListener(this);
//        holder.tv_name.setText(data.getGroupData().get(position));
        return convertView;
    }

    @Override
    public void onClick(View v) {
        _context.startActivity(new Intent(_context, FilmActivity.class));
    }

    class ViewHolder {
        CubeImageView iv_icon;
        TextView tv_name;

        public void loadViews(View convertView) {
            iv_icon = (CubeImageView) convertView.findViewById(R.id.iv_icon);
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        }

        public void fill(Channel channel) {
            iv_icon.loadImage(_imageLoader, channel.getIcon());
            tv_name.setText(channel.getTitle() + "");
        }


    }
}
