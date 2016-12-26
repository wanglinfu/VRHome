package com.vrseen.vrstore.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 可以监听OnScroll的ScrollView
 * Created by vinda on 14-8-6.
 */
public class StatefullScrollView extends ScrollView {
    private OnScrollListener onScrollListener;
    /**
     * 主要是用在用户手指离开后，ScrollView还在继续滑动，我们用来保存Y的距离，然后做比较
     */
    private int lastScrollY;
    //检查次数
    private int checktime = 0;

    public StatefullScrollView(Context context) {
        this(context, null);
    }

    public StatefullScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatefullScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置滚动接口
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }


    /**
     * 用于用户手指离开的时候获取ScrollView滚动的Y距离，然后回调给onScroll方法中
     */
    private Handler handler = new Handler();

    private Runnable checkStateRunnable = new Runnable() {
        @Override
        public void run() {
            int scrollY = StatefullScrollView.this.getScrollY();
            //此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
            if (lastScrollY != scrollY) {
                lastScrollY = scrollY;
                handler.postDelayed(this, 5);
                if (onScrollListener != null) {
                    onScrollListener.onScroll(scrollY);
                }
                checktime = 0;
            } else if (checktime < 100) {
                //即使位置没变，也检查100次
                checktime++;
                handler.postDelayed(this, 5);
            }
        }
    };

    /**
     * 重写onTouchEvent， 当用户的手在ScrollView上面的时候，
     * 直接将ScrollView滑动的Y方向距离回调给onScroll方法中，当用户抬起手的时候，
     * ScrollView可能还在滑动，所以当用户抬起手我们隔5毫秒给handler发送消息，在handler处理
     * ScrollView滑动的距离
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onScrollListener != null) {
            onScrollListener.onScroll(lastScrollY = this.getScrollY());
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                checktime = 0;
                handler.postDelayed(checkStateRunnable, 5);
                break;
            default:
                handler.removeCallbacks(checkStateRunnable);
                break;
        }
        return super.onTouchEvent(ev);
    }


    /**
     * 滚动的回调接口
     */
    public interface OnScrollListener {
        /**
         * 回调方法， 返回ScrollView滑动的Y方向距离
         *
         * @param scrollY
         */
        void onScroll(int scrollY);
    }
}
