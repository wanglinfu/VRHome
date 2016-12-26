package com.vrseen.vrstore.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * 完全展开的GridView，高度为所有子节点的高度之和，
 * 重写onMeasure方法，解决在ScrollView中只能显示一行的问题
 * Created by jiangs on 15/11/13.
 */
public class ExpGridView extends GridView{
    public ExpGridView(Context context) {
        super(context);
    }

    public ExpGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate entire height by providing a very large height hint.
        // View.MEASURED_SIZE_MASK represents the largest height possible.
        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
