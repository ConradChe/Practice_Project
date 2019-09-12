/**
 * 类型操作通用操作
 */

var leftTree; // 定义左边的树对象
var mytab; // 定义Tab对象
var updateForm; // 更新表单
var insertForm; // 添加表单同级
var insertChildForm; // 添加子项目表单
var selectItem; // 保存当前选中的Tree数据，因为后面都要使用

$(document).ready(function(){
	//从页面获取相关对象
	leftTree = hait.getCompById("leftTree");
	mytab = hait.getCompById("mytab");
	updateForm = hait.getCompById("updateForm");
	insertForm = hait.getCompById("insertForm");
	insertChildForm = hait.getCompById("insertChildForm");
	
	// 为更新表单增加删除后事件
	updateForm.onafterdelete = function() {
		leftTree.refresh();
	};
	
	// 所有表单和远程进行数据交互后，都刷新树形
	updateForm.onaftersubmit = function() {
		leftTree.refresh();
	};
	
	updateForm.obj.find("#submitBtn").click(function() {
		var primaryKeyValue = updateForm.getValue(leftTree.idName);
		if (primaryKeyValue == null || primaryKeyValue.length == 0 || primaryKeyValue == "自动编号") {
			alert("更新时主键信息不能为空");
			return;
		}
		if (!window.confirm("您确定要保存当前班级？")) {
			return;
		}
		updateForm.submit("hex_class_updateClass");
	});

	insertForm.onaftersubmit = function() {
		leftTree.refresh();
	};
	insertForm.obj.find("#submitBtn").click(function() {
		if (!window.confirm("您确定要添加该同级班级？")) {
			return;
		}
		insertForm.submit("hex_class_insertClassFunction");
	});

	insertChildForm.onaftersubmit = function() {
		leftTree.refresh();
	};
	insertChildForm.obj.find("#submitBtn").click(function() {
		if (!window.confirm("您确定要添加该下级班级？")) {
			return;
		}
		insertChildForm.submit("hex_class_insertClassFunction");
	});
});

/**
 * 树形点击事件
 */
function leftTreeClick(tree, treeId, treeNode) {
	selectItem = treeNode;

	// 更新表单
	var param = new Object();
	updateForm.reset();
	param[leftTree.idName] = selectItem[leftTree.idName];
	updateForm.load("hex_class_queryClassById", param);
	// 并转到选项0，并忽略change事件
	mytab.selected(0, true);
}


/**
 * 选项卡的切换事件
 */
function tabChange(oldItem, newItem) {
	if (selectItem == null) {
		alert("必须选择一项后才能操作表单!");
		return false;
	}
	switch (newItem.index()) {
	case 0:
		// 更新表单
		var param = new Object();
		param[leftTree.idName] = selectItem[leftTree.idName];
		updateForm.reset();
		updateForm.load("hex_class_queryClassById", param);
		break;
	case 1:
		// 添加同级
		insertForm.reset();
		hait.getCompById("same_region").setValue(selectItem[leftTree.parentId]);
		break;
	case 2:
		// 添加子级
		insertChildForm.reset();
		hait.getCompById("next_region").setValue(selectItem[leftTree.idName]);
		break;
	default:
		break;
	}
}

/**
 * 删除按钮的事件
 */
function updateFormDelete() {
	if (selectItem == null) {
		alert("您没有选择任何值!");
		return;
	}
	if (!confirm("您确定要删除选中的数据？")) {
		return false;
	}
	var params = {};
	params.funcId = "hex_class_deleteGradeFunction";
	params["grade_id"] = selectItem["grade_id"];
	// 执行这个操作
	request({
		data : [ params ],
		func : function(data) {
			if(data.responses[0].flag <= 0){
				alert(data.responses[0].message);
				return;
			}
			leftTree.refresh();
			updateForm.reset();
		}
	});
}
