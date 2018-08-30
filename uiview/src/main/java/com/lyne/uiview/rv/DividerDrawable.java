package com.lyne.uiview.rv;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator  on 2016/9/16 16:39.
 * Desc:
 */

public class DividerDrawable extends Drawable {

    public static int DIVIDER_LEFT = 0x01;

    public static int DIVIDER_TOP = DIVIDER_LEFT << 1;

    public static int DIVIDER_RIGHT = DIVIDER_LEFT << 2;

    public static int DIVIDER_BOTTOM = DIVIDER_LEFT << 3;


    private int divider;

    Paint mPaint;


    public DividerDrawable(int color, int width) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        divider=DIVIDER_LEFT|DIVIDER_TOP|DIVIDER_RIGHT|DIVIDER_BOTTOM;
    }

    public void setDividerColor(int color) {
        mPaint.setColor(color);
    }

    public void setDividerWidth(int width) {
        mPaint.setStrokeWidth(width);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getBounds();
        int left = rect.left;
        int top = rect.top;
        int right = rect.right;
        int bottom = rect.bottom;
        if (DIVIDER_LEFT == (this.divider & DIVIDER_LEFT)) {
            canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, mPaint);
        }
        if (DIVIDER_TOP == (this.divider & DIVIDER_TOP)) {
            canvas.drawLine(rect.left, rect.top, rect.right, rect.top, mPaint);
        }
        if (DIVIDER_RIGHT == (this.divider & DIVIDER_RIGHT)) {
            canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, mPaint);
        }
        if (DIVIDER_BOTTOM == (this.divider & DIVIDER_BOTTOM)) {
            canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, mPaint);
        }
    }

    /**
     * * @param position
     */
    public void setDividerPosition(int position) {
        this.divider =  position;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
