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

	private String mPackageN;// ѡ���Ӧ�ð���
	private String randomDB;// ����ֻ�Ʒ��
	private String randomImei;// ���imei
	private String randomDN;// ����ͺ�
	private String randomOV;// ���ϵͳ�汾
	private String randomSH;// ����ֱ��ʸ�
	private String randomSW;// ����ֱ��ʿ�
	private String randomZW;// ���ָ��
	private String randomCPU;// ���cpu
	private String randomSB;// ����豸��
	private String randomCP;// �����Ʒ��
	private String randomZZS;// ���������
	private String randomYJ;// ���Ӳ��
	private String randomGJBB;// ����̼��汾
	private String randomUA;// ���UserAgent
	private String realSsid;// ��ǰ��ʵ��·������

	private int Ip;// ����ip
	private boolean isRunLiucun;// �Ƿ�������
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
				deleteData();// ��ռ�¼������
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
								+ ": ��װ�ɹ�---" + f.getFilePath());

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
								+ ": ��");
					}

				} else if (xpType == 2) {
					Log.e("XpressThread--run--2", "Thread start");
					if (InstallUtil.clientUninstall(f.getFileName()))
						Log.e("InstallUtil--UninstallAppArray", f.getFileName()
								+ ": ж�سɹ�");
				}

				try {
					Thread.sleep(time * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (xpType == 2)
				MyFileUtil.deleteFolder(path);// ɾ��APK
			stopProgress();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		setContentView(R.layout.activity_main);

		init();
		initView();

		// �ж�Ӧ����û��rootȨ��
		if (!InstallUtil.haveRoot("chmod 777 " + getPackageCodePath()))
			showAlDialog("û��rootȨ��,��ȷ���Ѿ���ȷ��root!");

		// if(!Common.haveSDcard())
		// showAlDialog("û���ҵ�sd��,��ȷ���ֻ�װ��sd��!");

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
	// String str = getDateString(time);// ����
	// String str2 = getDateString(time2);// ǰ��
	//
	// HistoryDBManager dbManager = new HistoryDBManager(ctx);
	// dbManager.onCreateTable("hdj" + str);// �����±�
	// dbManager.deleteTab("hdj" + str2);// ��ǰ��ı�ɾ��
	// }

	private void init() {
		ctx = this;
		MobclickAgent.setDebugMode(true);
		// CreateDB();
		startService(new Intent(ctx, InstallApkService.class));// ��������
		preferences = this.getSharedPreferences("prefs",
				Context.MODE_WORLD_READABLE);

		// dataArray = dataToJSONArray(getImeiToFile("Hdj008_imei"));
		dataArray = RandomData.dataToJSONArray(RandomData.getImeiToFile(ctx,
				"Hdj008_imei2"));
		JiZhanDBManager dbm = new JiZhanDBManager(this);
		dbm.openDatabase();
		dbm.close();
	}

	/** ��ʼ���ؼ� */
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
		
		btn_random1.setOnClickListener(new OnClickListener() {// �����ť

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

		btn_bc1.setOnClickListener(new OnClickListener() {// ���水ť

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (!canSaveData)
					return;

				canSaveData = false;
				saveData();// �����������
				saveDateToDB();// �������ݵ����ݿ���

				String packag = preferences.getString("packageName", "");
				SetSystemValueHelper.deleteSelect(ctx, true);// ���ϵͳֵ
				RecordAppFileHandler.deleteDate(ctx, packag);// ����ļ�

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
				// arg0.showContextMenu();//�����˵�
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
				showProgress("���ڴ�����");
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
				showProgress("���ڴ�����");
				new Thread(xpRunnable).start();
			}
		});

		// registerForContextMenu(btn_model1);
		startData();
	}

	private void startData() {

		// ��ȡ�ֻ�imei

		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);

		WifiManager wifi_service = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);

		WifiInfo wifiInfo = wifi_service.getConnectionInfo();

		String imei = tm.getDeviceId();
		setEditText(et_imei, imei);// �ֻ�imei

		String ai = Settings.Secure.getString(ctx.getContentResolver(),
				Settings.Secure.ANDROID_ID);
		setEditText(et_android, ai);// �ֻ�ANDROID_ID

		String phonenumber = tm.getLine1Number();
		setEditText(et_phone, phonenumber);// �ֻ�������

		String sim = tm.getSimSerialNumber();
		setEditText(et_phonenum, sim);// �ֻ������к�

		String imsi = tm.getSubscriberId();
		setEditText(et_imsi, imsi);// �ֻ�imsi

		String yys = tm.getSimOperator();
		setEditText(et_yys, yys);// ��Ӫ��

		String net = tm.getNetworkOperatorName();
		setEditText(et_net, net);// ������Ӫ����
		
		String net_num = tm.getNetworkType() + "";
		setEditText(et_netnum, net_num);// ��������

		String mac = wifiInfo.getMacAddress();
		setEditText(et_mac, mac);// mac��ַ

		realSsid = wifiInfo.getSSID();
		setEditText(et_wx, realSsid);// ����·������

		String wxdz = wifiInfo.getBSSID();
		setEditText(et_wxdz, wxdz);// ����·������ַ

		// setEditText(et_lac, null);// ��վlac
		// setEditText(et_cid, null);// ��վcid

		String xlh = android.os.Build.SERIAL;
		setEditText(et_ck, xlh);// �������к�

		String bb = android.os.Build.VERSION.RELEASE;
		setEditText(et_bb, bb);// ϵͳ�汾

		String bbz = android.os.Build.VERSION.SDK;
		setEditText(et_bbz, bbz);// ϵͳ�汾ֵ

		String pp = android.os.Build.BRAND;
		setEditText(et_pp, pp);// �ֻ�Ʒ��

		String xh = android.os.Build.MODEL;
		setEditText(et_xh, xh);// �ֻ��ͺ�

		String cpm = android.os.Build.PRODUCT;
		setEditText(et_cpm, cpm);// ��Ʒ����

		String zzs = android.os.Build.MANUFACTURER;
		setEditText(et_zzs, zzs);// ������

		String sb = android.os.Build.DEVICE;
		setEditText(et_sbm, sb);// �豸��

		String fingerprint = android.os.Build.FINGERPRINT;
		setEditText(et_zw, fingerprint);// ָ��

		String hardware = android.os.Build.HARDWARE;
		setEditText(et_yj, hardware);// Ӳ��

		String gj = android.os.Build.getRadioVersion();
		setEditText(et_gjbb, gj);// �̼��汾

		String cpu = Common.getCpuName(ctx);
		setEditText(et_yj, cpu);// cpu
		
		String id = android.os.Build.ID;
		setEditText(et_id, id);
		
		String display = android.os.Build.DISPLAY;
		setEditText(et_display, display);
		
		String host = android.os.Build.HOST;
		setEditText(et_host, host);

		String ip = RandomData.intToIp(wifiInfo.getIpAddress());
		setEditText(et_ip, ip);// ����IP

		int state = tm.getSimState();
		setEditText(et_simstate, state + "");// �ֻ���״̬

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
				+ displaymetrics.heightPixels);// ��Ļ�ֱ���

		String appN = preferences.getString("appName", "");
		mPackageN = preferences.getString("packageName", "");

		if (appN.length() > 0 && mPackageN.length() > 0) {
			btn_model1.setText(appN + "_" + mPackageN);
			return;
		}

		btn_model1.setText("����Ӧ��");

	}

	// activity�رյ�ʱ��رչ㲥
	public void unRegisterNotify() {

		if (receiver != null)
			this.unregisterReceiver(receiver);
		receiver = null;

	}

	/**
	 * ����������
	 * */
	protected void liucunData() {
		// TODO Auto-generated method stub

		int i = preferences.getInt("Liucun", 20);// ����������
		if (i <= 0) {// û���������ʾ��������
			randomData();
			Log.e("MainActivity--liucunData()", "������Ϊ0");
			return;
		}

		HistoryDBManager dbManager = new HistoryDBManager(ctx);
		long time = System.currentTimeMillis();
		String now = getDateString(time);// ����
		int maxData = dbManager.getSize(DBOpenHelper.TABNAME,now);// ����֮ǰ������ 
		if(maxData>0)
			maxData = dbManager.getSize(DBOpenHelper.TABNAME);//�������ݿ��ж�������
		int liucun = maxData * i / 100;// ��Ҫ�ܶ�����������
		int run = dbManager.getSize(DBOpenHelper.RUNTABNAME);// ���˶�������������

		if (maxData <= 0) {
			randomData();
			Log.e("MainActivity--liucunData()", "�������û������");
			return;
		}

		if (run < liucun) {

			// û����������ʹ����ݿ��л�ȡ����
			Random random = new Random();
			int id = random.nextInt(maxData);// �������id
			HistoryData historyData = dbManager.getData(id + "");// ����id��ȡ����
			
			if(historyData!=null){
				showData(historyData);
				Log.e("MainActivity--liucunData()", "������");
			}else
				randomData();

		} else {
			randomData();
			Log.e("MainActivity--liucunData()", "��������������");
		}
	}

	/**
	 * �������
	 * */
	private void randomData() {
		// Log.e("MainActivity----------randomData()", "���");
		isRunLiucun = false;

		if (isRandomData)
			return;
		if (!randomArray2()) {
			Toast.makeText(ctx, "�������ʧ��", 1000).show();
			return;
		}
		setEditText(et_pp, randomDB);// ���Ʒ��
		setEditText(et_imei, RandomData.randomImei(randomImei));
		setEditText(et_android, RandomData.getRandomCharAndNumr(16));

		String[] a = RandomData.getSimData(randomImei);
		if (a.length == 5) {
			setEditText(et_imsi, a[0]);// imsi
			setEditText(et_yys, a[1]);// ��Ӫ��
			setEditText(et_net, a[2]);// ����������
			setEditText(et_phonenum, a[3]);// �ֻ����к�
			setEditText(et_netnum, a[4]);// ��������
		}

		String wx = RandomData.random16(10);
		String wxdz = RandomData.randomMac();
		setEditText(et_wxdz, wxdz);// ·������ַ
		setEditText(et_wx, wx);// ·������

		mWifiData = RandomData.randomWifiData(this, wx, wxdz);

		JiZhan jizhan = randomJiZhan();
		setEditText(et_lac, jizhan.getLac() + "");// ��վlac
		setEditText(et_cid, jizhan.getCellId() + "");// ��վcid
		setEditText(et_lat, RandomData.randomLat(jizhan.getLat()));
		setEditText(et_lng, RandomData.randomLng(jizhan.getLng()));

		setEditText(et_simstate, "5");// �ֻ���״̬
		setEditText(et_phone, RandomData.randomPhone(a[0]));// �ֻ�����
		setEditText(et_mac, RandomData.randomMac());
		setEditText(et_ck, RandomData.random16(8));// �������к�
		setEditText(et_bb, randomOV);
		setEditText(et_bbz, RandomData.randomBBZ(randomOV));// sdk
		setEditText(et_zw, randomZW);// ָ��
		setEditText(et_gjbb, randomGJBB);// �̼��汾
		setEditText(et_yj, randomYJ);// Ӳ��
		setEditText(et_cpm, randomCP);// ��Ʒ����
		setEditText(et_zzs, randomZZS);// ������
		setEditText(et_sbm, randomSB);// �豸��
		setEditText(et_cpu, randomCPU);// cpu
		setEditText(et_id, RandomData.randomHexadecimalString(12));// id
		setEditText(et_display, RandomData.randomHexadecimalString(12));// display
		setEditText(et_host, RandomData.randomHexadecimalString(12));// host
		setEditText(et_ua, randomUA);// UserAgent
		setEditText(et_xh, randomDN);// ����
		setEditText(et_ly, RandomData.randomMac());// ����
		// ����IP
		Ip = RandomData.randomIP(ctx);
		setEditText(et_ip, RandomData.intToIp(Ip));
		setEditText(et_fbl, randomSW + "x" + randomSH);

	}

	/** ��ʾ�������� */
	private void showData(HistoryData data) {
		// TODO Auto-generated method stub
		Log.e("MainActivity----------showData()", "������");
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
		
		setEditText(et_pp, brand);// ���Ʒ��

		setEditText(et_imei, imei);
		setEditText(et_android, android_id);
		setEditText(et_imsi, imsi);// imsi
		setEditText(et_yys, yys);// ��Ӫ��
		setEditText(et_net, network_operatorName);// ����������
		setEditText(et_phonenum, phone_num);// �ֻ������к�
		setEditText(et_netnum, network_type);// ��������
		setEditText(et_simstate, simstate);// �ֻ���״̬
		setEditText(et_phone, phone);// �ֻ�����
		setEditText(et_mac, mac);
		setEditText(et_wx, wx);// ·������
		setEditText(et_ck, serial);// �������к�
		setEditText(et_wxdz, wxdz);
		setEditText(et_bb, version);
		setEditText(et_bbz, sdk);
		setEditText(et_zw, zhiwen);// ָ��
		setEditText(et_gjbb, radioVersion);// �̼��汾
		setEditText(et_yj, hardware);// Ӳ��
		setEditText(et_cpm, product);// ��Ʒ����
		setEditText(et_zzs, manufacturer);// ������
		setEditText(et_sbm, device);// �豸��
		setEditText(et_cpu, cpu);// cpu
		setEditText(et_ua, ua);// UserAgent
		setEditText(et_xh, xh);
		// ����
		setEditText(et_ly, bluemac);
		// ����IP
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
	// * �������
	// * */
	// private void randomData() {
	//
	// if (!randomArray())
	// return;
	//
	// setEditText(et_pp, randomDB);// ���Ʒ��
	//
	// setEditText(et_imei, randomImei(randomImei));
	// setEditText(et_android, getRandomCharAndNumr(16));
	//
	// String[] a = getSimData();
	// if (a.length == 5) {
	// setEditText(et_imsi, a[0]);// imsi
	// setEditText(et_yys, a[1]);// ��Ӫ��
	// setEditText(et_net, a[2]);// ����������
	// setEditText(et_phonenum, a[3]);// �ֻ����к�
	// setEditText(et_netnum, a[4]);// ��������
	// }
	//
	// setEditText(et_simstate, "5");// �ֻ���״̬
	// setEditText(et_phone, randomPhone(a[0]));// �ֻ�����
	//
	// setEditText(et_mac, randomMac());
	// setEditText(et_wx, random16(10));//·������
	// setEditText(et_ck, random16(8));//�������к�
	//
	// setEditText(et_wxdz, randomMac());
	// setEditText(et_bb, randomOV);
	// setEditText(et_bbz, randomBBZ(randomOV));
	//
	// setEditText(et_cpm, randomDB + " " + randomDN);// ��Ʒ����
	// setEditText(et_zzs, randomDB);// ������
	// setEditText(et_sbm, randomDN);// �豸��
	//
	// setEditText(et_xh, randomDN);
	//
	// // ����
	// setEditText(et_ly, randomMac());
	//
	// // ����IP
	// setEditText(et_ip, randomIP() + "");
	//
	// setEditText(et_fbl, randomSW + "x" + randomSH);
	//
	// setEditText(et_lat, randomLat());
	// setEditText(et_lng, randomLng());
	//
	// }

	// /** ��Hdj008_imei�����ݻ�ȡ���� */
	// private boolean randomArray() {
	//
	// // String head[] = new String[dataArray.length()];
	// try {
	// if (dataArray == null) {
	// Toast.makeText(ctx, "�������ʧ�ܣ���ȡ����ʧ��!", Toast.LENGTH_SHORT)
	// .show();
	// return false;
	// }
	// if (dataArray.length() == 0) {
	// Toast.makeText(ctx, "Hdj008_imei����Ϊ��", Toast.LENGTH_SHORT)
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
	// Toast.makeText(ctx, "imei������Ϊ��", Toast.LENGTH_SHORT).show();
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

	/** ��Hdj008_imei2�������ȡimei���ֻ����� */
	private boolean randomArray2() {

		isRandomData = true;

		// String head[] = new String[dataArray.length()];
		try {
			if (dataArray == null) {
				Toast.makeText(ctx, "�������ʧ�ܣ���ȡ����ʧ��!", Toast.LENGTH_SHORT)
						.show();
				return false;
			}
			if (dataArray.length() == 0) {
				Toast.makeText(ctx, "Hdj008_imei2����Ϊ��", Toast.LENGTH_SHORT)
						.show();
				return false;
			}

			Random rnd = new Random();
			int n = rnd.nextInt(dataArray.length());
			JSONObject obj = dataArray.getJSONObject(n);

			randomDB = obj.getString("BRAND");// �ֻ�Ʒ��
			randomImei = obj.getString("getDeviceId");
			randomDN = obj.getString("MODEL");// ����
			randomOV = obj.getString("RELEASE");// ϵͳ�汾
			randomZW = obj.getString("FINGERPRINT");// ���ָ��
			randomCPU = obj.getString("setCpuName");// ���cpu
			randomSB = obj.getString("DEVICE");// ����豸��
			randomCP = obj.getString("PRODUCT");// �����Ʒ��
			randomZZS = obj.getString("MANUFACTURER");// ���������
			randomYJ = obj.getString("HARDWARE");// ���Ӳ��
			randomGJBB = obj.getString("getRadioVersion");// ����̼��汾

			String hw = obj.getString("getMetrics");
			String[] s = hw.split("x");
			randomSW = s[0];// ��
			randomSH = s[1];// ��

			String[] s2 = randomZW.split("/");
			randomUA = RandomData.randomUA(randomOV, randomDN, s2[3]);// ���UserAgent

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

	/** ��Lac_cell���ݿ��������ȡ��վ���� */
	private JiZhan randomJiZhan() {

		JiZhanDBManager dbm = new JiZhanDBManager(this);
		dbm.openDatabase();
		JiZhan jizhan = dbm.randomJiZhanData();
		dbm.close();
		return jizhan;
	}

	/**
	 * ɾ����������
	 * */
	protected void deleteData() {
		// TODO Auto-generated method stub

		String packag = preferences.getString("packageName", "");

		RecordAppFileHandlerHelper.clearFileData("InstallApk.txt");// ���apk���ؼ�¼�ļ�
		RecordAppFileHandlerHelper.clearFileData("InstallApkPackageName.txt");// ���apk���ؼ�¼�ļ�
		InstallUtil.clearAppData("pm clear " + packag);// ���Ӧ�û��������

		List<String> Browserlist = ListToString
				.stringToList2(RecordAppFileHandlerHelper
						.getFileAllContent("BrowserAppActivity"));// ��ȡ��Ҫ��������������

		if (Browserlist != null)
			for (String pack : Browserlist) {
				InstallUtil.clearAppData("pm clear " + pack);// �����������������
			}

		MyFileUtil.deleteFolder(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Hdj008k/UnInstall");// ɾ��APK

		List<MyFileInfo> list = new ArrayList<MyFileInfo>();

		String path = preferences.getString("delFolder", null);// ��Ҫɾ�����ļ���·��
		if (path != null) {

			File file = new File(path);
			list = InstallUtil.checkFolder(ctx, file);

			MyFileUtil.deleteFolder(path);// ɾ���ļ���

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
		hdjDbService.delete();// �����װ��¼��

		if (packag.length() > 0)
			((ActivityManager) ctx.getSystemService("activity"))
					.killBackgroundProcesses(packag);
	}

	/** ����rootȨ����ʾ�� */
	private void showAlDialog(String str) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(str);
		builder.setNegativeButton("֪����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		builder.setCancelable(false); // ���ð�ť�Ƿ���԰����ؼ�ȡ��,false�򲻿���ȡ��
		AlertDialog dialog = builder.create(); // �����Ի���
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
	 * ��������
	 */
	private void saveData() {

		Editor pre = preferences.edit();
		// Log.e("hhl", "��������");
		Toast.makeText(getApplication(), "�����޸ĳɹ���", Toast.LENGTH_SHORT).show();

		String yys = et_yys.getText().toString().trim();
		pre.putString("yys", yys);// ��Ӫ��
		pre.putString("cellLocation", RandomData.randCellLocation(yys));
		pre.putString("imei", et_imei.getText().toString());
		pre.putString("android_id", et_android.getText().toString());
		pre.putString("phone", et_phone.getText().toString());// �ֻ�������
		pre.putString("phone_num", et_phonenum.getText().toString());// �ֻ������к�
		pre.putString("imsi", et_imsi.getText().toString());
		pre.putString("mac", et_mac.getText().toString());// mac��ַ
		pre.putString("wx", et_wx.getText().toString());// ����·����
		pre.putString("wxdz", et_wxdz.getText().toString());// ����·������ַ
		pre.putString("version", et_bb.getText().toString());// ϵͳ�汾
		pre.putString("sdk", et_bbz.getText().toString());// ϵͳ�汾ֵ
		pre.putString("brand", et_pp.getText().toString());// Ʒ��
		pre.putString("xh", et_xh.getText().toString());// �ͺ�
		pre.putString("bluemac", et_ly.getText().toString());// ����
		pre.putString("ipadress", Ip + "");// ����ip
		pre.putString("network_type", et_netnum.getText().toString());// ��������
		pre.putString("network_operatorName", et_net.getText().toString());// ����������
		pre.putString("simstate", et_simstate.getText().toString());// �ֻ���״̬
		pre.putString("serial", et_ck.getText().toString());// �������к�
		pre.putString("zhiwen", et_zw.getText().toString());// ָ��
		pre.putString("radioVersion", et_gjbb.getText().toString());// �̼��汾
		pre.putString("hardware", et_yj.getText().toString());// Ӳ��
		pre.putString("manufacturer", et_zzs.getText().toString());// ������
		pre.putString("product", et_cpm.getText().toString());// ��Ʒ����
		pre.putString("device", et_sbm.getText().toString());// �豸��
		pre.putString("userAgent", et_ua.getText().toString());// userAgent
		pre.putString("lac", et_lac.getText().toString());// lac
		pre.putString("cid", et_cid.getText().toString());// cid
		pre.putString("realSsid", realSsid);// ��ǰ��ʵ��·������

		pre.putString("build_id", et_id.getText().toString());// cid
		pre.putString("build_dislay", et_display.getText().toString());// cid
		pre.putString("build_host", et_host.getText().toString());// cid
		
		
		// ��γ��
		pre.putString("lat", et_lat.getText().toString());
		pre.putString("lng", et_lng.getText().toString());

		// �ֱ�����ز���
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

		pre.putString("fbl_w", randomSW);// �ֱ���-��
		pre.putString("fbl_h", randomSH);// �ֱ���-��
		pre.putString("density", density + "");
		pre.putString("densityDpi", densityDpi + "");
		pre.putString("scaledDensity", density + "");
		pre.putString("xdpi", xdpi + "");
		pre.putString("ydpi", ydpi + "");
		// Log.e("hdj", "����ȥ�ĵ�ַ:" + et_ip.getText().toString());
		pre.apply();

		// �����޸ĵ�cpu�ļ���Ϣ
		Common.setCpuName(ctx, et_cpu.getText().toString());

		// �����޸ĵ�wifi�б���Ϣ
		WifiDataHelper.saveWifiData(mWifiData);

	}

	/** �������ݵ����ݿ��� */
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
		int now = Integer.parseInt(getDateString(time));// ����
		data.setSaveTime(now);

		int max = preferences.getInt("liucunMax", 1000);// �������ݿ����ж���������
		max = max > 0 ? max : 1000;
		HistoryDBManager dbManager = new HistoryDBManager(this);
		int maxData = dbManager.getSize(DBOpenHelper.TABNAME);// ��������ж���������
		
		if (maxData < max) {
			dbManager.insert(data);// û�ﵽ���޾Ͳ�������
		} else {
			dbManager.update(data);// �ﵽ���޾͸�������
		}
		
		if (preferences.getBoolean("openLiucun", false)) {
			
			int old = preferences.getInt("liucunDay", 0);
			if (now != old) {
				preferences.edit().putInt("liucunDay", now).commit();
				boolean b = dbManager.delete(DBOpenHelper.RUNTABNAME);
				Log.e("", "");
			}
			if(isRunLiucun)
				dbManager.insertRun(imei);// ���ܴ�������
		}
		stopProgress();
		canSaveData = true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null)
			return;
		if (requestCode == 2) {// SelectAppActivity���ص�Ӧ�ð�����Ӧ����

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
		// menu.setHeaderTitle("ѡ��ģʽ");
		// menu.add(0, 0, 0, modName[0]);
		// menu.add(0, 1, 0, modName[1]);
		// menu.add(0, 2, 0, "����Ӧ��");
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

	// ��ȡ��ǰ����
	private String getDateString(long time) {
		Date date = new Date(time);
		String str = new SimpleDateFormat("yyyy-MM-dd").format(date);
		str = str.replace("-", "");
		return str;
	}

	private void setEditText(EditText editText, String s) {
		editText.setText(s);
	}

	// ˢ��TextView
	private void refreshView() {
		// TODO Auto-generated method stub

		if (preferences.getString("xpFolder", "").equals("")) {
			mLayout.setVisibility(View.GONE);
		} else {
			mLayout.setVisibility(View.VISIBLE);
		}

		// ��Ĭ��װ
		if (!preferences.getBoolean("openInstall", false)) {
			tv_install.setText("��Ĭ��װ�ѹر�");
			textview1.setVisibility(View.INVISIBLE);
		} else {
			tv_install.setText("��Ĭ��װ�ѿ���");
			textview1.setVisibility(View.VISIBLE);

			int time1 = preferences.getInt("WaitTiem", 15);
			if (time1 > 59) {
				int seconds = time1 % 60;
				int minute = (time1 - seconds) / 60;
				textview1.setText("���ʱ��:" + minute + "��  " + seconds + "��");
			} else {
				textview1.setText("���ʱ��:" + time1 + "��");
			}
		}

		// ��Ĭж��
		if (!preferences.getBoolean("openUnInstall", false)) {
			tv_uninstall.setText("��Ĭж���ѹر�");
			textview2.setVisibility(View.INVISIBLE);
		} else {
			tv_uninstall.setText("��Ĭж���ѿ���");
			textview2.setVisibility(View.VISIBLE);

			int time2 = preferences.getInt("UninstallTime", 5);
			if (time2 > 59) {
				int seconds = time2 % 60;
				int minute = (time2 - seconds) / 60;
				textview2.setText("ж��ʱ��:" + minute + "��  " + seconds + "��");
			} else {
				textview2.setText("ж��ʱ��:" + time2 + "��");
			}
		}

		if (!preferences.getBoolean("openAutomatic", false))
			textview3.setText("�ر�");
		else
			textview3.setText("����");

		if (!preferences.getBoolean("browser", false))
			tv_browser.setText("���������ѹر�");
		else
			tv_browser.setText("���������ѿ���");

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
