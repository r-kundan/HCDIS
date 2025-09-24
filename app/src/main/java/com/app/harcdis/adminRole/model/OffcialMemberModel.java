package com.app.harcdis.adminRole.model;

public class OffcialMemberModel {
    String name;
    String mobile;
    String username;

    public OffcialMemberModel(String name, String mobile, String username) {
        this.name = name;
        this.mobile = mobile;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return mobile + "-" + username;

    }
}
