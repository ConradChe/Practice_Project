package com.yunyun.school.action.login;

import com.haitsoft.framework.core.servlet.IAction;
import com.haitsoft.framework.data.bean.DaoResult;
import com.haitsoft.framework.data.context.DataContext;
import com.yunyun.school.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterAction implements IAction {
    private String loginUrl = "../login.html";
    private String registerUrl = "../register.html";

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    @Override
    public void doAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 清除登录成功标志
        HttpSession session = request.getSession();

        // 读取前台传递过来的信息，并保存这些信息
        String loginName = request.getParameter("login_name");
        String loginPwd = request.getParameter("login_pwd");
        String realName = request.getParameter("real_name");


        // 进行二次基本判断
        if (loginName == null || loginName.length() == 0 || loginPwd == null || loginPwd.length() == 0) {
            session.setAttribute("errorMsg", "用户名或密码不允许为空!");
            response.sendRedirect(this.getRegisterUrl());
            return;
        }

        // 防止简单的SQL注入
        if (StringUtil.isHaveBlank(loginName) || StringUtil.isHaveBlank(loginPwd)) {
            session.setAttribute("errorMsg", "用户名或密码不允许存在空格和单引号!");
            response.sendRedirect(this.getRegisterUrl());
            return;
        }

        Map param = new HashMap();
        String userId = DataContext.getContext().getSequenceId("U");
        param.put("user_id",userId);
        param.put("login_name",loginName);
        param.put("login_pwd",loginPwd);
        param.put("real_name",realName);
        DataContext.getContext().doHexById("hex_user_insertUser", param);

        response.sendRedirect(this.getLoginUrl());


    }

    public String getLoginUrl() {
        return loginUrl;
    }
}
