package com.yunyun.school.action.upload;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.haitsoft.framework.core.context.HaitContext;
import com.haitsoft.framework.core.servlet.IAction;
import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.bean.Response;
import com.haitsoft.framework.data.context.DataContext;
import com.yunyun.school.util.StringUtil;

/**
 * 统一资源删除入口
 * 
 * @author Dubingxin
 * 
 * 
 */
public class MediaFileDeleteAction implements IAction {

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 解析链接中的参数
		String outerUrl = request.getParameter("outer_url");
		String token = request.getParameter("token");

		if (outerUrl == null || token == null) {
			this.resultJSON(-1, "未传入约定的参数！", response);
			return;
		}

		String currentUserId, currentUserType;

		// 获取用户信息
		String[] deTokenArr = StringUtil.decryptTokenToArray(token);
		currentUserId = deTokenArr[0]; // 当前用户编号
		currentUserType = deTokenArr[1]; // 当前用户类型

//		TokenCheck check = TokenCheckFactory.getTokenCheck(token);
//		if (!check.isEnabled(token)) {
//			this.resultJSON(-1, "token无效，请检查！", response);
//			return;
//		}
		// 获取参数内的文件完整外部路径,并检查该文件在数据库中是否存在
		DataContext dataCon = DataContext.getContext();

		// 这里只能是实时查询
		Map params = new HashMap();
		params.put("outer_url", outerUrl);
		params.put("student_id", currentUserId);
		params.put("user_type", currentUserType);
		Map resource = dataCon.doHexByIdToMap("hex_resource_user_query_by_outerurl", params);

		// 如果没有找到，直接返回空
		if (resource == null) {
			resultJSON(-1, "不存在该资源，请检查传递的资源路径!", response);
			return;
		}

		// 如果toke类型不是平台员工，就要判断是否是当前用户，只能自己才能删除自己的图片
//		if (!TokenCheckFactory.EMP_TOKEN_TYPE.equalsIgnoreCase(currentUserType)) {
//			String resourUserId = resource.get("student_id").toString();
//			String resourUserType = resource.get("user_type").toString();
//			if (!currentUserId.equalsIgnoreCase(resourUserId) || !currentUserType.equals(resourUserType)) {
//				// 如果token中的用户ID和数据库中存储的用户ID不同
//				resultJSON(-1, "您没有操作该资源的权限，请检查!", response);
//				return;
//			}
//		}

		// 如果当前用户有该资源的操作权限，先操作文件，再删除数据库内容
		// 拼接出要删除的文件所在的路径
		String deletePath = HaitContext.BASE_PATH + resource.get("inner_path").toString();

		// 准备在本地删除该文件
		File deleteFile = new File(deletePath);
		if (!deleteFile.exists()) {
			resultJSON(-1, "该文件不存在", response);
			return;
		}
		if (!deleteFile.canWrite()) {
			resultJSON(-1, "该文件被写锁定，请检查文件写保护状态", response);
			return;
		}

		// 删除磁盘上的文件
		try {
			deleteFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
			this.resultJSON(-1, "删除文件失败!", response);
			return;
		}

		// 删除数据库中的记录
		DaoResult daoResult = DataContext.getContext().doHexById("hex_resource_user_delete_by_outerurl", params);
		if (daoResult.getFlag() <= 0) {
			this.resultJSON(daoResult, response);
			return;
		}

		// 删除完成，返回
		resultJSON(1, "文件删除成功", response);
	}

	protected void resultJSON(long flag, HttpServletResponse response) throws IOException {
		resultJSON(new DaoResult(flag, HaitContext.getMessageByCode(flag)), response);
	}

	protected void resultJSON(long flag, String message, HttpServletResponse response) throws IOException {
		resultJSON(new DaoResult(flag, message), response);
	}

	protected void resultJSON(DaoResult daoResult, HttpServletResponse response) throws IOException {
		Response haitResponse = new Response();
		haitResponse.getResults().add(daoResult);
		haitResponse.setFlag(daoResult.getFlag());
		haitResponse.setMessage(daoResult.getMessage());
		// 将结果使用HTML方式返回
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().write(haitResponse.asJSON());
		response.flushBuffer();
	}

}
