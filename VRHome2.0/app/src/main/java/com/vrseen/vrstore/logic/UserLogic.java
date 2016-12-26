package com.vrseen.vrstore.logic;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.fragment.user.MineFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.model.user.UserInfo;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import in.srain.cube.util.NetworkStatusManager;

/**
 * Created by mll on 2016/5/10.
 */
public class UserLogic {

    private boolean _isLogin = false;
    //角色数据
    private UserInfo _userInfo = null;
    private static UserLogic _instance;

    public static UserLogic getInstance()
    {
        if(_instance == null)
        {
            _instance = new UserLogic();
        }
        return _instance;
    }

    public boolean getLogin() {
        return _isLogin;
    }

    public void setLogin(boolean login) {
        _isLogin = login;
    }


    public boolean checkLoginSuc()
    {
        return _isLogin;
    }


    /**
     * 非wifi状态下载
     * 参数/返回
     * created at 2016/6/27
     */

    public boolean checkLoginSucAndHasMsg(Context context)
    {
        if(_isLogin == false)
        {
            ToastUtils.showShort(context, R.string.message_account_need_login);
        }
        return _isLogin;
    }

    public boolean isVIp()
    {
        if(_userInfo != null && _userInfo.getIs_vip() > 0)
        {
            return true;
        }

        return false;
    }

    public void setUserInfo(UserInfo userInfo)
    {
        _isLogin = userInfo != null ? true:false;
        _userInfo = userInfo;
    }

    public UserInfo getUserInfo()
    {
       return _userInfo;
    }

    public int getVip(){
        if(checkLoginSuc()) {
            if (_userInfo != null) {
                return _userInfo.getIs_vip();
            }
        }
        return 9;
    }

    public void cleanData()
    {
//        if(_userInfo != null)
//        {
//            _userInfo.setId(0);
//            _userInfo.setAvatar("");
//            _userInfo.setName("guest");
//            _userInfo.setEmail("");
//            _userInfo.setMobile("");
//            _userInfo.setOpenid_qq("");
//            _userInfo.setOpenid_weixin("");
//            _userInfo.setOpenid_weibo("");
//        }
       // _userInfo = null;
        setUserInfo(null);
    }

    public void initLogin(Context context)
    {
       boolean isThird = (boolean)SharedPreferencesUtils.getParam(context.getApplicationContext()
               , SPFConstant.KEY_IS_THIRD, false);
        if(isThird == true)
        {
            //是第三方
            String platName = (String) SharedPreferencesUtils.getParam(context.getApplicationContext()
                    ,SPFConstant.KEY_PLAT_NANE,"");
            if(StringUtils.isNotBlank(platName))
            {
                Platform plat = ShareSDK.getPlatform(platName);
                if(plat != null)
                {
                    loginByThird(context, plat);
                }
            }
        }
        else
        {
            String name = (String) SharedPreferencesUtils.getParam(context.getApplicationContext()
                    ,SPFConstant.KEY_USER_NAME,"");
            String pwd =(String) SharedPreferencesUtils.getParam(context.getApplicationContext()
                    ,SPFConstant.KEY_USER_PWD,"");
            if(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(pwd))
            {
                loginByPhone(context,name, pwd);
            }
        }
    }

    /**
     * 登出
     * @param  context
     * created at 2016/6/29
     */
    public void loginOut(Context context)
    {
        SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_USER_NAME, "");
        SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_USER_PWD, "");
        SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_IS_THIRD, false);
        SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_PLAT_NANE,"");
        cleanData();
        //结束账户的统计
        MobclickAgent.onProfileSignOff();
    }

    public void loginByThird(final Context context, final Platform platform) {
        //第三方登录
        UserRestClient.getInstance(context).loginByThird(platform.getName(), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                Response resp = new Response();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseInfo.result.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                resp.setData(jsonObject);

                MobclickAgent.onProfileSignIn(platform.getName(),platform.getDb().getUserId());
                CommonRestClient.getInstance(context).saveToken(resp);
                ToastUtils.showShort(context, R.string.mine_login_suc);
                //登录成功后保存数据
                //保存数据
                SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_IS_THIRD, true);
                SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_PLAT_NANE, platform.getName());
                //获取个人数据
                // 获取个人数据
                Log.d("onSuccess:", ""+resp.toString());
                UserRestClient.getInstance(context).getUserInfo(context, new AbstractRestClient.ResponseCallBack() {
                    @Override
                    public void onFailure(Response resp, Throwable e) {
                        ToastUtils.showShort(context, R.string.mine_get_personal_data_fail);
                        CommonUtils.showResponseMessage(context, resp, e, R.string.mine_get_personal_info_fail);
                    }

                    @Override
                    public void onSuccess(Response resp) {
//                        ToastUtils.showShort(context, R.string.mine_get_personal_data_success);
                        //获取个人信息
                        Log.d("onSuccess 个人:", ""+resp.toString());
                        UserLogic.getInstance().setUserInfo((UserInfo) resp.getModel());
                        Activity activity = (Activity) context;
                        if(activity instanceof MainActivity)
                        {
                            return;
                        }
                        activity.finish();
                    }
                });
            }

            @Override
            public void onFailure(HttpException e, String s) {
                ToastUtils.showShort(context, R.string.mine_login_fail);
                //CommonUtils.showResponseMessage(context, resp, e, R.string.mine_login_error);
                Log.d("onFailure:", ""+"登录失败");
            }

        });
    }

    public void loginByThird2(final Context context, final Platform platform) {
        //第三方登录
        UserRestClient.getInstance(context).loginByThird2(platform.getName(), new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                ToastUtils.showShort(context, R.string.mine_login_fail);
                CommonUtils.showResponseMessage(context, resp, e, R.string.mine_login_error);
                Log.d("onFailure:", ""+"登录失败");
            }

            @Override
            public void onSuccess(Response resp) {
                CommonRestClient.getInstance(context).saveToken(resp);
                ToastUtils.showShort(context, R.string.mine_login_suc);
                //登录成功后保存数据
                //保存数据
                SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_IS_THIRD, true);
                SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_PLAT_NANE, platform.getName());
                //获取个人数据
                // 获取个人数据
                Log.d("onSuccess:", ""+resp.toString());
                UserRestClient.getInstance(context).getUserInfo(context, new AbstractRestClient.ResponseCallBack() {
                    @Override
                    public void onFailure(Response resp, Throwable e) {
                        ToastUtils.showShort(context, R.string.mine_get_personal_data_fail);
                        CommonUtils.showResponseMessage(context, resp, e, R.string.mine_get_personal_info_fail);
                    }

                    @Override
                    public void onSuccess(Response resp) {
                        ToastUtils.showShort(context, R.string.mine_get_personal_data_success);
                        //获取个人信息
                        Log.d("onSuccess 个人:", ""+resp.toString());
                        UserLogic.getInstance().setUserInfo((UserInfo) resp.getModel());
                        Activity activity = (Activity) context;
                        if(activity instanceof MainActivity)
                        {
                            return;
                        }
                        activity.finish();
                    }
                });
            }
        });
    }

    public void loginByPhone(final Context context, final String phoneName, final String pwd)
    {
        //申请登陆
        UserRestClient.getInstance(context).loginByPhone(phoneName,pwd, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(context,resp,e,R.string.mine_login_error);
            }

            @Override
            public void onSuccess(Response resp) {
                CommonRestClient.getInstance(context).saveToken(resp);
                ToastUtils.showShort(context,R.string.mine_login_suc);

                //登录成功后保存数据
                //保存数据
                SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_USER_NAME, phoneName);
                SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_USER_PWD, pwd);
                SharedPreferencesUtils.setParam(context.getApplicationContext(), SPFConstant.KEY_IS_THIRD, false);

                //统计账户
                MobclickAgent.onProfileSignIn(phoneName);
                // 获取个人数据
                UserRestClient.getInstance(context).getUserInfo(context, new AbstractRestClient.ResponseCallBack() {
                    @Override
                    public void onFailure(Response resp, Throwable e) {
                        CommonUtils.showResponseMessage(context,resp,e,R.string.mine_get_personal_info_fail);
                    }

                    @Override
                    public void onSuccess(Response resp) {
                        //获取个人信息
                        UserLogic.getInstance().setUserInfo((UserInfo)resp.getModel());
                        Activity activity = (Activity)context;
                        if(activity instanceof MainActivity)
                        {
                            return;
                        }
                        activity.finish();
                    }
                });
            }
        });
    }
}
