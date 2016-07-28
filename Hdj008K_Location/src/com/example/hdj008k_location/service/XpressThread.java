package com.example.hdj008k_location.service;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.hdj008k_location.obj.MyFileInfo;
import com.example.hdj008k_location.util.InstallUtil;
import com.example.hdj008k_location.util.MyFileUtil;

public class XpressThread implements Runnable {
	
	private Context context;
	private int type;
	private String path;
	
	
	public XpressThread(Context context,int type,String path) {
		this.context = context;
		this.type = type;
		this.path = path;
	}

	@Override
	public void run() {
		
		
    	
	}

}
