/**
 * 用户信息Grid
 */
var userGrid;

/**
 * 用户信息Grid选择行
 */
var userGridSelectRow;

/**
 * 用户编辑窗口
 */
var userOperateDialog;

/**
 * 用户编辑表单
 */
var userOperateForm;

/**
 * 操作标识
 */
var operateFlag;

/**
 * 初始化页面控件
 */
$(document).ready(function(){
	userGrid = hait.getCompById("userGrid");
	userOperateDialog = hait.getCompById("userOperateDialog");
	userOperateForm = hait.getCompById("userOperateForm");
	
	// 表单加载完成后的操作
	userOperateForm.onafterload = function() {
		// 设置密码始终为未填写状态
		userOperateForm.setValue("login_pwd", "");
	};
	
	// 表单提交后的操作
	userOperateForm.onaftersubmit = function() {
		userOperateForm.getElement("login_pwd").setDisabled(false);
		userOperateDialog.hide();	// 隐藏弹出框
		userGrid.refresh();	// 刷新列表数据
	};
});

/**
 * 用户管理列表 单击行
 */
function userGridRowClick(row){
	userGridSelectRow = row;
}

/**
 * 弹出新增用户窗口
 */
function showAddUserDialog(){
	userOperateForm.getElement("login_name").setDisabled(false);
	userOperateForm.reset();
	userOperateDialog.setTitle("新增用户");
	userOperateDialog.show();
	operateFlag = "add";
}

/**
 * 弹出修改用户窗口
 */
function showModifyUserDialog(){
	if(userGridSelectRow == null || userGridSelectRow.length == 0){
		alert("您没有选择任何数据");
		return;
	}
	userOperateForm.reset();
	var params = {};
	params["user_id"] = userGridSelectRow["user_id"];
	userOperateForm.load("hex_user_queryUserByUserId",params);
	userOperateDialog.setTitle("修改用户");
	userOperateForm.getElement("login_name").setDisabled(true);
	userOperateDialog.show();
	operateFlag = "edit";
}

/**
 * 删除用户
 */
function doDeleteUser(){
	if (userGridSelectRow == null || userGridSelectRow.length == 0) {
		alert("您没有选择任何数据!");
		return;
	}
	if (!confirm("您确定要删除选中的数据？")) {
		return false;
	}

	// 执行删除操作
	var params = {};
	// 添加删除指令
	params["funcId"] = "hex_user_deleteUserFunction";
	params["user_id"] = userGridSelectRow["user_id"];

	// 执行这个操作
	request({
		data : [ params ],
		func : function(data) {
			if (data.responses[0].flag <= 0) {
				alert(data.responses[0].message);
				return;
			}
			userGrid.refresh();
			userGridSelectRow = null;
		}
	});
}


/**
 * 执行用户相关操作
 */
function saveUserData(){
	if(operateFlag == "add"){
		if(!confirm("您确定要添加此用户吗？")){
			return;
		}
		var params = {};
		userOperateForm.submit("hex_user_insertUserFunction",params);
	}
	if(operateFlag == "edit"){
		if(!confirm("您确定要修改此用户信息吗？")){
			return;
		}
		var userPwd = userOperateForm.getElement("login_pwd");
		if ($.trim(userPwd.getValue()) == ""){
			userPwd.setDisabled(true);
		}
		var params = {};
		params["user_id"] = userGridSelectRow["user_id"];
		userOperateForm.submit("hex_user_updateUserFunction",params);
	}
}