package com.vrseen.vrstore.activity.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ToastUtils;

/**
 * 意见反馈
 * Created by mll on 2016/5/5.
 */
public class FeedbackActivity extends BaseActivity {


    private EditText _adviceTitleEditText;
    private Context _context;

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,FeedbackActivity.class);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setPageName("FeedbackActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        _context = this;
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        _adviceTitleEditText = (EditText)findViewById(R.id.adviceEditText);
        EditChangedListener editChangedListener = new EditChangedListener();
        editChangedListener.update(200, _adviceTitleEditText);

        findViewById(R.id.view_back).setOnClickListener(this);
        findViewById(R.id.textview_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.view_back:
                this.finish();
                break;
            case R.id.textview_submit:
                //submit
                submit();
                break;
            default:break;
        }
    }

    private void submit()
    {
        String strAdvice = _adviceTitleEditText.getText().toString();
        if(StringUtils.isBlank(strAdvice) || strAdvice == null){
            String msg = getString(R.string.message_server_1001019);
            ToastUtils.showShort(this,msg);
            return ;
        }

        UserRestClient.getInstance(this).submitAdvice(_adviceTitleEditText.getText().toString(), new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                ToastUtils.showShort(_context,R.string.mine_feedback_fail);
            }

            @Override
            public void onSuccess(Response resp) {
                if (resp != null) {
                    ToastUtils.showShort(_context,R.string.mine_feedback_success);
                    Activity ac = (Activity) _context;
                    ac.finish();
                }
            }
        });
    }


    private class EditChangedListener implements TextWatcher {
        private CharSequence temp;//监听前的文本
        private int editStart;//光标开始位置
        private int editEnd;//光标结束位置
        private int _charMaxNum = 10;
        private EditText _editText;

        public void update(int maxNum,EditText editText)
        {
            _charMaxNum = maxNum;
            _editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // _editText.setText("还能输入" + (_charMaxNum - s.length()) + "字符");
        }

        @Override
        public void afterTextChanged(Editable s) {
            /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
            editStart = _editText.getSelectionStart();
            editEnd = _editText.getSelectionEnd();
            if (temp.length() > _charMaxNum)
            {
                String msg = getString(R.string.message_account_error_input);
             ToastUtils.showShort(getApplicationContext(),msg+_charMaxNum +getString(R.string.alert_byte));
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                _editText.setText(s);
                _editText.setSelection(tempSelection);
            }
        }
    }
}
