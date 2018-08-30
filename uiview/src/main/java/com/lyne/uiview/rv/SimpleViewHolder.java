package com.lyne.uiview.rv;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by tiny  on 2016/11/1 14:45.
 * Desc:
 */

public abstract class SimpleViewHolder<T> extends BaseViewHolder<T> {

    SparseArray<View> mViewCached=new SparseArray<View>();


    public SimpleViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(int mViewType) {

    }

    @Override
    public void bindData(int position, T mItem, int mViewType) {
        convert(this,position,mItem);
    }

    public abstract void convert(SimpleViewHolder<T> viewHolder,int position,T mItem);

    public SimpleViewHolder<T> setText(int id,String text){
        TextView textView=getView(id);
        textView.setText(text);
        return this;
    }
    public SimpleViewHolder<T> setVisible(int id,int visible){
        View view=getView(id);
        view.setVisibility(visible);
        return this;
    }
    public SimpleViewHolder<T> setImageDrawable(int id,int drawableId){
        ImageView imageView=getView(id);
        imageView.setImageResource(drawableId);
        return this;
    }

    public <T extends View> T getView(int viewId) {
        T view = (T) mViewCached.get(viewId);
        if (view == null) {
            view= (T) itemView.findViewById(viewId);
            mViewCached.put(viewId,view);
        }
        return view;
    }
}
