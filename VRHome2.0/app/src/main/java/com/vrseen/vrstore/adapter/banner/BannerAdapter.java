/**
 *
 */
package com.vrseen.vrstore.adapter.banner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.model.bannel.Banner;

import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;


/**
 * Banner适配器
 *
 * @author jiangs
 */
public class BannerAdapter extends PagerAdapter implements View.OnClickListener {

    private final List<Banner> mBanners;
    private in.srain.cube.image.ImageLoader imageLoader ;
    private OnItemClickListener mOnItemClickListener;
    private DisplayImageOptions displayOptions;
    private LayoutInflater mLayoutInflater;

    public BannerAdapter(final Context context, List<Banner> banners) {
        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(context);
        handler.setLoadingResources(R.drawable.jiazaiguanggao);
        handler.setErrorResources(R.drawable.jiazaishibai_guanggao);
        imageLoader= ImageLoaderFactory.create(context,handler);

        this.mBanners = banners;
        mLayoutInflater = LayoutInflater.from(context);
//        this.displayOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.color.transparent)
//                .showImageForEmptyUri(R.color.transparent)
//                .bitmapConfig(Config.RGB_565)
//                .showImageOnFail(R.color.transparent)
//                .considerExifParams(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//                .cacheInMemory(true).cacheOnDisk(true).build();

    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.item_banner, null);
        CubeImageView image = (CubeImageView) view.findViewById(R.id.image);

        container.addView(view, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        String path = mBanners.get(position).getImagurl();
        image.loadImage(imageLoader,path);
        view.setOnClickListener(this);
        view.setTag(mBanners.get(position));
        return view;
    }

    @Override
    public int getCount() {
        return mBanners.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ImageView image = (ImageView) view.findViewById(R.id.image);
        container.removeView(view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        Banner tag = (Banner) ((View) object).getTag();
        int index = mBanners.indexOf(tag);
        if (index == -1) {
            return POSITION_NONE;
        } else {
            return index;
        }
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            Banner b = (Banner) view.getTag();
            mOnItemClickListener.onClick(b);
        }
    }

    public interface OnItemClickListener {
        void onClick(Banner b);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }
}