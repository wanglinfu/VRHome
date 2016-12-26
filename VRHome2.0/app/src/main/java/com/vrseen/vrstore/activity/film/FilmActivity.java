package com.vrseen.vrstore.activity.film;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.fragment.film.TelevisionFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.model.film.FilmCateroryData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import java.util.List;

import in.srain.cube.util.NetworkStatusManager;

/**
 * Created by jiangs on 16/5/6.
 */
public class FilmActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    private View _viewBack;
    private ViewPager _viewPager;
    private SmartTabLayout _viewPagerTab;
    private View _ll_nav;

    //判断改变悬浮按钮旋转动画
    private boolean turned = false;
    private boolean ismoving = false;
    private boolean movedLater = false;//true=刚刚移动过
    //获取手机的宽高
    private int screenwidth;
    private int screenheight;
    private int lastX;
    private int lastY;

    private Animation toFork;
    private Animation toNormal;

    private ImageButton float_button;

    private ProgressRelativeLayout _progressRelativeLayout;

    private Context _context;

    //打开对应影视的播放
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, FilmActivity.class);
        CommonUtils.startActivityWithAnim(context, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("FilmActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_television);
        _context=this;
        initView();
    }

    protected void initView() {

        float_button = (ImageButton) findViewById(R.id.float_button);

        _progressRelativeLayout = (ProgressRelativeLayout) findViewById(R.id.progress_layout);
        _progressRelativeLayout.showProgress();

        if (!NetworkStatusManager.getInstance(_context).isNetworkConnectedHasMsg(false)) {
            _progressRelativeLayout.showErrorText(getResources().getString(R.string.get_data_fail));
            return;
        }

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenwidth = dm.widthPixels;
        screenheight = dm.heightPixels - 130;
        toFork = AnimationUtils.loadAnimation(this, R.anim.rotate_to_fork);
        toNormal = AnimationUtils.loadAnimation(this, R.anim.rotate_to_normal);

        float_button.setOnClickListener(this);
        float_button.setOnTouchListener(this);

        _viewBack = findViewById(R.id.view_back);
        _viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        _viewPager = (ViewPager) findViewById(R.id.viewpager);
        _ll_nav = findViewById(R.id.ll_nav);

        _ll_nav.setOnClickListener(this);
        _viewBack.setOnClickListener(this);
        requestVideoCategory();
    }

    private void requestVideoCategory() {
        CommonRestClient.getInstance(this).getVideoCategory(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(FilmActivity.this, resp, e, _context.getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) {
                FilmCateroryData filmCateroryData = (FilmCateroryData) resp.getModel();
                if (filmCateroryData != null && filmCateroryData.getData() != null && filmCateroryData.getData().size() > 0) {


                    FragmentPagerItems.Creator creator = FragmentPagerItems.with(FilmActivity.this);
                    List<FilmCateroryData.Category> categories = filmCateroryData.getData();
                    for (FilmCateroryData.Category category : categories) {
                        creator.add(category.getGenre_name(), TelevisionFragment.class,
                                new Bundler().putSerializable(TelevisionFragment.KEY_MODEL_CATEGORY, category).get());
                    }
                    FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                            getSupportFragmentManager(), creator.create());

                    _viewPager.setAdapter(adapter);
                    _viewPagerTab.setViewPager(_viewPager);
                }

                _progressRelativeLayout.showContent();

            }
        });
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();

        switch (vid) {
            case R.id.view_back:
                finish();
                break;
            case R.id.ll_nav:
                Intent intent = new Intent(this, VideoNavActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.float_button:
                if (!ismoving) {
                    if (movedLater) {
                        movedLater = false;
                        return;
                    }
                    //float_button.startAnimation(toFork);
                    U3dMediaPlayerLogic.getInstance().comeinVR(this,"loadlevel|DYY_wj");
//
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int position = data.getIntExtra(VideoNavActivity.KEY_POSITION, 0);
            _viewPager.setCurrentItem(position);
        }
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //获取控件一开始的位置
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                ismoving = false;
                break;

            case MotionEvent.ACTION_MOVE:
                int det = 10;//小于此偏移量都当没有移动
                //获取移动的距离
                int dx = (int) (event.getRawX() - lastX);
                int dy = (int) (event.getRawY() - lastY);
                if (Math.abs(dx) < det && Math.abs(dy) < det) {//当只是移动没有点击时，无需变换悬浮按钮的状态
                    //如果移动时正好时叉号状态，变回原样
                    ismoving = false;
                } else {
                    movedLater = true;
                    ismoving = true;
                    if (turned) {
                        turned = false;
                        //float_button.startAnimation(toNormal);
                    }
                }
                //getLeft()方法得到的是控件坐标距离父控件原点(左上角，坐标（0，0）)的x轴距离，
                //getRight()是控件右边距离父控件原点的x轴距离，同理，getTop和getButtom是距离的y轴距离。
                int left = v.getLeft() + dx;
                int right = v.getRight() + dx;
                int top = v.getTop() + dy;
                int bottom = v.getBottom() + dy;

                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }

                if (right > screenwidth) {
                    right = screenwidth;
                    left = right - v.getWidth();
                }

                if (top < 0) {
                    top = 0;
                    bottom = top + v.getHeight();
                }

                if (bottom > screenheight) {
                    bottom = screenheight;
                    top = bottom - v.getHeight();
                }

                //到达边界后不能再移动
                v.layout(left, top, right, bottom);
                //Log.i("@@@@@@", "position:" + left + ", " + top + ", " + right + ", " + bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //Log.i("lastx ,lasty", "lastx:"+lastX+"lasty:"+lastY);
                break;

            case MotionEvent.ACTION_UP:

                int detUp = 10;//小于此偏移量都当没有移动
                //获取移动的距离
                int dxUp = (int) (event.getRawX() - lastX);
                int dyUp = (int) (event.getRawY() - lastY);

                int leftUp = v.getLeft() + dxUp;
                int rightUp = v.getRight() + dxUp;
                int topUp = v.getTop() + dyUp;
                int bottomUp = v.getBottom() + dyUp;

//                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v,"translationX", 0);

                if (leftUp <= screenwidth / 2) {

                    ObjectAnimator.ofFloat(v, "translationX",leftUp, 0f).setDuration(300).start();
                    leftUp = 0;
                    rightUp = leftUp + v.getWidth();

                }

                if (rightUp > screenwidth / 2) {

                    rightUp = screenwidth;
                    leftUp = rightUp - v.getWidth();
                    ObjectAnimator.ofFloat(v, "translationX",-360f, 0f).setDuration(300).start();

                }

                if (topUp < 0) {
                    topUp = 0;
                    bottomUp = topUp + v.getHeight();
                }

                if (bottomUp > screenheight) {
                    bottomUp = screenheight;
                    topUp = bottomUp - v.getHeight();
                }
//                if (leftUp <= screenwidth / 2) {
//                    animation = new TranslateAnimation(leftUp, 0, 0, 0);
//                } else {
//
//                    animation = new TranslateAnimation(leftUp, screenwidth, 0, 0);
//                }
//
//                animation.setDuration(500);
//                v.startAnimation(animation);
                v.layout(leftUp, topUp, rightUp, bottomUp);
                //Log.i("@@@@@@", "position:" + left + ", " + top + ", " + right + ", " + bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //Log.i("lastx ,lasty", "lastx:"+lastX+"lasty:"+lastY);

                break;
        }

        return false;
    }

}
