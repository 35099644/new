package com.example.hdj008k_location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hdj008k_location.util.JSON;
import com.example.hdj008k_location.util.ListToString;
import com.example.hdj008k_location.util.SetSystemValueHelper;

/**
 * 监听系统值设置
 * */
public class SetSystemValueActivity extends Activity implements OnClickListener {

	private Button btn_delete, btn_clear;

	private TextView tv_app, tv_packagename;

	private CheckBox cb_all;

	private ListView listView;

	private MyAdapter adapter;

	 private SharedPreferences preferences;

	private static JSONArray jsonArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system);

		btn_delete = (Button) findViewById(R.id.btn_delete_sys);
		btn_clear = (Button) findViewById(R.id.btn_clear_sys);
		cb_all = (CheckBox) findViewById(R.id.cb_all_sys);
		tv_app = (TextView) findViewById(R.id.tv_app_sys);
		tv_packagename = (TextView) findViewById(R.id.tv_packagename_sys);
		listView = (ListView) findViewById(R.id.lv_system);

		// 获取数据
		preferences = getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);
		String appName = preferences.getString("appName", "");// 监听的应用名字
		String packageName = preferences.getString("packageName", "");// 监听的应用包名

		jsonArray = JSON.toArray(SetSystemValueHelper
				.getFileData("SetSystemValueActivity"));
		if (jsonArray == null)
			jsonArray = new JSONArray();

		if (preferences.getBoolean("checkAll", true)) {
			cb_all.setChecked(true);
		}

		adapter = new MyAdapter();
		listView.setAdapter(adapter);

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

				boolean b = isChecked ? true : false;// 全选或全部不选

				// 将jsonArray的check全部改掉
				for (int i = 0; i < jsonArray.length(); i++) {

					try {
						JSONObject obj = jsonArray.getJSONObject(i);
						obj.optBoolean("check", b);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				
				preferences.edit().putBoolean("checkAll", b).commit();
				
				// 刷新listView
				adapter.notifyDataSetChanged();

			}
		});

	}

	// 弹出修改窗口
	public void showAlertDialog(final String key) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		View localView = LayoutInflater.from(SetSystemValueActivity.this)
				.inflate(R.layout.dialog_system, null);
		localBuilder.setView(localView);

		final EditText localEditText = (EditText) localView
				.findViewById(R.id.et_value);// 输入框
		Button localButton1 = (Button) localView.findViewById(R.id.btn_save);// 保存按钮
		Button localButton2 = (Button) localView.findViewById(R.id.btn_clear);// 清空按钮

		String str2 = Settings.System.getString(getContentResolver(), key);// 设置的系统值

		if (str2 != null)
			localEditText.setText(str2);

		final AlertDialog localAlertDialog = localBuilder.create();

		localButton2.setOnClickListener(new View.OnClickListener() {// 清空数据
					public void onClick(View paramAnonymousView) {

						Log.e("", "key="+key);
						android.provider.Settings.System.putString(SetSystemValueActivity.this.getContentResolver(), key, null);
						showTosat("清空成功！");
						localAlertDialog.cancel();

					}
				});

		localButton1.setOnClickListener(new View.OnClickListener() {// 保存数据
					public void onClick(View paramAnonymousView) {

						String str = localEditText.getText().toString();

						Settings.System.putString(SetSystemValueActivity.this
								.getContentResolver(), key, str);

						showTosat("设置成功！");
						localAlertDialog.cancel();
						return;
					}
				});
		localAlertDialog.show();

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_delete_sys:// 清除所选数据

			int j = 0;

			for (int i = 0; i < jsonArray.length(); i++) {

				try {
					String str = jsonArray.getJSONObject(i).optString(
							"systemkey");
					boolean bool = jsonArray.getJSONObject(i).optBoolean(
							"check", false);

					if (bool) {
						j++;
						Settings.System.putString(getContentResolver(), str,
								null);// 将系统值改为null
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			adapter.notifyDataSetChanged();
			showTosat("共清除" + j + "条记录");

			break;
		case R.id.btn_clear_sys:// 清空记录

			jsonArray = null;
			SetSystemValueHelper.deleteSelect(SetSystemValueActivity.this, true);
			SetSystemValueHelper.saveDataToFile("SetSystemValueActivity", JSON.toString(jsonArray));
			adapter.notifyDataSetChanged();

			break;

		}

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		SetSystemValueHelper.saveDataToFile("SetSystemValueActivity", JSON.toString(jsonArray));
		super.onDestroy();
	}

	// 弹出提示
	public void showTosat(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}

	/** 自定义adapter */
	public class MyAdapter extends BaseAdapter {

		LayoutInflater inflater;

		public MyAdapter() {
			inflater = LayoutInflater.from(SetSystemValueActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jsonArray != null ? jsonArray.length() : 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = inflater.inflate(R.layout.item_system, null);

				holder.buttonChange = (Button) convertView
						.findViewById(R.id.btn_change);
				holder.text_name = (TextView) convertView
						.findViewById(R.id.tv_key_name);
				holder.text_handleWay = (TextView) convertView
						.findViewById(R.id.tv_way);
				holder.check = (CheckBox) convertView
						.findViewById(R.id.checkBox);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}


			String str1 = "";
			String str2 = "";
			try {
				str1 = jsonArray.getJSONObject(position).getString("systemkey");
				str2 = jsonArray.getJSONObject(position).getString("statu");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			holder.text_name.setText(str1);
			holder.text_handleWay.setText(str2);
			holder.check.setTag(position);

			if (preferences.getBoolean("checkAll", true)) {
				holder.check.setChecked(true);
			} else {

				if (preferences.getBoolean(str1, false))
					holder.check.setChecked(true);
				else
					holder.check.setChecked(false);

			}

			holder.check
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							
							try {
								JSONObject obj = jsonArray.getJSONObject(position);
								
								// 保存状态
								if (isChecked)
									obj.put("check", true);
								else {
									obj.put("check", false);
									cb_all.setChecked(false);
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

			holder.buttonChange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						String key = jsonArray.getJSONObject(position)
								.getString("systemkey");
						showAlertDialog(key);// 弹出修改窗口
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			return convertView;
		}

		class ViewHolder {

			Button buttonChange;// 修改按钮
			CheckBox check;// 选择框
			TextView text_handleWay;// 操作方式
			TextView text_name;// key名

		}

	}

}
