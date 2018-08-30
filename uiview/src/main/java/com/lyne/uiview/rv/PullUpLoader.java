package com.lyne.uiview.rv;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by tiny  on 2016/10/25 17:06.
 * Desc:用于RecyclerView的上啦加载更多
 */

public class PullUpLoader {

    static final String TAG = "OnLastItemVisibleListener";
    RecyclerView mRecyclerView;

    boolean mLastItemVisible;

    OnLoadListener mListener;

    boolean enable=true;

    boolean isRefreshing;



    private PullUpLoader(RecyclerView mRecyclerView, OnLoadListener listener) {
        this.mRecyclerView = mRecyclerView;
        mRecyclerView.addOnScrollListener(mScrollListener);
        this.mListener=listener;

    }
    private void assetLayoutManager(){
        RecyclerView.LayoutManager lm= mRecyclerView.getLayoutManager();
        if(!(lm instanceof LinearLayoutManager)){
            throw new IllegalArgumentException("The RecyclerView must have a LinearLayoutManager");
        }
    }
    public void setLoadMoreEnable(boolean enable){
        this.enable=enable;
    }
    public static PullUpLoader bind(RecyclerView recyclerView, OnLoadListener listener){
        return new PullUpLoader(recyclerView,listener);
    }

    public void setLoading(boolean isRefreshing){
        this.isRefreshing=isRefreshing;
    }
    public boolean isLoading(){
        return isRefreshing;
    }
    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    assetLayoutManager();
                    int i = recyclerView.getAdapter().getItemCount() - 1;
                    LinearLayoutManager lm= ((LinearLayoutManager) recyclerView.getLayoutManager());
                    int last=lm.findLastVisibleItemPosition();
                    if (i <= last) {
                        if(mListener!=null&&enable&&!isLoading()){
                            mListener.onLoadMore();
                        }
                    }
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

        }
    };
    public boolean canLoadMore(){
        return enable;
    }

    ;
    public interface OnLoadListener{
        void onLoadMore();
    }

}
