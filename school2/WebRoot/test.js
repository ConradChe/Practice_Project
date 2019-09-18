//选中行对象
var studentRow;

//操作标识符
var operateFlag="";

$(function (){
    studentDialog = hait.getCompById("studentDialog");
    studentOperateForm = hait.getCompById("studentOperateForm");
    //获取表对象
    studentGrid = hait.getCompById("studentGrid");

    studentOperateForm.onaftersubmit = function() {
        studentDialog.hide();	// 隐藏弹出框
        studentGrid.refresh();	// 刷新列表数据
    };
    studentGrid.refresh();	// 刷新列表数据
});


/*当列表行被点击*/
function studentRowClick(row){
    studentRow = row;
    alert("id:" + studentRow["id"] + ",name:" + studentRow["name"] +
    ",age:" + studentRow["age"] + ",grade:" + studentRow["grade"]);
}

function addStudent() {
    alert("增加数据")
    studentOperateForm.reset();
    studentDialog.setTitle("添加学生信息");
    studentDialog.show();
    operateFlag="add";


}

function updateStudent() {
    alert("修改数据")
    studentOperateForm.reset();
    studentDialog.setTitle("更新学生信息");

    alert(studentRow["name"])
    studentDialog.show();
    operateFlag="update";
}

function deleteStudent() {
    //获取表对象
    var studentGrid = hait.getCompById("studentGrid");
    var id = studentRow["id"];
    studentGrid.deleteItem("id="+id);
}

function selectStudent() {
    //获取表对象
    var studentGrid = hait.getCompById("studentGrid");
    var id = studentRow["id"];
    studentGrid.setSelected("id=" + id);
}

/*
* 修改或新增学生
 */
function saveStudentData() {

    if (operateFlag == "add"){
        if(!confirm("您确定要添加此学生吗？")){
            return;
        }
        var studentId = studentOperateForm.getValue("student_id");
        var studentName = studentOperateForm.getValue("student_name");
        var studentAge = studentOperateForm.getValue("student_age");
        var studentGrade = studentOperateForm.getValue("student_grade");
        studentGrid.insertItem({
            id : studentId,
            name : studentName,
            age : studentAge,
            grade : studentGrade
        });
        var params = {}
        studentOperateForm.submit("",params);
    }
    if (operateFlag == "update"){
        if(!confirm("您确定要更新此学生吗？")){
            return;
        }
        var studentId = hait.getCompByName("student_id");
        var studentName = hait.getCompByName("student_name");
        var studentAge = hait.getCompByName("student_age");
        var studentGrade = hait.getCompByName("student_grade");
        studentGrid.updateItem()({
            id : studentId,
            name : studentName,
            age : studentAge,
            grade : studentGrade
        });
        var params = {}
        studentOperateForm.submit("",params);
    }

}