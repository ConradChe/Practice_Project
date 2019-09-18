
/*学生列表当前选中行*/
var studentGridSelectRow;


/*单击学生信息列表*/
function studentGridRowClick(row) {
    studentGridSelectRow = row;
}

/**
 * 操作标识
 */
var operateFlag="";


/*
页面加载完毕
 */
$(document).ready(function () {
    studentGrid =hait.getCompById("studentGrid");
    studentOperateForm = hait.getCompById("studentOperateForm");
    studentOperateDialog = hait.getCompById("studentOperateDialog");

    //当表单请求完毕
    studentOperateForm.onaftersubmit = function () {
        studentOperateDialog.hide();//隐藏对话框
        studentGrid.refresh();//刷新列表数据
    }
});



/*
* 弹出增加学生端口
*/
function showAddStudentDialog() {

    studentOperateForm.reset();
    studentOperateDialog.setTitle("添加学生");
    studentOperateDialog.show();
    operateFlag = "add";

}

/*
* 弹出修改学生端口
*/
function showModifyStudentDialog() {
    if(studentGridSelectRow == null || studentGridSelectRow.length == 0){
        alert("您没有选择任何数据");
        return;
    }

    studentOperateForm.reset();
    var params = {}
    params["student_id"] =studentGridSelectRow["student_id"];
    studentOperateForm.load("hex_school_student_queryStudentById",params);
    studentOperateDialog.setTitle("修改学生信息");
    studentOperateDialog.show();
    operateFlag = "edit";
}

/*
提交数据
 */
function saveUserData() {
    if (operateFlag == "add"){
        if(!confirm("您确定要添加此学生吗？")){
            return;
        }
        var params = {};
        studentOperateForm.submit("hex_school_student_insertStudentFunction",params);
    }
    if (operateFlag == "edit"){
        if(!confirm("您确定要修改此学生信息吗？")){
            return;
        }
        var params = {}
        params["student_id"] = studentGridSelectRow["student_id"];
        studentOperateForm.submit("hex_school_student_updateStudentFunction",params);
    }
}
/**
 * 根据条件查询学生信息
 */
function searchStudentByConditions() {
    //初始化查询参数
    studentGrid.params = {};

    var searchColumn = hait.getCompById("searchColumn").getValue();
    var searchValue = hait.getCompById("searchValue").getValue();
    if(searchValue != null && searchValue.length > 0 && searchColumn != "0"){
        studentGrid.params[searchColumn] = searchValue;
    }
    var startTime = hait.getCompById("startTime").getValue();
    var endTime = hait.getCompById("endTime").getValue();
    if(startTime != null && startTime.length>0){
        studentGrid.params["start_time"] = startTime;
        studentGrid.params["end_time"] = endTime;
    }

    //刷新列表页面
    studentGrid.refresh();
}

/*
 *删除学生信息
 */
function doDeleteStudent() {
    if (studentGridSelectRow == null || studentGridSelectRow.length == 0){
        alert("您没有选择任何数据");
        return;
    }

    if (!confirm("您确定要删除选中的数据？")) {
        return false;
    }

    //删除指令
    var params = {};
    params["student_id"] = studentGridSelectRow["student_id"];
    params["funcId"] = "hex_school_student_deleteStudentFunction";

    request({
       data:[params],
        func:function (data) {
            if (data.responses[0].flag <= 0){
                alert(data.responses[0].message);
                return;
            }
            studentGrid.refresh();
            studentGridSelectRow = null;
        }
    });

}







