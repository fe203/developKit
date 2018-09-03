package com.lyne.socialmodule;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lyne.fw.statusBar.MeizuStatusBarProxy;


public class ShareActivity extends Activity {

    public static final String TYPE = "extra_type";

    private int mType;

    private boolean isNew;

    public static Intent newInstance(Context context, int type) {
        Intent intent = new Intent(context, ShareActivity.class);
        if (context instanceof Application) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(TYPE, type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isNew = true;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (Build.VERSION.SDK_INT >= 23){
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                MeizuStatusBarProxy.setStatusBarDarkMode(getWindow(), true);
            }

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LoginUtils.handleIntent(-1, -1, getIntent());
        mType = getIntent().getIntExtra(TYPE, 0);
        if (mType == 1) {
            // 分享
            ShareUtils.action(this);
        } else if (mType == 2) {
            // 登录
            LoginUtils.action(this);
        } else if (mType == 3) {
            // 小程序调转
            MPUtils.action(this);
        } else {
            // handle 微信回调
            LoginUtils.handleIntent(-1, -1, getIntent());
            ShareUtils.handleResult(getIntent());
            MPUtils.handleResult(getIntent());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNew){
            isNew = false;
        }else {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        setIntent(intent);
        LoginUtils.handleIntent(-1, -1, intent);
        ShareUtils.handleResult(intent);
        MPUtils.handleResult(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoginUtils.handleIntent(requestCode, resultCode, data);
        ShareUtils.handleResult(data);
        MPUtils.handleResult(data);
    }
}
