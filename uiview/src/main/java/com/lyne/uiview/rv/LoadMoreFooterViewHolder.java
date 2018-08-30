package com.lyne.uiview.rv;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lyne.uiview.R;


/**
 * Created by tiny  on 2016/11/25 16:13.
 * Desc:
 */

public class LoadMoreFooterViewHolder extends BaseViewHolder implements PullUpLoader.OnLoadListener{

    private TextView textMore;
    private View progressBarLayout;
    private TextView textFinish;

    private PullUpLoader.OnLoadListener mLoaderListener;
    private PullUpLoader mLoader;
    private boolean isLoadingMore;
    public static LoadMoreFooterViewHolder create(RecyclerView recyclerView, PullUpLoader.OnLoadListener listener) {

        LoadMoreFooterViewHolder holder = new LoadMoreFooterViewHolder(recyclerView);
        holder.setPullLoaderListener(listener);
        return holder;
    }

    private LoadMoreFooterViewHolder(RecyclerView recyclerView) {
        super(View.inflate(recyclerView.getContext(), R.layout.view_refresh_list_footer, null));
        setRecyclerView(recyclerView);
        textMore = itemView.findViewById(R.id.text_more);
        textFinish = itemView.findViewById(R.id.text_finish);
        progressBarLayout = itemView.findViewById(R.id.load_progress_bar_layout);
    }

    @Override
    public void onLoadMore() {
        startLoadingMore();
        if(mLoaderListener!=null){
            mLoaderListener.onLoadMore();
        }
    }
    private void startLoadingMore() {
        mLoader.setLoading(true);
        isLoadingMore = true;
        textMore.setVisibility(View.GONE);
        textFinish.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void setPullLoaderListener(PullUpLoader.OnLoadListener loaderListener) {
        this.mLoaderListener = loaderListener;
        mLoader=PullUpLoader.bind(getRecyclerView(), this);
    }

    public void finishLoadMore(boolean enable) {
        mLoader.setLoading(false);
        isLoadingMore=false;
        progressBarLayout.setVisibility(View.GONE);
        mLoader.setLoadMoreEnable(enable);
        if(enable){
            enableLoadMore();
        }else {
            disableLoadMore();
        }
    }

    private void enableLoadMore() {
        textMore.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.GONE);
        textFinish.setVisibility(View.GONE);
    }

    private void disableLoadMore() {
        textMore.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
        textFinish.setVisibility(View.GONE);
    }
    public boolean canLoadMore(){
        return mLoader.canLoadMore();
    }

    @Override
    public void bindView(int mViewType) {

    }

    @Override
    public void bindData(int position, Object mItem, int mViewType) {

    }

}
