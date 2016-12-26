package com.vrseen.vrstore.model.panorama;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景列表页
 * 创建人：郝晓辉
 * 创建时间：2016/6/2 9:47
 * 修改人：郝晓辉
 * 修改时间：2016/6/2 9:47
 * 修改备注：
 */
public class PanoramaListData implements JsonConvertable<PanoramaListData> {

    private List<PanoramaList> data;

    public List<PanoramaList> getData() {
        return data;
    }

    public void setData(List<PanoramaList> data) {
        this.data = data;
    }

    @Override
    public PanoramaListData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), PanoramaListData.class);
    }

    public class PanoramaList implements Serializable{

        private int id;//记录ID
        private int user_id;//用户ID
        private int cate_id;//分类ID
        private String name;//全景名
        private String note;//全景标注
        private String description;//全景描述
        private String tags;//全景标签
        private int type;//全景类型
        private String thumbnail;//全景封面
        private int views;//浏览次数
        private String avg_score;//平均分
        private String storagepath;//资源地址
        private String resource_id;

        public String getStoragepath() {
            return storagepath;
        }

        public void setStoragepath(String storagepath) {
            this.storagepath = storagepath;
        }

        public String getResource_id() {
            return resource_id;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }

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

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public String getAvg_score() {
            return avg_score;
        }

        public void setAvg_score(String avg_score) {
            this.avg_score = avg_score;
        }

    }

}
