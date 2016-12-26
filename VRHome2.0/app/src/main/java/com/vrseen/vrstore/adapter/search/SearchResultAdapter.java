package com.vrseen.vrstore.adapter.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.app.AppDetailActivity;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.activity.panorama.PanoramaDetailActivity;
import com.vrseen.vrstore.http.SearchRestClient;
import com.vrseen.vrstore.model.app.AppListData;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.film.FilmListData;
import com.vrseen.vrstore.model.find.FindLocalFileInfo;
import com.vrseen.vrstore.model.panorama.PanoramaDetailData;
import com.vrseen.vrstore.model.search.FilmSearchResultData;
import com.vrseen.vrstore.model.search.PanoramaSearchResultData;

import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * 项目名称：VRHome2.0
 * 类描述：搜索结果
 * 创建人：admin
 * 创建时间：2016/6/10 18:50
 * 修改人：admin
 * 修改时间：2016/6/10 18:50
 * 修改备注：
 */
public class SearchResultAdapter extends BaseAdapter{

    private Context _context;
    private List<FilmSearchResultData.FilmSearchResult> _listFilm;
    private List<AppListData.App> _listApplication;
    private List<PanoramaSearchResultData.PanoramaSearchResult> _listPanorama;
    private LayoutInflater _layoutInflater;
    private in.srain.cube.image.ImageLoader imageLoader;

    private int _type = -1;

    public SearchResultAdapter(Context _context,
                               List<FilmSearchResultData.FilmSearchResult> _listFilm,
                               List<AppListData.App> _listApplication,
                               List<PanoramaSearchResultData.PanoramaSearchResult> _listPanorama,
                                       int type) {
        this._context = _context;
        this._layoutInflater = LayoutInflater.from(_context);
        this._type = type;
        this._listFilm=_listFilm;
        this._listApplication=_listApplication;
        this._listPanorama=_listPanorama;
        init();
    }

    public void addData(List<FilmSearchResultData.FilmSearchResult> listFilm,
                         List<AppListData.App> listApplicationint,
                         List<PanoramaSearchResultData.PanoramaSearchResult> listPanorama,
                         int _type){
        switch (_type){
            case SearchRestClient.VIDEO_TAG:
                _listFilm.addAll(listFilm);
                break;
            case SearchRestClient.APPLICATION_TAG:
                _listApplication.addAll(listApplicationint);
                break;
            case SearchRestClient.PANORAMA_TAG:
                _listPanorama.addAll(listPanorama);
                break;
        }

        notifyDataSetChanged();
    }

    private void init() {
        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(_context);
        handler.setLoadingResources(R.drawable.jiazaiquanjing);
        handler.setErrorResources(R.drawable.jiazaishibai_quanjing);
        imageLoader = ImageLoaderFactory.create(_context, handler);
    }

    @Override
    public int getCount() {
        if(_listFilm!=null && _listFilm.size()>0){
            return _listFilm.size();
        }else if(_listPanorama!=null && _listPanorama.size()>0){
            return _listPanorama.size();
        }else if(_listApplication!=null && _listApplication.size()>0){
            return _listApplication.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            switch (_type) {

                case SearchRestClient.PANORAMA_TAG:
                    convertView = _layoutInflater.inflate(R.layout.item_search_result_panorama, parent, false);
                    break;
                case SearchRestClient.VIDEO_TAG:
                    convertView = _layoutInflater.inflate(R.layout.item_search_result_film, parent, false);
                    break;
                case SearchRestClient.APPLICATION_TAG:
                    convertView = _layoutInflater.inflate(R.layout.item_search_result_application, parent, false);
                    break;
            }

            holder.initView(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fill(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (_type){

                    case SearchRestClient.PANORAMA_TAG:

                        Intent intent=new Intent(_context, PanoramaDetailActivity.class);
                        intent.putExtra("panoramaId",_listPanorama.get(position).getId());
                        _context.startActivity(intent);

                        break;

                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvTime;
        TextView tvDescription;
        CubeImageView ivImage;
        TextView tvYear;
        TextView tvMainCharator;
        TextView tvViews;
        TextView tvType;
        ImageView ivStars;
        Button btnStart;
        Button btnDetail;

        public void initView(View convertView) {

            tvName=(TextView)convertView.findViewById(R.id.tv_search_result_name);
            ivImage=(CubeImageView)convertView.findViewById(R.id.iv_search_result_image);

            switch (_type){
                case SearchRestClient.PANORAMA_TAG:

                    tvTime=(TextView)convertView.findViewById(R.id.tv_search_result_time);
                    tvDescription=(TextView)convertView.findViewById(R.id.tv_search_result_description);

                    break;
                case SearchRestClient.VIDEO_TAG:

                    tvYear=(TextView)convertView.findViewById(R.id.tv_search_result_year);
                    tvMainCharator=(TextView) convertView.findViewById(R.id.tv_search_result_maincharator);
                    tvDescription=(TextView)convertView.findViewById(R.id.tv_search_result_description);
                    btnStart=(Button)convertView.findViewById(R.id.btn_search_result_start);

                    break;
                case SearchRestClient.APPLICATION_TAG:

                    tvViews=(TextView)convertView.findViewById(R.id.tv_search_result_views);
                    tvType=(TextView)convertView.findViewById(R.id.tv_search_result_type);
                    ivStars=(ImageView)convertView.findViewById(R.id.iv_search_result_star);
                    btnDetail=(Button)convertView.findViewById(R.id.btn_search_result_start);

                    break;
            }

        }

        private void fill(final int position){

            switch (_type){
                case SearchRestClient.PANORAMA_TAG:
                    PanoramaSearchResultData.PanoramaSearchResult panoramaSearchResult=_listPanorama.get(position);
                    tvName.setText(panoramaSearchResult.getName());
                    ivImage.loadImage(imageLoader,panoramaSearchResult.getThumbnail());
                    tvTime.setText(panoramaSearchResult.getCreated_at());
                    tvDescription.setText(panoramaSearchResult.getDescription());
                    break;
                case SearchRestClient.VIDEO_TAG:

                    FilmSearchResultData.FilmSearchResult filmSearchResult=_listFilm.get(position);
                    ivImage.loadImage(imageLoader,filmSearchResult.getImage_url());
                    tvName.setText(filmSearchResult.getTitle());
                    tvYear.setText(filmSearchResult.getYear());
                    tvMainCharator.setText(filmSearchResult.getActors());
                    tvDescription.setText(filmSearchResult.getPlot());
                    btnStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FilmDetailData.PlayProgressData data=new FilmDetailData.PlayProgressData();
                            data.setId(_listFilm.get(position).getId());
                            FilmDetailActivity.actionStart(_context,data);
                        }
                    });

                    break;
                case SearchRestClient.APPLICATION_TAG:

                    AppListData.App app=_listApplication.get(position);
                    tvName.setText(app.getName());
                    tvViews.setText(app.getDownload_count()+"");
                    tvType.setText(app.getApp_characters().get(0).getName());
                    ivImage.loadImage(imageLoader,app.getImage());
                    switch (app.getAvg_score()){
                        case 0:
                            ivStars.setImageResource(R.drawable.star1);
                            break;
                        case 1:
                            ivStars.setImageResource(R.drawable.star2);
                            break;
                        case 2:
                            ivStars.setImageResource(R.drawable.star3);
                            break;
                        case 3:
                            ivStars.setImageResource(R.drawable.star4);
                            break;
                        case 4:
                            ivStars.setImageResource(R.drawable.star5);
                            break;
                    }
                    btnDetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppDetailActivity.actionStart(_context,_listApplication.get(position).getId());
                        }
                    });
                    break;
            }

        }
    }

}
