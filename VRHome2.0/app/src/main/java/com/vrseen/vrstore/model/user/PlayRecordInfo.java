package com.vrseen.vrstore.model.user;


import android.os.Parcel;
import android.os.Parcelable;

import com.vrseen.vrstore.activity.user.MyPlayRecordActivity;


public class PlayRecordInfo implements Parcelable {

    private String isDelete = MyPlayRecordActivity.STATE_NODELETE;

    private String created_at;
    private int id;
    private int resource_id;
    private int user_id;
    private String last_tick;
    private int last_channel_id;
    private int last_episode_id;
    private int type;
    private String last_episode_text;


    private String title;//       "title": "3D少年斯派维的奇异旅行",
    private String subtitle;//     "subtitle": "天使爱美丽导演打造科学天才斯派维的奇旅",
    private String episode;//       "episode": "",
    private int fee;//     "fee": 0,
    private String image_url;//     "image_url": "http://beta.image.api.vrseen.net/img/2016/4/3/613/613/20160403155337868650c5e70.jpg",
    private int tag;//      "tag": 0


    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResource_id() {
        return resource_id;
    }

    public void setResource_id(int resource_id) {
        this.resource_id = resource_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getLast_tick() {
        return last_tick;
    }

    public void setLast_tick(String last_tick) {
        this.last_tick = last_tick;
    }

    public int getLast_channel_id() {
        return last_channel_id;
    }

    public void setLast_channel_id(int last_channel_id) {
        this.last_channel_id = last_channel_id;
    }

    public int getLast_episode_id() {
        return last_episode_id;
    }

    public void setLast_episode_id(int last_episode_id) {
        this.last_episode_id = last_episode_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLast_episode_text() {
        return last_episode_text;
    }

    public void setLast_episode_text(String last_episode_text) {
        this.last_episode_text = last_episode_text;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(isDelete);
        dest.writeString(created_at);
        dest.writeString(last_tick);
        dest.writeString(last_episode_text);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(episode);
        dest.writeString(image_url);
        dest.writeInt(fee);
        dest.writeInt(tag);
        dest.writeInt(id);
        dest.writeInt(resource_id);
        dest.writeInt(user_id);
        dest.writeInt(last_channel_id);
        dest.writeInt(last_episode_id);
        dest.writeInt(type);
    }

    public static final Parcelable.Creator<PlayRecordInfo> CREATOR = new Creator<PlayRecordInfo>() {
        @Override
        public PlayRecordInfo createFromParcel(Parcel source) {
            PlayRecordInfo playRecordInfo = new PlayRecordInfo();
            playRecordInfo.isDelete = source.readString();
            playRecordInfo.created_at = source.readString();
            playRecordInfo.last_tick = source.readString();
            playRecordInfo.last_episode_text = source.readString();
            playRecordInfo.title = source.readString();
            playRecordInfo.subtitle = source.readString();
            playRecordInfo.episode = source.readString();
            playRecordInfo.image_url = source.readString();
            playRecordInfo.fee = source.readInt();
            playRecordInfo.tag = source.readInt();
            playRecordInfo.id = source.readInt();
            playRecordInfo.resource_id = source.readInt();
            playRecordInfo.user_id = source.readInt();
            playRecordInfo.last_channel_id = source.readInt();
            playRecordInfo.last_episode_id = source.readInt();
            playRecordInfo.type = source.readInt();
            return playRecordInfo;
        }

        @Override
        public PlayRecordInfo[] newArray(int size) {
            return new PlayRecordInfo[size];
        }
    };
}
