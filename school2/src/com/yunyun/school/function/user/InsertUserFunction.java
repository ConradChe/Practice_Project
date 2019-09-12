package com.yunyun.school.function.user;

import java.util.HashMap;
import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;
import com.yunyun.school.util.StringUtil;

/**
 * InsertUserFunction
 * @description 插入用户function
 * @author XuTianYi
 * @creation time 2017年12月28日 上午10:17:12
 */

public class InsertUserFunction implements IFunction {

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		//获取所需参数
		String loginName = StringUtil.convertObject(params.get("login_name"));
		String loginPwd = StringUtil.convertObject(params.get("login_pwd"));
		String realName = StringUtil.convertObject(params.get("real_name"));
		
		//进行基础判断
		if(loginName.equals("")){
			return new DaoResult(-1,"请输入登录名");
		}
		if(loginPwd.equals("")){
			return new DaoResult(-1,"请输入密码");
		}
		if(realName.equals("")){
			return new DaoResult(-1,"请输入真实姓名");
		}
		
		//查询登录名是否已存在
		Map isHadUser = new HashMap();
		isHadUser = DataContext.getContext().doHexByIdToMap("hex_user_queryUserByLoginName", "login_name=" + loginName);
		if(isHadUser != null){
			return new DaoResult(-1,"该登录名已存在，请重新输入");
		}
		
		//进行逻辑操作
		Map param = new HashMap();
		String userId = DataContext.getContext().getSequenceId("U");
		param.put("user_id", userId);
		param.put("login_name", loginName);
		param.put("login_pwd", loginPwd);
		param.put("real_name", realName);

		//执行并返回结果
		DaoResult daoResult = DataContext.getContext().doHexById("hex_user_insertUser", param);
		return daoResult;
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub

	}

}
