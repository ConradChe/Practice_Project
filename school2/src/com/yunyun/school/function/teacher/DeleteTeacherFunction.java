package com.yunyun.school.function.teacher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.bean.Response;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;
import com.yunyun.school.util.StringUtil;

/**
 * DeleteTeacherFunction
 * @description 删除教师function
 * @author XuTianYi
 * @creation time 2017年12月28日 下午4:07:05
 */

public class DeleteTeacherFunction implements IFunction {

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		//获取所需参数
		String teacherId = StringUtil.convertObject(params.get("teacher_id"));
		String majorId = StringUtil.convertObject(params.get("major_id"));
		
		
		//查询该条记录是否存在
		Map isHadTeacher = new HashMap();
		isHadTeacher = DataContext.getContext().doHexByIdToMap("hex_teacher_queryTeacherByTeacherId", "teacher_id="+teacherId);
		if(isHadTeacher == null || isHadTeacher.isEmpty()){
			return new DaoResult(-1,"该条数据不存在");
		}

		//执行并返回结果
		List<Map> paramList = new ArrayList<Map>();
		Map param = new HashMap();
		param.put("funcId", "hex_teacher_deleteTeacher");
		param.put("teacher_id",teacherId); 
		paramList.add(param);
		
		Map param2 = new HashMap();
		param2.put("funcId", "hex_teacher_deleteTeacherAndMajor");
		param2.put("teacher_id", teacherId);
		param2.put("major_id", majorId);
		paramList.add(param2);
		
		Response response = DataContext.getContext().doHexsByTransaction(null , paramList);
		DaoResult daoResult = new DaoResult(response.getFlag(),response.getMessage());
		return daoResult;
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub

	}

}
