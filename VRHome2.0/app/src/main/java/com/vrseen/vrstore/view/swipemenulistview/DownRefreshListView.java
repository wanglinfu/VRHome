package com.vrseen.vrstore.view.swipemenulistview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.vr.cardboard.ThreadUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vrseen.vrstore.R;

/**
 * Created by VRSeen_Software on 2016/6/28.
 */
public class DownRefreshListView extends ListView {

    protected static final int msg_loadingMore_finish = 1;

    public DownRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public DownRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DownRefreshListView(Context context) {
        super(context);
        initView();
    }

    // 刷新头的跟布局 会添加轮播图
    @ViewInject(R.id.refresh_header_root)
    private LinearLayout refresh_header_root;

    // 刷新头布局
    @ViewInject(R.id.refresh_header_view)
    private LinearLayout refresh_header_view;

    @ViewInject(R.id.refresh_header_progressbar)
    private ProgressBar refresh_header_progressbar;

    @ViewInject(R.id.refresh_header_imageview)
    private ImageView refresh_header_imageview;

    // 标题：下拉刷新、释放刷新、正在刷新
    @ViewInject(R.id.refresh_header_text)
    private TextView refresh_header_text;

    private RotateAnimation _down2up;
    private RotateAnimation _up2down;
    private View            _rollView;

    private boolean         _isLoadingMore = false;//正在加载更多

    /**
     * 初始化 1。添加刷新头
     */
    private void initView() {
        initAnimation();
        View refreshView = View.inflate(getContext(), R.layout.refresh_header, null);
        // 注册
        ViewUtils.inject(this, refreshView);
        // 添加刷新头
        addHeaderView(refreshView);
        // 隐藏刷新头
        // refresh_header_view.setPadding(0, 60, 0, 0); 向下偏移
        // refresh_header_view.setPadding(0, -60, 0, 0); 向上偏移
        // 完全隐藏 paddingTop = -的刷新头的高度
        // refresh_header_view.getHeight();//获取的高度是，显示到屏幕上的高度
        // 获取测 量后的高度 （手动测量）
        refresh_header_view.measure(0, 0);// 00代表的是没有规则，随意测量，测量结果以真实的宽高为准
        // MeasureSpec.AT_MOST 最大值0-1000
        // MeasureSpec.EXACTLY 精确值
        // MeasureSpec.UNSPECIFIED 未指定
        refreshHeaderViewHeight = refresh_header_view.getMeasuredHeight();
        refresh_header_view.setPadding(0, -refreshHeaderViewHeight, 0, 0);
        //添加加载更多脚布局
        footerView = View.inflate(getContext(), R.layout.refresh_footer, null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
//        addFooterView(footerView);
        setOnScrollListener(new OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                /**
                 *
                 */
//                OnScrollListener.SCROLL_STATE_FLING;// 飞翔
//                OnScrollListener.SCROLL_STATE_IDLE;//空闲
//                OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;//按着滚动
                int lastVisiblePosition = getLastVisiblePosition();//显示的最后一个条目索引
                //如果当前是处于空闲状态，并且显示的是最后一个条目
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisiblePosition == getAdapter().getCount()-1){
                    if(!_isLoadingMore){
                        _isLoadingMore = true;
                        Log.i("DownRefreshListView", "加载下一页");
                        //通过handler模拟加载下一页请求
                       handler.sendEmptyMessageDelayed(msg_loadingMore_finish, 2000);
                        if(onRefreshListener!=null){
                            onRefreshListener.onLoadMore();//真正请求下一页逻辑
                        }
                    }
                }
                //如果不是在最后一个条目，再次显示加载更多脚布局
                if(lastVisiblePosition != getAdapter().getCount()-1){
                    footerView.setPadding(0, 0, 0, 0);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                getLastVisiblePosition();
//                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisiblePosition == getAdapter().getCount()-1){
//                    Log.i("DownRefreshListView", "加载下一页");
//                }
            }
        });

    }

    /**
     * 添加轮播图
     */
   public void addRollView(View _rollView) {
        this._rollView = _rollView;
        refresh_header_root.addView(_rollView);
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        // 由下向上旋转 (如果是逆时针，旋转角度就是负值)
        _down2up = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        _down2up.setDuration(200);
        _down2up.setFillAfter(true);
        // 由上向下旋转
        _up2down = new RotateAnimation(-180, -360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        _up2down.setDuration(200);
        _up2down.setFillAfter(true);

    }

    // @Override
    // public boolean dispatchTouchEvent(MotionEvent ev) {
    //
    // return super.dispatchTouchEvent(ev);
    // }
    private int downY = 0;

    private static final int state_down_refresh = 0;
    private static final int state_relese_refresh = 1;
    private static final int state_ing_refresh = 2;

    private static final int msg_refresh_finish = 0;
    private int currentState = state_down_refresh;// 当前状态 下拉刷新

    private int refreshHeaderViewHeight;

    private int myDownY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 好像获取不到down事件，viewpager给抢走了
        // Log.i("DownRefreshListView",
        // "downRefreshListView.onTouchEvent:"+ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//            downY = (int) ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:
//            // 第一个move点作为down点
//            if (downY == 0) {
//                downY = (int) ev.getY();
//                break;
//            }
//            Log.i("DownRefreshListView", "downY:"+downY);
                //如果轮播图的位置和listview的位置重合之后，再去改变paddingTop
                if(rollViewY>=listViewY){
                    if(myDownY==0){//listview和轮播图重合之后的第一个movey
                        myDownY = (int) ev.getY();
                        break;
                    }
                    int moveY = (int) ev.getY();
                    int disY = moveY - myDownY;
//                Log.i("DownRefreshListView", "disY:"+disY);
                    if (disY < 0) {// 向下滑动，再改变paddingTop显示刷新头
                        // 改变刷新头的paddingTop
                        int paddingTop = -refreshHeaderViewHeight + disY;
//                    Log.i("DownRefreshListView", "paddingTop:" + paddingTop);
                        refresh_header_view.setPadding(0, paddingTop, 0, 0);

                        /**
                         * 状态：1.下拉刷新、2.释放刷新、3.正在刷新
                         * 1.当前状态是下拉刷新，paddingTop >=0 ，变为释放刷新
                         * 2.当前状态是释放刷新，paddingTOp < 0 , 变为下拉刷新
                         * 3.如果当前状态是释放刷新，松手，变为正在刷新
                         * 4.如果当前状态是下拉刷新，松手，状态不变，直接隐藏刷新头
                         */
                        if (currentState == state_down_refresh && paddingTop >= 0) {
                            currentState = state_relese_refresh;
                            refreshState();
                        }
                        if (currentState == state_relese_refresh && paddingTop < 0) {
                            currentState = state_down_refresh;
                            refreshState();
                        }
                        return true;//如果我们自己在处理刷新头显示和隐藏，listview就不要处理上下滑动
                    }
                }


                break;
            case MotionEvent.ACTION_UP:

                myDownY = 0;
                /**
                 * * 3.如果当前状态是释放刷新，松手，变为正在刷新 4.如果当前状态是下拉刷新，松手，状态不变，直接隐藏刷新头
                 */
                if (currentState == state_relese_refresh) {
                    currentState = state_ing_refresh;
                    refreshState();
                }
                if (currentState == state_down_refresh) {
                    // 隐藏刷新头
                    refresh_header_view.setPadding(0, -refreshHeaderViewHeight, 0, 0);
                }

                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);//listview上下滚动逻辑
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(_rollView == null) return false;

        //获取listview的位置
        int[] location = new int [2];
        //获取当前控件的位置，x、y。 传递给location。
        getLocationInWindow(location);
        listViewY = location[1];
        //获取轮播图的位置
        _rollView.getLocationInWindow(location);
        rollViewY = location[1];
//        Log.i("DownRefreshListView", "listViewY:"+listViewY);
//        Log.i("DownRefreshListView", "rollViewY:"+rollViewY);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 刷新状态
     */
    private void refreshState() {
        switch (currentState) {
            case state_relese_refresh: // 下拉刷新变为释放刷新
                // 箭头 向上旋转 文字 ： 释放刷新
                refresh_header_imageview.startAnimation(_down2up);
                refresh_header_text.setText("");
                refresh_header_view.startAnimation(_down2up);
                break;
            case state_down_refresh: // 释放刷新变为下拉刷新
                // 箭头 向下旋转 文字 ： 下拉刷新
                refresh_header_imageview.startAnimation(_up2down);
                refresh_header_text.setText("");

                break;
            case state_ing_refresh:  // 释放刷新变为正在刷新
                // 箭头隐藏 进度条出现 文字：正在刷新
                refresh_header_imageview.setVisibility(View.GONE);
                // 清除动画
                refresh_header_imageview.clearAnimation();
                refresh_header_progressbar.setVisibility(View.VISIBLE);
                refresh_header_text.setText("");
                // 刷新头完全显示
                refresh_header_view.setPadding(0, 0, 0, 0);
                // 发送延迟消息，模拟网络操作
//            handler.sendEmptyMessageDelayed(msg_refresh_finish, 2000);
                if(onRefreshListener!=null){
                    onRefreshListener.onRefresh();//真正刷新操作
                }
                break;

            default:
                break;
        }

    }

    private Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case msg_refresh_finish:
                    refreshFinish();
                    break;
                case msg_loadingMore_finish:
                    //隐藏加载下一页脚布局  改变paddingTop让其隐藏
               footerView.setPadding(0, -footerViewHeight, 0, 0);
                    loadMoreFinish();
                    break;

                default:
                    break;
            }
        }




    };
    /**
     * 刷新完成
     */
    public void refreshFinish() {
        // 状态复原
        currentState = state_down_refresh;
        refresh_header_text.setText("");
        refresh_header_imageview.setVisibility(View.VISIBLE);
        refresh_header_progressbar.setVisibility(View.INVISIBLE);
        // 刷新头隐藏
        refresh_header_view.setPadding(0, -refreshHeaderViewHeight, 0, 0);
    };
    public void loadMoreFinish() {
        footerView.setPadding(0, 0, 0, -footerViewHeight);//正的bottom是往上偏移，负的bottom是往下偏移（隐藏）
        _isLoadingMore = false;
    }

    private int listViewY;

    private int rollViewY;

    private int footerViewHeight;

    private View footerView;

    private OnRefreshListener onRefreshListener;
    /**
     * 自定义的刷新监听
     * 1. 下拉刷新
     * 2. 上拉加载更多
     */
    public interface OnRefreshListener{
        /**
         * 下拉刷新回调
         */
        public void onRefresh();
        /**
         * 加载更多回调
         */
        public void onLoadMore();
    }
    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        this.onRefreshListener = onRefreshListener;
    }


}
