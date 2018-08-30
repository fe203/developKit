package com.lyne.uiview.rv;

import android.view.View;

/**
 * Created by Administrator on 2015/5/22.
 */
public interface OnItemClickListener {

    /**
     * @param mItemView
     * @param position if the recyclerView contains a headView,the position had bean regulated.
     *                 so you can find item with this position .
     */
    void onItemClick(View mItemView, int position);
}
