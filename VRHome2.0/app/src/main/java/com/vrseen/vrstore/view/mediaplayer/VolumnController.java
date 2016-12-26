package com.vrseen.vrstore.view.mediaplayer;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vrseen.vrstore.R;

public class VolumnController {
	private static Toast t;
	private VolumnView tv;

	private Context context;
	private int picID;
	private String text;

	public VolumnController(Context context, int picID, String text) {
		this.context = context;
		this.picID = picID;
		this.text = text;
	}

	public void show(float progress) {
		if (t == null) {
			t = new Toast(context);
		}
		View layout = LayoutInflater.from(context). inflate(R.layout.volumn_layout, null);
		tv = (VolumnView) layout.findViewById(R.id.volumnView);
		t.setView(layout);
		t.setGravity(Gravity.BOTTOM, 0, 100);
		t.setDuration(Toast.LENGTH_SHORT);

		tv.setPicID(picID);
		tv.setText(text);
		tv.setProgress(progress);
		t.show();
	}
}
