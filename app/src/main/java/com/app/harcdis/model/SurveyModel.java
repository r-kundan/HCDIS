package com.app.harcdis.model;

public class SurveyModel {
    String remarks;
    String n_d_name;
    String n_t_name;
    String n_v_name;
    String n_murr_no;
    String n_khas_no;
    String uploadimage;

    public String getUploadimage1() {
        return uploadimage1;
    }

    public String getUploadimage2() {
        return uploadimage2;
    }

    public String getUploadimage3() {
        return uploadimage3;
    }

    String uploadimage1;
    String uploadimage2;
    String uploadimage3;
    String verifiedBy;
    String user_name;
    String verified;

    public String getGisId() {
        return gisId;
    }

    public void setGisId(String gisId) {
        this.gisId = gisId;
    }

    String gisId;
    public String getVerificationDate() {
        return verificationDate;
    }

    String verificationDate;
    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public  SurveyModel(String remarks, String n_d_name, String n_t_name, String n_v_name, String n_murr_no, String n_khas_no, String uploadimage,String uploadimage1,String uploadimage2,String uploadimage3, String verifiedBy, String user_name, String verified,String verificationDate,String gisId) {
        this.remarks = remarks;
        this.n_d_name = n_d_name;
        this.n_t_name = n_t_name;
        this.n_v_name = n_v_name;
        this.n_murr_no = n_murr_no;
        this.n_khas_no = n_khas_no;
        this.uploadimage = uploadimage;
        this.uploadimage1 = uploadimage1;
        this.uploadimage2 = uploadimage2;
        this.uploadimage3 = uploadimage3;
        this.verifiedBy = verifiedBy;
        this.user_name = user_name;
        this.verified = verified;
        this.verificationDate = verificationDate;
        this.gisId = gisId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getN_d_name() {
        return n_d_name;
    }

    public void setN_d_name(String n_d_name) {
        this.n_d_name = n_d_name;
    }

    public String getN_t_name() {
        return n_t_name;
    }

    public void setN_t_name(String n_t_name) {
        this.n_t_name = n_t_name;
    }

    public String getN_v_name() {
        return n_v_name;
    }

    public void setN_v_name(String n_v_name) {
        this.n_v_name = n_v_name;
    }

    public String getN_murr_no() {
        return n_murr_no;
    }

    public void setN_murr_no(String n_murr_no) {
        this.n_murr_no = n_murr_no;
    }

    public String getN_khas_no() {
        return n_khas_no;
    }

    public void setN_khas_no(String n_khas_no) {
        this.n_khas_no = n_khas_no;
    }

    public String getUploadimage() {
        return uploadimage;
    }

    public void setUploadimage(String uploadimage) {
        this.uploadimage = uploadimage;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
