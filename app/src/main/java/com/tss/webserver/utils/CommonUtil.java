package com.tss.webserver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class CommonUtil {

	/**
	 * 判断是否为芯片卡
	 * 
	 * @param track2data
	 * @return
	 */
	public static boolean isIcCard(String track2data) {

		if ("".equals(track2data) || track2data == null) {
			return false;
		}
		if ((!track2data.contains("=")) && (!track2data.contains("D"))) {
			return false;
		}
		String temp[] = null;
		String key = "";
		if (track2data.contains("=")) {
			temp = track2data.split("=");
			key = temp[1].substring(4, 5);
		} else if (track2data.contains("D")) {
			temp = track2data.split("D");
			key = temp[1].substring(4, 5);
		} else {
			return false;
		}
		return "2".equals(key) || "6".equals(key);
	}

	/**
	 * 卡号加密保留前6位与末尾4位，中间用*号代替
	 * 
	 * @param carno
	 * @return
	 */
	public static String getMarkCarno(String carno) {
		String maskNo = "*********";
		String start = carno.substring(0, 6);
		String end = carno.substring(carno.length() - 4);
		maskNo = start + "******" + end;
		return maskNo;
	}

	/**
	 * 获取连接的IP与端口例：127.0.0.1:8888
	 * 
	 * @param context
	 * @return
	 */
	public static String getIpAndPort(Context context) {

		// String
		// url=context.getResources().getString(R.string.address_set_shanghai_inner);//上海内网
		//// String url =
		// context.getResources().getString(R.string.address_set_shanghai_out);//上海外网
		//// String url =
		// context.getResources().getString(R.string.address_set_shanghai_proc);//生产
		//
		//// String url =
		// context.getResources().getString(R.string.address_set_shanghai_3g_proc);
		return null;
	}

	/**
	 * 获取连接的3GIP与端口例：127.0.0.1:8888
	 * 
	 * @param context
	 * @return
	 */
	public static String getIpAndPort3G(Context context) {
//		String url = context.getResources().getString(R.string.address_set_shanghai_3g);
		return "";
	}

	public static void setDateTime(int year, int month, int day, int hour, int minute)
			throws IOException, InterruptedException {

		requestPermission();

		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);

		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(when);
		}

		long now = Calendar.getInstance().getTimeInMillis();
		// Log.d(TAG, "set tm="+when + ", now tm="+now);

		if (now - when > 1000)
			throw new IOException("failed to set Date.");

	}

	static void requestPermission() throws InterruptedException, IOException {
		createSuProcess("chmod 666 /dev/alarm").waitFor();
	}

	static Process createSuProcess() throws IOException {
		File rootUser = new File("/system/xbin/ru");
		if (rootUser.exists()) {
			return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
		} else {
			return Runtime.getRuntime().exec("su");
		}
	}

	static Process createSuProcess(String cmd) throws IOException {

		DataOutputStream os = null;
		Process process = createSuProcess();

		try {
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit $?\n");
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}

		return process;
	}

	public static String getAppVersion(Context context) {
		String version = "";
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
			version = "0.0.0";
			e.printStackTrace();
		}
		version = "V" + version;
		return version;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {

			String name = info.getTypeName();
			Log.d("==", "当前网络名称：" + name);
			return true;
		}
		return false;
	}

	/**
	 * 判断是否安装指定应用
	 * 
	 * @param context
	 * @param packageName
	 *            应用包名
	 * @return
	 */
	public static boolean isInstallApp(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		if (null == pm) {
			return false;
		}
		PackageInfo info = null;
		try {
			info = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			info = null;
			e.printStackTrace();
		}
		if (null == info) {
			return false;
		}
		return true;

	}

	/**
	 * 返回当前程序版本名称
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// Get the package info
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo("com.lkl.cloudpos.payment", 0);
			versionName = pi.versionName;
		} catch (Exception e) {
		}
		return versionName;
	}
	
	public static String getNetworkType(Context context){
		String type = null;
		 ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo info = connectMgr.getActiveNetworkInfo();
		 if(info == null){
			 return "Unconnected";
		 }
		 if(info.getType() == ConnectivityManager.TYPE_WIFI){
			 type = "WIFI";
		 }
		 if(info !=null && info.getType() ==  ConnectivityManager.TYPE_MOBILE){
			 switch (info.getSubtype()) {
			case TelephonyManager.NETWORK_TYPE_CDMA:
				type = "CDMA";
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				type = "EDGE";
				break;			
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				type = "EVDO0";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				type = "EVDOA";
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:
				type = "GPRS";
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				type = "HSDPA";
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA:
				type = "HSPA";
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				type = "HSUPA";
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				type = "UMTS";
			    break;  
			default:
				type = "UNKNOWN";
				break;
			}
		 }
		 return type;
	}
	
    public static boolean is960F(Context cnt){
    	WindowManager wm = (WindowManager) cnt.getSystemService(Context.WINDOW_SERVICE);

    	int width = wm.getDefaultDisplay().getWidth();
    	int height = wm.getDefaultDisplay().getHeight();
    	if(width == 1280 && height == 800 ){
    		//c960F
    		return true;
    	}
    	
    	return false;
    }
    public static boolean is960E(Context cnt){
    	WindowManager wm = (WindowManager) cnt.getSystemService(Context.WINDOW_SERVICE);

    	int width = wm.getDefaultDisplay().getWidth();
    	int height = wm.getDefaultDisplay().getHeight();
    	if(width == 1024 && height == 768 ){
    		//c960E
    		return true;
    	}
    	
    	return false;
    }
    public static boolean isK9(Context cnt){
    	WindowManager wm = (WindowManager) cnt.getSystemService(Context.WINDOW_SERVICE);

    	int width = wm.getDefaultDisplay().getWidth();
    	int height = wm.getDefaultDisplay().getHeight();
    	if(width == 720 && height == 1280 ){
    		//k9
    		return true;
    	}
    	
    	return false;
    }
	/**
	 * 保存临时值
	 * 
	 * @param key
	 * @param value
	 */
	public static void setTempValue(Context contex, String key, String value) {
		SharedPreferences preferences = contex.getSharedPreferences("TEMP", Activity.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// 用putString的方法保存数据
		editor.putString(key, value);
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 获取临时值
	 * 
	 * @param key
	 * @return
	 */
	public static String getTempValue(Context mContext, String key, String defaultValue) {
		SharedPreferences preferences = mContext.getSharedPreferences("TEMP", Activity.MODE_PRIVATE);
		return preferences.getString(key, defaultValue);
	}
	
	
	/**
	 * 保存临时值
	 * 
	 * @param key
	 * @param value
	 */
	public static void setCameraMode(Context contex, String value) {
		SharedPreferences preferences = contex.getSharedPreferences("TEMP", Activity.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// 用putString的方法保存数据
		editor.putString("camera_mode", value);
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 获取临时值
	 * 
	 * @param key
	 * @return
	 */
	public static String getCameraMode(Context mContext) {
		String defaultValue = "slide";
		if(is960F(mContext)){
			defaultValue = "front";
		}
		SharedPreferences preferences = mContext.getSharedPreferences("TEMP", Activity.MODE_PRIVATE);
		return preferences.getString("camera_mode", defaultValue);
	}
}
