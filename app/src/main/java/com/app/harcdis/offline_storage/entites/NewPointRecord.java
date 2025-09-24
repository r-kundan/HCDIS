package com.app.harcdis.offline_storage.entites;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NewPointRecord {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "latitude")
    public String latitude;

    @ColumnInfo(name = "longitude")
    public String longitude;

    @ColumnInfo(name = "ca_name")
    public String ca_name;

    @ColumnInfo(name = "dev_plan")
    public String dev_plan;

    @ColumnInfo(name = "n_d_code")
    public String n_d_code;

    @ColumnInfo(name = "n_d_name")
    public String n_d_name;

    @ColumnInfo(name = "n_t_code")
    public String n_t_code;

    @ColumnInfo(name = "n_t_name")
    public String n_t_name;

    @ColumnInfo(name = "n_v_code")
    public String n_v_code;

    @ColumnInfo(name = "n_v_name")
    public String n_v_name;

    @ColumnInfo(name = "n_murr_no")
    public String n_murr_no;
    @ColumnInfo(name = "n_khas_no")
    public String n_khas_no;
    @ColumnInfo(name = "year")
    public String year;
    @ColumnInfo(name = "remarks")
    public String remarks;

    @ColumnInfo(name = "verifiedBy")
    public String verifiedBy;

    @ColumnInfo(name = "user_name")
    public String user_name;

    @ColumnInfo(name = "verified")
    public String verified;

    @ColumnInfo(name = "nearByLandMark")
    public String nearByLandMark;

    @ColumnInfo(name = "feedback")
    public String feedback;

    @ColumnInfo(name = "pointSource")
    public String pointSource;

    @ColumnInfo(name = "CA_Key_GIS")
    public String CA_Key_GIS;

    @ColumnInfo(name = "AOI_DP")
    public String AOI_DP;

    @ColumnInfo(name = "AOI_UA")
    public String AOI_UA;

    @ColumnInfo(name = "AOI_CA")
    public String AOI_CA;

    @ColumnInfo(name = "image1")
    public String image1;

    @ColumnInfo(name = "image2")
    public String image2;

    @ColumnInfo(name = "image3")
    public String image3;

    @ColumnInfo(name = "image4")
    public String image4;

    @ColumnInfo(name = "video")
    public String video;

    @ColumnInfo(name = "auth_status")
    public String auth_status;


    @ColumnInfo(name = "owner_name")
    public String owner_name;

    @ColumnInfo(name = "UAKey")
    public String UAKey;


    public NewPointRecord() {
    }

    public NewPointRecord(String latitude, String longitude, String ca_name, String dev_plan, String n_d_code, String n_d_name, String n_t_code, String n_t_name, String n_v_code, String n_v_name, String n_murr_no, String n_khas_no, String year, String remarks, String verifiedBy, String user_name, String verified, String nearByLandMark, String feedback, String pointSource, String CA_Key_GIS, String AOI_DP, String AOI_UA, String AOI_CA, String image1, String image2, String image3, String image4, String video, String auth_status,String owner_name,String UAKey) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.ca_name = ca_name;
        this.dev_plan = dev_plan;
        this.n_d_code = n_d_code;
        this.n_d_name = n_d_name;
        this.n_t_code = n_t_code;
        this.n_t_name = n_t_name;
        this.n_v_code = n_v_code;
        this.n_v_name = n_v_name;
        this.n_murr_no = n_murr_no;
        this.n_khas_no = n_khas_no;
        this.year = year;
        this.remarks = remarks;
        this.verifiedBy = verifiedBy;
        this.user_name = user_name;
        this.verified = verified;
        this.nearByLandMark = nearByLandMark;
        this.feedback = feedback;
        this.pointSource = pointSource;
        this.CA_Key_GIS = CA_Key_GIS;
        this.AOI_DP = AOI_DP;
        this.AOI_UA = AOI_UA;
        this.AOI_CA = AOI_CA;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.video = video;
        this.auth_status = auth_status;
        this.owner_name = owner_name;
        this.UAKey = UAKey;

    }

    public NewPointRecord(int uid, String latitude, String longitude, String ca_name, String dev_plan, String n_d_code, String n_d_name, String n_t_code, String n_t_name, String n_v_code, String n_v_name, String n_murr_no, String n_khas_no, String year, String remarks, String verifiedBy, String user_name, String verified, String nearByLandMark, String feedback, String pointSource, String CA_Key_GIS, String AOI_DP, String AOI_UA, String AOI_CA, String image1, String image2, String image3, String image4, String video, String auth_status,String owner_name,String UAKey) {
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ca_name = ca_name;
        this.dev_plan = dev_plan;
        this.n_d_code = n_d_code;
        this.n_d_name = n_d_name;
        this.n_t_code = n_t_code;
        this.n_t_name = n_t_name;
        this.n_v_code = n_v_code;
        this.n_v_name = n_v_name;
        this.n_murr_no = n_murr_no;
        this.n_khas_no = n_khas_no;
        this.year = year;
        this.remarks = remarks;
        this.verifiedBy = verifiedBy;
        this.user_name = user_name;
        this.verified = verified;
        this.nearByLandMark = nearByLandMark;
        this.feedback = feedback;
        this.pointSource = pointSource;
        this.CA_Key_GIS = CA_Key_GIS;
        this.AOI_DP = AOI_DP;
        this.AOI_UA = AOI_UA;
        this.AOI_CA = AOI_CA;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.video = video;
        this.auth_status = auth_status;
        this.owner_name = owner_name;
        this.UAKey = UAKey;
    }

    public String getUAKey() {
        return UAKey;
    }

    public void setUAKey(String UAKey) {
        this.UAKey = UAKey;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getN_d_code() {
        return n_d_code;
    }

    public void setN_d_code(String n_d_code) {
        this.n_d_code = n_d_code;
    }

    public String getN_d_name() {
        return n_d_name;
    }

    public void setN_d_name(String n_d_name) {
        this.n_d_name = n_d_name;
    }

    public String getN_t_code() {
        return n_t_code;
    }

    public void setN_t_code(String n_t_code) {
        this.n_t_code = n_t_code;
    }

    public String getN_t_name() {
        return n_t_name;
    }

    public void setN_t_name(String n_t_name) {
        this.n_t_name = n_t_name;
    }

    public String getN_v_code() {
        return n_v_code;
    }

    public void setN_v_code(String n_v_code) {
        this.n_v_code = n_v_code;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getNearByLandMark() {
        return nearByLandMark;
    }

    public void setNearByLandMark(String nearByLandMark) {
        this.nearByLandMark = nearByLandMark;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getPointSource() {
        return pointSource;
    }

    public void setPointSource(String pointSource) {
        this.pointSource = pointSource;
    }

    public String getCA_Key_GIS() {
        return CA_Key_GIS;
    }

    public void setCA_Key_GIS(String CA_Key_GIS) {
        this.CA_Key_GIS = CA_Key_GIS;
    }

    public String getAOI_DP() {
        return AOI_DP;
    }

    public void setAOI_DP(String AOI_DP) {
        this.AOI_DP = AOI_DP;
    }

    public String getAOI_UA() {
        return AOI_UA;
    }

    public void setAOI_UA(String AOI_UA) {
        this.AOI_UA = AOI_UA;
    }

    public String getAOI_CA() {
        return AOI_CA;
    }

    public void setAOI_CA(String AOI_CA) {
        this.AOI_CA = AOI_CA;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

}
