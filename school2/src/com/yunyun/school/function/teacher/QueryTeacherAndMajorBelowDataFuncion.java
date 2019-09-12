package com.yunyun.school.function.teacher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;

/**
 * QueryTeacherAndMajorBelowDataFuncion
 * @description 查询专业下的教师
 * @author XuTianYi
 * @creation time 2017年12月25日 上午11:33:20
 */

public class QueryTeacherAndMajorBelowDataFuncion implements IFunction{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
public DaoResult doFunction(Map params, Map context) throws Exception {
		
		/*------ 逻辑操作 ------*/
		List returnList = new ArrayList();	// 返回数据List
		
		// 1. 查询所有的专业数据
		List majorDataList = DataContext.getContext().doHexByIdToList("hex_major_queryAllMajorData", "");
		if (majorDataList != null && majorDataList.size() > 0){
			// 查询每个专业对应的教师数据,并封装成分组列表的返回格式
			Map returnMap;
			for (int i = 0; i < majorDataList.size(); i++){
				Map majorMap = (Map)majorDataList.get(i);
				returnMap = new HashMap();
				returnMap.put("major_id", majorMap.get("major_id"));
				returnMap.put("major_name", majorMap.get("major_name"));
				// 查询专业下的教师数据
				params.put("major_id", majorMap.get("major_id"));
				List teacherList = DataContext.getContext().doHexByIdToList("hex_teacher_queryTeacherByConditions", params);
				returnMap.put("items", teacherList);
				returnList.add(returnMap);
			}
		}
		/*------------------------------*/
		
		
		/*------ 返回执行结果 ------*/
		DaoResult daoResult = new DaoResult(1, "success");
		daoResult.setItems(returnList);
		daoResult.setTotal(returnList.size());
		daoResult.setLength(returnList.size());
		return daoResult;
		/*------------------------------*/
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
