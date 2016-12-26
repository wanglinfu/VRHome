package com.vrseen.vrstore;

import android.app.Activity;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jiangs on 16/4/29.
 */
public class VRHomeApplication extends android.support.multidex.MultiDexApplication {
    private static final String TAG = "VRHomeApplication";
    private static VRHomeApplication _instance;
    private Context _context;

    private List<Activity> _activityList = new LinkedList<>();   //用于存放每个Activity的List

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        _context = this.getApplicationContext();
        initComponents();
    }

    /**
     * 初始化第三方组件
     */
    protected void initComponents() {
        initImageLoader();
//        initUMeng();
//        initEaseMob();
    }

    /**
     * 初始化图像加载组件
     */
    protected final void initImageLoader() {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        // Initialize ImageLoader with configuration
        ImageLoader.getInstance().init(config);
    }

    public static VRHomeApplication getInstance() {
        return _instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        _activityList.add(activity);
    }

    public void deleteActivity(Activity activity)
    {
        if(activity == null)
            return;

        if(_activityList.contains(activity))
        {
            _activityList.remove(activity);
        }

    }

    public void exit() {    //遍历List，退出每一个Activity
        try {
            for (Activity activity : _activityList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();   //告诉系统回收
    }

}
