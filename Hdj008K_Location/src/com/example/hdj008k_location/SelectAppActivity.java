package com.example.hdj008k_location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.example.hdj008k_location.obj.App;
import com.example.hdj008k_location.util.StringUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 选择应用界面
 * */
public class SelectAppActivity extends Activity {

	private ListView listView;

	private MyListAdapter adapter;

	private List<App> apps;

	private List<App> newApps = new ArrayList<App>();

	private ProgressDialog progress;

	private String[] indexStr = { "#", "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z" };

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			adapter = new MyListAdapter(SelectAppActivity.this, newApps);
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

		listView = (ListView) findViewById(R.id.lv_select);
		progress = new ProgressDialog(this);
		progress.setMessage("正在加载应用列表");
		progress.setCancelable(false);
		progress.show();

		apps = new ArrayList<App>();

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				getInstalledApps();

				String[] arrayOfString = sortIndex(apps);
				sortList(arrayOfString);

				runOnUiThread(runnable);
			}

		}.start();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				String appN = newApps.get(position).getName();
				String packageN = newApps.get(position).getPackAgeName();

				if (appN != null && packageN != null) {
					Intent localIntent = new Intent();
					localIntent.putExtra("appName", appN);
					localIntent.putExtra("packageName", packageN);
					SelectAppActivity.this.setResult(0, localIntent);
					SelectAppActivity.this.finish();
				}

			}
		});
	}

	// 将数组重新组装成list
	private void sortList(String[] paramArrayOfString) {

		for (int i = 0; i < paramArrayOfString.length; i++) {

			App localApp = new App();

			if (paramArrayOfString[i].length() == 1) {

				// Log.e("SelectAppActivity---sortList", "paramArrayOfString = "
				// + paramArrayOfString[i]);

				localApp.setName(paramArrayOfString[i]);
				newApps.add(localApp);
			} else {
				for (int j = 0; j < apps.size(); j++) {

					if (paramArrayOfString[i].equals(apps.get(j)
							.getPinYinName())) {

						// Log.e("SelectAppActivity---sortList", "PinYinName = "
						// + apps.get(j).getPinYinName());

						localApp.setName(apps.get(j).getName());
						localApp.setPinYinName(apps.get(j).getPinYinName());
						localApp.setPackAgeName(apps.get(j).getPackAgeName());
						newApps.add(localApp);

					}
				}

			}

		}

		Log.e("SelectAppActivity---sortList", "newApps = " + newApps.size());
	}

	/**
	 * 判断某一个应用程序是不是系统的应用程序，
	 * 
	 * @return 如果是返回true，否则返回false。
	 */
	public boolean filterApp(ApplicationInfo info) {
		// 有些系统应用是可以更新的，如果用户自己下载了一个系统的应用来更新了原来的，它还是系统应用，这个就是判断这种情况的
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			Log.e("@@@@@@@@@@@@@@@@@@@@@", "!!!-----"+(info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP));
			return false;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {// 判断是不是系统应用
			return false;
		}
		return true;
	}

	// 获取手机应用
	private void getInstalledApps() {
		
		List<PackageInfo> packageInfos = getPackageManager()
				.getInstalledPackages(
						PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

		for (PackageInfo info : packageInfos) {

			if (!filterApp(info.applicationInfo)) {
				App myAppInfo = new App();
				// 拿到包名
				myAppInfo.setPackAgeName(info.packageName.trim());
				// 拿到应用程序的信息
				ApplicationInfo appInfo = info.applicationInfo;
				// 拿到应用程序的程序名
				myAppInfo.setName(appInfo.loadLabel(getPackageManager())
						.toString());
				apps.add(myAppInfo);
			}
		}

	}

	// 将list转成数组并排序
	public static String[] sortIndex(final List<App> list) {
		TreeSet<String> set = new TreeSet<String>();
		Iterator<App> iterator = list.iterator();
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
		return array;
	}

	class MyListAdapter extends BaseAdapter {

		private Context context;
		private List<App> list;
		private ViewHolder viewHolder;

		public MyListAdapter(Context paramContext, List<App> paramList) {
			this.context = paramContext;
			this.list = paramList;
		}

		public int getCount() {
			return this.list.size();
		}

		public Object getItem(int paramInt) {
			return this.list.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			String str = ((App) this.list.get(paramInt)).getName();
			ViewHolder localViewHolder;
			if (paramView == null) {
				localViewHolder = new ViewHolder();
				paramView = LayoutInflater.from(this.context).inflate(
						R.layout.item_select, null);
				localViewHolder.indexTv = (TextView) paramView
						.findViewById(R.id.tv_index);
				localViewHolder.itemTv = (TextView) paramView
						.findViewById(R.id.tv_appname);
				paramView.setTag(localViewHolder);

			} else {
				localViewHolder = (ViewHolder) paramView.getTag();
			}

			if (str.length() == 1) {
				localViewHolder.indexTv.setText(list.get(paramInt).getName());
				localViewHolder.indexTv.setVisibility(View.VISIBLE);
				localViewHolder.itemTv.setVisibility(View.GONE);
				return paramView;
			}

			localViewHolder.itemTv.setText(list.get(paramInt).getName());
			localViewHolder.itemTv.setTag(((App) this.list.get(paramInt))
					.getPackAgeName());
			localViewHolder.itemTv.setVisibility(View.VISIBLE);
			localViewHolder.indexTv.setVisibility(View.GONE);

			return paramView;
		}

		public boolean isEnabled(int paramInt) {
			return list.get(paramInt).getName().length() != 1
					&& super.isEnabled(paramInt);
		}

		private class ViewHolder {
			private TextView indexTv;
			private TextView itemTv;

			private ViewHolder() {
			}
		}

	}

}
