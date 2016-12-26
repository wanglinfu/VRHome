package com.vrseen.vrstore.logic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.lidroid.xutils.util.LogUtils;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.model.find.LocalResInfo;
import com.vrseen.vrstore.util.DownloadUtils;
import com.vrseen.vrstore.util.FileUtils;
import com.vrseen.vrstore.util.MediaFileUtil;
import com.vrseen.vrstore.util.SDCardReaderUtil;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ThumbnailUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 本地文件夹处理
 * Created by mll on 2016/3/28.
 */
public class FileLogic {

    //本地数据
    public ArrayList<LocalResInfo> localFileArrList = new ArrayList<>();

    private static FileLogic _instance;
    public static FileLogic getInstance()
    {
        if(_instance == null)
        {
            _instance = new FileLogic();
        }
        return _instance;
    }

    //初始化本地路径
    public void initByHandler(Context context, Handler handler, int msgType)
    {
       init(context);
       handler.sendEmptyMessage(msgType);
    }

    public String getLocalFilePath(String packageName){
        String url = SDCardReaderUtil.CheckSDCardPath();
        //暂时不用外置SD卡
//        if(FileUtil.checkExternalSDExists())
//        {
//            //有sd卡,Android/data/{apppackage-name}不能改
//            return FileUtil.getExternalSDRoot() + File.separator
//                    + "Android" + File.separator
//                    + "data" + File.separator
//                    + packageName + File.separator
//                    + "VRSeenLocal" + File.separator
//                    + "ZTE"+ File.separator;
//        }
        return SDCardReaderUtil.GetInnerSDCardPath() + File.separator
                    + "VRSeenLocal" + File.separator
                    + VRHomeConfig.SDCARD_PATH_NAME + File.separator;

    }



    //创建文件夹
    public void createLocalFile(Context context) {

        // FIXME: 2016/6/24 暂时不区分 上下左右的3d
        initPath(context, VRHomeConfig.SAVE_MOVIE_3D + "LR" + File.separator, "");
        initPath(context, VRHomeConfig.SAVE_MOVIE_3D + "TB"+ File.separator,"");
        //initPath(context, VRHomeConfig.SAVE_MOVIE_3D,"3D");
        initPath(context, VRHomeConfig.SAVE_MOVIE_2D,"");
        initPath(context, VRHomeConfig.SAVE_PANO_VIDEO,"");
        initPath(context, VRHomeConfig.SAVE_PANO_IMAGE, "");
        try {
            FileUtils.copyDataToSD(context, "localReadme.txt", VRHomeConfig.SAVE_PATH , "localReadme.txt");
        }catch (IOException e){
            Log.e("copyDataToSD" , e.toString());
        }
    }

    public void init(Context context)
    {
        //synchronized (this)
        {
            localFileArrList.clear();
            //初始化数据
            initPath(context, VRHomeConfig.SAVE_MOVIE_3D + "LR"+ File.separator,"LR");
            initPath(context, VRHomeConfig.SAVE_MOVIE_3D +"TB"+ File.separator,"TB");
            //initPath(context, VRHomeConfig.SAVE_MOVIE_3D,"3D");
            initPath(context, VRHomeConfig.SAVE_MOVIE_2D,"2D");
            initPath(context, VRHomeConfig.SAVE_PANO_VIDEO,"video");
            initPath(context, VRHomeConfig.SAVE_PANO_IMAGE,"image");

        }

    }

    //删除数据
    public void removeFile(LocalResInfo vo)
    {
        for (int i = 0; i < localFileArrList.size(); i++) {

            LocalResInfo localFile = localFileArrList.get(i);
            if(vo.getThumbUrl().equals(localFile.getThumbUrl()))
            {
                File file = new File(localFile.getFileSavePath());
                if(file.exists())
                {
                    file.delete();
                }

                File file1 = new File(localFile.getThumbUrl());
                if(file1.exists())
                {
                    file1.delete();
                }
                localFileArrList.remove(i);
                //删除文件
                return;
            }
        }
    }

    private void initPath(Context context, String fileStr, String file_Name)
    {
        String thumbPath = VRHomeConfig.SAVE_THUMBS;

        if(thumbPath == null || fileStr == null || file_Name == null)
            return;

        //文件夹操作，加锁
        //_lock.lock();

        File file = new File(fileStr);
        if (!file.exists()) {
            file.mkdirs();
        }

        File file1 = new File(thumbPath);
        if (!file1.exists()) {

            file1.mkdirs();
        }
        if(StringUtils.isBlank(file_Name))
        {
            return;
        }

        try{
            File[] files = file.listFiles();
            if(files == null) return;

            if(files.length > 0)
            {
                //synchronized(this)//注释掉，在调用处添加
                {
                    for(int j=0;j<files.length;j++)
                    {
                        if(!files[j].isDirectory())
                        {
                            String fileName = files[j].getName();
                            String name  =  FileUtils.getFileNameNoFormat(fileName);
                            String fullFileName = fileStr + fileName;
                            String saveThumbImageName = thumbPath + "thumb_"+ name +".jpg";
                            Long lastModifiedTime = files[j].lastModified();
                            File thumbFile = new File(saveThumbImageName);
                            //生成数据
                            if(name != null)
                            {
                                LocalResInfo vo = new LocalResInfo();

                                vo.setLastModifiedTime(lastModifiedTime);
                                vo.setFileName(name);
                                vo.setThumbUrl(saveThumbImageName);
                                vo.setFileSavePath(fullFileName);

                                int id = DownloadUtils.getInstance().getDownloadInfoListCount() * localFileArrList.size() + 1;

                                if(DownloadUtils.getInstance().getDownloadVOBySaveName(fullFileName) == null)
                                {
                                    vo.setId(id);
                                    if(file_Name.equals("image"))
                                    {
                                        vo.setType(VRHomeConfig.LOCAL_TYPE_PANO_IMAGE);
                                    }
                                    else if(file_Name.equals("video"))
                                    {
                                        vo.setType(VRHomeConfig.LOCAL_TYPE_PANO_VIDEO);
                                    }else if(file_Name.equals("2D"))
                                    {
                                        vo.setType(VRHomeConfig.LOCAL_TYPE_MOVIE_2D);
                                    }
                                    else if(file_Name.equals("3D"))
                                    {
                                        vo.setType(VRHomeConfig.LOCAL_TYPE_MOVIE_3D);
                                    }
                                    else if(file_Name.equals("LR"))
                                    {
                                        vo.setType(VRHomeConfig.LOCAL_TYPE_MOVIE_3D);
                                        vo.setSmallType(VRHomeConfig.LOCAL_TYPE_MOVIE_3D_LR);
                                    }
                                    else if(file_Name.equals("TB"))
                                    {
                                        vo.setType(VRHomeConfig.LOCAL_TYPE_MOVIE_3D);
                                        vo.setSmallType(VRHomeConfig.LOCAL_TYPE_MOVIE_3D_TB);
                                    }

                                    localFileArrList.add(vo);
                                }
                            }
                            if(!thumbFile.exists())
                            {
                                Bitmap bitmap = null;
                                if(MediaFileUtil.isVideoFileType(fullFileName))
                                {
                                    //视频

                                    bitmap = ThumbnailUtil.getVideoThumbnail(fullFileName, 400, 384, MediaStore.Images.Thumbnails.MINI_KIND);
                                }
                                else
                                {
                                    //图片
                                    bitmap = ThumbnailUtil.getImageThumbnail(fullFileName,512,384);
                                }
                                if(bitmap == null){
                                    Resources res= context.getResources();
                                    bitmap= BitmapFactory.decodeResource(res, R.drawable.jiazaishibai_quanjing);
                                }
                                saveBitmap(thumbFile,bitmap);
                            }
                        }
                    }
                }

            }
        }
        catch(Exception e)
        {
            LogUtils.e("fileError：" + e.getMessage());
        }

       // _lock.unlock();//解锁
    }

    private int getType(String fileName)
    {
        int type1 ;
        if(fileName.equals("pano"))
        {
            type1 = 1;
        }else if(fileName.equals("movie"))
        {
            type1 = 2;
        }
        else if(fileName.equals("app"))
        {
            type1 = 0;
        }
        if(fileName.equals("movie_2D"))
        {
            type1 = 1;
        }else if(fileName.equals("movie_3D"))
        {
            type1 = 2;
        }
        else if(fileName.equals("pano_image"))
        {
            type1 = 1;
        }
        if(fileName.equals("pano_video"))
        {
            type1 = 2;
        }else if(fileName.equals("movie_LR"))
        {
            type1 = 1;
        }
        else if(fileName.equals("movie_TB"))
        {
            type1 = 2;
        }
        else
        {
            type1 = -1;
        }
        return type1;
    }


    public void saveBitmap(File file, Bitmap bitmap) {

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            bitmap.recycle();
            bitmap = null;
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
