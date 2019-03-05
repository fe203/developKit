package com.lyne.socialmodule.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.lyne.socialmodule.ShareUtils;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.List;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by shaohui on 2016/11/18.
 */

public class QQShare implements BaseShare {

    private Tencent mTencent;
    private IUiListener mIUiListener;
    private ShareListener mShareListener;
    private Activity mActivity;

    public QQShare(Activity activity, String app_id, ShareListener shareListener) {
        mTencent = Tencent.createInstance(app_id, activity.getApplicationContext());
        mIUiListener = new IUiListener() {
            @Override
            public final void onComplete(Object o) {
                mShareListener.shareSuccess();
            }

            @Override
            public final void onError(UiError uiError) {
                mShareListener.shareFailure(new Exception(uiError == null ? "DEFAULT_QQ_SHARE_ERROR" : uiError.errorDetail));
            }

            @Override
            public final void onCancel() {
                mShareListener.shareCancel();
            }
        };
        mShareListener = shareListener;
        mActivity = activity;
    }

    @Override
    public void shareText(ShareUtils.SharePlatform platform, String text) {
        if (platform == ShareUtils.SharePlatform.QZONE) {
            shareToQZoneForText(text);
        } else {
            mShareListener.shareFailure(new Exception("QQ_NOT_SUPPORT_SHARE_TXT"));
        }
    }

    @Override
    public void shareMedia(final ShareUtils.SharePlatform platform, final String title, final String targetUrl,
                           final String summary, final ShareImageObject shareImageObject) {
        Observable.fromEmitter(new Action1<Emitter<String>>() {
            @Override
            public void call(Emitter<String> emitter) {
                try {
                    emitter.onNext(ImageDecoder.decode(mActivity, shareImageObject));
                    emitter.onCompleted();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (platform == ShareUtils.SharePlatform.QZONE) {
                            shareToQZoneForMedia(title, targetUrl, summary, s);
                        } else {
                            shareToQQForMedia(title, summary, targetUrl, s);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mShareListener.shareFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void shareImage(final ShareUtils.SharePlatform platform, final ShareImageObject shareImageObject) {
        Observable.fromEmitter(new Action1<Emitter<String>>() {
            @Override
            public void call(Emitter<String> emitter) {
                try {
                    emitter.onNext(ImageDecoder.decode(mActivity, shareImageObject));
                    emitter.onCompleted();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String localPath) {
                        if (platform == ShareUtils.SharePlatform.QZONE) {
                            shareToQzoneForImage(localPath);
                        } else {
                            shareToQQForImage(localPath);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mShareListener.shareFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void shareMP(String path, String title, String targetUrl, String summary, ShareImageObject shareImageObject) {
        mShareListener.shareFailure(new Exception("QQ木有小程序，你是呆子吗"));
    }

    @Override
    public void handleResult(Intent data) {
        Tencent.handleResultData(data, mIUiListener);
    }

    @Override
    public boolean isInstall(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return false;
        }

        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        for (PackageInfo info : packageInfos) {
            if (TextUtils.equals(info.packageName.toLowerCase(), "com.tencent.mobileqq")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void recycle() {
        if (mTencent != null) {
            mTencent.releaseResource();
            mTencent = null;
        }
        mActivity = null;
        mShareListener = null;
        mIUiListener = null;
    }

    private void shareToQQForMedia(String title, String summary, String targetUrl, String thumbUrl) {
        final Bundle params = new Bundle();
        params.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_URL, thumbUrl);
        mTencent.shareToQQ(mActivity, params, mIUiListener);
    }

    private void shareToQQForImage(String localUrl) {
        Bundle params = new Bundle();
        params.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, localUrl);
        mTencent.shareToQQ(mActivity, params, mIUiListener);
    }

    private void shareToQZoneForText(String text) {
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, text);
        mTencent.publishToQzone(mActivity, params, mIUiListener);
    }

    private void shareToQZoneForMedia(String title, String targetUrl, String summary, String imageUrl) {
        final Bundle params = new Bundle();
        final ArrayList<String> image = new ArrayList<>();
        image.add(imageUrl);
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, image);
        mTencent.shareToQzone(mActivity, params, mIUiListener);
    }

    private void shareToQzoneForImage(String imagePath) {
        final Bundle params = new Bundle();
        final ArrayList<String> image = new ArrayList<>();
        image.add(imagePath);
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, image);
        mTencent.publishToQzone(mActivity, params, mIUiListener);
    }
}
