package com.example.hdj008k_location.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import com.example.hdj008k_location.obj.JiZhan;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class JiZhanDBManager {

	private final int BUFFER_SIZE = 1024;
	private SQLiteDatabase database;
	private Context context;
	private int num;

	public JiZhanDBManager(Context context) {
		this.context = context;
	}

	public void openDatabase() {
		File sdFile = Environment.getExternalStorageDirectory();
		File gpsPath = new File(sdFile.getPath() + "/Hdj008k/.db/Lac_cell.db");

		if (!gpsPath.exists()) {
			try {
				// 创建目录
				File pmsPaht = new File(sdFile.getPath() + "/Hdj008k");
				Log.i("pmsPaht", "pmsPaht: " + pmsPaht.getPath());
				if (!pmsPaht.exists())
					pmsPaht.mkdirs();

				File dbPaht = new File(pmsPaht.getPath() + "/.db");
				Log.i("dbPaht", "dbPaht: " + dbPaht.getPath());
				if (!dbPaht.exists())
					dbPaht.mkdirs();

				AssetManager am = this.context.getAssets();
				InputStream is = am.open("Lac_cell.mp3");

				FileOutputStream fos = new FileOutputStream(gpsPath);

				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.flush();

				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			database = SQLiteDatabase.openOrCreateDatabase(gpsPath, null);
		} catch (SQLiteCantOpenDatabaseException e) {
			// TODO: handle exception
			database = SQLiteDatabase.openOrCreateDatabase(gpsPath, null);
		}
		
		num = getDataLineSize();
	}

	/**
	 * 获取数据表有多少条数据
	 * 
	 * @param tabName
	 *            表名
	 * 
	 * */
	public int getDataLineSize() {
		int num = 0;

		Cursor cursor = null;

		try {

			if (!tabIsExist()) {
				Log.e("JiZhanDBManager--getDataLineSize", "数据库表-Cellid不存在");
				return num;
			}
			

			// 调用查找书库代码并返回数据源
			cursor = database.rawQuery("select count(*) from Cellid",
					null);

			// 游标移到第一条记录准备获取数据
			if (cursor.moveToFirst()) {

				// 获取数据中的LONG类型数据
				num = cursor.getInt(0);
				Long count = cursor.getLong(0);

			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return num;
	}

	@SuppressWarnings("null")
	public JiZhan randomJiZhanData() {

		JiZhan data = new JiZhan();
		if (database == null || num == 0)
			return data;

		Random random = new Random();
		int id = random.nextInt(num);

		Cursor cursor = null;

		try {
			cursor = database.rawQuery("select * from Cellid where _id=?",

			new String[] { id + "" });

			int i = cursor.getCount();
			if (i == 0) {
				Log.e("JiZhanDBManager--randomJiZhanData", "_id:" + id + "不存在");
				return data;
			}

			if (cursor.moveToFirst()) {
				int lac = cursor.getInt(cursor.getColumnIndex("LAC"));
				int cell = cursor.getInt(cursor.getColumnIndex("CELL"));
				float lng = cursor.getFloat(cursor.getColumnIndex("LNG"));
				float lat = cursor.getFloat(cursor.getColumnIndex("LAT"));
				String address = cursor.getString(cursor
						.getColumnIndex("ADDRESS"));

				data.setLac(lac);
				data.setCellId(cell);
				data.setLng(lng);
				data.setLat(lat);

			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return data;
	}

	public void close() {
		if (database != null) {
			this.database.close();
		}
	}

	/** 判断数据库中是否存在tabName表 */
	public boolean tabIsExist() {
		boolean result = false;
		Cursor cursor = null;
		if (database == null)
			return false;
		try {
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='Cellid' ";
			cursor = database.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

}
