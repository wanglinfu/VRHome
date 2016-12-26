package com.vrseen.vrstore.adapter.user;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.film.FilmListData;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.ToastUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;


public class MyCollectAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context _context;
    private RelativeLayout rl_movie;
    private List<FilmListData.BaseFilm> _baseFilmList;
    private in.srain.cube.image.ImageLoader imageLoader;
    private int _columns;

    public MyCollectAdapter(Context context, List<FilmListData.BaseFilm> baseFilmList) {
        _context = context;
        layoutInflater = LayoutInflater.from(context);
        _baseFilmList = new ArrayList<FilmListData.BaseFilm>();
        _baseFilmList.addAll(baseFilmList);

        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(context);
        handler.setLoadingResources(R.drawable.yingshi_zhengzaijiazai);
        handler.setErrorResources(R.drawable.jiazaishibai_yingshi);
        imageLoader = ImageLoaderFactory.create(context, handler);
    }

    public void refreshData(List<FilmListData.BaseFilm> list)
    {
        _baseFilmList.clear();
        _baseFilmList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (_baseFilmList != null && _baseFilmList.size() > 0)
            return _baseFilmList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return _baseFilmList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.item_movie, parent, false);

            holder.loadViews(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FilmListData.BaseFilm baseFilm = _baseFilmList.get(position);
        holder.fill(baseFilm, position);
        return convertView;
    }


    class ViewHolder {
        CubeImageView iv_cover;
        TextView tv_title;
        TextView tv_sub_title;
        ImageView iv_vip;

        public void loadViews(View convertView) {
            iv_cover = (CubeImageView) convertView.findViewById(R.id.iv_cover);
            tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            tv_sub_title = (TextView) convertView.findViewById(R.id.tv_sub_title);
            iv_vip = (ImageView) convertView.findViewById(R.id.iv_vip);
        }

        public void fill(final FilmListData.BaseFilm baseFilm, final int position) {
            if (baseFilm != null) {

                if(baseFilm.getFee() == 1){
                    iv_vip.setVisibility(View.VISIBLE);
                }else {
                    iv_vip.setVisibility(View.INVISIBLE);
                }

                iv_cover.loadImage(imageLoader, baseFilm.getImage_url());
                tv_title.setText(baseFilm.getTitle());
                tv_sub_title.setText(baseFilm.getSubtitle());
            }
        }
    }
}
