package com.lyne.socialmodule.mp;

import android.app.Activity;
import android.content.Intent;

import com.lyne.socialmodule.BuildConfig;
import com.lyne.socialmodule.MPUtils;
import com.lyne.socialmodule.ShareConfig;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by liht on 2018/7/24.
 */

public class WXMiniProgram {

    private IWXAPI mIWXAPI;
    private MPListener mCallListener;
    private Activity mActivity;


    public WXMiniProgram(Activity activity, String appId, MPListener callListener) {
        mIWXAPI = WXAPIFactory.createWXAPI(activity.getApplicationContext(), appId, true);
        mIWXAPI.registerApp(appId);

        mActivity = activity;
        mCallListener = callListener;
    }

    public void jumpTo(String path) {
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = ShareConfig.get().getWxMiniProgramId();
        req.path = path;

        req.miniprogramType = BuildConfig.DEBUG ? WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW : WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;

        mIWXAPI.sendReq(req);
    }

    public void handleResult(Intent intent) {
        mIWXAPI.handleIntent(intent, new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {

            }

            @Override
            public void onResp(BaseResp baseResp) {
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        MPUtils.mCallListener.callSuccess();
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        MPUtils.mCallListener.callCancel();
                        break;
                    default:
                        MPUtils.mCallListener.callFailure(new Exception(baseResp.errStr));
                }
            }
        });
    }
}
