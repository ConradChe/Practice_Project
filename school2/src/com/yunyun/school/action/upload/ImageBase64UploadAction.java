package com.yunyun.school.action.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloopen.rest.sdk.utils.encoder.BASE64Decoder;
import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.bean.Response;
import com.yunyun.school.util.StringUtil;

public class ImageBase64UploadAction extends MediaFileUploadAction {

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter("token"); // 获得登陆标示
		String base64Content = request.getParameter("base64_content");// 文件内容base64形式
		String imageName = request.getParameter("image_name");// 文件名称
		String imageType = request.getParameter("image_type");// 文件类型
		String resourcePurposeName = request.getParameter("purpose"); // 用途

		String currentUserId, currentUserType;

		// 解析用户类型和编号
		try {
			String[] deTokenArr = StringUtil.decryptTokenToArray(token);
			currentUserId = deTokenArr[0]; // 当前用户编号
			currentUserType = "user"; // 当前用户类型
		} catch (Exception e) {
			e.printStackTrace();
			super.resultJSON(-1, "没有按照约定传递参数，请检查!", response);
			return;
		}

		// token有效性检查
//		TokenCheck check = TokenCheckFactory.getTokenCheck(token);
//		if (!check.isEnabled(token)) {
//			super.resultJSON(-1, "token无效，请检查！", response);
//			return;
//		}

		// 计算资源名称
		String resourceFileName = imageName;
		if (resourceFileName == null || resourceFileName.length() == 0) {
			resourceFileName = UUID.randomUUID().toString();
			resourceFileName = resourceFileName.replaceAll("-", "");// 去掉-号
		}
		// 如果传入的名称带点，那么去掉点后面的字符串
		if (resourceFileName.indexOf(".") >= 0) {
			resourceFileName = resourceFileName.substring(0, resourceFileName.indexOf("."));
		}

		// 计算文件的后缀名
		String suffix = null;
		switch (imageType) {
		case "image/jpeg":
			suffix = "jpg";
			break;
		case "image/png":
			suffix = "png";
			break;
		case "image/gif":
			suffix = "gif";
			break;
		}

		if (suffix == null || suffix.length() == 0) {
			super.resultJSON(-1, "不能识别的文件格式，请检查", response);
			return;
		}

		// 进行上传预处理，获得文件参数集合
		Map fileParams = MediaFileUploadAction.fileSaveBefore(resourcePurposeName, currentUserType, currentUserId);
		if (fileParams == null) {
			this.resultJSON(-1, "获取图片用途信息失败，请重试！", response);
			return;
		}

		String savePath = (String) fileParams.get("savePath");
		String innerPath = (String) fileParams.get("innerPath");
		String outerUrl = (String) fileParams.get("outerUrl");

		// base64图片上传开始
		// ==================================================================================================
		// 拼接图片全称
		String uploadFileName = resourceFileName + "." + suffix;

		// 将文件存入服务器磁盘
		try {
			parseBase64Image(base64Content, savePath + uploadFileName);
		} catch (Exception e) {
			e.printStackTrace();
			this.resultJSON(-1, "文件上传失败，请重试！", response);
			return;
		}

		// ====================================================================================
		// base64图片上传结束

		// 文件存入服务器成功后需要向数据库中保存书库，这里通过新线程完成
		outerUrl += uploadFileName;
		innerPath += uploadFileName;

		// 重写外部路径和内部路径
		fileParams.put("outerUrl", outerUrl);
		fileParams.put("innerPath", innerPath);

		// 文件上传的后处理，主要是进行数据库添加操作
		MediaFileUploadAction.fileSaveAfter(resourcePurposeName, currentUserType, currentUserId, fileParams);

		Response haitResponse = new Response();
		haitResponse.getResults().add(new DaoResult(1, outerUrl));
		haitResponse.setFlag(1);
		haitResponse.setMessage("上传成功");
		// 将结果使用HTML方式返回
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().write(haitResponse.asJSON());
		response.flushBuffer();
	}

	/**
	 * 将base64编码字符串转换为图片
	 * 
	 * @param imgStr
	 * @param path
	 * @return
	 */
	public static boolean parseBase64Image(String imgStr, String path) {
		if (imgStr == null) {
			return false;
		}

		// 获取文件夹路径
		String folder = path.substring(0, path.lastIndexOf("/"));
		File dir = new File(folder);
		if (!dir.exists()) {// 路径不存在则创建
			dir.mkdirs();
		}

		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// 解密
			byte[] b = decoder.decodeBuffer(imgStr);
			// 处理数据
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			OutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
