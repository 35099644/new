package com.example.hdj008k_location.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Random;

import android.os.Environment;
import android.util.Log;

public class MyFileUtil {

	/**����д������*/
	public static boolean fileCoverWrite(File file, String data) {
		boolean b = false;
		
		if (!file.exists()) {
			Log.e("MyFileUtil--fileCoverWrite", file.getName()+"�ļ�������");
			return b;
		}

		try {

			FileOutputStream fileoutputstream = new FileOutputStream(file);
			fileoutputstream.write(data.getBytes("UTF-8"));
			fileoutputstream.flush();
			fileoutputstream.close();
			return true;
		} catch (IOException ioexception) {
			// Log.e("saveDataToFile", "ttt-ioexception = " + ioexception);
		}
		return b;
	}
	
	/**׷��д������*/
	public static boolean fileAdditionalWrite(File file, String data) {
		boolean b = false;
		
		if (!file.exists()) {
			Log.e("MyFileUtil--fileAdditionalWrite", file.getName()+"�ļ�������");
			return b;
		}

		try {

			RandomAccessFile localRandomAccessFile = new RandomAccessFile(
					file, "rw");
			localRandomAccessFile.seek(localRandomAccessFile.length());
			localRandomAccessFile.write(("\n" + data).getBytes("UTF-8"));
			localRandomAccessFile.close();
			return true;
		} catch (IOException ioexception) {
			// Log.e("saveDataToFile", "ttt-ioexception = " + ioexception);
		}
		return b;
	}

	/** ɾ�������ļ��м��ļ����������ļ� */
	public static void delete(File file) {
		if (file.isFile()) {
			if (file.getName().indexOf("aaa.txt") == -1
					&& file.getName().indexOf("MobileAnJian") == -1)
				file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				if (file.isFile())
					if (file.getName().indexOf("aaa.txt") == -1
							&& file.getName().indexOf("MobileAnJian") == -1)
						file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			if (file.getName().indexOf("aaa.txt") == -1
					&& file.getName().indexOf("MobileAnJian") == -1)
				file.delete();
		}
	}

	/** ɾ���ļ����������ļ� */
	public static void deleteFolder(String path) {

		if (path == null || path.length() == 0)
			return;

		File file = new File(path);
		if (!file.exists())
			return;
		else
			delete(file);

		File file2 = new File(path);
		if (!file2.exists())
			file2.mkdir();

	}

	/**
	 * �����ļ�
	 * 
	 * @param fromPath
	 *            Դ�ļ�·��
	 * @param toPath
	 *            Ŀ���ļ�·��
	 * 
	 * @return Դ�ļ������ڡ�Ŀ���ļ����ڻ���ʧ��,����false;���򷵻�true.
	 **/
	public static boolean copyFile(String fromPath, String toPath) {

		File oldFile = new File(fromPath);
		File newFile = new File(toPath);

		return copyFile(oldFile, newFile);
	}

	/**
	 * �����ļ�
	 * 
	 * @param fromPath
	 *            Դ�ļ�
	 * @param toPath
	 *            Ŀ���ļ�
	 * 
	 * @return Դ�ļ������ڡ�Ŀ���ļ����ڻ���ʧ��,����false;���򷵻�true.
	 **/
	public static boolean copyFile(File fromFile, File toFile) {
		boolean b = false;

		if (!fromFile.exists() || toFile.exists())
			return b;

		try {

			InputStream source = new FileInputStream(fromFile);
			OutputStream destination = new FileOutputStream(toFile);
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

			b = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return b;
	}

	/** �޸�����apk���ֲ��ƶ���ָ���ļ����� */
	public static String changeName(String path) {
		String str = null;

		try {

			File oldFile = new File(path);
			if (!oldFile.exists())
				oldFile.createNewFile();
			else
				Log.e("changeName------", "ԭ·��path = " + path);

			if (!StringUtil.isChinese(oldFile.getName()))
				return oldFile.getAbsolutePath();

			File file1 = new File(Environment.getExternalStorageDirectory(),
					"Hdj008k");
			if (!file1.exists())
				file1.mkdir();

			File file2 = new File(file1, "UnInstall");
			if (!file2.exists())
				file2.mkdir();

			while (true) {

				File newFile = new File(file2, randomFileName());
				if (!newFile.exists()) {
					// if(!oldFile.renameTo(newFile))//�ƶ��ļ�
					// Log.e("changeName------", "������ʧ��");

					if (!copyFile(oldFile, newFile))
						Log.e("changeName------", "������ʧ��");

					return str = newFile.getAbsolutePath();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str;
	}

	/**
	 * ���apk��
	 * */
	private static String randomFileName() {
		String name = "";
		Random random = new Random();
		for (int i = 0; i < 7; i++) {
			name = name + random.nextInt(10);
		}
		return name + ".apk";

	}

}
