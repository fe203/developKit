package com.lyne.socialmodule.share;

import android.graphics.Bitmap;

/**
 * Created by shaohui on 2016/11/19.
 */

public class ShareImageObject {

    private Bitmap mBitmap;

    private String mPathOrUrl;

    private int defaultImgResId;

    public ShareImageObject(Bitmap bitmap, int defaultImgResId) {
        mBitmap = bitmap;
        this.defaultImgResId = defaultImgResId;
    }

    public ShareImageObject(String pathOrUrl, int defaultImgResId) {
        mPathOrUrl = pathOrUrl;
        this.defaultImgResId = defaultImgResId;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public String getPathOrUrl() {
        return mPathOrUrl;
    }

    public void setPathOrUrl(String pathOrUrl) {
        mPathOrUrl = pathOrUrl;
    }

    public int getDefaultImgResId() {
        return defaultImgResId;
    }

    public void setDefaultImgResId(int defaultImgResId) {
        this.defaultImgResId = defaultImgResId;
    }
}
