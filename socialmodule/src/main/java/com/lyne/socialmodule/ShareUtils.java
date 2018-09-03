package com.lyne.socialmodule;

/**
 * Created by liht on 2018/1/24.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.lyne.socialmodule.share.BaseShare;
import com.lyne.socialmodule.share.ShareImageObject;
import com.lyne.socialmodule.share.ShareListener;
import com.lyne.socialmodule.share.WXShare;


/**
 * Created by shaohui on 2016/11/18.
 */

public class ShareUtils {
    /**
     * 测试case
     *
     * 1. 本地图片 vs 网络图片
     * 2. 图片大小限制
     * 3. 文字长度限制
     */

    public enum SharePlatform{
        QQ, QZONE, WX, WX_TIMELINE, WX_MP, WEIBO
    }

    public static ShareListener mShareListener;

    private static BaseShare mShareInstance;

    private final static int TYPE_IMAGE = 1;
    private final static int TYPE_TEXT = 2;
    private final static int TYPE_MEDIA = 3;
    private final static int TYPE_MP = 4;

    private static int mType;
    private static SharePlatform mPlatform;
    private static String mText;
    private static ShareImageObject mShareImageObject;
    private static String mTitle;
    private static String mSummary;
    private static String mTargetUrl;
    private static String mPath;

    private static Activity mActivity;

    static void action(Activity activity) {
        mActivity = activity;
        mShareInstance = getShareInstance();

        // 防止之后调用 NullPointException
        if (mShareListener == null) {
            activity.finish();
            return;
        }

        switch (mType) {
            case TYPE_TEXT:
                mShareInstance.shareText(mPlatform, mText);
                break;
            case TYPE_IMAGE:
                mShareInstance.shareImage(mPlatform, mShareImageObject);
                break;
            case TYPE_MEDIA:
                mShareInstance.shareMedia(mPlatform, mTitle, mTargetUrl, mSummary, mShareImageObject);
                break;
            case TYPE_MP:
                mShareInstance.shareMP(mPath, mTitle, mTargetUrl, mSummary, mShareImageObject);
        }
    }

    public static void shareText(Context context, SharePlatform platform, String text,
                                 ShareListener listener) {
        mType = TYPE_TEXT;
        mText = text;
        mPlatform = platform;
        mShareListener = buildProxyListener(listener);

        context.startActivity(ShareActivity.newInstance(context, 1));
    }

    public static void shareImage(Context context, SharePlatform platform,
                                  String urlOrPath, int defaultImgResId, ShareListener listener) {
        mType = TYPE_IMAGE;
        mPlatform = platform;
        mShareImageObject = new ShareImageObject(urlOrPath, defaultImgResId);
        mShareListener = buildProxyListener(listener);

        context.startActivity(ShareActivity.newInstance(context, 1));
    }

    public static void shareImage(Context context, SharePlatform platform,
                                  final Bitmap bitmap, int defaultImgResId, ShareListener listener) {
        mType = TYPE_IMAGE;
        mPlatform = platform;
        mShareImageObject = new ShareImageObject(bitmap, defaultImgResId);
        mShareListener = buildProxyListener(listener);

        context.startActivity(ShareActivity.newInstance(context, 1));
    }

    public static void shareMedia(Context context, SharePlatform platform,
                                  String title, String summary, String targetUrl, Bitmap thumb, int defaultImgResId, ShareListener listener) {
        mType = TYPE_MEDIA;
        mPlatform = platform;
        mShareImageObject = new ShareImageObject(thumb, defaultImgResId);
        mSummary = summary;
        mTargetUrl = targetUrl;
        mTitle = title;
        mShareListener = buildProxyListener(listener);

        context.startActivity(ShareActivity.newInstance(context, 1));
    }

    public static void shareMedia(Context context, SharePlatform platform,
                                  String title, String summary, String targetUrl, String thumbUrlOrPath, int defaultImgResId,
                                  ShareListener listener) {
        mType = TYPE_MEDIA;
        mPlatform = platform;
        mShareImageObject = new ShareImageObject(thumbUrlOrPath, defaultImgResId);
        mSummary = summary;
        mTargetUrl = targetUrl;
        mTitle = title;
        mShareListener = buildProxyListener(listener);

        context.startActivity(ShareActivity.newInstance(context, 1));
    }

    public static void shareMicroProgram(Context context, String webUrl, String mpPath,
                                         String title, String summary, Bitmap bitmap, int defaultImgResId,
                                         ShareListener listener){
        mType = TYPE_MP;
        mPlatform = SharePlatform.WX_MP;
        mShareImageObject = new ShareImageObject(bitmap, defaultImgResId);
        mSummary = summary;
        mTargetUrl = webUrl;
        mTitle = title;
        mPath = mpPath;
        mShareListener = buildProxyListener(listener);

        context.startActivity(ShareActivity.newInstance(context, 1));
    }

    private static ShareListener buildProxyListener(ShareListener listener) {
        return new ShareListenerProxy(listener);
    }

    public static void handleResult(Intent data) {
        // 微博分享会同时回调onActivityResult和onNewIntent， 而且前者返回的intent为null
        if (mShareInstance != null && data != null) {
            mShareInstance.handleResult(data);
        }
    }

    private static BaseShare getShareInstance() {
        switch (mPlatform) {
            case WX:
            case WX_TIMELINE:
            default:
                return new WXShare(mActivity, ShareConfig.get().getWxId(), mShareListener);
        }
    }

    public static void recycle() {
        mTitle = null;
        mSummary = null;
        mShareListener = null;

        // bitmap recycle
        if (mShareImageObject != null
                && mShareImageObject.getBitmap() != null
                && !mShareImageObject.getBitmap().isRecycled()) {
            mShareImageObject.getBitmap().recycle();
        }
        mShareImageObject = null;

        if (mShareInstance != null) {
            mShareInstance.recycle();
        }
        mShareInstance = null;
        if (mActivity != null){
            mActivity.finish();
        }
    }

    private static class ShareListenerProxy extends ShareListener {

        private final ShareListener mShareListener;

        ShareListenerProxy(ShareListener listener) {
            mShareListener = listener;
        }

        @Override
        public void shareSuccess() {
            ShareUtils.recycle();
            mShareListener.shareSuccess();
        }

        @Override
        public void shareFailure(Exception e) {
            ShareUtils.recycle();
            mShareListener.shareFailure(e);
        }

        @Override
        public void shareCancel() {
            ShareUtils.recycle();
            mShareListener.shareCancel();
        }
    }
}

