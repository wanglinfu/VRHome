package com.vrseen.vrstore.model.panorama;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景城市
 * 创建人：郝晓辉
 * 创建时间：2016/6/2 11:05
 * 修改人：郝晓辉
 * 修改时间：2016/6/2 11:05
 * 修改备注：
 */
public class PanoramaCityData {

    private String id;//城市id
    private String pinyin;//城市名称拼音
    private String name;//城市名称

    public PanoramaCityData(String name,String pinyin,String id){
        this.name=name;
        this.pinyin=pinyin;
        this.id=id;
    }

    public PanoramaCityData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
