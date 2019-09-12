package com.yunyun.school.function.grade;

import java.util.HashMap;
import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;
import com.yunyun.school.util.StringUtil;
import com.yunyun.school.util.UserUtil;

/**
 * InsertClassFunction
 * @description 添加班级function
 * @author XuTianYi
 * @creation time 2017年12月26日 下午4:19:10
 */
public class InsertClassFunction implements IFunction{

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		/*-----------获取所需参数-----------*/
		String gradeName = StringUtil.convertObject(params.get("grade_name"));//班级名称
		String parentId = StringUtil.convertObject(params.get("parent_id"));//父级ID
		int sort = StringUtil.convertObjectToInt(params.get("sort"));//排序号
		/*-----------------------------*/
		
		/*-----------基础判断------------*/
		if(gradeName.equals("")){
			return new DaoResult(-1,"请输入班级名称");
		}
		
		/*-----------------------------*/
		
		/*------进一步判断-------*/
		//根据父级ID设置是否为子叶
		int isLeaf = 0;
		if(parentId.equals("")){
			isLeaf = 2;
		}
		else{
			isLeaf = 1;
		}
		
		if(isLeaf == 0){
			return new DaoResult(-1,"子叶参数有误");
		}
		
		//设置父级ID
		if(parentId.equals("")){
			parentId = "0";
		}
		
		/*-----------------------*/
		
		/*--------逻辑操作---------*/
		Map param = new HashMap();
		String gradeId = DataContext.getContext().getSequenceId("");
		param.put("grade_id", gradeId);
		param.put("grade_name", gradeName);
		param.put("is_leaf", isLeaf);
		param.put("parent_id", parentId);
		param.put("sort", sort);
		String operatorId = UserUtil.getUserId(params);//通过session获取用户id
		String operatorName = UserUtil.getUserName(params);//用户名称
		param.put("operator_id", operatorId);
		param.put("operator_name", operatorName);
		/*-----------------------*/
		
		
		//执行并返回结果
		return DataContext.getContext().doHexById("hex_class_insertClass", param);

	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
