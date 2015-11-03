package com.finance.util;

import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * @author 陈霖 2015-8-31
 */
public class TimeUtil {

	/**
	 * 不指定时间格式
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat();
	/**
	 * 时间格式 yyyy/MM/dd HH:mm:ss.SSS 
	 */
	private static final SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	/**
	 * 时间格式 yyyy/MM/dd HH:mm:ss
	 */
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	/**
	 * 时间格式 yyyy/MM/dd HH:mm
	 */
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	/**
	 * 时间格式 yyyy/MM/dd
	 */
	private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd");
	/**
	 * 时间格式 HH:mm:ss.SSS
	 */
	private static final SimpleDateFormat sdf4 = new SimpleDateFormat("HH:mm:ss.SSS");
	/**
	 * 时间格式 HH:mm:ss
	 */
	private static final SimpleDateFormat sdf5 = new SimpleDateFormat("HH:mm:ss");
	/**
	 * 时间格式 HH:mm
	 */
	private static final SimpleDateFormat sdf6 = new SimpleDateFormat("HH:mm");
	/**
	 * 时间格式 MM/dd HH:mm
	 */
	private static final SimpleDateFormat sdf7 = new SimpleDateFormat("MM/dd HH:mm");

	/**
	 * 把日期转换成yyyy/MM/dd HH:mm:ss.SSS格式(如2015/08/31 10:30:15.500)的字符串(注意：如果日期对象为空，则返回空字符串)
	 * 
	 * @param date
	 *            日期
	 * @return 转换后的字符串
	 */
	public static String date2Str0(Date date) {
		return sdf0.format(date);
	}

	/**
	 * 把日期转换成yyyy/MM/dd HH:mm:ss格式(如2015/08/31 10:30:15)的字符串(注意：如果日期对象为空，则返回空字符串)
	 * 
	 * @param date
	 *            日期
	 * @return 转换后的字符串
	 */
	public static String date2Str1(Date date) {
		return sdf1.format(date);
	}

	/**
	 * 把日期转换成yyyy/MM/dd HH:mm格式(如2015/08/31 10:30)的字符串(注意：如果日期对象为空，则返回空字符串)
	 * 
	 * @param date
	 *            日期
	 * @return 转换后的字符串
	 */
	public static String date2Str2(Date date) {
		return sdf2.format(date);
	}

	/**
	 * 把日期转换成yyyy/MM/dd格式(如2015/08/31)的字符串(注意：如果日期对象为空，则返回空字符串)
	 * 
	 * @param date
	 *            日期
	 * @return 转换后的字符串
	 */
	public static String date2Str3(Date date) {
		return sdf3.format(date);
	}

	/**
	 * 把日期转换成HH:mm:ss.SSS格式(如10:30:15.500)的字符串(注意：如果日期对象为空，则返回空字符串)
	 * 
	 * @param date
	 *            时间
	 * @return 转换后的字符串
	 */
	public static String time2Str0(Date date) {
		return sdf4.format(date);
	}

	/**
	 * 把日期转换成HH:mm:ss格式(如10:30:15)的字符串(注意：如果日期对象为空，则返回空字符串)
	 * 
	 * @param date
	 *            时间
	 * @return 转换后的字符串
	 */
	public static String time2Str1(Date date) {
		return sdf5.format(date);
	}

	/**
	 * 把日期转换成HH:mm格式(如10:30)的字符串(注意：如果日期对象为空，则返回空字符串)
	 * 
	 * @param date
	 *            时间
	 * @return 转换后的字符串
	 */
	public static String time2Str2(Date date) {
		return sdf6.format(date);
	}
	/**
	 * 把日期转换成MM/dd HH:mm格式(如11/02 10:30)的字符串(注意：如果日期对象为空，则返回空字符串)
	 * 
	 * @param date
	 *            时间
	 * @return 转换后的字符串
	 */
	public static String time2Str3(Date date) {
		return sdf7.format(date);
	}

	/**
	 * 把日期转换成指定格式(如pattern 10:30:15)的字符串(注意：如果日期对象为空，则返回空字符串)
	 * 
	 * @param date
	 *            时间
	 * @param pattern
	 *            指定转换格式
	 * @return 转换后的字符串
	 */
	public static String date2Str(Date date, String pattern) {
		sdf.applyPattern(pattern);
		return sdf.format(date);
	}

}
