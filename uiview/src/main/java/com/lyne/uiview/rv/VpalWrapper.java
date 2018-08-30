package com.lyne.uiview.rv;

import android.animation.Animator;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;

/**
 * Created by tiny on 2016/7/4.
 */
public class VpalWrapper implements Animator.AnimatorListener{

    final ViewPropertyAnimatorListener listener;
    final View view;
    public VpalWrapper(View view, ViewPropertyAnimatorListener listener){
        this.listener=listener;
        this.view=view;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        listener.onAnimationStart(view);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        listener.onAnimationEnd(view);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        listener.onAnimationCancel(view);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
