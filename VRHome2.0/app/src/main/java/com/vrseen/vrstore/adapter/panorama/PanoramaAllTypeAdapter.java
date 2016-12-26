package com.vrseen.vrstore.adapter.panorama;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.model.panorama.PanoramaAllTypeData;
import com.vrseen.vrstore.model.panorama.PanoramaCategoryData;
import com.vrseen.vrstore.model.panorama.PanoramaCollectionData;

import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * 项目名称：VRHome2.0
 * 类描述：
 * 创建人：admin
 * 创建时间：2016/6/5 16:13
 * 修改人：admin
 * 修改时间：2016/6/5 16:13
 * 修改备注：
 */
public class PanoramaAllTypeAdapter extends BaseAdapter {

    private List<PanoramaAllTypeData> _listCategory;
    private LayoutInflater _layoutInflater;
    private Context _context;

    public PanoramaAllTypeAdapter(Context context,List<PanoramaAllTypeData> listCategory){
        this._context=context;
        this._listCategory=listCategory;
        _layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if(_listCategory!=null && _listCategory.size()>0){
            return _listCategory.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return _listCategory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = _layoutInflater.inflate(R.layout.item_panorama_city, parent, false);
            holder.loadViews(convertView);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fill(_listCategory.get(position));

        if(position==0){
            holder.categoryNameTextView.setTextColor(_context.getResources().getColor(R.color.common_app_color));
        }else{
            holder.categoryNameTextView.setTextColor(_context.getResources().getColor(R.color.contentColor));
        }

        return convertView;
    }

    class ViewHolder {
        TextView categoryIdTextView;//类型id
        TextView categoryNameTextView;//类型名

        public void loadViews(View convertView) {
            categoryNameTextView = (TextView) convertView.findViewById(R.id.tv_city_item_name);

            categoryIdTextView = (TextView) convertView.findViewById(R.id.tv_city_item_id);

        }

        public void fill(PanoramaAllTypeData patd) {
            categoryNameTextView.setText(patd.getTitle());
        }

    }

}
