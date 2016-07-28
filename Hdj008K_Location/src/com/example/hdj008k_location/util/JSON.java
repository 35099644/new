package com.example.hdj008k_location.util;

import org.json.JSONArray;

import android.util.Log;

public class JSON {

	/**
	 * str转JSONArray类
	 * 
	 **/
	public static JSONArray toArray(String str) {

		JSONArray array = new JSONArray();
//		Log.e("JSON--toArray", "ttt-str="+str);
		try {
			
			JSONArray array2 = new JSONArray(str);
			if(array2!=null){
//				Log.e("JSON--toArray", "ttt-array2");
				return array2;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
//		Log.e("JSON--toArray", "ttt-array");
		return array;

	}

	/**
	 * JSONArray转str类
	 * 
	 **/
	public static String toString(JSONArray array) {

		String str = "";

		if (array != null)
			str = array.toString();

//		Log.e("JSON--toString", "ttt-str = "+str);
		return str;

	}
	
}
