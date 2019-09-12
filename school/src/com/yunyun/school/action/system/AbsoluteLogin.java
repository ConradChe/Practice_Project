package com.yunyun.school.action.system;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用户登录抽象类
 * @author Abby.Wang
 * @description 
 * @code by 2017年12月17日 下午8:45:12
 */
public abstract class AbsoluteLogin {

	/**
	 * 当前登录用户实体类
	 */
	private LoginUserEntity currentUser;
	
	
	/**
	 * 验证登录名和密码
	 * @param userName 登录名
	 * @param userPwd 密码
	 * @return
	 */
	public abstract boolean validateUserAndPwd(String loginName, String loginPwd);

	/**
	 * @Title: changeSession
	 * @Description: 更新会话，防止固话攻击
	 * @param request
	 */
	public void changeSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
			if (request.getCookies() != null && request.getCookies().length > 0) {
				Cookie cookie = request.getCookies()[0];
				cookie.setMaxAge(0);
			}
		}
		session = request.getSession(true);
	}
	
	public LoginUserEntity getCurrentUser() {
		return currentUser;
	}


	public void setCurrentUser(LoginUserEntity currentUser) {
		this.currentUser = currentUser;
	}
	
	
}
