package com.lyne.socialmodule.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alipay.sdk.app.AuthTask;
import com.lyne.socialmodule.LoginResult;
import com.lyne.socialmodule.LoginUtils;
import com.lyne.socialmodule.ShareConfig;
import com.lyne.socialmodule.login.utils.AuthResult;
import com.lyne.socialmodule.login.utils.OrderInfoUtil2_0;
import com.lyne.socialmodule.token.BaseToken;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by liht on 2018/7/6.
 */

public class AlipayLogin extends BaseLogin {

    private static final String RSA2_PRIVATE = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCNmLguPBymkO3p" +
            "c0yiiHyxxKDKUTg9+G9ZnitDkm8iP2T/znWDoFK/8qwILypqdAyZas0SOg8qe110" +
            "XtjUD1LcgOjXSQn66KyJzZ/Nucc+nGnbFYWZzH8WiAs0V0Mo4jVy5yy2IQmee+q1" +
            "F6Hsd/tqEYLSa2fpr3pqhEkVLwfDiZfKiCotW0oTTgjEMszIbWiHs79GY9U06Syo" +
            "631i2l6VNzcRBQ1/aA1iPBjfibJDIm++YRMMpYD9E2pFggd+gPoh3lg6PwZVa8bj" +
            "yxIoNCfZ/wcDdPMfgc8smFbbGmmbz/XTnzbbQbn54CXneHdw8TnkOyQzLvfK/aqw" +
            "h6OhEP7lAgMBAAECggEAUBpMX4OMJcKphL7AZNZXzG1TFQFbkts/ivBKw2BM1JAO" +
            "gBR5MGx7Q7FyFvMvyKc8FXJw7tW/jKXbmaCqg1Tj6HBYyKm+Jp6INTEJLWXizI3J" +
            "6LHFMduZLdLqm8Kd8Lv/xq/3AFWyTmEFx0rGRLMfnMKpQoaWc4Hc+/M4MO65MqmA" +
            "tQj48GpnFOAN6bUVEjYC1sm95cNd7Hk6wiiLoGpaWpz68u4HUI0fIMpyGImYJMD2" +
            "zAoNAE1oKylpH0tUIGQ9EbJwq7Xm621oMWJf8wm9UNCt7XB6D9AXWqHVX7AtO0yq" +
            "bV9UDJx5qrToo2xkbYyuXuPEFVxHKSJYf8VOMb4+JQKBgQDsFt4N8bjVPiXAIRzI" +
            "a8rtVrzLZaDjf1roadoVUII/DLY0lurmbYxONn/iMYvoyoP7JdeB7bKs+HNjxtIe" +
            "IZlyBZXOsi2H095I/v0kkx2Ar2zwIs5KjbKbkACKvgmJTjL9i2s10cLwkACmFhge" +
            "1++luv7eCkVyx417YfvhiJo7PwKBgQCZicSDz55YMi32BziiCKLACqi3E58Pu5/E" +
            "2tH6ub9P2qZFi4tNSNUV8hM8s2u3Lz1XP/nTqwhlWxktW5miJcCVsWJ9hBLGLUhj" +
            "0n0W/sjG3q+DVRQ6rsD2ZsZSmNxILXNj6mwTdbgqLYpvZw+g6dBQDAbUhIm7HFO0" +
            "W2IvE1uw2wKBgGeGd4Th1E0d3WvsmTSBoBCHEm6WeACKXhDAKfNixFpldpiE/Shi" +
            "LT42w8kqNNG1zD4bD+9XjhwvLlo4Wvb/gMvxCwoCM9j13OlhdxvMrwk446pBePhU" +
            "Plfcw4ATaRcehVmNA0wsWTEn1EEAOz97LQkdgJlZI677Nl0+UDdLsWX9AoGAXA6M" +
            "MvPC3uKHSCIgRJak7jv12H79ObOnIaKJj6jbGgfg9YWrahUnHddczWJOTTEXuD51" +
            "jgHYMN0kBVwMi3nTNG18vgD4OLZh1ugXdDgjLYnK9hWG4YBx1crdUhywxt3pd5jQ" +
            "YAKO/0r627yRVEucSMJNPhKRegyL63F3Nv6GVU0CgYAMRrSCVPpqb6Rgj5c/8qbx" +
            "sOFhMS9C0kBp7DH44E1WG5JZlWWdKVFYgmWHAGI3mXQjRMWSOu8f1hSppRzVbGqj" +
            "9DXViKaOjQQxwYp2tH6Gu9uWbX6P6rK7C8mdL1I8R+tInyOzuJ1f1+BHr3vMmuUT" +
            "5TLAt46gNSHoWhQ4OL4FpA==";

    private LoginUtils.LoginListener mLoginListener;
    private boolean fetchUserInfo;

    public AlipayLogin(Activity activity, LoginUtils.LoginListener listener, boolean fetchUserInfo) {
        mLoginListener = listener;
        this.fetchUserInfo = fetchUserInfo;
    }

    @Override
    public void doLogin(final Activity activity) {
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(ShareConfig.get().getAlipayPId(), ShareConfig.get().getAlipayId(), String.valueOf(System.currentTimeMillis()), true);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = RSA2_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, true);
        final String authInfo = info + "&" + sign;

        Observable.create(new Observable.OnSubscribe<LoginResult>() {
            @Override
            public void call(Subscriber<? super LoginResult> subscriber) {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(activity);
                // 调用授权接口，获取授权结果

                Map<String, String> result = authTask.authV2(authInfo, true);

                AuthResult authResult = new AuthResult(result, true);
                if (TextUtils.equals(authResult.getResultStatus(), "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {

                    BaseToken baseToken = new BaseToken();
                    baseToken.setOpenid(authResult.getAlipayOpenId());
                    baseToken.setCode(authResult.getAuthCode());

                    LoginResult loginResult = new LoginResult(LoginUtils.LoginType.ALIPAY, baseToken);

                    subscriber.onNext(loginResult);
                    subscriber.onCompleted();
                }else {
                    subscriber.onError(new Throwable("授权失败"));
                }

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LoginResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginListener.loginFailure(new Exception(e));
                    }

                    @Override
                    public void onNext(LoginResult loginResult) {
                        mLoginListener.loginSuccess(loginResult);
                    }
                });

    }

    @Override
    public void fetchUserInfo(BaseToken token) {

    }

    @Override
    public void recycle() {

    }

    @Override
    public void handleIntent(int reqCode, int resultCode, Intent intent) {

    }
}
