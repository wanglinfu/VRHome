package com.vrseen.vrstore.view;


import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class ScrollableHelper {

    private View mCurrentScrollableView;

    private ScrollableContainer mCurrentScrollableCainer;

    private int sysVersion = android.os.Build.VERSION.SDK_INT;

    /**
     * a viewgroup whitch contains ScrollView/ListView/RecycelerView..
     */
    public interface ScrollableContainer {
        /**
         * @return ScrollView/ListView/RecycelerView..'s instance
         */
        View getScrollableView();
    }

    public void setCurrentScrollableContainer(ScrollableContainer scrollableContainer) {
        this.mCurrentScrollableCainer = scrollableContainer;
    }

    public void setCurrentScrollableContainer(View view) {
        this.mCurrentScrollableView = view;
    }

    private View getScrollableView() {
        if (mCurrentScrollableCainer == null) {
            return mCurrentScrollableView;
        }
        return mCurrentScrollableCainer.getScrollableView();
    }

    public void setScrollableView(View view){
        this.mCurrentScrollableView=view;
    }

    public boolean isTop() {
       View scrollableView = getScrollableView();
        if (scrollableView == null) {

            return false;
           // throw new NullPointerException("You should call ScrollableHelper.setCurrentScrollableContainer() to set ScrollableContainer.");
        }
        if (scrollableView instanceof AdapterView) {
            return isAdapterViewTop((AdapterView) scrollableView);
        }
        if (scrollableView instanceof ScrollView) {
            return isScrollViewTop((ScrollView) scrollableView);
        }
        if (scrollableView instanceof WebView) {
            return isWebViewTop((WebView) scrollableView);
        }

        if (scrollableView instanceof GridViewWithHeaderAndFooter) {
            return isGridViewTop((GridViewWithHeaderAndFooter) scrollableView);
        }

        throw new IllegalStateException("scrollableView must be a instance of AdapterView|ScrollView|RecyclerView");
    }

    private static boolean isAdapterViewTop(AdapterView adapterView) {
        if (adapterView != null) {
            int firstVisiblePosition = adapterView.getFirstVisiblePosition();
            View childAt = adapterView.getChildAt(0);
            if (childAt == null || (firstVisiblePosition == 0 && childAt != null && childAt.getTop() == 0)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isGridViewTop(GridViewWithHeaderAndFooter gridViewWithHeaderAndFooter) {
        if (gridViewWithHeaderAndFooter != null) {
            int firstVisiblePosition = gridViewWithHeaderAndFooter.getFirstVisiblePosition();
            View childAt = gridViewWithHeaderAndFooter.getChildAt(0);
            if (childAt == null || (firstVisiblePosition == 0 && childAt != null && childAt.getTop() == 0)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isScrollViewTop(ScrollView scrollView) {
        if (scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    private static boolean isWebViewTop(WebView scrollView){
        if(scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    @SuppressLint("NewApi")
    public void smoothScrollBy(int velocityY, int distance, int duration) {
        View scrollableView = getScrollableView();
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            if (sysVersion >= 21) {
                absListView.fling(velocityY);
            } else {
                absListView.smoothScrollBy(distance, duration);
            }
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocityY);
        }
    }
}
