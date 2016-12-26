package com.vrseen.vrstore.model.Home;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jiangs on 16/5/4.
 * 首页数据模型
 */
public class HomeData implements JsonConvertable<HomeData> {
    private List<GroupData> data;

    @Override
    public HomeData fromJson(Context context, JSONObject json) throws JSONException {
        Gson gson = new Gson();

        return gson.fromJson(json.toString(), HomeData.class);
    }

    public List<GroupData> getData() {
        return data;
    }

    public void setData(List<GroupData> data) {
        this.data = data;
    }

    public static class GroupData {
        private int id;
        private int type;
        private String title;
        private int style;
        private List<Item> items;
        private List<String> tempItems;

        public List<String> getTempItems() {
            return tempItems;
        }

        public void setTempItems(List<String> tempItems) {
            this.tempItems = tempItems;
        }

        public List<Item> getItems() {
            return items;
        }

        public int getStyle() {
            return style - 1;
        }

        public void setStyle(int style) {
            this.style = style;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

//        public List<Item> getItems() {
//            return items;
//        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public static class Item {
            private int id;
            private int type;//"type": 1,
            private String title;//       "title": "妻子的秘密_14",
            private String subtitle;//     "subtitle": "“高富帅”与“白富美”的旷世奇恋",
            private String pic;//     "pic": "http://120.26.217.87:2182/yingshi_moren.png",
            private String tag;//     "tag": "",
            private int content_type;

            public int getContent_type() {
                return content_type;
            }

            public void setContent_type(int content_type) {
                this.content_type = content_type;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            private String state;// 影视的剧集状态，其他为空
            private int views;

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
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

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public int getViews() {
                return views;
            }

            public void setViews(int views) {
                this.views = views;
            }
        }
    }

}
