package com.lyne.fw.log;

import android.util.Log;


public class LogUtils {
    public static final int LEVEL_DEBUG = 0;
    public static final int LEVEL_WARN = 1;
    public static final int LEVEL_ERROR = 2;

    public static int logLevel = LEVEL_ERROR;

    public static void initLogConfig(int logLevel){
        LogUtils.logLevel = logLevel;
    }

    public static void print(Class claxx, String msg){
        if(logLevel <= LEVEL_DEBUG){
            Log.d(LogUtils.class.getSimpleName() + "_" + (claxx == null? "" : claxx.getSimpleName()), msg);
        }
    }

    public static void warn(Class claxx, String message){
        if(logLevel <= LEVEL_WARN){
            Log.w(LogUtils.class.getSimpleName() + "_" + (claxx == null? "" : claxx.getSimpleName()) , message);
        }
    }

    public static void error(Class claxx, String message){
        Log.e(LogUtils.class.getSimpleName() + "_" + (claxx == null? "" : claxx.getSimpleName()) , message);
    }
}
