package com.vrseen.vrstore.fragment.channel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.GridView;

import com.umeng.analytics.MobclickAgent;
import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.adapter.channel.ChannelAdapter;
import com.vrseen.vrstore.fragment.BaseFragment;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.model.channel.Channel;
import com.vrseen.vrstore.model.channel.ChannelData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.view.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.util.NetworkStatusManager;

/**
 * Created by jiangs on 16/4/29.
 */
public class ChannelFragment extends BaseFragment implements View.OnClickListener {

    public static final String FILM_CHANNEL = "video";
    public static final String PANO_CHANNEL = "pano";
    public static final String LIVE_CHANNEL = "live";

    private View _thisFragment;
    private MainActivity _context;
    private ProgressRelativeLayout _progressRelativeLayout;
    private ChannelAdapter _channelAdapter;
    private GridView _gridView;
    private boolean _initData;
    private Context  mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.setPageName("ChannelFragment");
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();
        if (_thisFragment == null) {
            _thisFragment = inflater.inflate(R.layout.fragment_channel, null);
            initView();
        } else {
            // 从parent删除
            ViewGroup parent = (ViewGroup) _thisFragment.getParent();
            if (parent != null) {
                parent.removeView(_thisFragment);
            }
        }
        _context = (MainActivity) getContext();
        return _thisFragment;

    }

    private void initView() {
        _gridView = (GridView) _thisFragment.findViewById(R.id.gridview);
        _progressRelativeLayout = (ProgressRelativeLayout)_thisFragment.findViewById(R.id.progress_layout);
    }


    @Override
    public void onClick(View v) {

    }


    /**
     * 频道首页数据
     */
    private void requestChannels() {

        if(_initData == true)
        {
            return;
        }

        if(!NetworkStatusManager.getInstance(this.getContext()).isNetworkConnectedHasMsg(false))
        {
            _progressRelativeLayout.showErrorText(getActivity().getString(R.string.get_recommend_fail));
            return;
        }

        _progressRelativeLayout.showProgress();
        CommonRestClient.getInstance(getActivity()).getChannels(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                String msg = getString(R.string.get_recommend_fail);
                CommonUtils.showResponseMessage(getActivity(), resp, e, msg);
                _progressRelativeLayout.showErrorText(msg);
            }

            @Override
            public void onSuccess(Response resp) {
                ChannelData channelData = (ChannelData) resp.getModel();

                // FIXME: 2016/6/15 ，临时只显示影视和全景中国,暂时不去设置常量video和pano，mll
                List<Channel> tempList = new ArrayList<Channel>();
                for (int i = 0; i < channelData.getData().size(); i++) {
                    if(channelData.getData().get(i).getChannel().equals(FILM_CHANNEL)
                            ||channelData.getData().get(i).getChannel().equals(PANO_CHANNEL)
                            ||channelData.getData().get(i).getChannel().equals(LIVE_CHANNEL))
                   {
                        tempList.add(channelData.getData().get(i));
                   }
                }

                if (channelData != null) {
                    _channelAdapter = new ChannelAdapter(getActivity(), tempList);
                    _gridView.setAdapter(_channelAdapter);
                }
                _progressRelativeLayout.showContent();
                _initData = true;
            }
        });
    }

  /*  @Override
    public void onResume() {
        super.onResume();
        requestChannels();
    }


    @Override
    public void onPause() {
        super.onPause();
    }*/
  public void onResume() {
      super.onResume();
      requestChannels();
      MobclickAgent.onResume(mContext);       //统计时长
  }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }

}
