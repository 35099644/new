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
 * ѡ��Ӧ�ý���
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
		progress.setMessage("���ڼ���Ӧ���б�");
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

	// ������������װ��list
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
	 * �ж�ĳһ��Ӧ�ó����ǲ���ϵͳ��Ӧ�ó���
	 * 
	 * @return ����Ƿ���true�����򷵻�false��
	 */
	public boolean filterApp(ApplicationInfo info) {
		// ��ЩϵͳӦ���ǿ��Ը��µģ�����û��Լ�������һ��ϵͳ��Ӧ����������ԭ���ģ�������ϵͳӦ�ã���������ж����������
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			Log.e("@@@@@@@@@@@@@@@@@@@@@", "!!!-----"+(info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP));
			return false;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {// �ж��ǲ���ϵͳӦ��
			return false;
		}
		return true;
	}

	// ��ȡ�ֻ�Ӧ��
	private void getInstalledApps() {
		
		List<PackageInfo> packageInfos = getPackageManager()
				.getInstalledPackages(
						PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

		for (PackageInfo info : packageInfos) {

			if (!filterApp(info.applicationInfo)) {
				App myAppInfo = new App();
				// �õ�����
				myAppInfo.setPackAgeName(info.packageName.trim());
				// �õ�Ӧ�ó������Ϣ
				ApplicationInfo appInfo = info.applicationInfo;
				// �õ�Ӧ�ó���ĳ�����
				myAppInfo.setName(appInfo.loadLabel(getPackageManager())
						.toString());
				apps.add(myAppInfo);
			}
		}

	}

	// ��listת�����鲢����
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
					.toString());// ת����ƴ��

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
