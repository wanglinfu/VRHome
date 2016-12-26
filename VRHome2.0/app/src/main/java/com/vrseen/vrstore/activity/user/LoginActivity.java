package com.vrseen.vrstore.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ToastUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;


/**
 * Created by mll on 2016/5/4.
 */
public class LoginActivity extends BaseActivity {

    private EditText _etPhone = null;
    private EditText _etPwd = null;
    private Context _context = null;
    private String _strPhone = null;
    private String _pwd = null;

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,LoginActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);

    }

    protected void onCreate(Bundle savedInstanceState) {

        setPageName("LoginActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _context = this;
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        _etPhone = (EditText) findViewById(R.id.et_phone);
        String phone = (String) SharedPreferencesUtils.getParam(_context, "phone", "");
        _etPhone.setText(phone);
        _etPhone.setSelection(phone.length());
        _etPwd = (EditText)findViewById(R.id.et_pwd);

        findViewById(R.id.view_back).setOnClickListener(this);
        findViewById(R.id.img_qq).setOnClickListener(this);
        findViewById(R.id.img_weibo).setOnClickListener(this);
        findViewById(R.id.img_weixin).setOnClickListener(this);
        findViewById(R.id.textview_forget).setOnClickListener(this);
        findViewById(R.id.textview_register).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.view_back:
                this.finish();
                break;
            case R.id.img_qq:
                loginByThird(QQ.NAME);
                break;
            case R.id.img_weibo:
                loginByThird(SinaWeibo.NAME);
                break;
            case R.id.img_weixin:
                loginByThird(Wechat.NAME);
                break;
            case R.id.textview_forget:
                ForgetPwdActivity.actionStart(this);
                this.finish();
                break;
            case R.id.textview_register:
                RegisterActivity.actionStart(this);
                this.finish();
                break;
            case R.id.btn_login:
                //登陆
                loginByPhone();
                break;
            default: break;
        }
    }

    private void loginByPhone()
    {
        _strPhone = _etPhone.getText().toString().trim();
        _pwd = _etPwd.getText().toString().trim();

        if(StringUtils.isBlank(_strPhone) || StringUtils.isBlank(_pwd))
        {
            ToastUtils.showShort(_context,R.string.mine_login_null_error);
            return;
        }

        SharedPreferencesUtils.setParam(_context, "phone", _strPhone);

        UserLogic.getInstance().loginByPhone(this,_strPhone,_pwd);
    }

    public void loginByThird(final String platName)
    {
        final Platform plat = ShareSDK.getPlatform(platName);
        if(plat.isValid())
        {
            plat.removeAccount();
        }
        if(plat == null)
        {
            return;
        }

        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onError(Platform platform, int action, Throwable t) {
                if (action == Platform.ACTION_USER_INFOR) {
                    ToastUtils.showShort(_context,R.string.mine_authorization_fail);
                }
                t.printStackTrace();
            }

            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                if (action == Platform.ACTION_USER_INFOR || action == Platform.ACTION_AUTHORIZING ) {
                    //申请登录
                    UserLogic.getInstance().loginByThird(_context,plat);
                }
            }

            @Override
            public void onCancel(Platform platform, int action) {
                if (action == Platform.ACTION_USER_INFOR) {
                    ToastUtils.showShort(_context,R.string.mine_cancle_authorization);
                }
            }

        });

        plat.SSOSetting(false);
        plat.authorize();
        //plat.showUser(null);
    }
}
