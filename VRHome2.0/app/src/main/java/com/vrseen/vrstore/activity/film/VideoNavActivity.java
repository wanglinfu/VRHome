package com.vrseen.vrstore.activity.film;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.adapter.film.FilmNavAdapter;
import com.vrseen.vrstore.http.AbstractRestClient;
import com.vrseen.vrstore.http.CommonRestClient;
import com.vrseen.vrstore.http.Response;
import com.vrseen.vrstore.model.film.FilmCateroryData;
import com.vrseen.vrstore.util.CommonUtils;

import java.util.List;

/**
 * Created by jiangs on 16/5/16.
 */
public class VideoNavActivity extends BaseActivity implements View.OnClickListener {
    public static final String KEY_POSITION = "KEY_POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("VideoNavActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_nav);
        initView();
    }

    private GridView _gridview;
    private View _viewBack;

    protected void initView() {
        _gridview = (GridView) findViewById(R.id.gridview);
        _viewBack = findViewById(R.id.view_back);
        _viewBack.setOnClickListener(this);
        _gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(KEY_POSITION, position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        requestVideoCategory();

    }


    private FilmNavAdapter filmNavAdapter;
    private List<FilmCateroryData.Category> categories;

    private void requestVideoCategory() {
        CommonRestClient.getInstance(this).getVideoCategory(new AbstractRestClient.ResponseCallBack() {
            @Override
            public void onFailure(Response resp, Throwable e) {
                CommonUtils.showResponseMessage(VideoNavActivity.this, resp, e, getResources().getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(Response resp) {
                FilmCateroryData filmCateroryData = (FilmCateroryData) resp.getModel();
                if (filmCateroryData != null && filmCateroryData.getData() != null && filmCateroryData.getData().size() > 0) {
                    categories = filmCateroryData.getData();
                    filmNavAdapter = new FilmNavAdapter(VideoNavActivity.this, categories);
                    _gridview.setAdapter(filmNavAdapter);

                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.view_back) {
            finish();
        }
    }

}
