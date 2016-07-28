package com.example.hdj008k_location;

import com.example.hdj008k_location.db.DBOpenHelper;
import com.example.hdj008k_location.db.HistoryDBManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 选择留存界面
 * */
public class LiuCunActivity extends Activity {

	private ToggleButton tb_liucun;
	private TextView tv_liucun,text1,text2;
	private LinearLayout ll_liucun;
	private Button btn_liucun;
	private EditText et_liucun,et_liucunmax;

	private Context mContenxt;
	private SharedPreferences preferences;

	private int num,max;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_liucun);

		mContenxt = this;
		initView();

	}

	private void initView() {

		preferences = this.getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);

		tb_liucun = (ToggleButton) findViewById(R.id.tb_liucun);
		tv_liucun = (TextView) findViewById(R.id.tv_liucun);
		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		ll_liucun = (LinearLayout) findViewById(R.id.ll_liucun);
		btn_liucun = (Button) findViewById(R.id.btn_liucun);
		et_liucun = (EditText) findViewById(R.id.et_liucun);
		et_liucunmax = (EditText) findViewById(R.id.et_liucunmax);

		num = preferences.getInt("Liucun", 20);
		max = preferences.getInt("liucunMax", 1000);
		et_liucun.setText(num + "");
		et_liucunmax.setText(max + "");

		tb_liucun.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				showLayout(isChecked);
				preferences.edit().putBoolean("openLiucun", isChecked).commit();
			}
		});

		btn_liucun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String s = et_liucun.getText().toString().trim();
				String str = et_liucunmax.getText().toString().trim();

				if (!TextUtils.isEmpty(s) && isNum(s))
					num = Integer.valueOf(s);
				
				if (!TextUtils.isEmpty(str) && isNum(str))
					max = Integer.valueOf(str);

				if (num >= 0 && num <= 100) {
					
					if(max > 0){
						Editor editor = preferences.edit();
						editor.putInt("Liucun", num);
						editor.putInt("liucunMax", max).commit();
						Toast.makeText(mContenxt, "保存成功", 1000).show();
					}else
						Toast.makeText(mContenxt, "数据最大数必须大于0", 1000).show();
					
				} else
					Toast.makeText(mContenxt, "留存率不能大于100或小于0", 1000).show();

			}
		});
		
		HistoryDBManager dbManager = new HistoryDBManager(this);
		int sqlSize = dbManager.getSize(DBOpenHelper.TABNAME);
		int run = dbManager.getSize(DBOpenHelper.RUNTABNAME);
		
		text1.setText("留存数据:"+sqlSize);
		text2.setText("已跑留存数据:"+run);
		
		showLayout(preferences.getBoolean("openLiucun", false));

	}

	// 判断字符串是不是纯数字
	private boolean isNum(String txt) {
		boolean result = txt.matches("[0-9]+");
		return result;
	}

	private void showLayout(boolean b) {
		// TODO Auto-generated method stub
		if (!b) {
			tv_liucun.setText("次日留存已关闭");
			tb_liucun.setChecked(false);
			ll_liucun.setVisibility(View.GONE);
		} else {
			tv_liucun.setText("次日留存已开启");
			tb_liucun.setChecked(true);
			ll_liucun.setVisibility(View.VISIBLE);
		}
	}

}
