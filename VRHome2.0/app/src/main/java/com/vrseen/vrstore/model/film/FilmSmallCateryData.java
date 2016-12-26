package com.vrseen.vrstore.model.film;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 对应各种类型影视的小分类
 * Created by mll on 2016/6/2.
 */
public class FilmSmallCateryData implements JsonConvertable<FilmSmallCateryData> {

    public List<FilmCateroryData.Category> getData() {
        return data;
    }

    public void setData(List<FilmCateroryData.Category> data) {
        this.data = data;
    }

    private List<FilmCateroryData.Category> data;

    @Override
    public FilmSmallCateryData fromJson(Context context, JSONObject json) throws JSONException {

        return new Gson().fromJson(json.toString(),FilmSmallCateryData.class);
    }
}
