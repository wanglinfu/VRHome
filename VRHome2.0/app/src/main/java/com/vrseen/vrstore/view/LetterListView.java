package com.vrseen.vrstore.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.vrseen.vrstore.R;

public class LetterListView extends View {

	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
			"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
			"Y", "Z" ,""};
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;

	public LetterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LetterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LetterListView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int height = getHeight();
		int width = getWidth();

		paint.setColor(getResources().getColor(R.color.black));
//			canvas.drawColor(getResources().getColor(R.color.black));
		RectF outerRect = new RectF(0, 0, width, height);
		canvas.drawRoundRect(outerRect,width/2 ,width/2,paint);

		int singleHeight = height / b.length;
		for (int i = 0; i < b.length; i++) {
			paint.setColor(getResources().getColor(R.color.white));
			paint.setTextSize(30);
			paint.setAntiAlias(true);
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			if(i==b.length-1){
				yPos = singleHeight * i + singleHeight/2;
				canvas.drawText(b[i], xPos, yPos, paint);
			}else {
				canvas.drawText(b[i], xPos, yPos, paint);
			}
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * b.length);
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				showBkg = true;
				if (oldChoose != c && listener != null) {
					if (c >= 0 && c < b.length) {
						listener.onTouchingLetterChanged(b[c]);
						choose = c;
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (oldChoose != c && listener != null) {
					if (c >= 0 && c < b.length) {
						listener.onTouchingLetterChanged(b[c]);
						choose = c;
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				showBkg = false;
				choose = -1;
				invalidate();
				break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}
}
