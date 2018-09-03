package com.lyne.socialmodule.login;

import android.app.Activity;
import android.content.Intent;

import com.lyne.socialmodule.token.BaseToken;


/**
 * Created by liht on 2018/1/24.
 */

public abstract class BaseLogin {

    public abstract void doLogin(Activity activity);

    public abstract void fetchUserInfo(BaseToken token);

    public abstract void recycle();

    public abstract void handleIntent(int reqCode, int resultCode, Intent intent);
}
