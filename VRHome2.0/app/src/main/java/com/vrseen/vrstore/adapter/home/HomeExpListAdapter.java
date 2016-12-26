package com.vrseen.vrstore.adapter.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vrseen.vrstore.MainActivity;
import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.film.FilmActivity;
import com.vrseen.vrstore.activity.panorama.PanoramaActivity;
import com.vrseen.vrstore.model.Home.HomeData;
import com.vrseen.vrstore.view.ExpGridView;

import java.util.List;

/**
 * Created by jiangs on 16/5/4.
 */
public class HomeExpListAdapter extends BaseExpandableListAdapter {
    private List<HomeData.GroupData> homeData;
    LayoutInflater inflater;
    private Context _context;
    private HomeGridAdapter homeGridAdapter;

    public HomeExpListAdapter(Context context, List<HomeData.GroupData> homeData) {
        this.homeData = homeData;
        _context= context;
        inflater = LayoutInflater.from(context);


    }

    @Override
    public int getGroupCount() {
        if (homeData != null && homeData.size() > 0)
            return homeData.size();
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return homeData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return homeData.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expendlist_item_group, null);
            holder = new GroupViewHolder();
            holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            holder.viewMoreTextView = (TextView) convertView.findViewById(R.id.tv_viewMore);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        holder.tv_type.setText(homeData.get(groupPosition).getTitle());
        final int positon = groupPosition;

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expendlist_item_child, null);
            holder = new ChildViewHolder();
            holder.gridview = (ExpGridView) convertView.findViewById(R.id.gridview);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.gridview.setHorizontalSpacing(20);
        holder.gridview.setVerticalSpacing(20);
        int style = homeData.get(groupPosition).getStyle();
//        holder.tv_type.setText(homeDataList.get(groupPosition).getGroupName());
        if (style == HomeGridAdapter.TYPE_MOVIE) {
            holder.gridview.setNumColumns(3);

        } else if (style == HomeGridAdapter.TYPE_QUJIN) {
            holder.gridview.setNumColumns(2);

        } else if (style == HomeGridAdapter.TYPE_APP) {
            holder.gridview.setNumColumns(4);
        }

        homeGridAdapter = new HomeGridAdapter(_context, homeData.get(groupPosition));
        holder.gridview.setAdapter(homeGridAdapter);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    class GroupViewHolder {
        TextView tv_type;
        TextView viewMoreTextView;
    }


    class ChildViewHolder {
        ExpGridView gridview;
        RelativeLayout rl_root;
    }
}
