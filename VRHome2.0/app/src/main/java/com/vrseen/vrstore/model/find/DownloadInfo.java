package com.vrseen.vrstore.model.find;

import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.http.HttpHandler;

import java.io.File;


public class DownloadInfo {

    public DownloadInfo() {
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    private String views;

    private long id;

    @Transient
    private HttpHandler<File> handler;

    private HttpHandler.State state;

    private String downloadUrl;

    private String fileName;

    private String fileSavePath;

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    private int resourceId;

    private long progress;

    private long fileLength;

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    private String scheme;							//app包名

    private long lastModifiedTime;

    private boolean autoResume;

    private boolean autoRename;

    private String thumbUrl;

    private boolean localThumbExist;

    public boolean isLocalThumbExist() {
        return localThumbExist;
    }

    public void setLocalThumbExist(boolean localThumbExist) {
        this.localThumbExist = localThumbExist;
    }

    public int getType() {
        return type;
    }

    public int getTypeForUnity(){
        if(type == 2){
            String[] s = fileSavePath.split("\\.");
            if(s.length > 1) {
                String h = s[s.length - 1];
                if (h.equals("jpg") || h.equals("png")) {
                    return 1;
                }
            }
            return 2;
        }
        return type;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


    public void setType(int type) {
        this.type = type;
    }

    private int type;//0,app,1,全景

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl ;
    }

    public void setThumbUrlForLocal(String thumbUrl){
        this.thumbUrl = thumbUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HttpHandler<File> getHandler() {
        return handler;
    }

    public void setHandler(HttpHandler<File> handler) {
        this.handler = handler;
    }

    public HttpHandler.State getState() {
        return state;
    }

    public void setState(HttpHandler.State state) {
        this.state = state;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public boolean isAutoResume() {
        return autoResume;
    }

    public void setAutoResume(boolean autoResume) {
        this.autoResume = autoResume;
    }

    public boolean isAutoRename() {
        return autoRename;
    }

    public void setAutoRename(boolean autoRename) {
        this.autoRename = autoRename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadInfo)) return false;

        DownloadInfo that = (DownloadInfo) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
