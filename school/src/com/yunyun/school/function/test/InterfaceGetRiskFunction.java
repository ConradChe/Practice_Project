package com.yunyun.school.function.test;

//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;

/**
 * 
 * Title:InterfaceGetRiskFunction 
 * Description:接口获取预警消息
 * @author long JunTao smile_jt@qq.com
 * @date 2018年2月6日 上午9:31:49
 */
public class InterfaceGetRiskFunction implements IFunction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public DaoResult doFunction(Map params, Map context) throws Exception {

		DaoResult daoResult;

		// 时间格式化

		// 获取消息类型
		String getMsgKind =(String)params.get("get_msg_type");

		HashMap map = new HashMap();
		map.put("id", "1");

		// 查询该保险公司的预警消息
		daoResult = DataContext.getContext().doHexById("hex_risk_company_getRiskByCompanyIdData", map);

		// 返回查询结果
		return daoResult;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void doRollback(Map arg0) {
		// TODO Auto-generated method stub

	}

}
