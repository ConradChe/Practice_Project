package com.yunyun.school.util;

import java.util.Map;

/**
 * 系统员工Util
 * @author Abby.Wang
 * @creation time 2017年12月14日 下午9:59:32
 */
public class UserUtil {
	
	/**
	 * 获取用户ID
	 */
	public static String getUserId(Map params) {
		return params.get("session.user_id").toString();
	}
	
	/**
	 * 获取用户名称
	 * @param params
	 * @return
	 */
	public static String getUserName(Map params) {
		return params.get("session.real_name").toString();
	}
	
	/**
	 * 获取用户账户（电话）
	 * @param params
	 * @return
	 */
	public static String getUserAccount(Map params) {
		return params.get("session.login_name").toString();
	}
}
