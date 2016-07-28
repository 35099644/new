package com.example.hdj008k_location;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 工具箱界面
 * */
public class ToolkitActivity extends Activity implements OnClickListener {

	private Button btn_file, btn_setting, btn_hide, btn_install, btn_wifi,
			btn_liucun, btn_density, btn_deldata,btn_xpress,btn_global,btn_browser;

	private SharedPreferences preferences;
	private Context mContext;
	private boolean isDensity;
	private boolean isScan;
	private boolean isGlobal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_af);

		mContext = this;
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		btn_file = (Button) findViewById(R.id.btn_file);
		btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_hide = (Button) findViewById(R.id.btn_hide);
		btn_install = (Button) findViewById(R.id.btn_install);
		btn_wifi = (Button) findViewById(R.id.btn_wifi);
		btn_liucun = (Button) findViewById(R.id.btn_liucun);
		btn_density = (Button) findViewById(R.id.btn_density);
		btn_deldata = (Button) findViewById(R.id.btn_deldata);
		btn_xpress = (Button) findViewById(R.id.btn_xpress);
		btn_global = (Button) findViewById(R.id.btn_global);
		btn_browser = (Button) findViewById(R.id.btn_browser);

		ResumeText();

		btn_file.setOnClickListener(this);
		btn_setting.setOnClickListener(this);
		btn_hide.setOnClickListener(this);
		btn_install.setOnClickListener(this);
		btn_wifi.setOnClickListener(this);
		btn_liucun.setOnClickListener(this);
		btn_density.setOnClickListener(this);
		btn_deldata.setOnClickListener(this);
		btn_xpress.setOnClickListener(this);
		btn_global.setOnClickListener(this);
		btn_browser.setOnClickListener(this);
	}

	private void ResumeText() {
		// TODO Auto-generated method stub
		
		preferences = this.getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);
		
		isScan = preferences.getBoolean("isScan", false);//
		if (!isScan)
			btn_wifi.setText("修改wifi列表已开启");
		else
			btn_wifi.setText("修改wifi列表已关闭");

		isDensity = preferences.getBoolean("isDensity", false);//
		if (isDensity)
			btn_density.setText("修改密度已开启");
		else
			btn_density.setText("修改密度已关闭");
		
		isGlobal = preferences.getBoolean("isGlobal", true);//
		if (isGlobal)
			btn_global.setText("全局修改已开启");
		else
			btn_global.setText("全局修改已关闭");

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Editor edit = preferences.edit();
		switch (v.getId()) {

		case R.id.btn_file:

			intent.setClass(ToolkitActivity.this, RecordAppFileHandler.class);
			startActivity(intent);

			break;
		case R.id.btn_setting:

			intent.setClass(ToolkitActivity.this, SetSystemValueActivity.class);
			startActivity(intent);

			break;
		case R.id.btn_hide:

			intent.setClass(ToolkitActivity.this, HideAppActivity.class);
			startActivity(intent);

			break;
		case R.id.btn_wifi:

			if (isScan) {
				isScan = false;
				btn_wifi.setText("修改wifi列表已开启");
			} else {
				isScan = true;
				btn_wifi.setText("修改wifi列表已关闭");
			}

			edit.putBoolean("isScan", isScan);

			break;
		case R.id.btn_density:

			if (isDensity) {
				isDensity = false;
				btn_density.setText("修改密度已关闭");
			} else {
				isDensity = true;
				btn_density.setText("修改密度已开启");
			}

			edit.putBoolean("isDensity", isDensity);

			break;
		case R.id.btn_global:
			
			if (isGlobal) {
				isGlobal = false;
				btn_global.setText("全局修改已关闭");
			} else {
				isGlobal = true;
				btn_global.setText("全局修改已开启");
			}
			
			edit.putBoolean("isGlobal", isGlobal);
			
			break;
		case R.id.btn_deldata:

			intent.setClass(ToolkitActivity.this, DelFolderActivity.class);
			startActivity(intent);

			break;
		case R.id.btn_xpress:
			
			if(!preferences.getBoolean("openInstall", false)){
				
				intent.setClass(ToolkitActivity.this, XpressActivity.class);
				startActivity(intent);
				
			}else{
				
				showAlDialog("要开启一键功能\n请先关闭自动安装卸载");
				
			}
			break;
		case R.id.btn_install:

			if(preferences.getString("xpFolder", "").equals("")){
			
				intent.setClass(ToolkitActivity.this, SilentSettingActivity.class);
				startActivity(intent);
			
			}else{
				
				showAlDialog("要开启自动安装卸载\n请先关闭一键功能");
				
			}

			break;
		case R.id.btn_liucun:

			intent.setClass(ToolkitActivity.this, LiuCunActivity.class);
			startActivity(intent);

			break;
		case R.id.btn_browser:
			
			intent.setClass(ToolkitActivity.this, BrowserAppActivity.class);
			startActivity(intent);
			
			break;
		}

		edit.commit();

	}

	private void showAlDialog(String str) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(str);
		builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				dialog.cancel();
			}
		});
		builder.setCancelable(false); // 设置按钮是否可以按返回键取消,false则不可以取消
		AlertDialog dialog = builder.create(); // 创建对话框
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		ResumeText();
	}

}
