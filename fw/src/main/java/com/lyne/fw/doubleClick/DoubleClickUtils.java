package com.lyne.fw.doubleClick;

/**
 * Created by chenning on 2018/6/6
 */
public class DoubleClickUtils {

    /** 判断是否是快速点击 */
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 100) {

            return true;
        }
        lastClickTime = time;
        return false;
    }
}
