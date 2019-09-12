/**
 * 教师分组列表
 */
var teacherAndMajorGrid;

/**
 * 当前选中行
 */
var teacherAndMajorGridSelectRow;

/**
 * 新增教师弹出框
 */
var teacherOperateDialog;

/**
 * 修改教师弹出框
 */
var teacherUpdateDialog;

/**
 * 修改教师表单
 */
var teacherUpdateForm;

/**
 * 教师信息编辑表单
 */
var teacherOperateForm;

/**
 * 操作标识
 */
var operateFlag="";

$(document).ready(function(){
	//初始化页面控件
	teacherAndMajorGrid = hait.getCompById("teacherAndMajorGrid");
	teacherOperateDialog = hait.getCompById("teacherOperateDialog");
	teacherOperateForm = hait.getCompById("teacherOperateForm");
	teacherUpdateDialog = hait.getCompById("teacherUpdateDialog");
	teacherUpdateForm = hait.getCompById("teacherUpdateForm");
	
	// 当表单提交请求完毕后执行的操作
	teacherOperateForm.onaftersubmit = function() {
		teacherOperateDialog.hide();	// 隐藏弹出框
		teacherAndMajorGrid.refresh();	// 刷新列表数据
	};
	
	teacherAndMajorGrid.refresh();

});

/**
 * 根据条件查询教师
 */
function searchTeacherByConditions(){
	//初始化查询参数
	teacherAndMajorGrid.params = {};
	
	var searchColumn = hait.getCompById("searchColumn").getValue();
	var searchValue = hait.getCompById("searchValue").getValue();
	if(searchValue != null && searchValue.length>0 && searchColumn != "0"){
		teacherAndMajorGrid.params[searchColumn] = searchValue;
	}
	
	//刷新列表页面
	teacherAndMajorGrid.refresh();

}

/**
 * 教师信息列表Grid-单击行
 */
function teacherGridRowClick(row){
	teacherAndMajorGridSelectRow = row;
}

/**
 * 弹出增加教师窗口
 */
function showAddTeacherDialog(){
	teacherOperateForm.reset();
	teacherOperateDialog.setTitle("添加新教师");
	teacherOperateDialog.show();
	operateFlag = "add";
}

/**
 * 弹出修改教师窗口
 */
function showUpdateTeacherDialog(){
	if(teacherAndMajorGridSelectRow == null || teacherAndMajorGridSelectRow.length == 0){
		alert("您没有选择任何数据");
		return;
	}
	teacherUpdateForm.reset();
	var params = {};
	params["teacher_id"] = teacherAndMajorGridSelectRow["teacher_id"];
	teacherUpdateForm.load("hex_teacher_queryTeacherByTeacherId",params);
	teacherUpdateDialog.show();
	teacherUpdateForm.getElement("teacher_no").setDisabled(true);
	operateFlag = "edit";
}

/**
 * 执行相关操作
 */
function saveTeacherData(){
	if(operateFlag == "add"){
		if(!confirm("您确定要添加此教师吗？")){
			return;
		}
		var params = {};
		teacherOperateForm.submit("hex_teacher_insertTeacherFunction",params);
	}
	else if(operateFlag =="edit"){
		if(!confirm("您确定要修改此教师吗？")){
			return;
		}
		var params = {};
		teacherUpdateForm.submit("hex_teacher_updateTeacherFunction",params);
	}
	
}

/**
 * 新增教师设置major_name
 */
function setMajorName(obj,preval,val,option){
	var majorNameObj = hait.getCompById("setMajorName");
	var majorNameText = option["text"];
	majorNameObj.setValue(majorNameText);
}

/**
 * 更新教师设置major_name
 */
function setMajorName2(obj,preval,val,option){
	var majorNameObj = hait.getCompById("updateMajorName");
	var majorNameText = option["text"];
	majorNameObj.setValue(majorNameText);
	
}

/**
 * 删除教师
 */
function doDeleteTeacher(){
	if (teacherAndMajorGridSelectRow == null || teacherAndMajorGridSelectRow.length == 0) {
		alert("您没有选择任何数据!");
		return;
	}
	if (!confirm("您确定要删除选中的数据？")) {
		return false;
	}

	// 执行删除操作
	var params = {};
	// 添加删除指令
	params["funcId"] = "hex_teacher_deleteTeacherFunction";
	params["major_id"] = teacherAndMajorGridSelectRow["major_ids"];
	params["teacher_id"] = teacherAndMajorGridSelectRow["teacher_id"];
	
	// 执行这个操作
	request({
		data : [ params ],
		func : function(data) {
			if (data.responses[0].flag <= 0) {
				alert(data.responses[0].message);
				return;
			}
			teacherAndMajorGrid.refresh();
			teacherAndMajorGridSelectRow = null;
		}
	});
}
