package com.vrseen.vrstore.model.Home;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by mll on 2016/6/10.
 */
public class HomeAdData implements JsonConvertable<HomeAdData> {

    public List<HomeAd> getData() {
        return data;
    }

    public void setData(List<HomeAd> data) {
        this.data = data;
    }

    private List<HomeAd> data;


    @Override
    public HomeAdData fromJson(Context context, JSONObject json) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",json.getJSONArray("heads"));

        return new Gson().fromJson(jsonObject.toString(),HomeAdData.class);
    }

    public static class HomeAd
    {
        private int id;
        private String resource_id = "";
        private String image = "";
        private String title = "";
        private String description = "";
        private String type = "";

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getResource_id() {
            return resource_id;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
