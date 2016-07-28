package com.example.hdj008k_location.db;

import java.util.ArrayList;
import java.util.List;

import com.example.hdj008k_location.obj.AdStatus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HdjDbService {
	private DBOpenHelper dbOpenHelper;

	public HdjDbService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
	}

	public void save(AdStatus adStatus) {
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into hdj (adpath,status,packageName,create_time) values(?,?,?,?)",
				new Object[] { adStatus.getAdpath(), adStatus.getStatus(),
						adStatus.getPackageName(), adStatus.getTime() });
		// Log.e("=========================", "数据保存成功！");
	}

	public void update(AdStatus adStatus) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update hdj set status=?,create_time=? where adpath=?",
				new Object[] { adStatus.getStatus(), adStatus.getTime(),
						adStatus.getAdpath() });
	}

	public void delete() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("DELETE FROM hdj");
	}

	public AdStatus find(String adpath) {
		// 如果只对数据进行读取，建议使用此方法
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("select * from hdj where adpath=?",
					new String[] { adpath });
			if (cursor.moveToFirst()) {
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				int status = cursor.getInt(cursor.getColumnIndex("status"));
				long time = cursor
						.getLong(cursor.getColumnIndex("create_time"));
				String packageName = cursor.getString(cursor
						.getColumnIndex("packageName"));
				AdStatus adStatus = new AdStatus();
				adStatus.setId(id);
				adStatus.setAdpath(adpath);
				adStatus.setStatus(status);
				adStatus.setPackageName(packageName);
				adStatus.setTime(time);
				return adStatus;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public List<AdStatus> getScrollData() {
		List<AdStatus> adStatus = new ArrayList<AdStatus>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

		Cursor cursor = null;

		try {
			cursor = db.query("hdj", null, null, null, null, null, null);
			if (cursor != null) {

				while (cursor.moveToNext()) {
					AdStatus aStatu = new AdStatus();
					int id = cursor.getInt(cursor.getColumnIndex("_id"));
					int status = cursor.getInt(cursor.getColumnIndex("status"));
					String adpath = cursor.getString(cursor
							.getColumnIndex("adpath"));
					String packageName = cursor.getString(cursor
							.getColumnIndex("packageName"));
					long time = cursor.getLong(cursor
							.getColumnIndex("create_time"));
					aStatu.setId(id);
					aStatu.setAdpath(adpath);
					aStatu.setStatus(status);
					aStatu.setPackageName(packageName);
					aStatu.setTime(time);

					adStatus.add(aStatu);
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return adStatus;
	}

}
