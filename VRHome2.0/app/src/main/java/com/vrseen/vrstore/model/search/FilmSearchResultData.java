package com.vrseen.vrstore.model.search;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 项目名称：VRHome2.0
 * 类描述：电影搜索结果
 * 创建人：郝晓辉
 * 创建时间：2016/6/11 11:58
 * 修改人：郝晓辉
 * 修改时间：2016/6/11 11:58
 * 修改备注：
 */
public class FilmSearchResultData implements JsonConvertable<FilmSearchResultData>{

    private List<FilmSearchResult> data;

    public List<FilmSearchResult> getData() {
        return data;
    }

    public void setData(List<FilmSearchResult> data) {
        this.data = data;
    }

    public static class FilmSearchResult{

        private int id;
        private String title;
        private String subtitle;
        private String year;
        private String actors;
        private String plot;
        private String episode;
        private String fee;
        private String image_url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getActors() {
            return actors;
        }

        public void setActors(String actors) {
            this.actors = actors;
        }

        public String getPlot() {
            return plot;
        }

        public void setPlot(String plot) {
            this.plot = plot;
        }

        public String getEpisode() {
            return episode;
        }

        public void setEpisode(String episode) {
            this.episode = episode;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
    }



    @Override
    public FilmSearchResultData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), FilmSearchResultData.class);
    }
}
