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

/**
 * DeleteStudentFunction
 * @description 删除学生function
 * @author XuTianYi
 * @creation time 2017年12月22日 下午4:44:44
 */

public class DeleteStudentFunction implements IFunction{

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		/*------ 获取需要操作的参数 ------*/
		String studentId = StringUtil.convertObject(params.get("student_id"));	// 用户编号
		/*------------------------------*/
		
		
		/*------ 进行基础的数据判断 ------*/
		if (studentId.equals("")){
			return new DaoResult(-1, "请选择您要删除的学生");
		}
		
		// 查询要修改的用户是否存在
		Map isHadStudentMap = DataContext.getContext().doHexByIdToMap("hex_student_queryStudentByStudentId", "student_id=" + studentId);
		if (isHadStudentMap == null || isHadStudentMap.isEmpty()){
			return new DaoResult(-1, "您要删除的学生信息异常,请刷新后重试");
		}
		/*------------------------------*/
		
		
		/*------ 逻辑操作 ------*/
		// 涉及多个操作, 所以采用事务统一执行
		List<Map> paramList = new ArrayList<Map>();
		Map param = new HashMap();
		
		// 1. 删除用户信息
		param.put("funcId", "hex_student_deleteStudentByStudentId");
		param.put("student_id", studentId);
		paramList.add(param);
		
		// 2. 删除用户角色关联
		param = new HashMap();
		param.put("funcId", "hex_student_deleteStudentAndMajor");
		param.put("student_id", studentId);
		paramList.add(param);
		
		// 执行事务
		Response response = DataContext.getContext().doHexsByTransaction(null, paramList);
		/*------------------------------*/
		
		
		/*------ 返回执行结果 ------*/
		DaoResult daoresult = new DaoResult(response.getFlag(),response.getMessage());
		return daoresult;
		/*------------------------------*/
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
