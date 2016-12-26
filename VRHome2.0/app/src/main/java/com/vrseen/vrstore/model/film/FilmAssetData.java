package com.vrseen.vrstore.model.film;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 获取单个视频的数据
 * Created by mll on 2016/5/27.
 */
public class FilmAssetData implements JsonConvertable<FilmAssetData> {

    private List<FilmDetailData.FilmChannelAsset> data;

    public List<FilmDetailData.FilmChannelAsset> getData() {
        return data;
    }

    public void setData(List<FilmDetailData.FilmChannelAsset> data) {
        this.data = data;
    }

    @Override
    public FilmAssetData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), FilmAssetData.class);
    }
}
