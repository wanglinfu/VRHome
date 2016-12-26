package com.vrseen.vrstore.model.film;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jiangs on 16/5/15.
 */
public class FilmListData implements JsonConvertable<FilmListData> {
    private int total;//"total": 50,
    private String per_page;//"per_page":"5",
    private int current_page;//"current_page":1,
    private int last_page;//     "last_page":10,
    private String next_page_url;//      "next_page_url":"http://beta.vrseenhome.api.vrseen.net/api/video/find?page=2",
    private String prev_page_url;//   "prev_page_url":null,
    private int from;//      "from":1,
    private int to;//      "to":5,
    private List<BaseFilm> data;

    @Override
    public FilmListData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), FilmListData.class);
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getPer_page() {
        return per_page;
    }

    public void setPer_page(String per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        this.next_page_url = next_page_url;
    }

    public String getPrev_page_url() {
        return prev_page_url;
    }

    public void setPrev_page_url(String prev_page_url) {
        this.prev_page_url = prev_page_url;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public List<BaseFilm> getData() {
        return data;
    }

    public void setData(List<BaseFilm> data) {
        this.data = data;
    }

    public static class BaseFilm {
        private int id;// "id": 133877,
        private String title;//       "title": "3D少年斯派维的奇异旅行",
        private String subtitle;//     "subtitle": "天使爱美丽导演打造科学天才斯派维的奇旅",
        private String episode;//       "episode": "",
        private int fee;//     "fee": 0,
        private String image_url;//     "image_url": "http://beta.image.api.vrseen.net/img/2016/4/3/613/613/20160403155337868650c5e70.jpg",
        private int tag;//      "tag": 0

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

        public String getEpisode() {
            return episode;
        }

        public void setEpisode(String episode) {
            this.episode = episode;
        }

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }
    }
}
