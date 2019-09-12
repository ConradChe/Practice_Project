package com.yunyun.school.action.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.haitsoft.framework.core.servlet.IAction;
import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.bean.Request;
import com.haitsoft.framework.data.bean.Response;
import com.haitsoft.framework.data.context.DataContext;
import com.yunyun.school.action.system.LoginUserEntity;
import com.yunyun.school.util.SystemConstantUtil;

/**
 * 前端数据访问入口Action
 * @author Abby.Wang
 * @description 
 * @code by 2017年12月17日 下午9:37:45
 */
public class ManageDataAction implements IAction {

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LoginUserEntity loginUserEntity = SystemConstantUtil.getCurUserEntityByCache(request);
		// 如果当前用户为空，那么说明还没有登录
		if (null == loginUserEntity) {
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write(new Response(-1, "您还没有登陆，不能使用该功能!").asJSON());
			response.flushBuffer();
			return;
		}
		Map currentUser = loginUserEntity.toMap();
		if (currentUser == null) {
			currentUser = new HashMap();
		}
		
		// 接受并解析前端请求
		Request helomeRequest = Request.parse(request.getInputStream());
		Response helomeResponse = new Response(1, "success");
		DataContext dataContext = DataContext.getContext();

		for (Map paramMap : helomeRequest.getRequests()) {
			String funcId = (String) paramMap.get("funcId");
			if (funcId == null) {
				helomeResponse.getResults().add(new DaoResult(-10200001));
				continue;
			}
			// 将用户登录信息统一绑定到请求参数中
			dataContext.bindUser(paramMap, currentUser);

		}

		// 准备执行操作，这里存在一种特殊情况，全部都是jdbc，并且要求事务执行，那么先做特殊
		Map global = helomeRequest.getGlobal();
		if (global.get("transaction") != null && global.get("transaction").toString().equals("true")) {
			helomeResponse = dataContext.doHexsByTransaction(global, helomeRequest.getRequests());
			// 将结果使用JSON方式返回
			response.setContentType("text/json; charset=UTF-8");
			response.getWriter().write(helomeResponse.asJSON());
			response.flushBuffer();
			return;
		}

		// 批量执行Hex操作，此种情况下不存在执行失败的问题，因为执行结果都分散在各个DaoResult中
		for (Map paramMap : helomeRequest.getRequests()) {
			String funcId = (String) paramMap.get("funcId");
			DaoResult daoResult = dataContext.doHexById(funcId, paramMap);
			helomeResponse.getResults().add(daoResult);
			// 如果任何业务发生问题，那么都返回错误
			if (daoResult.getFlag() < 0) {
				helomeResponse.setFlag(-1);
				helomeResponse.setMessage("fail");
			}
		}

		// 根据情况返回相应的数据
		String resultType = helomeRequest.getGlobal().get("resultType");
		resultType = resultType != null ? resultType : "json";
		if ("json".equals(resultType)) {
			response.setContentType("text/json; charset=UTF-8");
			response.getWriter().write(helomeResponse.asJSON());
		} else {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().write(helomeResponse.asXML());
		}
		response.flushBuffer();
	}

}
