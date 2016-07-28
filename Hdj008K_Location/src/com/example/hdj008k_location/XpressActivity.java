package com.example.hdj008k_location;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hdj008k_location.obj.Folder;

public class XpressActivity extends Activity implements OnClickListener {

	private Button btn_folder1, btn_folder2;
	
	private LinearLayout mLayout;
	
	private EditText mEdit;
	
	private ListView mListView;

	private XpListAdapter mAdapter;

	private List<Folder> mList;

	private ProgressDialog mProgress;

	private SharedPreferences preferences;
	
	private int mTime;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mAdapter.notifyDataSetChanged();
				if (mProgress != null && mProgress.isShowing())
					mProgress.cancel();
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delfolder);

		preferences = this.getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub

		mLayout = (LinearLayout) findViewById(R.id.ll_delfolder);
		mEdit = (EditText) findViewById(R.id.et_delfolder);
		btn_folder1 = (Button) findViewById(R.id.btn_folder1);
		btn_folder2 = (Button) findViewById(R.id.btn_folder2);
		mListView = (ListView) findViewById(R.id.lv_delfolder);
		mProgress = new ProgressDialog(this);
		mProgress.setMessage("正在加载列表");
		mProgress.setCancelable(false);
		mProgress.show();

		mList = new ArrayList<Folder>();
		mAdapter = new XpListAdapter(this);
		mListView.setAdapter(mAdapter);
		mLayout.setVisibility(View.VISIBLE);
		
		mTime = preferences.getInt("xpTime", 10);
		mEdit.setText(mTime+"");
		
		
		btn_folder1.setOnClickListener(this);
		btn_folder2.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				XpListAdapter.ViewHolder holder = (XpListAdapter.ViewHolder) view
						.getTag();
				holder.checkBox.toggle();// 在每次获取点击的item时改变checkbox的状态

				for (int i = 0; i < mList.size(); i++) {
					if (i == position) {
						mList.get(i).setchoose(holder.checkBox.isChecked());
					} else
						mList.get(i).setchoose(false);
				}
				mAdapter.notifyDataSetChanged();
			}

		});

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mList = sortIndex(getFolderList());
				handler.sendEmptyMessage(0);
			}

		}.start();

	}

	/** 获取文件夹列表 */
	private List<Folder> getFolderList() {
		List<Folder> list = new ArrayList<Folder>();

		File file = Environment.getExternalStorageDirectory();
		File[] childFiles = file.listFiles();
		if (childFiles != null && childFiles.length > 0) {

			for (File f : childFiles) {
				if (f.isDirectory()) {
					Folder xpress = new Folder();
					String path = f.getAbsolutePath();
					xpress.setFolderName(f.getName());
					xpress.setFolderPath(path);
					if (path.equals(preferences.getString("xpFolder", "")))
						xpress.setchoose(true);
					else
						xpress.setchoose(false);
					list.add(xpress);
				}
			}

		}

		return list;
	}

	// 将list转成数组并排序
	public List<Folder> sortIndex(List<Folder> list) {

		List<Folder> l = new ArrayList<Folder>();

		if (list != null && list.size() > 0) {
			String[] array = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				array[i] = list.get(i).getFolderName();
			}

			Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);

			for (int i = 0; i < array.length; i++) {

				for (int j = 0; j < list.size(); j++) {

					if (array[i].equals(list.get(j).getFolderName())) {
						Folder xp = list.get(j);
						l.add(xp);
					}

				}

			}

		}

		return l;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_folder1:

			String path = null;
			for (int i = 0; i < mList.size(); i++) {

				if (mList.get(i).ischoose()) {
					path = mList.get(i).getFolderPath();
				}

			}
			
			String str = mEdit.getText().toString().trim();
			if(!TextUtils.isEmpty(str) && !str.equals("0")){
				int n = Integer.valueOf(str);
				
				Editor edit = preferences.edit(); 
				edit.putInt("xpTime", n);
				edit.putString("xpFolder", path).commit();
//				Log.e("########", str);
				finish();
			}else{
				
				Toast.makeText(XpressActivity.this, "请正确输入时间", 1000).show();
				
			}

			break;
		case R.id.btn_folder2:

			preferences.edit().putString("xpFolder", null).commit();
			finish();

			break;

		default:
			break;
		}
	}

	class XpListAdapter extends BaseAdapter {

		private Context context;

		public XpListAdapter(Context paramContext) {
			this.context = paramContext;
		}

		public int getCount() {
			return mList.size();
		}

		public Object getItem(int paramInt) {
			return mList.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(final int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			String name = mList.get(paramInt).getFolderName();
			ViewHolder localViewHolder;

			if (paramView == null) {
				localViewHolder = new ViewHolder();
				paramView = LayoutInflater.from(this.context).inflate(
						R.layout.item_delfolder, null);
				localViewHolder.itemTv = (TextView) paramView
						.findViewById(R.id.tv_folder);
				localViewHolder.checkBox = (CheckBox) paramView
						.findViewById(R.id.cb_folder);
				paramView.setTag(localViewHolder);

			} else {
				localViewHolder = (ViewHolder) paramView.getTag();
			}

			localViewHolder.itemTv.setText(name);
			localViewHolder.checkBox.setChecked(mList.get(paramInt).ischoose());

			return paramView;
		}

		private class ViewHolder {
			private TextView itemTv;
			private CheckBox checkBox;

			private ViewHolder() {
			}
		}

	}
}
