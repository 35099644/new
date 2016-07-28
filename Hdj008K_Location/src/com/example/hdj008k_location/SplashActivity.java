package com.example.hdj008k_location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hdj008k_location.util.BmobHelper;

/**
 * 
 * 
 * @ClassName: SplashActivity
 */
public class SplashActivity extends Activity {

	private SharedPreferences preferences;
	private Context mContext;
	private BmobHelper bmobTool;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				preferences.edit().putBoolean("CasttoTime", false).commit();
				jumpHome();
				break;
			case 100:
				showdialog("注册成功,后台正在审核!\n请稍后重新启动应用");
				break;
			case 300:
				showdialog("使用时间到了或被后台禁止了!");
				break;
			case 400:
				showdialog("后台数据出错了!");
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slpash);
		mContext = this;
		init();
//		mHandler2.sendEmptyMessageDelayed(200, 2000);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				jumpHome();
				break;
			}
		}
	};

	private void init() {

		bmobTool = new BmobHelper(mContext);
		bmobTool.setHandler(mHandler);

		preferences = getSharedPreferences(BmobHelper.PACKAGE_NAME, MODE_PRIVATE);
		
		if (!isNetworkAvailable(this)) {
			showdialog("启动应用需要网络,否则无法使用!");
			return;
		}
		
		if (preferences.getBoolean("firstOpen", true)) {
			showRegisterDialog();
		} else {
			bmobTool.isAllowRun();				
		}
 
	}

	/** 弹出注册框 */
	private void showRegisterDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_register, null);
		final EditText num = (EditText) view.findViewById(R.id.ed_number);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("请输入手机编号!");
		builder.setView(view);
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				String str2 = num.getText().toString().trim();
//				if (!TextUtils.isEmpty(str2)) {
//					dialog.cancel();
//					bmobTool.register(str2);
//
//				} else {
//
//					Toast.makeText(mContext, "手机编号不能为空!", 1000).show();
//
//				}
				jumpHome();
			}
		});
		builder.setCancelable(false); // 设置按钮是否可以按返回键取消,false则不可以取消
		AlertDialog dialog = builder.create(); // 创建对话框
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	/** 弹出提示框 */
	private void showdialog(String str) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(str);
		builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		builder.setCancelable(false); // 设置按钮是否可以按返回键取消,false则不可以取消
		AlertDialog dialog = builder.create(); // 创建对话框
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	/** 检查是否有网络 */
	public boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	private NetworkInfo getNetworkInfo(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	private void jumpHome() {
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

}
