package com.example.hdj008k_location.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DATABASENAME = "hdj.db"; // ���ݿ�����
	private static final int DATABASEVERSION = 2;// ���ݿ�汾

	public static final String TABNAME = "liucun";// �������ݱ���
	public static final String RUNTABNAME = "run";// �����������ݱ���

	public DBOpenHelper(Context context) {
		super(context, DATABASENAME, null, DATABASEVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS hdj (_id integer primary key AUTOINCREMENT, adpath varchar(64), status integer,packageName varchar(64),create_time Long)");// apk·������
		// db.execSQL("CREATE TABLE IF NOT EXISTS liucun (_id integer primary key AUTOINCREMENT, save_day varchar(64), run_liucun integer)");//��������

		/** 2016-02-25�޸� */
		db.execSQL("CREATE TABLE IF NOT EXISTS " + RUNTABNAME
				+ " (_id integer primary key AUTOINCREMENT, imei varchar(64))");// ��������
		/** 2016-02-25�޸� */
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABNAME
				+ " (_id integer primary key AUTOINCREMENT, imei varchar(64), android_id varchar(64), phone varchar(64), "
				+ "phone_num varchar(64), imsi varchar(64), yys varchar(64), mac varchar(64), wx varchar(64), wxdz varchar(64), "
				+ "version varchar(64), sdk varchar(64), brand varchar(64), xh varchar(64), bluemac varchar(64), ipadress varchar(64), "
				+ "network_type varchar(64), network_operatorName varchar(64), simstate varchar(64), serial varchar(64), zhiwen varchar(64), "
				+ "radioVersion varchar(64), hardware varchar(64), manufacturer varchar(64), product varchar(64), device varchar(64), "
				+ "lat varchar(64), lng varchar(64), fbl_w varchar(64), fbl_h varchar(64), cpu varchar(64), useragent varchar(64), lac varchar(64), "
				+ "cellid varchar(64), build_id varchar(64),build_display varchar(64),build_host varchar(64),savetime integer)");// ��������
	}

	// ���±�
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.onDelete(db);
		this.onCreate(db);
	}

	/** ɾ���� */
	public void onDelete(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS hdj");
		db.execSQL("DROP TABLE IF EXISTS " + TABNAME);
		db.execSQL("DROP TABLE IF EXISTS " + RUNTABNAME);
	}

	/**
	 * ɾ��������������
	 * 
	 * @param tabName
	 *            ����
	 * */
	public boolean delete(String tabName) {
		Log.e("SQLite", "----delete----");
		StringBuffer str = new StringBuffer("delete from " + tabName);

		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			db.execSQL(str.toString());
			db.setTransactionSuccessful();
			Log.e("DBOpenHelper---delete", "ɾ�����ݳɹ�");

		} catch (Exception e) {
			Log.e("DBOpenHelper---delete", "ɾ������ʧ��:" + e.getMessage());
			return false;
		} finally {
			db.endTransaction();
		}
		db.close();
		return true;
	}

	/**
	 * ��������
	 * 
	 * @param tabName
	 *            ����
	 * @param values
	 *            ���������
	 * 
	 * */
	public boolean insert(String tabName, ContentValues values) {

		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			db.insert(tabName, null, values);
			db.setTransactionSuccessful();
			Log.e("DBOpenHelper---insert", "�������ݳɹ�");
		} catch (Exception e) {
			Log.e("DBOpenHelper---insert", "��������ʧ��:" + e.getMessage());
			return false;
		} finally {
			db.endTransaction();
		}
		db.close();
		return true;
	}

	/**
	 * ��������
	 * 
	 * @param tabName
	 *            ����
	 * @param clause
	 *            ������� ��:"_ID=?"
	 * @param args
	 *            ������������ ��:{ 3 }
	 * @param values
	 *            ���µ�����
	 * 
	 * */
	public boolean update(String tabName, String clause, String[] args,
			ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {

			db.update(tabName, values, clause, args);
			db.setTransactionSuccessful();
			Log.e("DBOpenHelper---update", "�������ݳɹ�");
		} catch (Exception e) {
			Log.e("DBOpenHelper---update", "��������ʧ��:" + e.getMessage());
			return false;
		} finally {
			db.endTransaction();
		}
		db.close();
		return true;

	}

	/**
	 * ��ѯ�����ж���������
	 * 
	 * @param tabName
	 *            ����
	 * @param clause
	 *            ������� ��:"_ID=?"
	 * @param selectionArgs
	 *            ������������ ��:new String[] { 3 }
	 * */
	public int getSize(String tabName, String clause, String[] selectionArgs) {

		StringBuffer str = new StringBuffer("select count(*) from " + tabName);

		if (!TextUtils.isEmpty(clause)) {
			str.append(" where ");
			str.append(clause);
		}

		Cursor cursor = null;
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			cursor = db.rawQuery(str.toString(), selectionArgs);
			cursor.moveToFirst();
			// ��ȡ�����е�LONG��������
			int num = cursor.getInt(0);
			// Long count = cursor.getLong(0);
			return num;
		} catch (Exception e) {
			Log.e("DatabaseManager--getSize", e.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();

			db.endTransaction();
			db.close();
		}

		return 0;
	}

}
