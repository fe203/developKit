package com.lyne.business.actionSheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.lyne.business.R;
import com.lyne.uiview.shapeFactory.GradientDrawableFactory;
import com.lyne.fw.device.DeviceInfo;
import com.lyne.uiview.rv.BaseViewHolder;
import com.lyne.uiview.rv.OnItemClickListener;
import com.lyne.uiview.rv.RecyclerViewAdapter;

import java.util.List;

/**
 * Created by liht on 2018/7/30
 */
public class ActionSheetDialog {

    private TextView tvCancel;
    private Dialog bottomDialog;
    private View contentView;

    private RecyclerView rcvSheet;

    private List<ActionItem> mItemList;
    private String cancelText;
    private ActionSheetListener actionSheetListener;


    private ActionSheetDialog() {

    }

    private void initView(Context context) {
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_content_circle, null);
        tvCancel = contentView.findViewById(R.id.tv_item_cancle);
        tvCancel.getPaint().setFakeBoldText(true);
        tvCancel.setText(cancelText);

        tvCancel.setBackground(new GradientDrawableFactory.Builder()
                .setRadius(context.getResources().getDimensionPixelOffset(R.dimen.dp_750_24))
                .setSolidColor(Color.parseColor("#DDDDDD"))
                .build());

        rcvSheet = contentView.findViewById(R.id.rcv_bottom_sheet);
        rcvSheet.setBackground(new GradientDrawableFactory.Builder()
                .setRadius(context.getResources().getDimensionPixelOffset(R.dimen.dp_750_24))
                .setSolidColor(Color.WHITE)
                .build());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, OrientationHelper.VERTICAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);

        rcvSheet.setLayoutManager(layoutManager);

        RecyclerViewAdapter itemAdapter = new RecyclerViewAdapter<ActionItem>(context, mItemList, rcvSheet) {
            @Override
            public BaseViewHolder createViewHolderByViewType(Context context, ViewGroup parent, int viewType) {
                return new SheetItemViewHolder(context, parent);
            }

        };

        rcvSheet.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View mItemView, int position) {
                if (actionSheetListener != null){
                    actionSheetListener.onActionClick(position);
                }
                bottomDialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionSheetListener != null){
                    actionSheetListener.onPositiveButtonClick();
                }
                bottomDialog.dismiss();
            }
        });
    }

    public static class Builder{

        private Context mContext;

        private List<ActionItem> mItemList;
        private String cancelText;
        private ActionSheetListener actionSheetListener;

        public Builder(Context context){
            this.mContext = context;
        }

        public Builder setItems(List<ActionItem> itemList){
            this.mItemList = itemList;

            //找出第一个不为空的即可
            for (ActionItem actionItem : mItemList){
                if (!TextUtils.isEmpty(actionItem.name)){
                    actionItem.type = 1;
                    break;
                }
            }

            return this;
        }

        public Builder setNavigateButton(String text){
            this.cancelText = text;
            return this;
        }

        public Builder setActionSheetListener(ActionSheetListener actionSheetListener){
            this.actionSheetListener = actionSheetListener;
            return this;
        }

        public Dialog build(){
            ActionSheetDialog actionSheetDialog = new ActionSheetDialog();
            actionSheetDialog.mItemList = mItemList;
            actionSheetDialog.cancelText = cancelText;
            actionSheetDialog.actionSheetListener = actionSheetListener;

            actionSheetDialog.initView(mContext);

            return actionSheetDialog.getBottomDialog(mContext);
        }

    }

    private Dialog getBottomDialog(Context context) {
        bottomDialog = new Dialog(context, R.style.BottomDialog);

        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = DeviceInfo.getScreenWidth(context) - context.getResources().getDimensionPixelOffset(R.dimen.dp_750_40);
        params.bottomMargin = context.getResources().getDimensionPixelOffset(R.dimen.dp_750_20);
        contentView.setLayoutParams(params);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        return bottomDialog;
    }

    public interface ActionSheetListener {

        void onActionClick(int position);

        void onPositiveButtonClick();
    }

}
