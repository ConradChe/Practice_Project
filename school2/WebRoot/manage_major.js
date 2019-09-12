/**
 *	专业信息列表
 */
var majorGrid;

/**
 * 	专业信息列表当前选中行
 */
var majorGridSelectRow;

/**
 *	专业编辑窗口
 */
var majorOperateDialog;

/**
 *	专业编辑表单
 */
var majorOperateForm;

/**
 *	操作标识
 */
var operateFlag;

$(document).ready(function(){
	//初始化页面控件
	majorGrid = hait.getCompById("majorGrid");
	majorOperateDialog = hait.getCompById("majorOperateDialog");
	majorOperateForm = hait.getCompById("majorOperateForm");
	
	// 当表单提交请求完毕后执行的操作
	majorOperateForm.onaftersubmit = function() {
		majorOperateDialog.hide();	// 隐藏弹出框
		majorGrid.refresh();	// 刷新列表数据
	};
});

/**
 * 专业信息列表Grid-单击行
 */
function majorGridRowClick(row){
	majorGridSelectRow = row;
}

/**
 * 弹出新增专业窗口
 */
function showAddMajorDialog(){
	majorOperateForm.reset();
	majorOperateDialog.setTitle("新增专业");
	majorOperateDialog.show();
	operateFlag = "add";
}

/**
 * 弹出修改专业窗口
 */
function showModifyMajorDialog(){
	if(majorGridSelectRow == null || majorGridSelectRow.length == 0){
		alert("您没有选择任何数据");
		return;
	}
	majorOperateForm.reset();
	var params = {};
	params["major_id"] = majorGridSelectRow["major_id"];
	majorOperateForm.load("hex_major_queryMajorByMajorId",params);
	majorOperateDialog.show();
	majorOperateDialog.setTitle("修改专业");
	operateFlag = "edit";
}

/**
 * 删除专业
 */
function doDeleteMajor(){
	if (majorGridSelectRow == null || majorGridSelectRow.length == 0) {
		alert("您没有选择任何数据!");
		return;
	}
	if (!confirm("您确定要删除选中的数据？")) {
		return false;
	}

	// 执行删除操作
	var params = {};
	// 添加删除指令
	params["funcId"] = "hex_major_deleteMajorFunction";
	params["major_id"] = majorGridSelectRow["major_id"];

	// 执行这个操作
	request({
		data : [ params ],
		func : function(data) {
			if (data.responses[0].flag <= 0) {
				alert(data.responses[0].message);
				return;
			}
			majorGrid.refresh();
			majorGridSelectRow = null;
		}
	});
}

/**
 * 执行用户操作(添加/修改)
 */
function saveMajorData(){
	if(operateFlag == "add"){
		if(!confirm("您确定要添加此专业吗？")){
			return;
		}
		var params = {};
		majorOperateForm.submit("hex_major_insertMajorFunction",params);
	}
	if(operateFlag == "edit"){
		if(!confirm("您确定要修改此专业信息吗？")){
			return;
		}
		var params = {};
		params["major_id"] = majorGridSelectRow["major_id"];
		majorOperateForm.submit("hex_major_updateMajorFunction",params);
	}
}