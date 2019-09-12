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
import com.yunyun.school.util.UserUtil;

/**
 * InsertTeacherFunction
 * @description 新增教师function
 * @author XuTianYi
 * @creation time 2017年12月25日 下午3:43:14
 */

public class InsertTeacherFunction implements IFunction{

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		/*----------获取所需参数------------*/
		String teacherNo = StringUtil.convertObject(params.get("teacher_no")); //教师工号
		String teacherName = StringUtil.convertObject(params.get("teacher_name")); //教师姓名
		String shortPinyin = StringUtil.convertObject(params.get("short_pinyin")); //姓名短拼
		String phoneNumber = StringUtil.convertObject(params.get("phone_number")); //电话号码
		String majorId = StringUtil.convertObject(params.get("major_ids")); //专业ID
		String majorName = StringUtil.convertObject(params.get("major_names")); //专业名称
		/*-------------------------------*/
		
		/*----------进行基础判断------------*/
		if(teacherNo.equals("")){
			return new DaoResult(-1,"工号不能为空");
		}
		
		if(teacherName.equals("")){
			return new DaoResult(-1,"教师姓名不能为空");
		}
		
		if(phoneNumber.equals("")){
			return new DaoResult(-1,"手机号码不能为空");
		}
		
		if(shortPinyin.equals("")){
			return new DaoResult(-1,"姓名短拼不能为空");
		}
		
		if(majorId.equals("")){
			return new DaoResult(-1,"请选择专业");
		}
		
		if(majorName.equals("")){
			return new DaoResult(-1,"请选择专业名称");
		}
		/*-------------------------------*/
		
		//查询工号是否重复
		Map teacherMap = DataContext.getContext().doHexByIdToMap("hex_teacher_queryTeacherByNo","teacher_no="+teacherNo);
		if(teacherMap!=null && !teacherMap.isEmpty()){
			return new DaoResult(-1,"您输入的工号已存在，请检查");
		}
		
		/*------------逻辑操作-------------*/
		List<Map> paramList = new ArrayList<Map>();
		Map param = new HashMap();
		
		String teacherId = DataContext.getContext().getSequenceId("t");
		param.put("funcId", "hex_teacher_insertTeacher");
		param.put("teacher_id", teacherId);
		param.put("teacher_no", teacherNo);
		param.put("teacher_name", teacherName);
		param.put("short_pinyin", shortPinyin);
		param.put("phone_number", phoneNumber);
		param.put("major_ids", majorId);
		param.put("major_names", majorName);
		String operaterId = UserUtil.getUserId(params);
		String operaterName = UserUtil.getUserName(params);
		param.put("operator_id", operaterId);
		param.put("operator_name", operaterName);

		paramList.add(param);
		
		//添加关联表信息
		Map param2 = new HashMap();
		param2.put("funcId", "hex_teacher_insertTeacherAndMajor");
		param2.put("teacher_id", teacherId);
		param2.put("major_id", majorId);
		
		paramList.add(param2);
		/*------------执行事物-------------*/
		Response response = DataContext.getContext().doHexsByTransaction(null, paramList);
		
		/*------------返回结果-------------*/
		DaoResult daoResult = new DaoResult(response.getFlag(),response.getMessage());
		
		return daoResult;

	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
