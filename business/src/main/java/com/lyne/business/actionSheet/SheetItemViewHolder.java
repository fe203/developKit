package com.lyne.business.actionSheet;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyne.business.R;
import com.lyne.uiview.rv.BaseViewHolder;


/**
 * Created by liht on 2018/8/9.
 */

public class SheetItemViewHolder extends BaseViewHolder<ActionItem> {

    private ImageView imgView;
    private TextView textView;

    private View topDivider;

    private int itemHeight;


    public SheetItemViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.share_bottom_sheet_item, parent, false));

        itemHeight = context.getResources().getDimensionPixelOffset(R.dimen.dp_750_100);

    }

    @Override
    public void bindView(int mViewType) {

        imgView = itemView.findViewById(R.id.iv_item);
        textView = itemView.findViewById(R.id.tv_item);

        textView.getPaint().setFakeBoldText(true);

        topDivider = itemView.findViewById(R.id.top_divider);
    }

    @Override
    public void bindData(int position, ActionItem mItem, int mViewType) {
        if (mItem.imgResId > 0){
            imgView.setImageResource(mItem.imgResId);
            imgView.setVisibility(View.VISIBLE);
        }else {
            imgView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mItem.name)){
            textView.setText(mItem.name);
            textView.setTextColor(mItem.textColorResId > 0 ? getResources().getColor(mItem.textColorResId) : Color.parseColor("#333333"));
            itemView.getLayoutParams().height = itemHeight;
        }else {
            itemView.getLayoutParams().height = 0;
        }

        topDivider.setVisibility(mItem.type == 1 ? View.GONE : View.VISIBLE);
    }
}
