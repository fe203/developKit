package com.lyne.uiview.rv;

import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tiny  on 2016/11/24 21:22.
 * Desc:用于管理头部或底部的固定的item
 */

public class FixedViewHolderHelper {

    final AtomicInteger mTypeGenerator;

    final ArrayList<FixedViewInfo> mViewInfos = new ArrayList<FixedViewInfo>();

    final Set<Integer> mTypeSet = new HashSet<Integer>();


    FixedViewHolderHelper(int start) {
        this.mTypeGenerator = new AtomicInteger(start);
    }

    void add(BaseViewHolder<?> holder, Object tag) {
        FixedViewInfo info = new FixedViewInfo();
        info.holder = holder;
        info.tag = tag;
        info.vieType = generateHeaderViewType();
        mTypeSet.add(info.vieType);
        mViewInfos.add(info);
    }

    void addToTop(BaseViewHolder<?> holder, Object tag) {
        if(tag==null){
            throw new IllegalArgumentException("FixedViewHolder tag can`t be null!!");
        }
        FixedViewInfo info = new FixedViewInfo();
        info.holder = holder;
        info.tag = tag;
        info.vieType = generateHeaderViewType();
        mTypeSet.add(info.vieType);
        mViewInfos.add(0, info);
    }

    int size() {
        return mViewInfos.size();
    }

    boolean isEmpty() {
        return mViewInfos.isEmpty();
    }

    int getViewType(int position) {
        return mViewInfos.get(position).vieType;
    }

    boolean contains(int viewType) {
        return mTypeSet.contains(viewType);
    }

    BaseViewHolder<?> remove(Object tag) {
        final ArrayList<FixedViewInfo> infos = mViewInfos;
        for (int i = 0; i < infos.size(); i++) {
            FixedViewInfo info = infos.get(i);
            if (info.tag.equals(tag)) {
                infos.remove(info);
                mTypeSet.remove(info.vieType);
                return info.holder;
            }
        }
        return null;
    }
    BaseViewHolder<?> remove(View view){
        final ArrayList<FixedViewInfo> infos = mViewInfos;
        for (int i = 0; i < infos.size(); i++) {
            FixedViewInfo info = infos.get(i);
            if (info.holder.itemView==view) {
                infos.remove(info);
                return info.holder;
            }
        }
        return null;
    }
    BaseViewHolder<?> getViewHolderByType(int viewType) {
        if (!mTypeSet.contains(viewType)) {
            return null;
        }
        final ArrayList<FixedViewInfo> infos = mViewInfos;
        for (int i = 0; i < infos.size(); i++) {
            FixedViewInfo info = infos.get(i);
            if(info.vieType==viewType){
                return info.holder;
            }
        }
        return null;
    }
    BaseViewHolder<?> getViewHolderByTag(Object tag) {
        if(tag==null){
            return null;
        }
        final ArrayList<FixedViewInfo> infos = mViewInfos;
        for (int i = 0; i < infos.size(); i++) {
            FixedViewInfo info = infos.get(i);
            if(info.tag.equals(tag)){
                return info.holder;
            }
        }
        return null;
    }

    boolean hasTag(Object tag){
        if (tag==null){
            return false;
        }
        final ArrayList<FixedViewInfo> infos = mViewInfos;
        for (int i = 0; i < infos.size(); i++) {
            FixedViewInfo info = infos.get(i);
            if(info.tag.equals(tag)){
                return true;
            }
        }
        return false;
    }

    FixedViewInfo getViewByTag(Object tag){
        if (tag==null){
            return null;
        }
        final ArrayList<FixedViewInfo> infos = mViewInfos;
        for (int i = 0; i < infos.size(); i++) {
            FixedViewInfo info = infos.get(i);
            if(info.tag.equals(tag)){
                return info;
            }
        }
        return null;
    }



    private int generateHeaderViewType() {
        return mTypeGenerator.getAndIncrement();
    }


    static class FixedViewInfo {
        int vieType;
        Object tag;
        BaseViewHolder<?> holder;

    }


}
