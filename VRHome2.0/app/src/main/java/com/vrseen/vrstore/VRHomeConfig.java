package com.vrseen.vrstore;

import java.io.File;

/**
 * 配置
 * Created by mll on 2016/5/20.
 */
public class VRHomeConfig {

    public static String SDCARD_PATH_NAME = "VrHome2.0";
    public static final String SERVER_VERSION = "application/x.vr.v2+json"; //头文件类型
    public  static String SAVE_PATH = ""; //总文件路口
    public  static String DEFAULT_SAVE_IMAGES_PATH = ""; // 默认下载图片的路径
    public  static String DEFAULT_SAVE_VIDEOS_PATH = ""; // 默认下载视频的路径
    public  static String DEFAULT_SAVE_OTHERS_PATH = ""; // 默认下载其他的路径
    public  static String SAVE_PANO_IMAGE = "";
    public  static String SAVE_PANO_VIDEO = "";
    public  static String SAVE_MOVIE_2D = "";
    public  static String SAVE_MOVIE_3D = "";
    public  static String SAVE_THUMBS = "";
    public  static String DEFAULT_SAVE_DB_PATH = "";

    public static final int DOWN_APP = 0;
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;

    public static final int TYPE_APP = 1; //app
    public static final int TYPE_PANORAMA = 2;//全景
    public static final int TYPE_VIDEO= 3;//影视

    public static final int VR_STORE_ID = 0;//应用商店
    public static final int VR_RECENTLYVIEWED_ID = 1;//历史记录
    public static final int VR_PANOTEXTURE_ID = 4;//全景图片
    public static final int VR_PANOVIDEO_ID = 5;//全景视频
    public static final int VR_PANOROAM_ID = 6;//全景漫游
    public static final int VR_MYDOWNLOAD_ID = 7;//本地
    public static final int VR_CINEMA_ID = 8;//影视
    public static final int VR_OFFLINESTORE_ID = 9;//ZTE VR
    public static final int VR_DEFAULT_ID = 10;//默认场景
    public static final int VR_PANO_LIVE_ID = 11;//在线全景(VR那边通过后缀判断是全景视频、全景图片、漫游)

    //VR模式
    public static final int TYPE_VR = 1;//VR模式
    public static final int TYPE_NO_VR  = 0;//非VR模式

    //维度
    public static final String TYPE_2D  = "2D";
    public static final String TYPE_3D  = "3D";

    //渲染类型
    public static final String TYPE_RENDER_LR  = "LR";//左右
    public static final String TYPE_RENDER_TB  = "TB";//上下

    //分类的类型
    public static final int LOCAL_TYPE_APP = 0;
    public static final int LOCAL_TYPE_PANO_IMAGE = 1;
    public static final int LOCAL_TYPE_PANO_VIDEO = 2;
    public static final int LOCAL_TYPE_MOVIE_2D = 3;
    public static final int LOCAL_TYPE_MOVIE_3D_TB = 4;
    public static final int LOCAL_TYPE_MOVIE_3D_LR = 5;
    public static final int LOCAL_TYPE_MOVIE_3D = 6;

    public static final int BANNER_CHANGE_INTERVAL = 5000;


    public  static final int GET_LOCALRES_THUMB      = 0X200012;

    public enum ReleaseType{
        OFFICIAL,//官方版本
        ZTE//中兴版本
    }

    public static ReleaseType CUR_RELEASE_TYPE = ReleaseType.OFFICIAL;

    //------------------------中兴帐号 start---------------------
    public static String VR_HOME_ID  = "z-vr";//此z-vr可以自行定义但需通知帐号小组）
    public final static int ZTE_REQUEST_ADD_ACCOUNT = 1;//账号添加界面
    public final static int ZTE_REQUEST_ACCOUNT_MANAGER = 2;//账号管理


    //------------------------中兴帐号 end---------------------



    public static void setSavePath(String savePath){
        SAVE_PATH = savePath;
        if(VRHomeConfig.CUR_RELEASE_TYPE == ReleaseType.ZTE)
            SDCARD_PATH_NAME = "ZTE";
        // 默认下载图片的路径
        DEFAULT_SAVE_IMAGES_PATH = SAVE_PATH + "download/images" + File.separator;
        // 默认下载视频的路径
        DEFAULT_SAVE_VIDEOS_PATH =  SAVE_PATH + "download/videos" + File.separator;
        // 默认下载其他的路径
        DEFAULT_SAVE_OTHERS_PATH =  SAVE_PATH + "download/others" + File.separator;
        SAVE_PANO_IMAGE = SAVE_PATH + "360/image" + File.separator;
        SAVE_PANO_VIDEO = SAVE_PATH + "360/video" + File.separator;
        SAVE_MOVIE_2D = SAVE_PATH + "movie/2D" + File.separator;
        SAVE_MOVIE_3D = SAVE_PATH + "movie/3D" + File.separator;
        SAVE_THUMBS = SAVE_PATH + "thumb" + File.separator;
        //data数据
        DEFAULT_SAVE_DB_PATH = SAVE_PATH + "data/db" + File.separator;
    }
}
