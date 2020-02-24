package com.tss.webserver.utils;

import android.util.Log;

/**
 * 日志工具类
 * 
 * 
 */
public class LogUtil {

	private final static String TAG = "AllinpaySYB";

	private static boolean isDebug = true;

	public static void debug(String msg) {
		if (isDebug) {
			Log.d(TAG, msg);
		}
	}

	public static void debug(String tag, String msg) {
		if (isDebug) {
			Log.d(tag, msg);
		}
	}

	public static void info(String msg) {
		if (isDebug) {
			Log.i(TAG, msg);
		}
	}

	public static void info(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void error(String msg) {
		if (isDebug) {
			Log.e(TAG, msg);
		}
	}

	public static void error(String tag, String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}

	public static void warn(String msg) {
		if (isDebug) {
			Log.w(TAG, msg);
		}
	}

	public static void warn(String tag, String msg) {
		if (isDebug) {
			Log.w(tag, msg);
		}
	}
}
