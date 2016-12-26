package com.vrseen.vrstore.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mll on 2016/5/4.
 */
public class RegisterActivity extends BaseActivity {

    private final String TAG = "RegisterActivity";
    private final int STEP_INPUT_PHONE = 1;
    private final int STEP_INPUT_PASSWORD = 2;
    private final int STEP_INPUT_VALIDATE = 3;
    private int _step = STEP_INPUT_PHONE;

    private EditText _etPhone = null;
    private EditText _etCode = null;
    private EditText _etPwd = null;
    private Button _btnGetcode = null;
    private LinearLayout _ll_step_input_phone, _ll_step_input_password,_ll_step_input_validate;
    private CheckBox _checkbox;
    private int _leftTime;
    private String _strPhone;
    private String _code;
    private String _pwd;
    private Context _context;

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,RegisterActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    protected void onCreate(Bundle savedInstanceState) {

        setPageName("RegisterActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _context = this;
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        _ll_step_input_phone = (LinearLayout)findViewById(R.id.ll_step_input_phone);
        _ll_step_input_password = (LinearLayout)findViewById(R.id.ll_step_input_password);
        _ll_step_input_validate = (LinearLayout)findViewById(R.id.ll_step_input_validate);
        _etPhone = (EditText) findViewById(R.id.et_phone);
        _etCode = (EditText) findViewById(R.id.et_code);
        _btnGetcode = (Button) findViewById(R.id.btn_getcode);
        _checkbox = (CheckBox)findViewById(R.id.checkbox_agreement);
        _etPwd = (EditText)findViewById(R.id.et_pwd);

        findViewById(R.id.view_back).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_validate).setOnClickListener(this);
        findViewById(R.id.btn_config).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    Runnable countDownRunnable = new Runnable() {
        public void run() {
            _btnGetcode.setText(_leftTime + " s");
            _leftTime--;
            if (_leftTime == -1) {
                // 停止
                stopCountDown();
            } else {
                _handler.postDelayed(countDownRunnable, 1000);
            }
        }
    };

    /**
     * 停止重新发送验证码倒计时，显示按钮
     */
    private void stopCountDown() {
        _handler.removeCallbacks(countDownRunnable);
        _btnGetcode.setClickable(true);
        _btnGetcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_radius_blue_style));
        _btnGetcode.setText("重新获取");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.view_back:
                this.finish();
                if(_step == STEP_INPUT_PHONE)
                {
                    this.finish();
                }
                else if(_step == STEP_INPUT_PASSWORD)
                {
                    showStep(STEP_INPUT_VALIDATE);
                }
                else if(_step == STEP_INPUT_VALIDATE)
                {
                    showStep(STEP_INPUT_PHONE);
                }
                break;
            case R.id.btn_next:
                next();
                _step = STEP_INPUT_VALIDATE;
                break;
            case R.id.btn_validate:
                _code = _etCode.getText().toString();
                showStep(STEP_INPUT_PASSWORD);
                _step = STEP_INPUT_PASSWORD;
                break;
            case R.id.btn_config:
                //注册是否成功
                requestRegister();
                break;
            default: break;
        }
    }


    private void requestRegister()
    {
        _pwd = _etPwd.getText().toString();
        UserRestClient.getInstance(this).registerAcc(_strPhone,_pwd,_code,new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context,resp,e,R.string.mine_regist_fail);
            }

            @Override
            public void onSuccess(Response responseInfo) {
                if (responseInfo != null) {
                    ToastUtils.showShort(_context,R.string.mine_regist_success);
                }

                UserLogic.getInstance().loginByPhone(RegisterActivity.this,_strPhone,_pwd);
            }
        });
    }

    //下一步，进入输入验证码
    private void next()
    {
        String strPhone = _etPhone.getText().toString();
        if(!_checkbox.isChecked())
        {
            ToastUtils.showShort(this,R.string.message_account_checkbox);
            return;
        }

        if(StringUtils.isBlank(strPhone))
        {
            ToastUtils.showShort(this,R.string.message_account_null);
            return;
        }

        if(!StringUtils.isMobileNumber(strPhone))
        {
            ToastUtils.showShort(this,R.string.message_account_phone);
            return;
        }

        _strPhone = strPhone;
        mobileCheck();
    }

    private void mobileCheck()
    {
        UserRestClient.getInstance(_context).mobileCheck(_strPhone, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {

            }

            @Override
            public void onSuccess(Response resp) {

                JSONObject jsonObject = (JSONObject)resp.getData();
                try {
                    int status =  jsonObject.getInt("status");
                    if(status == 0)
                    {
                        //不存在的时候，才可以注册
                        requestCode();
                        showStep(STEP_INPUT_VALIDATE);
                    }
                    else
                    {
                        ToastUtils.showShort(_context,R.string.message_server_1001003);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void  requestCode()
    {
        _btnGetcode.setClickable(false);
        _btnGetcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_radius_gray_style));
        startCountDown();
        //获取验证码和倒计时
        UserRestClient.getInstance(this).requestGetCode(_etPhone.getText().toString(),UserRestClient.REGISTER_TYPE, new AbstractRestClient.ResponseCallBack() {

            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(_context,resp,e,R.string.message_account_getRegCode_error);
            }

            @Override
            public void onSuccess(Response resp) {
                ToastUtils.showShort(_context,R.string.message_account_getRegCode_suc);
            }
        });
    }



    private Handler _handler = new Handler();

    /**
     * 开始重新发送验证码倒计时
     */
    private void startCountDown() {
        _leftTime = 60;
        // 开始倒计时
        _handler.post(countDownRunnable);
    }

    private void showStep(int step) {
        View hide = null;
        View show = null;
        switch (step) {
            case STEP_INPUT_PASSWORD:
                hide = _ll_step_input_validate;
                show = _ll_step_input_password;
                break;
            case STEP_INPUT_PHONE:
                 hide = _ll_step_input_password;
                 show = _ll_step_input_phone;
                break;
            case STEP_INPUT_VALIDATE:
                hide = _ll_step_input_phone;
                show = _ll_step_input_validate;
                break;
        }
        TranslateAnimation hideAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        hideAnimation.setDuration(500);
        final View fhide = hide;
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fhide.setVisibility(View.GONE);
            }
        });
        TranslateAnimation showAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        showAnimation.setDuration(500);
        hide.startAnimation(hideAnimation);
        show.setVisibility(View.VISIBLE);
        show.startAnimation(showAnimation);
    }

}
