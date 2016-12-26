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
 * 类描述：全景专辑详情
 * 创建人：郝晓辉
 * 创建时间：
 * 修改人：郝晓辉
 * 修改时间：
 * 修改备注：
 */
public class PanoramaCollectionDetailData implements JsonConvertable<PanoramaCollectionDetailData>{

    private PanoramaCollectionDetail data;

    public PanoramaCollectionDetail getData() {
        return data;
    }

    public void setData(PanoramaCollectionDetail data) {
        this.data = data;
    }

    @Override
    public PanoramaCollectionDetailData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), PanoramaCollectionDetailData.class);
    }

    public class PanoramaCollectionDetail{
        public String getShootinglocation() {
            return shootinglocation;
        }

        public void setShootinglocation(String shootinglocation) {
            this.shootinglocation = shootinglocation;
        }

        public String getShootingdate() {
            return shootingdate;
        }

        public void setShootingdate(String shootingdate) {
            this.shootingdate = shootingdate;
        }

        private int id;//记录ID
        private int user_id;//用户ID
        private String title;//专辑标题
        private String description;//专辑描述
        private String thumbnail;//全景封面
        private String meta;//全景维度
        private String type;//类型
        private String filesize;//大小
        private String shootinglocation;//位置
        private String shootingdate;//时间
        private List<PanoramaListData.PanoramaList> posts;

        public List<PanoramaListData.PanoramaList> getPosts() {
            return posts;
        }

        public void setPosts(List<PanoramaListData.PanoramaList> posts) {
            this.posts = posts;
        }

        public String getMeta() {
            return meta;
        }

        public void setMeta(String meta) {
            this.meta = meta;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFilesize() {
            return filesize;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
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

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

    }


}
