package com.vrseen.vrstore.adapter.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vrseen.vrstore.fragment.app.BaseCategoryFragment;

import java.util.ArrayList;

public class CommonFragmentAdapter extends FragmentPagerAdapter {

	private ArrayList<BaseCategoryFragment> fragments;

	public CommonFragmentAdapter(FragmentManager fm, ArrayList<BaseCategoryFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {

		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
