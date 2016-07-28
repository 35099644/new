package com.example.hdj008k_location.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import com.example.hdj008k_location.util.InstallUtil;

/**
 * 卸载应用线程
 * */
public class Deletethread implements Runnable {
	
	private Context mContext;
	private String packageName;
	
	public Deletethread(Context context,String packageName ){
		
		this.mContext = context;
		this.packageName =packageName;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Looper.prepare();
	
		InstallUtil.clientUninstall(packageName);
		Log.e("xxxxxxxxxxxxxx", packageName+"卸载成功！");
		Looper.loop();

	}
	private String getUninstallAPKInfo(Context ctx,String archiveFilePath) {
		
		String pakName = null;
		PackageManager pm=ctx.getPackageManager();
		PackageInfo pakinfo=pm.getPackageArchiveInfo(archiveFilePath,PackageManager.GET_ACTIVITIES);
		if (pakinfo!=null) {
			ApplicationInfo appinfo=pakinfo.applicationInfo;
		
			pakName=appinfo.packageName;

		}
		return pakName;
	}

}
