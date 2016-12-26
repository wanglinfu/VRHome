package com.vrseen.vrstore.adapter.film;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.model.film.FilmCateroryData;

import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * Created by jiangs on 16/5/10.
 */
public class FilmNavAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context _context;
    List<FilmCateroryData.Category> categories;
    private in.srain.cube.image.ImageLoader imageLoader;

    public FilmNavAdapter(Context context, List<FilmCateroryData.Category> categories) {
        _context = context;
        layoutInflater = LayoutInflater.from(_context);
        this.categories = categories;
        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(_context);
        imageLoader = ImageLoaderFactory.create(_context, handler);
    }

    @Override
    public int getCount() {
        if (categories != null && categories.size() > 0)
            return categories.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_video_nav, parent, false);
            holder.loadViews(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FilmCateroryData.Category category = categories.get(position);
        holder.fill(category);
        return convertView;
    }


    class ViewHolder {
        CubeImageView iv_category;
        TextView tv_name;

        public void loadViews(View convertView) {
            iv_category = (CubeImageView) convertView.findViewById(R.id.iv_category);
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);

        }

        public void fill(final FilmCateroryData.Category category) {
            if (category != null) {
//                iv_category.loadImage(imageLoader, category.getIcon());
                tv_name.setText(category.getGenre_name());
                switch (category.getGenre_name()){
                    case "3D":
                        iv_category.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_3d));
                        break;
                    case "电影":
                        iv_category.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_dianying));
                        break;
                    case "电视剧":
                        iv_category.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_dianshiju));
                        break;
                    case "求索":
                        iv_category.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_qiusuo));
                        break;
                    case "综艺":
                        iv_category.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_zongyi));
                        break;
                }
            }
        }
    }
}
