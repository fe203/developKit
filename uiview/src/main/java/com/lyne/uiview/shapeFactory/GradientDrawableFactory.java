package com.lyne.uiview.shapeFactory;

import android.graphics.drawable.GradientDrawable;

/**
 * Created by liht on 2018/7/13.
 */

public class GradientDrawableFactory {

    public static class Builder{
        private int strokeColor;
        private int strokeWidth;
        private int solidColor;
        private int radius;

        public Builder setStrokeColor(int strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }

        public Builder setStrokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Builder setSolidColor(int solidColor) {
            this.solidColor = solidColor;
            return this;
        }

        public Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public GradientDrawable build(){
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.RECTANGLE);

            if (strokeWidth > 0){
                gd.setStroke(strokeWidth, strokeColor);
            }

            gd.setColor(solidColor);
            gd.setCornerRadius(radius);

            return gd;

        }

    }

}
