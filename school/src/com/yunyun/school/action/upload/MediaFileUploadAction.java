package com.yunyun.school.action.upload;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.haitsoft.framework.core.context.HaitContext;
import com.haitsoft.framework.core.servlet.IAction;
import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.bean.Response;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.IDao;
import com.yunyun.school.util.StringUtil;

/**
 * 统一资源上传入口
 * 
 * @author pandong
 * @date 2016年10月21日 下午2:28:36
 * @copyright(c) yunlaila.com.cn
 */
public class MediaFileUploadAction implements IAction {
	/**
	 * 定义允许上传的文件扩展名，默认为以下
	 * 
	 */
	private String allowedAuffix = "jpg,png,gif,amr,mp4,zip,rar";

	protected final static long JPG_MAX_SIZE = 3000000;// 图片文件3M
	private final static long AMR_MAX_SIZE = 2000000;// 语音文件2M
	private final static long MP4_MAX_SIZE = 10000000;// 视频文件10M
	private final static long OTHER_MAX_SIZE = 10000000;// 其他文件10M

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 解析链接中的参数
		Map requestParams = this.analyzeURL(request.getQueryString());
		String token, currentUserId, currentUserType, resourcePurposeName, resourceFileName;

		// 获取参数
		try {
			token = requestParams.get("token").toString(); // 获取token
			resourcePurposeName = requestParams.get("purpose").toString();// 获得用途
			resourceFileName = requestParams.get("file_name") != null ? requestParams.get("file_name").toString() : null;// 获取资源名称
			String[] deTokenArr = StringUtil.decryptTokenToArray(token);
			currentUserId = deTokenArr[0]; // 当前用户编号
			currentUserType = deTokenArr[1]; // 当前用户类型
		} catch (Exception e) {
			e.printStackTrace();
			this.resultJSON(-1, "没有按照约定传递参数，请检查!", response);
			return;
		}

		// token有效性检查
//		TokenCheck check = TokenCheckFactory.getTokenCheck(token);
//		if (!check.isEnabled(token)) {
//			this.resultJSON(-1, "token无效，请检查！", response);
//			return;
//		}

		// 计算资源名称
		if (resourceFileName == null || resourceFileName.length() == 0) {
			resourceFileName = UUID.randomUUID().toString();
			resourceFileName = resourceFileName.replaceAll("-", "");// 去掉-号
		}
		// 如果传入的名称带点，那么去掉点后面的字符串
		if (resourceFileName.indexOf(".") >= 0) {
			resourceFileName = resourceFileName.substring(0, resourceFileName.indexOf("."));
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

		// 二进制文件上传开始
		// ==================================================================================================

		if (!ServletFileUpload.isMultipartContent(request)) {// 判断来的请求是否是文件上传请求
			resultJSON(-10300002, response);
			return;
		}

		// 检查目录
		File uploadDir = new File(savePath);
		if (!uploadDir.isDirectory()) {
			// 上传目录不存在则创建
			uploadDir.mkdirs();
		}

		// 检查目录写权限
		if (!uploadDir.canWrite()) {
			resultJSON(-10300003, response);
			return;
		}

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			this.resultJSON(-10300004, HaitContext.getMessageByCode(-10300004, e.getMessage()), response);
			return;
		}

		// 解析上传的数据
		Iterator itr = items.iterator();
		if (!itr.hasNext()) {
			this.resultJSON(-10300004, HaitContext.getMessageByCode(-10300004, "not element"), response);
			return;
		}
		FileItem item = (FileItem) itr.next();
		String fileName = item.getName();
		if (item.isFormField()) {
			this.resultJSON(-10300004, HaitContext.getMessageByCode(-10300004, "not file data"), response);
			return;
		}
		// 检查扩展名
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		if (!Arrays.<String> asList(allowedAuffix.split(",")).contains(fileExt)) {
			this.resultJSON(-10300006, HaitContext.getMessageByCode(-10300006, this.allowedAuffix), response);
			return;
		}

		// 检查文件大小
		long maxSize = -1;
		switch (fileExt) {
		case "jpg":
			maxSize = JPG_MAX_SIZE;
			break;
		case "amr":
			maxSize = AMR_MAX_SIZE;
			break;
		case "mp4":
			maxSize = MP4_MAX_SIZE;
			break;
		default:
			maxSize = OTHER_MAX_SIZE;
			break;
		}
		if (item.getSize() > maxSize) {
			this.resultJSON(-10300005, HaitContext.getMessageByCode(-10300005, maxSize / 1000000), response);
			return;
		}

		// 生成全新的文件名称
		String uploadFileName = resourceFileName + "." + fileExt;

		try {
			File uploadedFile = new File(savePath, uploadFileName);
			item.write(uploadedFile);
		} catch (Exception e) {
			this.resultJSON(-10300007, response);
			return;
		}
		// ====================================================================================
		// 二进制文件上传结束

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
	 * 解析字符串键值对参数
	 * 
	 * @param queryString
	 * @return
	 */
	private Map analyzeURL(String queryString) {
		Map params = new HashMap();
		if (queryString == null || queryString.length() == 0) {
			return params;
		}

		if (queryString.indexOf("?") != -1) {
			queryString = queryString.substring(queryString.indexOf("?") + 1);
		}

		String[] queryArray = queryString.split("&");
		for (int i = 0; i < queryArray.length; i++) {
			// 分离出其中的键值
			String key = queryArray[i].split("=")[0];
			String value = queryArray[i].split("=")[1];
			params.put(key, value);
		}

		return params;
	}

	/**
	 * 文件上传预处理
	 * 
	 * @param resourcePurposeName
	 *            图片用途
	 * @param currentUserType
	 *            当前用户类型
	 * @param currentUserId
	 *            当前用户ID
	 * @return
	 */
	public static Map fileSaveBefore(String resourcePurposeName, String currentUserType, String currentUserId) {

		// 根据系统参数获取以下参数
		String disk = DataContext.getSystemParam("CURRENT_STORAGE_DISK");// 获取存储盘符
		String outerUrl = DataContext.getSystemParam("DOMAIN_NAME"); // 获得对外域名

		// 先通过传入的资源用途名称获取资源用途相关信息
		Map purposeParam = new HashMap();
		purposeParam.put(IDao.QUERY_BY_CACHE, true);
		purposeParam.put(IDao.QUERY_BY_CACHE_TIME_OUT, 60 * 60 * 2);// 缓存设置为两小时
		purposeParam.put("resource_purpose_name", resourcePurposeName);
		DaoResult purposeDaoResult = DataContext.getContext().doHexById("hex_resource_purpose_query_by_terms", purposeParam);

		// 获得图片用途内容
		List purposeItem = purposeDaoResult.getItems();
		if (purposeItem.size() < 1) {
			return null;
		}
		Map purpose = (Map) purposeItem.get(0);
		String resourceFolder = (String) purpose.get("resource_folder");// 资源路径文件夹
		int isOnlyInt = Integer.parseInt((String) purpose.get("is_only"));// 是否唯一
		boolean isExpire = Integer.parseInt((String) purpose.get("is_expire")) == 1 ? true : false;// 是否过期
		int expireDay = Integer.parseInt((String) purpose.get("expire_day"));// 过期时间，单位天

		// 以上获得了所需的resource_purpose表中的内容，以下开始组装要往resource_user表中插入的数据
		SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
		SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentTime = new Date();
		String uploadTime = datetimeformat.format(currentTime);

		// 获得年份
		int year = Integer.parseInt(yearformat.format(currentTime));

		// 获得资源过期时间
		String expirationTime;
		Calendar ca = Calendar.getInstance();
		ca.setTime(currentTime);
		if (isExpire) {
			// 如果该资源是限时资源
			ca.add(Calendar.DATE, expireDay);
		} else {
			// 如果该资源不是限时资源
			ca.add(Calendar.YEAR, 100);
		}
		expirationTime = datetimeformat.format(ca.getTime());

		// 拼接内部地址
		String innerPath = "/" + disk + "/" + currentUserType + currentUserId + "/" + resourceFolder + "/" + year + "/";

		// 拼接本地文件保存地址
		String savePath = HaitContext.BASE_PATH + innerPath;

		// 拼接外部访问链接
		outerUrl += innerPath;

		// 组装正常返回的Map
		Map fileParams = new HashMap();
		fileParams.put("savePath", savePath);
		fileParams.put("isOnlyInt", isOnlyInt);
		fileParams.put("resourcePurposeName", resourcePurposeName);
		fileParams.put("outerUrl", outerUrl);
		fileParams.put("innerPath", innerPath);
		fileParams.put("resourceFolder", resourceFolder);
		fileParams.put("year", year);
		fileParams.put("uploadTime", uploadTime);
		fileParams.put("expirationTime", expirationTime);
		return fileParams;
	}

	/**
	 * 文件上传的后处理，主要是进行数据库添加操作
	 * 
	 * @param resourcePurposeName
	 *            资源用途
	 * @param currentUserType
	 *            用户类型
	 * @param currentUserId
	 *            用户编号
	 * @param fileParams
	 *            文件参数
	 */
	public static void fileSaveAfter(String resourcePurposeName, String currentUserType, String currentUserId, Map fileParams) {

		// 获得参数
		int isOnlyInt = (int) fileParams.get("isOnlyInt");// 是否唯一
		String innerPath = (String) fileParams.get("innerPath");// 内部路径
		String outerUrl = (String) fileParams.get("outerUrl");// 获取外部路径
		String resourceFolder = (String) fileParams.get("resourceFolder");// 资源路径
		int year = (int) fileParams.get("year");// 年份
		String uploadTime = (String) fileParams.get("uploadTime");// 上传时间
		String expirationTime = (String) fileParams.get("expirationTime");// 过期时间

		Map params = new HashMap();
		// 如果传入的资源用途是唯一的，做如下操作
		if (isOnlyInt == 1) {
			// 组装需要做唯一性检查的参数放入执行队列中去
			params = new HashMap();
			params.put("user_type", currentUserType);
			params.put("user_id", currentUserId);
			params.put("resource_purpose_name", resourcePurposeName);
			// 删除符合唯一条件的图片
			List fileList = DataContext.getContext().doHexByIdToList("hex_resource_user_query_by_user_and_purpose", params);
			for (int i = 0; i < fileList.size(); i++) {
				Map fileMap = (Map) fileList.get(i);
				String filePath = HaitContext.BASE_PATH + fileMap.get("inner_path");
				try {
					File file = new File(filePath);
					file.delete();
				} catch (Exception e) {
					HaitContext.getLogger().warn("MediaFileUploadAction", "删除其他唯一文件[" + filePath + "]时失败，原因可能是：" + e.getMessage());
				}
			}
			// 删除数据库记录
			DataContext.getContext().doHexById("hex_resource_user_delete_by_user_and_purpose", params);
		}

		params = new HashMap();
		params.put("resource_id", DataContext.getContext().getSequenceId());
		params.put("user_type", currentUserType);
		params.put("user_id", currentUserId);
		params.put("outer_url", outerUrl);
		params.put("inner_path", innerPath);
		params.put("resource_folder", resourceFolder);
		params.put("resource_purpose_name", resourcePurposeName);
		params.put("is_only", isOnlyInt);
		params.put("year", year);
		params.put("upload_time", uploadTime);
		params.put("expiration_time", expirationTime);
		// 删除数据库记录
		DataContext.getContext().doHexById("hex_resource_user_insert", params);
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
