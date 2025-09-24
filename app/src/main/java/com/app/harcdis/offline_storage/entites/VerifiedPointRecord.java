package com.app.harcdis.offline_storage.entites;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VerifiedPointRecord {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "OBJECTID")
    public String OBJECTID;

    @ColumnInfo(name = "year")
    public String year;

    @ColumnInfo(name = "remarks")
    public String remarks;

    @ColumnInfo(name = "verifiedBy")
    public String verifiedBy;

    @ColumnInfo(name = "verificationDate")
    public String verificationDate;

    @ColumnInfo(name = "user_name")
    public String user_name;

    @ColumnInfo(name = "verified")
    public String verified;

    @ColumnInfo(name = "nearByLandMark")
    public String nearByLandMark;

    @ColumnInfo(name = "feedback")
    public String feedback;

    @ColumnInfo(name = "gisId")
    public String gisId;

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


    @ColumnInfo(name = "n_d_name")
    public String n_d_name;

    @ColumnInfo(name = "n_t_name")
    public String n_t_name;

    @ColumnInfo(name = "n_v_name")
    public String n_v_name;

    @ColumnInfo(name = "n_murra_no")
    public String n_murra_no;

    @ColumnInfo(name = "n_khas_no")
    public String n_khas_no;

    @ColumnInfo(name = "latitude")
    public String latitude;

    @ColumnInfo(name = "longitude")
    public String longitude;


    @ColumnInfo(name = "auth_status")
    public String auth_status;

    @ColumnInfo(name = "owner_name")
    public String owner_name;


    public VerifiedPointRecord(String OBJECTID, String year, String remarks, String verifiedBy, String verificationDate, String user_name, String verified, String nearByLandMark, String feedback, String gisId, String image1, String image2, String image3, String image4, String video, String n_d_name, String n_t_name, String n_v_name, String n_murra_no, String n_khas_no, String latitude, String longitude, String auth_status,String owner_name) {
        this.OBJECTID = OBJECTID;
        this.year = year;
        this.remarks = remarks;
        this.verifiedBy = verifiedBy;
        this.verificationDate = verificationDate;
        this.user_name = user_name;
        this.verified = verified;
        this.nearByLandMark = nearByLandMark;
        this.feedback = feedback;
        this.gisId = gisId;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.video = video;
        this.n_d_name = n_d_name;
        this.n_t_name = n_t_name;
        this.n_v_name = n_v_name;
        this.n_murra_no = n_murra_no;
        this.n_khas_no = n_khas_no;
        this.latitude = latitude;
        this.longitude = longitude;
        this.auth_status = auth_status;
        this.owner_name = owner_name;
    }

    public VerifiedPointRecord() {
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public VerifiedPointRecord(int uid, String OBJECTID, String year, String remarks, String verifiedBy, String verificationDate, String user_name, String verified, String nearByLandMark, String feedback, String gisId, String image1, String image2, String image3, String image4, String video, String n_d_name, String n_t_name, String n_v_name, String n_murra_no, String n_khas_no, String latitude, String longitude, String auth_status, String owner_name) {
        this.uid = uid;
        this.OBJECTID = OBJECTID;
        this.year = year;
        this.remarks = remarks;
        this.verifiedBy = verifiedBy;
        this.verificationDate = verificationDate;
        this.user_name = user_name;
        this.verified = verified;
        this.nearByLandMark = nearByLandMark;
        this.feedback = feedback;
        this.gisId = gisId;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.video = video;
        this.n_d_name = n_d_name;
        this.n_t_name = n_t_name;
        this.n_v_name = n_v_name;
        this.n_murra_no = n_murra_no;
        this.n_khas_no = n_khas_no;
        this.latitude = latitude;
        this.longitude = longitude;
        this.auth_status = auth_status;
        this.owner_name = owner_name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getOBJECTID() {
        return OBJECTID;
    }

    public void setOBJECTID(String OBJECTID) {
        this.OBJECTID = OBJECTID;
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

    public String getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(String verificationDate) {
        this.verificationDate = verificationDate;
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

    public String getGisId() {
        return gisId;
    }

    public void setGisId(String gisId) {
        this.gisId = gisId;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
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

    public String getN_murra_no() {
        return n_murra_no;
    }

    public void setN_murra_no(String n_murra_no) {
        this.n_murra_no = n_murra_no;
    }

    public String getN_khas_no() {
        return n_khas_no;
    }

    public void setN_khas_no(String n_khas_no) {
        this.n_khas_no = n_khas_no;
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

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}