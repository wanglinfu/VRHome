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
 * 类描述：全景专辑
 * 创建人：郝晓辉
 * 创建时间：2016/6/2 14:43
 * 修改人：郝晓辉
 * 修改时间：2016/6/2 14:43
 * 修改备注：
 */
public class PanoramaCollectionData implements JsonConvertable<PanoramaCollectionData>{

    private List<PanoramaCollection> data;

    public List<PanoramaCollection> getData() {
        return data;
    }

    public void setData(List<PanoramaCollection> data) {
        this.data = data;
    }

    @Override
    public PanoramaCollectionData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), PanoramaCollectionData.class);
    }

    public class PanoramaCollection{

        private int id;//记录ID
        private int user_id;//用户ID
        private String title;//专辑标题
        private String description;//专辑描述
        private String thumbnail;//全景封面

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
