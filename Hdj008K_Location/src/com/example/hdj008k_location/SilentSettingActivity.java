package com.example.hdj008k_location;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//分钟的NumberPicker数字显示有问题
/**
 * 选择静默安装界面
 * 
 * */
public class SilentSettingActivity extends Activity implements
		OnValueChangeListener, OnScrollListener, Formatter {

	private ToggleButton tb_install, tb_uninstall,tb_open;
	private TextView tv_in, tv_un,tv_open;
	private NumberPicker np_minute, np_seconds, np_minute2, np_seconds2;
	private LinearLayout ll_install, ll_uninstall,ll_open;
	private RelativeLayout rl_uninstall,rl_open;

	private SharedPreferences preferences;

	private int minute, seconds, minute2, seconds2;

	private int waitTime, uninstallTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_silent);

		preferences = this.getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);
		waitTime = preferences.getInt("WaitTiem", 15);// 线程的休眠时间
		uninstallTime = preferences.getInt("UninstallTime", 5);// 多长时间后静默卸载
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		tv_in = (TextView) findViewById(R.id.tv_in);
		tv_un = (TextView) findViewById(R.id.tv_un);
		tv_open = (TextView) findViewById(R.id.tv_open);

		tb_install = (ToggleButton) findViewById(R.id.tb_install);
		tb_uninstall = (ToggleButton) findViewById(R.id.tb_uninstall);
		tb_open = (ToggleButton) findViewById(R.id.tb_open);

		rl_uninstall = (RelativeLayout) findViewById(R.id.rl_uninstall1);
		rl_open = (RelativeLayout) findViewById(R.id.rl_open1);
		ll_install = (LinearLayout) findViewById(R.id.ll_install);
		ll_uninstall = (LinearLayout) findViewById(R.id.ll_uninstall);

		np_minute = (NumberPicker) findViewById(R.id.np_minute);
		np_seconds = (NumberPicker) findViewById(R.id.np_seconds);
		np_minute2 = (NumberPicker) findViewById(R.id.np_minute2);
		np_seconds2 = (NumberPicker) findViewById(R.id.np_seconds2);

		tb_install.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					tv_in.setText("静默安装已开启");
					ll_install.setVisibility(View.VISIBLE);
					rl_uninstall.setVisibility(View.VISIBLE);
					rl_open.setVisibility(View.VISIBLE);
				} else {
					tv_in.setText("静默安装已关闭");
					ll_install.setVisibility(View.GONE);
					rl_uninstall.setVisibility(View.GONE);
					rl_open.setVisibility(View.GONE);
					preferences.edit().putBoolean("openUnInstall", false).commit();//保存设置
					preferences.edit().putBoolean("openAutomatic", false).commit();//保存设置
				}
				preferences.edit().putBoolean("openInstall", isChecked).commit();//保存设置

			}
		});

		tb_uninstall.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					tv_un.setText("静默卸载已开启");
					ll_uninstall.setVisibility(View.VISIBLE);
				} else {
					tv_un.setText("静默卸载已关闭");
					ll_uninstall.setVisibility(View.GONE);
				}
				preferences.edit().putBoolean("openUnInstall", isChecked).commit();//保存设置

			}
		});
		
		tb_open.setOnCheckedChangeListener(new OnCheckedChangeListener() {//自动打开app功能
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				if (isChecked) {
					tv_open.setText("静默卸载已开启");
				} else {
					tv_open.setText("静默卸载已关闭");
				}
				preferences.edit().putBoolean("openAutomatic", isChecked).commit();//保存设置
				
			}
		});

		//获取以前保存的数据，分成 分和秒
		if (waitTime > 59) {
			seconds = waitTime % 60;
			minute = (waitTime - seconds) / 60;
		} else {
			seconds = waitTime;
			minute = 0;
		}
		
		if (uninstallTime > 59) {
			seconds2 = uninstallTime % 60;
			minute2 = (uninstallTime - seconds2) / 60;
		} else {
			seconds2 = uninstallTime;
			minute2 = 0;
		}

		//设置NumberPicker控件
		np_minute.setFormatter(this);
		np_minute.setOnValueChangedListener(this);
		np_minute.setOnScrollListener(this);
		np_minute.setMaxValue(5);
		np_minute.setMinValue(0);
		np_minute.setValue(minute);
		Log.e("SilentSettingActivity--", "minute = " + minute);
		np_minute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);// 禁止输入

		np_seconds.setFormatter(this);
		np_seconds.setOnValueChangedListener(this);
		np_seconds.setOnScrollListener(this);
		np_seconds.setMaxValue(59);
		np_seconds.setMinValue(0);
		np_seconds.setValue(seconds);
		np_seconds.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		np_minute2.setFormatter(this);
		np_minute2.setOnValueChangedListener(this);
		np_minute2.setOnScrollListener(this);
		np_minute2.setMaxValue(5);
		np_minute2.setMinValue(0);
		np_minute2.setValue(minute2);
		Log.e("SilentSettingActivity--", "minute2 = " + minute2);
		np_minute2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		np_seconds2.setFormatter(this);
		np_seconds2.setOnValueChangedListener(this);
		np_seconds2.setOnScrollListener(this);
		np_seconds2.setMaxValue(59);
		np_seconds2.setMinValue(0);
		np_seconds2.setValue(seconds2);
		np_seconds2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		//判断是否显示NumberPicker控件
		if (!preferences.getBoolean("openInstall", false)) {
			tv_in.setText("静默安装已关闭");
			tb_install.setChecked(false);
			ll_install.setVisibility(View.GONE);
			rl_uninstall.setVisibility(View.GONE);
			rl_open.setVisibility(View.GONE);
		} else {
			tv_in.setText("静默安装已开启");
			tb_install.setChecked(true);
			ll_install.setVisibility(View.VISIBLE);
			rl_uninstall.setVisibility(View.VISIBLE);
			rl_open.setVisibility(View.VISIBLE);
		}

		if (!preferences.getBoolean("openUnInstall", false)) {
			tv_un.setText("静默卸载已关闭");
			tb_uninstall.setChecked(false);
			ll_uninstall.setVisibility(View.GONE);
		} else {
			tv_un.setText("静默卸载已开启");
			tb_uninstall.setChecked(true);
			ll_uninstall.setVisibility(View.VISIBLE);
		}
		
		if (!preferences.getBoolean("openAutomatic", false)) {
			tv_open.setText("打开app功能(关闭)");
			tb_open.setChecked(false);
		} else {
			tv_open.setText("打开app功能(开启)");
			tb_open.setChecked(true);
		}

	}

	@Override
	public String format(int value) {
		// TODO Auto-generated method stub
		String tmpStr = String.valueOf(value);//小与10的前面多加个0，这样显示比较好看
		if (value < 10) {
			tmpStr = "0" + tmpStr;
		}
		return tmpStr;
	}

	@Override
	public void onScrollStateChange(NumberPicker view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
//			Log.e("SilentSettingActivity--", "后续滑动(飞呀飞，根本停下来)");
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
//			Log.e("SilentSettingActivity--", "不滑动");
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//			Log.e("SilentSettingActivity--", "滑动中");
			break;
		}
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub

		//NumberPicker当前显示的值
		switch (picker.getId()) {
		case R.id.np_minute:
			minute = newVal;
			break;
		case R.id.np_seconds:
			seconds = newVal;
			break;
		case R.id.np_minute2:
			minute2 = newVal;
			break;
		case R.id.np_seconds2:
			seconds2 = newVal;
			break;
		}

//		Log.e("SilentSettingActivity--" + picker.getId(), "原来的值 " + oldVal
//				+ "--新值: " + newVal);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(!saveData())
			return;
		super.onDestroy();
	}

	/**保存数据*/
	private boolean saveData() {
		int w = minute * 60 + seconds;
		int u = minute2 * 60 + seconds2;
		
		Log.e("SilentSettingActivity--", "WaitTiem = " + w
				+ "UninstallTime = " + u);
		
		if(w==0||u==0){
			Toast.makeText(this, "时间不能为0", Toast.LENGTH_LONG).show();
			return false;
		}
		
		Editor edit = preferences.edit();
		edit.putInt("WaitTiem", w);// 线程的休眠时间
		edit.putInt("UninstallTime", u);// 线程的休眠时间
		edit.commit();
		
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !saveData()) {

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
