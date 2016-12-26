package com.vrseen.vrstore.model.user;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.vrseen.vrstore.http.JsonConvertable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mll on 2016/5/4.
 */
public class UserInfo implements JsonConvertable<UserInfo>{

    private String name = "";
    private int id = 0;
    private String avatar ="";
    private String openid_qq;
    private String openid_weibo;
    private String openid_weixin;
    private String mobile;
    private String email;
    private int is_vip;
    private String vip_end_date = null;//VIP有效期限至
    private Bitmap _avatarBitmap = null;//中兴用户头像
    private String extType = null;//中兴帐号第三方登录类型


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOpenid_qq() {
        return openid_qq;
    }

    public void setOpenid_qq(String openid_qq) {
        this.openid_qq = openid_qq;
    }

    public String getOpenid_weibo() {
        return openid_weibo;
    }

    public void setOpenid_weibo(String openid_weibo) {
        this.openid_weibo = openid_weibo;
    }

    public String getOpenid_weixin() {
        return openid_weixin;
    }

    public void setOpenid_weixin(String openid_weixin) {
        this.openid_weixin = openid_weixin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    @Override
    public UserInfo fromJson(Context context, JSONObject json) throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject = json.getJSONObject("data");

        return  gson.fromJson(jsonObject.toString(),UserInfo.class);
    }

    public Bitmap get_avatarBitmap() {
        return _avatarBitmap;
    }

    public void set_avatarBitmap(Bitmap _avatarBitmap) {
        this._avatarBitmap = _avatarBitmap;
    }

    public String getVip_end_date() {
        return vip_end_date;
    }

    public void setVip_end_date(String vip_end_date) {
        this.vip_end_date = vip_end_date;
    }

    public String getExtType(){
        return extType;
    }

    public void setExtType(String value){
        extType = value;
    }
}
