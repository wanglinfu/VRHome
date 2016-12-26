package com.vrseen.vrstore.model.film;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * 对应各种类型影视的小分类
 * Created by mll on 2016/6/2.
 */
public class FilmFilterCateryData implements JsonConvertable<FilmFilterCateryData> {

    public List<FilmFilterCatery> getData() {
        return data;
    }

    public void setData(List<FilmFilterCatery> data) {
        this.data = data;
    }

    private List<FilmFilterCatery> data;

    @Override
    public FilmFilterCateryData fromJson(Context context, JSONObject json) throws JSONException {

        // FIXME: 2016/6/6  转换数据
        JSONObject jsonObject = json.getJSONObject("data");
        Iterator iterator = jsonObject.keys();
        JSONArray jsonArray = new JSONArray();

        while (iterator.hasNext())
        {
            String key = (String)iterator.next();
            JSONObject jsonObject1 = jsonObject.getJSONObject(key);
            jsonArray.put(jsonObject1);
        }

        JSONObject data = new JSONObject();
        data.put("data",jsonArray);

        return new Gson().fromJson(data.toString(),FilmFilterCateryData.class);
    }

    /**
     * 过滤的数据
     * 参数/返回
     * created at 2016/6/6
     */
    public static class FilmFilterCatery extends FilmCateroryData.Category {

        public List<FilmCateroryData.Category> getGenres() {
            return genres;
        }

        public void setGenres(List<FilmCateroryData.Category> genres) {
            this.genres = genres;
        }

        private List<FilmCateroryData.Category> genres;

    }
}
