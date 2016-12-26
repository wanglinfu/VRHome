package com.vrseen.vrstore.adapter.panorama;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.panorama.PanoramaCollectionDetailActivity;
import com.vrseen.vrstore.model.panorama.PanoramaCollectionData;

import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景专辑相关adapter
 * 创建人：郝晓辉
 * 创建时间：2016/6/1 10:31
 * 修改人：郝晓辉
 * 修改时间：2016/6/1 10:31
 * 修改备注：
 */
public class PanoramaRelatedAdapter extends BaseAdapter implements View.OnClickListener {

    private List<PanoramaCollectionData.PanoramaCollection> _collectionList;//专辑列表
    private in.srain.cube.image.ImageLoader _imageLoader;
    private LayoutInflater _layoutInflater;
    private Context _context;

    public PanoramaRelatedAdapter(Context context, List<PanoramaCollectionData.PanoramaCollection> collectionList) {
        _layoutInflater = LayoutInflater.from(context);

        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(context);
        handler.setLoadingResources(R.drawable.jiazaiguanggao);
        handler.setErrorResources(R.drawable.jiazaishibai_guanggao);
        _imageLoader = ImageLoaderFactory.create(context, handler);

        this._collectionList = collectionList;
        this._context = context;
    }

    @Override
    public int getCount() {
        if (_collectionList != null && _collectionList.size() > 0)
            return _collectionList.size();
        return 0;
    }

    @Override
    public PanoramaCollectionData.PanoramaCollection getItem(int position) {
        return _collectionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return _collectionList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = _layoutInflater.inflate(R.layout.item_panorama_album, parent, false);

            holder.loadViews(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fill(_collectionList.get(position));

        return convertView;
    }

    class ViewHolder {
        CubeImageView iv_image;//专辑图片
        TextView tv_title;//专辑名称

        public void loadViews(View convertView) {
            iv_image = (CubeImageView) convertView.findViewById(R.id.iv_cover);
            tv_title = (TextView) convertView.findViewById(R.id.tv_title);


        }

        public void fill(PanoramaCollectionData.PanoramaCollection pcd) {
//            iv_image.loadImage(_imageLoader, pcd.getThumbnail());
            BitmapUtils bu=new BitmapUtils(_context);
            bu.display(iv_image,pcd.getThumbnail());
            tv_title.setText(pcd.getTitle());
        }


    }

    public void addData(List<PanoramaCollectionData.PanoramaCollection> list) {
        _collectionList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }
}
