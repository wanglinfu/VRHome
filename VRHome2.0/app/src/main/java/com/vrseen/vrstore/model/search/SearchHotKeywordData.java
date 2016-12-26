package com.vrseen.vrstore.model.search;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 项目名称：VRHome2.0
 * 类描述：搜索关键字
 * 创建人：郝晓辉
 * 创建时间：2016/6/10 13:20
 * 修改人：郝晓辉
 * 修改时间：2016/6/10 13:20
 * 修改备注：
 */
public class SearchHotKeywordData implements JsonConvertable<SearchHotKeywordData>{

    public List<SearchHotKeyword> getData() {
        return data;
    }

    public void setData(List<SearchHotKeyword> data) {
        this.data = data;
    }

    private List<SearchHotKeyword> data;



    @Override
    public SearchHotKeywordData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), SearchHotKeywordData.class);
    }

    public static class SearchHotKeyword{
        private int id;
        private String search_key;
        private String search_count;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSearch_key() {
            return search_key;
        }

        public void setSearch_key(String search_key) {
            this.search_key = search_key;
        }

        public String getSearch_count() {
            return search_count;
        }

        public void setSearch_count(String search_count) {
            this.search_count = search_count;
        }
    }

}
