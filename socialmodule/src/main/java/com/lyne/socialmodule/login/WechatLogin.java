package com.lyne.socialmodule.login;

import android.app.Activity;
import android.content.Intent;

import com.lyne.socialmodule.LoginResult;
import com.lyne.socialmodule.LoginUtils;
import com.lyne.socialmodule.ShareConfig;
import com.lyne.socialmodule.token.BaseToken;
import com.lyne.socialmodule.token.WxToken;
import com.lyne.socialmodule.token.WxUser;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liht on 2018/1/24.
 */

public class WechatLogin extends BaseLogin{

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    public static final String SCOPE_USER_INFO = "snsapi_userinfo";
    private static final String SCOPE_BASE = "snsapi_base";

    private static final String BASE_URL = "https://api.weixin.qq.com/sns/";

    private IWXAPI mIWXAPI;

    private LoginUtils.LoginListener mLoginListener;

    private OkHttpClient mClient;

    private boolean fetchUserInfo;

    public WechatLogin(Activity activity, LoginUtils.LoginListener listener, boolean fetchUserInfo) {
        mLoginListener = listener;
        mIWXAPI = WXAPIFactory.createWXAPI(activity, ShareConfig.get().getWxId());
        mClient = new OkHttpClient();
        this.fetchUserInfo = fetchUserInfo;
    }

    @Override
    public void doLogin(Activity activity) {
        if (!mIWXAPI.isWXAppInstalled()){
            mLoginListener.loginFailure(new Exception("未安装微信客户端"));
        }else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = SCOPE_USER_INFO;
            req.state = String.valueOf(System.currentTimeMillis());
            mIWXAPI.sendReq(req);
        }
    }

    private void getToken(final String code) {
        Observable.fromEmitter(new Action1<Emitter<WxToken>>() {
            @Override
            public void call(Emitter<WxToken> wxTokenEmitter) {
                Request request = new Request.Builder().url(buildTokenUrl(code)).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WxToken token = WxToken.parse(jsonObject);
                    token.setCode(code);
                    wxTokenEmitter.onNext(token);
                } catch (IOException | JSONException e) {
                    wxTokenEmitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WxToken>() {
                    @Override
                    public void call(WxToken wxToken) {
                        //if (fetchUserInfo) {
                            fetchUserInfo(wxToken);
                        /*} else {
                            mLoginListener.loginSuccess(new LoginResult(LoginUtils.LoginType.WECHAT, wxToken));
                        }*/
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mLoginListener.loginFailure(new Exception(throwable.getMessage()));
                    }
                });
    }

    @Override
    public void fetchUserInfo(final BaseToken token) {
        Observable.fromEmitter(new Action1<Emitter<WxUser>>() {
            @Override
            public void call(Emitter<WxUser> wxUserEmitter) {
                Request request = new Request.Builder().url(buildUserInfoUrl(token)).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WxUser user = WxUser.parse(jsonObject);
                    wxUserEmitter.onNext(user);
                } catch (IOException | JSONException e) {
                    wxUserEmitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WxUser>() {
                    @Override
                    public void call(WxUser wxUser) {
                        mLoginListener.loginSuccess(new LoginResult(LoginUtils.LoginType.WECHAT, token, wxUser));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mLoginListener.loginFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void handleIntent(final int reqCode, int resultCode, Intent data) {
        mIWXAPI.handleIntent(data, new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {

            }

            @Override
            public void onResp(BaseResp baseResp) {
                if (baseResp instanceof SendAuth.Resp && baseResp.getType() == RETURN_MSG_TYPE_LOGIN) {
                    SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                    switch (resp.errCode) {
                        case BaseResp.ErrCode.ERR_OK:
                            WxToken wxToken = new WxToken();
                            wxToken.setCode(resp.code);
                            mLoginListener.loginSuccess(new LoginResult(LoginUtils.LoginType.WECHAT, wxToken));
                            //getToken(resp.code);
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            mLoginListener.loginCancel();
                            break;
                        case BaseResp.ErrCode.ERR_SENT_FAILED:
                            mLoginListener.loginFailure(new Exception("WX_ERR_SENT_FAILED"));
                            break;
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                            mLoginListener.loginFailure(new Exception("WX_ERR_UNSUPPORT"));
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            mLoginListener.loginFailure(new Exception("WX_ERR_AUTH_DENIED"));
                            break;
                        default:
                            mLoginListener.loginFailure(new Exception("WX_ERR_AUTH_ERROR"));
                    }
                }
            }
        });
    }

    @Override
    public void recycle() {
        if (mIWXAPI != null) {
            mIWXAPI.detach();
        }
    }
//https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
    private String buildTokenUrl(String code) {
        return BASE_URL
                + "oauth2/access_token?appid="
                + ShareConfig.get().getWxId()
                + "&secret="
                + ShareConfig.get().getWxSecret()
                + "&code="
                + code
                + "&grant_type=authorization_code";
    }

    private String buildUserInfoUrl(BaseToken token) {
        return BASE_URL
                + "userinfo?access_token="
                + token.getAccessToken()
                + "&openid="
                + token.getOpenid();
    }
}
