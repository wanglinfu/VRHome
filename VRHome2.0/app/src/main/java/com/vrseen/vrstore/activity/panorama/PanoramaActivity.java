package com.vrseen.vrstore.activity.panorama;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.activity.film.VideoNavActivity;
import com.vrseen.vrstore.activity.search.SearchActivity;
import com.vrseen.vrstore.adapter.panorama.PanoramaAllTypeAdapter;
import com.vrseen.vrstore.fragment.panorama.PanoramaHomeFragment;
import com.vrseen.vrstore.fragment.panorama.PanoramaListFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.PanoramaRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.SearchRestClient;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.model.panorama.PanoramaAllTypeData;
import com.vrseen.vrstore.model.panorama.PanoramaCategoryData;
import com.vrseen.vrstore.util.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haoxiaohui
 *         全景资源
 */

public class PanoramaActivity extends BaseActivity implements View.OnClickListener , View.OnTouchListener{

    private Context _context;
    /**
     * 城市选择request（startActivityForResult）
     */
    private static final int CITY_SELECT = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("PanoramaActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama);
        _context = this;
        initView();
    }

    private Button _searchButton;

    /**
     * 返回键
     */
    private View _view_back;
    /**
     * 一级滑动菜单
     */
    private SmartTabLayout _viewPagerTab;
    /**
     * 滑动翻页
     */
    private ViewPager _viewPager;
    /**
     * 导航按钮
     */
    private View _ll_nav;

    /**
     * 二级菜单
     */
    private LinearLayout _ll_menu;
    /**
     * 二级菜单菜单项
     */

    private TextView view_menu2_video;

    private int _index = 0;
    /**
     * 被选择的类型id
     */
    public static int _cate_id_selected = 0;
    /**
     * 城市选择
     */
    private LinearLayout _citySelectLinearLayout;
    private TextView _cityTextView;

    /**
     * 当前城市
     */
    private static String _currentCity = "杭州";
    private static String _currentCityId = "19";

    private PanoramaCategoryData _panoramaCateroryData;
    private List<PanoramaCategoryData.Category> _categories;
    /**
     * 全景全部分类
     */
    private List<PanoramaAllTypeData> _types;

    private static String _currentAllTypeId = "-1";

    private FragmentPagerItemAdapter _fragmentAdapter;

    private GridView _panoramaAllTypeGridView;

    private FragmentPagerItems.Creator _creator;

    private int pageIndex = 0;

    @Override
    protected void initView() {
        super.initView();

        _categories = new ArrayList<>();
        _categories.add(new PanoramaCategoryData.Category(-1, getResources().getString(R.string.film_all_film)));

        _types = new ArrayList<>();
        _types.add(new PanoramaAllTypeData("-1", getResources().getString(R.string.film_all_film)));

        _searchButton=(Button)findViewById(R.id.btn_search);
        _searchButton.setOnClickListener(this);

        _view_back = findViewById(R.id.view_back);
        _view_back.setOnClickListener(this);

        _viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        _viewPager = (ViewPager) findViewById(R.id.viewpager);
        _ll_nav = findViewById(R.id.ll_nav);
        _ll_nav.setOnClickListener(this);
        _ll_nav.setVisibility(View.GONE);

        _ll_menu = (LinearLayout) findViewById(R.id.view_menu2);
        _ll_menu.setVisibility(View.GONE);

        _citySelectLinearLayout=(LinearLayout)findViewById(R.id.ll_panorama_city);
        _cityTextView=(TextView)findViewById(R.id.tv_panorama_city);
        _citySelectLinearLayout.setOnClickListener(this);
        _cityTextView.setText(_currentCity);

        _panoramaAllTypeGridView = (GridView) findViewById(R.id.gv_panorama_all_type);
        _panoramaAllTypeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                itemSelect(_panoramaAllTypeGridView, position);

                int index = _viewPager.getCurrentItem();

                _currentAllTypeId = _types.get(position).getApi_tag();

                Fragment fragment = _fragmentAdapter.getPage(index);//获取当前页的fragment

                if (fragment instanceof PanoramaHomeFragment) {
                } else if (fragment instanceof PanoramaListFragment) {
                    ((PanoramaListFragment) fragment).refresh(_currentAllTypeId, _context);
                }

            }
        });

        _viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position==0){
                    _ll_menu.setVisibility(View.GONE);
                }else{
                    _ll_menu.setVisibility(View.VISIBLE);
                }

                Fragment fragment = _fragmentAdapter.getPage(position);//获取当前页的fragment

                Log.e("fragment", "" + fragment.getId()+"==="+position);

                if (fragment instanceof PanoramaHomeFragment) {
                } else if (fragment instanceof PanoramaListFragment) {
                    ((PanoramaListFragment) fragment).refresh(_currentAllTypeId, _context);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        requestPanoramaCategory(_currentCityId);
        requestPanoramaAllType(_currentCityId);

        float_button = (ImageButton)findViewById(R.id.float_button);
//        UserLogic.getInstance().initLogin(_context);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenwidth = dm.widthPixels;
        screenheight = dm.heightPixels - 130;

        toFork = AnimationUtils.loadAnimation(_context, R.anim.rotate_to_fork);
        toNormal = AnimationUtils.loadAnimation(_context, R.anim.rotate_to_normal);
        float_button.setOnClickListener(this);
        float_button.setOnTouchListener(this);


    }

    private void itemSelect(GridView gridView, int position) {

        int count = gridView.getAdapter().getCount();

        for (int i = 0; i < count; i++) {

            if (i == position) {
                ((TextView) gridView.getChildAt(i).findViewById(R.id.tv_city_item_name)).setTextColor(getResources().getColor(R.color.common_app_color));

            } else {

                ((TextView) gridView.getChildAt(i).findViewById(R.id.tv_city_item_name)).setTextColor(getResources().getColor(R.color.contentColor));

            }

        }

    }


    private void requestPanoramaAllType(String currentCityId) {
        PanoramaRestClient.getInstance(this).getPanoramaAllType(currentCityId, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(PanoramaActivity.this, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) throws JSONException {


                JSONObject jo = (JSONObject) resp.getData();

                String str = jo.getString("data");

                Gson g = new Gson();

                List<PanoramaAllTypeData> types = g.fromJson(str, new TypeToken<List<PanoramaAllTypeData>>() {
                }.getType());
                _types.addAll(types);

                PanoramaAllTypeAdapter panoramaAllTypeAdapter = new PanoramaAllTypeAdapter(_context, _types);
                _panoramaAllTypeGridView.setNumColumns(_types.size());
                _panoramaAllTypeGridView.setAdapter(panoramaAllTypeAdapter);


            }
        });
    }

    private void requestPanoramaCategory(String currentCityId) {
        PanoramaRestClient.getInstance(this).getPanoramaCategory(currentCityId, new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(PanoramaActivity.this, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) {

                _panoramaCateroryData = (PanoramaCategoryData) resp.getModel();

                if (_panoramaCateroryData != null && _panoramaCateroryData.getData() != null && _panoramaCateroryData.getData().size() > 0) {

                    _categories.addAll(_panoramaCateroryData.getData());

                    initFragment();

                }

            }
        });
    }


    private void initFragment() {

        if (_creator == null) {
            _creator =
                    FragmentPagerItems.with(PanoramaActivity.this);

        }

        for (PanoramaCategoryData.Category category : _categories) {

            if (category.getId() == -1) {
                _creator.add(category.getCate(), PanoramaHomeFragment.class,
                        new Bundler().putSerializable(PanoramaHomeFragment.KEY_MODEL_CATEGORY, category).get());

            } else {
                _creator.add(category.getCate(), PanoramaListFragment.class,
                        new Bundler().putSerializable(PanoramaListFragment.KEY_MODEL_CATEGORY, category).get());

            }

        }


        _fragmentAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), _creator.create());

        _viewPager.setAdapter(_fragmentAdapter);
        _viewPagerTab.setViewPager(_viewPager);

    }

    @Override
    public void onClick(View v) {

        int vid = v.getId();
        if (vid == R.id.view_back) {
            finish();
        } else if (vid == R.id.ll_nav) {
            Intent intent = new Intent(this, VideoNavActivity.class);
            startActivityForResult(intent, 1);
        } else if (vid == R.id.ll_panorama_city) {
            Intent intent = new Intent(this, CitySelectActivity.class);
            startActivityForResult(intent, CITY_SELECT);
        }else if(vid==R.id.btn_search){
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("from", SearchRestClient.PANORAMA_TAG);
            startActivity(intent);
        }else if(vid==R.id.float_button){


                if(!ismoving)
                {
                    if(movedLater){
                        movedLater = false;
                        return;
                    }
                    //float_button.startAnimation(toFork);
                    U3dMediaPlayerLogic.getInstance().comeinVR(this,"loadlevel|Scene_Home|5");;
//
                }


        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CITY_SELECT:

                if (resultCode == 0) {
                    String citySelect = data.getExtras().getString("city_name");
                    if (citySelect != null) {
                        _currentCity = data.getExtras().getString("city_name");
                        _currentCityId = data.getExtras().getString("city_id");
                        _cityTextView.setText(_currentCity);
                        startActivity(new Intent(_context, PanoramaActivity.class));
                        finish();

                    }
                }

                break;
        }
    }

    public static String getCityId() {
        return _currentCityId;
    }//获取城市ID

    public static String getAllTypeId() {
        return _currentAllTypeId;
    }//获取

    /**
     * y1手指按下
     * y2手指抬起
     */
    float y1 = 0;
    float y2 = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        float distance = getResources().getDimension(R.dimen.menu_disappear_distance);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y1 = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:

                if(_viewPager.getCurrentItem()!=0) {

                    y2 = ev.getY();
                    if (y2 - y1 > distance) {
                        _ll_menu.setVisibility(View.VISIBLE);
                    } else if (y2 - y1 < -distance) {
                        _ll_menu.setVisibility(View.GONE);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if(_viewPager.getCurrentItem()!=0) {
                    y2 = ev.getY();
                    if (y2 - y1 > distance) {
                        _ll_menu.setVisibility(View.VISIBLE);
                    } else if (y2 - y1 < -distance) {
                        _ll_menu.setVisibility(View.GONE);
                    }
                    y1 = 0;
                    y2 = 0;
                }
                break;

        }

        return super.dispatchTouchEvent(ev);
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action)
        {
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
                if(Math.abs(dx) < det && Math.abs(dy) < det )
                {//当只是移动没有点击时，无需变换悬浮按钮的状态
                    //如果移动时正好时叉号状态，变回原样
                    ismoving = false;
                }else {
                    movedLater = true;
                    ismoving = true;
                    if(turned)
                    {
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

                if(left < 0)
                {
                    left = 0;
                    right = left + v.getWidth();
                }

                if(right > screenwidth)
                {
                    right = screenwidth;
                    left = right - v.getWidth();
                }

                if(top < 0)
                {
                    top = 0;
                    bottom = top + v.getHeight();
                }

                if(bottom > screenheight)
                {
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

                if (leftUp < screenwidth / 2) {
                    leftUp = 0;
                    rightUp = leftUp + v.getWidth();
                }
                if (rightUp > screenwidth / 2) {
                    rightUp = screenwidth;
                    leftUp = rightUp - v.getWidth();
                }

                if (topUp < 0) {
                    topUp = 0;
                    bottomUp = topUp + v.getHeight();
                }

                if (bottomUp > screenheight) {
                    bottomUp = screenheight;
                    topUp = bottomUp - v.getHeight();
                }

                //到达边界后不能再移动
                v.layout(leftUp, topUp, rightUp, bottomUp);
                //Log.i("@@@@@@", "position:" + left + ", " + top + ", " + right + ", " + bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //Log.i("lastx ,lasty", "lastx:"+lastX+"lasty:"+lastY);

                break;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        Log.e("ondestory","+++++++++++++++");
        super.onDestroy();
    }



}
