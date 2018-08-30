package com.lyne.uiview.rv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lyne.uiview.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tiny  on 2016/11/2 15:23.
 * Desc:
 */

public abstract class SimpleRecyclerViewAdapter<T,VH extends SimpleRecyclerViewAdapter.CommonViewHolder<T>> extends RecyclerViewAdapter<T> {


    private final HashMap<Integer,OnClickEvent<T>> mClickEvent=new HashMap<Integer, OnClickEvent<T>>();

    public SimpleRecyclerViewAdapter(Context mContext, List<T> mList, RecyclerView mRecyclerView) {
        super(mContext, mList, mRecyclerView);
    }


    @Override
    public void bindViewData(BaseViewHolder holder,T item, int position, int mViewType) {
        super.bindViewData(holder, item,position, mViewType);
        VH vh= (VH) holder;
        HashMap<Integer,OnClickEvent<T>> events=mClickEvent;
        Iterator<Map.Entry<Integer,OnClickEvent<T>>> iterator= events.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer,OnClickEvent<T>> entry=iterator.next();
            int id=entry.getKey();
            View view=vh.getView(id);
            view.setTag(R.id.rv_item_tag,item);
            view.setTag(R.id.rv_item_position,position);
        }
    }

    @Override
    public void bindViewHolderItem(BaseViewHolder holder, int mViewType) {
        super.bindViewHolderItem(holder, mViewType);
        VH vh= (VH) holder;
        HashMap<Integer,OnClickEvent<T>> events=mClickEvent;
        if(events.isEmpty()){
            return;
        }
        for (Map.Entry<Integer, OnClickEvent<T>> entry : events.entrySet()) {
            int id = entry.getKey();
            View view = vh.getView(id);
            view.setOnClickListener(mViewClickListener);
        }


    }

    public void addClickEvent(int viewId,OnClickEvent<T> event){
        mClickEvent.put(viewId,event);
    }


    private View.OnClickListener mViewClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int position= (Integer) v.getTag(R.id.rv_item_position);
            T item= (T) v.getTag(R.id.rv_item_tag);
            int id=v.getId();
            OnClickEvent<T> event= mClickEvent.get(id);
            if(event!=null){
                event.onViewClick(v,item,position);
            }
        }
    };

    public abstract void convert(CommonViewHolder<T> viewHolder, int position,T mItem) ;

    public   static class CommonViewHolder<T> extends SimpleViewHolder<T>{

        SimpleRecyclerViewAdapter<T,? extends CommonViewHolder<T>> mAdapter;
        public CommonViewHolder(View itemView, SimpleRecyclerViewAdapter<T,? extends CommonViewHolder<T>> adapter) {
            super(itemView);
            this.mAdapter=adapter;
        }

        @Override
        public final void convert(SimpleViewHolder<T> viewHolder,int position, T mItem) {
            CommonViewHolder<T> vh= (CommonViewHolder<T>) viewHolder;
            mAdapter.convert(vh,position,mItem);
        }
    }

    public interface OnClickEvent<T>{
        void onViewClick(View view, T item, int position);
    }

}
