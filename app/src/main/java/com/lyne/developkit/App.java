package com.lyne.developkit;

import android.app.Application;

import com.lyne.socialmodule.ShareConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ShareConfig.get()
                .setQqId("101553813")
                .setWeiboId("1383999554")
        .setWeiboRedirectUrl("https://www.sina.com");
    }
}
