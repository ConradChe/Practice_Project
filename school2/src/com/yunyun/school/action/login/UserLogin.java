package com.yunyun.school.action.login;

import java.util.HashMap;
import java.util.Map;

import com.haitsoft.framework.data.context.DataContext;
import com.yunyun.school.action.system.AbsoluteLogin;
import com.yunyun.school.action.system.LoginUserEntity;

/**
 * 系统用户登录
 * @author Abby.Wang
 * @description 
 * @code by 2017年12月17日 下午8:52:24
 */
public class UserLogin extends AbsoluteLogin {

	@Override
	public boolean validateUserAndPwd(String loginName, String loginPwd) {
		/*--------------- 查询用户名和密码是否正确 ---------------*/
		HashMap loginParam = new HashMap();
		loginParam.put("login_name", loginName);
		loginParam.put("login_pwd", loginPwd);
		Map validateMap = DataContext.getContext().doHexByIdToMap("hex_system_querySystemUserWhenLogin", loginParam);
		if (validateMap == null || validateMap.isEmpty()) {
			return false;
		}
		/*-----------------------------------------------------*/
		
		/*--------------- 根据登录名查询用户信息 ---------------*/
		Map userMap = DataContext.getContext().doHexByIdToMap("hex_system_querySystemUserByLoginName", "login_name=" + loginName);
		LoginUserEntity currentUser = new LoginUserEntity();
		currentUser.setUserId(userMap.get("user_id").toString());
		currentUser.setLoginName(userMap.get("login_name").toString());
		currentUser.setRealName(userMap.get("real_name").toString());
		// 保存当前登录用户的基础信息
		setCurrentUser(currentUser);
		/*-----------------------------------------------------*/
		return true;
	}

}
