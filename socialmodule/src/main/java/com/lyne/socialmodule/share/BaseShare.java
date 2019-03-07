package com.lyne.socialmodule.share;

import android.content.Context;
import android.content.Intent;

import com.lyne.socialmodule.ShareUtils;


/**
 * Created by shaohui on 2016/11/18.
 */

public interface BaseShare {

    void shareText(ShareUtils.SharePlatform platform, String text);

    void shareMedia(ShareUtils.SharePlatform platform, String title, String targetUrl, String summary,
                    ShareImageObject shareImageObject);

    void shareImage(ShareUtils.SharePlatform platform, ShareImageObject shareImageObject);

    void shareVideo(ShareUtils.SharePlatform platform, String title, String targetUrl, String summary, ShareImageObject shareImageObject);

    void shareMP(String path, String title, String targetUrl, String summary, ShareImageObject shareImageObject);

    void handleResult(Intent data);

    boolean isInstall(Context context);

    void recycle();
}
