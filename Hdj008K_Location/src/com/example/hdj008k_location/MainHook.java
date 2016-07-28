package com.example.hdj008k_location;

import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.System;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.hdj008k_location.obj.WifiData;
import com.example.hdj008k_location.util.ListToString;
import com.example.hdj008k_location.util.RandomData;
import com.example.hdj008k_location.util.RecordAppFileHandlerHelper;
import com.example.hdj008k_location.util.SetSystemValueHelper;
import com.example.hdj008k_location.util.WifiDataHelper;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * 
 *
 */
public class MainHook implements IXposedHookLoadPackage {

	private static boolean init = false;

	private static String appPackageName = null;

	private static String modle = "";// 模式

	private String thisPackageName = "";

	public XSharedPreferences pre;

	/**
	 * 使用SharedPreferences做共享数据
	 * 
	 * @param lpparam
	 */
	private void readData(LoadPackageParam lpparam) {

		try {

			thisPackageName = this.getClass().getPackage().getName();// 自己的应用包名
			pre = new XSharedPreferences(thisPackageName, "prefs");
			appPackageName = pre.getString("packageName", null);
			// modle = pre.getString("modle", "3");

			if (lpparam.packageName.equals(thisPackageName))// 自己的就不hook
				return;
			if (!pre.getBoolean("isGlobal", true)) {
				if (!lpparam.packageName.equals(appPackageName))
					return;
			} else if (lpparam.packageName.equals(appPackageName)) {
				Log.e("MainHook--packageName", "TTT------appPackageName = "
						+ lpparam.packageName);
			} else if (lpparam.packageName.equals("com.android.browser")) {
				Log.e("MainHook--packageName", "TTT------browserPackageName = "
						+ lpparam.packageName);
			} else if (!WhetherThePackageName(lpparam.packageName)) {
				return;
			}
			
			if (android.os.Process.myUid() <= 1000)
				return;

			Log.e("MainHook--readData2", "TTT------packageName = "
					+ lpparam.packageName);
			HookAll(lpparam);

		} catch (Throwable e) {
			Log.e("TTT",
					"{" + lpparam.packageName + "}读取储存内容失败:" + e.getMessage());
		}
	}

	/** 判断包名是否与记录的包名相同 */
	public boolean WhetherThePackageName(String name) {
		String s = RecordAppFileHandlerHelper
				.getFileAllContent("InstallApkPackageName.txt");
		String[] packName = s.split("\n");

		for (int i = 0; i < packName.length; i++) {
			if (packName[i].equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if ("".equals(lpparam.packageName))// System
		{
			return;
		}
		readData(lpparam);
	}

	private void HookAll(LoadPackageParam lpparam) {

		String packageName = lpparam.packageName;
		ClassLoader classLoader = lpparam.classLoader;

		try {
//
			if (pre.getString("version",null) != null)
				XposedHelpers.findField(android.os.Build.VERSION.class,
						"RELEASE").set(null, pre.getString("version",null));// 系统版本
			if (pre.getString("sdk",null) != null)
				XposedHelpers.findField(android.os.Build.VERSION.class, "SDK")
						.set(null, pre.getString("sdk",null));// 系统版本值
			if (!TextUtils.isEmpty(pre.getString("manufacturer", null)))
				XposedHelpers.findField(android.os.Build.class, "MANUFACTURER")
						.set(null, pre.getString("manufacturer", ""));// 制造商
			if (pre.getString("brand",null) != null)
				XposedHelpers.findField(android.os.Build.class, "BRAND").set(
						null, pre.getString("brand",null));// 品牌
			if (pre.getString("xh",null) != null)
				XposedHelpers.findField(android.os.Build.class, "MODEL").set(
						null, pre.getString("xh",null));// 机型
			if (!TextUtils.isEmpty(pre.getString("device", null)))
				XposedHelpers.findField(android.os.Build.class, "DEVICE").set(
						null, pre.getString("device", ""));// 设备名
			if (!TextUtils.isEmpty(pre.getString("product", null)))
				XposedHelpers.findField(android.os.Build.class, "PRODUCT").set(
						null, pre.getString("product", ""));// 产品名
			if (!TextUtils.isEmpty(pre.getString("serial", null)))
				XposedHelpers.findField(android.os.Build.class, "SERIAL").set(
						null, pre.getString("serial", ""));// 串口序列号
			if (!TextUtils.isEmpty(pre.getString("zhiwen", null)))
				XposedHelpers.findField(android.os.Build.class, "FINGERPRINT")
						.set(null, pre.getString("zhiwen", ""));// 指纹
			if (!TextUtils.isEmpty(pre.getString("hardware", null)))
				XposedHelpers.findField(android.os.Build.class, "HARDWARE")
						.set(null, pre.getString("hardware", ""));// 硬件
			
			if (!TextUtils.isEmpty(pre.getString("build_id", null)))
				XposedHelpers.findField(android.os.Build.class, "ID")
						.set(null, pre.getString("build_id", ""));
			
			if (!TextUtils.isEmpty(pre.getString("build_dislay", null)))
				XposedHelpers.findField(android.os.Build.class, "DISPLAY")
						.set(null, pre.getString("build_dislay", ""));
			
			if (!TextUtils.isEmpty(pre.getString("build_host", null))){
				XposedHelpers.findField(android.os.Build.class, "HOST")
						.set(null, pre.getString("build_host", ""));
				XposedHelpers.findField(android.os.Build.class, "USER")
				.set(null, pre.getString("build_host", ""));
				XposedHelpers.findField(android.os.Build.class, "TAGS")
				.set(null, pre.getString("build_host", ""));
				XposedHelpers.findField(android.os.Build.class, "TYPE")
				.set(null, pre.getString("build_host", ""));
			}
//
		} catch (Throwable e) {
			Log.e("TTT", "修改 Build123 失败!" + e.getMessage());
		}

		// 改变imei
		HookMethod(TelephonyManager.class, "getDeviceId", pre.getString("imei",null), 1);

		// 改变android_id(有两个获取android_id的方法)
		HookMethod(android.provider.Settings.Secure.class, "getString", pre.getString("android_id",null), 4);
		HookMethod(android.provider.Settings.System.class, "getString", pre.getString("android_id",null), 4);
		HookMethod(TelephonyManager.class, "getLine1Number", pre.getString("phone",null), 1);// 手机号码
		HookMethod(TelephonyManager.class, "getSimSerialNumber", pre.getString("phone_num",null), 1);// 手机卡序列号
		HookMethod(TelephonyManager.class, "getSubscriberId", pre.getString("imsi",null), 1);// 改变imsi
		HookMethod(TelephonyManager.class, "getSimOperator", pre.getString("yys",null), 1);// 运营商
		HookMethod(TelephonyManager.class, "getNetworkType", pre.getString("network_type",null), 2);// 网络类型
		HookMethod(TelephonyManager.class, "getNetworkOperatorName", pre.getString("network_operatorName",null), 1);// 网络类型名
		HookMethod(TelephonyManager.class, "getSimState", pre.getString("simstate",null), 2);// 手机卡状态
		HookMethod(WifiInfo.class, "getMacAddress", pre.getString("mac",null), 1);// mac地址
		HookMethod(WifiInfo.class, "getSSID", pre.getString("wx",null), 1);// 无线路由器名
		HookMethod(WifiInfo.class, "getBSSID", pre.getString("wxdz",null), 1);// 无线路由器地址
		HookMethod(WifiInfo.class, "getIpAddress", pre.getString("ipadress",null), 2);// IP地址

		// 屏幕宽高
		HookMethod(Display.class, "getHeight", pre.getString("fbl_h",null), 2);
		HookMethod(Display.class, "getWidth", pre.getString("fbl_w",null), 2);

		// 需要在wifi开启的情况下才能修改成功GPS经纬度，否则都会是0；
		HookMethod(Location.class, "getLatitude", pre.getString("lat",null), 0);
		HookMethod(Location.class, "getLongitude", pre.getString("lng",null), 0);
		/** Location有可能获取为空，所有保证Location不为空的情况下次才能让应用获取到经纬度 */
		Object obj0[] = new Object[1];
		obj0[0] = String.class.getName();
		Hook1(packageName, LocationManager.class.getName(),
				"getLastKnownLocation", classLoader, obj0);

		// 蓝牙地址
		HookMethod(BluetoothAdapter.class, "getAddress", pre.getString("bluemac",null), 1);
		
		
		/*********************分辨率******************/
		try {
			XposedHelpers.findAndHookMethod(Display.class, "getMetrics",
					new Object[] { android.util.DisplayMetrics.class,
							new XC_MethodHook() {
								protected void afterHookedMethod(
										MethodHookParam param) throws Throwable {

									// onLog("TTT--修改分辨率 前！");

									DisplayMetrics ddm = (DisplayMetrics) param.args[0];

									if (pre.getString("fbl_h",null) != null)// 屏幕分辨率_高
										ddm.heightPixels = Integer.valueOf(pre.getString("fbl_h",null));

									if (pre.getString("fbl_w",null) != null)// 屏幕分辨率_宽
										ddm.widthPixels = Integer.valueOf(pre.getString("fbl_w",null));

									if (pre.getString("density",null) != null)
										ddm.density = Float.valueOf(pre.getString("density",null));

									if (pre.getString("scaledDensity",null) != null)
										ddm.scaledDensity = Float.valueOf(pre.getString("scaledDensity",null));

									if (pre.getString("densityDpi",null) != null)
										ddm.densityDpi = Integer.valueOf(pre.getString("densityDpi",null));

									if (pre.getString("xdpi",null) != null)
										ddm.xdpi = Float.valueOf(pre.getString("xdpi",null));

									if (pre.getString("ydpi",null) != null)
										ddm.ydpi = Float.valueOf(pre.getString("ydpi",null));

									param.setResult(ddm);
									// onLog("TTT---修改分辨率后！");
								}

							} });
		} catch (Throwable e) {
			// TODO: handle exception
		}

		/******************** 监听文件操作 ********************/
		Object obj1[] = new Object[1];
		obj1[0] = String.class.getName();

		Object obj2[] = new Object[2];
		obj2[0] = String.class.getName();
		obj2[1] = String.class.getName();

		Object obj3[] = new Object[2];
		obj3[0] = File.class.getName();
		obj3[1] = String.class.getName();

		Object obj4[] = new Object[1];
		obj4[0] = String.class.getName();

		Hook2(packageName, FileReader.class.getName(), classLoader, obj1);
		Hook2(packageName, File.class.getName(), classLoader, obj2);
		Hook2(packageName, File.class.getName(), classLoader, obj3);
		Hook2(packageName, File.class.getName(), classLoader, obj4);

		/******************** 监听系统值设置 ********************/
		String systemS = System.class.getName();

		Object obj6[] = new Object[3];
		obj6[0] = ContentResolver.class.getName();
		obj6[1] = String.class.getName();
		obj6[2] = Float.TYPE.getName();

		Object obj7[] = new Object[3];
		obj7[0] = ContentResolver.class.getName();
		obj7[1] = String.class.getName();
		obj7[2] = Integer.TYPE.getName();

		Object obj8[] = new Object[3];
		obj8[0] = ContentResolver.class.getName();
		obj8[1] = String.class.getName();
		obj8[2] = Long.TYPE.getName();

		Object obj9[] = new Object[3];
		obj9[0] = ContentResolver.class.getName();
		obj9[1] = String.class.getName();
		obj9[2] = String.class.getName();

		Object obj10[] = new Object[2];
		obj10[0] = ContentResolver.class.getName();
		obj10[1] = String.class.getName();

		Hook1(packageName, systemS, "putFloat", classLoader, obj6);
		Hook1(packageName, systemS, "putInt", classLoader, obj7);
		Hook1(packageName, systemS, "putLong", classLoader, obj8);
		Hook1(packageName, systemS, "putString", classLoader, obj9);
		Hook1(packageName, systemS, "getFloat", classLoader, obj10);
		Hook1(packageName, systemS, "getInt", classLoader, obj10);
		Hook1(packageName, systemS, "getLong", classLoader, obj10);
		Hook1(packageName, systemS, "getString", classLoader, obj10);
		Hook1(packageName, systemS, "getFloat", classLoader, obj6);
		Hook1(packageName, systemS, "getInt", classLoader, obj7);
		Hook1(packageName, systemS, "getLong", classLoader, obj8);
		Hook1(packageName, systemS, "getLong", classLoader, obj8);

		/****************************************/
		Object obj11[] = new Object[2];
		obj11[0] = String.class.getName();
		obj11[1] = String.class.getName();

		Object obj12[] = new Object[1];
		obj12[0] = String.class.getName();

		Object aobj19[] = new Object[1];
		aobj19[0] = String.class.getName();

		Object aobj20[] = new Object[2];
		aobj20[0] = String.class.getName();
		aobj20[1] = Map.class.getName();

		Hook1(packageName, "android.os.SystemProperties", "get", classLoader,
				obj11);
		Hook1(packageName, "android.os.SystemProperties", "get", classLoader,
				obj12);
		Hook1(packageName, TelephonyManager.class.getName(), "hasIccCard",
				classLoader, new Object[0]);// hasIccCard判断手机卡状态
		Hook1(packageName, WifiManager.class.getName(), "getScanResults",
				classLoader, new Object[0]);// getScanResults获取wifi列表
		Hook1(packageName, Build.class.getName(), "getRadioVersion",
				classLoader, new Object[0]);//固件版本
		Hook1(packageName, WebView.class.getName(), "getSettings", classLoader,
				new Object[0]);// 修改UA
		Hook1(packageName, URLConnection.class.getName(), "getInputStream",
				classLoader, new Object[0]);// 修改UA
		Hook1(packageName, HttpURLConnection.class.getName(),
				"getResponseCode", classLoader, new Object[0]);// 修改UA

		Hook1(packageName, WebView.class.getName(), "loadUrl", classLoader,
				aobj19);
		Hook1(packageName, WebView.class.getName(), "loadUrl", classLoader,
				aobj20);

		/******************** 基站数据 ********************/
		Hook1(packageName, GsmCellLocation.class.getName(), "getLac",
				classLoader, new Object[0]);
		Hook1(packageName, GsmCellLocation.class.getName(), "getCid",
				classLoader, new Object[0]);
		Hook1(packageName, GsmCellLocation.class.getName(), "toString",
				classLoader, new Object[0]);
		Hook1(packageName, CdmaCellLocation.class.getName(), "getNetworkId",
				classLoader, new Object[0]);
		Hook1(packageName, CdmaCellLocation.class.getName(),
				"getBaseStationId", classLoader, new Object[0]);
		Hook1(packageName, CdmaCellLocation.class.getName(), "toString",
				classLoader, new Object[0]);
		Hook1(packageName, TelephonyManager.class.getName(), "getCellLocation",
				classLoader, new Object[0]);

		// //////////////////////////////////////////////////
		Hook1(packageName, TelephonyManager.class.getName(),
				"getSimCountryIso", classLoader, new Object[0]);// 手机卡国家代号
		Hook1(packageName, TelephonyManager.class.getName(),
				"getNetworkOperator", classLoader, new Object[0]);// 网络供应商
		Hook1(packageName, TelephonyManager.class.getName(),
				"getNetworkCountryIso", classLoader, new Object[0]);// 网络供应国家代号
		Hook1(packageName, TelephonyManager.class.getName(), "getPhoneType",
				classLoader, new Object[0]);// 手机网络类型
		Hook1(packageName, NetworkInfo.class.getName(), "getExtraInfo",
				classLoader, new Object[0]);// 修改网络信息
		Hook1(packageName, Resources.class.getName(), "getDisplayMetrics",
				classLoader, new Object[0]);// 修改分辨率

		// 隐藏包名
		try {
			XposedHelpers.findAndHookMethod(
					"android.app.ApplicationPackageManager",
					lpparam.classLoader, "getInstalledPackages", new Object[] {
							int.class, new XC_MethodHook() {

								protected void afterHookedMethod(
										MethodHookParam param) throws Throwable {

									List<String> list = ListToString
											.stringToList2(RecordAppFileHandlerHelper
													.getFileAllContent("HideAppActivity"));// 获取需要隐藏包名的应用列表

									if (list == null) {
										// onLog("ttt--list = null");
										super.afterHookedMethod(param);
										return;
									} else if (list.size() == 0) {
										// onLog("ttt--list.size = 0");
										super.afterHookedMethod(param);
										return;
									}

									List<PackageInfo> paklist = (List<PackageInfo>) param
											.getResult();// 监听到的应用列表

									// 循环对比，把需要隐藏的移除
									for (int i = 0; i < paklist.size(); i++) {
										PackageInfo packageInfo = (PackageInfo) paklist
												.get(i);

										for (String hideApp : list) {
											onLog("TTT-修改隐藏2******hideApp="
													+ hideApp);
											if (packageInfo.packageName
													.equals(hideApp))
												paklist.remove(i);
										}

									}
									// 返回经过处理的列表
									param.setResult(paklist);
									onLog("ttt****************修改hou******"
											+ param.getResult());

								}

							} });
		} catch (Throwable e) {
			onLog("hdj****************修改333333" + e.getMessage());
		}

	}

	/**
	 * 
	 * @param packageName
	 *            hook到的包名
	 * @param className
	 *            需要监听的类名
	 * @param methodName
	 *            需要监听的方法名
	 * @param classloader
	 *            LoadPackageParam的ClassLoader
	 * @param aobj
	 *            参数类型和回调的数组
	 * 
	 * */
	private void Hook1(String packageName, String className, String methodName,
			ClassLoader classloader, Object aobj[]) {
		// TODO Auto-generated method stub

		TestHook testHook = new TestHook(packageName, className, methodName);

		// 组装object数组
		Object aobj1[] = new Object[1 + aobj.length];
		for (int i = 0; i < aobj.length; i++) {
			aobj1[i] = aobj[i];
		}
		aobj1[aobj.length] = testHook;
		// onLog("ttt****************Hook1");
		XposedHelpers.findAndHookMethod(className, classloader, methodName,
				aobj1);
		// onLog("ttt****************Hook2");

	}

	/**
	 * 监听文件类操作
	 * 
	 * @param packageName
	 *            hook到的包名
	 * @param className
	 *            需要监听的类名
	 * @param classloader
	 *            LoadPackageParam的ClassLoader
	 * @param aobj
	 *            参数类型和回调的数组
	 * 
	 * */
	private void Hook2(String packageName, String className,
			ClassLoader classloader, Object aobj[]) {
		// TODO Auto-generated method stub

		TestHook testHook = new TestHook(packageName, className, null);
		Object aobj1[] = new Object[1 + aobj.length];

		for (int i = 0; i < aobj.length; i++) {
			aobj1[i] = aobj[i];
		}
		aobj1[aobj.length] = testHook;
		XposedHelpers.findAndHookConstructor(className, classloader, aobj1);

	}

	@SuppressLint("NewApi")
	private void HookMethod(final Class cl, final String method,
			final Object result, final int type) {
		// XposedBridge.log("hjj--------method = " + method
		// + "------result = " + result);
		try {
			Object[] obj = null;

			if (result == null)
				return;

			if (String.valueOf(result).length() == 0)
				return;

			if (type == 4) {

				// 修改android_id
				obj = new Object[] { ContentResolver.class, String.class,
						new XC_MethodHook() {

							protected void afterHookedMethod(
									MethodHookParam param) throws Throwable {
								String aid = (String) result;

								if (param.args.length >= 2
										&& param.args[1] == "android_id"
										&& aid.length() > 0) {

									param.setResult(aid);
								}

							}

						} };

			} else {

				// onLog("TTT---method = "+method+" -result="+result+" -type="+type);

				obj = new Object[] { new XC_MethodHook() {
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {

						switch (type) {
						case 0:
							// onLog("TTT---method = " + method + " -result="
							// + result + " -type1=" + type);
							param.setResult(Double.valueOf(result + ""));
							break;
						case 1:
							// onLog("TTT---method = " + method + " -result="
							// + result + " -type1=" + type);
							param.setResult((String) result);
							break;
						case 2:
							// onLog("TTT---method = " + method + " -result="
							// + result + " -type2=" + type);
							param.setResult(Integer.parseInt(result + ""));
							break;
						}
						// onLog("TTT---type = "+type);
					}

				} };

			}

			XposedHelpers.findAndHookMethod(cl, method, obj);
		} catch (Throwable e) {
			Log.e("TTT", "修改" + method + "失败!" + e.getMessage());
		}
	}

	// 打印log
	public void onLog(String s) {
		XposedBridge.log(s);
	}

	class TestHook extends XC_MethodHook {
		private String className;// 类名
		private String packageName;// 应用的包名
		private String methodName;// 方法名

		private String b = Environment.getExternalStorageDirectory() + "";

		private String c = (new StringBuilder())
				.append(Environment.getExternalStorageDirectory())
				.append(File.separator).append("Hdj008k/cpuinfo").toString();

		public TestHook(String packageName, String className, String methodName) {
			this.packageName = packageName;
			this.className = className;
			this.methodName = methodName;

		}

		protected void beforeHookedMethod(MethodHookParam paramMethodHookParam)
				throws Throwable {
			// param.args[0];//方法的参数，如果该方法没有参数可能出错
			String s = "";

			if (className.equals(File.class.getName())) {
				if (paramMethodHookParam.args.length == 1) {

					s = new StringBuilder()
							.append(paramMethodHookParam.args[0]).toString();

				} else if (paramMethodHookParam.args.length == 2) {

					if (paramMethodHookParam.args[0] instanceof File) {
						s = String
								.valueOf(((File) paramMethodHookParam.args[0])
										.getAbsolutePath())
								+ File.separator + paramMethodHookParam.args[1];
					} else {
						s = paramMethodHookParam.args[0] + File.separator
								+ paramMethodHookParam.args[1];
					}
				}

				if (s.indexOf(b) != 0 && s.indexOf("sdcard") < 0) {
					if (!s.contains("usbotg")) {
						// onLog("TTT-访问非内存目录1:" + s);
					}
				} else {
					if (s.indexOf(String.valueOf(b) + File.separator
							+ "Android/data/" + packageName) == 0) {
						// onLog("TTT-访问data目录1:" + s);
						return;
					}
					if (!s.equals("/proc/cpuinfo") || !new File(c).exists()) {
						// packageName.equals(appPackageName)判断必须要，否则会有问题
						if (packageName.equals(appPackageName)
								&& !s.trim().equals(b)
								&& s.indexOf("Hdj008k") < 0
								&& RecordAppFileHandlerHelper.saveDataToFile2(
										packageName, s)) {
							// onLog("TTT-记录文件操作packageName1" + s);
						}
						super.afterHookedMethod(paramMethodHookParam);
						return;
					}

					if (s.indexOf("Hdj008k") < 0) {// $$

						if (paramMethodHookParam.args.length == 1) {
							paramMethodHookParam.args[0] = c;
							return;
						}
						onLog("设置cpu信息1");
						if (paramMethodHookParam.args.length == 2) {
							if (paramMethodHookParam.args[0] instanceof File) {
								paramMethodHookParam.args[0] = Environment
										.getExternalStorageDirectory();
								paramMethodHookParam.args[1] = "Hdj008k/cpuinfo";
								return;
							}
							paramMethodHookParam.args[0] = Environment
									.getExternalStorageDirectory()
									.getAbsolutePath();
							paramMethodHookParam.args[1] = "Hdj008k/cpuinfo";
						}

					}// $$
				}

			} else if (FileReader.class.getName().equals(className)) {

				if (paramMethodHookParam.args[0].equals("/proc/cpuinfo")) {

					if (new File(c).exists()) {
						onLog("设置cpu信息2");
						paramMethodHookParam.args[0] = c;
					}
				} else if (paramMethodHookParam.args[0] instanceof String) {// ``
					final String s2 = (String) paramMethodHookParam.args[0];

					if (s2.indexOf(b) != 0 && s2.indexOf("sdcard") < 0) {
						// onLog("TTT-访问非内存目录2:" + s2);
						return;
					}
					if (s2.indexOf(String.valueOf(b) + File.separator
							+ "Android/data/" + packageName) == 0) {
						// onLog("TTT-访问data目录2:" + s2 + "  " + packageName);
						return;
					}
					// packageName.equals(appPackageName)判断必须要，否则会有问题
					if (packageName.equals(appPackageName)
							&& !s2.trim().equals(b)
							&& s2.indexOf("Hdj008k") < 0
							&& RecordAppFileHandlerHelper.saveDataToFile2(
									packageName, s2)) {
						// onLog("TTT-记录文件操作2" + s);
					}

				}// ``
			} else if ("android.os.SystemProperties".equals(className)) {

				if (paramMethodHookParam.args.length >= 1) {

					if (paramMethodHookParam.args[0]
							.equals("gsm.version.baseband")) {
						if (pre.getString("radioVersion", null) != null) {
							onLog("gsm.version.baseband "
									+ pre.getString("radioVersion", null));
							paramMethodHookParam.setResult(pre.getString(
									"radioVersion", ""));
							return;
						}
					} else if (paramMethodHookParam.args[0]
							.equals("gsm.sim.state"))// 默认sim卡状态5
						paramMethodHookParam.setResult("READY");
					else if (paramMethodHookParam.args[0].equals("ro.serialno")
							&& pre.getString("serial", null) != null)
						paramMethodHookParam.setResult(pre.getString("serial",
								""));// 改变串口序列号
				}

			} else if (TelephonyManager.class.getName().equals(className)) {

				// hasIccCard()检测是否有sim卡，这里默认返回有卡
				if (methodName.equals("hasIccCard"))
					paramMethodHookParam.setResult(Boolean.valueOf(true));

			} else if (WifiManager.class.getName().equals(className)) {

			}

		}

		// afterHookedMethod是钩到的方法执行后做处理
		protected void afterHookedMethod(MethodHookParam paramMethodHookParam)
				throws Throwable {

			if (Settings.System.class.getName().equals(className)) {// 系统值

				// packageName.equals(appPackageName)判断必须要，否则会有问题
				if (!packageName.equals(appPackageName)) {
					return;
				}

				// onLog("ttt****************Hook4");

				if (methodName.indexOf("put") < 0) {//

					if (!paramMethodHookParam.args[1].equals("android_id")) {
						if (paramMethodHookParam.args.length == 2) {//
							// onLog("TTT-获取系统值" +
							// paramMethodHookParam.args[1]);
							// onLog("TTT-值" +
							// paramMethodHookParam.getResult());

							SetSystemValueHelper.addItem(
									paramMethodHookParam.args[1], "读",
									paramMethodHookParam.getResult());

						} else if (paramMethodHookParam.args.length == 3) {
							// onLog("TTT-获取系统值" + paramMethodHookParam.args[1]
							// + "---" + paramMethodHookParam.args[2]);
							// onLog("TTT-值" +
							// paramMethodHookParam.getResult());
							SetSystemValueHelper.addItem(
									paramMethodHookParam.args[1], "读",
									paramMethodHookParam.getResult());
						}
					}
				} else {

					// onLog("TTT-设置系统值" + paramMethodHookParam.args[1] + "---"
					// + paramMethodHookParam.args[2]);
					SetSystemValueHelper.addItem(paramMethodHookParam.args[1],
							"写", paramMethodHookParam.args[2]);
				}

			} else if (WebView.class.getName().equals(className)) {

				if (methodName.equals("loadUrl")) {

					String us = pre.getString("userAgent", "");
					if (us.length() == 0)
						return;
					WebView localWebView = (WebView) paramMethodHookParam.thisObject;
					localWebView.getSettings().setUserAgentString(us);

				} else if (methodName.equals("getSettings")) {
					String us = pre.getString("userAgent", "");
					if (us.length() == 0)
						return;

					WebSettings webSettings = (WebSettings) paramMethodHookParam
							.getResult();
					webSettings.setUserAgentString(us);

					paramMethodHookParam.setResult(webSettings);

					Log.e("hdj****************",
							"-----" + webSettings.getUserAgentString());
				}

			} else if (WifiManager.class.getName().equals(className)) {

				if (methodName.equals("getScanResults")
						&& !pre.getBoolean("isScan", false)) {
					onLog("TTT-修改wifi列表");

					List<ScanResult> list = (List<ScanResult>) paramMethodHookParam
							.getResult();

					List<ScanResult> scanResults = new ArrayList<ScanResult>();

					if (list == null) {
						list = new ArrayList<ScanResult>();
						onLog("TTT1-list==null");
					}

					for (int i = 0; i < list.size(); i++) {

						ScanResult s = list.get(i);
						String ssid = s.SSID;

						if (!isMoveSSid(ssid)) {

							Random random = new Random();

							onLog("TTT-" + "ssid-" + i + "=" + ssid);
							onLog("TTT-" + "realSsid-"
									+ pre.getString("realSsid", ""));

							List<WifiData> wifiList = WifiDataHelper
									.getWifiData();
							if (wifiList.size() >= list.size()) {

								s.BSSID = wifiList.get(i).getBssid();
								s.SSID = wifiList.get(i).getSsid();
								s.level = wifiList.get(i).getLevel();

							} else {

								int level = random.nextInt(98) + 1;
								s.BSSID = RandomData.randomBSSID();
								s.SSID = RandomData.randomSSID();
								s.level = -level;

							}
							scanResults.add(s);
						}
					}
					paramMethodHookParam.setResult(scanResults);
				} else
					super.afterHookedMethod(paramMethodHookParam);

			} else if (GsmCellLocation.class.getName().equals(className)) {

				if ((this.methodName.equals("getLac"))) {

					paramMethodHookParam.setResult(Integer.parseInt(pre
							.getString("lac", "0")));

				} else if ((this.methodName.equals("getCid"))) {

					paramMethodHookParam.setResult(Integer.parseInt(pre
							.getString("cid", "0")));

				} else if ((this.methodName.equals("toString"))) {

					String str = String.format("[%s,%s,%s]",
							pre.getString("lac", "0"),
							pre.getString("cid", "0"), "-1");
					paramMethodHookParam.setResult(str);

				}

			} else if (CdmaCellLocation.class.getName().equals(className)) {

				if ((this.methodName.equals("getNetworkId"))) {

					paramMethodHookParam.setResult(Integer.parseInt(pre
							.getString("lac", "0")));

				} else if ((this.methodName.equals("getBaseStationId"))) {

					paramMethodHookParam.setResult(Integer.parseInt(pre
							.getString("cid", "0")));

				} else if ((this.methodName.equals("toString"))) {

					double lng1 = Double.valueOf(pre.getString("lng", "0"));
					int lng2 = (int) lng1 * 14400;

					double lat1 = Double.valueOf(pre.getString("lat", "0"));
					int lat2 = (int) lat1 * 14400;

					String str = String.format("[%s,%s,%s,%s,%s]",
							pre.getString("lac", ""), lat2, lng2,
							pre.getString("cid", ""), "1");
					paramMethodHookParam.setResult(str);

				}
			} else if (TelephonyManager.class.getName().equals(className)) {

				if ((this.methodName.equals("getCellLocation"))) {

					CellLocation celllocation = null;
					if (pre.getString("cellLocation", "GsmCellLocation")
							.equals("GsmCellLocation"))
						celllocation = new GsmCellLocation();
					else
						celllocation = new CdmaCellLocation();

					paramMethodHookParam.setResult(celllocation);
				} else if (this.methodName.equals("getPhoneType")
						&& pre.getString("yys", "").length() > 0) {

					String result = pre.getString("yys", "");
					if (result.equals("46003") || result.equals("46007"))
						paramMethodHookParam.setResult(Integer.valueOf(2));
					else
						paramMethodHookParam.setResult(Integer.valueOf(1));
				} else if (this.methodName.equals("getNetworkCountryIso")) {
					String iso = "cn";
					paramMethodHookParam.setResult(iso);
				} else if (this.methodName.equals("getSimCountryIso")) {
					String iso = "cn";
					paramMethodHookParam.setResult(iso);
				} else if (this.methodName.equals("getNetworkOperator")
						&& pre.getString("yys", null) != null) {
					paramMethodHookParam.setResult(pre.getString("yys", null));
				}

			} else if (LocationManager.class.getName().equals(className)) {

				if (methodName.equals("getLastKnownLocation")) {
					Location mLocation = (Location) paramMethodHookParam
							.getResult();
					if (mLocation == null) {
						mLocation = new Location("");
					}

					if (mLocation == null)
						onLog("TTT-----mLocation = null");
					else
						onLog("TTT-----mLocation != null");

					paramMethodHookParam.setResult(mLocation);
				}

			} else if (NetworkInfo.class.getName().equals(className)) {

				if (methodName.equals("getExtraInfo")) {

					String extra = pre.getString("wx", "");
					if (extra.length() > 0)
						paramMethodHookParam.setResult(extra);
				}

			} else if (Resources.class.getName().equals(className)) {

				if (this.methodName.equals("getDisplayMetrics")) {

					DisplayMetrics ddm = (DisplayMetrics) paramMethodHookParam
							.getResult();

					String fbl_h = pre.getString("fbl_h", "");
					String fbl_w = pre.getString("fbl_w", "");
					String density = pre.getString("density", "");
					String scaledDensity = pre.getString("scaledDensity", "");
					String densityDpi = pre.getString("densityDpi", "");
					String xdpi = pre.getString("xdpi", "");
					String ydpi = pre.getString("ydpi", "");

					if (fbl_h.length() > 0)// 屏幕分辨率_高
						ddm.heightPixels = Integer.valueOf(fbl_h);

					if (fbl_w.length() > 0)// 屏幕分辨率_宽
						ddm.widthPixels = Integer.valueOf(fbl_w);

					if (density.length() > 0)
						ddm.density = Float.valueOf(density);

					if (scaledDensity.length() > 0)
						ddm.scaledDensity = Float.valueOf(scaledDensity);

					if (densityDpi.length() > 0)
						ddm.densityDpi = Integer.valueOf(densityDpi);

					if (xdpi.length() > 0)
						ddm.xdpi = Float.valueOf(xdpi);

					if (ydpi.length() > 0)
						ddm.ydpi = Float.valueOf(ydpi);

					paramMethodHookParam.setResult(ddm);

				}

			} else if ((URLConnection.class.getName().equals(this.className))
					|| (HttpURLConnection.class.getName()
							.equals(this.className))) {
				URLConnection localURLConnection = (URLConnection) paramMethodHookParam.thisObject;
				String str3 = pre.getString("userAgent", "");
				onLog("URLConnection" + str3);
				localURLConnection.setRequestProperty("User-Agent", str3);
				onLog("URLConnection getInputStream");
				return;
			}
			// else if (className
			// .equals("android.app.ApplicationPackageManager")
			// && list != null) {// 隐藏包名
			//
			// onLog("ttt--ApplicationPackageManager");
			//
			// if (methodName.equals("getInstalledApplications")) {
			// onLog("ttt--getInstalledApplications");
			// List<ApplicationInfo> paklist = (List<ApplicationInfo>)
			// paramMethodHookParam
			// .getResult();
			//
			// for (int i = 0; i < paklist.size(); i++) {
			// ApplicationInfo applicationInfo = (ApplicationInfo) paklist
			// .get(i);
			//
			// for (String hideApp : list) {
			// onLog("TTT-修改隐藏1******hideApp=" + hideApp);
			// if (applicationInfo.packageName.equals(hideApp))
			// paklist.remove(i);
			// }
			//
			// }
			//
			// paramMethodHookParam.setResult(paklist);
			// onLog("TTT-修改隐藏1******" + paramMethodHookParam.getResult());
			//
			// } else if (methodName.equals("getInstalledPackages")) {
			// onLog("ttt--getInstalledPackages");
			// List<PackageInfo> paklist = (List<PackageInfo>)
			// paramMethodHookParam
			// .getResult();
			//
			// for (int i = 0; i < paklist.size(); i++) {
			// PackageInfo packageInfo = (PackageInfo) paklist.get(i);
			//
			// for (String hideApp : list) {
			// onLog("TTT-修改隐藏2******hideApp=" + hideApp);
			// if (packageInfo.packageName.equals(hideApp))
			// paklist.remove(i);
			// }
			//
			// }
			//
			// paramMethodHookParam.setResult(paklist);
			// onLog("TTT-修改隐藏2******" + paramMethodHookParam.getResult());
			// }
			// }
			// else if (className.equals("android.app.ActivityManager") &&
			// list!=null) {// 隐藏进程
			// onLog("ttt--ActivityManager");
			// if (methodName.equals("getRecentTasks")) {
			// onLog("ttt--getRecentTasks");
			// List<RecentTaskInfo> paklist = (List<RecentTaskInfo>)
			// paramMethodHookParam
			// .getResult();
			//
			// for (int i = 0; i < paklist.size(); i++) {
			// RecentTaskInfo recentTaskInfo = (RecentTaskInfo) paklist
			// .get(i);
			//
			// for (String hideApp : list){
			// onLog("TTT-修改隐藏3******hideApp=" + hideApp);
			// if
			// (recentTaskInfo.baseIntent.getComponent().getPackageName().equals(hideApp))
			// paklist.remove(i);
			// }
			//
			// }
			//
			// paramMethodHookParam.setResult(paklist);
			// onLog("TTT-修改隐藏3******" + paramMethodHookParam.getResult());
			//
			// } else if (methodName.equals("getRunningAppProcesses")) {
			// onLog("ttt--getRunningAppProcesses");
			// List<RunningAppProcessInfo> paklist =
			// (List<RunningAppProcessInfo>) paramMethodHookParam
			// .getResult();
			//
			// for (int i = 0; i < paklist.size(); i++) {
			// RunningAppProcessInfo appProcessInfo = (RunningAppProcessInfo)
			// paklist
			// .get(i);
			//
			// for (String hideApp : list){
			// onLog("TTT-修改隐藏4******hideApp=" + hideApp);
			// if (appProcessInfo.processName.equals(hideApp))
			// paklist.remove(i);
			// }
			//
			// }
			//
			// paramMethodHookParam.setResult(paklist);
			// onLog("TTT-修改隐藏4******" + paramMethodHookParam.getResult());
			// }
			//
			// }
		}
	}

	public boolean isMoveSSid(String ssid) {
		if (TextUtils.isEmpty(ssid))
			return true;

		String str = ssid.toLowerCase();
		if (str.indexOf("cmcc") < 0 && str.indexOf("chinanet") < 0)
			return false;
		else
			return true;
	}

}
