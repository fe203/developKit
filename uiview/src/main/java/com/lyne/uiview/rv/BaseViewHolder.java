package com.lyne.uiview.rv;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 *
 * Created by Administrator on 2015/5/5.
 * 尽量构造方法别带其他参数，否则adapter里通过反射构造会失败
 */
public abstract  class BaseViewHolder<T> extends RecyclerView.ViewHolder  {


    protected RecyclerView mRecyclerView;


    private int mViewType;


    private T mCurrnetItem;




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

    public TextView findTextView(int id) {
        return (TextView) (getItemView().findViewById(id));
    }

    public ImageView findImageView(int id) {
        return (ImageView) (getItemView().findViewById(id));
    }

    public View findViewById(int id){
        return  itemView.findViewById(id);
    }




    public Context getContext(){
        return itemView.getContext();
    }


    final void bindItemView(int mViewType){
        bindViewInternal(mViewType);
        bindView(mViewType);
    }
    final void bindItemData(int position, T mItem, int mViewType){
        bindDataInternal(position, mItem, mViewType);
        bindData(position, mItem, mViewType);
    }

    protected void bindViewInternal(int mViewType){
       this.mViewType=mViewType;
    }


    protected  void bindDataInternal(int position, T mItem, int mViewType){
        this.mCurrnetItem=mItem;
    }

    public String getString(int id){
        return getContext().getString(id);
    }
    public T getCurrnetData(){
        return mCurrnetItem;
    }
    public abstract void bindView(int mViewType);

    public abstract void bindData(int position, T mItem, int mViewType);


    public Resources getResources(){
        return getContext().getResources();
    }


}
