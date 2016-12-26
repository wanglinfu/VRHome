package com.vrseen.vrstore.model.film;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jiangs on 16/5/13.
 */
public class FilmCateroryData implements JsonConvertable<FilmCateroryData> {
    private List<Category> data;

    @Override
    public FilmCateroryData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), FilmCateroryData.class);
    }

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }

    public static class Category implements Serializable {

        private int id;// "id": 3065,
        private String genre_name; // "genre_name": "3D",
        private String genre_tag; //
        private int sort;
        private String icon;
        private String icon_down;
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGenre_name() {
            return genre_name;
        }

        public void setGenre_name(String genre_name) {
            this.genre_name = genre_name;
        }

        public String getGenre_tag() {
            return genre_tag;
        }

        public void setGenre_tag(String genre_tag) {
            this.genre_tag = genre_tag;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIcon_down() {
            return icon_down;
        }

        public void setIcon_down(String icon_down) {
            this.icon_down = icon_down;
        }
    }
}
