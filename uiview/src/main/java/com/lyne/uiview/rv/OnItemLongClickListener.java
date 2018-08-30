package com.lyne.uiview.rv;

import android.view.View;

/**
 * Created by Administrator on 2015/5/22.
 */
public interface OnItemLongClickListener {

    /**
     * @param mItemView
     * @param position if the recyclerView contains a headView,the position had bean regulated.
     *                 so you can find item with this position .
     */
    void onItemLongClick(View mItemView, int position);
}
