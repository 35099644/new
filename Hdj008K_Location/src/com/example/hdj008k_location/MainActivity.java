package com.example.hdj008k_location;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hdj008k_location.common.Common;
import com.example.hdj008k_location.db.DBOpenHelper;
import com.example.hdj008k_location.db.HdjDbService;
import com.example.hdj008k_location.db.HistoryDBManager;
import com.example.hdj008k_location.db.JiZhanDBManager;
import com.example.hdj008k_location.obj.AdStatus;
import com.example.hdj008k_location.obj.HistoryData;
import com.example.hdj008k_location.obj.JiZhan;
import com.example.hdj008k_location.obj.MyFileInfo;
import com.example.hdj008k_location.obj.WifiData;
import com.example.hdj008k_location.service.InstallApkService;
import com.example.hdj008k_location.service.InstallReceiver;
import com.example.hdj008k_location.util.InstallUtil;
import com.example.hdj008k_location.util.ListToString;
import com.example.hdj008k_location.util.MyFileUtil;
import com.example.hdj008k_location.util.RandomData;
import com.example.hdj008k_location.util.RecordAppFileHandlerHelper;
import com.example.hdj008k_location.util.SetSystemValueHelper;
import com.example.hdj008k_location.util.WifiDataHelper;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	private ProgressDialog mProgress;
	private LinearLayout mLayout;
	private Button btn_random1, btn_bc1, btn_tool1, btn_model1,
			btn_xpress_install, btn_xpress_open, btn_xpress_uninstall;
	private TextView tv_install, textview1, tv_uninstall, textview2, textview3,
			tv_browser;
	private EditText et_imei, et_android, et_phone, et_phonenum, et_imsi,
			et_yys, et_net, et_netnum, et_mac, et_wx, et_wxdz, et_bb, et_bbz,
			et_fbl, et_pp, et_xh, et_cpm, et_zzs, et_sbm, et_ck, et_ly, et_ip,
			et_yj, et_zw, et_lat, et_lng, et_simstate, et_cpu, et_gjbb, et_ua,
			et_lac, et_cid,et_id,et_display,et_host;

	private String mPackageN;// 选择的应用包名
	private String randomDB;// 随机手机品牌
	private String randomImei;// 随机imei
	private String randomDN;// 随机型号
	private String randomOV;// 随机系统版本
	private String randomSH;// 随机分辨率高
	private String randomSW;// 随机分辨率宽
	private String randomZW;// 随机指纹
	private String randomCPU;// 随机cpu
	private String randomSB;// 随机设备名
	private String randomCP;// 随机产品名
	private String randomZZS;// 随机制造商
	private String randomYJ;// 随机硬件
	private String randomGJBB;// 随机固件版本
	private String randomUA;// 随机UserAgent
	private String realSsid;// 当前真实的路由器名

	private int Ip;// 内网ip
	private boolean isRunLiucun;// 是否跑留存
	private boolean isRandomData;
	private boolean canSaveData = true;

	private Context ctx;
	private SharedPreferences preferences;
	private JSONArray dataArray;
	private List<WifiData> mWifiData;
	public static InstallReceiver receiver = null;

	private int xpType;
	private Runnable xpRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Log.e("XpressThread--run", "Thread start");

			if (xpType == 4) {
				deleteData();// 清空记录的数据
				return;
			}

			String path = preferences.getString("xpFolder", null);
			File file = new File(path);
			List<MyFileInfo> list = InstallUtil.checkFolder(ctx, file);
			Iterator<MyFileInfo> it = list.iterator();

			int time = 4;

			while (it.hasNext()) {

				MyFileInfo f = it.next();

				if (xpType == 0) {
					Log.e("XpressThread--run--0", "size = " + list.size());

					String xpath = MyFileUtil.changeName(f.getFilePath());

					if (InstallUtil.haveRoot("pm install -r " + xpath))
						Log.e("MainActivity--xpRunnable", f.getFileName()
								+ ": 安装成功---" + f.getFilePath());

				} else if (xpType == 1) {
					Log.e("XpressThread--run--1", "Thread start");

					time = ctx.getSharedPreferences("prefs",
							Context.MODE_WORLD_READABLE).getInt("xpTime", 10);

					PackageManager packageManager = ctx.getPackageManager();
					Intent intent2 = packageManager.getLaunchIntentForPackage(f
							.getFileName());
					if (intent2 != null) {
						ctx.startActivity(intent2);
						Log.e("InstallUtil--UninstallAppArray", f.getFileName()
								+ ": 打开");
					}

				} else if (xpType == 2) {
					Log.e("XpressThread--run--2", "Thread start");
					if (InstallUtil.clientUninstall(f.getFileName()))
						Log.e("InstallUtil--UninstallAppArray", f.getFileName()
								+ ": 卸载成功");
				}

				try {
					Thread.sleep(time * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (xpType == 2)
				MyFileUtil.deleteFolder(path);// 删除APK
			stopProgress();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_main);

		init();
		initView();

		// 判断应用有没有root权限
		if (!InstallUtil.haveRoot("chmod 777 " + getPackageCodePath()))
			showAlDialog("没有root权限,请确认已经正确的root!");

		// if(!Common.haveSDcard())
		// showAlDialog("没有找到sd卡,请确保手机装有sd卡!");

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshView();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unRegisterNotify();
		stopService(new Intent(ctx, InstallApkService.class));
		super.onDestroy();
	}

	// private void CreateDB() {
	// // TODO Auto-generated method stub
	//
	// long time = System.currentTimeMillis();
	// long time2 = time - (48 * 60 * 60 * 1000);
	// String str = getDateString(time);// 今天
	// String str2 = getDateString(time2);// 前天
	//
	// HistoryDBManager dbManager = new HistoryDBManager(ctx);
	// dbManager.onCreateTable("hdj" + str);// 创建新表
	// dbManager.deleteTab("hdj" + str2);// 把前天的表删掉
	// }

	private void init() {
		ctx = this;
		MobclickAgent.setDebugMode(true);
		// CreateDB();
		startService(new Intent(ctx, InstallApkService.class));// 启动服务
		preferences = this.getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);

		// dataArray = dataToJSONArray(getImeiToFile("Hdj008_imei"));
		dataArray = RandomData.dataToJSONArray(RandomData.getImeiToFile(ctx,
				"Hdj008_imei2"));
		JiZhanDBManager dbm = new JiZhanDBManager(this);
		dbm.openDatabase();
		dbm.close();
	}

	/** 初始化控件 */
	private void initView() {

		mLayout = (LinearLayout) findViewById(R.id.ll_xpress);

		tv_install = (TextView) findViewById(R.id.tv_install);
		tv_uninstall = (TextView) findViewById(R.id.tv_uninstall);
		textview1 = (TextView) findViewById(R.id.textview1);
		textview2 = (TextView) findViewById(R.id.textview2);
		textview3 = (TextView) findViewById(R.id.textview3);
		tv_browser = (TextView) findViewById(R.id.tv_browser);

		btn_random1 = (Button) findViewById(R.id.btn_random1);
		btn_bc1 = (Button) findViewById(R.id.btn_bc1);
		btn_tool1 = (Button) findViewById(R.id.btn_tool1);
		btn_model1 = (Button) findViewById(R.id.btn_model1);
		btn_xpress_install = (Button) findViewById(R.id.btn_xpress_install);
		btn_xpress_open = (Button) findViewById(R.id.btn_xpress_open);
		btn_xpress_uninstall = (Button) findViewById(R.id.btn_xpress_uninstall);

		et_imei = (EditText) findViewById(R.id.et_imei);
		et_android = (EditText) findViewById(R.id.et_android);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_phonenum = (EditText) findViewById(R.id.et_phonenum);
		et_imsi = (EditText) findViewById(R.id.et_imsi);
		et_yys = (EditText) findViewById(R.id.et_yys1);
		et_net = (EditText) findViewById(R.id.et_net);
		et_netnum = (EditText) findViewById(R.id.et_netnum);
		et_mac = (EditText) findViewById(R.id.et_mac);
		et_wx = (EditText) findViewById(R.id.et_wx);
		et_wxdz = (EditText) findViewById(R.id.et_wxdz);
		et_bb = (EditText) findViewById(R.id.et_bb);
		et_bbz = (EditText) findViewById(R.id.et_bbz);
		et_fbl = (EditText) findViewById(R.id.et_fbl);
		et_pp = (EditText) findViewById(R.id.et_pp);
		et_xh = (EditText) findViewById(R.id.et_xh);
		et_cpm = (EditText) findViewById(R.id.et_cpm);
		et_zzs = (EditText) findViewById(R.id.et_zzs);
		et_sbm = (EditText) findViewById(R.id.et_sbm);
		et_ck = (EditText) findViewById(R.id.et_ck);
		et_ly = (EditText) findViewById(R.id.et_ly);
		et_ip = (EditText) findViewById(R.id.et_ip);
		et_lat = (EditText) findViewById(R.id.et_lat);
		et_lng = (EditText) findViewById(R.id.et_lng);
		et_simstate = (EditText) findViewById(R.id.et_simstate1);
		et_yj = (EditText) findViewById(R.id.et_yj);
		et_zw = (EditText) findViewById(R.id.et_zw);
		et_cpu = (EditText) findViewById(R.id.et_cpu);
		et_gjbb = (EditText) findViewById(R.id.et_gjbb);
		et_ua = (EditText) findViewById(R.id.et_ua1);
		et_lac = (EditText) findViewById(R.id.et_lac);
		et_cid = (EditText) findViewById(R.id.et_cid);
		et_id = (EditText) findViewById(R.id.et_id);
		et_display = (EditText) findViewById(R.id.et_display);
		et_host = (EditText) findViewById(R.id.et_host);
		
		btn_random1.setOnClickListener(new OnClickListener() {// 随机按钮

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						if (preferences.getBoolean("openLiucun", false)) {
							liucunData();
						} else {
							randomData();
						}
					}
				});

		btn_bc1.setOnClickListener(new OnClickListener() {// 保存按钮

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (!canSaveData)
					return;

				canSaveData = false;
				saveData();// 保存随机数据
				saveDateToDB();// 保存数据到数据库中

				String packag = preferences.getString("packageName", "");
				SetSystemValueHelper.deleteSelect(ctx, true);// 清除系统值
				RecordAppFileHandler.deleteDate(ctx, packag);// 清除文件

				xpType = 4;
				new Thread(xpRunnable).start();

			}
		});

		btn_tool1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ctx, ToolkitActivity.class);
				startActivity(intent);
			}
		});

		btn_model1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// arg0.showContextMenu();//弹出菜单
				Intent intent = new Intent();
				intent.setClass(ctx, SelectAppActivity.class);
				startActivityForResult(intent, 2);
			}
		});

		btn_xpress_install.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				xpType = 0;
				showProgress("正在处理中");
				new Thread(xpRunnable).start();

			}
		});

		btn_xpress_open.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				xpType = 1;
				new Thread(xpRunnable).start();
			}
		});

		btn_xpress_uninstall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				xpType = 2;
				showProgress("正在处理中");
				new Thread(xpRunnable).start();
			}
		});

		// registerForContextMenu(btn_model1);
		startData();
	}

	private void startData() {

		// 获取手机imei

		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);

		WifiManager wifi_service = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);

		WifiInfo wifiInfo = wifi_service.getConnectionInfo();

		String imei = tm.getDeviceId();
		setEditText(et_imei, imei);// 手机imei

		String ai = Settings.Secure.getString(ctx.getContentResolver(),
				Settings.Secure.ANDROID_ID);
		setEditText(et_android, ai);// 手机ANDROID_ID

		String phonenumber = tm.getLine1Number();
		setEditText(et_phone, phonenumber);// 手机卡号码

		String sim = tm.getSimSerialNumber();
		setEditText(et_phonenum, sim);// 手机卡序列号

		String imsi = tm.getSubscriberId();
		setEditText(et_imsi, imsi);// 手机imsi

		String yys = tm.getSimOperator();
		setEditText(et_yys, yys);// 运营商

		String net = tm.getNetworkOperatorName();
		setEditText(et_net, net);// 网络运营商名
		
		String net_num = tm.getNetworkType() + "";
		setEditText(et_netnum, net_num);// 网络类型

		String mac = wifiInfo.getMacAddress();
		setEditText(et_mac, mac);// mac地址

		realSsid = wifiInfo.getSSID();
		setEditText(et_wx, realSsid);// 无线路由器名

		String wxdz = wifiInfo.getBSSID();
		setEditText(et_wxdz, wxdz);// 无线路由器地址

		// setEditText(et_lac, null);// 基站lac
		// setEditText(et_cid, null);// 基站cid

		String xlh = android.os.Build.SERIAL;
		setEditText(et_ck, xlh);// 串口序列号

		String bb = android.os.Build.VERSION.RELEASE;
		setEditText(et_bb, bb);// 系统版本

		String bbz = android.os.Build.VERSION.SDK;
		setEditText(et_bbz, bbz);// 系统版本值

		String pp = android.os.Build.BRAND;
		setEditText(et_pp, pp);// 手机品牌

		String xh = android.os.Build.MODEL;
		setEditText(et_xh, xh);// 手机型号

		String cpm = android.os.Build.PRODUCT;
		setEditText(et_cpm, cpm);// 产品名称

		String zzs = android.os.Build.MANUFACTURER;
		setEditText(et_zzs, zzs);// 制造商

		String sb = android.os.Build.DEVICE;
		setEditText(et_sbm, sb);// 设备名

		String fingerprint = android.os.Build.FINGERPRINT;
		setEditText(et_zw, fingerprint);// 指纹

		String hardware = android.os.Build.HARDWARE;
		setEditText(et_yj, hardware);// 硬件

		String gj = android.os.Build.getRadioVersion();
		setEditText(et_gjbb, gj);// 固件版本

		String cpu = Common.getCpuName(ctx);
		setEditText(et_yj, cpu);// cpu
		
		String id = android.os.Build.ID;
		setEditText(et_id, id);
		
		String display = android.os.Build.DISPLAY;
		setEditText(et_display, display);
		
		String host = android.os.Build.HOST;
		setEditText(et_host, host);

		String ip = RandomData.intToIp(wifiInfo.getIpAddress());
		setEditText(et_ip, ip);// 内网IP

		int state = tm.getSimState();
		setEditText(et_simstate, state + "");// 手机卡状态

		WebView webview = new WebView(this);
		webview.layout(0, 0, 0, 0);
		WebSettings settings = webview.getSettings();
		String ua = settings.getUserAgentString();
		setEditText(et_ua, ua);

		setEditText(et_lat, Common.getLat(ctx) + "");
		setEditText(et_lng, Common.getLng(ctx) + "");

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		setEditText(et_fbl, displaymetrics.widthPixels + "x"
				+ displaymetrics.heightPixels);// 屏幕分辨率

		String appN = preferences.getString("appName", "");
		mPackageN = preferences.getString("packageName", "");

		if (appN.length() > 0 && mPackageN.length() > 0) {
			btn_model1.setText(appN + "_" + mPackageN);
			return;
		}

		btn_model1.setText("单个应用");

	}

	// activity关闭的时候关闭广播
	public void unRegisterNotify() {

		if (receiver != null)
			this.unregisterReceiver(receiver);
		receiver = null;

	}

	/**
	 * 跑留存数据
	 * */
	protected void liucunData() {
		// TODO Auto-generated method stub

		int i = preferences.getInt("Liucun", 20);// 次日留存率
		if (i <= 0) {// 没设置留存率就随机数据
			randomData();
			Log.e("MainActivity--liucunData()", "留存率为0");
			return;
		}

		HistoryDBManager dbManager = new HistoryDBManager(ctx);
		long time = System.currentTimeMillis();
		String now = getDateString(time);// 今天
		int maxData = dbManager.getSize(DBOpenHelper.TABNAME,now);// 今天之前的数据 
		if(maxData>0)
			maxData = dbManager.getSize(DBOpenHelper.TABNAME);//留存数据库有多少数据
		int liucun = maxData * i / 100;// 需要跑多少留存数据
		int run = dbManager.getSize(DBOpenHelper.RUNTABNAME);// 跑了多少条留存数据

		if (maxData <= 0) {
			randomData();
			Log.e("MainActivity--liucunData()", "留存表中没有数据");
			return;
		}

		if (run < liucun) {

			// 没有跑完留存就从数据库中获取数据
			Random random = new Random();
			int id = random.nextInt(maxData);// 随机主键id
			HistoryData historyData = dbManager.getData(id + "");// 根据id获取数据
			
			if(historyData!=null){
				showData(historyData);
				Log.e("MainActivity--liucunData()", "跑留存");
			}else
				randomData();

		} else {
			randomData();
			Log.e("MainActivity--liucunData()", "留存数据已跑完");
		}
	}

	/**
	 * 随机数据
	 * */
	private void randomData() {
		// Log.e("MainActivity----------randomData()", "随机");
		isRunLiucun = false;

		if (isRandomData)
			return;
		if (!randomArray2()) {
			Toast.makeText(ctx, "随机数据失败", 1000).show();
			return;
		}
		setEditText(et_pp, randomDB);// 随机品牌
		setEditText(et_imei, RandomData.randomImei(randomImei));
		setEditText(et_android, RandomData.getRandomCharAndNumr(16));

		String[] a = RandomData.getSimData(randomImei);
		if (a.length == 5) {
			setEditText(et_imsi, a[0]);// imsi
			setEditText(et_yys, a[1]);// 运营商
			setEditText(et_net, a[2]);// 网络类型名
			setEditText(et_phonenum, a[3]);// 手机序列号
			setEditText(et_netnum, a[4]);// 网络类型
		}

		String wx = RandomData.random16(10);
		String wxdz = RandomData.randomMac();
		setEditText(et_wxdz, wxdz);// 路由器地址
		setEditText(et_wx, wx);// 路由器名

		mWifiData = RandomData.randomWifiData(this, wx, wxdz);

		JiZhan jizhan = randomJiZhan();
		setEditText(et_lac, jizhan.getLac() + "");// 基站lac
		setEditText(et_cid, jizhan.getCellId() + "");// 基站cid
		setEditText(et_lat, RandomData.randomLat(jizhan.getLat()));
		setEditText(et_lng, RandomData.randomLng(jizhan.getLng()));

		setEditText(et_simstate, "5");// 手机卡状态
		setEditText(et_phone, RandomData.randomPhone(a[0]));// 手机号码
		setEditText(et_mac, RandomData.randomMac());
		setEditText(et_ck, RandomData.random16(8));// 串口序列号
		setEditText(et_bb, randomOV);
		setEditText(et_bbz, RandomData.randomBBZ(randomOV));// sdk
		setEditText(et_zw, randomZW);// 指纹
		setEditText(et_gjbb, randomGJBB);// 固件版本
		setEditText(et_yj, randomYJ);// 硬件
		setEditText(et_cpm, randomCP);// 产品名称
		setEditText(et_zzs, randomZZS);// 制造商
		setEditText(et_sbm, randomSB);// 设备名
		setEditText(et_cpu, randomCPU);// cpu
		setEditText(et_id, RandomData.randomHexadecimalString(12));// id
		setEditText(et_display, RandomData.randomHexadecimalString(12));// display
		setEditText(et_host, RandomData.randomHexadecimalString(12));// host
		setEditText(et_ua, randomUA);// UserAgent
		setEditText(et_xh, randomDN);// 机型
		setEditText(et_ly, RandomData.randomMac());// 蓝牙
		// 内网IP
		Ip = RandomData.randomIP(ctx);
		setEditText(et_ip, RandomData.intToIp(Ip));
		setEditText(et_fbl, randomSW + "x" + randomSH);

	}

	/** 显示留存数据 */
	private void showData(HistoryData data) {
		// TODO Auto-generated method stub
		Log.e("MainActivity----------showData()", "跑留存");
		isRunLiucun = true;

		String imei = data.getImei();
		String android_id = data.getAndroidId();
		String phone = data.getPhone();
		String phone_num = data.getPhoneNum();
		String imsi = data.getImsi();
		String yys = data.getYys();
		String mac = data.getMac();
		String wx = data.getWx();
		String wxdz = data.getWxdz();
		String version = data.getVersion();
		String sdk = data.getSdk();
		String brand = data.getBrand();
		String xh = data.getXh();
		String bluemac = data.getBluemac();
		String ipadress = data.getIpadress();
		String network_type = data.getNetworkType();
		String network_operatorName = data.getNetworkOperatorName();
		String simstate = data.getSimstate();
		String serial = data.getSerial();
		String zhiwen = data.getZhiwen();
		String radioVersion = data.getRadioVersion();
		String hardware = data.getHardware();
		String manufacturer = data.getManufacturer();
		String product = data.getProduct();
		String device = data.getDevice();
		String lat = data.getLat();
		String lng = data.getLng();
		randomSW = data.getFbl_w();
		randomSH = data.getFbl_h();
		String cpu = data.getCpu();
		String ua = data.getUserAgent();
		String lac = data.getLac();
		String cid = data.getCellid();

		String id = data.getId();
		String display = data.getDisplay();
		String host = data.getHost();
		
		setEditText(et_pp, brand);// 随机品牌

		setEditText(et_imei, imei);
		setEditText(et_android, android_id);
		setEditText(et_imsi, imsi);// imsi
		setEditText(et_yys, yys);// 运营商
		setEditText(et_net, network_operatorName);// 网络类型名
		setEditText(et_phonenum, phone_num);// 手机卡序列号
		setEditText(et_netnum, network_type);// 网络类型
		setEditText(et_simstate, simstate);// 手机卡状态
		setEditText(et_phone, phone);// 手机号码
		setEditText(et_mac, mac);
		setEditText(et_wx, wx);// 路由器名
		setEditText(et_ck, serial);// 串口序列号
		setEditText(et_wxdz, wxdz);
		setEditText(et_bb, version);
		setEditText(et_bbz, sdk);
		setEditText(et_zw, zhiwen);// 指纹
		setEditText(et_gjbb, radioVersion);// 固件版本
		setEditText(et_yj, hardware);// 硬件
		setEditText(et_cpm, product);// 产品名称
		setEditText(et_zzs, manufacturer);// 制造商
		setEditText(et_sbm, device);// 设备名
		setEditText(et_cpu, cpu);// cpu
		setEditText(et_ua, ua);// UserAgent
		setEditText(et_xh, xh);
		// 蓝牙
		setEditText(et_ly, bluemac);
		// 内网IP
		setEditText(et_ip, ipadress);
		setEditText(et_fbl, randomSW + "x" + randomSH);
		setEditText(et_lat, lat);
		setEditText(et_lng, lng);
		setEditText(et_lac, lac);
		setEditText(et_cid, cid);
		setEditText(et_id, id);
		setEditText(et_display, display);
		setEditText(et_host, host);

	}

	// /**
	// * 随机数据
	// * */
	// private void randomData() {
	//
	// if (!randomArray())
	// return;
	//
	// setEditText(et_pp, randomDB);// 随机品牌
	//
	// setEditText(et_imei, randomImei(randomImei));
	// setEditText(et_android, getRandomCharAndNumr(16));
	//
	// String[] a = getSimData();
	// if (a.length == 5) {
	// setEditText(et_imsi, a[0]);// imsi
	// setEditText(et_yys, a[1]);// 运营商
	// setEditText(et_net, a[2]);// 网络类型名
	// setEditText(et_phonenum, a[3]);// 手机序列号
	// setEditText(et_netnum, a[4]);// 网络类型
	// }
	//
	// setEditText(et_simstate, "5");// 手机卡状态
	// setEditText(et_phone, randomPhone(a[0]));// 手机号码
	//
	// setEditText(et_mac, randomMac());
	// setEditText(et_wx, random16(10));//路由器名
	// setEditText(et_ck, random16(8));//串口序列号
	//
	// setEditText(et_wxdz, randomMac());
	// setEditText(et_bb, randomOV);
	// setEditText(et_bbz, randomBBZ(randomOV));
	//
	// setEditText(et_cpm, randomDB + " " + randomDN);// 产品名称
	// setEditText(et_zzs, randomDB);// 制造商
	// setEditText(et_sbm, randomDN);// 设备名
	//
	// setEditText(et_xh, randomDN);
	//
	// // 蓝牙
	// setEditText(et_ly, randomMac());
	//
	// // 内网IP
	// setEditText(et_ip, randomIP() + "");
	//
	// setEditText(et_fbl, randomSW + "x" + randomSH);
	//
	// setEditText(et_lat, randomLat());
	// setEditText(et_lng, randomLng());
	//
	// }

	// /** 从Hdj008_imei中数据获取数据 */
	// private boolean randomArray() {
	//
	// // String head[] = new String[dataArray.length()];
	// try {
	// if (dataArray == null) {
	// Toast.makeText(ctx, "随机数据失败，获取资料失败!", Toast.LENGTH_SHORT)
	// .show();
	// return false;
	// }
	// if (dataArray.length() == 0) {
	// Toast.makeText(ctx, "Hdj008_imei数据为空", Toast.LENGTH_SHORT)
	// .show();
	// return false;
	// }
	//
	// Random rnd = new Random();
	// int n = rnd.nextInt(dataArray.length());
	//
	// randomDB = dataArray.getJSONObject(n).getString("db");
	// JSONArray array = dataArray.getJSONObject(n).getJSONArray("array");
	//
	// if (array.length() == 0) {
	// Toast.makeText(ctx, "imei等数据为空", Toast.LENGTH_SHORT).show();
	// return false;
	// }
	//
	// Random rnd2 = new Random();
	// int m = rnd2.nextInt(array.length());
	//
	// randomImei = array.getJSONObject(m).getString("imei");
	// randomDN = array.getJSONObject(m).getString("dn");
	// randomOV = array.getJSONObject(m).getString("ov");
	// randomSH = array.getJSONObject(m).getString("sh");
	// randomSW = array.getJSONObject(m).getString("sw");
	//
	// return true;
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return false;
	// }
	//
	// }

	/** 从Hdj008_imei2中随机获取imei等手机数据 */
	private boolean randomArray2() {

		isRandomData = true;

		// String head[] = new String[dataArray.length()];
		try {
			if (dataArray == null) {
				Toast.makeText(ctx, "随机数据失败，获取资料失败!", Toast.LENGTH_SHORT)
						.show();
				return false;
			}
			if (dataArray.length() == 0) {
				Toast.makeText(ctx, "Hdj008_imei2数据为空", Toast.LENGTH_SHORT)
						.show();
				return false;
			}

			Random rnd = new Random();
			int n = rnd.nextInt(dataArray.length());
			JSONObject obj = dataArray.getJSONObject(n);

			randomDB = obj.getString("BRAND");// 手机品牌
			randomImei = obj.getString("getDeviceId");
			randomDN = obj.getString("MODEL");// 机型
			randomOV = obj.getString("RELEASE");// 系统版本
			randomZW = obj.getString("FINGERPRINT");// 随机指纹
			randomCPU = obj.getString("setCpuName");// 随机cpu
			randomSB = obj.getString("DEVICE");// 随机设备名
			randomCP = obj.getString("PRODUCT");// 随机产品名
			randomZZS = obj.getString("MANUFACTURER");// 随机制造商
			randomYJ = obj.getString("HARDWARE");// 随机硬件
			randomGJBB = obj.getString("getRadioVersion");// 随机固件版本

			String hw = obj.getString("getMetrics");
			String[] s = hw.split("x");
			randomSW = s[0];// 宽
			randomSH = s[1];// 高

			String[] s2 = randomZW.split("/");
			randomUA = RandomData.randomUA(randomOV, randomDN, s2[3]);// 随机UserAgent

			isRandomData = false;
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("MainActivity--randomArray2()", e.getMessage());
		}
		isRandomData = false;
		return false;
	}

	/** 从Lac_cell数据库中随机获取基站数据 */
	private JiZhan randomJiZhan() {

		JiZhanDBManager dbm = new JiZhanDBManager(this);
		dbm.openDatabase();
		JiZhan jizhan = dbm.randomJiZhanData();
		dbm.close();
		return jizhan;
	}

	/**
	 * 删除所有数据
	 * */
	protected void deleteData() {
		// TODO Auto-generated method stub

		String packag = preferences.getString("packageName", "");

		RecordAppFileHandlerHelper.clearFileData("InstallApk.txt");// 清除apk下载记录文件
		RecordAppFileHandlerHelper.clearFileData("InstallApkPackageName.txt");// 清除apk下载记录文件
		InstallUtil.clearAppData("pm clear " + packag);// 清除应用缓存等数据

		List<String> Browserlist = ListToString
				.stringToList2(RecordAppFileHandlerHelper
						.getFileAllContent("BrowserAppActivity"));// 获取需要清除的浏览器包名

		if (Browserlist != null)
			for (String pack : Browserlist) {
				InstallUtil.clearAppData("pm clear " + pack);// 清除浏览器缓存等数据
			}

		MyFileUtil.deleteFolder(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Hdj008k/UnInstall");// 删除APK

		List<MyFileInfo> list = new ArrayList<MyFileInfo>();

		String path = preferences.getString("delFolder", null);// 需要删除的文件夹路径
		if (path != null) {

			File file = new File(path);
			list = InstallUtil.checkFolder(ctx, file);

			MyFileUtil.deleteFolder(path);// 删除文件夹

		}

		HdjDbService hdjDbService = new HdjDbService(ctx);
		List<AdStatus> adStatusList = hdjDbService.getScrollData();
		for (int i = 0; i < adStatusList.size(); i++) {
			AdStatus adStatus = adStatusList.get(i);
			String packageNA = adStatus.getPackageName();
			String filePath = adStatus.getAdpath();
			if (!TextUtils.isEmpty(packageNA) && !TextUtils.isEmpty(filePath)) {
				MyFileInfo fileInfo = new MyFileInfo();
				fileInfo.setFileName(packageNA);
				fileInfo.setFilePath(filePath);
				list.add(fileInfo);
			}

		}

		InstallUtil.UninstallAppArray(list);//
		hdjDbService.delete();// 清除安装记录表

		if (packag.length() > 0)
			((ActivityManager) ctx.getSystemService("activity"))
					.killBackgroundProcesses(packag);
	}

	/** 弹出root权限提示框 */
	private void showAlDialog(String str) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
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

	protected void showProgress(String string) {
		// TODO Auto-generated method stub
		mProgress = new ProgressDialog(ctx);
		mProgress.setMessage(string);
		mProgress.setCancelable(false);
		mProgress.show();
	}

	private void stopProgress() {
		// TODO Auto-generated method stub
		if (mProgress != null && mProgress.isShowing())
			mProgress.cancel();
	}

	/**
	 * 保存数据
	 */
	private void saveData() {

		Editor pre = preferences.edit();
		// Log.e("hhl", "保存内容");
		Toast.makeText(getApplication(), "数据修改成功！", Toast.LENGTH_SHORT).show();

		String yys = et_yys.getText().toString().trim();
		pre.putString("yys", yys);// 运营商
		pre.putString("cellLocation", RandomData.randCellLocation(yys));
		pre.putString("imei", et_imei.getText().toString());
		pre.putString("android_id", et_android.getText().toString());
		pre.putString("phone", et_phone.getText().toString());// 手机卡号码
		pre.putString("phone_num", et_phonenum.getText().toString());// 手机卡序列号
		pre.putString("imsi", et_imsi.getText().toString());
		pre.putString("mac", et_mac.getText().toString());// mac地址
		pre.putString("wx", et_wx.getText().toString());// 无线路由器
		pre.putString("wxdz", et_wxdz.getText().toString());// 无线路由器地址
		pre.putString("version", et_bb.getText().toString());// 系统版本
		pre.putString("sdk", et_bbz.getText().toString());// 系统版本值
		pre.putString("brand", et_pp.getText().toString());// 品牌
		pre.putString("xh", et_xh.getText().toString());// 型号
		pre.putString("bluemac", et_ly.getText().toString());// 蓝牙
		pre.putString("ipadress", Ip + "");// 内网ip
		pre.putString("network_type", et_netnum.getText().toString());// 网络类型
		pre.putString("network_operatorName", et_net.getText().toString());// 网络类型名
		pre.putString("simstate", et_simstate.getText().toString());// 手机卡状态
		pre.putString("serial", et_ck.getText().toString());// 串口序列号
		pre.putString("zhiwen", et_zw.getText().toString());// 指纹
		pre.putString("radioVersion", et_gjbb.getText().toString());// 固件版本
		pre.putString("hardware", et_yj.getText().toString());// 硬件
		pre.putString("manufacturer", et_zzs.getText().toString());// 制造商
		pre.putString("product", et_cpm.getText().toString());// 产品名称
		pre.putString("device", et_sbm.getText().toString());// 设备名
		pre.putString("userAgent", et_ua.getText().toString());// userAgent
		pre.putString("lac", et_lac.getText().toString());// lac
		pre.putString("cid", et_cid.getText().toString());// cid
		pre.putString("realSsid", realSsid);// 当前真实的路由器名

		pre.putString("build_id", et_id.getText().toString());// cid
		pre.putString("build_dislay", et_display.getText().toString());// cid
		pre.putString("build_host", et_host.getText().toString());// cid
		
		
		// 经纬度
		pre.putString("lat", et_lat.getText().toString());
		pre.putString("lng", et_lng.getText().toString());

		// 分辨率相关参数
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		int densityDpi = displaymetrics.densityDpi;
		float density = displaymetrics.density;
		float xdpi = displaymetrics.xdpi;
		float ydpi = displaymetrics.ydpi;

		if (preferences.getBoolean("isDensity", false)) {
			densityDpi = RandomData.getDensityDpi(randomSW);
			density = RandomData.getDensity(randomSW);
			xdpi = RandomData.getDpi(densityDpi);
			ydpi = RandomData.getDpi(densityDpi);
		}

		pre.putString("fbl_w", randomSW);// 分辨率-宽
		pre.putString("fbl_h", randomSH);// 分辨率-高
		pre.putString("density", density + "");
		pre.putString("densityDpi", densityDpi + "");
		pre.putString("scaledDensity", density + "");
		pre.putString("xdpi", xdpi + "");
		pre.putString("ydpi", ydpi + "");
		// Log.e("hdj", "传过去的地址:" + et_ip.getText().toString());
		pre.apply();

		// 保存修改的cpu文件信息
		Common.setCpuName(ctx, et_cpu.getText().toString());

		// 保存修改的wifi列表信息
		WifiDataHelper.saveWifiData(mWifiData);

	}

	/** 保存数据到数据库中 */
	protected void saveDateToDB() {
		// TODO Auto-generated method stub
		
		HistoryData data = new HistoryData();
		String imei = et_imei.getText().toString();
		data.setImei(imei);
		data.setAndroidId(et_android.getText().toString());
		data.setPhone(et_phone.getText().toString());
		data.setPhoneNum(et_phonenum.getText().toString());
		data.setImsi(et_imsi.getText().toString());
		data.setYys(et_yys.getText().toString());
		data.setMac(et_mac.getText().toString());
		data.setWx(et_wx.getText().toString());
		data.setWxdz(et_wxdz.getText().toString());
		data.setVersion(et_bb.getText().toString());
		data.setSdk(et_bbz.getText().toString());
		data.setBrand(et_pp.getText().toString());
		data.setXh(et_xh.getText().toString());
		data.setBluemac(et_ly.getText().toString());
		data.setIpadress(Ip + "");
		data.setNetworkType(et_netnum.getText().toString());
		data.setNetworkOperatorName(et_net.getText().toString());
		data.setSimstate(et_simstate.getText().toString());
		data.setSerial(et_ck.getText().toString());
		data.setZhiwen(et_zw.getText().toString());
		data.setRadioVersion(et_gjbb.getText().toString());
		data.setHardware(et_yj.getText().toString());
		data.setManufacturer(et_zzs.getText().toString());
		data.setProduct(et_cpm.getText().toString());
		data.setDevice(et_sbm.getText().toString());
		data.setLat(et_lat.getText().toString());
		data.setLng(et_lng.getText().toString());
		data.setFbl_w(randomSW);
		data.setFbl_h(randomSH);
		data.setUserAgent(et_ua.getText().toString());
		data.setLac(et_lac.getText().toString());
		data.setCellid(et_cid.getText().toString());
		data.setCpu(et_cpu.getText().toString());
		
		data.setId(et_id.getText().toString());
		data.setDisplay(et_display.getText().toString());
		data.setHost(et_host.getText().toString());
		
		long time = System.currentTimeMillis();
		int now = Integer.parseInt(getDateString(time));// 今天
		data.setSaveTime(now);

		int max = preferences.getInt("liucunMax", 1000);// 允许数据库中有多少条数据
		max = max > 0 ? max : 1000;
		HistoryDBManager dbManager = new HistoryDBManager(this);
		int maxData = dbManager.getSize(DBOpenHelper.TABNAME);// 留存表中有多少条数据
		
		if (maxData < max) {
			dbManager.insert(data);// 没达到上限就插入数据
		} else {
			dbManager.update(data);// 达到上限就更新数据
		}
		
		if (preferences.getBoolean("openLiucun", false)) {
			
			int old = preferences.getInt("liucunDay", 0);
			if (now != old) {
				preferences.edit().putInt("liucunDay", now).commit();
				boolean b = dbManager.delete(DBOpenHelper.RUNTABNAME);
				Log.e("", "");
			}
			if(isRunLiucun)
				dbManager.insertRun(imei);// 已跑次日留存
		}
		stopProgress();
		canSaveData = true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null)
			return;
		if (requestCode == 2) {// SelectAppActivity返回的应用包名和应用名

			String appN = data.getStringExtra("appName");
			String packageN = data.getStringExtra("packageName");

			SharedPreferences sh = ctx.getSharedPreferences("prefs",
					Context.MODE_WORLD_READABLE);
			Editor pre = sh.edit();

			if (packageN != null && appN != null) {

				btn_model1.setText(appN + "_" + packageN);

				pre.putString("appName", appN);
				pre.putString("packageName", packageN);
				Log.e("MainActivity----onActivityResult", "packageN = "
						+ packageN);
				pre.commit();

				((ActivityManager) getSystemService("activity"))
						.killBackgroundProcesses(packageN);

			}
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// menu.setHeaderTitle("选择模式");
		// menu.add(0, 0, 0, modName[0]);
		// menu.add(0, 1, 0, modName[1]);
		// menu.add(0, 2, 0, "单个应用");
		// menu.add(0, 3, 0, modName[3]);
		// menu.add(0, 4, 0, modName[4]);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuitem) {
		SharedPreferences sh = ctx.getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);
		// Editor pre = sh.edit();
		//
		// int i = menuitem.getItemId();
		// if (i == 0) {
		// btn_model1.setText(modName[0]);
		// pre.putString("modle", "0");
		// pre.putString("appName", null);
		// pre.putString("packageName", null);
		// pre.commit();
		// } else {
		// if (i == 1) {
		// pre.putString("modle", "1");
		// btn_model1.setText(modName[1]);
		// pre.commit();
		// return true;
		// }
		// if (i == 2) {
		// Intent intent = new Intent();
		// intent.setClass(this, SelectAppActivity.class);
		// startActivityForResult(intent, 2);
		// return true;
		// }
		// if (i == 3) {
		// pre.putString("modle", "3");
		// btn_model1.setText(modName[3]);
		// pre.putString("appName", null);
		// pre.putString("packageName", null);
		// pre.commit();
		// return true;
		// }
		// if (i == 4) {
		// Intent intent = new Intent();
		// intent.setClass(this, SelectAppActivity.class);
		// startActivityForResult(intent, 2);
		// return true;
		// }
		// }
		return true;
	}

	// 获取当前日期
	private String getDateString(long time) {
		Date date = new Date(time);
		String str = new SimpleDateFormat("yyyy-MM-dd").format(date);
		str = str.replace("-", "");
		return str;
	}

	private void setEditText(EditText editText, String s) {
		editText.setText(s);
	}

	// 刷新TextView
	private void refreshView() {
		// TODO Auto-generated method stub

		if (preferences.getString("xpFolder", "").equals("")) {
			mLayout.setVisibility(View.GONE);
		} else {
			mLayout.setVisibility(View.VISIBLE);
		}

		// 静默安装
		if (!preferences.getBoolean("openInstall", false)) {
			tv_install.setText("静默安装已关闭");
			textview1.setVisibility(View.INVISIBLE);
		} else {
			tv_install.setText("静默安装已开启");
			textview1.setVisibility(View.VISIBLE);

			int time1 = preferences.getInt("WaitTiem", 15);
			if (time1 > 59) {
				int seconds = time1 % 60;
				int minute = (time1 - seconds) / 60;
				textview1.setText("间隔时间:" + minute + "分  " + seconds + "秒");
			} else {
				textview1.setText("间隔时间:" + time1 + "秒");
			}
		}

		// 静默卸载
		if (!preferences.getBoolean("openUnInstall", false)) {
			tv_uninstall.setText("静默卸载已关闭");
			textview2.setVisibility(View.INVISIBLE);
		} else {
			tv_uninstall.setText("静默卸载已开启");
			textview2.setVisibility(View.VISIBLE);

			int time2 = preferences.getInt("UninstallTime", 5);
			if (time2 > 59) {
				int seconds = time2 % 60;
				int minute = (time2 - seconds) / 60;
				textview2.setText("卸载时间:" + minute + "分  " + seconds + "秒");
			} else {
				textview2.setText("卸载时间:" + time2 + "秒");
			}
		}

		if (!preferences.getBoolean("openAutomatic", false))
			textview3.setText("关闭");
		else
			textview3.setText("开启");

		if (!preferences.getBoolean("browser", false))
			tv_browser.setText("清除浏览器已关闭");
		else
			tv_browser.setText("清除浏览器已开启");

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
