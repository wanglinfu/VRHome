package com.vrseen.vrstore.model.Home;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jiangs on 16/5/13.
 */
public class RecommendData implements JsonConvertable<RecommendData> {

    private List<Recommend> data;

    @Override
    public RecommendData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), RecommendData.class);
    }

    public List<Recommend> getData() {
        return data;
    }

    public void setData(List<Recommend> data) {
        this.data = data;
    }

    public static class Recommend {
        private int id;
        private int video_id;
        private int sort;
        private String group;
        private Video video;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getVideo_id() {
            return video_id;
        }

        public void setVideo_id(int video_id) {
            this.video_id = video_id;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public Video getVideo() {
            return video;
        }

        public void setVideo(Video video) {
            this.video = video;
        }

        public static class Video {
            private int id;// "id": 134088,
            private String title;//       "title": "《魔兽》吴彦祖版古尔丹正脸首次曝光",
            private String subtitle;//     "subtitle": "《魔兽》吴彦祖版古尔丹正脸首次曝光",
            private int episode_count;//      "episode_count": 1,
            private int fee;//        "fee": 0,
            private String image_url;//       "image_url": "http://beta.image.api.vrseen.net/img/2016/4/8/715/715/201604081018109267f89631a.jpg",
            private int current_count;//   "current_count": 1

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
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

            public String getSubtitle() {
                return subtitle;
            }

            public void setSubtitle(String subtitle) {
                this.subtitle = subtitle;
            }

            public int getEpisode_count() {
                return episode_count;
            }

            public void setEpisode_count(int episode_count) {
                this.episode_count = episode_count;
            }

            public int getFee() {
                return fee;
            }

            public void setFee(int fee) {
                this.fee = fee;
            }

            public int getCurrent_count() {
                return current_count;
            }

            public void setCurrent_count(int current_count) {
                this.current_count = current_count;
            }
        }
    }
}
