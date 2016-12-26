package com.vrseen.vrstore.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.model.user.UserInfo;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ToastUtils;

/**
 * 兑换会员卡
 * Created by mll on 2016/5/10.
 */
public class ExChangeActivity extends BaseActivity {


    private TextView _textViewCard = null;

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,ExChangeActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setPageName("ExChangeActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_vip);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        _textViewCard = (TextView)findViewById(R.id.et_card);

        findViewById(R.id.view_back).setOnClickListener(this);
        findViewById(R.id.textview_config).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.view_back:
                this.finish();
                break;
            case R.id.textview_config:
                if(StringUtils.isBlank( _textViewCard.getText().toString()))
                {
                    ToastUtils.showShort(ExChangeActivity.this,R.string.mine_vip_card_null);
                    return;
                }
                UserRestClient.getInstance(this).registerVIP(_textViewCard.getText().toString(), new AbstractRestClient.ResponseCallBack() {
                    @Override
                    public void onFailure(Response resp, Throwable e) {
                        CommonUtils.showResponseMessage(ExChangeActivity.this,resp,e,R.string.mine_vip_exchange_fail);
                    }

                    @Override
                    public void onSuccess(Response resp) {

                        ToastUtils.showShort(ExChangeActivity.this,R.string.mine_vip_exchange_suc);
                        UserInfo userInfo = UserLogic.getInstance().getUserInfo();
                        userInfo.setIs_vip(1);
                        UserLogic.getInstance().setUserInfo(userInfo);

                        ExChangeActivity.this.finish();
                    }
                });

                break;
            default:break;
        }
    }

}
