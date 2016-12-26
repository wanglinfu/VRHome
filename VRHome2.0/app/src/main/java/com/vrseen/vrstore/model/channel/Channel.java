package com.vrseen.vrstore.model.channel;

/**
 * Created by jiangs on 16/5/13.
 */
public class Channel {
    private int id;// "id": 11,
    private String title;//"title": "影视在线",
    private String icon;//"icon":"http://beta.image.api.vrseen.net/img/2016/05/11/20160511573310ece71b5.jpg",
    private String type;//        "type":"1",
    private int style;//        "style":"",
    private String channel;//       "channel":"video",
    private int sort;
    private String link;


    public String getChannel() {
        return channel;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
