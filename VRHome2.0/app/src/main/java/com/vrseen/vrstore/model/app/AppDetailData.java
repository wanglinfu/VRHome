package com.vrseen.vrstore.model.app;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mll on 2016/5/16.
 */
public class AppDetailData implements JsonConvertable<AppDetailData> {

    public AppDetail getData() {
        return data;
    }

    public void setData(AppDetail data) {
        this.data = data;
    }

    private AppDetail data;

    public String getQrtext() {
        return qrtext;
    }

    public void setQrtext(String qrtext) {
        this.qrtext = qrtext;
    }

    private String qrtext ="";


    @Override
    public AppDetailData fromJson(Context context, JSONObject json) throws JSONException {

        JSONObject jsonObject1 = json;

        return new Gson().fromJson(json.toString(),AppDetailData.class);
    }


    public static class AppDetail extends AppListData.App implements Serializable {
        public List<AppPlatform> getApp_platforms() {
            return app_platforms;
        }

        public void setApp_platforms(List<AppPlatform> app_platforms) {
            this.app_platforms = app_platforms;
        }

        public AppVersion getMax_version() {
            return max_version;
        }

        public void setMax_version(AppVersion max_version) {
            this.max_version = max_version;
        }

        private AppVersion max_version;
        private List<AppPlatform> app_platforms;

    }

    public static class AppPlatform implements Serializable{

        private int max_version_platform_id;
        private int app_description_id;

        public AppVersion getMax_version_platform() {
            return max_version_platform;
        }

        public void setMax_version_platform(AppVersion max_version_platform) {
            this.max_version_platform = max_version_platform;
        }

        private AppVersion max_version_platform;

        public int getApp_description_id() {
            return app_description_id;
        }

        public void setApp_description_id(int app_description_id) {
            this.app_description_id = app_description_id;
        }

        public int getMax_version_platform_id() {
            return max_version_platform_id;
        }

        public void setMax_version_platform_id(int max_version_platform_id) {
            this.max_version_platform_id = max_version_platform_id;
        }
    }

    public static class AppVersion implements  Serializable{

        private int id;
        private String summary;
        private String description;
        private int version_status;
        private String size;
        private String path;
        private String link;
        private List<AppversionBanner> app_images;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }


        public AppVersion getMax_version() {
            return max_version;
        }

        public void setMax_version(AppVersion max_version) {
            this.max_version = max_version;
        }

        private AppVersion max_version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        private String version;

        public List<AppversionBanner> getApp_images() {
            return app_images;
        }

        public void setApp_images(List<AppversionBanner> app_images) {
            this.app_images = app_images;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getVersion_status() {
            return version_status;
        }

        public void setVersion_status(int version_status) {
            this.version_status = version_status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class AppversionBanner implements  Serializable{

        private int app_version_id;
        private String image;

        public int getApp_version_id() {
            return app_version_id;
        }

        public void setApp_version_id(int app_version_id) {
            this.app_version_id = app_version_id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
