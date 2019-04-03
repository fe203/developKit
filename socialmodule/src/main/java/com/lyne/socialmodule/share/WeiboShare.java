package com.lyne.socialmodule.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Pair;

import com.lyne.socialmodule.ShareUtils;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import java.io.File;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liht on 2018/3/14.
 */

public class WeiboShare implements BaseShare {

    private ShareListener mShareListener;
    private Activity mActivity;

    private WbShareHandler mShareHandler;
    private WbShareCallback mShareCallback;

    public WeiboShare(Activity activity, String app_id, String redirectUrl,  ShareListener shareListener){
        WbSdk.install(activity.getApplicationContext(), new AuthInfo(activity.getApplicationContext(), app_id, redirectUrl, ""));
        mShareHandler = new WbShareHandler(activity);
        mShareHandler.registerApp();

        mShareListener = shareListener;
        mActivity = activity;

        mShareCallback = new WbShareCallback() {
            @Override
            public void onWbShareSuccess() {
                mShareListener.shareSuccess();
            }

            @Override
            public void onWbShareCancel() {
                mShareListener.shareCancel();
            }

            @Override
            public void onWbShareFail() {
                mShareListener.shareSuccess();
            }
        };
    }

    @Override
    public void shareText(ShareUtils.SharePlatform platform, String text) {
        WeiboMultiMessage message = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.text = text;
        message.textObject = textObject;
        sendRequest(message);
    }

    @Override
    public void shareMedia(ShareUtils.SharePlatform platform, final String title, final String targetUrl, final String summary, final ShareImageObject shareImageObject) {

        Observable.fromEmitter(new Action1<Emitter<Pair<Bitmap, byte[]>>>() {
            @Override
            public void call(Emitter<Pair<Bitmap, byte[]>> emitter) {
                try {
                    String imagePath = ImageDecoder.decode(mActivity.getApplicationContext(), shareImageObject);
                    emitter.onNext(Pair.create(BitmapFactory.decodeFile(imagePath),
                            ImageDecoder.compress2Byte(imagePath, -1, -1)));
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<Bitmap, byte[]>>() {
                    @Override
                    public void call(Pair<Bitmap, byte[]> pair) {
                        WeiboMultiMessage message = new WeiboMultiMessage();

                        TextObject textObject = new TextObject();
                        textObject.text = summary;
                        textObject.actionUrl = targetUrl;
                        textObject.title = title;
                        message.textObject = textObject;

                        ImageObject imageObject = new ImageObject();
                        imageObject.setImageObject(pair.first);
                        message.imageObject = imageObject;
                        sendRequest(message);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mShareListener.shareFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void shareImage(ShareUtils.SharePlatform platform, final ShareImageObject shareImageObject) {

        Observable.fromEmitter(new Action1<Emitter<Bitmap>>() {
            @Override
            public void call(Emitter<Bitmap> emitter) {
                try {
                    String imagePath = ImageDecoder.decode(mActivity.getApplicationContext(), shareImageObject);
                    emitter.onNext(BitmapFactory.decodeFile(imagePath));
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        WeiboMultiMessage message = new WeiboMultiMessage();
                        ImageObject imageObject = new ImageObject();
                        imageObject.setImageObject(bitmap);
                        message.imageObject = imageObject;
                        sendRequest(message);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mShareListener.shareFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void shareVideo(ShareUtils.SharePlatform platform, final String title, final String targetUrl, final String summary, final ShareImageObject shareImageObject) {

        Observable.fromEmitter(new Action1<Emitter<String>>() {
            @Override
            public void call(Emitter<String> emitter) {
                try {
                    String imagePath = ImageDecoder.decode(mActivity.getApplicationContext(), shareImageObject);
                    emitter.onNext(imagePath);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String path) {

                        WeiboMultiMessage message = new WeiboMultiMessage();

                        TextObject textObject = new TextObject();
                        textObject.text = summary;
                        textObject.title = title;
                        message.textObject = textObject;

                        VideoSourceObject videoSourceObject = new VideoSourceObject();
                        videoSourceObject.coverPath = Uri.fromFile(new File(path));
                        videoSourceObject.videoPath = Uri.fromFile(new File(targetUrl));
                        message.videoSourceObject = videoSourceObject;
                        sendRequest(message);
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
        mShareListener.shareFailure(new Exception("微博木有小程序，你是呆子吗"));
    }

    @Override
    public void handleResult(Intent data) {
        mShareHandler.doResultIntent(data, mShareCallback);
    }

    @Override
    public boolean isInstall(Context context) {
        return mShareHandler.isWbAppInstalled();
    }

    @Override
    public void recycle() {
        if (mShareHandler != null) {
            mShareHandler = null;
        }
        mActivity = null;
        mShareListener = null;
        mShareCallback = null;
    }

    private void sendRequest(WeiboMultiMessage message) {
        mShareHandler.shareMessage(message, false);
    }
}
