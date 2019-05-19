package com.lyne.uiview.imageView;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.lyne.uiview.R;
import com.lyne.uiview.imageView.transformation.GlideTransformation;

public class BaseNetworkRoundedImageView extends AppCompatImageView {
    private String imageUrl;
    private boolean dontLoadSameUrl;
    
    private int currDisplayedDefaultResId;
    private int defaultResId;
    private Drawable currDisplayedDefaultDrawable, defaultDrawable;

    private int cornerRadius, borderWidth, borderColor;
    private boolean isCornerLT, isCornerRT, isCornerLB, isCornerRB;

    private GlideTransformation mTransformation;

    public BaseNetworkRoundedImageView(Context context) {
        super(context);
        initAttrs(context, null);
    }
    
    public BaseNetworkRoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs){

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseNetworkRoundedImageView);

        cornerRadius = a.getDimensionPixelSize(R.styleable.BaseNetworkRoundedImageView_riv_corner_radius, -1);
        borderWidth = a.getDimensionPixelSize(R.styleable.BaseNetworkRoundedImageView_riv_border_width, -1);

        borderColor = a.getColor(R.styleable.BaseNetworkRoundedImageView_riv_border_color, Color.BLACK);

        isCornerLT = a.getBoolean(R.styleable.BaseNetworkRoundedImageView_riv_corner_lt, true);
        isCornerRT = a.getBoolean(R.styleable.BaseNetworkRoundedImageView_riv_corner_rt, true);
        isCornerLB = a.getBoolean(R.styleable.BaseNetworkRoundedImageView_riv_corner_lb, true);
        isCornerRB = a.getBoolean(R.styleable.BaseNetworkRoundedImageView_riv_corner_rb, true);

        a.recycle();

        mTransformation = new GlideTransformation(getContext())
                .setBorderColor(borderColor)
                .setBorderWidth(borderWidth)
                .setCornerEnable(isCornerLT, isCornerRT, isCornerLB, isCornerRB)
                .setCornerRadius(cornerRadius);
    }

    public GlideTransformation getTransformation() {
        return mTransformation;
    }

    public void setDefaultDrawableRes(int res){
        defaultResId = res;
        defaultDrawable = getContext().getResources().getDrawable(defaultResId);
    }

    public void setDefaultDrawable(Drawable defaultDrawable){
        this.defaultDrawable = defaultDrawable;
    }

    public void setDontLoadSameUrl(boolean dontLoadSameUrl){
        this.dontLoadSameUrl = dontLoadSameUrl;
    }

    public void setImageUrl(String url, String thumbUrl){
        setImageUrl(url, null, thumbUrl);
    }

    public void setImageUrl(String url, final OnGetImageSizeListener listener, final String thumbUrl){
        if(dontLoadSameUrl){
            if(url != null && url.equals(imageUrl) && (currDisplayedDefaultResId == defaultResId && currDisplayedDefaultDrawable == defaultDrawable)){
                return;
            }
        }
        
        imageUrl = (url == null)? "" : url;

        currDisplayedDefaultResId = defaultResId;
        currDisplayedDefaultDrawable = defaultDrawable;

        if(getContext() instanceof Activity){
            if(((Activity) getContext()).isDestroyed()){
                return;
            }
        }

        if(listener == null){

            Glide.with(getContext())
                    .asBitmap()
                    .load(thumbUrl)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA).error(defaultDrawable).transform(mTransformation))
                    .into(this);
        }else {
            Glide.with(getContext())
                    .asBitmap()
                    .load(thumbUrl)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA).error(defaultDrawable).transform(mTransformation))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            listener.onGetImageSize(imageUrl, resource.getWidth(), resource.getHeight());
                            return false;
                        }
                    })
                    .into(this);

        }
    }

    public void setImageUrl(String url, final String thumbUrl, int width, int height){
        if(dontLoadSameUrl){
            if(url != null && url.equals(imageUrl) && (currDisplayedDefaultResId == defaultResId && currDisplayedDefaultDrawable == defaultDrawable)){
                return;
            }
        }
        imageUrl = (url == null)? "" : url;
        currDisplayedDefaultResId = defaultResId;
        currDisplayedDefaultDrawable = defaultDrawable;
        if(getContext() instanceof Activity){
            if(((Activity) getContext()).isDestroyed()){
                return;
            }
        }

        Glide.with(getContext())
                .asBitmap()
                .load(thumbUrl)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA).override(width, height).error(defaultDrawable).transform(mTransformation))
                .into(this);
    }

    public interface OnGetImageSizeListener{
        public void onGetImageSize(String url, int width, int height);
    }

}
