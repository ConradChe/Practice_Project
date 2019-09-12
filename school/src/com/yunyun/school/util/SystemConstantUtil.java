package com.yunyun.school.util;

import java.io.Serializable;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import com.haitsoft.framework.cache.bean.Cache;
import com.haitsoft.framework.cache.context.CacheContext;
import com.haitsoft.framework.core.util.CookieUtil;
import com.yunyun.school.action.system.LoginUserEntity;

/**
 * 系统常量
 * @author Abby.Wang
 * @description 
 * @code by 2017年12月17日 下午9:24:17
 */
public class SystemConstantUtil {
	
	/**
	 * 登录用户token标识
	 */
	public static final String USER_TOKEN = "user_token";
	
	/**
	 * 登录用户：用户信息
	 */
	public static final String CURRENT_ADMIN = "current_admin";
	
	/**
	 * @Title: getCurUserEntityByCache
	 * @Description: 得到当前登录用户的实体对象
	 * @param request
	 * @return
	 */
	public static LoginUserEntity getCurUserEntityByCache(HttpServletRequest request) {
		String userToken = CookieUtil.getCookie(USER_TOKEN, request);
		// 如果当前用户为空，那么说明还没有登录
		if (null == userToken || userToken.length() <= 0) {
			return null;
		}
		// 解密获取token中的信息
		String[] deTokenArr = StringUtil.decryptToken(userToken).split("_");
		String currentUserId = deTokenArr[0];
		Object object = getCacheValByIdAndKey(CURRENT_ADMIN, currentUserId);
		if (null == object) {
			return null;
		}
		LoginUserEntity resultEntity = (LoginUserEntity) object;
		return resultEntity;
	}
	
	/**
	 * @Title: getCacheValByIdAndKey
	 * @Description: 根据缓存ID与key得到相应的值
	 * @param cacheId
	 * @param key
	 * @return
	 */
	public static Object getCacheValByIdAndKey(String cacheId, String key) {
		Cache cache = CacheContext.getContext().getCache(cacheId);
		if (null == cache) {
			return null;
		}
		Object object = cache.get(key);
		return object;
	}
	
	/**
	 * @Title: cacheCurDay
	 * @Description: 缓存当天
	 * @param cacheId
	 * @param key
	 * @param value
	 */
	public static void cacheCurDay(String cacheId, String key, Serializable value) {
		Calendar curCalendar = Calendar.getInstance();
		Calendar dayEndCalendar = Calendar.getInstance();
		dayEndCalendar.set(Calendar.HOUR_OF_DAY, 23);
		dayEndCalendar.set(Calendar.MINUTE, 59);
		dayEndCalendar.set(Calendar.SECOND, 59);
		// 有效时间
		Long effectiveTime = dayEndCalendar.getTime().getTime() - curCalendar.getTime().getTime();
		int effectiveSecondTime = effectiveTime.intValue() / 1000;
		// 保存到缓存中
		CacheContext.getContext().getCache(cacheId, effectiveSecondTime).put(key, value);
	}
}
