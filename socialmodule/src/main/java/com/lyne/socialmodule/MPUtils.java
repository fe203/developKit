package com.lyne.socialmodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lyne.socialmodule.mp.MPListener;
import com.lyne.socialmodule.mp.WXMiniProgram;


/**
 * Created by liht on 2018/7/24.
 */

public class MPUtils {

    private static String mPath;
    public static MPListener mCallListener;
    private static Activity mActivity;
    private static WXMiniProgram wxMiniProgram;

    public static void jumpMicroProgram(Context context, String path, MPListener listener){
        mPath = path;
        mCallListener = buildProxyListener(listener);

        context.startActivity(ShareActivity.newInstance(context, 3));
    }

    private static MPListener buildProxyListener(MPListener listener) {
        return new MPUtils.MPListenerProxy(listener);
    }

    static void action(ShareActivity activity) {
        mActivity = activity;

        // 防止之后调用 NullPointException
        if (mCallListener == null) {
            activity.finish();
            return;
        }

        wxMiniProgram = new WXMiniProgram(mActivity, ShareConfig.get().getWxId(), mCallListener);
        wxMiniProgram.jumpTo(mPath);

    }

    static void handleResult(Intent intent) {
        if (wxMiniProgram != null && intent != null) {
            wxMiniProgram.handleResult(intent);
        }
    }

    static void recycle() {
        mCallListener = null;
        mPath = null;

        wxMiniProgram = null;
        if (mActivity != null){
            mActivity.finish();
        }
    }

    private static class MPListenerProxy extends MPListener {

        private final MPListener mMPListener;

        MPListenerProxy(MPListener listener) {
            mMPListener = listener;
        }

        @Override
        public void callSuccess() {
            MPUtils.recycle();
            if (mMPListener != null){
                mMPListener.callSuccess();
            }
        }

        @Override
        public void callFailure(Exception e) {
            MPUtils.recycle();
            if (mMPListener != null){
                mMPListener.callFailure(e);
            }
        }

        @Override
        public void callCancel() {
            MPUtils.recycle();
            if (mMPListener != null){
                mMPListener.callCancel();
            }
        }
    }
}
