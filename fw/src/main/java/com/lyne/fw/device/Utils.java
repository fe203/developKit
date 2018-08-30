package com.lyne.fw.device;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/5/7.
 */

public class Utils {

    public static void hideKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        try{
            if (context.getCurrentFocus() != null && context.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }else{
                imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }catch(Exception e){};
    }

    public static void hideKeyboard(Dialog dialog) {

        InputMethodManager imm = (InputMethodManager) dialog.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        Window window=dialog.getWindow();
        try{
            if (window.getCurrentFocus() != null && window.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(window.getCurrentFocus().getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }else{
                imm.hideSoftInputFromWindow(window.getDecorView().getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }catch(Exception e){};
    }

    public static void showKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isPhoneNumber(String text){
        if(TextUtils.isEmpty(text) || text.length() != 11){
            return false;
        }
        return text.matches("^[1][3,4,5,7,8][0-9]{9}$");
    }

    /**
     * 显示隐藏状态栏，全屏不变，只在有全屏时有效
     * @param enable
     */
    public static void setStatusBarVisibility(Activity activity, boolean enable) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (enable) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        activity.getWindow().setAttributes(lp);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static int getVersionCode(Context context){
        int versionCode = 0;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getVersionName(Context context){
        String versionName = "";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    public static boolean currentMainProcess(Context context){
        ActivityManager am = ((ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getApplicationContext().getPackageName();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (mainProcessName.equals(info.processName) && android.os.Process.myPid() == info.pid) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        String mainProcessName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : lists){
            if(info.processName.equals(mainProcessName + ":" + proessName)){
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    public static void extractUrl2Link(TextView tvContent, String schema, String url) {
        String mentionsScheme = String.format("%s/?%s=", schema, url);
        String check = "(((http|https)://)|(www.))(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
        Pattern p = Pattern.compile(check);
        Linkify.addLinks(tvContent, p, mentionsScheme);

        removeHyperLinkUnderline(tvContent);
    }

    public static void extractPhoneLink(TextView tvContent) {
        Linkify.addLinks(tvContent, Linkify.PHONE_NUMBERS);
    }

    private static void removeHyperLinkUnderline(TextView tvContent) {
        if (tvContent.getText() instanceof Spannable){
            Spannable spannable = (Spannable) tvContent.getText();
            spannable.setSpan(new UnderlineSpan(){
                                  @Override
                                  public void updateDrawState(TextPaint ds) {
                                      ds.setUnderlineText(false);
                                  }
                              },
                    0, tvContent.getText().length(), Spanned.SPAN_MARK_MARK);
        }

    }

}
