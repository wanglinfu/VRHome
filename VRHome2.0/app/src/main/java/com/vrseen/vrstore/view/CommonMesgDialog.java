package com.vrseen.vrstore.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vrseen.vrstore.R;

/**
 * Created by John on 16/2/2.
 */
public class CommonMesgDialog extends Dialog {

    private TextView _messageTextView;

    private TextView _confirmTextView;

    private RelativeLayout _dialogLayout;

    public CommonMesgDialog(Context context) {
        super(context);

        initView(context);
    }

    protected CommonMesgDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    public CommonMesgDialog(Context context, int theme) {
        super(context, theme);
        initView(context);
    }

    private void initView(Context context)
    {

        View view = View.inflate(context, R.layout.common_message_dialog,null);

        _dialogLayout = (RelativeLayout)view.findViewById(R.id.common_dialog_layout);

        _messageTextView = (TextView)view.findViewById(R.id.dialog_message);

        _confirmTextView = (TextView)view.findViewById(R.id.dialog_confirm);

        setContentView(view);

        _confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                CommonMesgDialog.this.dismiss();

            }
        });

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                _dialogLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    public CommonMesgDialog setMessage(String message)
    {

        this._messageTextView.setText(message);

        return this;
    }


}
