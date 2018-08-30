package com.lyne.uiview.imageView.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by liht on 2018/8/29.
 */


public class GlideTransformation extends BitmapTransformation {

    private static final String ID = "com.lyne.uiview.imageView.transformation.GlideTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);


    private float mCornerRadius = 0f;
    private int mBorderWidth;
    private int borderColor;

    private boolean isCornerLT, isCornerRT, isCornerLB, isCornerRB;

    public GlideTransformation(Context context) {
        super(context);
    }

    public GlideTransformation setBorderColor(int borderColor){
        this.borderColor = borderColor;
        return this;
    }

    public GlideTransformation setBorderWidth(int borderWidth){
        mBorderWidth = borderWidth;
        return this;
    }

    public GlideTransformation setCornerRadius(int radius){
        mCornerRadius = radius;
        if (mCornerRadius < 0){
            mCornerRadius = 0;
        }
        return this;
    }

    public GlideTransformation setCornerEnable(boolean lt, boolean rt, boolean lb, boolean rb){
        isCornerLT = lt;
        isCornerRT = rt;
        isCornerLB = lb;
        isCornerRB = rb;
        return this;
    }


    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());


        if (mBorderWidth > 0) {
            canvas.drawRoundRect(rectF, Math.max(mCornerRadius, 0), Math.max(mCornerRadius, 0), paint);

            Paint mBorderPaint = new Paint();
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStrokeWidth(mBorderWidth);

            RectF mBorderRect = new RectF();

            mBorderRect.set(rectF);
            mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);

            canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);
            canvas.drawRoundRect(mBorderRect, mCornerRadius, mCornerRadius, mBorderPaint);
        } else {
            drawRoundRect(canvas, rectF, mCornerRadius, paint);
        }

        return result;
    }

    /** edit  by liht on 2016/8/15 11:00 暂不支持带bordor的圆角处理 */
    private void drawRoundRect(Canvas canvas, RectF rect, float r, Paint paint){
        canvas.drawRoundRect(rect, r, r, paint);
        if (r > 0){
            float cx = (rect.left + rect.right) / 2;
            float cy = (rect.top + rect.bottom) / 2;
            if (!isCornerLT){
                canvas.drawRect(rect.left, rect.top, cx, cy, paint);
            }
            if (!isCornerRT){
                canvas.drawRect(cx, rect.top, rect.right, cy, paint);
            }
            if (!isCornerLB){
                canvas.drawRect(rect.left, cy, cx, rect.bottom, paint);
            }
            if (!isCornerRB){
                canvas.drawRect(cx, cy, rect.right, rect.bottom, paint);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GlideTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
