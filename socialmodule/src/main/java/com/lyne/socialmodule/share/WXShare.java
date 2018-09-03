package com.lyne.socialmodule.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import com.lyne.socialmodule.BuildConfig;
import com.lyne.socialmodule.ShareConfig;
import com.lyne.socialmodule.ShareUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by shaohui on 2016/11/18.
 */

public class WXShare implements BaseShare {

    /**
     * 微信分享限制thumb image必须小于32Kb，否则点击分享会没有反应
     */

    private IWXAPI mIWXAPI;
    private ShareListener mShareListener;
    private Activity mActivity;

    private static final int THUMB_SIZE = 32 * 1024 * 8;

    private static final int TARGET_SIZE = 200;

    public WXShare(Activity activity, String appId, ShareListener shareListener) {
        mIWXAPI = WXAPIFactory.createWXAPI(activity.getApplicationContext(), appId, true);
        mIWXAPI.registerApp(appId);

        mActivity = activity;
        mShareListener = shareListener;
    }

    @Override
    public void shareText(ShareUtils.SharePlatform platform, String text) {
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = textObject;
        message.description = text;

        sendMessage(platform, message, buildTransaction("text"));
    }

    @Override
    public void shareMedia(
            final ShareUtils.SharePlatform platform, final String title, final String targetUrl, final String summary,
            final ShareImageObject shareImageObject) {
        Observable.fromEmitter(new Action1<Emitter<byte[]>>() {

            @Override
            public void call(Emitter<byte[]> emitter) {
                try {
                    String imagePath = ImageDecoder.decode(mActivity.getApplicationContext(), shareImageObject);
                    emitter.onNext(ImageDecoder.compress2Byte(imagePath, TARGET_SIZE, THUMB_SIZE));
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<byte[]>() {
                    @Override
                    public void call(byte[] bytes) {
                        WXWebpageObject webpageObject = new WXWebpageObject();
                        webpageObject.webpageUrl = targetUrl;

                        WXMediaMessage message = new WXMediaMessage(webpageObject);
                        message.title = title;
                        message.description = summary;
                        message.thumbData = bytes;

                        sendMessage(platform, message, buildTransaction("webPage"));
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
        Observable.fromEmitter(new Action1<Emitter<Pair<Bitmap, byte[]>>>() {
            @Override
            public void call(Emitter<Pair<Bitmap, byte[]>> emitter) {
                try {
                    String imagePath = ImageDecoder.decode(mActivity.getApplicationContext(), shareImageObject);
                    emitter.onNext(Pair.create(BitmapFactory.decodeFile(imagePath),
                            ImageDecoder.compress2Byte(imagePath, TARGET_SIZE, THUMB_SIZE)));
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
                        WXImageObject imageObject = new WXImageObject(pair.first);

                        WXMediaMessage message = new WXMediaMessage();
                        message.mediaObject = imageObject;
                        message.thumbData = pair.second;

                        sendMessage(platform, message, buildTransaction("image"));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mShareListener.shareFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void shareMP(final String path, final String title, final String targetUrl, final String summary, final ShareImageObject shareImageObject){
        Observable.fromEmitter(new Action1<Emitter<byte[]>>() {

            @Override
            public void call(Emitter<byte[]> emitter) {
                try {
                    String imagePath = ImageDecoder.decode(mActivity.getApplicationContext(), shareImageObject);
                    emitter.onNext(ImageDecoder.compress2Byte(imagePath, 1000, 128 * 1024));
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<byte[]>() {
                    @Override
                    public void call(byte[] bytes) {
                        WXMiniProgramObject miniProgramObject = new WXMiniProgramObject();
                        miniProgramObject.webpageUrl = targetUrl;
                        miniProgramObject.userName = ShareConfig.get().getWxMiniProgramId();
                        miniProgramObject.path = path;
                        miniProgramObject.miniprogramType = BuildConfig.DEBUG ? WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW : WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;

                        WXMediaMessage message = new WXMediaMessage(miniProgramObject);
                        message.title = title;
                        message.description = summary;
                        message.thumbData = bytes;

                        sendMessage(ShareUtils.SharePlatform.WX_MP, message, "miniProgram");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mShareListener.shareFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void handleResult(Intent data) {
        mIWXAPI.handleIntent(data, new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {

            }

            @Override
            public void onResp(BaseResp baseResp) {
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        ShareUtils.mShareListener.shareSuccess();
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        ShareUtils.mShareListener.shareCancel();
                        break;
                    default:
                        ShareUtils.mShareListener.shareFailure(new Exception(baseResp.errStr));
                }
            }
        });
    }

    @Override
    public boolean isInstall(Context context) {
        return mIWXAPI.isWXAppInstalled();
    }

    @Override
    public void recycle() {
        mIWXAPI.detach();
        mShareListener = null;
        mActivity = null;
    }

    private void sendMessage(ShareUtils.SharePlatform platform, WXMediaMessage message, String transaction) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = message;
        req.scene = platform == ShareUtils.SharePlatform.WX_TIMELINE ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        mIWXAPI.sendReq(req);
    }

    private String buildTransaction(String type) {
        return System.currentTimeMillis() + type;
    }

}
