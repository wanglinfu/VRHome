package com.vrseen.vrstore.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vrseen.vrstore.R;

/**
 * Toast相关工具
 * Created by jiangs on 16/4/27.
 */

public class ToastUtils {
    public static final int TYPE_ERROR = 1;
    public static final int TYPE_WARN = 2;
    public static final int TYPE_SUCCESS = 3;//文字前带'√'的标志

    public static Toast mToast = null;

    public static void show(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void show(Context context, final @StringRes int resId, int duration) {
        Toast.makeText(context, resId, duration).show();
    }

    public static void show(Context context, int resId, int duration, int type) {
        show(context, context.getString(resId), duration, type);
    }

    public static void showShort(Context context, String message) {
        show(context, message, Toast.LENGTH_SHORT);
    }

    public static void showShort(Context context, final @StringRes int messageId) {
        show(context, messageId, Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, String message) {
        show(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 显示消息, 并且停止之前的消息, 保持消息的时效性, 避免一个接一个不停弹出消息的情景.
     * @param context
     * @param message
     * @param duration
     */
    public static void showOnly(Context context, String message, int duration) {
        if (null == mToast) {
            mToast = Toast.makeText(context, message, duration);
        } else {
            //先取消之前的toast, 避免等待和混杂
            mToast.cancel();
            mToast = Toast.makeText(context, message, duration);
        }
        mToast.show();
    }

    /**
     * 显示消息, 并且停止之前的消息, 保持消息的时效性, 避免一个接一个不停弹出消息的情景.可以设置显示位置.
     * @param context
     * @param message
     * @param duration
     * @param gravity
     */
    public static void showOnly(Context context, String message, int duration, int gravity) {
        if (null == mToast) {
            mToast = Toast.makeText(context, message, duration);
        } else {
            //先取消之前的toast, 避免等待和混杂
            mToast.cancel();
            mToast = Toast.makeText(context, message, duration);
        }
        mToast.setGravity(gravity, 0, 0);
        mToast.show();
    }

    /**
     * 显示消息
     *
     * @param context  context
     * @param message  消息内容
     * @param duration 显示时长
     * @param type     消息类型（决定显示的图片）
     */
    public static void show(Context context, String message, int duration, int type) {
        Toast toast = Toast.makeText(context, message, duration);
        View view = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
        ImageView icon = (ImageView) view.findViewById(R.id.iv_icon);
        switch (type) {
            case TYPE_SUCCESS:
//                icon.setImageResource(R.drawable.ic_toast_success);
                break;
            //TODO 其他类型
            default:
                icon.setVisibility(View.GONE);
                break;
        }
        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        tv_message.setText(message);
        toast.setView(view);
        //屏幕2/3高度的位置
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                0,
                DensityUtil.getScreenHeight(context) * 2 / 3);
        toast.show();
    }



}
