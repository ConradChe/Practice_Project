package com.yunyun.school.function.major;

import java.util.HashMap;
import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;
import com.yunyun.school.util.StringUtil;

/**
 * InsertMajorFunction
 * @description 新增专业function
 * @author XuTianYi
 * @creation time 2017年12月27日 上午11:57:02
 */

public class InsertMajorFunction implements IFunction {

	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {
		//获取所需参数
		String majorName = StringUtil.convertObject(params.get("major_name"));      //专业名
		String shortPinyin = StringUtil.convertObject(params.get("short_pinyin"));  //拼音缩写
		
		//进行基础判断
		if(majorName.equals("")){
			return new DaoResult(-1,"专业名称不能为空");
		}
		
		if(shortPinyin.equals("")){
			return new DaoResult(-1,"拼音缩写不能为空");
		}
		
		//查询增加专业是否已经存在
		Map isHadMajor = new HashMap();
		isHadMajor = DataContext.getContext().doHexByIdToMap("hex_major_queryMajorByMajorName", "major_name="+majorName);
		if(isHadMajor != null && !isHadMajor.isEmpty()){
			return new DaoResult(-1,"该专业已存在！");
		}
		
		//进行逻辑操作
		Map param = new HashMap();
		
		String majorId = DataContext.getContext().getSequenceId("");
		param.put("major_id", majorId);
		param.put("major_name", majorName);
		param.put("short_pinyin", shortPinyin);
		
		//执行并返回结果
		DaoResult daoResult = DataContext.getContext().doHexById("hex_major_insertMajor", param);
		return daoResult;
	}

	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub

	}

}
