package com.lyne.uiview.rv;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;


/**
 *
 * Created by Administrator on 2015/5/5.
 * 尽量构造方法别带其他参数，否则adapter里通过反射构造会失败
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder  {


    protected RecyclerView mRecyclerView;

    private int mViewType;

    private T mCurrentItem;


    public BaseViewHolder(View itemView) {
        super(itemView);
    }



    public View getItemView() {
        return super.itemView;
    }


    public void setRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView=mRecyclerView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public final <T extends View> T findViewById(@IdRes int id) {
        return itemView.findViewById(id);
    }


    public Context getContext(){
        return itemView.getContext();
    }


    final void bindItemView(int mViewType){
        bindViewInternal(mViewType);
        bindView(mViewType);
    }

    final void bindItemData(int position, T mItem, int mViewType, List<Object> payLoads){
        bindDataInternal(position, mItem, mViewType);
        if (payLoads == null || payLoads.isEmpty()){
            bindData(position, mItem, mViewType);
        }else {
            bindData(position, mItem, mViewType, payLoads);
        }
    }

    protected void bindViewInternal(int mViewType){
       this.mViewType=mViewType;
    }


    protected  void bindDataInternal(int position, T mItem, int mViewType){
        this.mCurrentItem =mItem;
    }

    public String getString(int id){
        return getContext().getString(id);
    }
    public T getCurrnetData(){
        return mCurrentItem;
    }
    public abstract void bindView(int mViewType);

    public abstract void bindData(int position, T mItem, int mViewType);

    public void bindData(int position, T mItem, int mViewType, List<Object> payLoads){};


    public Resources getResources(){
        return getContext().getResources();
    }


}
