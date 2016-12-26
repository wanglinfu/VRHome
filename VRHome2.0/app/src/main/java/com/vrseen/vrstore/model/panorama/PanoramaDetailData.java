package com.vrseen.vrstore.model.panorama;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景详情
 * 创建人：郝晓辉
 * 创建时间：2016/6/3 14:35
 * 修改人：郝晓辉
 * 修改时间：2016/6/3 14:35
 * 修改备注：
 */
public class PanoramaDetailData implements JsonConvertable<PanoramaDetailData>{

    public PanoramaDetail getData() {
        return data;
    }

    public void setData(PanoramaDetail data) {
        this.data = data;
    }

    private PanoramaDetail data;

    public static class PanoramaDetail{

        private int id;//记录ID
        private int user_id;//用户ID
        private int cate_id;//分类ID
        private String name;//全景名
        private String note;//全景标注
        private String description;//全景描述
        private String tags;//全景标签
        private String type;//全景类型
        private String thumbnail;//全景封面
        private String avg_score;//平均分
        private String shootingdate;//拍摄时间
        private String shootinglocation;//拍摄地点
        private String producer;//厂家
        private String filesize;//文件大小
        private String fileformat;//文件扩展名
        private String position;//位置
        private String resolution;//分辨率
        private String previewing;//缩略图
        private String storagepath;//存储路径
        private String views;//浏览次数
        private String downloads;//下载数
        private String likes;//喜欢数
        private String shares;//共享数
        private String sort;//排序
        private String created_at;//创建时间
        private String updated_at;//修改时间
        private String score;//总评分
        private String score_count;//评分次数
        private String city_id;//城市ID
        private String season;//季节
        private String weather;//天气
        private String gps;//gps位置
        private String vodlist;//不同的播放源
        private String meta;//播放参数
        private String favs;//收藏数
        private String comments;//评论数
        private String status;//状态

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

        public String getAvg_score() {
            return avg_score;
        }

        public void setAvg_score(String avg_score) {
            this.avg_score = avg_score;
        }

        public String getShootingdate() {
            return shootingdate;
        }

        public void setShootingdate(String shootingdate) {
            this.shootingdate = shootingdate;
        }

        public String getShootinglocation() {
            return shootinglocation;
        }

        public void setShootinglocation(String shootinglocation) {
            this.shootinglocation = shootinglocation;
        }

        public String getProducer() {
            return producer;
        }

        public void setProducer(String producer) {
            this.producer = producer;
        }

        public String getFilesize() {
            return filesize;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getFileformat() {
            return fileformat;
        }

        public void setFileformat(String fileformat) {
            this.fileformat = fileformat;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }

        public String getPreviewing() {
            return previewing;
        }

        public void setPreviewing(String previewing) {
            this.previewing = previewing;
        }

        public String getStoragepath() {
            return storagepath;
        }

        public void setStoragepath(String storagepath) {
            this.storagepath = storagepath;
        }

        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }

        public String getDownloads() {
            return downloads;
        }

        public void setDownloads(String downloads) {
            this.downloads = downloads;
        }

        public String getLikes() {
            return likes;
        }

        public void setLikes(String likes) {
            this.likes = likes;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getShares() {
            return shares;
        }

        public void setShares(String shares) {
            this.shares = shares;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getScore_count() {
            return score_count;
        }

        public void setScore_count(String score_count) {
            this.score_count = score_count;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getGps() {
            return gps;
        }

        public void setGps(String gps) {
            this.gps = gps;
        }

        public String getVodlist() {
            return vodlist;
        }

        public void setVodlist(String vodlist) {
            this.vodlist = vodlist;
        }

        public String getMeta() {
            return meta;
        }

        public void setMeta(String meta) {
            this.meta = meta;
        }

        public String getFavs() {
            return favs;
        }

        public void setFavs(String favs) {
            this.favs = favs;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

    private List<PanoramaDetail> dataList;

    public List<PanoramaDetail> getDataList() {
        return dataList;
    }

    public void setDataList(List<PanoramaDetail> dataList) {
        this.dataList = dataList;
    }

    @Override
    public PanoramaDetailData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), PanoramaDetailData.class);
    }
}
