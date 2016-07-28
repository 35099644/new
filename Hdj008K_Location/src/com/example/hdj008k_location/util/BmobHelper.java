package com.example.hdj008k_location.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

import com.example.hdj008k_location.obj.MobileData;

public class BmobHelper {

	public static final String BMOB_APPID = "9c5cda6af792af9a7cf9545c9ae13d90";// 在Bmob服务器端创建的Application
	private final String BMOB_OBJECTID = "YiWq555P";// Bmob服务器端创建的HDJ_register表的objectId
	public static final String PACKAGE_NAME = "com.example.dbtest";

	private Context mContext;
	private SharedPreferences mPreferences;
	private Handler mHandler;

	public BmobHelper(Context context) {
		this.mContext = context;
		Bmob.initialize(mContext, BMOB_APPID);
		BmobUpdateAgent.setUpdateOnlyWifi(false);
		// BmobUpdateAgent.initAppVersion(this);//第一次运行时要打开此段代码，在后台建立表，发布时要屏蔽掉
		mPreferences = context.getSharedPreferences(PACKAGE_NAME,
				context.MODE_PRIVATE);
	}

	public void setHandler(Handler mHandler) {
		// TODO Auto-generated method stub
		this.mHandler = mHandler;
	}

	public void register(final String num) {

		checkNum(num);
	}

	public void checkNum(final String num) {

		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String imei = telephonyManager.getDeviceId();
		BmobQuery<MobileData> query = new BmobQuery<MobileData>();

		try {
			query.addWhereEqualTo("imei", imei);
		} catch (Exception e) {
			// System.out.println("query.addWhereEqualTo()出错");
		}

		query.count(mContext, MobileData.class, new CountListener() {
			@Override
			public void onSuccess(int count) {
				// TODO Auto-generated method stub
				// 解除圈圈
				if (count > 0) {
					Toast.makeText(mContext, "手机已注册过!", 1000).show();
				} else {
					Log.e("BmobTool--checkNumToMobile()", "imei:" + imei
							+ "不存在");
					saveData(num);
				}
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				// 解除圈圈
				Log.e("BmobTool--checkNumToMobile()", "code = " + code
						+ "  错误:" + msg);
				
//				code = 9016  错误msg:The network is not available,please check your network!
						
						
				if (9010 == code || 9016 == code) {// 没有网络或网络延时的情况下执行
					Toast.makeText(mContext, "没有网络或网络延时", 1000).show();
				}
			}
		});

	}

	MobileData data;

	public void saveData(final String num) {
		data = new MobileData();

		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		data.setImei(imei);
		data.setIsAllowRun(false);
		data.setNum(num);
		// data.setRunTime(0);

		data.save(mContext, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				String objectId = data.getObjectId();
				Editor edit = mPreferences.edit();
				edit.putBoolean("firstOpen", false);
				edit.putString("objectId", objectId);
				edit.putString("num", num);
				edit.commit();
				Log.e("BmobTool--saveDataToMobile()", "objectId = " + objectId);
				if (mHandler != null)
					mHandler.sendEmptyMessage(100);
			}

			@Override
			public void onFailure(int code, String arg1) {
				// TODO Auto-generated method stub
				Log.e("BmobTool--saveDataToMobile()", "code = " + code
						+ "  错误:" + arg1);
				if (9010 == code || 9016 == code) {// 没有网络或网络延时的情况下执行
					Toast.makeText(mContext, "没有网络或网络延时", 1000).show();
				}
			}
		});
	}

	/**
	 * 判断是否允许运行
	 * */
	public void isAllowRun() {

		final String objectId = mPreferences.getString("objectId", null);
		String num = mPreferences.getString("num", null);

		if (TextUtils.isEmpty(objectId)) {

			return;
		}

		// test对应你刚刚创建的云端代码名称
		String cloudCodeName = "HdJ_BmobCode";
		JSONObject params = new JSONObject();
		// name是上传到云端的参数名称，值是bmob，云端代码可以通过调用request.body.name获取这个值
		try {
			params.put("objectId", objectId);
			params.put("num", num);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Log.e("", "objectId = "+objectId);
		// 创建云端代码对象
		AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
		// 异步调用云端代码
		cloudCode.callEndpoint(mContext, cloudCodeName, params,
				new CloudCodeListener() {

					// 执行成功时调用，返回result对象
					@Override
					public void onSuccess(Object result) {
						Log.e("BmobTool--isAllowRun()",
								"  result:" + result.toString());

						try {
							JSONObject obj = new JSONObject(result.toString());
							JSONArray array = obj.getJSONArray("results");

							for (int i = 0; i < array.length(); i++) {

								JSONObject re = array.getJSONObject(i);
								String update = re.getString("updatedAt");
								String id = re.getString("objectId");
								// int runTime = re.getInt("runTime");
								boolean isAllow = re.getBoolean("isAllowRun");

								if (id.equals(objectId)) {
									
									Editor edit = mPreferences.edit();
									// edit.putInt("runTime", runTime);
									edit.putString("update", update).commit();

									if (isAllow)
										mHandler.sendEmptyMessageDelayed(200,
												2000);
									else
										mHandler.sendEmptyMessage(300);
								}

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							mHandler.sendEmptyMessage(400);
							Log.e("BmobTool--isAllowRun()", "  错误:" + e);
						}

					}

					@Override
					public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
						Log.e("BmobTool--isAllowRun()", "code = " + code
								+ "  错误:" + msg);
						if (101 == code) {
							mHandler.sendEmptyMessage(400);
						} else if (9010 == code || 9016 == code) {// 没有网络或网络延时的情况下执行
							Toast.makeText(mContext, "没有网络或网络延时", 1000).show();
						}
					}
				});

	}
}
