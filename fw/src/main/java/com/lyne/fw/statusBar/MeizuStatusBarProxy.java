package com.lyne.fw.statusBar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


import com.lyne.fw.log.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MeizuStatusBarProxy {
	private final static String TAG = "StatusBar";


	private static boolean isMiUiModel;//是否为小米系统
	private static boolean isMeizuModelFlag;//是否为魅族系统
    private static int statusBarHeight = 0;


	static {
		//初始化反射检查是否为小米系统或魅族系统
		checkMiUiModel();
		checkMeizuModel();
	}
	private static void checkMeizuModel(){

		try {
			Field darkFlag = WindowManager.LayoutParams.class
					.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
			Field meizuFlags = WindowManager.LayoutParams.class
					.getDeclaredField("meizuFlags");
			isMeizuModelFlag=true;
		}catch (Exception e){
//			e.printStackTrace();
			isMeizuModelFlag=false;
		}
		LogUtils.print(MeizuStatusBarProxy.class,"isMeizuModelFlag2 = "+isMeizuModelFlag);

	}
	private static void checkMiUiModel(){
		try {
			Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
			isMiUiModel=true;
		}catch (Exception e){
//			e.printStackTrace();
			isMiUiModel=false;
		}
		LogUtils.print(MeizuStatusBarProxy.class,"isMiUiModel = "+isMiUiModel);

	}


	/**
	 * 设置状态栏图标为深色和魅族特定的文字风格
	 * @param window 需要设置的窗口
	 * @param dark 是否把状态栏颜色设置为深色
	 * @return  boolean 成功执行返回true
	 */
	private static boolean setMeizuStatusBarDarkIcon(Window window, boolean dark) {
		if(!isMeizuModelFlag){
			return false;
		}
		boolean result = false;
		if (window != null) {
			try {
				WindowManager.LayoutParams lp = window.getAttributes();
				Field darkFlag = WindowManager.LayoutParams.class
						.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
				Field meizuFlags = WindowManager.LayoutParams.class
						.getDeclaredField("meizuFlags");
				darkFlag.setAccessible(true);
				meizuFlags.setAccessible(true);
				int bit = darkFlag.getInt(null);
				int value = meizuFlags.getInt(lp);
				if (dark) {
					value |= bit;
				} else {
					value &= ~bit;
				}
				meizuFlags.setInt(lp, value);
				window.setAttributes(lp);
				result = true;
			} catch (Exception e) {
				Log.e(TAG, "setStatusBarDarkIcon: failed");
			}
		}
		return result;
	}

	private static void setMIUIStatusBarDarkMode(Window window, boolean darkmode) {
		if(!isMiUiModel){
			return;
		}
        Class<? extends Window> clazz = window.getClass();
        try {
        int darkModeFlag = 0;
        Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
        Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
        darkModeFlag = field.getInt(layoutParams);
        Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
        extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public static void setStatusBarDarkMode(Window window, boolean darkmode){
		if (Build.VERSION.SDK_INT < 23){
			setMIUIStatusBarDarkMode(window, darkmode);
			setMeizuStatusBarDarkIcon(window, darkmode);
		}
	}

	/**
	 * 设置沉浸式窗口，设置成功后，状态栏则透明显示
	 * @param window 需要设置的窗口
	 * @param immersive 是否把窗口设置为沉浸
	 * @return boolean 成功执行返回true
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static boolean setImmersedWindow(Window window, boolean immersive) {
		boolean result = false;
		if (window != null) {
			WindowManager.LayoutParams lp = window.getAttributes();
			int trans_status = 0;
			Field flags;
			if (Build.VERSION.SDK_INT < 19) {
				try {
					trans_status = 1 << 6;
					flags = lp.getClass().getDeclaredField("meizuFlags");
					flags.setAccessible(true);
					int value = flags.getInt(lp);
					if (immersive) {
						value = value | trans_status;
					} else {
						value = value & ~trans_status;
					}
					flags.setInt(lp, value);
					result = true;
				} catch (Exception e) {
					Log.e(TAG, "setImmersedWindow: failed");
				}
			} else {
				lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
				window.setAttributes(lp);
				result = true;
			}
		}
		return result;
	}

	/**
	 * 获取状态栏高度
	 * @param context 上下文
	 * @return int 状态栏高度
	 */
	public static int getStatusBarHeight(Context context) {
        if (statusBarHeight != 0){
            return statusBarHeight;
        }
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int height = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(height);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
        statusBarHeight = 75;
		return statusBarHeight;
	}

}
