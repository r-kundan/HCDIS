package com.app.harcdis.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    // ===================== OTP (NON-USER) =====================
    // Base: https://onemapdepts.gmda.gov.in/
    @FormUrlEncoded
    @POST("smsapi/query/send_otp_nonuser")
    Call<ResponseBody> sendOtpToUser(
            @Field("mobile") String mobile,
            @Field("send_by") String send_by,   // e.g., "harsac"
            @Field("msgType") String msgType    // e.g., "harsac"
    );

    @FormUrlEncoded
    @POST("smsapi/query/verify_otp_nonuser")
    Call<ResponseBody> VerifyOtpUser(
            @Field("mobile") String mobile,
            @Field("otp") String otp
    );

    // ===================== App/Version =====================
    @FormUrlEncoded
    @POST("developer_services/api/ReadAppInfo")
    Call<ResponseBody> checkVersion(
            @Field("app_name") String app_name
    );

    // ===================== Multipart Upload =====================
    @Multipart
    @POST("API_tcp_encroachment_v1.0/uploaddatamultipart")
    Call<ResponseBody> upload_data_to_layer_via_api_via_multipart(
            @Part("OBJECTID") RequestBody id,
            @Part("year") RequestBody year,
            @Part("remarks") RequestBody remarks,
            @Part("verifiedBy") RequestBody verifiedBy,
            @Part("verificationDate") RequestBody verificationDate,
            @Part("user_name") RequestBody user_name,
            @Part("verified") RequestBody verified,
            @Part("nearByLandMark") RequestBody nearByLandMark,
            @Part("feedback") RequestBody feedback,
            @Part("gisId") RequestBody gisId,
            @Part List<MultipartBody.Part> file
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getfeaturelayer")
    Call<ResponseBody> getFeatureLayerByDisCode(
            @Field("district_code") String dis_code,
            @Field("token") String token
    );

    // Mobile-based existence check (used in this flow)
    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/checkUserExist")
    Call<ResponseBody> checkUserExit(
            @Field("user_mobile_no") String user_mobile_no
    );

    // Username-based check (kept for compatibility if elsewhere needed)
    @FormUrlEncoded
    @POST("checkUserExist")
    Call<ResponseBody> checkUserExist(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getUserData")
    Call<ResponseBody> getUserData(
            @Field("user_mobile_no") String user_mobile_no
    );

    @FormUrlEncoded
    @POST("API_gfasalmaster_v1.0/master")
    Call<ResponseBody> getAllTehsil(
            @Field("districtcode") String districtcode
    );

    @FormUrlEncoded
    @POST("API_gfasalmaster_v1.0/master")
    Call<ResponseBody> getAllVillage(
            @Field("districtcode") String districtcode,
            @Field("tehsilcode") String tehsilcode
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/requiredetails")
    Call<ResponseBody> getAllNeededData(
            @Field("token") String token
    );

    @GET("API_tcp_encroachment_v1.0/getAllDistrict")
    Call<ResponseBody> getAllDistrict();

    @GET("API_gfasalmaster_v1.0/getdistrict")
    Call<ResponseBody> getListOfDistricts();

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getDashboardDetail")
    Call<ResponseBody> getDashboardDetails(
            @Field("mobile") String mobile,
            @Field("n_d_code") String n_d_code
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/totalverifiedlist")
    Call<ResponseBody> totalVerifiedList(
            @Field("n_d_code") String n_d_code,
            @Field("n_t_code") String n_t_code,
            @Field("n_v_code") String n_v_code
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/totalunverifiedlist")
    Call<ResponseBody> totalUnVerifiedList(
            @Field("n_d_code") String n_d_code,
            @Field("n_t_code") String n_t_code,
            @Field("n_v_code") String n_v_code
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getrecordbydate")
    Call<ResponseBody> getRecordByDate(
            @Field("startDate") String startDate,
            @Field("endDate") String endDate
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getAllRecordList")
    Call<ResponseBody> getAllRecordList(
            @Field("n_d_code") String n_d_code,
            @Field("n_t_code") String n_t_code,
            @Field("n_v_code") String n_v_code,
            @Field("page") int page
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/fcmtokenregister")
    Call<ResponseBody> updateTokenInDb(
            @Field("user_mobile_no") String user_mobile_no,
            @Field("fcm_token") String fcm_token
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getMainDashboardData")
    Call<ResponseBody> getMainDashboardData(
            @Field("n_d_code") String user_mobile_no
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/reportbydate")
    Call<ResponseBody> reportByDate(
            @Field("searchdate") String searchdate,
            @Field("weeksearch") String weeksearch,
            @Field("monthsearch") String monthsearch
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getHistory")
    Call<ResponseBody> getHistory(
            @Field("gisId") String gisId
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getUserHistory")
    Call<ResponseBody> getUserHistory(
            @Field("verifiedBy") String verifiedBy
    );

    @GET("API_tcp_encroachment_v1.0/getuserlist")
    Call<ResponseBody> getAdminUserList();

    @GET("API_tcp_encroachment_v1.0/getReport")
    Call<ResponseBody> getReport();

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/locationHistory")
    Call<ResponseBody> locationHistory(
            @Field("userId") String userId,
            @Field("lattitude") String latitude,
            @Field("longitude") String longitude,
            @Field("date") String date,
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getNewPointInformationApp")
    Call<ResponseBody> getNewPointInformationApp(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("district_code") String district_code
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getNewPointInformation")
    Call<ResponseBody> getNewPointInformation(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("district_code") String district_code
    );

    @Multipart
    @POST("API_tcp_encroachment_v1.0/addNewPoint")
    Call<ResponseBody> addNewPoint(
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("ca_name") RequestBody ca_name,
            @Part("dev_plan") RequestBody dev_plan,
            @Part("n_d_code") RequestBody n_d_code,
            @Part("n_d_name") RequestBody n_d_name,
            @Part("n_t_code") RequestBody n_t_code,
            @Part("n_t_name") RequestBody n_t_name,
            @Part("n_v_code") RequestBody n_v_code,
            @Part("n_v_name") RequestBody n_v_name,
            @Part("n_murr_no") RequestBody n_murr_no,
            @Part("n_khas_no") RequestBody n_khas_no,
            @Part("year") RequestBody year,
            @Part("remarks") RequestBody remarks,
            @Part("verifiedBy") RequestBody verifiedBy,
            @Part("user_name") RequestBody user_name,
            @Part("verified") RequestBody verified,
            @Part("nearByLandMark") RequestBody nearByLandMark,
            @Part("feedback") RequestBody feedback,
            @Part("pointSource") RequestBody pointSource,
            @Part("CA_Key_GIS") RequestBody CA_Key_GIS,
            @Part("AOI_DP") RequestBody AOI_DP,
            @Part("AOI_UA") RequestBody AOI_UA,
            @Part("AOI_CA") RequestBody AOI_CA,
            @Part List<MultipartBody.Part> file
    );

    @GET("API_tcp_encroachment_v1.0/getFloor")
    Call<ResponseBody> getFloor();

    @GET("API_tcp_encroachment_v1.0/getStructure")
    Call<ResponseBody> getStructure();

    @GET("API_tcp_encroachment_v1.0/getLand")
    Call<ResponseBody> getLand();

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/built_up_area_history")
    Call<ResponseBody> built_up_area_history(
            @Field("gisId") String gisId,
            @Field("year") String year,
            @Field("remarks1") String remarks1,
            @Field("verifiedBy") String verifiedBy,
            @Field("user_name") String user_name,
            @Field("verified") String verified,
            @Field("pointSource") String pointSource,
            @Field("construction_type") String construction_type,
            @Field("floor") String floor,
            @Field("tcp_area_verified") Double tcp_area_verified
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/changeStatus")
    Call<ResponseBody> changeStatus(
            @Field("gisId") String gisId,
            @Field("action") String action
    );

    // Legacy SSO login (kept for compatibility if needed elsewhere)
    @FormUrlEncoded
    @POST("markpointLogin")
    Call<ResponseBody> SSO_Login(
            @Field("command") String command,
            @Field("UserName") String UserName,
            @Field("ModuleId") String ModuleId,
            @Field("EventName") String EventName,
            @Field("OTP") String OTP,
            @Field("IsResend") String IsResend
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/tcp_sso_register")
    Call<ResponseBody> Register_SSO_User(
            @Field("MobileNo") String MobileNo,
            @Field("DesignationID") String DesignationID,
            @Field("Designation") String Designation,
            @Field("OfficeID") String OfficeID,
            @Field("OfficeName") String OfficeName,
            @Field("userGroupId") String userGroupId,
            @Field("userid") String userid,
            @Field("name") String name,
            @Field("username") String username,
            @Field("emailid") String emailid
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getControlledArea")
    Call<ResponseBody> getControlledArea(
            @Field("n_d_code") String n_d_code
    );

    @GET("API_tcp_encroachment_v1.0/getSecurityQuestion")
    Call<ResponseBody> getListOfSecurityQuestions();

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/citizenRegister")
    Call<ResponseBody> citizenRegister(
            @Field("n_d_code") String n_d_code,
            @Field("n_d_name") String n_d_name,
            @Field("name") String name,
            @Field("mobile") String mobile,
            @Field("user_pin") String user_pin,
            @Field("user_ques") String user_ques,
            @Field("user_ans") String user_ans,
            @Field("Designation") String Designation,
            @Field("roleId") String roleId
    );


    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/markpointLogin")
    Call<ResponseBody> citizenLogin(
            @Field("mobile") String mobile,
            @Field("user_pin") String user_pin
    );


    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/generatePin")
    Call<ResponseBody> forgotPin(
            @Field("mobile") String mobile,
            @Field("user_pin") String user_pin,
            @Field("confirm_pin") String confirm_pin,
            @Field("user_ans") String user_ans
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/changePin")
    Call<ResponseBody> changePin(
            @Field("mobile") String mobile,
            @Field("old_pin") String old_pin,
            @Field("new_pin") String new_pin
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/dataForSurveyReport")
    Call<ResponseBody> dataForSurveyReport(
            @Field("district") String district
    );

    @GET("API_tcp_encroachment_v1.0/getSurveyReportData")
    Call<ResponseBody> getSurveyReportDataOfAllDistrict();

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/addNewPointInDatabase_v2")
    Call<ResponseBody> addNewPointWithBase64V2(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("ca_name") String ca_name,
            @Field("dev_plan") String dev_plan,
            @Field("n_d_code") String n_d_code,
            @Field("n_d_name") String n_d_name,
            @Field("n_t_code") String n_t_code,
            @Field("n_t_name") String n_t_name,
            @Field("n_v_code") String n_v_code,
            @Field("n_v_name") String n_v_name,
            @Field("n_murr_no") String n_murr_no,
            @Field("n_khas_no") String n_khas_no,
            @Field("year") String year,
            @Field("verifiedBy") String verifiedBy,
            @Field("user_name") String user_name,
            @Field("verified") String verified,
            @Field("nearByLandMark") String nearByLandMark,
            @Field("pointSource") String pointSource,
            @Field("CA_Key_GIS") String CA_Key_GIS,
            @Field("AOI_DP") String AOI_DP,
            @Field("AOI_UA") String AOI_UA,
            @Field("AOI_CA") String AOI_CA,
            @Field("verifyImg1") String verifyImg1,
            @Field("verifyImg2") String verifyImg2,
            @Field("verifyImg3") String verifyImg3,
            @Field("verifyImg4") String verifyImg4,
            @Field("verifyVideo") String verifyVideo,
            @Field("auth_status") String auth_status,
            @Field("auth_reason") String auth_reason,
            @Field("auth_remarks") String auth_remarks,
            @Field("owner_name") String owner_name,
            @Field("UAkey") String UAkey
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/addNewPointInDatabase")
    Call<ResponseBody> addNewPointWithBase64(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("ca_name") String ca_name,
            @Field("dev_plan") String dev_plan,
            @Field("n_d_code") String n_d_code,
            @Field("n_d_name") String n_d_name,
            @Field("n_t_code") String n_t_code,
            @Field("n_t_name") String n_t_name,
            @Field("n_v_code") String n_v_code,
            @Field("n_v_name") String n_v_name,
            @Field("n_murr_no") String n_murr_no,
            @Field("n_khas_no") String n_khas_no,
            @Field("year") String year,
            @Field("remarks") String remarks,
            @Field("verifiedBy") String verifiedBy,
            @Field("user_name") String user_name,
            @Field("verified") String verified,
            @Field("nearByLandMark") String nearByLandMark,
            @Field("feedback") String feedback,
            @Field("pointSource") String pointSource,
            @Field("CA_Key_GIS") String CA_Key_GIS,
            @Field("AOI_DP") String AOI_DP,
            @Field("AOI_UA") String AOI_UA,
            @Field("AOI_CA") String AOI_CA,
            @Field("verifyImg1") String verifyImg1,
            @Field("verifyImg2") String verifyImg2,
            @Field("verifyImg3") String verifyImg3,
            @Field("verifyImg4") String verifyImg4,
            @Field("verifyVideo") String verifyVideo
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/uploadDataInDatabase")
    Call<ResponseBody> updatePointBase64(
            @Field("OBJECTID") String id,
            @Field("year") String year,
            @Field("remarks") String remarks,
            @Field("verifiedBy") String verifiedBy,
            @Field("verificationDate") String verificationDate,
            @Field("user_name") String user_name,
            @Field("verified") String verified,
            @Field("nearByLandMark") String nearByLandMark,
            @Field("feedback") String feedback,
            @Field("gisId") String gisId,
            @Field("verifyImg1") String verifyImg1,
            @Field("verifyImg2") String verifyImg2,
            @Field("verifyImg3") String verifyImg3,
            @Field("verifyImg4") String verifyImg4,
            @Field("verifyVideo") String verifyVideo
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/uploadDataInDatabase_v2")
    Call<ResponseBody> updatePointBase64V2(
            @Field("OBJECTID") String id,
            @Field("year") String year,
            @Field("verifiedBy") String verifiedBy,
            @Field("verificationDate") String verificationDate,
            @Field("user_name") String user_name,
            @Field("verified") String verified,
            @Field("nearByLandMark") String nearByLandMark,
            @Field("gisId") String gisId,
            @Field("verifyImg1") String verifyImg1,
            @Field("verifyImg2") String verifyImg2,
            @Field("verifyImg3") String verifyImg3,
            @Field("verifyImg4") String verifyImg4,
            @Field("verifyVideo") String verifyVideo,
            @Field("auth_status") String auth_status,
            @Field("auth_reason") String auth_reason,
            @Field("auth_remarks") String auth_remarks,
            @Field("owner_name") String owner_name
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getCycleStartDate")
    Call<ResponseBody> getCycleStartDate(
            @Field("month") String month,
            @Field("year") String year,
            @Field("n_d_code") String n_d_code
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getCycleEndDate")
    Call<ResponseBody> getCycleEndDate(
            @Field("cycle_start_date") String cycle_start_date,
            @Field("n_d_code") String n_d_code
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getAllData")
    Call<ResponseBody> getAllDataUID(
            @Field("where") String where
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getMasterAuthUnauth")
    Call<ResponseBody> getMasterAuthUnauth(
            @Field("status") String status,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/listAccordingStatus")
    Call<ResponseBody> listAccordingStatus(
            @Field("status") String status,
            @Field("n_d_code") String n_d_code,
            @Field("n_t_code") String n_t_code,
            @Field("n_v_code") String n_v_code,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getOfficialsList")
    Call<ResponseBody> getOfficialsList(
            @Field("n_d_code") String n_d_code,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/updateStatusByAdmin")
    Call<ResponseBody> updateStatusByAdmin(
            @Field("status") String status,
            @Field("UID") String UID,
            @Field("assigner_name") String assigner_name,
            @Field("assigner_mobile") String assigner_mobile,
            @Field("assignee_name") String assignee_name,
            @Field("assignee_mobile") String assignee_mobile,
            @Field("token") String token
    );

    @GET("API_tcp_encroachment_v1.0/getStatusMaster")
    Call<ResponseBody> getStatusMaster();

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getBeforeDemolishData")
    Call<ResponseBody> getBeforeDemolishData(
            @Field("username") String username,
            @Field("UID") String UID,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getAllForwardedData")
    Call<ResponseBody> getAllForwardedData(
            @Field("username") String username,
            @Field("n_d_code") String n_d_code,
            @Field("n_t_code") String n_t_code,
            @Field("n_v_code") String n_v_code,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/getUidData")
    Call<ResponseBody> getUidData(
            @Field("UID") String UID,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("API_tcp_encroachment_v1.0/updateStausToDemolish")
    Call<ResponseBody> updateStausToDemolish(
            @Field("UID") String UID,
            @Field("token") String token,
            @Field("type") String type,
            @Field("status") String status,
            @Field("forward_id") String forward_id,
            @Field("demolished_status") String demolished_status,
            @Field("username") String username,
            @Field("img1") String img1,
            @Field("img2") String img2,
            @Field("remark") String remark
    );
}
