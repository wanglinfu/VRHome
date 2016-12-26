package com.vrseen.vrstore.adapter.panorama;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.model.panorama.PanoramaListData;

import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景列表adapter
 * 创建人：郝晓辉
 * 创建时间：2016/6/3 11:35
 * 修改人：郝晓辉
 * 修改时间：2016/6/3 11:35
 * 修改备注：
 */
public class PanoramaListAdapter extends BaseAdapter implements View.OnClickListener {

    private List<PanoramaListData.PanoramaList> _panoramaListDataList;
    private Context _context;
    private LayoutInflater _layoutInflater;
    private in.srain.cube.image.ImageLoader imageLoader;
    private in.srain.cube.image.ImageLoader imageRoundLoader;

    public PanoramaListAdapter(Context _context, List<PanoramaListData.PanoramaList> _panoramaListDataList) {
        this._context = _context;
        this._panoramaListDataList = _panoramaListDataList;
        this._layoutInflater = LayoutInflater.from(_context);
        init();
    }

    private void init() {

        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(_context);
        handler.setLoadingResources(R.drawable.jiazaiquanjing);
        handler.setErrorResources(R.drawable.jiazaishibai_quanjing);
        imageLoader = ImageLoaderFactory.create(_context, handler);

        DefaultImageLoadHandler roundHandler = new DefaultImageLoadHandler(_context);
        roundHandler.setImageRounded(true, 20);

        imageRoundLoader = ImageLoaderFactory.create(_context, roundHandler);
    }

    @Override
    public int getCount() {
        if (_panoramaListDataList != null && _panoramaListDataList.size() > 0) {
            return _panoramaListDataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return _panoramaListDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return _panoramaListDataList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = _layoutInflater.inflate(R.layout.grid_item_home_type2, parent, false);
            holder.initView(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (_panoramaListDataList.size() > 0)
            holder.fill(_panoramaListDataList.get(position));
        return convertView;
    }

    public void addData(List<PanoramaListData.PanoramaList> list) {
        _panoramaListDataList.addAll(list);
        notifyDataSetChanged();
    }


    class ViewHolder {
        TextView tv_title;
        ImageView tv_type;
        CubeImageView iv_cover;
        TextView tv_views;


        public void initView(View convertView) {
            tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            tv_views = (TextView) convertView.findViewById(R.id.tv_views);
            iv_cover = (CubeImageView) convertView.findViewById(R.id.iv_cover);
            tv_type = (ImageView) convertView.findViewById(R.id.tv_type);
        }


        public void fill(final PanoramaListData.PanoramaList panoramaListData) {

            tv_views.setText(panoramaListData.getViews()+"");
            iv_cover.loadImage(imageLoader, panoramaListData.getThumbnail());
            tv_title.setText(panoramaListData.getName());
            int type = panoramaListData.getType();
            switch (type) {
                case 0:
                    tv_type.setImageResource(R.drawable.icon_video);
                    break;
                case 1:
                    tv_type.setImageResource(R.drawable.icon_tupian);
                    break;
                case 2:
                    tv_type.setImageResource(R.drawable.icon_video);
                    break;
                case 3:
                    tv_type.setImageResource(R.drawable.icon_video);
                    break;
            }


//            //选择打开的功能
//            iv_cover.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent=new Intent(_context,PanoramaDetailActivity.class);
//                    intent.putExtra("panoramaId",panoramaListData.getId());
//                    _context.startActivity(intent);
//
//
//                }
//            });
        }


    }

    @Override
    public void onClick(View v) {

    }
}
