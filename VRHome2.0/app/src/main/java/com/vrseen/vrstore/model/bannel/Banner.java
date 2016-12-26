package com.vrseen.vrstore.model.bannel;

/**
 * Created by jiangs on 16/5/6.
 */
public class Banner {
    private String imagurl;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 广告栏ID

     */
    private int id;

    private String type;//广告类型（全景、全景资源）

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagurl() {
        return imagurl;
    }

    public void setImagurl(String imagurl) {
        this.imagurl = imagurl;
    }
}
