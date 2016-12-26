package com.vrseen.vrstore.model.app;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * app广告
 * Created by mll on 2016/5/14.
 */
public class AppBannerData implements JsonConvertable<AppBannerData> {

    private List<AppBanner> data;

    public List<AppBanner> getData() {
        return data;
    }

    public void setData(List<AppBanner> data) {
        this.data = data;
    }

    @Override
    public AppBannerData fromJson(Context context, JSONObject json) throws JSONException {
        Gson gson = new Gson();
        JSONArray jsonObject = json.getJSONArray("heads");

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("data",jsonObject);

        AppBannerData appBannerData = gson.fromJson(jsonObject1.toString(),AppBannerData.class);

        return appBannerData;
    }

    public static class AppBanner implements Serializable
    {
        private String id;
        private String title;
        private String description;
        private String image;
        private int app_description_id;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getApp_description_id() {
            return app_description_id;
        }

        public void setApp_description_id(int app_description_id) {
            this.app_description_id = app_description_id;
        }


    }

}
