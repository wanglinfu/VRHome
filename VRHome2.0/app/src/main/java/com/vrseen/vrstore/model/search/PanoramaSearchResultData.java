package com.vrseen.vrstore.model.search;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景搜索结果
 * 创建人：郝晓辉
 * 创建时间：2016/6/11 12:03
 * 修改人：郝晓辉
 * 修改时间：2016/6/11 12:03
 * 修改备注：
 */
public class PanoramaSearchResultData implements JsonConvertable<PanoramaSearchResultData> {

    private List<PanoramaSearchResult> data;

    public List<PanoramaSearchResult> getData() {
        return data;
    }

    public void setData(List<PanoramaSearchResult> data) {
        this.data = data;
    }

    public static class PanoramaSearchResult {

        private int id;
        private int user_id;
        private int cate_id;
        private String name;
        private String note;
        private String description;
        private String tags;
        private String type;
        private String thumbnail;
        private String views;
        private String created_at;
        private String avg_score;
        private String featured;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getCate_id() {
            return cate_id;
        }

        public void setCate_id(int cate_id) {
            this.cate_id = cate_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String create_at) {
            this.created_at = create_at;
        }

        public String getAvg_score() {
            return avg_score;
        }

        public void setAvg_score(String avg_score) {
            this.avg_score = avg_score;
        }

        public String getFeatured() {
            return featured;
        }

        public void setFeatured(String featured) {
            this.featured = featured;
        }
    }

    @Override
    public PanoramaSearchResultData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), PanoramaSearchResultData.class);
    }
}
