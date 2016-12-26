package com.vrseen.vrstore.util;

import android.content.Context;

import com.vrseen.vrstore.R;

import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * 设置默认图
 * Created by mll on 2016/5/14.
 */
public class ConfigDefaultImageUtils {

    private static  ConfigDefaultImageUtils _instance = null;
    public static ConfigDefaultImageUtils getInstance()
    {
        if(_instance == null)
        {
            _instance = new ConfigDefaultImageUtils();
        }
        return _instance;
    }

    private ImageLoader _adImageLoader;
    public ImageLoader getADImageLoader(Context context)
    {
        if(_adImageLoader == null)
        {
            DefaultImageLoadHandler handler = new DefaultImageLoadHandler(context);
            handler.setLoadingResources(R.drawable.jiazaiguanggao);
            handler.setErrorResources(R.drawable.jiazaishibai_guanggao);
            _adImageLoader= ImageLoaderFactory.create(context,handler);
        }
        return _adImageLoader;
    }

    private ImageLoader _panoramaImageLoader;
    public ImageLoader getPanoramaImageLoader(Context context)
    {
        if(_panoramaImageLoader == null)
        {
            DefaultImageLoadHandler handler = new DefaultImageLoadHandler(context);
            handler.setLoadingResources(R.drawable.jiazaiquanjing);
            handler.setErrorResources(R.drawable.jiazaishibai_quanjing);
            _panoramaImageLoader = ImageLoaderFactory.create(context,handler);
        }
        return _panoramaImageLoader;
    }

    private ImageLoader _appImageLoader;
    public ImageLoader getAppImageLoader(Context context)
    {
        if(_appImageLoader == null)
        {
            DefaultImageLoadHandler handler = new DefaultImageLoadHandler(context);
            handler.setImageRounded(true, context.getResources().getDimension(R.dimen.app_radius_value));
            handler.setLoadingResources(R.drawable.yingyong_zhengzaijiazai);
            handler.setErrorResources(R.drawable.jiazaishibai_yingyong);
            _appImageLoader = ImageLoaderFactory.create(context, handler);
            //_appImageLoader = ImageLoaderFactory.create(context);
        }
        return _appImageLoader;
    }

    private ImageLoader _filmImageLoader;
    public ImageLoader getFilmImageLoader(Context context)
    {
        if(_filmImageLoader == null)
        {
            DefaultImageLoadHandler handler = new DefaultImageLoadHandler(context);
            handler.setLoadingResources(R.drawable.yingshi_zhengzaijiazai);
            handler.setErrorResources(R.drawable.jiazaishibai_yingshi);
            _filmImageLoader = ImageLoaderFactory.create(context, handler);
            //  _filmImageLoader = ImageLoaderFactory.create(context);
        }

        return _filmImageLoader;
    }
}
