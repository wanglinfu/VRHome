package com.vrseen.vrstore.model.film;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mll on 2016/5/24.
 */
public class FilmRelateData implements JsonConvertable<FilmRelateData> {

    public ArrayList<FilmListData.BaseFilm> getData() {
        return data;
    }

    public void setData(ArrayList<FilmListData.BaseFilm> data) {
        this.data = data;
    }

    private ArrayList<FilmListData.BaseFilm> data;

    @Override
    public FilmRelateData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), FilmRelateData.class);
    }
}
