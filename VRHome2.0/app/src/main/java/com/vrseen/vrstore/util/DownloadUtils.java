package com.vrseen.vrstore.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.converter.ColumnConverterFactory;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.logic.AppLogic;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.server.DownloadService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：VRHome
 * 类描述：下载
 * 创建人：mll
 * 创建时间：2016/1/13 9:23
 * 修改人：mll
 * 修改时间：2016/1/13 9:23
 * 修改备注：
 */

public class DownloadUtils {

    private List<DownloadInfo> _downloadInfoVOList;

    private int _maxDownloadThread = 3;

    private static Context _context = null;//默认使用ApplicationContext
    private DbUtils _db;

    private static Handler _handler = new Handler();

    //修改意外断开download后的文件状态
    private static List<DownloadInfo> _fixDownLoadInfoVOList = new ArrayList<DownloadInfo>();

    /**
     * 初始化加载器
     *
     * @param context
     */
    public static void initDownload(Context context) {
        LogUtils.d("initDownLoad:" + context.getPackageName());
        if (_context == null) {
            _context = context.getApplicationContext();
            fixDownload();
        }
    }

    public static void fixDownload() {
        List<DownloadInfo> downloadInfoVOList = DownloadUtils.getInstance().getlist();
        for (int i = 0; i < downloadInfoVOList.size(); i++) {
            _fixDownLoadInfoVOList.add(downloadInfoVOList.get(i));
        }
        _handler.postDelayed(runnable, 1000);
    }

    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //修改意外断开download后的文件状态
            for (int j = 0; j < _fixDownLoadInfoVOList.size(); j++) {
                DownloadInfo fixVo = _fixDownLoadInfoVOList.get(j);
                DownloadInfo vo = DownloadUtils.getInstance().getDownloadVO(fixVo.getResourceId());
                if (fixVo.getProgress() == vo.getProgress() && vo.getState() == HttpHandler.State.LOADING) {
                    vo.setState(HttpHandler.State.CANCELLED);
                }
            }
        }
    };

    private static DownloadUtils _instance;

    public static DownloadUtils getInstance() {
        if (_instance == null) {
            _instance = DownloadService.getDownloadManager(_context);
        }
        return _instance;
    }

    public DownloadUtils(Context appContext) {
        ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class, new HttpHandlerStateConverter());
        if (appContext == null) {
            throw new RuntimeException("No ApplicationContext！！！");
        }

        try {




            _db = DbUtils.create(appContext, "VrHomeDb");
           // _db = DbUtils.create(appContext,VRHomeConfig.DEFAULT_SAVE_DB_PATH,"VrHomeDb");
        } catch (NullPointerException e) {
            Log.e("create DB", "create DB error!");
        }

        try {
            _downloadInfoVOList = _db.findAll(Selector.from(DownloadInfo.class));
        } catch (DbException e) {
            LogUtils.e(e.getMessage(), e);
        } catch (NullPointerException e) {
            Log.e("create DB", "create DB error!");
        }
        if (_downloadInfoVOList == null) {
            _downloadInfoVOList = new ArrayList<DownloadInfo>();
        }
    }

    public int getDownloadInfoListCount() {
        return _downloadInfoVOList.size();
    }

    public DownloadInfo getDownloadInfo(int index) {
        return _downloadInfoVOList.get(index);
    }

    /**
     * 返回全景，影视文件地址
     */
    public DownloadInfo getDownloadVO(int resourceId) {

        for (int i = 0; i < _downloadInfoVOList.size(); i++) {
            DownloadInfo vo = _downloadInfoVOList.get(i);
            if (resourceId == vo.getResourceId()) {
                return vo;
            }
        }
        return null;
    }

    /**
     * 获取VO
     */
    public DownloadInfo getDownloadVOByName(String resourceName) {

        for (int i = 0; i < _downloadInfoVOList.size(); i++) {
            DownloadInfo vo = _downloadInfoVOList.get(i);
            if (resourceName.equals(vo.getFileName())) {
                return vo;
            }
        }
        return null;
    }

    /**
     * 获取VO
     */
    public DownloadInfo getDownloadVOBySaveName(String resourceName) {
        for (int i = 0; i < _downloadInfoVOList.size(); i++) {
            DownloadInfo vo = _downloadInfoVOList.get(i);
            if (resourceName.equals(vo.getFileSavePath())) {
                return vo;
            }
        }
        return null;
    }


    /**
     * 检测是否下载中
     *
     * @param url
     * @return
     */
    public boolean checkInDowning(String url) {
        for (int i = 0; i < _downloadInfoVOList.size(); i++) {
            DownloadInfo vo = _downloadInfoVOList.get(i);
            if (url.equals(vo.getDownloadUrl())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkInDowning(int id) {
        for (int i = 0; i < _downloadInfoVOList.size(); i++) {
            DownloadInfo vo = _downloadInfoVOList.get(i);
            if (id == vo.getResourceId()) {
                return true;
            }
        }
        return false;
    }

    public HttpHandler.State getDownLoadState(String url) {
        for (int i = 0; i < _downloadInfoVOList.size(); i++) {
            DownloadInfo vo = _downloadInfoVOList.get(i);
            if (url.equals(vo.getDownloadUrl())) {
                return vo.getState();
            }
        }
        return null;
    }

    public String getSdUrl(String url) {
        for (int i = 0; i < _downloadInfoVOList.size(); i++) {
            DownloadInfo vo = _downloadInfoVOList.get(i);
            if (url.equals(vo.getDownloadUrl())) {
                return vo.getFileSavePath();
            }
        }
        return null;
    }

    public List<DownloadInfo> getlist() {
        return _downloadInfoVOList;
    }

    public void addNewDownload(DownloadInfo downloadInfoVO, final RequestCallBack<File> callback) throws DbException {
        HttpUtils http = new HttpUtils();
        http.configRequestThreadPoolSize(_maxDownloadThread);
        HttpHandler<File> handler = http.download(
                downloadInfoVO.getDownloadUrl(),
                downloadInfoVO.getFileSavePath(),
                downloadInfoVO.isAutoResume(),
                downloadInfoVO.isAutoRename(),
                new ManagerCallBack(downloadInfoVO, callback));
        downloadInfoVO.setHandler(handler);
        downloadInfoVO.setState(handler.getState());
        Log.e("6666666666666",downloadInfoVO.getFileName()+"     "+downloadInfoVO.getState().toString());
        _downloadInfoVOList.add(downloadInfoVO);
        _db.saveBindingId(downloadInfoVO);

        Intent intent = new Intent();
        intent.setAction(downloadInfoVO.getScheme());
        intent.putExtra("CurPercent",_context.getResources().getString(R.string.download_wait));
        _context.sendBroadcast(intent);

        noticeDownLoad(_context, "《" + downloadInfoVO.getFileName() + "》" + "" + _context.getResources().getText(R.string.download_cache_exit));
    }

    public void resumeDownload(int index, final RequestCallBack<File> callback) throws DbException {
        final DownloadInfo downloadInfoVO = _downloadInfoVOList.get(index);
        resumeDownload(downloadInfoVO, callback);
    }

    public void resumeDownload(DownloadInfo downloadInfoVO, final RequestCallBack<File> callback) throws DbException {
        HttpUtils http = new HttpUtils();
        http.configRequestThreadPoolSize(_maxDownloadThread);
        HttpHandler<File> handler = http.download(
                downloadInfoVO.getDownloadUrl(),
                downloadInfoVO.getFileSavePath(),
                downloadInfoVO.isAutoResume(),
                downloadInfoVO.isAutoRename(),
                new ManagerCallBack(downloadInfoVO, callback));
        downloadInfoVO.setHandler(handler);
        downloadInfoVO.setState(handler.getState());
        _db.saveOrUpdate(downloadInfoVO);
    }

    //打开下载的通知
    private void noticeDownLoad(Context context, String msg) {
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification myNotify = new Notification();
//        myNotify.icon = R.drawable.duanxin;
//        myNotify.tickerText = msg;
//        myNotify.when = System.currentTimeMillis();
//        myNotify.flags = Notification.FLAG_NO_CLEAR;// 不能够自动清除
//        RemoteViews rv = new RemoteViews(context.getPackageName(),
//                R.layout.notification_download);
//        rv.setTextViewText(R.id.text_content, "");
//        myNotify.contentView = rv;
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
//                intent, 1);
//        myNotify.contentIntent = contentIntent;
//        manager.notify(1, myNotify);
    }

    public void removeDownload(int index) throws DbException {
        DownloadInfo downloadInfoVO = _downloadInfoVOList.get(index);
        removeDownload(downloadInfoVO);
    }

    public void removeDownload(DownloadInfo downloadInfoVO) throws DbException {
        HttpHandler<File> handler = downloadInfoVO.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        }
        //删除数据
        FileUtils.deleteFileWithPath(downloadInfoVO.getFileSavePath());
        _downloadInfoVOList.remove(downloadInfoVO);
        _db.delete(downloadInfoVO);
    }

    public void stopDownload(int index) throws DbException {
        DownloadInfo downloadInfoVO = _downloadInfoVOList.get(index);
        stopDownload(downloadInfoVO);
    }

    public void stopDownload(DownloadInfo downloadInfoVO) throws DbException {
        HttpHandler<File> handler = downloadInfoVO.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        } else {
            downloadInfoVO.setState(HttpHandler.State.CANCELLED);
        }
        _db.saveOrUpdate(downloadInfoVO);
        ToastUtils.showShort(_context, "《" + downloadInfoVO.getFileName() + "》" +
                "" + _context.getResources().getText(R.string.download_pause));
    }

    public void stopAllDownload() throws DbException {
        for (DownloadInfo downloadInfoVO : _downloadInfoVOList) {
            HttpHandler<File> handler = downloadInfoVO.getHandler();
            if (handler != null && !handler.isCancelled()) {
                handler.cancel();
            } else {
                downloadInfoVO.setState(HttpHandler.State.CANCELLED);
            }
        }
        _db.saveOrUpdateAll(_downloadInfoVOList);
    }

    public void backupDownloadInfoList() throws DbException {
        for (DownloadInfo downloadInfoVO : _downloadInfoVOList) {
            HttpHandler<File> handler = downloadInfoVO.getHandler();
            if (handler != null) {
                downloadInfoVO.setState(handler.getState());
            }
        }
        _db.saveOrUpdateAll(_downloadInfoVOList);
    }

    public int getMaxDownloadThread() {
        return _maxDownloadThread;
    }

    public void setMaxDownloadThread(int _maxDownloadThread) {
        this._maxDownloadThread = _maxDownloadThread;
    }

    public class ManagerCallBack extends RequestCallBack<File> {
        private DownloadInfo downloadInfoVO;
        private RequestCallBack<File> baseCallBack;

        public RequestCallBack<File> getBaseCallBack() {
            return baseCallBack;
        }

        public void setBaseCallBack(RequestCallBack<File> baseCallBack) {
            this.baseCallBack = baseCallBack;
        }

        private ManagerCallBack(DownloadInfo downloadInfoVO, RequestCallBack<File> baseCallBack) {
            this.baseCallBack = baseCallBack;
            this.downloadInfoVO = downloadInfoVO;
        }

        @Override
        public Object getUserTag() {
            if (baseCallBack == null) return null;
            return baseCallBack.getUserTag();
        }

        @Override
        public void setUserTag(Object userTag) {
            if (baseCallBack == null) return;
            baseCallBack.setUserTag(userTag);
        }

        @Override
        public void onStart() {
            HttpHandler<File> handler = downloadInfoVO.getHandler();
            if (handler != null) {
                downloadInfoVO.setState(handler.getState());
            }
            try {
                _db.saveOrUpdate(downloadInfoVO);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            if (baseCallBack != null) {
                baseCallBack.onStart();
            }

            downloadInfoVO.setLastModifiedTime(System.currentTimeMillis());
            ToastUtils.showShort(_context, "《" + downloadInfoVO.getFileName() + "》" +
                    "" + _context.getResources().getText(R.string.download_start_or_continue_download));
        }

        public void onWaiting(){
            HttpHandler<File> handler = downloadInfoVO.getHandler();
            if (handler != null) {
                downloadInfoVO.setState(handler.getState());
            }
            try {
                _db.saveOrUpdate(downloadInfoVO);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            if (baseCallBack != null) {
                baseCallBack.onStart();
            }

            downloadInfoVO.setLastModifiedTime(System.currentTimeMillis());
            ToastUtils.showShort(_context, "《" + downloadInfoVO.getFileName() + "》" +
                    "" + _context.getResources().getText(R.string.download_wait));
        }

        @Override
        public void onCancelled() {
            HttpHandler<File> handler = downloadInfoVO.getHandler();
            if (handler != null) {
                downloadInfoVO.setState(handler.getState());
            }
            try {
                _db.saveOrUpdate(downloadInfoVO);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            if (baseCallBack != null) {
                baseCallBack.onCancelled();
            }
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            HttpHandler<File> handler = downloadInfoVO.getHandler();
            if (handler != null) {
                downloadInfoVO.setState(handler.getState());
            }
            downloadInfoVO.setFileLength(total);
            downloadInfoVO.setProgress(current);
            downloadInfoVO.setLastModifiedTime(System.currentTimeMillis());
            try {
                _db.saveOrUpdate(downloadInfoVO);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            if (baseCallBack != null) {
                baseCallBack.onLoading(total, current, isUploading);
            }
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            HttpHandler<File> handler = downloadInfoVO.getHandler();
            if (handler != null) {
                downloadInfoVO.setState(handler.getState());
            }
            try {
                _db.saveOrUpdate(downloadInfoVO);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            if (baseCallBack != null) {
                baseCallBack.onSuccess(responseInfo);
            }

            String msg = "《" + downloadInfoVO.getFileName() + "》" +
                    "" + _context.getResources().getText(R.string.download_complete);


            noticeDownLoad(_context, msg);

            if(AppLogic.isVr(_context)){
                U3dMediaPlayerLogic.getInstance().VRToast(msg);
            }else{
                ToastUtils.showShort(_context, msg);
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            HttpHandler<File> handler = downloadInfoVO.getHandler();
            if (handler != null) {
                downloadInfoVO.setState(handler.getState());
            }
            try {
                _db.saveOrUpdate(downloadInfoVO);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            if (baseCallBack != null) {
                baseCallBack.onFailure(error, msg);
            }
        }
    }

    private class HttpHandlerStateConverter implements ColumnConverter<HttpHandler.State> {

        @Override
        public HttpHandler.State getFieldValue(Cursor cursor, int index) {
            return HttpHandler.State.valueOf(cursor.getInt(index));
        }

        @Override
        public HttpHandler.State getFieldValue(String fieldStringValue) {
            if (fieldStringValue == null) return null;
            return HttpHandler.State.valueOf(fieldStringValue);
        }

        @Override
        public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
            return fieldValue.value();
        }

        @Override
        public ColumnDbType getColumnDbType() {
            return ColumnDbType.INTEGER;
        }
    }

    //下载apk
    public static File downloadAppfromServer(String path, ProgressDialog pd) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            //DataCleanUtil.getFormatSize(conn.getContentLength())
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = FileUtils.createFile(VRHomeConfig.DEFAULT_SAVE_OTHERS_PATH, "update.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }
}
