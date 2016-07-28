package com.example.hdj008k_location.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.hdj008k_location.obj.HistoryData;

public class HistoryDBManager {

	private DBOpenHelper dbOpenHelper;
	private Context context;

	public HistoryDBManager(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
		this.context = context;
	}
	
	/** 往留存表插入数据 */
	public boolean insert(HistoryData data) {

		if (data == null) {
			Log.e("DatabaseManager--insert", "插入数据失败,data为空");
			return false;
		}

		if (data.getImei() == null) {
			Log.e("DatabaseManager--insert", "插入数据失败,imei为空");
			return false;
		}

		String id = checkData(DBOpenHelper.TABNAME,data.getImei());
		if (id != null) {
			Log.e("DatabaseManager--insert", "插入数据失败,imei已存在");
			return false;
		}

		ContentValues values = new ContentValues();
		values.put("imei", data.getImei());
		values.put("android_id", data.getAndroidId());
		values.put("phone", data.getPhone());
		values.put("phone_num", data.getPhoneNum());
		values.put("imsi", data.getImsi());
		values.put("yys", data.getYys());
		values.put("mac", data.getMac());
		values.put("wx", data.getWx());
		values.put("wxdz", data.getWxdz());
		values.put("version", data.getVersion());
		values.put("sdk", data.getSdk());
		values.put("brand", data.getBrand());
		values.put("xh", data.getXh());
		values.put("bluemac", data.getBluemac());
		values.put("ipadress", data.getIpadress());
		values.put("network_type", data.getNetworkType());
		values.put("network_operatorName", data.getNetworkOperatorName());
		values.put("simstate", data.getSimstate());
		values.put("serial", data.getSerial());
		values.put("zhiwen", data.getZhiwen());
		values.put("radioVersion", data.getRadioVersion());
		values.put("hardware", data.getHardware());
		values.put("manufacturer", data.getManufacturer());
		values.put("product", data.getProduct());
		values.put("device", data.getDevice());
		values.put("lat", data.getLat());
		values.put("lng", data.getLng());
		values.put("fbl_w", data.getFbl_w());
		values.put("fbl_h", data.getFbl_h());
		values.put("cpu", data.getCpu());
		values.put("useragent", data.getUserAgent());
		values.put("lac", data.getLac());
		values.put("cellid", data.getCellid());
		values.put("savetime", data.getSaveTime());
		
		values.put("build_id", data.getId());
		values.put("build_display", data.getDisplay());
		values.put("build_host", data.getHost());

		return dbOpenHelper.insert(DBOpenHelper.TABNAME,values);
	}

	/**
	 * 往留存表更新数据
	 * 
	 * @param data
	 * 
	 * */
	public boolean update(HistoryData data) {

		if (data == null) {
			Log.e("DatabaseManager--update", "更新数据失败,data为空");
			return false;
		}

		if (data.getImei() == null) {
			Log.e("DatabaseManager--update", "更新数据失败,imei为空");
			return false;
		}

		String id = checkData(DBOpenHelper.TABNAME,data.getImei());
		ContentValues values = new ContentValues();
		if (id != null) {

			Log.e("DatabaseManager--update", "imei已存在,只更新savetime");
			values.put("savetime", data.getSaveTime());

		} else {

			Log.e("DatabaseManager--update", "imei不存在,根据id更新数据");
			id = getId();// 根据savetime升序排列获取时间最早的一条数据的主键id
			values.put("imei", data.getImei());
			values.put("android_id", data.getAndroidId());
			values.put("phone", data.getPhone());
			values.put("phone_num", data.getPhoneNum());
			values.put("imsi", data.getImsi());
			values.put("yys", data.getYys());
			values.put("mac", data.getMac());
			values.put("wx", data.getWx());
			values.put("wxdz", data.getWxdz());
			values.put("version", data.getVersion());
			values.put("sdk", data.getSdk());
			values.put("brand", data.getBrand());
			values.put("xh", data.getXh());
			values.put("bluemac", data.getBluemac());
			values.put("ipadress", data.getIpadress());
			values.put("network_type", data.getNetworkType());
			values.put("network_operatorName", data.getNetworkOperatorName());
			values.put("simstate", data.getSimstate());
			values.put("serial", data.getSerial());
			values.put("zhiwen", data.getZhiwen());
			values.put("radioVersion", data.getRadioVersion());
			values.put("hardware", data.getHardware());
			values.put("manufacturer", data.getManufacturer());
			values.put("product", data.getProduct());
			values.put("device", data.getDevice());
			values.put("lat", data.getLat());
			values.put("lng", data.getLng());
			values.put("fbl_w", data.getFbl_w());
			values.put("fbl_h", data.getFbl_h());
			values.put("cpu", data.getCpu());
			values.put("useragent", data.getUserAgent());
			values.put("lac", data.getLac());
			values.put("cellid", data.getCellid());
			values.put("savetime", data.getSaveTime());
			
			values.put("build_id", data.getId());
			values.put("build_display", data.getDisplay());
			values.put("build_host", data.getHost());

		}

		String[] args = { id };
		return dbOpenHelper.update(DBOpenHelper.TABNAME,"_ID=?", args, values);
	}
	
	/**删除表中所有数据
	 *@param tab 表名
	 * */
	public boolean delete(String tab){
		return dbOpenHelper.delete(tab);
	}
	
	/**根据id从留存表获取数据*/
	public HistoryData getData(String id){
		
		String spl = "select * from " + DBOpenHelper.TABNAME
				+ " where _id=?";
		HistoryData data = null;
		Cursor cursor = null;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.beginTransaction();

		try {
			cursor = db.rawQuery(spl, new String[] { id });
			int i = cursor.getCount();
			if (i == 0) {
				Log.e("DatabaseManager--getData", DBOpenHelper.TABNAME + "表--" + "_id:"
						+ id + "不存在");
				return null;
			}

			if (cursor.moveToFirst()) {

				String imei = cursor.getString(cursor.getColumnIndex("imei"));
				String android_id = cursor.getString(cursor
						.getColumnIndex("android_id"));
				String phone = cursor.getString(cursor.getColumnIndex("phone"));
				String phone_num = cursor.getString(cursor
						.getColumnIndex("phone_num"));
				String imsi = cursor.getString(cursor.getColumnIndex("imsi"));
				String yys = cursor.getString(cursor.getColumnIndex("yys"));
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				String wx = cursor.getString(cursor.getColumnIndex("wx"));
				String wxdz = cursor.getString(cursor.getColumnIndex("wxdz"));
				String version = cursor.getString(cursor
						.getColumnIndex("version"));
				String sdk = cursor.getString(cursor.getColumnIndex("sdk"));
				String brand = cursor.getString(cursor.getColumnIndex("brand"));
				String xh = cursor.getString(cursor.getColumnIndex("xh"));
				String bluemac = cursor.getString(cursor
						.getColumnIndex("bluemac"));
				String ipadress = cursor.getString(cursor
						.getColumnIndex("ipadress"));
				String network_type = cursor.getString(cursor
						.getColumnIndex("network_type"));
				String network_operatorName = cursor.getString(cursor
						.getColumnIndex("network_operatorName"));
				String simstate = cursor.getString(cursor
						.getColumnIndex("simstate"));
				String serial = cursor.getString(cursor
						.getColumnIndex("serial"));
				String zhiwen = cursor.getString(cursor
						.getColumnIndex("zhiwen"));
				String radioVersion = cursor.getString(cursor
						.getColumnIndex("radioVersion"));
				String hardware = cursor.getString(cursor
						.getColumnIndex("hardware"));
				String manufacturer = cursor.getString(cursor
						.getColumnIndex("manufacturer"));
				String product = cursor.getString(cursor
						.getColumnIndex("product"));
				String device = cursor.getString(cursor
						.getColumnIndex("device"));
				String lat = cursor.getString(cursor.getColumnIndex("lat"));
				String lng = cursor.getString(cursor.getColumnIndex("lng"));
				String fbl_w = cursor.getString(cursor.getColumnIndex("fbl_w"));
				String fbl_h = cursor.getString(cursor.getColumnIndex("fbl_h"));
				String cpu = cursor.getString(cursor.getColumnIndex("cpu"));
				String userAgent = cursor.getString(cursor
						.getColumnIndex("useragent"));
				String lac = cursor.getString(cursor.getColumnIndex("lac"));
				String cellid = cursor.getString(cursor
						.getColumnIndex("cellid"));
				String build_id = cursor.getString(cursor
						.getColumnIndex("build_id"));
				String build_display = cursor.getString(cursor
						.getColumnIndex("build_display"));
				String build_host = cursor.getString(cursor
						.getColumnIndex("build_host"));
				int saveTime = cursor.getInt(cursor
						.getColumnIndex("savetime"));
				
				data = new HistoryData();

				data.setImei(imei);
				data.setAndroidId(android_id);
				data.setPhone(phone);
				data.setPhoneNum(phone_num);
				data.setImsi(imsi);
				data.setYys(yys);
				data.setMac(mac);
				data.setWx(wx);
				data.setWxdz(wxdz);
				data.setVersion(version);
				data.setSdk(sdk);
				data.setBrand(brand);
				data.setXh(xh);
				data.setBluemac(bluemac);
				data.setIpadress(ipadress);
				data.setNetworkType(network_type);
				data.setNetworkOperatorName(network_operatorName);
				data.setSimstate(simstate);
				data.setSerial(serial);
				data.setZhiwen(zhiwen);
				data.setRadioVersion(radioVersion);
				data.setHardware(hardware);
				data.setManufacturer(manufacturer);
				data.setProduct(product);
				data.setDevice(device);
				data.setLat(lat);
				data.setLng(lng);
				data.setFbl_w(fbl_w);
				data.setFbl_h(fbl_h);
				data.setCpu(cpu);
				data.setUserAgent(userAgent);
				data.setLac(lac);
				data.setCellid(cellid);
				data.setId(build_id);
				data.setDisplay(build_display);
				data.setHost(build_host);
				data.setSaveTime(saveTime);
				Log.e("HistoryDBManager--getDBData", "获取数据成功--_id = " + id);
			}
			
		} catch (Exception e) {
			Log.e("DatabaseManager--getData", e.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();

			db.endTransaction();
			db.close();
		}
		return data;
	}

	/**
	 * 从留存表获取数据的主键id,按savetime升序
	 * */
	public String getId() {
		String id = "";
		String spl = "select _id from " + DBOpenHelper.TABNAME
				+ " order by savetime";
		Cursor cursor = null;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.beginTransaction();

		try {
			cursor = db.rawQuery(spl, null);
			cursor.moveToFirst();
			id = cursor.getString(cursor.getColumnIndex("_id"));

		} catch (Exception e) {
			Log.e("DatabaseManager--getId", e.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();

			db.endTransaction();
			db.close();
		}
		return id;
	}
	
	/** 往已跑留存数据表插入数据
	 * 
	 * @param imei imei号
	 *  */
	public boolean insertRun(String imei) {
		// TODO Auto-generated method stub
		String id = checkData(DBOpenHelper.RUNTABNAME,imei);
		if (id != null) {
			Log.e("DatabaseManager--insertRun", "run表插入数据失败,imei已存在");
			return false;
		}

		ContentValues values = new ContentValues();
		values.put("imei", imei);

		return dbOpenHelper.insert(DBOpenHelper.RUNTABNAME,values);
	}
	

	/**
	 * 检测数据imei是否已经存在
	 * 
	 * @param tabName 表名
	 * @param imei
	 *            imei号
	 * 
	 * @return 数据存在返回主键id，出错了或不存在返回null
	 * */
	public String checkData(String tabName,String imei) {

		String spl = "select _id from " + tabName
				+ " where imei=?";
		Cursor cursor = null;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.beginTransaction();

		try {
			cursor = db.rawQuery(spl, new String[] { imei });
			int i = cursor.getCount();
			if (i > 0) {
				while (cursor.moveToFirst()) {
					return cursor.getString(cursor.getColumnIndex("_id"));
				}
			}
		} catch (Exception e) {
			Log.e("DatabaseManager--checkData", e.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();

			db.endTransaction();
			db.close();
		}
		return null;
	}
	
	/**
	 * 查询表中有多少条数据
	 * @param tab 表名
	 * */
	public int getSize(String tab){
		return dbOpenHelper.getSize(tab,null,null);
	}
	
	/**
	 * 查询表中有多少今天之前的数据
	 * @param tab 表名
	 * */
	public int getSize(String tab,String time){
		return dbOpenHelper.getSize(tab,"savetime < ?",new String[]{time});
	}
	
	
	
	
	
	
//2016-02-25之前的逻辑
//	/**
//	 * 创建表
//	 * 
//	 * @param tabName
//	 *            表名
//	 * 
//	 * */
//	public void onCreateTable(String tableName) {
//		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//		db.execSQL("CREATE TABLE IF NOT EXISTS "
//				+ tableName
//				+ " (_id integer primary key AUTOINCREMENT, imei varchar(64), android_id varchar(64), phone varchar(64), "
//				+ "phone_num varchar(64), imsi varchar(64), yys varchar(64), mac varchar(64), wx varchar(64), wxdz varchar(64), "
//				+ "version varchar(64), sdk varchar(64), brand varchar(64), xh varchar(64), bluemac varchar(64), ipadress varchar(64), "
//				+ "network_type varchar(64), network_operatorName varchar(64), simstate varchar(64), serial varchar(64), zhiwen varchar(64), "
//				+ "radioVersion varchar(64), hardware varchar(64), manufacturer varchar(64), product varchar(64), device varchar(64), "
//				+ "lat varchar(64), lng varchar(64), fbl_w varchar(64), fbl_h varchar(64), cpu varchar(64), useragent varchar(64), lac varchar(64), cellid varchar(64))");// 执行有更改的sql语句
//		Log.e("HistoryDBManager--onCreateTable", "表-" + tableName + "创建成功");
//	}
//
//	/**
//	 * 保存数据到表中
//	 * 
//	 * @param tabName
//	 *            表名
//	 * 
//	 * */
//	public void save(String tabName, HistoryData data) {
//		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
//		Cursor cursor = null;
//		try {
//			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//
//			if (!tabIsExist(db, tabName)) {
//				onCreateTable(tabName);
//			}
//
//			cursor = db.rawQuery("select * from " + tabName + " where imei=?",
//					new String[] { data.getImei() });
//			int i = cursor.getCount();
//			if (i > 0) {
//				Log.e("HistoryDBManager--save", "数据已存在");
//				return;
//			}
//
//			ContentValues values = new ContentValues();
//			values.put("imei", data.getImei());
//			values.put("android_id", data.getAndroidId());
//			values.put("phone", data.getPhone());
//			values.put("phone_num", data.getPhoneNum());
//			values.put("imsi", data.getImsi());
//			values.put("yys", data.getYys());
//			values.put("mac", data.getMac());
//			values.put("wx", data.getWx());
//			values.put("wxdz", data.getWxdz());
//			values.put("version", data.getVersion());
//			values.put("sdk", data.getSdk());
//			values.put("brand", data.getBrand());
//			values.put("xh", data.getXh());
//			values.put("bluemac", data.getBluemac());
//			values.put("ipadress", data.getIpadress());
//			values.put("network_type", data.getNetworkType());
//			values.put("network_operatorName", data.getNetworkOperatorName());
//			values.put("simstate", data.getSimstate());
//			values.put("serial", data.getSerial());
//			values.put("zhiwen", data.getZhiwen());
//			values.put("radioVersion", data.getRadioVersion());
//			values.put("hardware", data.getHardware());
//			values.put("manufacturer", data.getManufacturer());
//			values.put("product", data.getProduct());
//			values.put("device", data.getDevice());
//			values.put("lat", data.getLat());
//			values.put("lng", data.getLng());
//			values.put("fbl_w", data.getFbl_w());
//			values.put("fbl_h", data.getFbl_h());
//			values.put("cpu", data.getCpu());
//			values.put("useragent", data.getUserAgent());
//			values.put("lac", data.getLac());
//			values.put("cellid", data.getCellid());
//			db.insert(tabName, null, values);
//
//			Log.e("HistoryDBManager--save", "数据库表-" + tabName + "添加数据成功");
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//
//	}
//
//	/**
//	 * 获取数据表有多少条数据
//	 * 
//	 * @param tabName
//	 *            表名
//	 * 
//	 * */
//	public int getDataLineSize(String tabName) {
//		int num = 0;
//		Cursor cursor = null;
//		try {
//
//			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//
//			if (!tabIsExist(db, tabName)) {
//				Log.e("HistoryDBManager--getDataLineSize", "数据库表-" + tabName
//						+ "不存在");
//				return num;
//			}
//
//			// 调用查找书库代码并返回数据源
//			cursor = db.rawQuery("select count(*) from " + tabName, null);
//
//			// 游标移到第一条记录准备获取数据
//			if (cursor.moveToFirst()) {
//
//				// 获取数据中的LONG类型数据
//				num = cursor.getInt(0);
//				Long count = cursor.getLong(0);
//
//			}
//
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return num;
//	}
//
//	// public void update(HistoryData data) {
//	// SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//	// ContentValues values = new ContentValues();
//	// }
//
//	/**
//	 * 获取表中的数据
//	 * 
//	 * @param tabName
//	 *            表名
//	 * @param id
//	 *            数据在表中的索引
//	 * 
//	 * */
//	public HistoryData getDBData(String tabName, int id) {
//
//		HistoryData data = null;
//		Cursor cursor = null;
//
//		try {
//
//			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//			cursor = db.rawQuery("select * from " + tabName + " where _id=?",
//					new String[] { id + "" });
//
//			int i = cursor.getCount();
//			if (i == 0) {
//				Log.e("HistoryDBManager--getDBData", tabName + "表--" + "_id:"
//						+ id + "不存在");
//				return null;
//			}
//
//			if (cursor.moveToFirst()) {
//
//				String imei = cursor.getString(cursor.getColumnIndex("imei"));
//				String android_id = cursor.getString(cursor
//						.getColumnIndex("android_id"));
//				String phone = cursor.getString(cursor.getColumnIndex("phone"));
//				String phone_num = cursor.getString(cursor
//						.getColumnIndex("phone_num"));
//				String imsi = cursor.getString(cursor.getColumnIndex("imsi"));
//				String yys = cursor.getString(cursor.getColumnIndex("yys"));
//				String mac = cursor.getString(cursor.getColumnIndex("mac"));
//				String wx = cursor.getString(cursor.getColumnIndex("wx"));
//				String wxdz = cursor.getString(cursor.getColumnIndex("wxdz"));
//				String version = cursor.getString(cursor
//						.getColumnIndex("version"));
//				String sdk = cursor.getString(cursor.getColumnIndex("sdk"));
//				String brand = cursor.getString(cursor.getColumnIndex("brand"));
//				String xh = cursor.getString(cursor.getColumnIndex("xh"));
//				String bluemac = cursor.getString(cursor
//						.getColumnIndex("bluemac"));
//				String ipadress = cursor.getString(cursor
//						.getColumnIndex("ipadress"));
//				String network_type = cursor.getString(cursor
//						.getColumnIndex("network_type"));
//				String network_operatorName = cursor.getString(cursor
//						.getColumnIndex("network_operatorName"));
//				String simstate = cursor.getString(cursor
//						.getColumnIndex("simstate"));
//				String serial = cursor.getString(cursor
//						.getColumnIndex("serial"));
//				String zhiwen = cursor.getString(cursor
//						.getColumnIndex("zhiwen"));
//				String radioVersion = cursor.getString(cursor
//						.getColumnIndex("radioVersion"));
//				String hardware = cursor.getString(cursor
//						.getColumnIndex("hardware"));
//				String manufacturer = cursor.getString(cursor
//						.getColumnIndex("manufacturer"));
//				String product = cursor.getString(cursor
//						.getColumnIndex("product"));
//				String device = cursor.getString(cursor
//						.getColumnIndex("device"));
//				String lat = cursor.getString(cursor.getColumnIndex("lat"));
//				String lng = cursor.getString(cursor.getColumnIndex("lng"));
//				String fbl_w = cursor.getString(cursor.getColumnIndex("fbl_w"));
//				String fbl_h = cursor.getString(cursor.getColumnIndex("fbl_h"));
//				String cpu = cursor.getString(cursor.getColumnIndex("cpu"));
//				String userAgent = cursor.getString(cursor
//						.getColumnIndex("useragent"));
//				String lac = cursor.getString(cursor.getColumnIndex("lac"));
//				String cellid = cursor.getString(cursor
//						.getColumnIndex("cellid"));
//
//				data = new HistoryData();
//				data.setImei(imei);
//				data.setAndroidId(android_id);
//				data.setPhone(phone);
//				data.setPhoneNum(phone_num);
//				data.setImsi(imsi);
//				data.setYys(yys);
//				data.setMac(mac);
//				data.setWx(wx);
//				data.setWxdz(wxdz);
//				data.setVersion(version);
//				data.setSdk(sdk);
//				data.setBrand(brand);
//				data.setXh(xh);
//				data.setBluemac(bluemac);
//				data.setIpadress(ipadress);
//				data.setNetworkType(network_type);
//				data.setNetworkOperatorName(network_operatorName);
//				data.setSimstate(simstate);
//				data.setSerial(serial);
//				data.setZhiwen(zhiwen);
//				data.setRadioVersion(radioVersion);
//				data.setHardware(hardware);
//				data.setManufacturer(manufacturer);
//				data.setProduct(product);
//				data.setDevice(device);
//				data.setLat(lat);
//				data.setLng(lng);
//				data.setFbl_w(fbl_w);
//				data.setFbl_h(fbl_h);
//				data.setCpu(cpu);
//				data.setUserAgent(userAgent);
//				data.setLac(lac);
//				data.setCellid(cellid);
//				Log.e("HistoryDBManager--getDBData", "获取数据成功--_id = " + id);
//			}
//
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return data;
//	}
//
//	/**
//	 * 删除表
//	 * 
//	 * @param tabName
//	 *            表名
//	 * 
//	 * */
//	public void deleteTab(String tabName) {
//
//		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//		db.execSQL("DROP TABLE IF EXISTS " + tabName);
//
//	}
//
//	/** 判断数据库中是否存在tabName表 */
//	public boolean tabIsExist(SQLiteDatabase db, String tabName) {
//		boolean result = false;
//		if (tabName == null) {
//			return false;
//		}
//		Cursor cursor = null;
//		try {
//			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
//					+ tabName.trim() + "' ";
//			cursor = db.rawQuery(sql, null);
//			if (cursor.moveToNext()) {
//				int count = cursor.getInt(0);
//				if (count > 0) {
//					result = true;
//				}
//			}
//
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return result;
//	}
//
//	/**
//	 * 获取liucun表中的数据(参数是"yyy-MM-dd"类型的字符串)
//	 * 
//	 * @param s1
//	 *            当前的日期
//	 * 
//	 * */
//	public int getRunNum(String s1) {
//		int num = 0;
//		Cursor cursor = null;
//		s1 = "hdj" + s1;
//		try {
//
//			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//
//			cursor = db.rawQuery("select * from " + "liucun"
//					+ " where save_day=?", new String[] { s1 });
//
//			int i1 = cursor.getCount();
//			if (i1 == 0) {
//				Log.e("HistoryDBManager--getRunNum", "liucun表--" + "save_day:"
//						+ s1 + "不存在");
//				return num;
//			}
//
//			if (cursor.moveToFirst())
//				num = cursor.getInt(cursor.getColumnIndex("run_liucun"));
//
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//
//		return num;
//	}
//
//	/**
//	 * 往liucun表存数据(参数是"yyy-MM-dd"类型的字符串)
//	 * 
//	 * @param s
//	 *            当前的日期
//	 * @param s1
//	 *            昨天的日期
//	 * @param num
//	 *            跑了多少条数据
//	 * 
//	 * */
//	public void setRunNum(String s1, String s2, int num) {
//
//		s1 = "hdj" + s1;
//		s2 = "hdj" + s2;
//
//		saveTodayData(s1, num);
//		deleYesterdayData(s2);
//
//	}
//
//	private void saveTodayData(String s1, int num) {
//		// TODO Auto-generated method stub
//		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//		Cursor cursor = null;
//		try {
//			cursor = db.rawQuery("select * from liucun where save_day=?",
//					new String[] { s1 });
//			int i = cursor.getCount();
//			if (i > 0) {
//
//				db.execSQL("update liucun set run_liucun=? where save_day=?",
//						new Object[] { num, s1 });
//
//			} else {
//
//				db.execSQL(
//						"insert into liucun (save_day,run_liucun) values(?,?)",
//						new Object[] { s1, 1 });
//
//			}
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//	}
//
//	private void deleYesterdayData(String s2) {
//		// TODO Auto-generated method stub
//		Cursor cursor = null;
//		try {
//			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//
//			cursor = db.rawQuery("select * from liucun where save_day=?",
//					new String[] { s2 });
//			if (cursor.getCount() > 0) {
//				db.execSQL("delete from liucun where save_day = '" + s2 + "'");
//			}
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//	}

}
