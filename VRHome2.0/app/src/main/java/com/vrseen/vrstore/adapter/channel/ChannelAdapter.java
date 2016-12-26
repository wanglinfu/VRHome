package com.vrseen.vrstore.adapter.channel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.channel.LiveChannelActivity;
import com.vrseen.vrstore.activity.film.FilmActivity;
import com.vrseen.vrstore.activity.panorama.Panorama1Activity;
import com.vrseen.vrstore.fragment.channel.ChannelFragment;
import com.vrseen.vrstore.model.Home.HomeData;
import com.vrseen.vrstore.model.channel.Channel;

import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * Created by jiangs on 16/5/4.
 */
public class ChannelAdapter extends BaseAdapter {
    private LayoutInflater _layoutInflater;
    private HomeData _data;
    private Context _context;
    private List<Channel> _channels;
    private in.srain.cube.image.ImageLoader _imageLoader;

    public ChannelAdapter(Context _context, List<Channel> _channels) {
        _layoutInflater = LayoutInflater.from(_context);
        this._context = _context;
        this._channels = _channels;
        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(_context);
        _imageLoader = ImageLoaderFactory.create(_context, handler);
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
            convertView = _layoutInflater.inflate(R.layout.item_channel, parent, false);
//                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.loadViews(convertView);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fill(_channels.get(position));

//        holder.tv_name.setText(_data.getGroupData().get(position));

        final String channel = _channels.get(position).getChannel();
        final String link = _channels.get(position).getLink();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (channel)
                {
                    case ChannelFragment.FILM_CHANNEL:
                        Intent i=new Intent();

                        FilmActivity.actionStart(_context);
                        break;
                    case ChannelFragment.PANO_CHANNEL:
                        _context.startActivity(new Intent(_context, Panorama1Activity.class));
                        break;
                    case ChannelFragment.LIVE_CHANNEL:
                        LiveChannelActivity.actionStart(_context,link);
                        break;
                }

            }
        });

        return convertView;
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
