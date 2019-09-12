/**
 * 学生信息列表
 */
var studentGrid;

/**
 * 学生信息列表当前选中行
 */
var studentGridSelectRow;

/**
 * 学生信息编辑弹出框(修改/新增)
 */
var studentOperateDialog;

/**
 * 学生信息编辑表单(修改/新增)
 */
var studentOperateForm;

/**
 * 操作标识
 */
var operateFlag="";

$(document).ready(function(){
	//初始化页面控件，根据编号获取已经生成的hait组件
	studentGrid = hait.getCompById("studentGrid");
	studentOperateDialog = hait.getCompById("studentOperateDialog");
	studentOperateForm = hait.getCompById("studentOperateForm");
	
	// 当表单提交请求完毕后执行的操作
	studentOperateForm.onaftersubmit = function() {
		studentOperateDialog.hide();	// 隐藏弹出框
		studentGrid.refresh();	// 刷新列表数据
	};
});

/**
 * 学生信息列表Grid-单击行
 */
function studentGridRowClick(row){
	studentGridSelectRow = row;
}

/**
 * 根据条件查询学生
 */
function searchStudentByConditions(){
	//初始化查询参数
	studentGrid.params = {};
	
	var searchColumn = hait.getCompById("searchColumn").getValue();
	var searchValue = hait.getCompById("searchValue").getValue();
	if(searchValue != null && searchValue.length>0 && searchColumn != "0"){
		studentGrid.params[searchColumn] = searchValue;
	}
	
	var startTime = hait.getCompById("startTime").getValue();
	var endTime = hait.getCompById("endTime").getValue();
	if(startTime != null && startTime.length>0){
		studentGrid.params["start_time"] = startTime;
		studentGrid.params["end_time"] = endTime;
	}
	
	var studentType = hait.getCompById("studentType").getValue();
	if(studentType != null && studentType != "" && studentType != "0"){
		studentGrid.params["student_type"] = studentType;
	}
	
	var majorNameObj = hait.getCompById("majorName");
	var majorName = majorNameObj.getValue();
	if(majorName != null && majorName != "" && majorName != "全部"){
		studentGrid.params["major_names"] = majorName;
	}
	
	//刷新列表页面
	studentGrid.refresh();

}

/**
 * 弹出增加学生窗口
 */
function showAddStudentDialog(){
	studentOperateForm.reset();
	studentOperateDialog.setTitle("添加新学生");
	studentOperateDialog.show();
	operateFlag = "add";
}

/**
 * 弹出修改学生窗口
 */
function showModifyStudentDialog(){
	if (studentGridSelectRow == null || studentGridSelectRow.length == 0) {
		alert("您没有选择任何数据!");
		return;
	}
	studentOperateForm.reset();
	var params = {};
	params["student_id"] = studentGridSelectRow["student_id"];
	studentOperateForm.load("hex_student_queryStudentByStudentId",params);
	studentOperateDialog.setTitle("修改用户信息");
	studentOperateForm.getElement("student_no").setDisabled(true);
	studentOperateDialog.show();
	operateFlag = "edit";
}

/**
 * 删除学生
 */
function doDeleteStudent(){
	if (studentGridSelectRow == null || studentGridSelectRow.length == 0) {
		alert("您没有选择任何数据!");
		return;
	}
	if (!confirm("您确定要删除选中的数据？")) {
		return false;
	}

	// 执行删除操作
	var params = {};
	// 添加删除指令
	params["funcId"] = "hex_student_deleteStudentFunction";
	params["student_id"] = studentGridSelectRow["student_id"];

	// 执行这个操作
	request({
		data : [ params ],
		func : function(data) {
			if (data.responses[0].flag <= 0) {
				alert(data.responses[0].message);
				return;
			}
			studentGrid.refresh();
			studentGridSelectRow = null;
		}
	});
}

/**
 * 执行用户操作(添加/修改)
 */
function saveUserData(){
	if(operateFlag == "add"){
		if(!confirm("您确定要添加此学生吗？")){
			return;
		}
		var params = {};
		studentOperateForm.submit("hex_student_insertStudentFunction",params);
	}
	if(operateFlag == "edit"){
		if(!confirm("您确定要修改此学生信息吗？")){
			return;
		}
		var params = {};
		params["student_id"] = studentGridSelectRow["student_id"];
		userOperateForm.submit("hex_student_updateStudentFunction",params);
	}
}

/**
 * 设置major_name
 */
function setMajorName(obj,preval,val,option){
	var majorNameObj = hait.getCompById("major_names");
    var majorNameText = option["text"];
	majorNameObj.setValue(majorNameText);
}

/**
 * 设置teacher_id
 */
function setTeacherId(obj,preval,val,option){
	var teacherIdObj = hait.getCompById("teacher_id");
    var teacherIdText = option["teacher_id"];
    teacherIdObj.setValue(teacherIdText);
}


