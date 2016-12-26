package com.vrseen.vrstore.adapter.home;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.app.AppDetailActivity;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.activity.panorama.PanoramaDetailActivity;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.Home.HomeData;
import com.vrseen.vrstore.util.ConfigDefaultImageUtils;
import com.vrseen.vrstore.util.StringUtils;

import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * Created by jiangs on 16/5/4.
 */
public class HomeGridAdapter extends BaseAdapter implements View.OnClickListener {
    public static final int TYPE_MOVIE = 2;
    public static final int TYPE_QUJIN = 1;
    public static final int TYPE_APP = 0;
    private LayoutInflater layoutInflater;
    private HomeData.GroupData data;
    private Context context;
    //    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions iconOptions;
    private DisplayImageOptions iconroundOptions;

    private in.srain.cube.image.ImageLoader imageLoader_App;

    private in.srain.cube.image.ImageLoader imageLoader_Pano;

    private in.srain.cube.image.ImageLoader imageLoader_Film;

    public HomeGridAdapter(Context context, HomeData.GroupData data) {

        this.data = data;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        init();

    }

    public HomeGridAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        init();
    }

    public void setGroupData(HomeData.GroupData data) {
        this.data = data;
    }


    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return data.getStyle();
    }

    @Override
    public int getCount() {
        if (data.getItems() != null && data.getItems().size() > 0)
            return data.getItems().size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return data.getItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        int type = getItemViewType(position);

        if (convertView == null) {

            switch (type) {
                case TYPE_MOVIE:
                    convertView = layoutInflater.inflate(R.layout.grid_item_home, parent, false);
                    holder = new ViewHolder();
                    holder.loadCommonView(convertView, data);
                    holder.fill(data, position);
                    convertView.setTag(holder);
                    break;
                case TYPE_QUJIN:
                    holder = new ViewHolder();
                    convertView = layoutInflater.inflate(R.layout.grid_item_home_type2, parent, false);
                    holder.loadCommonView(convertView, data);
                    holder.fill(data, position);
                    convertView.setTag(holder);
                    break;
                case TYPE_APP:
                    holder = new ViewHolder();
                    convertView = layoutInflater.inflate(R.layout.grid_item_home_type3, parent, false);

                    holder.loadCommonView(convertView, data);
                    holder.fill(data, position);
                    convertView.setTag(holder);
                    break;
            }

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(this);
        return convertView;
    }


    private void init() {
        imageLoader_App = ConfigDefaultImageUtils.getInstance().getAppImageLoader(context);
        imageLoader_Pano = ConfigDefaultImageUtils.getInstance().getPanoramaImageLoader(context);
        imageLoader_Film = ConfigDefaultImageUtils.getInstance().getFilmImageLoader(context);
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder {
        TextView tv_title;
        TextView tv_sub_title;
        CubeImageView iv_cover;
        ImageView img_tag;
        //type1有的字段
        TextView tv_movie_update_num;

        //type2有的字段
        TextView tv_views;
        ImageView tv_type;

        //type3有的字段
        TextView tv_detail;
        public void loadCommonView(View convertView, HomeData.GroupData groupData) {
            int style = groupData.getStyle();
            tv_title = (TextView) convertView.findViewById(R.id.tv_title);

            iv_cover = (CubeImageView) convertView.findViewById(R.id.iv_cover);
            img_tag = (ImageView) convertView.findViewById(R.id.img_tag);
            switch (style) {
                case TYPE_MOVIE:
                    tv_movie_update_num = (TextView) convertView.findViewById(R.id.tv_movie_update_num);
                    tv_sub_title = (TextView) convertView.findViewById(R.id.tv_sub_title);
                    break;
                case TYPE_QUJIN:
                    tv_views = (TextView) convertView.findViewById(R.id.tv_views);
                    tv_type = (ImageView) convertView.findViewById(R.id.tv_type);
                    break;
                case TYPE_APP:
                    tv_sub_title = (TextView) convertView.findViewById(R.id.tv_sub_title);
                    break;
            }
        }

        public void fill(final HomeData.GroupData groupData, int position) {
            List<HomeData.GroupData.Item> items = groupData.getItems();
            final HomeData.GroupData.Item item = items.get(position);
            item.setPic(item.getPic().replace(" ","%20"));

            String tag = item.getTag();
            if(StringUtils.isBlank(tag))
            {
                img_tag.setVisibility(View.GONE);
            }
            else
            {
                img_tag.setVisibility(View.VISIBLE);
                if(tag.equals("VIP"))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img_tag.setImageDrawable(context.getDrawable(R.drawable.icon_vip));
                    }
                }
                else if(tag.equals("HOT"))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img_tag.setImageDrawable(context.getDrawable(R.drawable.icon_remen));
                    }
                }
            }

            tv_title.setText(item.getTitle() + "");

            if (groupData.getStyle() == TYPE_QUJIN) {
                tv_views.setText(item.getViews() + "");
                iv_cover.loadImage(imageLoader_Pano, item.getPic());
                if(item.getContent_type() == 0)
                {
                    tv_type.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_video));
                }
                else
                {
                    tv_type.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_tupian));
                }


//                imageLoader.displayImage(item.getPic(), iv_cover, iconOptions);
            } else if (groupData.getStyle() == TYPE_MOVIE) {
                if(StringUtils.isBlank(item.getState()))
                {
                    tv_movie_update_num.setText(item.getState());
                }
                tv_sub_title.setText(item.getSubtitle() + "");
                iv_cover.loadImage(imageLoader_Film, item.getPic());
//                imageLoader.displayImage(item.getPic(), iv_cover, iconOptions);
            } else if (groupData.getStyle() == TYPE_APP) {
                tv_sub_title.setText(item.getSubtitle() + "");
                iv_cover.loadImage(imageLoader_App, item.getPic());
//                imageLoader.displayImage(item.getPic(), iv_cover, iconroundOptions);
            }


            //选择打开的功能
            iv_cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (groupData.getStyle())
                    {
                        case TYPE_QUJIN:
                            Intent intent=new Intent(context, PanoramaDetailActivity.class);
                            intent.putExtra("panoramaId",item.getId());
                            context.startActivity(intent);
                            break;
                        case TYPE_MOVIE:
                            FilmDetailData.PlayProgressData playProgressData = new FilmDetailData.PlayProgressData();
                            playProgressData.setId(item.getId());
                            FilmDetailActivity.actionStart(context,playProgressData);
                            break;
                        case TYPE_APP:
                            AppDetailActivity.actionStart(context,item.getId());
                            break;
                        default:break;
                    }


                }
            });
        }

    }
}
