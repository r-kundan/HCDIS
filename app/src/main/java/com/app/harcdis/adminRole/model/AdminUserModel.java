package com.app.harcdis.adminRole.model;

public class AdminUserModel {
    String name;
    String mobile;
    String n_d_name;
    String roleId;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    String user_name;

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    String designation;
    public AdminUserModel(String name, String mobile, String n_d_name, String roleId,String user_name,String designation) {
        this.name = name;
        this.mobile = mobile;
        this.n_d_name = n_d_name;
        this.roleId = roleId;
        this.user_name = user_name;
        this.designation = designation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getN_d_name() {
        return n_d_name;
    }

    public void setN_d_name(String n_d_name) {
        this.n_d_name = n_d_name;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
