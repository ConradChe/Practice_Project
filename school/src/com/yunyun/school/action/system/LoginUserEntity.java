package com.yunyun.school.action.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录用户实体类
 * @author Abby.Wang
 * @description 
 * @code by 2017年12月17日 下午8:25:49
 */
public class LoginUserEntity implements Serializable {

	/**
	 * 自动生成的ID
	 */
	private static final long serialVersionUID = -587065026262476338L;
	
	/**
	 * token
	 */
	private String token;
	
	/**
	 * 用户编号
	 */
	private String userId;
	
	/**
	 * 登录名
	 */
	private String loginName;
	
	/**
	 * 真实姓名
	 */
	private String realName;
	

	/**
	 * 属性封装
	 */
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Map toMap() {
	    Map user = new HashMap();
	    user.put("token", getToken());
	    user.put("user_id", getUserId());
	    user.put("login_name", getLoginName());
	    user.put("real_name", getRealName());
	    return user;
	}
	
}
