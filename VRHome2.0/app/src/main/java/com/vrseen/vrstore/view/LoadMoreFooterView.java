package com.vrseen.vrstore.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.util.ToastUtils;

import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreUIHandler;

/**
 * 项目名称：VRHome2.0
 * 类描述：
 * 创建人：admin
 * 创建时间：2016/6/20 13:20
 * 修改人：admin
 * 修改时间：2016/6/20 13:20
 * 修改备注：
 */
public class LoadMoreFooterView extends RelativeLayout
        implements LoadMoreUIHandler {

        private TextView _footerViewTextView;

        public LoadMoreFooterView(Context context) {
            super(context);
            initView();

        }

        private void initView(){
            LayoutInflater.from(getContext()).inflate(R.layout.item_list_or_grid_footerview, this);
            _footerViewTextView=(TextView)findViewById(R.id.tv_footer_textview);

        }

        @Override
        public void onLoading(LoadMoreContainer container) {

            setVisibility(VISIBLE);
            _footerViewTextView.setText("正在努力加载中...");

        }

        @Override
        public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore) {

            if (!hasMore) {
                setVisibility(VISIBLE);
                if (empty) {
                    _footerViewTextView.setText("数据已加载完成");
                } else {
                    _footerViewTextView.setText("数据已加载完成");
    //                ToastUtils.show(getContext(),"已加载全部数据",1000);
                }
            } else {
                setVisibility(INVISIBLE);
            }

        }

        @Override
        public void onWaitToLoadMore(LoadMoreContainer container) {
            setVisibility(VISIBLE);
            _footerViewTextView.setText("点击加载更多");
        }

        @Override
        public void onLoadError(LoadMoreContainer container, int errorCode, String errorMessage) {
            _footerViewTextView.setText("加载失败，点击重新加载");
        }
}
