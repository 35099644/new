package com.example.hdj008k_location;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.hdj008k_location.SelectAppActivity.MyListAdapter;
import com.example.hdj008k_location.obj.App;
import com.example.hdj008k_location.util.RecordAppFileHandlerHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

/**
 * 监听应用文件操作
 * */
public class RecordAppFileHandler extends Activity implements OnClickListener {

	private Button btn_delete, btn_clear;

	private TextView tv_app, tv_packagename;

	private CheckBox cb_all;

	private EditText edit;

	private ProgressDialog dialog;

	// private SharedPreferences preferences;

	private static String packageName;// 监听的应用的包名

	private String data;// 保存的文件操作数据

	private long fileSize;// 保存的文件的大小

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			edit.setText(data);
			dialog.cancel();
			if (fileSize > 204800L) {
				String str = "记录文件过大,只显示前200k，文件路径为/sdcard/.log/" + packageName
						+ "  ,该文件过大的原因可能是该应用扫描内存卡，请查看文件列表后，谨慎删除相应文件！";
				showTosat(str);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		btn_delete = (Button) findViewById(R.id.btn_delete_record);
		btn_clear = (Button) findViewById(R.id.btn_clear_record1);
		cb_all = (CheckBox) findViewById(R.id.cb_open_record);
		tv_app = (TextView) findViewById(R.id.tv_app_record);
		tv_packagename = (TextView) findViewById(R.id.tv_packagename_record);

		edit = (EditText) findViewById(R.id.et_record);
		// edit.setInputType(InputType.TYPE_NULL);

		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage("文件读取中，请稍候");
		dialog.show();

		// 获取数据
		SharedPreferences preferences = getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);
		String appName = preferences.getString("appName", "");// 监听的应用名字
		packageName = preferences.getString("packageName", "");// 监听的应用包名

		if (preferences.getBoolean("check", true)) {
			cb_all.setChecked(true);
		}

		tv_app.setText(appName);
		tv_packagename.setText(packageName);

		btn_delete.setOnClickListener(this);
		btn_clear.setOnClickListener(this);

		// check监听
		cb_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

			}
		});

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				fileSize = RecordAppFileHandlerHelper.getFileSize(packageName);
				Log.e("RecordAppFileHandler--Thread()", "fileSize = "
						+ fileSize);

				// data = fileSize > 204800 ? FileHookHelper
				// .getFileContent(packageName) : FileHookHelper
				// .getFileAllContent(packageName);

				if (fileSize > 204800L) {
					data = RecordAppFileHandlerHelper
							.getFileContent(packageName);

				} else {
					data = RecordAppFileHandlerHelper
							.getFileAllContent(packageName);

				}

				// Log.e("RecordAppFileHandler--Thread()", "data = " + data);

				runOnUiThread(runnable);
			}

		}.start();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_delete_record:// 清除记录中的数据

			deleteDate(RecordAppFileHandler.this, packageName);
			edit.setText("");

			break;
		case R.id.btn_clear_record1:// 清空记录

			showDialog();
			Log.e("sssssssssss", "ddddddddddddddddd");

			break;

		}
	}

	public static void deleteDate(Context context, String fileName) {

		String[] dataArray = RecordAppFileHandlerHelper.getFileAllContent(
				fileName).split("\n");

		int m = 0;
		for (int i = 0; i < dataArray.length; i++) {

			String str = dataArray[i];
			 Log.e("RecordAppFileHandler---deleteSelect---",
			 "ttt-str = "+str);

			if (str.indexOf("aaa.txt") == -1 && str.indexOf("MobileAnJian") == -1) {

				File file = new File(str.trim());

				if (file.exists() && file.isFile()) {
					try {
						file.delete();
						m++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
		
		RecordAppFileHandlerHelper.clearFileData(fileName);
		Toast.makeText(context, "清除成功,共清除" + m + "个文件", 0).show();

	}

	// 弹出提示窗口
	public final void showDialog() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder.setTitle("注意");
		localBuilder.setMessage("是否删除记录");
		localBuilder.setNegativeButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						RecordAppFileHandlerHelper.clearFileData(packageName);
						edit.setText("");
					}
				});

		localBuilder.setPositiveButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
		localBuilder.create().show();
	}

	public void showTosat(String s) {
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}

}
