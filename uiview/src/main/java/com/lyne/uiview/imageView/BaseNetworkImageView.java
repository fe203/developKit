package com.lyne.uiview.imageView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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


public class BaseNetworkImageView extends AppCompatImageView {
    protected String imageUrl;
    protected boolean dontLoadSameUrl;

    protected int currDisplayedDefaultResId;
    protected int defaultResId = R.color.default_color;

    public BaseNetworkImageView(Context context) {
        super(context);
    }
    
    public BaseNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setDefaultDrawableRes(int res){
        defaultResId = res;
    }

    public void setDontLoadSameUrl(boolean dontLoadSameUrl){
        this.dontLoadSameUrl = dontLoadSameUrl;
    }

    public void setImageUrl(String url, String thumbUrl){
        setImageUrl(url, null, thumbUrl);
    }
    
    public void setImageUrl(String url, final OnGetImageSizeListener listener, final String thumbUrl){
        if(dontLoadSameUrl){
            if(url != null && url.equals(imageUrl) && (currDisplayedDefaultResId == defaultResId)){
                return;
            }
        }
        
        imageUrl = (url == null)? "" : url;

        currDisplayedDefaultResId = defaultResId;

        if(getContext() instanceof Activity){
            if(((Activity) getContext()).isDestroyed()){
                return;
            }
        }

        if(listener == null){
            Glide.with(getContext()).setDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).error(defaultResId)).asBitmap().load(thumbUrl).into(this);
        }else {
            Glide.with(getContext()).setDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).error(defaultResId)).asBitmap().load(thumbUrl).listener(
                    new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            listener.onGetImageSize(imageUrl, resource.getWidth(), resource.getHeight());
                            return false;
                        }
                    }).into(this);
        }

    }

    public void setImageUrl(String url, final String thumbUrl, int width, int height){
        if(dontLoadSameUrl){
            if(url != null && url.equals(imageUrl) && (currDisplayedDefaultResId == defaultResId)){
                return;
            }
        }
        imageUrl = (url == null)? "" : url;
        currDisplayedDefaultResId = defaultResId;
        if(getContext() instanceof Activity){
            if(((Activity) getContext()).isDestroyed()){
                return;
            }
        }

        Glide.with(getContext()).setDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).override(width, height).error(defaultResId)).asBitmap().into(this);

    }


    public interface OnGetImageSizeListener{
        public void onGetImageSize(String url, int width, int height);
    }

}
