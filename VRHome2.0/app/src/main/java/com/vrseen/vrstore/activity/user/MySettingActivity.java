package com.vrseen.vrstore.activity.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.DataCleanUtils;
import com.vrseen.vrstore.util.DialogHelpUtils;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.ToastUtils;
import com.vrseen.vrstore.util.UmengUtils;

/**
 * Created by mll on 2016/5/5.
 */
public class MySettingActivity extends BaseActivity {

    private TextView _textViewCacheSize = null;
    private TextView _textview_version = null;
    private Context _context;
    private ToggleButton _toggleButton = null;
    private Button _loginOutBtn;

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,MySettingActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setPageName("MySettingActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        _context = this;
        initView();
        caculateCacheSize();
    }

    @Override
    protected void initView() {
        super.initView();

        _textViewCacheSize = (TextView)findViewById(R.id.textview_cacheValue);
        _textview_version = (TextView)findViewById(R.id.textview_version);
        _toggleButton = (ToggleButton)findViewById(R.id.toggleBtn_wifi);
        _loginOutBtn = (Button)findViewById(R.id.loginOut_button);

        String version_last = OnlineConfigAgent.getInstance().getConfigParams(_context, "version_last");
        if(version_last.isEmpty() )
        {
            _textview_version.setText(CommonUtils.getVersionName(this));
        }else if(!version_last.equals(CommonUtils.getVersionName(_context))) {
            _textview_version.setText(version_last);
            _textview_version.setTextColor(getResources().getColor(R.color.red));
        }else{
            _textview_version.setText(CommonUtils.getVersionName(this));
        }



        findViewById(R.id.rl_aboutUs).setOnClickListener(this);
        findViewById(R.id.rl_license).setOnClickListener(this);
        findViewById(R.id.rl_agreement).setOnClickListener(this);
        findViewById(R.id.rl_cleanCache).setOnClickListener(this);
        findViewById(R.id.view_back).setOnClickListener(this);
        findViewById(R.id.rl_updateVersion).setOnClickListener(this);
        findViewById(R.id.loginOut_button).setOnClickListener(this);

        boolean flag = (Boolean) SharedPreferencesUtils.getParam(_context,
                SPFConstant.KEY_USE_NO_WIFI_DOWN, false);
        _toggleButton.setChecked(!flag);

        //设置togglebutton
        _toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                }else{
                    DialogHelpUtils.getConfirmDialog(_context, "" + _context.getResources().getText(R.string.mine_setting_wifi_alert),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //设置开启
                                    SharedPreferencesUtils.setParam(_context, SPFConstant.KEY_USE_NO_WIFI_DOWN, true);
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    _toggleButton.setChecked(true);//设置关闭
                                    SharedPreferencesUtils.setParam(_context, SPFConstant.KEY_USE_NO_WIFI_DOWN, false);
                                }
                            }
                    ).show();
                }
            }
        });


        if(UserLogic.getInstance().checkLoginSuc())
        {
            //登录成功
            _loginOutBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            //
            _loginOutBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.view_back:
                finish();
                break;
            case R.id.rl_aboutUs:
                //关于我们
                AboutUsActivity.actionStart(_context);
                break;
            case R.id.rl_license:
                //许可
                AgreementActivity.actionStart(_context,AgreementActivity.LICENSE);
                break;
            case R.id.rl_agreement:
                //隐私协议
                AgreementActivity.actionStart(_context,AgreementActivity.AGREEMENT);
                break;
            case R.id.rl_cleanCache:
                //清理缓存
                DialogHelpUtils.getConfirmDialog(_context, "" + _context.getResources().getText(R.string.delete_cache),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DataCleanUtils.clearAllCache(_context);
                                caculateCacheSize();
                            }
                        }).show();

                break;
            case R.id.rl_updateVersion:
                UmengUtils.getInstance().UpdateVersionManual(this);
                break;
            case R.id.loginOut_button:
                //退出登录
                loginOut();
                break;
            default:break;
        }
    }

    //退出登录
    private void loginOut()
    {
        UserLogic.getInstance().loginOut(this);
        this.finish();
        ToastUtils.showShort(this,R.string.mine_setting_loginout);
    }

    //计算缓存
    private void caculateCacheSize() {
        try {
            String totalSize =  DataCleanUtils.getTotalCacheSize(_context);
            _textViewCacheSize.setText(totalSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
