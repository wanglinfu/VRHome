package com.vrseen.vrstore.model.app;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mll on 2016/5/17.
 */
public class AppCategoryData implements JsonConvertable<AppCategoryData> {

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }

    private List<Category> data;

    @Override
    public AppCategoryData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(),AppCategoryData.class);
    }

    public static class Category implements Serializable {

        public Category(int id, String name, int latest) {
            this.id = id;
            this.name = name;
            this.latest = latest;
        }

        public Category() {
        }

        private int id;// "id": 3065,
        private String name;

        public int getLatest() {
            return latest;
        }

        public void setLatest(int latest) {
            this.latest = latest;
        }

        private int latest;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
