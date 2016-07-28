package com.example.hdj008k_location.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.hdj008k_location.db.HdjDbService;
import com.example.hdj008k_location.obj.AdStatus;
import com.example.hdj008k_location.util.InstallUtil;
import com.example.hdj008k_location.util.RecordAppFileHandlerHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/** 安装广播监听 */
public class InstallReceiver extends BroadcastReceiver {

	Context mContext;

	ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(10);

	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;

		// TODO Auto-generated method stub

		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			final String packageName = intent.getDataString().substring(8);
			Toast.makeText(context, packageName + "安装成功", 1000).show();

			Log.e("77777777777777777777777777", packageName + "安装成功！");
			HdjDbService hdjDbService = new HdjDbService(mContext);

			List<AdStatus> adStatus = hdjDbService.getScrollData();
			if (adStatus != null) {

				for (int i = 0; i < adStatus.size(); i++) {

					if (InstallUtil.isApkCanInstall(mContext, adStatus.get(i).getAdpath())) {

						if (getUninstallAPKInfo(mContext,adStatus.get(i).getAdpath()).equals(packageName)
								&& adStatus.get(i).getStatus() == 0) {

							SharedPreferences preferences = context
									.getSharedPreferences("prefs",Context.MODE_WORLD_READABLE);

							if (preferences.getBoolean("openAutomatic", false)) {//是否自动打开
//								Log.e("88888888888888888888888888888", "自动打开");

								PackageManager packageManager = mContext
										.getPackageManager();
								Intent intent2 = packageManager
										.getLaunchIntentForPackage(packageName);
								context.startActivity(intent2);
							}else {
//								Log.e("88888888888888888888888888888", "没有自动打开");
							}

							long time = System.currentTimeMillis();
							SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
							Date data = new Date(time);
							
//							Log.e("88888888888888888888888888888", df.format(data));
							
							AdStatus adStatus2 = adStatus.get(i);
							adStatus2.setStatus(1);
							adStatus2.setTime(time);

							hdjDbService.update(adStatus2);

							// executorService.schedule(new
							// Deletethread(mContext, packageName), 15,
							// TimeUnit.SECONDS);

						}
					}
				}

			}

		}
	}

	private String getUninstallAPKInfo(Context ctx, String archiveFilePath) {

		String pakName = null;
		PackageManager pm = ctx.getPackageManager();
		PackageInfo pakinfo = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_ACTIVITIES);
		if (pakinfo != null) {
			ApplicationInfo appinfo = pakinfo.applicationInfo;

			pakName = appinfo.packageName;

		}
		return pakName;
	}

}
