package com.vrseen.vrstore.http;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 支持从json转为对象
 * Created by jiangs on 16/4/28.
 */
public interface JsonConvertable<T extends JsonConvertable> {
    T fromJson(Context context, JSONObject json) throws JSONException;
}
