package com.yunyun.school.function.school;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.bean.Response;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;
import com.yunyun.school.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertStudentFunction implements IFunction {
    @Override
    public DaoResult doFunction(Map params, Map map1) throws Exception {
        /*-------------获取参数-----------------*/
        String studentNo = params.get("student_no").toString();
        String studentName = params.get("student_name").toString();
        String phoneNumber = params.get("phone_number").toString();
        String studentSex = params.get("student_sex").toString();
        String startTime = params.get("start_time").toString();
        String endTime = params.get("end_time").toString();
        String studentClass = params.get("student_class").toString();
//        Object studentPhoto = StringUtil.convertObject(params.get("student_photo").toString());

        /*-----------------基础判断-------------------*/
        if("".equals(studentNo)){
            return new DaoResult(-1,"学号不能为空");
        }
        if("".equals(studentName)){
            return new DaoResult(-1,"姓名不能为空");
        }
        if("".equals(phoneNumber)){
            return new DaoResult(-1,"电话不能为空");
        }
        if("".equals(studentSex)){
            return new DaoResult(-1,"性别不能为空");
        }
        if("".equals(startTime)){
            return new DaoResult(-1,"入学时间不能为空");
        }
        if("".equals(endTime)){
            return new DaoResult(-1,"毕业时间不能为空");
        }
        if("".equals(studentClass)){
            return new DaoResult(-1,"班级不能为空");
        }

        /*-----------------查询学号是否重复-----------------*/
        //查询学号是否重复
        Map studentMap = DataContext.getContext().doHexByIdToMap("hex_school_student_queryStudentByNo","student_no="+studentNo );
        if(studentMap!=null && !studentMap.isEmpty()){
            return new DaoResult(-1,"您输入的学号"+studentMap.get("student_no")+"已存在，请检查");
        }

        /*------------------逻辑操作-------------------*/
        List<Map> paramList = new ArrayList<>();
        Map param = new HashMap();

        String studentId = DataContext.getContext().getSequenceId("s");
        param.put("funcId","hex_school_student_insertStudent");
        param.put("student_id",studentId);
        param.put("student_no",studentNo);
        param.put("student_name",studentName);
        param.put("phone_number",phoneNumber);
        param.put("student_sex",studentSex);
        param.put("start_time",startTime);
        param.put("end_time",endTime);
        param.put("student_class",studentClass);
//        param.put("student_photo",studentPhoto);
        paramList.add(param);

        //执行事务
        Response response = DataContext.getContext().doHexsByTransaction(null, paramList);

        //返回结果
        DaoResult daoResult = new DaoResult(response.getFlag(), response.getMessage());

        return daoResult;
    }

    @Override
    public void doRollback(Map map) {

    }
}
