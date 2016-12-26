package com.vrseen.vrstore.model.panorama;

import java.io.Serializable;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景全部分类
 * 创建人：郝晓辉
 * 创建时间：2016/6/5 17:20
 * 修改人：郝晓辉
 * 修改时间：2016/6/5 17:20
 * 修改备注：
 */
public class PanoramaAllTypeData implements Serializable {

    private String api_tag;
    private String title;

    public PanoramaAllTypeData(String api_tag, String title) {
        this.api_tag = api_tag;
        this.title = title;
    }

    public PanoramaAllTypeData() {
    }

    public String getApi_tag() {
        return api_tag;
    }

    public void setApi_tag(String api_tag) {
        this.api_tag = api_tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
