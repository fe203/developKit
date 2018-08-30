package com.lyne.uiview.rv;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tiny  on 2016/7/14 12:55.
 * Desc:覆盖掉FadeInAnimator的删除动画
 */
public class NoFadeOutAnimator extends FadeInAnimator {


    @Override
    protected void preAnimateRemove(RecyclerView.ViewHolder holder) {
//        super.preAnimateRemove(holder);
    }

    @Override
    protected void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
        super.preAnimateRemoveImpl(holder);

    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        super.preAnimateAddImpl(holder);
        holder.itemView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder holder) {
        holder.itemView.setAlpha(0);
//        super.animateRemoveImpl(holder);
    }
}
