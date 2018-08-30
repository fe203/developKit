package com.lyne.uiview.rv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/5/5.
 */

/**
 * Created by tiny on 2015/5/5.
 */
public abstract class RecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    public static final int TYPE_FOOTER = -2;

    final private Context mContext;
    private List<T> mList;
    private RecyclerView mRecyclerView;

    private LayoutInflater mInflater;


    private OnItemClickListener mItemClickListener;

    private OnItemLongClickListener mItemLongClickListener;


    final private FixedViewHolderHelper mHeaderViewHelper=new FixedViewHolderHelper(1000);

    final private FixedViewHolderHelper mFooterViewHelper=new FixedViewHolderHelper(2000);

    /**
     * @param mContext
     * @param mList
     * @param mRecyclerView
     */
    public RecyclerViewAdapter(Context mContext, List<T> mList, RecyclerView mRecyclerView) {
        this.mContext = mContext;
        this.mList = mList;
        this.mRecyclerView = mRecyclerView;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType =holder.getItemViewType();
        if(mHeaderViewHelper.contains(viewType)){
            //header不做处理
            return ;//
        };
        if(mFooterViewHelper.contains(viewType)){
            //footer不做处理
            return;
        }
        T item = null;
        item = getItem(regulatePosition(position));
        bindViewData(holder, item,position, getItemViewType(position));
//
    }

    public void bindViewData(BaseViewHolder holder,T item, int position, int mViewType) {

        holder.bindItemData(position, item, mViewType);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderViewHelper.contains(viewType)){
            BaseViewHolder<?> holder=mHeaderViewHelper.getViewHolderByType(viewType);
            if (holder.itemView.getLayoutParams() == null){
                holder.itemView.setLayoutParams(createDefaultLayoutParams());
            }
            return holder;
        }

        if(mFooterViewHelper.contains(viewType)){
            BaseViewHolder<?> holder=mFooterViewHelper.getViewHolderByType(viewType);
            if (holder.itemView.getLayoutParams() == null){
                holder.itemView.setLayoutParams(createDefaultLayoutParams());
            }
            return holder;
        }
        BaseViewHolder holder = createViewHolderByViewType(mContext, parent, viewType);
        bindViewHolderItem(holder, viewType);
        return holder;
    }


    public abstract BaseViewHolder createViewHolderByViewType(Context context, ViewGroup parent, int viewType);


    public void bindViewHolderItem(BaseViewHolder mHolder, int mViewType) {
        if(mHeaderViewHelper.contains(mViewType)){
            Log.e("RecyclerViewAdapter","BindViewHolderItem with a header ViewHolder!!");
            return;
        }
        if(mFooterViewHelper.contains(mViewType)){
            Log.e("RecyclerViewAdapter","BindViewHolderItem with a header ViewHolder!!");
            return;
        }
        View mClickView = mHolder.itemView;
//            }
        mClickView.setOnClickListener(this);
        if (mItemLongClickListener != null) {
            mClickView.setOnLongClickListener(this);
        }
        mHolder.setRecyclerView(mRecyclerView);
        mHolder.bindItemView(mViewType);
    }

    public boolean isHeader(int position){
        return position<mHeaderViewHelper.size();

    }

    public void addHeaderView(View view, Object tag){
        mHeaderViewHelper.add(new EmptyFooterViewHolder(view),tag);
    }
    public void addHeaderView(BaseViewHolder<?> viewHolder, Object tag){
        mHeaderViewHelper.add(viewHolder,tag);
    }
    public void addHeaderViewToTop(View view, Object tag){
        mHeaderViewHelper.addToTop(new EmptyFooterViewHolder(view),tag);
    }
    public void addHeaderViewToTop(BaseViewHolder<?> viewHolder, Object tag){
        mHeaderViewHelper.addToTop(viewHolder,tag);
    }
    public BaseViewHolder<?> removeHeaderView(Object tag){
       return mHeaderViewHelper.remove(tag);
    }
    public BaseViewHolder<?> removeHeaderView(View view){
        return mHeaderViewHelper.remove(view);
    }

    public BaseViewHolder<?> addFooterView(View view, Object tag){
        EmptyFooterViewHolder holder=new EmptyFooterViewHolder(view);
        mFooterViewHelper.add(holder,tag);
        return holder;
    }
    public BaseViewHolder<?> addFooterView(BaseViewHolder<?> viewHolder, Object tag){
        mFooterViewHelper.add(viewHolder,tag);
        return viewHolder;
    }
    public BaseViewHolder<?> addFooterViewToTop(View view, Object tag){
        EmptyFooterViewHolder holder=new EmptyFooterViewHolder(view);
        mFooterViewHelper.addToTop(new EmptyFooterViewHolder(view),tag);
        return holder;
    }
    public BaseViewHolder<?> addFooterViewToTop(BaseViewHolder<?> viewHolder, Object tag){
        mFooterViewHelper.addToTop(viewHolder,tag);
        return viewHolder;
    }
    public BaseViewHolder<?> removeFooterView(Object tag){
        return mFooterViewHelper.remove(tag);
    }
    public BaseViewHolder<?> removeFooterView(View view){
        return mFooterViewHelper.remove(view);
    }

    public BaseViewHolder<?> getHeaderViewHolderByTag(Object tag){
        return mHeaderViewHelper.getViewHolderByTag(tag);
    }
    public BaseViewHolder<?> getFooterViewHolderByTag(Object tag){
        return mFooterViewHelper.getViewHolderByTag(tag);
    }

    /**
     * 是否有Header布局
     * @return
     */
    public boolean hasHeaderItem(){
        return !mHeaderViewHelper.isEmpty();
    }
    protected RecyclerView.LayoutParams createDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    private boolean hasTag(Object tag){
        return mHeaderViewHelper.hasTag(tag) || mFooterViewHelper.hasTag(tag);
    }

    public RecyclerView getRecyclerView(){
        return mRecyclerView;
    }
    @Override
    public int getItemCount() {
        int itemCount = mList.size();
        itemCount = itemCount + mHeaderViewHelper.size();
        itemCount = itemCount + mFooterViewHelper.size();
        return itemCount;
    }

    public int getHeaderCount(){
        return mHeaderViewHelper.size();
    }

    public int getFooterCount(){
        return mFooterViewHelper.size();
    }

    public boolean isLastOne(int position){
        return mList.size() + mHeaderViewHelper.size() -1 == position;
    }

    public boolean isFirstOne(int position){
        return mHeaderViewHelper.size() == position;
    }

    /**
     * @param position 如果有headView，该position应该是未校正过的position
     * @return
     */
    public T getItem(int position) {
        return mList.get(position);
    }


    public int regulatePosition(int position) {
        int pos = position;
        int offset = mHeaderViewHelper.size();
        if(pos-offset>=0){
            pos = pos - offset;
        }
        if (pos >= mList.size()) {
            return mList.size()-1;
        }
        return pos;
    }



    public List<T> replaceList(List<T> newList) {
        List<T> old = mList;
        mList = newList;
        return old;
    }

    public Context getContext(){
        return mContext;
    }
    @Override
    public int getItemViewType(int position) {
        if(position<mHeaderViewHelper.size()){
            return mHeaderViewHelper.getViewType(position);
        }
        if(position>mList.size()+mHeaderViewHelper.size()-1&&position<getItemCount()){
            return mFooterViewHelper.getViewType(position-mHeaderViewHelper.size()-mList.size());
        }
        T item=getItem(regulatePosition(position));
        if (item instanceof IViewTypeResolver) {
            return ((IViewTypeResolver) (item)).getViewType();
        }else {
            return getRealItemViewType(regulatePosition(position));
        }
    }

    protected int getRealItemViewType(int position){
        return super.getItemViewType(position);
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        mItemClickListener = mListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mListener) {
        this.mItemLongClickListener = mListener;
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(v, regulatePosition(mRecyclerView.getChildAdapterPosition(v)));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mItemLongClickListener != null) {
            mItemLongClickListener.onItemLongClick(v, regulatePosition(mRecyclerView.getChildAdapterPosition(v)));
            return true;
        }
        return false;
    }


    private View emptyView;
    private int emptyLayoutId;

    /**
     * 外面设置展示emptyView
     * @param emptyView
     */
    public void setEmptyView(View emptyView){
        this.emptyView = emptyView;
    }

    public void setEmptyView(int layoutId){
        emptyLayoutId = layoutId;
    }

    public void removeEmpty(){
        if (hasTag("header_empty")){
            removeHeaderView("header_empty");
        }
    }

    /**
     * 用于展示空数据的，外面可以不用管它
     */
    private void notifyRefreshOver(){
        if (mList == null || mList.isEmpty()){
            if (!hasTag("header_empty")){
                if (emptyView == null && emptyLayoutId != 0){
                    emptyView = View.inflate(getContext(), emptyLayoutId, null);
                }else if (emptyView != null){
                    emptyView.setMinimumHeight(mRecyclerView.getHeight());
                    addHeaderView(emptyView, "header_empty");
                }
            }
        }else{
            removeEmpty();
        }
    }

    public void notifyChanged(){
        notifyRefreshOver();
        super.notifyDataSetChanged();
    }

//    /**
//     * 局部刷新某个item
//     * 建议调用方法的地方加同步锁，控制list的变化
//     *
//     * @param target
//     * @return
//     */
//    public boolean updateItem(T target) {
//        int position = findItemPositionInAdapter(target);
//        if (position != -1) {
//            mList.set(position, target);
//            RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(position);
//            if (vh != null) {
//                notifyItemChanged(position);
//            }
//            return true;
//        }
//        return false;
//
//    }

//    public int findItemPositionInAdapter(T target) {
//        for (int i = 0; i < getItemCount(); i++) {
//            T item = getItem(i);
//            if (item.equals(target)) {
//                return regulatePosition(i);
//            }
//        }
//        return -1;
//    }

    public interface IViewTypeResolver {

        /**
         * ViewHolder 的viewType
         * @return 别用-1 -2 等，-1 -2 预留给header footer
         */
        int getViewType();
    }

    private class EmptyFooterViewHolder extends BaseViewHolder{


        public EmptyFooterViewHolder(View itemView) {
            super(itemView);

        }

        @Override
        public void bindView(int mViewType) {

        }

        @Override
        public void bindData(int position, Object mItem, int mViewType) {

        }
    }

    private static class FixedViewInfo {
        int type;
        Object tag;
        BaseViewHolder<?>  holder;

    }

}
