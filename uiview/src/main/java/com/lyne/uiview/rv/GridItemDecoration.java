package com.lyne.uiview.rv;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by liht on 2018/1/2.
 */

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int top, bottom, hSpace, vSpace;

    private GridItemDecoration(){

    }

    public static class Builder{
        private int top, bottom, hSpace, vSpace;

        public Builder(){
            top = 0;
            bottom = 0;
            hSpace = 0;
            vSpace = 0;
        }

        public Builder setTop(Context context, int topId){
            if (topId > 0){
                top = context.getResources().getDimensionPixelOffset(topId);
            }
            return this;
        }

        public Builder setBottom(Context context, int bottomId){
            if (bottomId > 0){
                bottom = context.getResources().getDimensionPixelOffset(bottomId);
            }
            return this;
        }

        public Builder setHSpace(Context context, int hSpaceId){
            if (hSpaceId > 0){
                hSpace = context.getResources().getDimensionPixelOffset(hSpaceId);
            }
            return this;
        }

        public Builder setVSpace(Context context, int vSpaceId){
            if (vSpaceId > 0){
                vSpace = context.getResources().getDimensionPixelOffset(vSpaceId);
            }
            return this;
        }

        public Builder setVSpaceHeight(int vSpaceHeight){
            if (vSpaceHeight >= 0){
                vSpace = vSpaceHeight;
            }
            return this;
        }

        public Builder setTopHeight(int topHeight){
            if (topHeight >= 0){
                top = topHeight;
            }
            return this;
        }

        public GridItemDecoration build(){
            GridItemDecoration decoration = new GridItemDecoration();
            decoration.top = this.top;
            decoration.bottom = this.bottom;
            decoration.hSpace = this.hSpace;
            decoration.vSpace = this.vSpace;
            return decoration;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int position = parent.getChildAdapterPosition(view);

        int column = position % spanCount;
        outRect.left = column * hSpace / spanCount;
        outRect.right = hSpace - (column + 1) * hSpace / spanCount;
        outRect.top = isTop(parent, spanCount, childCount, position) ? top : 0;
        outRect.bottom = isBottom(parent, spanCount, childCount, position) ? bottom : vSpace;
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager){
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }



    //第一行
    private boolean isTop(RecyclerView parent, int spanCount, int childCount, int position) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return position < spanCount;
        }
        return false;
    }

    //最后一行
    private boolean isBottom(RecyclerView parent, int spanCount, int childCount, int position){
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int allRows = (childCount - 1) / spanCount + 1;
            int tempRows = position / spanCount + 1;
            return allRows == tempRows;
        }
        return false;
    }

    //第一列
    private boolean isLeft(RecyclerView parent, int spanCount, int childCount, int position) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return position % spanCount == 0;
        }
        return false;
    }

    //最后一列
    private boolean isRight(RecyclerView parent, int spanCount, int childCount, int position){
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return position % spanCount == (spanCount - 1);
        }
        return false;
    }
}
