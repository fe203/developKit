package com.lyne.uiview.rv;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tiny  on 2016/10/22 11:01.
 * Desc:用于监控RecyclerView的滚动，以处理类似社群主页这样的效果
 */

public class ScrollTracker<V extends View> {


    private V mView;
    RecyclerView mRecyclerView;
    NestedScrollView mScrollView;
    private int mRecyclerViewDy;

    float mMinDy;
    float mMaxDy;

    onScrollChangedListener<V> mTracker;
    /**
     * minDy必须小于maxDy
     *
     * @param recyclerView
     * @param view
     * @param minDy        最小滑动的距离，超过这个距离才会对 view 产生影响
     * @param <V>
     * @return
     */
    public static <V extends View> ScrollTracker<V> track(RecyclerView recyclerView, V view, int minDy, int maxDy, onScrollChangedListener<V> tracker) {

        return new ScrollTracker<V>(recyclerView, view, minDy, maxDy,tracker);
    }

    public static <V extends View> ScrollTracker<V> track(NestedScrollView scrollView, V view, int minDy, int maxDy, onScrollChangedListener<V> tracker) {

        return new ScrollTracker<V>(scrollView, view, minDy, maxDy,tracker);
    }

    private ScrollTracker(RecyclerView rv, V view, int minDy, int maxDy, onScrollChangedListener<V> tracker) {
        this.mRecyclerView = rv;
        this.mView = view;
        mRecyclerView.addOnScrollListener(mListener);
        this.mMinDy = minDy;
        this.mMaxDy = maxDy;
        mTracker=tracker;
    }
    private ScrollTracker(NestedScrollView scrollView, V view, int minDy, int maxDy, onScrollChangedListener<V> tracker) {
        this.mScrollView = scrollView;
        this.mView = view;
        this.mMinDy = minDy;
        this.mMaxDy = maxDy;
        mTracker=tracker;
        mScrollView.setOnScrollChangeListener(mScrollListener);
    }


    private NestedScrollView.OnScrollChangeListener mScrollListener=new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            float factor = 0;
            if (scrollY > mMaxDy) {
                factor = 1;
            } else if (scrollY < mMinDy) {
                factor = 0;
            } else {
                factor = (scrollY - mMinDy) / (mMaxDy - mMinDy);
            }
            if(mTracker!=null){
                mTracker.onScroll(factor,mView,scrollY);
            }
        }
    };

    private RecyclerView.OnScrollListener mListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mRecyclerViewDy = mRecyclerViewDy + dy;
            float factor = 0;
            if (mRecyclerViewDy > mMaxDy) {
                factor = 1;
            } else if (mRecyclerViewDy < mMinDy) {
                factor = 0;
            } else {
                factor = (mRecyclerViewDy - mMinDy) / (mMaxDy - mMinDy);
            }
            if(mTracker!=null){
                mTracker.onScroll(factor,mView,mRecyclerViewDy);
            }
//            Log.d("ScrollTracker", "min = " + mMinDy + " , max = " + mMaxDy);
//            Log.d("ScrollTracker", "scroll y = " + mRecyclerViewDy);
//            Log.d("ScrollTracker", "factor = " + factor);

        }
    };
    public interface onScrollChangedListener<V extends View>{
        void onScroll(float factor, V view, int scrollY);
    }
}
