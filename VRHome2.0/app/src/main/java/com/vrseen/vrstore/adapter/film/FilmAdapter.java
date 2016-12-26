package com.vrseen.vrstore.adapter.film;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.activity.user.MyCollectActivity;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.film.FilmListData;
import com.vrseen.vrstore.util.ConfigDefaultImageUtils;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.ToastUtils;

import org.json.JSONException;

import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * Created by jiangs on 16/5/10.
 */
public class FilmAdapter extends BaseAdapter {
    private Context _context;
    private RelativeLayout rl_movie;
    List<FilmListData.BaseFilm> baseFilmList;
    private in.srain.cube.image.ImageLoader imageLoader;
    private int _columns;

    public FilmAdapter(Context context, List<FilmListData.BaseFilm> baseFilmList) {
        _context = context;
        if(_context != null)
        {
            this.baseFilmList = baseFilmList;

            DefaultImageLoadHandler handler = new DefaultImageLoadHandler(context);
            handler.setLoadingResources(R.drawable.yingshi_zhengzaijiazai);
            handler.setErrorResources(R.drawable.jiazaishibai_yingshi);
            imageLoader = ImageLoaderFactory.create(context, handler);
        }
    }


    public void addData(List<FilmListData.BaseFilm> list)
    {
        baseFilmList.addAll(list);
        notifyDataSetChanged();
    }

    public void refreshData(List<FilmListData.BaseFilm> list)
    {
        baseFilmList.clear();
        baseFilmList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (baseFilmList != null && baseFilmList.size() > 0)
            return baseFilmList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return baseFilmList.get(position);
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
            convertView = View.inflate(_context, R.layout.item_movie, null);

            holder.loadViews(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(_context != null)
        {
            FilmListData.BaseFilm baseFilm = baseFilmList.get(position);
            holder.fill(baseFilm, position);
        }

        return convertView;
    }


    class ViewHolder {
        CubeImageView iv_cover;
        ImageView iv_vip;
        TextView tv_title;
        TextView tv_sub_title;
        TextView tv_update;

        public void loadViews(View convertView) {
            iv_cover = (CubeImageView) convertView.findViewById(R.id.iv_cover);
            tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            tv_sub_title = (TextView) convertView.findViewById(R.id.tv_sub_title);
            iv_vip = (ImageView) convertView.findViewById(R.id.iv_vip);
            tv_update=(TextView)convertView.findViewById(R.id.tv_movie_update_num);
        }

        public void fill(final FilmListData.BaseFilm baseFilm, final int position) {
            if (baseFilm != null)
            {
                if(baseFilm.getFee() == 1){
                    iv_vip.setVisibility(View.VISIBLE);
                }else {
                    iv_vip.setVisibility(View.INVISIBLE);
                }
                if(baseFilm!=null&& baseFilm.getEpisode()!=null && !baseFilm.getEpisode().equals("") && !baseFilm.getEpisode().equals("1")) {
                    tv_update.setVisibility(View.VISIBLE);
                    tv_update.setText(baseFilm.getEpisode());
                }else{
                    tv_update.setVisibility(View.GONE);
                }

                iv_cover.loadImage(imageLoader, baseFilm.getImage_url());
                tv_title.setText(baseFilm.getTitle());
                tv_sub_title.setText(baseFilm.getSubtitle());

                iv_cover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FilmDetailData.PlayProgressData playProgressData = new FilmDetailData.PlayProgressData();
                        playProgressData.setId(baseFilm.getId());
                        FilmDetailActivity.actionStart(_context,playProgressData);
                    }
                });

                if(_context instanceof MyCollectActivity)
                {
                    iv_cover.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            DialogHelpUtils.getConfirmDialog(_context, _context.getString(R.string.config_delete) + baseFilmList.get(position).getTitle() +"？", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserRestClient.getInstance(_context).cancelCollectFilm(String.valueOf(baseFilmList.get(position).getId()), new AbstractRestClient.ResponseCallBack() {
                                        @Override
                                        public void onFailure(Response resp, Throwable e) {
                                            ToastUtils.showShort(_context,_context.getString(R.string.delete_fail));
                                        }

                                        @Override
                                        public void onSuccess(Response resp) throws JSONException {
                                            baseFilmList.remove(position);
                                            notifyDataSetChanged();
                                            ToastUtils.showShort(_context,_context.getString(R.string.delete_suc));
                                        }
                                    });
                                }
                            }, null).show();

                            return false;
                        }
                    });
                }
            }
        }
    }
}
