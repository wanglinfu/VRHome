package com.vrseen.vrstore.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.fragment.user.MineFragment;
import com.vrseen.vrstore.util.SDCardReaderUtil;
import com.vrseen.vrstore.util.ThumbnailUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 项目名称：VRHome2.0
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/6/16 11:05
 * 修改人：Administrator
 * 修改时间：2016/6/16 11:05
 * 修改备注：
 */

/**
 * Created by VRSeen_Software on 2016/6/23.
 */
public class SelectPicPopupWindow extends Activity implements View.OnClickListener {
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 200;
    private static final int PHOTO_REQUEST_CUT = 300;
    private static final int REQUEST_CONTANT = 400;
    private static final int REQUEST_GET_LINKMAN = 500;
    private static int CURRENT_REQUSET_CODE = 0;
    private Button btn_cancel;
    private TextView tv_pick_photo;
    private  File file ;
    private String image_path;
    private File path1;
    private  RoundImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_dialog);

        tv_pick_photo = (TextView) this.findViewById(R.id.tv_pick_photo);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        tv_pick_photo.setOnClickListener(this);
        addListener();
    }

    private void addListener() {
        imageView = new RoundImageView(getApplicationContext());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(image_path);
                if (file != null && file.isFile() == true) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "image*//*");
                    //SelectPicPopupWindows.this.startActivity(intent);

                }
            }
        });
    }

    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_pick_photo:
               // if(android.os.Build.VERSION.SDK_INT >=23){}
                getImageFromAlbum();
                //getImageFromCamera();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
    /**
     * 从照相机获取照片
     */
    private void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            //文件夹jishitongxun
            //  outputImage = new File(Environment.getExternalStorageDirectory(),"output_image.jpg");
            // String path = Environment.getExternalStorageDirectory().toString() + "/jishitongxun";
            path1 = new File(Environment.getExternalStorageDirectory(),"output_image.jpg");
            if (!path1.exists()) {
                path1.mkdirs();
            }
            //指定一个图片路径对应的file对象
            file = new File(path1, System.currentTimeMillis() + ".jpg");
//            Log.i("----------------文件路径", file.toString());
            Uri mOutPutFileUri = Uri.fromFile(file);
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);

            //  Intent  intent = new Intent(Intent.ACTION_PICK, null);
            //  intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image*//*");

            startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
        } else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }
    //  Intent intent = new Intent(Intent.ACTION_PICK);
    //intent.setType("file/*");
    // intent.setType("image/*");//相片类型
    //intent.setType("txt/*");
    //  intent.setType("image/*");
    //intent.setType(“audio/*”); //选择音频
    //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
    //intent.setType(“video/*;image/*”);//同时选择视频和图片




    /**
     * 从本地相册获取图片
     */
    private void getImageFromAlbum() {

        CURRENT_REQUSET_CODE = REQUEST_CODE_PICK_IMAGE;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }
    /**
     * 开启文件管理器
     */
    private void getContant() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CONTANT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            //本地相册返回相册
            case REQUEST_CODE_PICK_IMAGE:
                resultFromPhoto(resultCode, data);
                return;
            //相机返回照片
            case REQUEST_CODE_CAPTURE_CAMEIA:
                resultFronCamare(resultCode, data);
                break;
            // 从剪切图片返回的数据
            case PHOTO_REQUEST_CUT:
                resultFromCut(data);
                break;
            //文件
            case REQUEST_CONTANT:
                resultFromFile(resultCode, data);
                break;

        }
        finish();
    }
    /**
     * 本地文件返回结果的处理
     *
     * @param resultCode 返回码
     * @param data       数据
     */
    private void resultFromFile(int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                if (data != null) {
                    Uri uri = data.getData();
                    //获取文件路径
                    String img_path = getFilePath(uri);
                    //  filePath.setText(img_path);

//                  File file = new File(img_path);
//                  File file1 = new File()
//                  Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 切图返回的结果
     *
     * @param data 数据
     */
    private void resultFromCut(Intent data) {
        if (data != null) {
            Bitmap bitmap = data.getParcelableExtra("data");
            // this.imageview.setImageBitmap(bitmap);
            MineFragment.uploadHeadPicture(bitmap);
        }
    }

    /**
     * 相机返回的结果处理
     *
     * @param resultCode 结果码
     * @param data       数据
     */
    private void resultFronCamare(int resultCode, Intent data) {
        CURRENT_REQUSET_CODE = REQUEST_CODE_CAPTURE_CAMEIA;
        if (resultCode == RESULT_OK) {
            if (data != null) { //可能尚未指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                        Log.i("<___________-------------", "data等于null");
                //返回有缩略图
                if (data.hasExtra("data")) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    //得到bitmap后的操作
                    //  imageview.setImageBitmap(bitmap);
                    MineFragment.uploadHeadPicture(bitmap);
                }
            } else {
                //由于指定了目标uri，存储在目标uri，intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                Uri cameiaUri = Uri.fromFile(file);
//                        获取图片路径
                String path = cameiaUri.getPath();
                //image_path = path;
                //获取缩略图
                //  Bitmap bitmap = decodeBitmap(path);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                // imageview.setImageBitmap(bitmap);
                MineFragment.uploadHeadPicture(bitmap);
//                filePath.setText(path);
//                ContentResolver resolver = getContentResolver();
//                try {
//                    // 通过目标uri，找到图片
//                    //使用ContentProvider通过URI获取原始图片
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, cameiaUri);
//                    if (bitmap != null) {
//                        //为防止原始图片过大导致内存溢出，这里可以先缩小原图显示，然后释放原始Bitmap占用的内存
//                        //释放原始图片占用的内存，防止out of memory异常发生
////                                    bitmap.recycle();
//                        Bitmap scaleBitmap = createScaleBitmap(bitmap, 100, 200);
//                        imageview.setImageBitmap(scaleBitmap);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                crop(cameiaUri);
            }

        }
    }
    /**
     * 本地相册返回的照片处理
     *
     * @param resultCode 结果码
     * @param data       数据
     */
    private void resultFromPhoto(int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                if (data != null) {
                    Uri uri = data.getData();
//                    image_path.startsWith("file"){}
//                    image_path.startsWith("content")
                    //获取图片路径,的路径信息
                    String path=uri.toString();
                    if(path.startsWith("content:")){
                        image_path = getFilePath(uri);
                    }else if(path.startsWith("file:")){
                        image_path = uri.toString().split("file://")[1];
                    }

                    crop(uri);

                   /* if (image_path.startsWith("file")){
                        image_path = getFilePath(uri);
                    }else if (image_path.startsWith("content")){
                      //  image_path = getFilePath(uri);
                        image_path = uri.toString().split("file://")[1];
                    }*/
                    //  image_path = uri.toString().split("file://")[1];

                    //   filePath.setText(image_path);
                    //获取缩略图
//                    Bitmap bitmap = decodeBitmap(image_path);
//                    if (bitmap!=null){
//                        //    imageview.setImageBitmap(bitmap);
//                        MineFragment.setHeadPicture(bitmap);
//                      /*  tv_pick_photo.setVisibility(View.GONE);
//                        btn_cancel.setVisibility(View.GONE);
//                        tvyuan.setVisibility(View.GONE);*/
//
//                    }
                    //剪切图片
                }
                break;
        }
    }

    /**
     * 通过uri获取路径
     *
     * @param uri
     * @return v
     */
    private String getFilePath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        if (actualimagecursor != null) {
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            return actualimagecursor.getString(actual_image_column_index);
        }
        return null;
    }

    /**
     * 剪切图片
     */
    private void crop(Uri uri) {
//        FileTransferManager transferManager = new FileTransferManager(MyApp.conn);
//        transferManager.
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 2);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 350);
        intent.putExtra("outputY", 350);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 创建缩略图
     *
     * @param imagePath 文件路径
     * @return 缩略图
     */
    public Bitmap decodeBitmap(String imagePath) {
//        ThumbnailUtils.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 通过这个bitmap获取图片的宽和高&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        if (bitmap == null) {
            System.out.println("bitmap为空");
        }
        float realWidth = options.outWidth;
        float realHeight = options.outHeight;
        System.out.println("真实图片高度：" + realHeight + "宽度:" + realWidth);
        // 计算缩放比&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / 1000);
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println("缩略图高度：" + h + "宽度:" + w);

        return bitmap;
    }



    /**
     * bitmap转base64字符串
     *
     * @param imgPath 图片路径
     * @return base64
     */
    public static String imgToBase64(String imgPath) {
        Bitmap bitmap = null;
        if (imgPath != null && imgPath.length() > 0) {
            bitmap = readBitmap(imgPath);
        }
        if (bitmap == null) {
            //bitmap not found!!
        }

        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);

            out.flush();
            out.close();

            byte[] imgBytes = out.toByteArray();
            String s = Base64.encodeToString(imgBytes, Base64.DEFAULT);
            return s;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap readBitmap(String imgPath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }

    }
}

