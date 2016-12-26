package com.vrseen.vrstore.model.app;

import android.content.Context;

import com.google.gson.Gson;
import com.lidroid.xutils.http.HttpHandler;
import com.vrseen.vrstore.http.JsonConvertable;
import com.vrseen.vrstore.logic.AppLogic;
import com.vrseen.vrstore.model.find.DownloadInfo;
import com.vrseen.vrstore.util.DownloadUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by mll on 2016/5/16.
 */
public class AppListData implements JsonConvertable<AppListData> {
    private List<App> data;

    public List<App> getData() {
        return data;
    }

    public void setData(List<App> data) {
        this.data = data;
    }

    @Override
    public AppListData fromJson(Context context, JSONObject json) throws JSONException {

        AppListData applistdata = new Gson().fromJson(json.toString(),AppListData.class);

        for(int i=0;i<applistdata.getData().size();i++)
        {
            App app = applistdata.getData().get(i);
            if(app != null) {
                DownloadInfo downloadInfo = DownloadUtils.getInstance().getDownloadVO(app.getId());
                if(downloadInfo != null)
                {
                    HttpHandler.State state = downloadInfo.getState();
                    if(state != null)
                    {
                        switch (state)
                        {
                            case LOADING:
                                app._state = App.State.STATE_LOADING;
                                break;
                            case FAILURE:
                                app._state = App.State.STATE_LOADFAILED;
                                break;
                            case SUCCESS:
                                app._state = App.State.STATE_LOADSUCCESSED;

                                if(AppLogic.isAppInstalled(context,app.getScheme()))
                                    app._state = App.State.STATE_INSTALLED;
                                else
                                    app._state = App.State.STATE_NOINSTALLED;

                                break;
                            case WAITING:
                                app._state = App.State.STATE_LOADWAITING;
                                break;
                        }
                    }
                }
            }
        }

        return applistdata;
    }

    public static class App implements Serializable
    {
        public enum State
        {
            STATE_NONE(0),          //未下载
            STATE_LOADWAITING(1),   //等待下载中
            STATE_LOADING(2),      //下载中
            STATE_LOADSUCCESSED(3),//下载成功（预备后面做站内消息推送）
            STATE_LOADFAILED(4),   //下载失败
            STATE_LOADPAUSED(5),   //下载暂停
            STATE_INSTALLED(6),    //已安装
            STATE_NOINSTALLED(7);  //未安装

            private int value = 0;

            private State(int value) {
                this.value = value;
            }

            public  State valueOf(int value) {
                switch(value) {
                    case 0:
                        return STATE_NONE;
                    case 1:
                        return STATE_LOADWAITING;
                    case 2:
                        return STATE_LOADING;
                    case 3:
                        return STATE_LOADSUCCESSED;
                    case 4:
                        return STATE_LOADFAILED;
                    case 5:
                        return STATE_LOADPAUSED;
                    case 6:
                        return STATE_INSTALLED;
                    case 7:
                        return STATE_NOINSTALLED;
                    default:
                        return STATE_NONE;
                }
            }

            public int value() {
                return this.value;
            }
        }

        private State  _state = State.STATE_NONE;

        public State getState(){ return _state; }
        public void setState(State state)
        {
            _state = state;
        }

        //down_part is protocol data
        private int     id;								//应用的id
        private String  description;					//app描述
        private String  name;							//应用名称
        private String  image;							//应用图标
        private int     avg_score;						//应用评分
        private int     download_count;					//下载次数
        private String  scheme;							//app包名
        private int	    browse_count ;					//浏览次数
        private int     version;                        //版本号
        private String  qrtext;                          //下载地址
        private List<AppCharacters> app_characters;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getQrtext() {
            return qrtext;
        }

        public void setQrtext(String qrtext) {
            this.qrtext = qrtext;
        }

        public List<AppCharacters> getApp_characters() {
            return app_characters;
        }

        public void setApp_characters(List<AppCharacters> app_characters) {
            this.app_characters = app_characters;
        }

        public int getBrowse_count() {
            return browse_count;
        }

        public void setBrowse_count(int browse_count) {
            this.browse_count = browse_count;
        }
        public String getScheme() {
            return scheme;
        }
        public void setScheme(String scheme) {
            this.scheme = scheme;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public int getDownload_count() {
            return download_count;
        }
        public void setDownload_count(int download_count) {
            this.download_count = download_count;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getImage() {
            return image;
        }
        public void setImage(String image) {
            this.image = image;
        }
        public int getAvg_score() {
            return avg_score;
        }
        public void setAvg_score(int avg_score) {
            this.avg_score = avg_score;
        }

    }

    public static class AppCharacters implements Serializable
    {
        private String name;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
