package com.yunyun.school.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haitsoft.framework.core.util.EnDeUtil;

/**
 * 字符串判断工具
 * @author Abby.Wang
 * @creation time 2017年12月14日 下午9:46:57
 */
public class StringUtil {
	
	/**
	 * 判断是否为空
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return (str == null) || "".equals(str) || "null".equals(str);
	}
	
	/**
	 * 判断手机号码是否正确
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		Pattern p = Pattern.compile("^1[3|4|5|6|7|8][0-9]\\d{8}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	/**
	 * 判断是否是Email
	 */
	public static boolean isEmail(String obj) {
		if (obj == null) {
			return false;
		}
		Pattern p = Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
		Matcher m = p.matcher(obj);
		return m.matches();
	}
	
	/**
	 * 判断是否含有空格和单引号，因为有可能就有可能存在注入攻击
	 * @param text
	 * @return
	 */
	public static boolean isHaveBlank(String text) {
		// 如果为空， 则不肯定没有空格
		if (isBlank(text)) {
			return false;
		}
		return text.indexOf(" ") == -1 && text.indexOf("'") == -1 ? false : true;
	}

	/**
	 * 转化变量为字符串,为空就返回""
	 * @param obj
	 * @return
	 */
	public static String convertObject(Object obj) {
		return obj == null || "".equals(obj.toString().trim()) ? "" : obj.toString();
	}
	
	/**
	 * 转化变量为整型, 为空就返回0
	 * @param obj
	 * @return
	 */
	public static int convertObjectToInt(Object obj) {
		String string = convertObject(obj);
		if (isBlank(string)) {
			return 0;
		}
		return Integer.valueOf(string.trim());
	}

	/**
	 * 转化变量为浮点型, 为空就返回0.0
	 * @param obj
	 * @return
	 */
	public static double convertObjectToDouble(Object obj) {
		String string = convertObject(obj);
		if (isBlank(string)) {
			return 0.0;
		}
		return Double.valueOf(string.trim());
	}
	
	/**
	 * 将多个不带引号的SQL参数转化为带引号的参数格式
	 * 例如: 123,456,789通过此方法转化为'123','456','789'
	 * @param str
	 * @return
	 */
	public static String transformationToInSql(String str) {
		if ("".equals(str)) {
			return "";
		}
		if (str.indexOf("'") != -1) {
			return str;
		}
		String[] strs = str.split(",");
		StringBuffer buffer = new StringBuffer();
		for (String string : strs) {
			if (StringUtil.isBlank(string)) {
				continue;
			}
			buffer.append("'").append(string).append("',");
		}
		return buffer.substring(0, buffer.length() - 1).toString();
	}

	/**
	 * 删除字符串最后一个字符
	 * @param str
	 * @return
	 */
	public static String deleteLastChar(String str) {
		if (str.length() > 0) {
			return str.substring(0, str.length() - 1);
		}
		return str;
	}
	
	/**
	 * 将参数字符串转化为JSON对象
	 * @param params
	 * @return
	 */
	public static JSONObject parseParamsToJSONObject(String params) {
		JSONObject runParams = new JSONObject();
		if (params == null || params.length() == 0) {
			return runParams;
		}
		String[] paramList = params.split("&");
		for (String param : paramList) {
			runParams.put(param.split("=")[0], param.split("=").length == 1 ? "" : param.split("=")[1]);
		}
		return runParams;
	}
	
	/**
	 * 把json字符串转换成List
	 * @param jsonStr
	 * @return
	 */
	public static List<Map> getJosnItem(String jsonStr) {
		if (null == jsonStr || jsonStr.trim().length() == 0) {
			return null;
		}
		List<Map> items = new ArrayList();
		items = JSON.parseArray(jsonStr, Map.class);

		return items;
	}
	
	/**
	 * 
	 * @Title: encryptToken
	 * @Description: 加密TOKEN
	 * @param token
	 * @return
	 */
	public static String encryptToken(String token) {
		EnDeUtil enDeUtil = new EnDeUtil("yunyun_token");
		return enDeUtil.encrypt(token).toUpperCase();
	}

	/**
	 * 
	 * @Title: decryptToken
	 * @Description: 解密TOKEN
	 * @param token
	 * @return
	 */
	public static String decryptToken(String token) {
		EnDeUtil enDeUtil = new EnDeUtil("yunyun_token");
		return enDeUtil.decrypt(token).toUpperCase();
	}
	
	public static String[] decryptTokenToArray(String token) {
		return decryptToken(token).split("_");
	}
}
