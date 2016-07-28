package com.example.hdj008k_location.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * 自定义工具类
 **/
public class SetSystemValueHelper {

	public static String finalFolder = "Hdj008k";

	public static JSONArray jsonArray;


	/**
	 * 获取文件中的数据
	 * 
	 * @param fileName
	 *            文件名
	 * @return
	 **/
	public static String getFileData(String fileName) {
		File file = new File(Environment.getExternalStorageDirectory(),
				finalFolder);
		if (!file.exists())
			file.mkdirs();
		File file1 = new File(file,fileName);
		boolean bool = file1.exists();
		try {
			if (!file1.exists())
				file1.createNewFile();
		} catch (IOException ioexception) {
			return "";
		}
		long size = file1.length();
		String s1 = "";
		if (size != 0L)
			try {
				FileInputStream fileinputstream = new FileInputStream(file1);
				byte abyte0[] = new byte[(int) size];
				s1 = new String(abyte0, 0, fileinputstream.read(abyte0),
						"UTF-8");
			} catch (Exception exception) {
				s1 = "";
			}

//		Log.e("SetSystemValueHelper--getFileData", "ttt-s1 = " + s1);
		return s1.trim();
	}

	/**
	 * 添加监听到的系统值操作
	 * 
	 * @param systemkey
	 *            系统值对应的key
	 * @param statu
	 *            读写的状态
	 * @param systemvalue
	 *            系统值
	 * 
	 **/
	public static void addItem(Object systemkey, String statu,
			Object systemvalue) {

		String data = getFileData("SetSystemValueActivity");
//		Log.e("SetSystemValueHelper--addItem", "ttt-data = " + data);

		try {
			if (jsonArray == null)
				jsonArray = JSON.toArray(getFileData("SetSystemValueActivity"));

			if (jsonArray == null) {
				// Log.e("SetSystemValueHelper--addItem",
				// "ttt-jsonArray = null");
				return;
			}

			JSONObject jsonObject = null;
			int length = jsonArray.length();

			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject obj = jsonArray.getJSONObject(i);
				if ((obj.getString("systemkey")).equals((String) systemkey)) {
					// Log.e("SetSystemValueHelper--addItem", "ttt-key存在");
					jsonObject = obj;
				}
			}
			// Log.e("SetSystemValueHelper--addItem",
			// "length1 = "+jsonArray.length());

			if (jsonObject == null) {
				jsonObject = new JSONObject();
				jsonArray.put(length, jsonObject);
			}

			// Log.e("SetSystemValueHelper--addItem",
			// "length2 = "+jsonArray.length());
			jsonObject.put("systemkey", systemkey);
			String str = jsonObject.optString("statu");
			boolean bool = jsonObject.optBoolean("check");

			if (str == null) {
				str = statu;
			} else if (str.indexOf(statu) < 0) {
				str = str + statu;
			}
			// Log.e("SetSystemValueHelper--addItem", "ttt-statu="+statu);
			jsonObject.put("systemvalue", systemvalue);
			jsonObject.put("statu", str);
			jsonObject.put("check", bool);
			// Log.e("SetSystemValueHelper--addItem",
			// "jsonObject = "+jsonObject.toString());
			// Log.e("SetSystemValueHelper--addItem",
			// "jsonArray = "+jsonArray.toString());
			saveDataToFile("SetSystemValueActivity", JSON.toString(jsonArray));

		} catch (Exception ex) {
			Log.e("SetSystemValueHelper--addItem",
					"ttt-Exception = " + ex.getMessage());
		}
		//
		// try {
		// if (jsonArray == null)
		// jsonArray = JSON.toArray(getFileData("SetSystemValueActivity"));
		//
		// if (jsonArray == null){
		// Log.e("SetSystemValueHelper--addItem", "ttt-jsonArray = null");
		// return;
		// }
		//
		// if(jsonArray.length()>0){
		//
		// for (int i = 0; i < jsonArray.length(); i++) {
		//
		// JSONObject obj = jsonArray.getJSONObject(i);
		//
		// String key = obj.optString("systemkey");
		// String str = obj.optString("statu");
		//
		// if (key.length() == 0) {// key不存在就保存数据
		//
		// Log.e("SetSystemValueHelper--addItem", "ttt-key不存在");
		//
		// obj.put("systemkey", systemkey);
		// obj.put("systemvalue", systemvalue);
		// obj.put("statu", statu);
		// obj.put("check", false);
		//
		// } else if (key.equals((String) systemkey)) {// key存在继续下一步判断
		//
		// Log.e("SetSystemValueHelper--addItem", "ttt-key存在");
		//
		// if (str.indexOf(statu) < 0)
		// str = str + statu;
		//
		// obj.put("statu", str);
		// obj.put("systemvalue", systemvalue);
		//
		// }
		// }
		// }else{
		//
		//
		//
		// }
		//
		// saveDataToFile("SetSystemValueActivity", JSON.toString(jsonArray));
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// Log.e("SetSystemValueHelper--addItem",
		// "ttt-Exception = "+e.getMessage());
		// }
	}

	/**
	 * 保存数据到指定文件中
	 * 
	 * @param fileName
	 *            文件名
	 * @param data
	 *            保存的数据
	 * @return 文件路径
	 **/
	public static String saveDataToFile(String fileName, String data) {
		File file = new File(Environment.getExternalStorageDirectory(),
				finalFolder);
		if (!file.exists())
			file.mkdirs();
		File file1 = new File(Environment.getExternalStorageDirectory(),
				finalFolder + File.separator + fileName);
		String str = file1.getAbsolutePath();
//		Log.e("saveDataToFile", "ttt-file1Path = " + str);
		try {
			if (!file1.exists()) {
//				Log.e("saveDataToFile", "ttt-file1不存在");
				file1.createNewFile();
			}

			// Log.e("saveDataToFile",
			// "ttt-fileName="+fileName+"  -data="+data);
			FileOutputStream fileoutputstream = new FileOutputStream(file1);
			fileoutputstream.write(data.getBytes("UTF-8"));
			fileoutputstream.flush();
			fileoutputstream.close();
		} catch (IOException ioexception) {
//			Log.e("saveDataToFile", "ttt-ioexception = " + ioexception);
			return "";
		}

		return str;

	}

	/**
	 * 清空设置的系统值
	 * 
	 * @param context
	 * @param checkAll
	 *            true是清空所有，否则为false
	 * */
	public static void deleteSelect(final Context context, boolean checkAll) {
		try {
			int num = 0;
			JSONArray array = JSON.toArray(getFileData("SetSystemValueActivity"));
//			Log.e("deleteSelect---", "ttt-array = " + array.toString());
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {

					JSONObject obj = array.getJSONObject(i);
					String key = obj.optString("systemkey");
					boolean check = obj.optBoolean("check");

//					Log.e("deleteSelect---", "ttt-obj = " + obj.toString());

					if (check || checkAll) {
						num++;

//						Log.e("deleteSelect---", "ttt-check = " + check
//								+ "  checkAll=" + checkAll);

						Settings.System.putString(context.getContentResolver(),
								key, null);
						String s = Settings.System.getString(context.getContentResolver(),
								key);
						
//						Log.e("deleteSelect---", "ttt-s = " + s);
						
					}
				}

				Toast.makeText(context, "共清除" + num + "条记录", 0).show();
				saveDataToFile("SetSystemValueActivity", "");
			}

		} catch (Exception localException) {
			
		}
	}
}
