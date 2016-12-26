package com.vrseen.vrstore.model.channel;

import android.content.Context;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jiangs on 16/5/13.
 */
public class ChannelData implements JsonConvertable<ChannelData> {
    private List<Channel> data;

    @Override
    public ChannelData fromJson(Context context, JSONObject json) throws JSONException {
        return new Gson().fromJson(json.toString(), ChannelData.class);
    }

    public List<Channel> getData() {
        return data;
    }

    public void setData(List<Channel> data) {
        this.data = data;
    }
}
