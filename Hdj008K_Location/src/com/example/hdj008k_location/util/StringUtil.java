package com.example.hdj008k_location.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class StringUtil {

	/**
	 * 获取拼音头字母
	 * 
	 * @param name
	 *            汉字或拼音
	 * @return 拼音
	 */
	public static String getPinYinHeadChar(String paramString) {
		String str = "";

		HanyuPinyinOutputFormat localHanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
		localHanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);

		for (int i = 0; i < paramString.length(); ++i) {
			final char char1 = paramString.charAt(i);

			final String[] a = PinyinHelper.toHanyuPinyinStringArray(char1,
					localHanyuPinyinOutputFormat);

			if (a != null) {
				str = String.valueOf(str) + a[0].charAt(0);
			} else {
				str = String.valueOf(str) + char1;
			}
		}
		return str.toUpperCase();
	}

	/**
	 * 汉字转拼音的方法
	 * 
	 * @param name
	 *            汉字或拼音
	 * @return 拼音
	 */
	public static String HanyuToPinyin(String name) {
		String pinyinName = "";
		char[] nameChar = name.toCharArray();

		HanyuPinyinOutputFormat localHanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
		localHanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		localHanyuPinyinOutputFormat
				.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		localHanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

		for (int i = 0; i < nameChar.length; i++) {

			try {
				if (Character.toString(nameChar[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {

					String[] s = PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], localHanyuPinyinOutputFormat);
					pinyinName = pinyinName + s[0];

				} else {
					pinyinName = pinyinName + Character.toString(nameChar[i]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return pinyinName;
	}

	/**
	 * 判断字符串是不是含有中文
	 * 
	 * @return true 含有
	 * */
	public static boolean isChinese(String txt) {

		char[] charArray = txt.toCharArray();

		for (int i = 0; i < charArray.length; i++) {

			if ((charArray[i] >= 0x4e00) & (charArray[i] <= 0x9fbb)) {
				return true;
			}

		}

		return false;
	}

}
