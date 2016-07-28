package com.example.hdj008k_location.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.hdj008k_location.obj.HideApp;

import android.util.Log;

/**
 * 自定义转换工具类
 * 
 * 
 * */
public class ListToString {

	/**
	 * string转list
	 **/
	public static ArrayList<Map<String, String>> stringToList(String str) {
		// TODO Auto-generated method stub
		ArrayList<Map<String, String>> list = null;

		if (str != null & str.length() > 0) {

			list = new ArrayList<Map<String, String>>();
			String[] strArray = str.split("\n");

			for (int i = 0; i < strArray.length; i++) {

				String[] s = strArray[i].split("_");

				if (s.length == 4) {
					Map<String, String> map = new HashMap<String, String>();

					map.put("systemkey", s[0]);
					map.put("systemvalue", s[1]);
					map.put("statu", s[2]);
					map.put("check", s[3]);

					list.add(map);
				}
			}
		}

		return list;
	}

	/**
	 * string转list
	 **/
	public static ArrayList<String> stringToList2(String str) {
		// TODO Auto-generated method stub
		ArrayList<String> list = null;
		
//		Log.e("ListToString--stringToList2", "ttt--str = "+str);

		if (str != null & str.length() > 0) {

			list = new ArrayList<String>();
			String[] strArray = str.split("\n");

			for (int i = 0; i < strArray.length; i++) {
				String s = strArray[i];
				if (s!=null&&s.length() > 0) {
					list.add(s);
				}
			}
//			Log.e("ListToString--stringToList2", "ttt--list = "+list.toString());
		}

		return list;
	}

}
