package com.example.hdj008k_location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hdj008k_location.obj.HideApp;
import com.example.hdj008k_location.util.ListToString;
import com.example.hdj008k_location.util.RecordAppFileHandlerHelper;
import com.example.hdj008k_location.util.SetSystemValueHelper;
import com.example.hdj008k_location.util.StringUtil;

public class BrowserAppActivity extends Activity {

	private ListView listView;

	private BrowserListAdapter adapter;

	private List<HideApp> apps;

	private List<HideApp> newApps = new ArrayList<HideApp>();

	private ProgressDialog progress;
	
	private SharedPreferences preferences;

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			adapter = new BrowserListAdapter(BrowserAppActivity.this, newApps);
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			if (progress != null && progress.isShowing())
				progress.cancel();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		
		preferences = this.getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);

		listView = (ListView) findViewById(R.id.lv_select);
		progress = new ProgressDialog(this);
		progress.setMessage("正在加载应用列表");
		progress.setCancelable(false);
		progress.show();

		apps = new ArrayList<HideApp>();

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				getBrowserApp(BrowserAppActivity.this);

				String[] arrayOfString = sortIndex(apps);
				// Log.e("", "ttt-arrayOfString = "+arrayOfString.length);
				sortList(arrayOfString);

				runOnUiThread(runnable);
			}

		}.start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	// 保存选择的应用包名
	private void savechoose() {
		String str = "";
		boolean b = false;

		if (newApps != null){
			for (int i = 0; i < newApps.size(); i++) {

				boolean bool = newApps.get(i).isHide();
				String s = newApps.get(i).getPackAgeName();

				if (s != null && s.length() > 0 && bool) {
					str = str + s + "\n";
					b = true;
				}
			}
		}

		preferences.edit().putBoolean("browser", b).commit();
		SetSystemValueHelper.saveDataToFile("BrowserAppActivity", str);

	}

	// 将数组重新组装成list
	private void sortList(String[] paramArrayOfString) {

		// Log.e("SelectAppActivity---sortList", "paramArrayOfString = "
		// + paramArrayOfString.length);

		for (int i = 0; i < paramArrayOfString.length; i++) {
			// Log.e("SelectAppActivity---sortList", "paramArrayOfString = "
			// + paramArrayOfString[i]);
			HideApp localApp = new HideApp();

			if (paramArrayOfString[i].length() == 1) {

				localApp.setName(paramArrayOfString[i]);
				newApps.add(localApp);
			} else {
				for (int j = 0; j < apps.size(); j++) {

					if (paramArrayOfString[i].equals(apps.get(j)
							.getPinYinName())) {

						localApp.setName(apps.get(j).getName());
						localApp.setPinYinName(apps.get(j).getPinYinName());
						localApp.setPackAgeName(apps.get(j).getPackAgeName());
						localApp.setHide(apps.get(j).isHide());
						newApps.add(localApp);

					}
				}

			}

		}

		// Log.e("SelectAppActivity---sortList", "newApps = " + newApps.size());
	}

	/**
	 * 判断某一个应用程序是不是系统的应用程序，
	 * 
	 * @return 如果是返回true，否则返回false。
	 */
	public boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return false;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {// 判断是不是系统应用
			return false;
		}
		return true;
	}

	// 获取手机浏览器信息
	public void getBrowserApp(Context context) {
		String default_browser = "android.intent.category.DEFAULT";
		String browsable = "android.intent.category.BROWSABLE";
		String view = "android.intent.action.VIEW";

		Intent intent = new Intent(view);
		intent.addCategory(default_browser);
		intent.addCategory(browsable);
		Uri uri = Uri.parse("http://");
		intent.setDataAndType(uri, null);

		// 找出手机当前安装的所有浏览器程序
		List<ResolveInfo> resolveInfoList = context.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.GET_INTENT_FILTERS);

		List<String> list = ListToString
				.stringToList2(RecordAppFileHandlerHelper
						.getFileAllContent("BrowserAppActivity"));

		for (ResolveInfo info : resolveInfoList) {

			HideApp myAppInfo = new HideApp();
			ActivityInfo activityInfo = info.activityInfo;
			String pack = activityInfo.packageName;
			String className = getProgramNameByPackageName(this,pack);
			boolean hide = false;

			// 拿到包名
			myAppInfo.setPackAgeName(pack);
			Log.e("BrowserAppActivity--getBrowserApp", "PackAgeName = "+pack);
			// 拿到应用程序的程序名
			myAppInfo.setName(className);

			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					if (pack.equals(list.get(i)))
						hide = true;
				}
			}
			myAppInfo.setHide(hide);
			apps.add(myAppInfo);
		}

	}

	public String getProgramNameByPackageName(Context context,

	String packageName) {

		PackageManager pm = context.getPackageManager();

		String name = null;

		try {

			name = pm.getApplicationLabel(

			pm.getApplicationInfo(packageName,

			PackageManager.GET_META_DATA)).toString();

		} catch (NameNotFoundException e) {

			e.printStackTrace();

		}

		return name;

	}

	// 将list转成数组并排序
	public static String[] sortIndex(final List<HideApp> list) {
		TreeSet<String> set = new TreeSet<String>();
		Iterator<HideApp> iterator = list.iterator();

		while (iterator.hasNext()) {

			String s = iterator.next().getName();
			String pinStr = StringUtil.getPinYinHeadChar(s).substring(0, 1);
			set.add(pinStr);
		}

		final String[] array = new String[list.size() + set.size()];
		final Iterator<String> iterator2 = set.iterator();
		int n = 0;

		while (iterator2.hasNext()) {
			array[n] = iterator2.next();
			++n;
		}

		final String[] array2 = new String[list.size()];

		for (int i = 0; i < list.size(); ++i) {

			String py = StringUtil.HanyuToPinyin(list.get(i).getName()
					.toString());// 转换成拼音

			list.get(i).setPinYinName(py);
			array2[i] = py;

		}

		System.arraycopy(array2, 0, array, set.size(), array2.length);
		Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
		// Log.e("","ttt-array = "+array.length);
		return array;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			savechoose();
		}
		return super.onKeyDown(keyCode, event);
	}

	class BrowserListAdapter extends BaseAdapter {

		private Context context;
		private List<HideApp> list;

		public BrowserListAdapter(Context paramContext, List<HideApp> list) {
			this.context = paramContext;
			this.list = list;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int paramInt) {
			return list.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(final int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			String str = list.get(paramInt).getName();
			ViewHolder localViewHolder;
			if (paramView == null) {
				localViewHolder = new ViewHolder();
				paramView = LayoutInflater.from(this.context).inflate(
						R.layout.item_hide, null);
				localViewHolder.layout = (LinearLayout) paramView
						.findViewById(R.id.ll_hide);
				localViewHolder.indexTv = (TextView) paramView
						.findViewById(R.id.tv_index2);
				localViewHolder.itemTv = (TextView) paramView
						.findViewById(R.id.tv_hide_appname);
				localViewHolder.checkBox = (CheckBox) paramView
						.findViewById(R.id.cb_hide);
				paramView.setTag(localViewHolder);

			} else {
				localViewHolder = (ViewHolder) paramView.getTag();
			}

			if (str.length() == 1) {
				localViewHolder.indexTv
						.setText(newApps.get(paramInt).getName());
				localViewHolder.indexTv.setVisibility(View.VISIBLE);
				localViewHolder.layout.setVisibility(View.GONE);
				return paramView;
			}

			localViewHolder.itemTv.setText(list.get(paramInt).getName());
			localViewHolder.itemTv.setTag(list.get(paramInt).getPackAgeName());
			localViewHolder.layout.setVisibility(View.VISIBLE);
			localViewHolder.indexTv.setVisibility(View.GONE);

			localViewHolder.checkBox.setTag(paramInt);
			localViewHolder.checkBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							newApps.get(paramInt).setHide(isChecked);
						}
					});
			localViewHolder.checkBox.setChecked(list.get(paramInt).isHide());

			return paramView;
		}

		public boolean isEnabled(int paramInt) {
			return newApps.get(paramInt).getName().length() != 1
					&& super.isEnabled(paramInt);
		}

		private class ViewHolder {
			private LinearLayout layout;
			private TextView indexTv;
			private TextView itemTv;
			private CheckBox checkBox;

			private ViewHolder() {
			}
		}

	}

}
