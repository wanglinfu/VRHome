package com.vrseen.vrstore.model.panorama;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * 项目名称：VRHome2.0
 * 类描述：全景资源类型
 * 创建人：郝晓辉
 * 创建时间：2016/6/1 9:29
 * 修改人：郝晓辉
 * 修改时间：2016/6/1 9:29
 * 修改备注：
 */
public class PanoramaCategoryData implements JsonConvertable<PanoramaCategoryData> {

    private List<Category> data;

    @Override
    public PanoramaCategoryData fromJson(Context context, JSONObject json) throws JSONException {

//        JSONObject jsonObject=json.getJSONObject("data");
//        Iterator iterator=jsonObject.keys();
//        JSONArray jsonArray=new JSONArray();
//
//        while (iterator.hasNext()){
//            String key=(String)iterator.next();
//            JSONObject jsonObject1=jsonObject.getJSONObject(key);
//            jsonArray.put(jsonObject1);
//        }
//
//        JSONObject data=new JSONObject();
//        data.put("data",jsonArray);

        return new Gson().fromJson(json.toString(), PanoramaCategoryData.class);
    }

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }

    public static class Category implements Serializable {

        public Category() {
        }

        public Category(int id, String cate) {
            this.id = id;
            this.cate = cate;
        }

        private int id;// 类型id
        private String cate; // 类型名
        private String allId;

        public String getAllId() {
            return allId;
        }

        public void setAllId(String allId) {
            this.allId = allId;
        }

        public int getId() {

            return id;
        }

        public void setId(int id) {

            this.id = id;
        }

        public String getCate() {
            return cate;
        }

        public void setCate(String cate) {
            this.cate = cate;
        }
    }
}
