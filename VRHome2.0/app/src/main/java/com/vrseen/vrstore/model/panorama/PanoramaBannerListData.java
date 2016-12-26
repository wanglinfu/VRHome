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
 * 类描述：全景广告栏
 * 创建人：郝晓辉
 * 创建时间：2016/6/1 13:42
 * 修改人：郝晓辉
 * 修改时间：2016/6/1 13:42
 * 修改备注：
 */
public class PanoramaBannerListData implements JsonConvertable<PanoramaBannerListData> {

    private List<PanoramaBanner> data;

    public List<PanoramaBanner> getData() {
        return data;
    }

    public void setData(List<PanoramaBanner> data) {
        this.data = data;
    }

    @Override
    public PanoramaBannerListData fromJson(Context context, JSONObject json) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",json.getJSONArray("heads"));
        return new Gson().fromJson(jsonObject.toString(), PanoramaBannerListData.class);
    }

    public static class PanoramaBanner implements Serializable {

        private int id;
        private String image;
        private String title;
        private String description;
        private String group;
        private PanoramaResource resource;
        private String type;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public PanoramaResource getResource() {
            return resource;
        }

        public void setResource(PanoramaResource resource) {
            this.resource = resource;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static class PanoramaResource implements Serializable {
            private int id;
            private int user_id;
            private int cate_id;
            private String name;
            private String note;
            private String description;
            private String tags;
            private String type;
            private String thumbnail;

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

        }

    }


}
