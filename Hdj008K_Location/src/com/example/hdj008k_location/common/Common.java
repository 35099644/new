package com.example.hdj008k_location.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.hdj008k_location.util.RecordAppFileHandlerHelper;
import com.example.hdj008k_location.util.SetSystemValueHelper;

/**
 * ������
 * 
 * @ClassName: Common
 */
public class Common {

	/** �Ƿ���sd�� */
	public static boolean haveSDcard() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/** ��ȡ�ֻ�imei */
	public static String getImei(Context context) {

		String imei = null; // ��ȡ�ֻ���did

		try {

			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			// ��ȡ�ֻ�imeiҪ����Ҫ��manifest�м���
			// <uses-permission
			// android:name="android.permission.READ_PHONE_STATE"/>
			if (tm.getDeviceId() != null) {
				imei = tm.getDeviceId();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return imei;
	}

	/** ��ȡӦ�������� */
	public static String getUmengChannelId(Context context) {

		String channelId = null;
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			Object value = ai.metaData.get("UMENG_CHANNEL");

			if (value != null) {
				String str = value.toString();
				int index1 = str.indexOf("hdj");

				// ���indexOf�������ش��ڵ���0��ֵ�����ʾ����
				// ������-1���ʾ�ַ����в�������
				if (index1 != -1) {// ����

					String[] temp = null;
					temp = str.split("hdj");
					channelId = temp[1];

				} else {// ������

					channelId = str;

				}

			}
		} catch (Exception e) {
		}
		return channelId;
	}

	/** ��ȡip */
	public static String getIP(Context context) {
		String ip = "";

		try {

			WifiManager wifi_service = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifi_service.getConnectionInfo();
			// ����ip
			if (wifiInfo.getIpAddress() != 0) {
				ip = intToIp(wifiInfo.getIpAddress());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return ip;
	}

	/** ��ȡip */
	public static int getIpAddress(Context context) {
		int ip = 0;

		try {

			WifiManager wifi_service = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifi_service.getConnectionInfo();
			// ����ip
			ip = wifiInfo.getIpAddress();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return ip;
	}

	/** ��ȡ�ֻ����� */
	public static String getTel(Context context) {
		String sim = "";

		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			if (tm.getLine1Number() != null) {

				sim = tm.getLine1Number();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return sim;
	}

	/** ��ȡ�ֻ�����ʶ */
	public static String getSim(Context context) {
		String sim = "";

		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm.getSubscriberId() != null) {

				sim = tm.getSubscriberId();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return sim;
	}

	/** ��ȡ�ֻ�aid */
	public static String getAid(Context context) {
		String aid = "";

		try {
			aid = Settings.Secure.getString(context.getContentResolver(),
					Settings.Secure.ANDROID_ID);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return aid;
	}

	/** ��ȡMac��ַ */
	public static String getMac(Context context) {
		String mac = "";

		try {
			WifiManager wifi_service = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifi_service.getConnectionInfo();

			if (wifiInfo.getMacAddress() != null) {

				mac = wifiInfo.getMacAddress();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return mac;
	}

	/** ��ȡ�ֻ������� */
	public static String getDB(Context context) {
		String db = "";

		String db_ = Build.MANUFACTURER.toLowerCase();

		if (db_ != null) {

			db = db_;
		}

		return db;
	}

	/** ��ȡ�豸�ͺ� */
	public static String getDN(Context context) {
		String dn = "";

		// �豸�ͺ�
		String dn_ = android.os.Build.MODEL;

		if (dn_ != null) {

			dn = dn_;
		}

		return dn;
	}

	/** ��ȡ����ϵͳ�汾 */
	public static String getOV(Context context) {
		String ov = "";

		String ov_ = new String(Build.VERSION.RELEASE);

		if (ov_ != null) {

			ov = ov_.replaceAll(" ", "");
		}

		return ov;
	}

	public static float getSWDis(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float width = dm.widthPixels;
		float height = dm.heightPixels;
		return width;
	}

	public static float getSHDis(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float width = dm.widthPixels;
		float height = dm.heightPixels;
		return height;
	}

	/** ��ȡ�ֻ�����Ļ�� */
	public static int getSW(Context context) {
		// ��ȡ�ֻ�����Ļ��,��
		int sw = 0;

		try {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);

			// ����Ļ������
			if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				sw = wm.getDefaultDisplay().getWidth();// ��Ļ���

			} else {

				sw = wm.getDefaultDisplay().getHeight();

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return sw;
	}

	/** ��ȡ�ֻ�����Ļ�� */
	public static int getSH(Context context) {
		int sh = 0;

		try {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);

			// ����Ļ������
			if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				sh = wm.getDefaultDisplay().getHeight();// ��Ļ�߶�

			} else {

				sh = wm.getDefaultDisplay().getWidth();

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return sh;
	}

	/** ��ȡ��վ��Ϣcid */
	public static String getCid(Context context) {
		int ci = 0;
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			int type = tm.getNetworkType();
			if (type == TelephonyManager.PHONE_TYPE_GSM // GSM��
					|| type == TelephonyManager.NETWORK_TYPE_EDGE
					|| type == TelephonyManager.NETWORK_TYPE_HSDPA) {

				GsmCellLocation gsm = ((GsmCellLocation) tm.getCellLocation());

				if (gsm != null) {
					// ȡ��SIM�������̴���,�ж���Ӫ�����й��ƶ�\�й���ͨ\�й�����
					// �ҹ�Ϊ460���й��ƶ�Ϊ00���й���ͨΪ01,�й�����Ϊ03
					// ��վID
					ci = gsm.getCid();

				} else {

				}
			} else if (type == TelephonyManager.NETWORK_TYPE_CDMA // ����cdma��
					|| type == TelephonyManager.NETWORK_TYPE_1xRTT
					|| type == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| type == TelephonyManager.NETWORK_TYPE_EVDO_A) {

				CdmaCellLocation cdma = (CdmaCellLocation) tm.getCellLocation();
				if (cdma != null) {

					ci = cdma.getBaseStationId();
					// ��Ӫ��1
					String mcc = tm.getNetworkOperator().substring(0, 3);
					// ��Ӫ��2
					String mnc = String.valueOf(cdma.getSystemId());

				} else {

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return ci + "";
	}

	/** ��ȡ��վ��Ϣcr */
	public static String getCr(Context context) {
		String cr = "";

		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			int type = tm.getNetworkType();
			if (type == TelephonyManager.PHONE_TYPE_GSM // GSM��
					|| type == TelephonyManager.NETWORK_TYPE_EDGE
					|| type == TelephonyManager.NETWORK_TYPE_HSDPA) {

				GsmCellLocation gsm = ((GsmCellLocation) tm.getCellLocation());

				if (gsm != null) {
					// ȡ��SIM�������̴���,�ж���Ӫ�����й��ƶ�\�й���ͨ\�й�����
					// �ҹ�Ϊ460���й��ƶ�Ϊ00���й���ͨΪ01,�й�����Ϊ03
					// ��վID
					cr = tm.getSimOperator();

				} else {

				}
			} else if (type == TelephonyManager.NETWORK_TYPE_CDMA // ����cdma��
					|| type == TelephonyManager.NETWORK_TYPE_1xRTT
					|| type == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| type == TelephonyManager.NETWORK_TYPE_EVDO_A) {

				CdmaCellLocation cdma = (CdmaCellLocation) tm.getCellLocation();
				if (cdma != null) {

					// ��Ӫ��1
					String mcc = tm.getNetworkOperator().substring(0, 3);
					// ��Ӫ��2
					String mnc = String.valueOf(cdma.getSystemId());
					cr = mcc + mnc;

				} else {

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return cr;
	}

	/** ��ȡγ����Ϣlat */
	public static double getLat(Context context) {
		double lat = 0.0;

		try {
			String serviceName = "location";
			Location location = getLocation(context);
			LocationManager locationManager = (LocationManager) context
					.getSystemService(serviceName);

			// ȡ���ֻ��û����ھ�γ��
			if (location != null) {
				// γ��
				lat = location.getLatitude();
			} else {
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null) {
					// γ��
					lat = location.getLatitude();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return lat;
	}

	/** ��ȡ������Ϣlng */
	public static double getLng(Context context) {
		double lng = 0.0;

		try {
			String serviceName = "location";
			Location location = getLocation(context);
			LocationManager locationManager = (LocationManager) context
					.getSystemService(serviceName);

			// ȡ���ֻ��û����ھ�γ��
			if (location != null) {
				// ����
				lng = location.getLongitude();
			} else {
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null) {
					// ����
					lng = location.getLongitude();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return lng;
	}

	/** ��ȡ����(·����)������(SSID) */
	public static String getSSID(Context context) {
		String ssid = "";

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		try {
			ssid = wifiInfo.getSSID();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return ssid;
	}

	/** ��ȡ�ź�ǿ��(Rssi) */
	public static int getRssi(Context context) {
		int rssi = 8;

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		try {
			rssi = wifiInfo.getRssi();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return rssi;
	}

	/** ��ȡ����(·����)��mac��ַ(BSSID) */
	public static String getBSSID(Context context) {
		String bssid = "";

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		try {
			bssid = wifiInfo.getBSSID();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bssid;
	}

	/** �ж��Ƿ���wifi����� */
	public static int iswifi(Context context) {
		// �ж��Ƿ���wifi�����
		int isw = 0;

		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);

			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null) {

				if ("WIFI".equalsIgnoreCase(ni.getTypeName())) {
					isw = 1;

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return isw;
	}

	/** ��ȡ��ǰλ�� */
	private static Location getLocation(Context context) {

		Location mlocation = null;

		try {
			// GPSγ�ȣ�����
			String serviceName = "location";
			// A LocationManager for controlling location (e.g., GPS) updates.
			LocationManager locationManager = (LocationManager) context
					.getSystemService(serviceName);
			// newһ����׼
			Criteria criteria = new Criteria();
			// ����Ϊ��󾫶� Criteria.ACCURACY_FINE=1
			criteria.setAccuracy(1);
			// ��Ҫ�󺣰���Ϣ
			criteria.setAltitudeRequired(false);
			// ��Ҫ��λ��Ϣ
			criteria.setBearingRequired(false);
			// �Ƿ�������
			criteria.setCostAllowed(true);
			// �Ե�����Ҫ�� Criteria.POWER_LOW=1
			criteria.setPowerRequirement(1);

			String provider = locationManager.getBestProvider(criteria, true);

			if ((provider != null) && (provider.length() > 0)) {

				mlocation = locationManager.getLastKnownLocation(provider);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return mlocation;
	}

	/** ��ȡ�ֻ�cpu�� */
	public static String getCpuName(Context context) {

		String s = null;
		FileReader filereader = null;
		BufferedReader bufferedreader = null;

		try {

			filereader = new FileReader("/proc/cpuinfo");
			bufferedreader = new BufferedReader(filereader);

			String s1 = null;
			String s2 = "";

			while ((s1 = bufferedreader.readLine()) != null)
				s2 = s2 + s1 + "\n";

			if (s2.indexOf("Hardware\t:") >= 0) {
				int i = s2.indexOf("Hardware\t:") + "Hardware\t:".length();
				int j = s2.indexOf("\n", i);// ��i������ʼ������\n
				if (j >= 0) {
					s = s2.substring(i, j).trim();
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (filereader != null)
					filereader.close();
				if (bufferedreader != null)
					bufferedreader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return s;
	}

	/**
	 * �����޸ĵ�cpu������cpuinfo�ļ�
	 * 
	 * @param paramString
	 *            �޸ĵĲ���
	 * */
	public static void setCpuName(Context context, String paramString) {
		String str = RecordAppFileHandlerHelper.getFileAllContent("cpuinfo");
		int i = str.indexOf("Hardware\t:");
		if (i < 0) {
			copyCpuinfo(context);
			str = RecordAppFileHandlerHelper.getFileAllContent("cpuinfo");
			i = str.indexOf("Hardware\t:");
		}

		if (i < 0) {
			Toast.makeText(context, "����cpu�ļ�ʧ��", 1000).show();
			return;
		}

		i = i + "Hardware\t:".length();
		int j = str.indexOf("\n", i);
		if (j >= 0)
			str = str.replace(str.substring(i, j).trim(), paramString.trim());
		SetSystemValueHelper.saveDataToFile("cpuinfo", str);
	}

	/** ��assets�е�cpuinfo�ļ�����sd���� */
	public static void copyCpuinfo(Context context) {
		// TODO Auto-generated method stub

		if (!haveSDcard())
			return;

		String str = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Hdj008k";

		try {
			File file = new File(str);
			if (!file.exists())
				file.mkdir();

			File file1 = new File(file, "cpuinfo");
			if (file1.exists()) {
				file1.delete();
			}

			InputStream source = context.getAssets().open("cpuinfo");
			OutputStream destination = new FileOutputStream(file1);
			byte[] buffer = new byte[1024];
			int n;

			while ((n = source.read(buffer)) != -1) {
				if (n == 0) {
					n = source.read();
					if (n < 0)
						break;
					destination.write(n);
					continue;
				}
				destination.write(buffer, 0, n);
			}
			source.close();
			destination.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String intToIp(int i) {
		return (i & 0xFF) + "." +

		((i >> 8) & 0xFF) + "." +

		((i >> 16) & 0xFF) + "." +

		(i >> 24 & 0xFF);

	}
}
