package com.app.harcdis.point_forward_flow.model;

public class MasterModel {
    String status_code;
    String status_name;

    public MasterModel(String status_code, String status_name) {
        this.status_code = status_code;
        this.status_name = status_name;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    @Override
    public String toString() {
        return  status_name;
    }
}
