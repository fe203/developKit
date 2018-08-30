package com.lyne.business.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyne.business.R;
import com.lyne.fw.device.DeviceInfo;
import com.lyne.uiview.rv.BaseViewHolder;
import com.lyne.uiview.rv.RecyclerViewAdapter;
import com.lyne.uiview.shapeFactory.GradientDrawableFactory;
import com.lyne.utils.algorithm.CollectionUtils;

import java.util.List;


public class DialogFactory {
    public final static int BTN_LEFT = 0;
    public final static int BTN_RIGHT = 1;

    public static class Builder{
        private Context context;
        private CharSequence title;
        private CharSequence content;
        private List<String> contentList;
        private View contentView;
        private CharSequence leftText;
        private CharSequence rightText = "确定";
        private OnDialogButtonClickListener listener;
        private double widthScale = 0.8;
        private int backgroundColor;
        private boolean isFloating = true;
        private boolean cancelable = true;
        private boolean canceledOnTouchOutside = true;
        private int titleMaxLines = 2;

        private Builder(){}

        public Builder(Context context){
            this.context = context;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setContent(CharSequence content) {
            this.content = content;
            return this;
        }

        /**
         * 长alert提示
         * @param contentList
         * @return
         */
        public Builder setLongContentList(List<String> contentList){
            this.contentList = contentList;
            return this;
        }

        public Builder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setFloating(boolean floating) {
            isFloating = floating;
            return this;
        }

        public Builder setLeftText(CharSequence leftText) {
            this.leftText = leftText;
            return this;
        }

        public Builder setListener(OnDialogButtonClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setRightText(CharSequence rightText) {
            this.rightText = rightText;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setWidthScale(double widthScale) {
            this.widthScale = widthScale;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setTitleMaxLines(int titleMaxLines) {
            this.titleMaxLines = titleMaxLines < 0 ? 0 : titleMaxLines;
            return this;
        }

        public Dialog build(){
            final Dialog dialog = new Dialog(context, isFloating? R.style.CustomDialog : R.style.CustomDialogNotFloating);
            dialog.setContentView(R.layout.dialog_common);
            android.view.WindowManager.LayoutParams windowLp = dialog.getWindow().getAttributes();
            windowLp.width = (int)(DeviceInfo.getScreenWidth(context) * widthScale);
            dialog.getWindow().setAttributes(windowLp);
            if(title != null){
                TextView titleText = dialog.findViewById(R.id.textTitle);
                titleText.setMaxLines(titleMaxLines);
                titleText.setText(title);
            }else{
                dialog.findViewById(R.id.textTitle).setVisibility(View.GONE);
                dialog.findViewById(R.id.conetentSpace).setVisibility(View.VISIBLE);
            }
            if(content != null){
                ((TextView)dialog.findViewById(R.id.textContent)).setText(content);
            }else if(contentView != null){
                dialog.findViewById(R.id.textContent).setVisibility(View.GONE);
                ((LinearLayout)dialog.findViewById(R.id.contentLayout)).addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            }else if (!CollectionUtils.isEmpty(contentList)){
                dialog.findViewById(R.id.textContent).setVisibility(View.GONE);
                RecyclerView listView = dialog.findViewById(R.id.long_text_list);
                listView.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, OrientationHelper.VERTICAL, false);
//                layoutManager.setSmoothScrollbarEnabled(true);
//                layoutManager.setAutoMeasureEnabled(true);
                listView.setLayoutManager(layoutManager);

                listView.setAdapter(new RecyclerViewAdapter<String>(context, contentList, listView) {
                    @Override
                    public BaseViewHolder createViewHolderByViewType(Context context, ViewGroup parent, int viewType) {

                        int textSize = context.getResources().getDimensionPixelOffset(R.dimen.size_normal);
                        int textColor = Color.parseColor("#666666");
                        int drawableWidth = context.getResources().getDimensionPixelOffset(R.dimen.dp_750_16);
                        int padding = context.getResources().getDimensionPixelOffset(R.dimen.dp_750_20);
                        GradientDrawable leftDrawable = new GradientDrawable();
                        leftDrawable.setColor(Color.parseColor("#2366CF"));
                        leftDrawable.setShape(GradientDrawable.OVAL);
                        leftDrawable.setBounds(0, 0, drawableWidth, drawableWidth);
                        TextView textView = new TextView(context);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                        textView.setTextColor(textColor);
                        textView.setMinHeight(context.getResources().getDimensionPixelOffset(R.dimen.dp_750_60));
                        textView.setCompoundDrawables(leftDrawable, null, null, null);
                        textView.setCompoundDrawablePadding(padding);
                        return new BaseViewHolder<String>(textView){

                            @Override
                            public void bindView(int mViewType) {

                            }

                            @Override
                            public void bindData(int position, String mItem, int mViewType) {
                                ((TextView)itemView).setText(mItem);
                            }
                        };
                    }
                });
            }

            Button btnLeft = dialog.findViewById(R.id.btnLeft);
            Button btnRight = dialog.findViewById(R.id.btnRight);
            btnRight.getPaint().setFakeBoldText(true);
            if(leftText != null){
                btnLeft.setText(leftText);
            }
            if(rightText != null){
                btnRight.setText(rightText);
            }
            if(TextUtils.isEmpty(leftText) && TextUtils.isEmpty(rightText)){
                dialog.findViewById(R.id.btnLayout).setVisibility(View.GONE);
            }
            View dialog_lp = dialog.findViewById(R.id.dialpg_lp);
            if(backgroundColor !=0){
                dialog_lp.setBackgroundResource(backgroundColor);
            }else {
                dialog_lp.setBackground(new GradientDrawableFactory.Builder()
                        .setRadius(context.getResources().getDimensionPixelOffset(R.dimen.dp_750_2))
                        .setSolidColor(Color.WHITE)
                        .build());
            }
            btnLeft.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onDialogButtonClick(dialog, BTN_LEFT);
                    }else{
                        dialog.dismiss();
                    }
                }
            });
            btnRight.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onDialogButtonClick(dialog, BTN_RIGHT);
                    }else{
                        dialog.dismiss();
                    }
                }
            });

            //默认布局为两个按钮，一个按钮时
            if(leftText == null){
                btnLeft.setVisibility(View.GONE);
            }
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

            return dialog;
        }
    }

    public interface OnDialogButtonClickListener{
        void onDialogButtonClick(Dialog dialog, int which);
    }
}
