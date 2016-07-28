package com.example.hdj008k_location.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import com.example.hdj008k_location.obj.AdStatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

/**
 * 自定义工具类
 **/
public class RecordAppFileHandlerHelper {

	public static HashMap<String, String> fileMap;

	public static String finalFolder = "Hdj008k";

	public static String ExternalStorage = "";

	/**
	 * 从指定文件中获取所有数据
	 * 
	 * @param fileName
	 *            文件名
	 * @return 返回字符串
	 * */
	public static String getFileAllContent(String fileName) {
		if (ExternalStorage.length() == 0)
			ExternalStorage = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		String s1 = "";
		try {
			File localFile1 = new File(ExternalStorage, finalFolder);
			if (!localFile1.exists())
				localFile1.mkdirs();
			File localFile2 = new File(ExternalStorage, finalFolder
					+ File.separator + fileName);
			try {
				if (!localFile2.exists())
					localFile2.createNewFile();
				BufferedReader bufferedReader = new BufferedReader(
						new FileReader(localFile2));

				String str1 = "";
				while ((str1 = bufferedReader.readLine()) != null) {

					s1 = s1 + str1 + "\n";
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
		return s1;
	}

	/**
	 * 从指定文件中获取数据(不超过204800)
	 * 
	 * @param fileName
	 *            文件名
	 * @return 返回字符串
	 * */
	public static String getFileContent(String fileName) {

		if (ExternalStorage.length() == 0)
			ExternalStorage = Environment.getExternalStorageDirectory()
					.getAbsolutePath();

		String str = "";
		try {
			File localFile1 = new File(ExternalStorage, finalFolder);
			if (!localFile1.exists())
				localFile1.mkdirs();
			File localFile2 = new File(ExternalStorage, finalFolder
					+ File.separator + fileName);
			try {
				if (!localFile2.exists())
					localFile2.createNewFile();
				RandomAccessFile localRandomAccessFile = new RandomAccessFile(
						localFile2, "rw");
				localRandomAccessFile.seek(0L);
				byte[] arrayOfByte = new byte[204800];
				int i = localRandomAccessFile.read(arrayOfByte);
				localRandomAccessFile.close();
				str = new String(arrayOfByte, 0, i);

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		return str.trim();
	}

	/**
	 * 保存数据到指定文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param dataString
	 *            需要保存的数据
	 * 
	 * @return 数据不存在并保存成功返回true，否则返回false
	 * 
	 * */
	public static boolean saveDataToFile(final String fileName,
			final String dataString) {

		// Log.e("FileHookHelper--saveDataToFile", "fileName = " + fileName
		// + "  dataString = " + dataString);

		if (ExternalStorage.length() == 0)
			ExternalStorage = Environment.getExternalStorageDirectory()
					.getAbsolutePath();

		String[] split = null;

		// Log.e("FileHookHelper--saveDataToFile", "fileMap------");

		try {

			File file1 = new File(ExternalStorage + File.separator
					+ finalFolder);
			if (!file1.exists())
				file1.mkdirs();

			File file2 = new File(ExternalStorage, finalFolder + File.separator
					+ fileName);

			try {

				if (!file2.exists())
					file2.createNewFile();

				RandomAccessFile localRandomAccessFile = new RandomAccessFile(
						file2, "rw");
				localRandomAccessFile.seek(localRandomAccessFile.length());
				localRandomAccessFile.write(("\n" + dataString).getBytes());
				localRandomAccessFile.close();
				fileMap.put(dataString.trim(), dataString.trim());

				// Log.e("FileHookHelper--saveDataToFile", "数据保存完毕");

				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		// Log.e("FileHookHelper--saveDataToFile", "数据已存在2");
		return false;

	}

	/**
	 * 保存数据到指定文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param dataString
	 *            需要保存的数据
	 * 
	 * @return 数据不存在并保存成功返回true，否则返回false
	 * 
	 * */
	public static boolean saveDataToFile2(final String fileName,
			final String dataString) {

		// Log.e("FileHookHelper--saveDataToFile", "fileName = " + fileName
		// + "  dataString = " + dataString);

		if (ExternalStorage.length() == 0)
			ExternalStorage = Environment.getExternalStorageDirectory()
					.getAbsolutePath();

		String[] split = null;

		if (fileMap == null || fileMap.get(dataString.trim()) == null) {

			// Log.e("FileHookHelper--saveDataToFile", "fileMap------");

			try {

				File file1 = new File(ExternalStorage + File.separator
						+ finalFolder);
				if (!file1.exists())
					file1.mkdirs();

				File file2 = new File(ExternalStorage, finalFolder
						+ File.separator + fileName);

				try {
					if (!file2.exists()) {
						file2.createNewFile();
					}
					if (fileMap == null) {

						// Log.e("FileHookHelper--saveDataToFile",
						// "fileMap == null");

						fileMap = new HashMap<String, String>();
						split = getFileAllContent(fileName).split("\n");

						for (int i = 0; i < split.length; i++) {

							if (split[i].length() > 0)
								fileMap.put(split[i], split[i]);

						}

						// Log.e("FileHookHelper--saveDataToFile",
						// "fileMap.size = " + fileMap.size());
					}

					if (fileMap.get(dataString.trim()) != null) {// 判断数据是否已经存在

						// Log.e("FileHookHelper--saveDataToFile", "数据已存在1");

						return false;
					}

					// if(dataString.indexOf(".apk")>0||dataString.indexOf(".APK")>0)//apk另外再保存
					saveApkPath("InstallApk.txt", dataString);

					RandomAccessFile localRandomAccessFile = new RandomAccessFile(
							file2, "rw");
					localRandomAccessFile.seek(localRandomAccessFile.length());
					localRandomAccessFile.write(("\n" + dataString).getBytes());
					localRandomAccessFile.close();
					fileMap.put(dataString.trim(), dataString.trim());

					// Log.e("FileHookHelper--saveDataToFile", "数据保存完毕");

					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		// Log.e("FileHookHelper--saveDataToFile", "数据已存在2");
		return false;

	}

	/**
	 * 保存apk地址到指定文件
	 * 
	 * @param dataString
	 *            需要保存的数据
	 * */
	public static void saveApkPath(final String fileName,
			final String dataString) {

		if (ExternalStorage.length() == 0)
			ExternalStorage = Environment.getExternalStorageDirectory()
					.getAbsolutePath();

		try {

			File file1 = new File(ExternalStorage + File.separator
					+ finalFolder);
			File file2 = new File(ExternalStorage, finalFolder + File.separator
					+ fileName);

			if (!file1.exists())
				file1.mkdirs();

			if (!file2.exists())
				file2.createNewFile();

			RandomAccessFile localRandomAccessFile = new RandomAccessFile(
					file2, "rw");
			localRandomAccessFile.seek(localRandomAccessFile.length());

			localRandomAccessFile.write(("\n" + dataString).getBytes());
			localRandomAccessFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * 获取文件大小
	 * 
	 * @param fileName
	 *            文件名
	 * @return long型
	 * */
	public static long getFileSize(String fileName) {
		if (ExternalStorage.length() == 0)
			ExternalStorage = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		long l = 0L;
		try {
			File localFile1 = new File(ExternalStorage, finalFolder);
			if (!localFile1.exists())
				localFile1.mkdirs();
			File localFile2 = new File(ExternalStorage, finalFolder
					+ File.separator + fileName);
			try {
				if (!localFile2.exists())
					localFile2.createNewFile();
				l = localFile2.length();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	/**
	 * 清除数据，删除文件
	 * */
	public static void clearFileData(String paramString) {
		try {
			File localFile1 = new File(
					Environment.getExternalStorageDirectory(), finalFolder);
			if (!localFile1.exists())
				localFile1.mkdirs();
			File localFile2 = new File(
					Environment.getExternalStorageDirectory(), finalFolder
							+ File.separator + paramString);
			
			if (paramString.indexOf("aaa.txt") == -1 && paramString.indexOf("MobileAnJian") == -1)
				if (localFile2.exists())
					localFile2.delete();
			
			return;
		} catch (Exception localException) {

		}
	}
}
