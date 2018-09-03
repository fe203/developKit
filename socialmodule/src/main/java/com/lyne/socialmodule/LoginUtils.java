package com.lyne.socialmodule;

import android.app.Activity;
import android.content.Intent;

import com.lyne.socialmodule.login.AlipayLogin;
import com.lyne.socialmodule.login.BaseLogin;
import com.lyne.socialmodule.login.WechatLogin;


/**
 * Created by liht on 2018/1/24.
 */

public class LoginUtils {

    private static BaseLogin mLoginInstance;
    private static LoginListenerProxy mLoginListener;
    private static boolean isFetchUserInfo = true;
    private static LoginType mLoginType;

    private static Activity mActivity;

    public static void handleIntent(int reqCode, int resultCode, Intent intent) {
        if (mLoginInstance != null){
            mLoginInstance.handleIntent(reqCode, resultCode, intent);
        }
    }

    public enum LoginType{
        WECHAT,
        QQ,
        ALIPAY,
    }

    public static void login(Activity activity, LoginType type, LoginListener loginListener){
        mLoginListener = new LoginListenerProxy(loginListener);
        mLoginType = type;
        activity.startActivity(ShareActivity.newInstance(activity, 2));
        activity.overridePendingTransition(0, 0);
//        action(activity);
    }

    public static void action(Activity activity){
        mActivity = activity;
        switch (mLoginType){
            case WECHAT:
                mLoginInstance = new WechatLogin(activity, mLoginListener, isFetchUserInfo);
                break;
            case ALIPAY:
                mLoginInstance = new AlipayLogin(activity, mLoginListener, isFetchUserInfo);

        }
        mLoginInstance.doLogin(activity);
    }

    public static void recycle() {
        if (mLoginInstance != null) {
            mLoginInstance.recycle();
        }
        mLoginInstance = null;
        mLoginListener = null;
        isFetchUserInfo = false;
        if (mActivity != null){
            mActivity.finish();
        }
        mActivity.overridePendingTransition(0, 0);
    }

    private static class LoginListenerProxy implements LoginListener {

        private LoginListener mListener;

        LoginListenerProxy(LoginListener listener) {
            mListener = listener;
        }

        @Override
        public void loginSuccess(LoginResult result) {
            mListener.loginSuccess(result);
            recycle();
        }

        @Override
        public void loginFailure(Exception e) {
            mListener.loginFailure(e);
            recycle();
        }

        @Override
        public void loginCancel() {
            mListener.loginCancel();
            recycle();
        }
    }

    public interface LoginListener{
        void loginSuccess(LoginResult result);

        void loginFailure(Exception e);

        void loginCancel();
    }
}
