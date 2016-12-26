/**
 *
 */
package com.vrseen.vrstore.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 完全展开的listview，高度为所有子节点的高度之和，重写onMeasure方法，解决在ScrollView中只能显示一行的问题
 *
 * @author jiangs
 * @ClassName ExpListView
 * @QQ 826565702
 * @date 2014-3-27上午11:03:47
 */
public class ExpListView extends ListView {

    public ExpListView(Context context) {
        super(context);
    }

    public ExpListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightSpec);
    }


}