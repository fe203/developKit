package com.lyne.uiview.rv;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator  on 2016/9/16 17:36.
 * Desc:
 */

public abstract class GridDividerDecoration extends RecyclerView.ItemDecoration {


    int color;
    DividerDrawable mDivider;


    public GridDividerDecoration(int color, int width) {
        this.color = color;
        this.mDivider = new DividerDrawable(color, width);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int childCount = parent.getChildCount();
        RecyclerView.Adapter adapter = parent.getAdapter();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int positionInAdapter = parent.getChildAdapterPosition(child);
            int viewType = adapter.getItemViewType(positionInAdapter);
            onPreDrawDivider(mDivider, positionInAdapter, viewType);

            int left = child.getLeft();
            int right = child.getRight();
            int top = child.getTop();
            int bottom = child.getBottom();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public abstract void onPreDrawDivider(DividerDrawable dividerDrawable, int positionInAdapter, int viewType);


}