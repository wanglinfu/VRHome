package com.vrseen.vrstore.model.film;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * 影视详情
 * Created by mll on 2016/5/23.
 */
public class FilmDetailData implements JsonConvertable<FilmDetailData> {

    public FilmDetail getData() {
        return data;
    }

    public void setData(FilmDetail data) {
        this.data = data;
    }

    private FilmDetail data;

    public List<FilmDetail> dataList;

    public List<FilmDetail> getDataList() {
        return dataList;
    }

    public void setDataList(List<FilmDetail> dataList) {
        this.dataList = dataList;
    }

    @Override
    public FilmDetailData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(),FilmDetailData.class);
    }

    public static class FilmDetail implements Serializable
    {
        private int id;             //视频ID
        private String title;       //标题
        private String subtitle;     //副标题
        private String screen;      //3D分屏情况，左右，上下,为空是2D.
        private String panorama;    //是否为全景
        private String year;        //年份
        private String released;    //发布时间
        private String writer;      //作者
        private String plot_keyword;//剧情关键字
        private String language;     //语言
        private String country;     //国家
        private int episode_count ; //共几集（电视剧会大于0）
        private String episode ;    //第几集（电视剧会显示：更新
        private String director;    //导演
        private String actors;      //演员
        private String plot;        //剧情
        private int  runtime;     //总时长（秒）
        private int  is_collected;    //1为已被当前用户收藏,0为未被收藏
        private int  fee;           //付费(1为需付费)
        private int refer_id;          //若请求的ID是电视剧某集的ID,则返回此集的ID
        private int  plays;         //播放次数（依靠播放接口进行累加，不会减少）
        private GenresData genres;
        private String recommend_image;
        private List<String> images ;
        private List<EpisodesData> episodes ;

        public GenresData getGenres() {
            return genres;
        }

        public void setGenres(GenresData genres) {
            this.genres = genres;
        }

        public List<EpisodesData> getEpisodes() {
            return episodes;
        }

        public void setEpisodes(List<EpisodesData> episodes) {
            this.episodes = episodes;
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

        public String getReleased() {
            return released;
        }

        public void setReleased(String released) {
            this.released = released;
        }

        public String getWriter() {
            return writer;
        }

        public void setWriter(String writer) {
            this.writer = writer;
        }

        public String getPlot_keyword() {
            return plot_keyword;
        }

        public void setPlot_keyword(String plot_keyword) {
            this.plot_keyword = plot_keyword;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public int getEpisode_count() {
            return episode_count;
        }

        public void setEpisode_count(int episode_count) {
            this.episode_count = episode_count;
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
        public int getRefer_id() {
            return refer_id;
        }
        public void setRefer_id(int refer_id) {
            this.refer_id = refer_id;
        }
        public int getPlays() {
            return plays;
        }
        public void setPlays(int plays) {
            this.plays = plays;
        }
        public String getPanorama() {
            return panorama;
        }
        public void setPanorama(String panorama) {
            this.panorama = panorama;
        }
        public String getScreen() {
            return screen;
        }
        public void setScreen(String screen) {
            this.screen = screen;
        }
        public int getIs_collected() {
            return is_collected;
        }
        public void setIs_collected(int is_collected) {
            this.is_collected = is_collected;
        }
        public String getRecommend_image() {
            return recommend_image;
        }
        public void setRecommend_image(String recommend_image) {
            this.recommend_image = recommend_image;
        }

        public String getActors() {
            return actors;
        }
        public void setActors(String actors) {
            this.actors = actors;
        }
        public String getDirector() {
            return director;
        }
        public void setDirector(String director) {
            this.director = director;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public List<String> getImages() {
            return images;
        }
        public void setImages(List<String> images) {
            this.images = images;
        }
        public String getPlot() {
            return plot;
        }
        public void setPlot(String plot) {
            this.plot = plot;
        }
        public int getRuntime() {
            return runtime;
        }
        public void setRuntime(int runtime) {
            this.runtime = runtime;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

    }

    public static class GenresData implements Serializable{
        private String main;
        private String tag;
        private String area;
        private String age;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }

    public static class EpisodesData implements Serializable{
        private int id;
        private String episode;
        private int fee;
        private int review;
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public int getReview() {
            return review;
        }

        public void setReview(int review) {
            this.review = review;
        }
    }

    public static class PlayProgressData implements Serializable{
        private int id;             //电影id
        private int proValue = 0;   //进度
        private int episodeID = 1;  //第几集

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProValue() {
            return proValue;
        }

        public void setProValue(int proValue) {
            this.proValue = proValue;
        }

        public int getEpisodeID() {
            return episodeID;
        }

        public void setEpisodeID(int episodeID) {
            this.episodeID = episodeID;
        }
    }

    public static class FilmChannelAsset implements Serializable{
        private String id;          //记录ID
        private String channel_url; //播放地址
        private String resource_id; //华数资源ID，（奇怪的结构，本身是object，却传回来一个String）
        private String quality_name; //清晰度

        public String getChannel_url() {
            return channel_url;
        }

        public String getResource_id() {
            return resource_id;
        }

        public void setChannel_url(String channel_url) {
            this.channel_url = channel_url;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }

        public String getQuality_name() {
            return quality_name;
        }

        public void setQuality_name(String quality_name) {
            this.quality_name = quality_name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    //根据channelId获取到详细信息
    public static class FilmAsset implements Serializable{
        private String assetId;
        private String catId;
        private int episode;
        public String getAssetId() {
            return assetId;
        }
        public void setAssetId(String assetId) {
            this.assetId = assetId;
        }
        public String getCatId() {
            return catId;
        }
        public void setCatId(String catId) {
            this.catId = catId;
        }
        public int getEpisode() {
            return episode;
        }
        public void setEpisode(int episode) {
            this.episode = episode;
        }
    }
}
