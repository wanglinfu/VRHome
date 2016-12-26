/**
 *
 */
package com.vrseen.vrstore.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 重写onMeasure方法，解决GridView在ScrollView中只能显示一行的问题
 *
 * @author Vinda.Z
 * @ClassName XGridView
 * @QQ 443550101
 * @Email vinda.z@outlook.com
 * @date 2014-3-20下午9:42:33
 */
public class XGridView extends GridView {
    public XGridView(Context context) {
        super(context);
    }

    public XGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;
        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
        } else {
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}