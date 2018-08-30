package com.lyne.fw.url;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.lyne.fw.log.LogUtils;

import java.net.URLDecoder;

/**
 * Created by liht  on 2016/9/26 15:15.
 * Desc:
 */
public class HttpUrlUtils {

    /**
     * 获取url查询参数
     * @param url
     * @param key
     * @return
     */
    public static String getUrlParamValue(String url, String key){

        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(key)){
            return "";
        }
        try{
            return Uri.parse(url).getQueryParameter(key);
        }catch (Exception e){
            return "";
        }

    }

    public static String getPath(String url){
        if (TextUtils.isEmpty(url)){
            return "";
        }
        try{
            return Uri.parse(url).getPath();
        }catch (Exception e){
            return "";
        }
    }


    public static String addParams(String url, String key, String value){
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(key) || TextUtils.isEmpty(value)){
            return url;
        }
        return Uri.parse(url).buildUpon().appendQueryParameter(key, value).build().toString();
    }

    public static String toURLDecoded(Context context, String paramString) {
        if (paramString == null || paramString.equals("")) {
            LogUtils.print(context.getClass(), "toURLDecoded error:"+paramString);
            return "";
        }
        try  {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLDecoder.decode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }

        return "";
    }


}
