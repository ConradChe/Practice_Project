package com.yunyun.school.function.school;

import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.bean.Response;
import com.haitsoft.framework.data.context.DataContext;
import com.haitsoft.framework.data.dao.function.IFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteStudentFunction implements IFunction {
    @Override
    public DaoResult doFunction(Map params, Map map1) throws Exception {

        /*----------------获取数据-------------------*/
        String studentId = params.get("student_id").toString();

        /*-----------------常规判断--------------------*/
        if ("".equals(studentId)){
            return new DaoResult(-1, "请选择您要删除的学生");
        }

        /*------------判断该用户是否存在-----------------*/
        Map isHadStudentMap = DataContext.getContext().doHexByIdToMap("hex_school_student_queryStudentById", "student_id" + studentId);
        if (isHadStudentMap == null || isHadStudentMap.isEmpty()){
            return new DaoResult(-1, "您要删除的学生信息异常,请刷新后重试");
        }

        /*-----------------逻辑操作-------------------*/
        List<Map> paramsList = new ArrayList<>();
        Map param = new HashMap();
        param.put("funcId","hex_school_student_deleteStudentById");
        param.put("student_id",studentId);
        paramsList.add(param);

        /*--------------------执行任务-------------------*/
        Response response = DataContext.getContext().doHexsByTransaction(null, paramsList);

        /*--------------------返回结果-------------------*/
        DaoResult daoResult = new DaoResult(response.getFlag(), response.getMessage());

        return daoResult;
    }

    @Override
    public void doRollback(Map map) {

    }
}
