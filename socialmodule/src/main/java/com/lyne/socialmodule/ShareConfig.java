package com.lyne.socialmodule;

/**
 * Created by liht on 2018/1/24.
 */

public class ShareConfig {

    private static ShareConfig inst = new ShareConfig();

    private String qqId;
    private String wxId;
    private String wxMiniProgramId;
    private String wxSecret;
    private String weiboId;
    private String weiboRedirectUrl;

    private String alipayId;
    private String alipayPId;

    private ShareConfig(){}

    public static ShareConfig get(){
        return inst;
    }

    public String getQqId() {
        return qqId;
    }

    public ShareConfig setQqId(String qqId) {
        this.qqId = qqId;
        return this;
    }

    public String getWxId() {
        return wxId;
    }

    public ShareConfig setWxId(String wxId) {
        this.wxId = wxId;
        return this;
    }

    public ShareConfig setWxMiniProgramId(String miniProgramId) {
        this.wxMiniProgramId = miniProgramId;
        return this;
    }

    public String getWxMiniProgramId() {
        return wxMiniProgramId;
    }

    public String getWxSecret() {
        return wxSecret;
    }

    public ShareConfig setWxSecret(String wxSecret) {
        this.wxSecret = wxSecret;
        return this;
    }

    public String getWeiboId() {
        return weiboId;
    }

    public ShareConfig setWeiboId(String weiboId) {
        this.weiboId = weiboId;
        return this;
    }

    public String getWeiboRedirectUrl() {
        return weiboRedirectUrl;
    }

    public ShareConfig setWeiboRedirectUrl(String weiboRedirectUrl) {
        this.weiboRedirectUrl = weiboRedirectUrl;
        return this;
    }

    public String getAlipayId() {
        return alipayId;
    }

    public ShareConfig setAlipayId(String alipayId) {
        this.alipayId = alipayId;
        return this;
    }

    public String getAlipayPId() {
        return alipayPId;
    }

    public ShareConfig setAlipayPId(String alipayPId) {
        this.alipayPId = alipayPId;
        return this;
    }
}
