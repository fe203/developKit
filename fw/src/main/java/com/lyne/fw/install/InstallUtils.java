package com.lyne.fw.install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by liht  on 2016/8/23 9:49.
 * Desc: 升级适用
 */
public class InstallUtils {

    public static void install(Context context, String path){
        if (TextUtils.isEmpty(path)){
            return;
        }
        install(context, new File(path));
    }

    private static void install(Context context, File file){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.zhouhua.juassistant.fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        }else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
