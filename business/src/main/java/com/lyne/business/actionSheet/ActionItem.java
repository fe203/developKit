package com.lyne.business.actionSheet;


/**
 * Created by liht on 2018/8/9.
 */

public class ActionItem {

    public int imgResId;
    public String name;
    public int textColorResId;

    int type = 0; //0-显示分割线 1-不显示分割线

    public ActionItem(String name){
        this(name, 0);
    }

    public ActionItem(String name, int textColorResId){
        this(name, textColorResId, 0);
    }

    public ActionItem(String name, int textColorResId, int imgResId){
        this.name = name;
        this.textColorResId = textColorResId;
        this.imgResId = imgResId;
    }

}
