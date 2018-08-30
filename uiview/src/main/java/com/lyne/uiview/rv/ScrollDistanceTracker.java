package com.lyne.uiview.rv;

import android.support.v7.widget.RecyclerView;

/**
 * Created by tiny  on 2016/11/10 11:27.
 * Desc:用于监听RecyclerView的滚动
 */

public class ScrollDistanceTracker {

    RecyclerView mRecyclerView;
    OnDistanceTracker mTracker;
    float mMinOffset;

    private ScrollDistanceTracker(RecyclerView recyclerView, OnDistanceTracker tracker, float minOffset){
        this.mRecyclerView=recyclerView;
        this.mTracker=tracker;
        this.mMinOffset=minOffset;
        mRecyclerView.addOnScrollListener(mListener);

    }

    private RecyclerView.OnScrollListener mListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(dy>0&&dy>mMinOffset){
                mTracker.onScrollUp();
                return;
            }
            if(dy<0&& Math.abs(dy)> mMinOffset){
                mTracker.onScrollDown();
                return;
            }
        }
    };
    public static ScrollDistanceTracker create(RecyclerView recyclerView, OnDistanceTracker  tracker, float minOffset){
            return new ScrollDistanceTracker(recyclerView,tracker,minOffset);
    }

    public interface OnDistanceTracker{
        void onScrollUp();
        void onScrollDown();
    }
}
