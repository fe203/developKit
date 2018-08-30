package com.lyne.uiview.rv;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tiny on 2015/10/14.
 */
public class ListDivider extends RecyclerView.ItemDecoration {

    int height;
    boolean hideFirstDivider;

    public ListDivider(int height) {
        this.height=height;
    }

    public ListDivider(int height, boolean hideFirstDivider){
        this.height=height;
        this.hideFirstDivider = hideFirstDivider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        outRect.set(0, 0, 0, (hideFirstDivider && position == 0) ? 0 : height);
    }
}
