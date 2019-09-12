package com.yunyun.school.function.student;

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
 * InsertStudentFunction
 * @description 新增学生function
 * @author XuTianYi
 * @creation time 2017年12月21日 上午10:48:26
 */

public class InsertStudentFunction implements IFunction{

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		/*----------获取所需参数------------*/
		String studentNo = params.get("student_no").toString(); //学生学号
		String studentName = params.get("student_name").toString(); //学生姓名
		String phoneNumber = params.get("phone_number").toString(); //手机号码
		int studentType = StringUtil.convertObjectToInt(params.get("student_type")); //学生类别
		String studentPhoto = StringUtil.convertObject(params.get("student_photo")); //学生照片
		String majorId = params.get("major_ids").toString(); //专业ID
		String majorName = params.get("major_names").toString(); //专业名称
		String teacherId = params.get("teacher_id").toString(); //导师ID
		String classId = params.get("class_id").toString(); //班级ID
		String admissionDate = params.get("admission_date").toString(); //入学时间
		/*-------------------------------*/
		
		/*----------进行基础判断------------*/
		if(studentNo.equals("")){
			return new DaoResult(-1,"学号不能为空");
		}
		
		if(studentName.equals("")){
			return new DaoResult(-1,"学生姓名不能为空");
		}
		
		if(phoneNumber.equals("")){
			return new DaoResult(-1,"手机号码不能为空");
		}
		
		if(studentType == 0){
			return new DaoResult(-1,"请选择学生类型");
		}
		
		if(majorId.equals("")){
			return new DaoResult(-1,"请选择专业");
		}
		
		if(teacherId.equals("")){
			return new DaoResult(-1,"请选择导师");
		}

		if(classId.equals("")){
			return new DaoResult(-1,"请选择班级");
		}
		
		if(admissionDate.equals("")){
			return new DaoResult(-1,"入学时间不能为空");
		}
		/*-------------------------------*/
		
		//查询学号是否重复
		Map studentMap = DataContext.getContext().doHexByIdToMap("hex_student_queryStudentByNo","student_no="+studentNo );
		if(studentMap!=null && !studentMap.isEmpty()){
			return new DaoResult(-1,"您输入的学号已存在，请检查");
		}

		/*------------逻辑操作-------------*/
		List<Map> paramList = new ArrayList<Map>();
		Map param = new HashMap();
		
		String studentId = DataContext.getContext().getSequenceId("s");
		param.put("funcId", "hex_student_insertStudent");
		param.put("student_id", studentId);
		param.put("student_no", studentNo);
		param.put("student_name", studentName);
		param.put("phone_number", phoneNumber);
		param.put("student_type", studentType);
		param.put("student_photo", studentPhoto);
		param.put("major_ids", majorId);
		param.put("major_names", majorName);
		param.put("teacher_id", teacherId);
		param.put("class_id", classId);
		param.put("admission_date", admissionDate);
		String operaterId = UserUtil.getUserId(params);
		String operaterName = UserUtil.getUserName(params);
		param.put("operator_id", operaterId);
		param.put("operator_name", operaterName);

		paramList.add(param);
		
		//添加关联表信息
		Map param2 = new HashMap();
		param2.put("funcId", "hex_student_insertStudentAndMajor");
		param2.put("student_id", studentId);
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
