package com.yunyun.school.function.user;

import java.util.HashMap;
import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;
import com.yunyun.school.util.StringUtil;

/**
 * UpdateUserFunction
 * @description 更新用户function
 * @author XuTianYi
 * @creation time 2017年12月28日 下午12:04:22
 */

public class UpdateUserFunction implements IFunction {

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		//获取所需参数
		String userId = StringUtil.convertObject(params.get("user_id"));
		String loginPwd = StringUtil.convertObject(params.get("login_pwd"));
		String realName = StringUtil.convertObject(params.get("real_name"));
		
		//进行基础判断
		if(userId.equals("")){
			return new DaoResult(-1,"用户ID获取异常");
		}
		if(realName.equals("")){
			return new DaoResult(-1,"请输入真实姓名");
		}
		
		//查询该用户是否存在
		Map isHadUser = new HashMap();
		isHadUser = DataContext.getContext().doHexByIdToMap("hex_user_queryUserByUserId", "user_id=" + userId);
		if(isHadUser.isEmpty() || isHadUser == null){
			return new DaoResult(-1,"该条数据不存在");
		}
		
		//逻辑操作
		Map param = new HashMap();
		param.put("user_id", userId);
		if(!loginPwd.equals("")){
			param.put("login_pwd", loginPwd);
		}
		param.put("real_name", realName);
		
		DaoResult daoResult = DataContext.getContext().doHexById("hex_user_updateUser", param);
		return daoResult;
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub

	}

}
