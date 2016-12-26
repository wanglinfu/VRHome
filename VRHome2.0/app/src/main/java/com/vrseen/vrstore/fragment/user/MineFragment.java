package com.vrseen.vrstore.fragment.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.film.FilmDetailActivity;
import com.vrseen.vrstore.activity.user.ExChangeActivity;
import com.vrseen.vrstore.activity.user.FeedbackActivity;
import com.vrseen.vrstore.activity.user.LoginActivity;
import com.vrseen.vrstore.activity.user.MyCollectActivity;
import com.vrseen.vrstore.activity.user.MyPlayRecordActivity;
import com.vrseen.vrstore.activity.user.MySettingActivity;
import com.vrseen.vrstore.adapter.user.PlayRecordAdapter;
import com.vrseen.vrstore.fragment.BaseFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.http.UserRestClient;
import com.vrseen.vrstore.logic.UserLogic;
import com.vrseen.vrstore.model.film.FilmDetailData;
import com.vrseen.vrstore.model.user.FilmListDatas;
import com.vrseen.vrstore.model.user.PlayRecordInfo;
import com.vrseen.vrstore.model.user.UserInfo;
import com.vrseen.vrstore.util.Constant;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.view.RoundImageView;
import com.vrseen.vrstore.view.SelectPicPopupWindow;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cz.msebera.android.httpclient.Header;

/**
 * Created by mll on 2016/5/4.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private View _thisFragment;
    private static MainActivity _context;
    private TextView _textViewName;
    private TextView _vipLimit;
    private ImageView _imgHuiYuan;
    private ImageView _loginPlat;
    private static RoundImageView _headImageView;
    private GridView _gridViewPlayRecord;
    private PlayRecordAdapter _playRecordAdapter;
    private RelativeLayout _relativePlayRecord;
    private int _page=1;
    private int _limit=15;
    private boolean _toFrushData = true;

    private List<FilmListDatas.Data.Data1> _data1List;
    private ArrayList<PlayRecordInfo> _palyRecordList = new ArrayList<>();

    public static MineFragment instance;

    private BitmapUtils _bitmapUtils;

    @Override
    public void onStart() {
        super.onStart();
        updateInfo();
    }

    public void updateInfo()
    {
        UserInfo userInfo = UserLogic.getInstance().getUserInfo();
        if(userInfo != null && _textViewName != null && _imgHuiYuan != null)
        {
            //登录成功
            _headImageView.setClickable(true);
            _textViewName.setText(userInfo.getName());
            if(userInfo.getIs_vip() == 0)
            {
                _imgHuiYuan.setVisibility(View.GONE);
                _vipLimit.setText(Html.fromHtml("<u>"+getResources().getString(R.string.to_get_vip)+"</u>"));
            }
            else
            {
                _imgHuiYuan.setVisibility(View.VISIBLE);
                _vipLimit.setText(getResources().getString(R.string.vip_limit_to) + userInfo.getVip_end_date());
            }
            //设置头像，如果没有头像数据，还用默认的头像
            //get_avatarBitmap()是中兴头像,有中兴头像优先显示中兴头像
            if(userInfo.get_avatarBitmap() != null){
                _headImageView.setImageBitmap(userInfo.get_avatarBitmap());
            }else if( StringUtils.isNotBlank(userInfo.getAvatar()))
            {
                _bitmapUtils.display(_headImageView,userInfo.getAvatar());
            }

        }
        else
        {
            if(VRHomeConfig.CUR_RELEASE_TYPE != VRHomeConfig.ReleaseType.ZTE)
                _headImageView.setClickable(false);
            if(_textViewName != null)
            {
                _textViewName.setText(getString(R.string.mine_login_guide));
                _vipLimit.setText("");
            }
            _imgHuiYuan.setVisibility(View.GONE);

            _headImageView.setImageDrawable(_thisFragment.getResources().getDrawable(R.drawable.default_icon));
        }

        String keyPlatName = (String) SharedPreferencesUtils.getParam(_context.getApplicationContext(), SPFConstant.KEY_PLAT_NANE, "");

        if(StringUtils.isBlank(keyPlatName) && (userInfo != null && userInfo.getExtType() == null))
        {
            _loginPlat.setVisibility(View.INVISIBLE);
        }
        else
        {
            if(keyPlatName.equals(QQ.NAME) || (userInfo != null && userInfo.getExtType().contains("qq"))){
                _loginPlat.setVisibility(View.VISIBLE);
                _loginPlat.setImageResource(R.drawable.icon_qq);
            }else  if(keyPlatName.equals(Wechat.NAME) || (userInfo != null && userInfo.getExtType().contains("weix"))){
                _loginPlat.setVisibility(View.VISIBLE);
                _loginPlat.setImageResource(R.drawable.icon_weix);
            }else  if(keyPlatName.equals(SinaWeibo.NAME) || (userInfo != null && userInfo.getExtType().contains("weib"))){
                _loginPlat.setVisibility(View.VISIBLE);
                _loginPlat.setImageResource(R.drawable.icon_weib);
            }
        }

        updatePlayRecord();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.setPageName("MineFragment");
        super.onCreateView(inflater, container, savedInstanceState);
        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_mine, null);
            initView();
        } else {
            // 从parent删除
            ViewGroup parent = (ViewGroup) _thisFragment.getParent();
            if (parent != null) {
                parent.removeView(_thisFragment);
            }
        }
        _context = (MainActivity) getActivity();
        instance = this;

        _bitmapUtils = new BitmapUtils(_context);
        return _thisFragment;

    }

    private void initView() {
        //处理账号
        _textViewName = (TextView) _thisFragment.findViewById(R.id.textview_name);
        _vipLimit = (TextView) _thisFragment.findViewById(R.id.vip_limit);
        _imgHuiYuan = (ImageView) _thisFragment.findViewById(R.id.img_huiyuan);
        _gridViewPlayRecord = (GridView) _thisFragment.findViewById(R.id.gridview_playRecord);
        _relativePlayRecord = (RelativeLayout) _thisFragment.findViewById(R.id.relative_playRecord);

        _headImageView = (RoundImageView) _thisFragment.findViewById(R.id.iv_headpicture);
        _loginPlat = (ImageView) _thisFragment.findViewById(R.id.login_plat);

        _headImageView.setOnClickListener(this);
        _vipLimit.setOnClickListener(this);
        _headImageView.setRectAdius(_thisFragment.getResources().getDimension(R.dimen.circle_value));
        _thisFragment.findViewById(R.id.rl_feedback).setOnClickListener(this);
        _thisFragment.findViewById(R.id.rl_account).setOnClickListener(this);
        _thisFragment.findViewById(R.id.rl_setting).setOnClickListener(this);
        _thisFragment.findViewById(R.id.rl_VIP).setOnClickListener(this);
        _thisFragment.findViewById(R.id.relative_myCollect).setOnClickListener(this);
        _thisFragment.findViewById(R.id.relative_playRecord).setOnClickListener(this);

        //更新的登录记录
        updatePlayRecord();
        _gridViewPlayRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilmDetailData.PlayProgressData playProgressData = new FilmDetailData.PlayProgressData();
                playProgressData.setId(_palyRecordList.get(position).getResource_id());
                playProgressData.setProValue(Integer.valueOf(_palyRecordList.get(position).getLast_tick()));
                int episodeID = _palyRecordList.get(position).getLast_episode_id();
                playProgressData.setEpisodeID(episodeID);
                FilmDetailActivity.actionStart(_context,playProgressData);
            }
        });
    }

    public android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                case 1:
                    super.handleMessage(msg);
                    if (_playRecordAdapter == null) {
                        _playRecordAdapter = new PlayRecordAdapter(getActivity(), _palyRecordList);
                        _gridViewPlayRecord.setAdapter(_playRecordAdapter);
                    } else if (_toFrushData) {
                        _playRecordAdapter.notifyDataSetChanged();
                    }
                    if (_playRecordAdapter.getCount() > 0 && UserLogic.getInstance().checkLoginSuc()) {
                        _gridViewPlayRecord.setVisibility(View.VISIBLE);
                        setGridView();
                    } else {
                        _gridViewPlayRecord.setVisibility(View.GONE);
                        setGridView();
                    }

                    if (_gridViewPlayRecord.getVisibility() == View.VISIBLE) {
                        _relativePlayRecord.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    } else {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                (int) getActivity().getResources().getDimension(R.dimen.no_player_record_height));
                        _relativePlayRecord.setLayoutParams(lp);
                    }
                    break;
                case 2:
                    updateInfo();
                    break;
            }
        }
    };

    private void updatePlayRecord()
    {
        if(UserLogic.getInstance().checkLoginSuc())
        {
            //已经登录
            UserRestClient.getInstance(getActivity()).getPlayHistroys(_page, _limit, new AbstractRestClient.ResponseCallBack() {
                @Override
                public void onFailure(Response resp, Throwable e) {


                }

                @Override
                public void onSuccess(Response resp) {
                    Object json = resp.getData();
                    String lastJson = (String) SharedPreferencesUtils.getParam(_context,SPFConstant.PLAYRECORD, "");
                    if(lastJson.equals(json.toString())){
                        _toFrushData = false;
                    }else {
                        _toFrushData = true;
                        SharedPreferencesUtils.setParam(_context, SPFConstant.PLAYRECORD, json.toString());
                    }

                    FilmListDatas filmListDatas = new Gson().fromJson(json.toString(), FilmListDatas.class);

                    _data1List = filmListDatas.getData().getData();
                    if (_data1List != null) {
                        _palyRecordList.clear();
                        for(int i = 0; i < _data1List.size(); i++){

                            if(_data1List.get(i).getType() == 2){

                                PlayRecordInfo playRecordInfo = new PlayRecordInfo();

                                playRecordInfo.setId(_data1List.get(i).getId());
                                playRecordInfo.setResource_id(_data1List.get(i).getResource_id());
                                playRecordInfo.setCreated_at(_data1List.get(i).getCreated_at());
                                playRecordInfo.setUser_id(_data1List.get(i).getUser_id());
                                playRecordInfo.setLast_tick(_data1List.get(i).getLast_tick());
                                playRecordInfo.setLast_channel_id(_data1List.get(i).getLast_channel_id());
                                playRecordInfo.setLast_episode_id(_data1List.get(i).getLast_episode_id());
                                playRecordInfo.setType(_data1List.get(i).getType());
                                playRecordInfo.setLast_episode_text(_data1List.get(i).getLast_episode_text());

                                playRecordInfo.setEpisode(_data1List.get(i).getResource().getEpisode());
                                playRecordInfo.setFee(_data1List.get(i).getResource().getFee());
                                playRecordInfo.setImage_url(_data1List.get(i).getResource().getImage_url());
                                playRecordInfo.setSubtitle(_data1List.get(i).getResource().getSubtitle());
                                playRecordInfo.setTag(_data1List.get(i).getResource().getTag());
                                playRecordInfo.setTitle(_data1List.get(i).getResource().getTitle());
                                _palyRecordList.add(playRecordInfo);
                            }

                        }
                        handler.sendEmptyMessage(1);
                    }
                }
            });
        }else {
            _palyRecordList.clear();
            handler.sendEmptyMessage(0);
        }

    }

    private void setGridView() {
        int size = _playRecordAdapter.getCount();
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        _gridViewPlayRecord.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        _gridViewPlayRecord.setColumnWidth(itemWidth); // 设置列表项宽
        _gridViewPlayRecord.setHorizontalSpacing(5); // 设置列表项水平间距
        _gridViewPlayRecord.setStretchMode(GridView.NO_STRETCH);
        _gridViewPlayRecord.setNumColumns(size); // 设置列数量=列表集合数
    }

    @Override
    public void onClick(View v) {
         switch ( v.getId() )
         {
             case R.id.rl_account :
                 if(VRHomeConfig.CUR_RELEASE_TYPE == VRHomeConfig.ReleaseType.ZTE){
                     MainActivity.instance.initZTE();
                 }else {
                     if (!UserLogic.getInstance().checkLoginSuc()) {
                         LoginActivity.actionStart(getActivity());
                     }
                 }
                 break;
             case R.id.rl_setting:
                 MySettingActivity.actionStart(getActivity());
                 break;
             case R.id.rl_VIP:
                 if(UserLogic.getInstance().checkLoginSucAndHasMsg(getActivity()))
                 {
                     ExChangeActivity.actionStart(getActivity());
                 }
                 break;
             case R.id.relative_myCollect:
                 if(UserLogic.getInstance().checkLoginSucAndHasMsg(getActivity()))
                 {
                     MyCollectActivity.actionStart(getActivity());
                 }
                 break;
             case R.id.relative_playRecord:
                 if(UserLogic.getInstance().checkLoginSucAndHasMsg(getActivity()))
                 {
                     Intent intent = new Intent(_context, MyPlayRecordActivity.class);
                     Bundle bundle = new Bundle();
                     bundle.putParcelableArrayList("data", _palyRecordList);
                     intent.putExtras(bundle);
                     startActivity(intent);
                 }
                 break;
             case R.id.rl_feedback:
                 //意见反馈
                 if(UserLogic.getInstance().checkLoginSucAndHasMsg(getActivity()))
                 {
                     FeedbackActivity.actionStart(getActivity());
                 }
                 break;
             case R.id.iv_headpicture:
                 if(VRHomeConfig.CUR_RELEASE_TYPE != VRHomeConfig.ReleaseType.ZTE)
                    startActivity(new Intent(_context,SelectPicPopupWindow.class));
                 else
                     MainActivity.instance.initZTE();
                 break;
             case R.id.vip_limit:
                 if(UserLogic.getInstance().checkLoginSucAndHasMsg(getActivity()))
                 {
                     ExChangeActivity.actionStart(getActivity());
                 }
                 break;
             default:break;
         }
    }

    public static void uploadHeadPicture(Bitmap bitmap){
        if(_headImageView != null)
            _headImageView.setImageBitmap(bitmap);

        saveBitmapFile(bitmap);

        UserRestClient.getInstance(_context).uploadAvatar(path, new AbstractRestClient.StringResponseCallback() {
            @Override
            public void onFailure(String resp, Throwable e) {

            }

            @Override
            public void onSuccess(String resp) {
                String url = Constant.SERVER_DOMAIN_MAIN + "/api/me/avatar" + "?avatar=" + resp;
                UserRestClient.getInstance(_context).put(url, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        });

    }

    /**
     * Bitmap对象保存味图片文件
     */
    private static String path;
    private static void saveBitmapFile(Bitmap bitmap){
        path = _context.getFilesDir().getAbsolutePath() + "/img.jpg";
        File file=new File(path);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(_context); //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(_context);
    }

}
