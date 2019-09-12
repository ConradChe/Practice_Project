package com.yunyun.school.function.user;

import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;
import com.yunyun.school.util.StringUtil;

/**
 * DeleteUserFunction
 * @description 删除用户function
 * @author XuTianYi
 * @creation time 2017年12月28日 上午10:16:49
 */

public class DeleteUserFunction implements IFunction {

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		String userId = StringUtil.convertObject(params.get("user_id"));
		
		//查询该条数据是否存在
		Map isHadUser = DataContext.getContext().doHexByIdToMap("hex_user_queryUserByUserId", "user_id=" + userId);
		if(isHadUser == null || isHadUser.isEmpty()){
			return new DaoResult(-1,"不存在该条数据");
		}
		
		//执行操作并返回结果
		DaoResult daoResult = DataContext.getContext().doHexById("hex_user_deleteUserByUserId", "user_id=" + userId);
		return daoResult;
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub

	}

}
