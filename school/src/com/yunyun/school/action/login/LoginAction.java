package com.yunyun.school.action.login;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.haitsoft.framework.cache.bean.Cache;
import com.haitsoft.framework.cache.context.CacheContext;
import com.haitsoft.framework.core.context.HaitContext;
import com.haitsoft.framework.core.servlet.IAction;
import com.haitsoft.framework.core.util.CookieUtil;
import com.yunyun.school.action.system.AbsoluteLogin;
import com.yunyun.school.action.system.LoginUserEntity;
import com.yunyun.school.util.StringUtil;
import com.yunyun.school.util.SystemConstantUtil;

/**
 * 登录
 * @author Abby.Wang
 * @description 
 * @code by 2017年12月17日 下午8:41:29
 */
public class LoginAction implements IAction {
	private String loginUrl = "../login.html";
	private String indexUrl = "../index.html";
	
	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// 清除登录成功标志
		this.removeCookie(response);
		HttpSession session = request.getSession();
				
		// 读取前台传递过来的信息，并保存这些信息
		String loginName = request.getParameter("login_name");
		String loginPwd = request.getParameter("login_pwd");

		// 进行二次基本判断
		if (loginName == null || loginName.length() == 0 || loginPwd == null || loginPwd.length() == 0) {
			session.setAttribute("errorMsg", "用户名或密码不允许为空!");
			response.sendRedirect(this.getLoginUrl());
			return;
		}

		// 防止简单的SQL注入
		if (StringUtil.isHaveBlank(loginName) || StringUtil.isHaveBlank(loginPwd)) {
			session.setAttribute("errorMsg", "用户名或密码不允许存在空格和单引号!");
			response.sendRedirect(this.getLoginUrl());
			return;
		}
		
		// 获取登录对象
		AbsoluteLogin login = (AbsoluteLogin) HaitContext.getBean("common.userLoginAcion").getInstance();
		
		// 通过接口调用密码验证的方法
		if (!login.validateUserAndPwd(loginName, loginPwd)) {
			session.setAttribute("errorMsg", "用户名或者密码错误!");
			response.sendRedirect(this.getLoginUrl());
			return;
		}
		
		// 读取当前用户
		LoginUserEntity admin = login.getCurrentUser();
		// 进行登录完成后的相关操作
		this.addCookie(response, admin);//添加cookie
		login.changeSession(request);

		// 将这些必要信息缓存起来
		SystemConstantUtil.cacheCurDay(SystemConstantUtil.CURRENT_ADMIN, admin.getUserId(), admin);

		// 转到管理中心主页
		String url = this.getIndexUrl();
		response.sendRedirect(url);
	}

	/**
	 * 
	 * @Title: addCookie
	 * @Description: 登录成功，存入cookie (有效期：当天)
	 * @param response
	 * @param currentUser
	 */
	protected void addCookie(HttpServletResponse response, LoginUserEntity currentUser) {
		// 生成结束时间
		Calendar curCalendar = Calendar.getInstance();
		Calendar dayEndCalendar = Calendar.getInstance();
		dayEndCalendar.set(Calendar.HOUR_OF_DAY, 23);
		dayEndCalendar.set(Calendar.MINUTE, 59);
		dayEndCalendar.set(Calendar.SECOND, 59);
		// 有效时间
		Long effectiveTime = dayEndCalendar.getTime().getTime() - curCalendar.getTime().getTime();
		// 到期时间
		Long expiredTime = dayEndCalendar.getTime().getTime();

		// 生成token:E4567890087655678_到期时间
		String token = currentUser.getUserId() + "_" + expiredTime.longValue();
		currentUser.setToken(StringUtil.encryptToken(token));// token加密(token是专门的加密)

		// 把token保存到cookie中
		CookieUtil.addCookie(SystemConstantUtil.USER_TOKEN, currentUser.getToken(), effectiveTime.intValue() / 1000, response);
		
		// 保存到缓存中
		Cache cacheUser = CacheContext.getContext().getCache(SystemConstantUtil.USER_TOKEN, effectiveTime.intValue() / 1000);
		cacheUser.put(currentUser.getUserId(), currentUser.getToken());
		
		this.setIndexUrl(indexUrl + "?token=" + currentUser.getToken());
	}
	
	/**
	 * @Title: removeCookie
	 * @Description: 登录前，清除cookie
	 * @param response
	 */
	protected void removeCookie(HttpServletResponse response) {
		CookieUtil.removeCookie(SystemConstantUtil.USER_TOKEN, response);
	}
	
	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	
	public String getIndexUrl() {
		return indexUrl;
	}

	public void setIndexUrl(String indexUrl) {
		this.indexUrl = indexUrl;
	}
}
