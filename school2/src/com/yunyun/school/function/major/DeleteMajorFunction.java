package com.yunyun.school.function.major;

import java.util.HashMap;
import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;
import com.yunyun.school.util.StringUtil;

/**
 * DeleteMajorFunction
 * @description 删除专业function
 * @author XuTianYi
 * @creation time 2017年12月27日 上午11:57:48
 */

public class DeleteMajorFunction implements IFunction {

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		//获取所需参数
		String majorId = StringUtil.convertObject(params.get("major_id"));
		
		//查询该条数据是否存在
		Map isHadMajor = DataContext.getContext().doHexByIdToMap("hex_major_queryMajorByMajorId", "major_id=" + majorId);
		if(isHadMajor == null || isHadMajor.isEmpty()){
			return new DaoResult(-1,"不存在该条数据");
		}
		
		//执行操作并返回结果
		DaoResult daoResult = DataContext.getContext().doHexById("hex_major_deleteMajorByMajorId", "major_id="+majorId);
		return daoResult;
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub

	}

}
