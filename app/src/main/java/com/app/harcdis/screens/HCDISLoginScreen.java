package com.app.harcdis.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.CitizenModule.CitizenLoginScreen;
import com.app.harcdis.R;
import com.app.harcdis.adminRole.AdminDashboardScreen;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Sp;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HCDISLoginScreen extends AppCompatActivity {

    private static final String TAG = "HCDISLogin";
    private EditText otpEditText, userNameEditText;
    private Button cancelButton, signInButton, sendOtpButton, resendOtpButton, continueAsCitizen;
    private TextView timerTextView, otpSendMessageText, terms_and_conditions_text_view;

    private Vibrator vi;
    private ProgressDialog progressDialog;

    private String number;
    private String username = ""; // expecting mobile number here
    private String otp = "";
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private CountDownTimer cTimer;

    private String user_login_type_save = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcdislogin_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Autologin if already logged in
        String is_user_login_already = Sp.read_shared_pref(HCDISLoginScreen.this, "login_status");
        String dis_code_already_save = Sp.read_shared_pref(HCDISLoginScreen.this, "dis_code_store");

        if (Sp.read_shared_pref(HCDISLoginScreen.this, "roleId") != null) {
            user_login_type_save = Sp.read_shared_pref(HCDISLoginScreen.this, "roleId");
        } else {
            user_login_type_save = "";
        }

        if (is_user_login_already != null) {
            if (user_login_type_save.equalsIgnoreCase("1")) {
                startActivity(new Intent(HCDISLoginScreen.this, AdminDashboardScreen.class));
                finish();
                return;
            } else {
                if (dis_code_already_save != null) {
                    startActivity(new Intent(HCDISLoginScreen.this, MapScreen.class));
                    finish();
                    return;
                } else {
                    startActivity(new Intent(HCDISLoginScreen.this, ChooseAreaScreen.class));
                    finish();
                    return;
                }
            }
        }

        initViews();
        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initViews() {
        resendOtpButton = findViewById(R.id.resendOtpButton);
        otpSendMessageText = findViewById(R.id.otpSendMessageText);
        timerTextView = findViewById(R.id.timerTextView);
        continueAsCitizen = findViewById(R.id.continueAsCitizen);
        otpEditText = findViewById(R.id.otpEditText);
        userNameEditText = findViewById(R.id.userNameEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        signInButton = findViewById(R.id.signInButton);
        cancelButton = findViewById(R.id.cancelButton);
        terms_and_conditions_text_view = findViewById(R.id.terms_and_conditions_text_view);

        progressDialog = new ProgressDialog(HCDISLoginScreen.this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));

        resendOtpButton.setVisibility(View.GONE);
        otpSendMessageText.setVisibility(View.GONE);

        buttonFunction();
    }

    private void buttonFunction() {
        cancelButton.setOnClickListener(v -> {
            // Clear fields and reset UI
            userNameEditText.setText("");
            otpEditText.setText("");
            otpSendMessageText.setVisibility(View.GONE);
            resendOtpButton.setVisibility(View.GONE);
            timerTextView.setText("");
            if (cTimer != null) cTimer.cancel();
        });

        sendOtpButton.setOnClickListener(v -> checkValidation_CheckTable_AndSendOtp());
        resendOtpButton.setOnClickListener(v -> checkValidation_CheckTable_AndSendOtp());

        continueAsCitizen.setOnClickListener(v ->
                startActivity(new Intent(this, CitizenLoginScreen.class)));

        signInButton.setOnClickListener(v -> checkValidation_MatchOTP_AndLogin());

        terms_and_conditions_text_view.setOnClickListener(view -> {
            String url = "https://onemapdepts.gmda.gov.in/privacypolicy/HARCDISAppPrivacyPolicy.html";
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
        });
    }

    private void checkValidation_MatchOTP_AndLogin() {
        username = userNameEditText.getText().toString().trim();
        otp = otpEditText.getText().toString().trim();

        if (username.isEmpty()) {
            userNameEditText.setError(getString(R.string.enter_username));
            vibrateShort();
            return;
        }
        if (otp.isEmpty()) {
            otpEditText.setError(getString(R.string.enter_otp));
            vibrateShort();
            return;
        }
        if (otp.length() < 4) {
            otpEditText.setError(getString(R.string.enter_valid_otp));
            vibrateShort();
            return;
        }

        VerifyOtp();
    }

    private void checkValidation_CheckTable_AndSendOtp() {
        username = userNameEditText.getText().toString().trim();
        if (username.isEmpty()) {
            userNameEditText.setError(getString(R.string.enter_username));
            vibrateShort();
        } else {
            check_user_exit(username);
        }
    }

    private void check_user_exit(String mobile) {
        progressDialog.show();

        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        // using API_tcp_encroachment_v1.0/checkUserExist for mobile-based check
        Call<ResponseBody> call = apiInterface.checkUserExit(mobile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string();
                        Log.d(TAG, "check_user_exit onResponse: " +username +" "+ result);

                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        String message = jsonObject.optString("message");

                        if (status) {
                            // User exists -> proceed to send OTP
                            otpSendMessageText.setVisibility(View.VISIBLE);
                            // FIX: use a literal instead of a missing string resource
                            otpSendMessageText.setText("User found. Proceed to send OTP.");
                            ValidateOkSendOtp();
                        } else {
                            vibrateShort();
                            Toast.makeText(HCDISLoginScreen.this, message, Toast.LENGTH_SHORT).show();
                            otpSendMessageText.setVisibility(View.GONE);
                            resendOtpButton.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        logAndFail(e);
                    }
                } else {
                    onFailed("An unexpected error has occurred.",
                            "Error Code: " + response.code() + "\nPlease Try Again later ");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                networkFail(t);
            }
        });
    }

        private void ValidateOkSendOtp() {
            progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(HCDISLoginScreen.this).create(ApiInterface.class);

        // As per your spec: send_by:harsac, msgType:harsac, mobile:<number>
        Call<ResponseBody> call = apiInterface.sendOtpToUser(username, "harsac", "harsac_login_otp");
//        Call<ResponseBody> call = apiInterface.sendOtpToUser(number, "harsac", "harsac");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string();



                        // 🔹 Add log here
//                        Log.d("OTP_API", "Raw OTP API Response: " + result);


                        JSONObject jsonObject = new JSONObject(result);

                        // Try to handle generic success keys
                        String responseType = jsonObject.optString("responseType", "");
                        String msg = "";
                        if (jsonObject.has("responseMsg") && jsonObject.opt("responseMsg") instanceof JSONObject) {
                            msg = jsonObject.optJSONObject("responseMsg").optString("msg", "");
                        } else {
                            msg = jsonObject.optString("message", "OTP sent (if mobile is valid).");
                        }

                        if ("success".equalsIgnoreCase(responseType) || msg.toLowerCase().contains("otp")) {
                            Toast.makeText(HCDISLoginScreen.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                            otpSendMessageText.setVisibility(View.VISIBLE);
                            otpSendMessageText.setText(getString(R.string.otp_sent));
                            resendOtpButton.setVisibility(View.GONE);
                            startTimer();
                        } else {
                            Toast.makeText(HCDISLoginScreen.this, msg.isEmpty() ? "Failed to send OTP." : msg, Toast.LENGTH_SHORT).show();
                            resendOtpButton.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        logAndFail(e);
                        resendOtpButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    onFailed("An unexpected error has occurred.",
                            "Error Code: " + response.code() + "\nPlease Try Again later ");
                    resendOtpButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                networkFail(t);
                resendOtpButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void VerifyOtp() {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.VerifyOtpUser(username, otp);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);

                        // Typical success keys
                        String responseType = jsonObject.optString("responseType", "");
                        String message = "";
                        if (jsonObject.has("responseMsg") && jsonObject.opt("responseMsg") instanceof JSONObject) {
                            message = jsonObject.optJSONObject("responseMsg").optString("msg", "");
                        } else {
                            message = jsonObject.optString("message", "");
                        }

                        boolean isSuccess = "success".equalsIgnoreCase(responseType)
                                || "success".equalsIgnoreCase(jsonObject.optString("Result"))
                                || message.toLowerCase().contains("verified");

                        if (isSuccess) {
                            Toast.makeText(HCDISLoginScreen.this, "OTP Verified", Toast.LENGTH_SHORT).show();

                            // Mark login + proceed. If your backend returns role later, you can enrich here.
                            Sp.write_shared_pref(HCDISLoginScreen.this, "login_status", "true");
                            Sp.write_shared_pref(HCDISLoginScreen.this, "processFlowStatus", "Not Seen");
                            // Optionally log location
                            makeLocationHistory();

                        } else {
                            vibrateShort();
                            Toast.makeText(HCDISLoginScreen.this,
                                    message.isEmpty() ? "Invalid OTP" : message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        logAndFail(e);
                    }
                } else {
                    onFailed("An unexpected error has occurred.",
                            "Error Code: " + response.code() + "\nPlease Try Again later ");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                networkFail(t);
            }
        });
    }

    // ======= (Optionally used elsewhere in your app) Register/Role based routing Helpers =======

    private void register_sso_user(String MobileNo, String DesignationID, String Designation, String OfficeID,
                                   String OfficeName, String userGroupId, String userid, String name,
                                   String username, String emailid) {

        ApiInterface api = RetrofitClient.getRetrofitClient(HCDISLoginScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = api.Register_SSO_User(
                MobileNo, DesignationID, Designation, OfficeID, OfficeName, userGroupId, userid, name, username, emailid
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObjectData = new JSONObject(result);
                        String message = jsonObjectData.optString("message");
                        boolean status = jsonObjectData.optBoolean("status");

                        Log.d(TAG, "register_sso_user onResponse: " + jsonObjectData);
                        if (status) {
                            JSONObject jsonObject = jsonObjectData.optJSONArray("data").getJSONObject(0);
                            Toast.makeText(HCDISLoginScreen.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                            if (!Objects.equals("null", jsonObject.optString("mobile"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "user_mobile", jsonObject.optString("mobile"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("DesignationID"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "DesignationID", jsonObject.optString("DesignationID"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("designation"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "Designation", jsonObject.optString("designation"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("OfficeID"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "OfficeID", jsonObject.optString("OfficeID"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("OfficeName"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "OfficeName", jsonObject.optString("OfficeName"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("name"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "name", jsonObject.optString("name"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("username"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "user_name", jsonObject.optString("username"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("emailid"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "emailid", jsonObject.optString("emailid"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("SSOLogOut"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "SSOLogOut", jsonObject.optString("SSOLogOut"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("logintime"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "logintime", jsonObject.optString("logintime"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("uid"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "userid", jsonObject.optString("uid"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("n_d_code"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "n_d_code", jsonObject.optString("n_d_code"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("n_d_name"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "n_d_name", jsonObject.optString("n_d_name"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("roleId"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "roleId", jsonObject.optString("roleId"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("assignedCA"))) {
                                Sp.write_shared_pref(HCDISLoginScreen.this, "assignedCA", jsonObject.optString("assignedCA"));
                            }

                            save_data_and_login_successfully2();
                        } else {
                            Toast.makeText(HCDISLoginScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        logAndFail(e);
                    }
                } else {
                    onFailed("An unexpected error has occurred.",
                            "Error Code: " + response.code() + "\nPlease Try Again later ");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                networkFail(t);
            }
        });
    }

    private void save_data_and_login_successfully2() {
        String roleId = Sp.read_shared_pref(HCDISLoginScreen.this, "roleId");
        Log.d(TAG, "save_data_and_login_successfully2: " + roleId);

        if ("1".equalsIgnoreCase(roleId)) {
            // Super Admin
            Sp.write_shared_pref(HCDISLoginScreen.this, "login_status", "true");
            Sp.write_shared_pref(HCDISLoginScreen.this, "processFlowStatus", "Not Seen");
            Toast.makeText(this, "Welcome Super Admin", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HCDISLoginScreen.this, AdminDashboardScreen.class));
            finish();
        } else if ("2".equalsIgnoreCase(roleId)) {
            // Admin
            Sp.write_shared_pref(HCDISLoginScreen.this, "login_status", "true");
            Sp.write_shared_pref(HCDISLoginScreen.this, "processFlowStatus", "Not Seen");
            Sp.write_shared_pref(HCDISLoginScreen.this, "assignedCA", null);
            Sp.write_shared_pref(HCDISLoginScreen.this, "roleName", "Admin");
            Sp.write_shared_pref(this, "userAccess", "Admin");
            makeLocationHistory();
        } else if ("3".equalsIgnoreCase(roleId)) {
            // Officials
            Sp.write_shared_pref(HCDISLoginScreen.this, "login_status", "true");
            Sp.write_shared_pref(HCDISLoginScreen.this, "processFlowStatus", "Not Seen");
            Sp.write_shared_pref(HCDISLoginScreen.this, "assignedCA", null);
            Sp.write_shared_pref(HCDISLoginScreen.this, "roleName", "Officials");
            Sp.write_shared_pref(this, "userAccess", "Officials");
            makeLocationHistory();
        } else if ("4".equalsIgnoreCase(roleId)) {
            // Surveyor
            Sp.write_shared_pref(HCDISLoginScreen.this, "login_status", "true");
            Sp.write_shared_pref(HCDISLoginScreen.this, "processFlowStatus", "Not Seen");
            Sp.write_shared_pref(this, "userAccess", "Surveyor");
            makeLocationHistory();
        } else if ("5".equalsIgnoreCase(roleId)) {
            // Viewer
            Sp.write_shared_pref(HCDISLoginScreen.this, "login_status", "true");
            Sp.write_shared_pref(HCDISLoginScreen.this, "processFlowStatus", "Not Seen");
            Sp.write_shared_pref(this, "userAccess", "Viewer");
            makeLocationHistory();
        } else if ("0".equalsIgnoreCase(roleId)) {
            Toast.makeText(this, "No Access To Use This App", Toast.LENGTH_SHORT).show();
        } else {
            // If roleId unavailable (simple OTP-only flow), continue to ChooseArea.
            startActivity(new Intent(HCDISLoginScreen.this, ChooseAreaScreen.class));
            finish();
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void makeLocationHistory() {
        ApiInterface api = RetrofitClient.getRetrofitClient(HCDISLoginScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = api.locationHistory(username,
                String.valueOf(latitude),
                String.valueOf(longitude),
                getDateTime(),
                "LOGIN");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);

                        boolean status = jsonObject.optBoolean("status");
                        String message = jsonObject.optString("message");
                        if (status && "Success".equalsIgnoreCase(message)) {
                            Toast.makeText(HCDISLoginScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(HCDISLoginScreen.this, ChooseAreaScreen.class));
                            finish();
                        } else {
                            Toast.makeText(HCDISLoginScreen.this, message, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "locationHistory onResponse: " + message);
                            startActivity(new Intent(HCDISLoginScreen.this, ChooseAreaScreen.class));
                            finish();
                        }
                    } catch (Exception e) {
                        logAndFail(e);
                    }
                } else {
                    onFailed("An unexpected error has occurred.",
                            "Error Code: " + response.code() + "\nPlease Try Again later ");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                networkFail(t);
            }
        });
    }

    private void onFailed(String title, String description) {
        vibrateShort();
        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
    }

    private void startTimer() {
        if (cTimer != null) cTimer.cancel();
        cTimer = new CountDownTimer(59000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(getString(R.string.time_left) + (millisUntilFinished / 1000));
            }

            public void onFinish() {
                timerTextView.setText("");
                resendOtpButton.setVisibility(View.VISIBLE);
            }
        };
        cTimer.start();
    }

    private void vibrateShort() {
        if (vi == null) return;
        if (vi.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vi.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vi.vibrate(100);
            }
        }
    }

    private void logAndFail(Exception e) {
        e.printStackTrace();
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Resp Exc: " + e.getMessage());
        }
        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\nPlease Try Again later ");
    }

    private void networkFail(Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Resp onFailure: " + t.getMessage());
        }
        String msg = (t.getMessage() != null) ? t.getMessage().toLowerCase() : "";
        if (msg.contains("unable to resolve host") || msg.contains("timeout")) {
            onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");
        } else {
            onFailed("An unexpected error has occurred.", "Error Failure: " + t.getMessage());
        }
    }
}
