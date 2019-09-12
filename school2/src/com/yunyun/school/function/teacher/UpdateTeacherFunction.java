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
 * UpdateTeacherFunction
 * @description 更新教师function
 * @author XuTianYi
 * @creation time 2017年12月28日 下午5:16:22
 */
public class UpdateTeacherFunction implements IFunction {

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		/*----------获取所需参数------------*/
		String teacherId = StringUtil.convertObject(params.get("teacher_id")); //教师内部编号
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
		
		//进行逻辑操作
		List<Map> paramList = new ArrayList<Map>();
		Map param = new HashMap();
		param.put("funcId", "hex_teacher_updateTeacher");
		param.put("teacher_id", teacherId);
		param.put("teacher_no", teacherNo);
		param.put("teacher_name", teacherName);
		param.put("short_pinyin", phoneNumber);
		param.put("major_id", majorId);
		param.put("major_names", majorName);
		String operatorId = UserUtil.getUserId(params);
		String operatorName = UserUtil.getUserName(params);
		param.put("operator_id", operatorId);
		param.put("operator_name", operatorName);
		paramList.add(param);
		//删除原有用户专业关联
		param = new HashMap();
		param.put("funcId", "hex_teacher_deleteTeacherAndMajor");
		param.put("teacher_id", teacherId);
		param.put("major_id", majorId);
		paramList.add(param);
		//新增用户关联
		param = new HashMap();
		param.put("funcId", "hex_teacher_insertTeacherAndMajor");
		param.put("teacher_id", teacherId);
		param.put("major_id", majorId);
		paramList.add(param);



		//执行操作并返回结果
		Response response = DataContext.getContext().doHexsByTransaction(null, paramList);
		DaoResult daoResult = new DaoResult(response.getFlag(),response.getMessage());
		
		return null;
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub

	}

}
