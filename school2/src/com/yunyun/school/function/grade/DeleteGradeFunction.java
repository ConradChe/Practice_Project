package com.yunyun.school.function.grade;

import java.util.HashMap;
import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;
import com.yunyun.school.util.StringUtil;

/**
 * DeleteGradeFunction
 * @description 删除班级function
 * @author XuTianYi
 * @creation time 2017年12月26日 下午4:47:01
 */

public class DeleteGradeFunction implements IFunction {

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		/*----------获取所需参数-----------*/
		String gradeId = StringUtil.convertObject(params.get("grade_id"));
		/*------------------------------*/
		
		/*-----------基础判断-------------*/
		Map isHadClass = new HashMap();
		isHadClass = DataContext.getContext().doHexByIdToMap("hex_class_queryClassById", "grade_id="+gradeId);
		if(isHadClass.isEmpty() || isHadClass == null){
			return new DaoResult(-1,"所选择的班级数据不存在");
		}
		/*------------------------------*/
		
		/*----------逻辑操作----------*/
		return DataContext.getContext().doHexById("hex_class_deleteGradeById", "grade_id=" + gradeId);
		
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub

	}

}
