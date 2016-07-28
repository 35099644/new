package com.example.hdj008k_location.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.example.hdj008k_location.obj.WifiData;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class WifiDataHelper {

	public static String finalFolder = "Hdj008k";

	/**
	 * 保存周围wifi信息
	 * */
	public static void saveWifiData(List<WifiData> mWifiData) {
		
		if(mWifiData == null){
			Log.e("FileHookHelper--saveWifiData", "mWifiData为null");
			return;
		}
		
		String ExternalStorage = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		try {

			File file1 = new File(ExternalStorage + "/Hdj008k/.db/wifiData");
			if (!file1.exists()) {
				
				File file2 = file1.getParentFile();
				if (!file2.exists()){
					File file3 = file2.getParentFile();
					if(!file3.exists())
						file3.mkdirs();
				}

				file1.createNewFile();

			}
			
			String dataString = "";
			for (int i = 0; i < mWifiData.size(); i++) {

				WifiData wifiData = mWifiData.get(i);
				dataString =dataString +"\n"+wifiData.getSsid() + "_"
						+ wifiData.getBssid() + "_" + wifiData.getLevel();

			}

			FileOutputStream fileoutputstream = new FileOutputStream(file1);
			fileoutputstream.write(dataString.getBytes("UTF-8"));
			fileoutputstream.flush();
			fileoutputstream.close();
			// Log.e("FileHookHelper--saveDataToFile", "数据保存完毕");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取周围wifi信息
	 * */
	public static List<WifiData> getWifiData() {

		String ExternalStorage = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		List<WifiData> list = new ArrayList<WifiData>();
		try {
			File localFile1 = new File(ExternalStorage
					+ "/Hdj008k/.db/wifiData");
			if (!localFile1.exists())
				return list;
			try {
				BufferedReader bufferedReader = new BufferedReader(
						new FileReader(localFile1));

				String str1 = "";
				while ((str1 = bufferedReader.readLine()) != null) {

					String[] str = str1.split("_");
					if (str != null && str.length == 3) {

						WifiData wifiData = new WifiData();
						wifiData.setSsid(str[0]);
						wifiData.setBssid(str[1]);
						wifiData.setLevel(Integer.valueOf(str[2]));
						list.add(wifiData);

					}
				}

				bufferedReader.close();

			} catch (IOException e) {
				e.printStackTrace();
				Log.e("getFileAllContent---", e.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Log.e("RecordAppFileHandlerHelper---getFileAllContent---",
		// "ttt-s1 = "+s1);
		return list;

	}

}
