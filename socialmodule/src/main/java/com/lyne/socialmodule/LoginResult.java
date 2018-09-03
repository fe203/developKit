package com.lyne.socialmodule;


import com.lyne.socialmodule.token.BaseToken;
import com.lyne.socialmodule.token.BaseUser;

/**
 * Created by liht on 2018/1/24.
 */

public class LoginResult {
    private LoginUtils.LoginType loginType;
    private BaseToken baseToken;
    private BaseUser baseUser;

    public LoginResult(LoginUtils.LoginType loginType, BaseToken baseToken){
        this.loginType = loginType;
        this.baseToken = baseToken;
    }

    public LoginResult(LoginUtils.LoginType loginType, BaseToken baseToken, BaseUser baseUser){
        this.loginType = loginType;
        this.baseToken = baseToken;
        this.baseUser = baseUser;
    }

    public LoginUtils.LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginUtils.LoginType loginType) {
        this.loginType = loginType;
    }

    public BaseToken getBaseToken() {
        return baseToken;
    }

    public void setBaseToken(BaseToken baseToken) {
        this.baseToken = baseToken;
    }

    public BaseUser getBaseUser() {
        return baseUser;
    }

    public void setBaseUser(BaseUser baseUser) {
        this.baseUser = baseUser;
    }
}
