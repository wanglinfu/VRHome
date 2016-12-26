package com.vrseen.vrstore.adapter.find;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FindPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> _list;
    public FindPagerAdapter(FragmentManager fm , List<Fragment> list) {
        super(fm);
        _list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return _list.get(position);
    }

    @Override
    public int getCount() {
        return _list.size();
    }
}
