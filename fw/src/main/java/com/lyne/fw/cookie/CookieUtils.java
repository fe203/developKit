package com.lyne.fw.cookie;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.lyne.utils.algorithm.CollectionUtils;

import java.net.HttpCookie;
import java.util.List;


public class CookieUtils {

    /**
     * 异步添加cookie
     * @param context
     * @param urls
     * @param cookieList
     */
    public static void asynCookie(final Context context, final List<String> urls, final List<HttpCookie> cookieList) {
        if(CollectionUtils.isEmpty(cookieList)){
            return;
        }
        /** edit  by liht on 2016/10/27 9:12
         *  修改成在子线程中执行，减少耗时
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                CookieSyncManager.createInstance(context.getApplicationContext());
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                for (String cookieUrl : urls){
                    for(HttpCookie cookie : cookieList){
                        cookieManager.setCookie(cookieUrl, cookie.getName() + "=" + cookie.getValue());
                    }
                }
                CookieSyncManager.getInstance().sync();
            }
        }).start();

    }

    public static void asynCookie(final Context context, final String url, final List<HttpCookie> cookieList) {
        if(CollectionUtils.isEmpty(cookieList)){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                CookieSyncManager.createInstance(context.getApplicationContext());
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                for(HttpCookie cookie : cookieList){
                    cookieManager.setCookie(url, cookie.getName() + "=" + cookie.getValue());
                }
                CookieSyncManager.getInstance().sync();

            }
        }).start();

    }

    public static String getStoredCookie(String url) {
        if(TextUtils.isEmpty(url)){
            return null;
        }
        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.removeSessionCookie();//移除
        return cookieManager.getCookie(url);
    }

    public static void removeAllCookie(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CookieSyncManager.createInstance(context.getApplicationContext());
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                CookieSyncManager.getInstance().sync();
            }
        }).start();

    }

}
