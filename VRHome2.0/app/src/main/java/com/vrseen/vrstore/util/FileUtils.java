/**
 *
 */
package com.vrseen.vrstore.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vrseen.vrstore.VRHomeApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author jiangs
 * @ClassName FileUtils
 * @QQ 826565702
 * @date 2014-3-24上午10:03:17
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    private static FileUtils instance;
    // 存储程序所有内容的目录
    private static final String APP_DIR = "vrseen";

//    private static final String TEMP_DIR = "cheniu/.TEMP";

//    /**
//     * 得到当前外部存储设备的目录,外部存储空间不可读返回null
//     *
//     * @return
//     */
//    private static String getSDPath() {
//        String sdPath = null;
//        if (isSDCanRead()) {
//            sdPath = Environment.getExternalStorageDirectory() + "/";
//        }
//        return sdPath;
//    }

    /**
     * 得到当前应用程序内容目录,外部存储空间不可用时返回null
     *
     * @return
     */
    public File getExtAppDir() {
        return VRHomeApplication.getInstance().getExternalFilesDir(null);
    }

    public File getExtAppCacheDir() {
        return VRHomeApplication.getInstance().getExternalCacheDir();
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName);
    }

    /**
     * 返回存储拍照后图片的文件夹
     *
     * @return
     */
    public File getPicturesDir() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), APP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    private FileUtils() {
        // 创建应用内容目录
        if (isSDCanWrite()) {
            File externalFile = VRHomeApplication.getInstance().getExternalFilesDir(null);
            if(externalFile != null) {
                externalFile.mkdirs();
            }
            File externalFileCache = VRHomeApplication.getInstance().getExternalCacheDir();
            if(externalFileCache != null){
                externalFileCache.mkdirs();
            }
        }
    }

    /**
     * 保存图像到本地
     *
     * @param bm
     * @return
     */
    public static String saveBitmapToLocal(Bitmap bm) {
        String path = null;
        try {
            File file = FileUtils.getInstance().createTempFile("IMG_", ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            path = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isBlank(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameNoFormat(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                point);
    }

    /**
     * 删除临时文件
     *
     * @return 删除的文件数量
     */
    public static int clearTempFile(Context context) {
        File[] subs;
        int tmpcount = 0;
//        //-------临时文件-------
//        File dir = new File(getSDPath() + TEMP_DIR);
//        File[] subs = dir.listFiles();
//        for (File file : subs) {
//            tmpcount += deleteDir(file);
//        }
        //-------外部缓存-------
        File exCacheDir = context.getExternalCacheDir();
        if (exCacheDir != null) {
            subs = exCacheDir.listFiles();
            for (File file : subs) {
                tmpcount += deleteDir(file);
            }
        }
        //-------内部缓存-------
        File cacheDir = context.getCacheDir();
        subs = cacheDir.listFiles();
        for (File file : subs) {
            tmpcount += deleteDir(file);
        }
        //-------图片-------
        ImageLoader.getInstance().clearDiskCache();
        return tmpcount;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return 删除的文件(文件夹)数量
     */
    public static int deleteDir(File dir) {
        int count = 0;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (String file : children) {
                count += deleteDir(new File(dir, file));
            }
        }
        Log.d(TAG, "delete file:" + dir.getAbsolutePath());
        // 目录此时为空或者是文件，可以删除
        if (dir.delete()) {
            count++;
        }
        return count;
    }

    public static FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    /**
     * 检查sd卡是否就绪并且可读
     *
     * @return
     */
    public static boolean isSDCanRead() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED)
                && Environment.getExternalStorageDirectory().canRead();
    }

    /**
     * 检查sd卡是否就绪并且可读写
     *
     * @return
     */
    public boolean isSDCanWrite() {
        return (isSDCanRead() && Environment.getExternalStorageDirectory().canWrite());
    }

    /**
     * 判断sd卡中的文件是否存在
     *
     * @param path
     * @return
     */
    public boolean isFileExists(String path) {
        if (!TextUtils.isEmpty(path) && isSDCanRead()) {
            File f = new File(path);
            return f.exists();
        }
        return false;
    }

    public File createTempFile() throws IOException {
        File file = new File(getExtAppCacheDir(), "" + System.currentTimeMillis());
        file.createNewFile();
        return file;
    }

//    /**
//     * 在SD卡根目录上创建文件
//     *
//     * @throws IOException
//     */
//    public File creatSDFile(String fileName) throws IOException {
//        File file = new File(getSDPath() + fileName);
//        file.createNewFile();
//        return file;
//    }

//    /**
//     * 在SD卡根目录上创建目录
//     *
//     * @param dirName
//     */
//    public File creatSDDir(String dirName) {
//        File dir = new File(getSDPath() + dirName);
//        dir.mkdirs();
//        return dir;
//    }

//    /**
//     * 在应用程序内容目录上创建文件
//     *
//     * @throws IOException
//     */
//    public File creatAppDirFile(String fileName) throws IOException {
//        File file = new File(getAppDirPath() + fileName);
//        file.createNewFile();
//        return file;
//    }
//
//    /**
//     * 在应用程序内容目录上创建目录
//     *
//     * @param dirName
//     */
//    public File creatAppDirDir(String dirName) {
//        File dir = new File(getAppDirPath() + dirName);
//        dir.mkdirs();
//        return dir;
//    }

    /**
     * 复制文件
     *
     * @param src
     * @param tar
     * @throws FileNotFoundException
     */
    public void copy(File src, File tar) throws IOException {
        Log.d(TAG, "Copy File[" + src.getAbsolutePath() + "] to [" + tar.getAbsolutePath() + "] ...");
        this.copy(new FileInputStream(src), tar);
        Log.d(TAG, "Copy File...DONE");
    }

    public void copy(byte[] bytes, File tar) throws IOException {
        this.copy(new ByteArrayInputStream(bytes), tar);
    }

    /**
     * 移动文件，会删除原文件
     *
     * @param src
     * @param tar
     * @throws IOException
     */
    public void move(File src, File tar) throws IOException {
        Log.d(TAG, "Move File[" + src.getAbsolutePath() + "] to [" + tar.getAbsolutePath() + "] ...");
        if (tar.exists()) {
            throw new IOException("Target File[" + tar.getAbsolutePath() + "] exists");
        }
        Log.d(TAG, "copy file...");
        copy(src, tar);
        Log.d(TAG, "delete src file...");
        if (src.delete()) {
            Log.d(TAG, "delete src file...SUCCESS");
        } else {
            Log.d(TAG, "delete src file...FAILED");
        }
        Log.d(TAG, "Move File...DONE");
    }


    /**
     * 保存照片到相册
     *
     * @param context
     * @param photoFile
     * @param targetPath
     * @return 相册中的文件
     * @throws IOException
     */
    public File savePhotoToGallery(Context context, File photoFile, String targetPath) throws IOException {
        Log.d(TAG, "SavePhotoToGallery...");
        File tarDir = new File(targetPath);
        if (!tarDir.exists()) {
            Log.d(TAG, "create dir...");
            tarDir.mkdirs();
        }
        File tar = new File(targetPath, FileUtils.generateFileName("CHENIU_IMG_", ".jpg"));
        Log.d(TAG, "move file...");
        try {
            this.move(photoFile, tar);
            Log.d(TAG, "insert record...");
            //插入到媒体库
            ContentValues values = new ContentValues(4);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.ORIENTATION, 0);
            values.put(MediaStore.Images.Media.DATA, tar.getAbsolutePath());
            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Log.d(TAG, "SavePhotoToGallery...DONE");
            return tar;
        } catch (IOException e) {
            Log.e(TAG, "SavePhotoToGallery...FAILED.");
            throw e;
        }
    }

    /**
     * 保存流到文件
     *
     * @param in
     * @param tar
     */
    public void copy(InputStream in, File tar) throws IOException {
        File temp = new File(tar.getAbsoluteFile() + ".tmp");
        if (temp.exists()) {
            Log.d(TAG, "temp file exists, delete it.");
            temp.delete();
        }
        if (temp.isDirectory()) {
            Log.e(TAG, "target file is dir.");
        }
        FileOutputStream os = new FileOutputStream(temp);
        byte[] buffer = new byte[8192];
        int count = 0;
        int total = 0;
        try {
            Log.d(TAG, "copy to temp file[" + temp.getAbsolutePath() + "]...");
            while ((count = in.read(buffer)) > 0) {
                os.write(buffer, 0, count);
                os.flush();
                total += count;
            }
            Log.d(TAG, "copy to temp file[" + temp.getAbsolutePath()
                    + "]... DONE. Length=" + total);
            Log.d(TAG, "rename temp file[" + temp.getAbsolutePath()
                    + "] to target file[" + tar.getAbsolutePath() + "]...");
            temp.renameTo(tar);
            Log.d(TAG, "rename temp file[" + temp.getAbsolutePath()
                    + "] to target file[" + tar.getAbsolutePath() + "]... DONE");
        } catch (IOException e) {
            Log.e(TAG, "copy failed.", e);
        } finally {
            try {
                in.close();
                os.close();
            } catch (IOException e) {
                Log.e(TAG, "close stream failed.", e);
            }
        }
    }

    /**
     * 生成文件名
     *
     * @param prefix
     * @param extension
     * @return
     */
    public static String generateFileName(String prefix, String extension) {
        StringBuilder name = new StringBuilder();
        if (prefix != null) {
            name.append(prefix);
        }
        name.append(DateUtils.format(DateUtils.FULL_DATA_FORMAT));
        if (extension != null) {
            name.append(extension);
        }
        return name.toString();
    }

    /**
     * 得到amr的时长,单位是毫秒
     *
     * @param file
     * @return
     * @throws IOException
     */
    public long getAmrDuration(File file) throws IOException {
        long duration = -1;
        int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
                0, 0};
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            long length = file.length();// 文件的长度
            int pos = 6;// 设置初始位置
            int frameCount = 0;// 初始帧数
            int packedPos = -1;
            // ///////////////////////////////////////////////////
            byte[] datas = new byte[1];// 初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }
            // ///////////////////////////////////////////////////
            duration += frameCount * 20;// 帧数*20
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
        return duration;
    }

    /**
     * @param prefix
     * @param extension
     * @return
     * @throws IOException
     */
    public File createTempFile(String prefix, String extension)
            throws IOException {
        File file = new File(getExtAppCacheDir(), prefix + System.currentTimeMillis() + extension);
        file.createNewFile();
        return file;
    }

    // *********
    // Variables
    /**
     * Number of bytes in one KB = 2<sup>10</sup>
     */
    public final static long SIZE_KB = 1024L;

    /**
     * Number of bytes in one MB = 2<sup>20</sup>
     */
    public final static long SIZE_MB = SIZE_KB * SIZE_KB;

    /**
     * Number of bytes in one GB = 2<sup>30</sup>
     */
    public final static long SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;

    // ********
    // Methods

    /**
     * @return Number of bytes available on external storage
     */
    public static long getExternalAvailableSpaceInBytes() {
        long availableSpace = -1L;
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                    .getPath());
            availableSpace = (long) stat.getAvailableBlocks()
                    * (long) stat.getBlockSize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableSpace;
    }

    /**
     * @return Number of kilo bytes available on external storage
     */
    public static long getExternalAvailableSpaceInKB() {
        return getExternalAvailableSpaceInBytes() / SIZE_KB;
    }

    /**
     * @return Number of Mega bytes available on external storage
     */
    public static long getExternalAvailableSpaceInMB() {
        return getExternalAvailableSpaceInBytes() / SIZE_MB;
    }

    /**
     * @return gega bytes of bytes available on external storage
     */
    public static long getExternalAvailableSpaceInGB() {
        return getExternalAvailableSpaceInBytes() / SIZE_GB;
    }

    /**
     * @return Total number of available blocks on external storage
     */
    public static long getExternalStorageAvailableBlocks() {
        long availableBlocks = -1L;
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                    .getPath());
            availableBlocks = stat.getAvailableBlocks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return availableBlocks;
    }


    /**
     * 读取asset文件
     *
     * @param context
     * @param filename
     * @return
     */
    public static String readAssets(Context context, String filename) throws IOException {
        String result = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = context.getAssets().open(filename);
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            result = sb.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static JSONObject getJSONObjectFromAssets(Context context, String fileName) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(readAssets(context, fileName));
        } catch (IOException e) {
            throw new IllegalArgumentException("wrong file name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static String getDisplayIndex(JSONObject tempJson) {
        int index = tempJson.optInt("index");
        return getDisplayIndex(index);

    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            Log.i("DirectoryManager deleteFile", filePath);
            f.delete();
            return true;
        }
        return false;
    }

    public static String getDisplayIndex(int index) {
        if (index < 0) {
            String result;
            switch (index) {
                case -1:
                    result = "A";
                    break;
                case -2:
                    result = "B";
                    break;
                case -3:
                    result = "C";
                    break;
                case -4:
                    result = "D";
                    break;
                default:
                    result = "set";
            }
            return result;
        } else {
            return String.valueOf(index);
        }
    }

    /**
     * 保存对象
     *
     * @param key
     * @param s
     */
    public static void saveObject(final Context context, final String key, final Serializable s, boolean runInMainThread) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(key, Context.MODE_PRIVATE));
                    if (s == null) {
                        context.deleteFile(key);
                    }
                    try {
                        oos.writeObject(s);
                        oos.flush();
                        Log.d(TAG, "save object success");
                    } finally {
                        oos.close();
                    }
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "saveObject failed.", e);
                } catch (IOException e) {
                    Log.e(TAG, "saveObject failed.", e);
                }
            }
        };
        if (runInMainThread) {
            r.run();
        } else {
            new Thread(r).start();
        }
    }

    public static void deleteObject(final Context context, final String key) {
        context.deleteFile(key);
    }

    /**
     * 读取对象
     *
     * @param key
     * @return
     */
    public static Object loadObject(Context context, String key) {
        Object temp = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(context.openFileInput(key));
            try {
                temp = ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ois.close();
            }
            Log.d(TAG, "loadObject success.");
        } catch (IOException e) {
            Log.e(TAG, "loadObject failed.", e);
        }
        return temp;

    }



    /**
     * 解压到指定目录
     *
     * @param zipFilePath
     * @param descDir     目标文件夹
     */
    public void unZipFile(String zipFilePath, File descDir) throws IOException {
        unZipFile(new File(zipFilePath), descDir);
    }

    /**
     * 解压文件到指定目录
     *
     * @param zipFile
     * @param descDir 目标文件夹
     */
    public void unZipFile(File zipFile, File descDir) throws IOException {
        Log.d(TAG, "unZipFile " + zipFile.getAbsolutePath() + " To " + descDir.getAbsolutePath());
        if (!descDir.exists()) {
            Log.d(TAG, "mkdirs: " + descDir.getAbsolutePath());
            descDir.mkdirs();
        }
        //压缩文件
        File srcZipFile = zipFile;
        //基本目录
        String dest = descDir.getAbsolutePath();
        if (!dest.endsWith("/")) {
            dest += "/";
        }
        String prefixion = dest;

        //压缩输入流
        ZipInputStream zipInput = new ZipInputStream(new FileInputStream(srcZipFile));
        //压缩文件入口
        ZipEntry currentZipEntry = null;
        //循环获取压缩文件及目录
        while ((currentZipEntry = zipInput.getNextEntry()) != null) {
            //获取文件名或文件夹名
            String fileName = currentZipEntry.getName();
            //Log.v("filename",fileName);
            //构成File对象
            File tempFile = new File(prefixion + fileName);
            //父目录是否存在
            if (!tempFile.getParentFile().exists()) {
                //不存在就建立此目录
                tempFile.getParentFile().mkdir();
            }
            if (tempFile.getName().contains(".DS_Store")) {
                Log.d(TAG, "ignore file: " + tempFile.getAbsolutePath());
                continue;
            }
            //如果是目录，文件名的末尾应该有“/"
            if (currentZipEntry.isDirectory()) {
                //如果此目录不在，就建立目录。
                if (!tempFile.exists()) {
                    Log.d(TAG, "create dir: " + tempFile.getAbsolutePath());
                    tempFile.mkdir();
                }
                //是目录，就不需要进行后续操作，返回到下一次循环即可。
                continue;
            }
            Log.d(TAG, "Extract: " + fileName + " To " + tempFile.getAbsolutePath());
            //如果是文件
            if (!tempFile.exists()) {
                //不存在就重新建立此文件。当文件不存在的时候，不建立文件就无法解压缩。
                Log.d(TAG, "create file: " + tempFile.getAbsolutePath());
                tempFile.createNewFile();
            }
            //输出解压的文件
            FileOutputStream tempOutputStream = new FileOutputStream(tempFile);

            //获取压缩文件的数据
            byte[] buffer = new byte[1024];
            int hasRead = 0;
            //循环读取文件数据
            while ((hasRead = zipInput.read(buffer)) > 0) {
                tempOutputStream.write(buffer, 0, hasRead);
            }
            tempOutputStream.flush();
            tempOutputStream.close();
        }
        zipInput.close();

//        Log.d(TAG, "unZipFile " + zipFile.getAbsolutePath() + " To " + descDir.getAbsolutePath());
//        if (!descDir.exists()) {
//            Log.d(TAG, "mkdirs: " + descDir.getAbsolutePath());
//            descDir.mkdirs();
//        }
//        ZipFile zip = new ZipFile(zipFile);
//        ZipInputStream zipInputStream = new ZipInputStream(
//                new BufferedInputStream(new FileInputStream(zipFile)));
//        ZipEntry entry = null;
//        while ((entry = zipInputStream.getNextEntry()) != null) {
//            String zipEntryName = entry.getName();
//            InputStream in = zip.getInputStream(entry);
//            String outPath = new File(descDir, zipEntryName).getAbsolutePath();// (descDir + zipEntryName).replaceAll("\\*", "/");
//            File outputFile = new File(outPath);
//            //判断路径是否存在,不存在则创建文件路径
//            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
//            Log.d(TAG, "Extract: " + outPath);
//            //检查目录是否存在
//            if (!file.exists()) {
//                Log.d(TAG, "mkdirs: " + file.getAbsolutePath());
//                file.mkdirs();
//            }
//            //判断文件全路径是否为文件夹,如果是,不解压
//            if (outputFile.isDirectory()) {
//                Log.d(TAG, "Dir: " + outPath + " exist, extract abort.");
//                continue;
//            }
//            copy(in, outputFile);
//        }
    }

    /**
     * 读取文本文件
     *
     * @param file
     * @return
     */
    public static String readTextFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        InputStream in = new FileInputStream(file);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(in, "UTF-8"));
        String str = null;
        while ((str = br.readLine()) != null) {
            content.append(str);
        }
        br.close();
        in.close();
        return content.toString();
    }



    /**
     * 得到每个json文件里面对应的检测点的数量
     *
     * @param jsonObject
     * @param isBuiltJson 是否为内装路饰json
     * @return
     */
    private static int getPointNumFromJson(JSONObject jsonObject, boolean isBuiltJson) {
        int count = 0;
        if (isBuiltJson) {
            JSONArray builtArray = jsonObject.optJSONArray("car_built");
            int length = builtArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject tempJson = builtArray.optJSONObject(i);
                count += tempJson.optJSONArray("points").length();
            }
        } else {
            JSONArray array = jsonObject.optJSONArray("detection_points");
            int size = array.length();
            for (int i = 0; i < size; i++) {
                if (array.optJSONObject(i).optJSONArray("defects") != null) {
                    count++;
                } else {
                    count += 2;
                }
            }
        }
        return count;
    }/**
     * 拷贝assets下的资源文件到手机
     * @param strAssetsFileName assets下的文件名,含后缀
     * @param strOutFileName 手机上的目标路径
     * @throws IOException
     */
    public static void copyDataToSD(Context context,String strAssetsFileName ,String strOutFilePath,String strOutFileName) throws IOException
    {
        //File file = createFile(strOutFilePath,strOutFileName);
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFilePath + strOutFileName);
        myInput = context.getAssets().open(strAssetsFileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

}