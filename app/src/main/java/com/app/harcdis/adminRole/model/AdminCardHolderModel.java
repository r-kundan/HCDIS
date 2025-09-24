package com.app.harcdis.adminRole.model;

public class AdminCardHolderModel {

    String n_d_name;
    String n_t_name;
    String n_v_name;
    String n_murr_no;
    String n_khas_no;
    String ca_name;
    String dev_plan;
    String entry_date;
    String verifiedBy;
    String verified;
    String uploadimage;
    String uploadimage1;
    String uploadimage2;
    String uploadimage3;
    Double latitude;
    Double longitude;
    String objectId;
    String auth_status;
    boolean isSelected;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    String user_name;
    String nearByLandmark;

    public String getNearByLandmark() {
        return nearByLandmark;
    }

    public void setNearByLandmark(String nearByLandmark) {
        this.nearByLandmark = nearByLandmark;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    String feedback;

    public String getGisId() {
        return gisId;
    }

    String gisId;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    String UID;

    public String getObjectId() {
        return objectId;
    }


    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public AdminCardHolderModel(String n_d_name, String n_t_name, String n_v_name, String n_murr_no, String n_khas_no, String ca_name, String dev_plan, String entry_date,
                                String verifiedBy, String verified,
                                String uploadimage, String uploadimage1, String uploadimage2, String uploadimage3,
                                Double latitude, Double longitude, String objectId, String gisId,
                                String nearByLandmark, String feedback, String user_name,
                                String UID

    ) {
        this.n_d_name = n_d_name;
        this.n_t_name = n_t_name;
        this.n_v_name = n_v_name;
        this.n_murr_no = n_murr_no;
        this.n_khas_no = n_khas_no;
        this.ca_name = ca_name;
        this.dev_plan = dev_plan;
        this.entry_date = entry_date;
        this.verifiedBy = verifiedBy;
        this.verified = verified;
        this.uploadimage = uploadimage;
        this.uploadimage1 = uploadimage1;
        this.uploadimage2 = uploadimage2;

        this.uploadimage3 = uploadimage3;
        this.latitude = latitude;
        this.longitude = longitude;
        this.objectId = objectId;
        this.gisId = gisId;
        this.nearByLandmark = nearByLandmark;
        this.feedback = feedback;
        this.user_name = user_name;
        this.UID = UID;

    }


    public AdminCardHolderModel(String n_d_name, String n_t_name, String n_v_name, String n_murr_no, String n_khas_no, String entry_date, String verifiedBy, String verified, String gisId, String UID,String auth_status) {
        this.n_d_name = n_d_name;
        this.n_t_name = n_t_name;
        this.n_v_name = n_v_name;
        this.n_murr_no = n_murr_no;
        this.n_khas_no = n_khas_no;
        this.entry_date = entry_date;
        this.verifiedBy = verifiedBy;
        this.verified = verified;
        this.gisId = gisId;
        this.UID = UID;
        this.auth_status = auth_status;
    }

    public AdminCardHolderModel(String n_d_name, String n_t_name, String n_v_name, String n_murr_no, String n_khas_no, String gisId, String UID, boolean isSelected

    ) {
        this.n_d_name = n_d_name;
        this.n_t_name = n_t_name;
        this.n_v_name = n_v_name;
        this.n_murr_no = n_murr_no;
        this.n_khas_no = n_khas_no;
        this.gisId = gisId;
        this.UID = UID;
        this.isSelected = isSelected;
    }


    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }


    public String getUploadimage() {
        return uploadimage;
    }

    public void setUploadimage(String uploadimage) {
        this.uploadimage = uploadimage;
    }

    public String getUploadimage1() {
        return uploadimage1;
    }

    public void setUploadimage1(String uploadimage1) {
        this.uploadimage1 = uploadimage1;
    }

    public String getUploadimage2() {
        return uploadimage2;
    }

    public void setUploadimage2(String uploadimage2) {
        this.uploadimage2 = uploadimage2;
    }

    public String getUploadimage3() {
        return uploadimage3;
    }

    public void setUploadimage3(String uploadimage3) {
        this.uploadimage3 = uploadimage3;
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

    public String getCa_name() {
        return ca_name;
    }

    public void setCa_name(String ca_name) {
        this.ca_name = ca_name;
    }

    public String getDev_plan() {
        return dev_plan;
    }

    public void setDev_plan(String dev_plan) {
        this.dev_plan = dev_plan;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
