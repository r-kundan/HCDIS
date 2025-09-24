package com.app.harcdis.adminRole.model;

public class SurveyHistoryModel {
    String verificationDate;
    String verifiedBy;
    String userName;
    String year;
    String uploadimage;
    String uploadimage1;
    String uploadimage2;
    String uploadimage3;
    String uploadVideo;
    String feedback;
    String nearByLandMark;
    String remarks;

    public SurveyHistoryModel(String verificationDate, String verifiedBy, String userName, String year, String uploadimage, String uploadimage1, String uploadimage2, String uploadimage3, String uploadVideo, String feedback, String nearByLandMark, String remarks) {
        this.verificationDate = verificationDate;
        this.verifiedBy = verifiedBy;
        this.userName = userName;
        this.year = year;
        this.uploadimage = uploadimage;
        this.uploadimage1 = uploadimage1;
        this.uploadimage2 = uploadimage2;
        this.uploadimage3 = uploadimage3;
        this.uploadVideo = uploadVideo;
        this.feedback = feedback;
        this.nearByLandMark = nearByLandMark;
        this.remarks = remarks;
    }

    public String getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(String verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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

    public String getUploadVideo() {
        return uploadVideo;
    }

    public void setUploadVideo(String uploadVideo) {
        this.uploadVideo = uploadVideo;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getNearByLandMark() {
        return nearByLandMark;
    }

    public void setNearByLandMark(String nearByLandMark) {
        this.nearByLandMark = nearByLandMark;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
