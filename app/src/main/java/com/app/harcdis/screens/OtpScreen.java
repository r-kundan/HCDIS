package com.app.harcdis.screens;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.R;
import com.app.harcdis.adminRole.AdminDashboardScreen;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Constants;
import com.app.harcdis.utils.LocationAssistant;
import com.app.harcdis.utils.Sp;
import com.google.firebase.BuildConfig;
import com.poovam.pinedittextfield.SquarePinField;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyTag";
    private final int requestCode = 2;
    private final String[] reqPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    SquarePinField squareFieldOtp;
    ProgressDialog progressDialog;
    TextView timeleft, resend_new_code;
    String received_mobile_number = "";
    String otp = "";
    LocationManager locationManager;
    Location location;
    ProgressBar resend_otp_progress_bar;
    private Button validateBtn;
    private RelativeLayout parentRelativeOtp;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private LocationAssistant assistant;
    Vibrator vi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_screen);
        // assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 4000, false);
        // assistant.setVerbose(true);
        Intent intent = getIntent();
        received_mobile_number = intent.getStringExtra(Constants.mobile_number);
        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        initViews();

        squareFieldOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                otp = String.valueOf(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        squareFieldOtp.setOnTextCompleteListener(s -> true);


//
//        boolean permissionCheck1 = ContextCompat.checkSelfPermission(OtpScreen.this, reqPermissions[0]) ==
//                PackageManager.PERMISSION_GRANTED;
//        boolean permissionCheck2 = ContextCompat.checkSelfPermission(OtpScreen.this, reqPermissions[1]) ==
//                PackageManager.PERMISSION_GRANTED;
//
//        if (!(permissionCheck1 && permissionCheck2)) {
//            // If permissions are not already granted, request permission from the user.
//            ActivityCompat.requestPermissions(OtpScreen.this, reqPermissions, requestCode);
//        } else {
//            Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
//
//        }
//
//        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//        if (Objects.equals("null", location)) {
//            Log.d(TAG, "onCreate: " + location);
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//        }

    }


    private void initViews() {
        resend_otp_progress_bar = findViewById(R.id.resend_otp_progress_bar);
        squareFieldOtp = findViewById(R.id.squareFieldOtp);
        parentRelativeOtp = findViewById(R.id.parentRelativeOtp);
        validateBtn = findViewById(R.id.validateBtn);
        resend_new_code = findViewById(R.id.resend_new_code);
        progressDialog = new ProgressDialog(OtpScreen.this);
        progressDialog.setTitle(R.string.app_name);
        progressDialog.setMessage(getString(R.string.velidate_and_loading_data));
        progressDialog.setCancelable(false);
        validateBtn.setOnClickListener(this);
        resend_new_code.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.validateBtn: {
                if (otp.isEmpty()) {
                    if (vi.hasVibrator()) {
                        vi.vibrate(100);
                    }
                    Toast.makeText(this, getString(R.string.enter_otp), Toast.LENGTH_SHORT).show();

                } else if (otp.length() < 4) {
                    if (vi.hasVibrator()) {
                        vi.vibrate(100);
                    }
                    Toast.makeText(this, getString(R.string.enter_valid_otp), Toast.LENGTH_SHORT).show();

                } else {
                    VerifyOtp();
                }
                break;
            }
            case R.id.resend_new_code: {
                resend_otp();
            }
        }
    }

    private void resend_otp() {
        resend_otp_progress_bar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RetrofitClient.getRetrofitClientSSO(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.SSO_Login(
                "sms",
                received_mobile_number,
                "HARSAC",
                "SSO Login OTP"
                , "0",
                "true"
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.optString("Result");
                        Log.d(TAG, "onResponse: " + result);
                        resend_otp_progress_bar.setVisibility(View.GONE);
                        if (message.equalsIgnoreCase("Success")) {
                            Toast.makeText(OtpScreen.this, "OTP Re-send Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OtpScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        resend_otp_progress_bar.setVisibility(View.GONE);
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    resend_otp_progress_bar.setVisibility(View.GONE);
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {

                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    resend_otp_progress_bar.setVisibility(View.GONE);
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resend_otp_progress_bar.setVisibility(View.GONE);
                if (BuildConfig.DEBUG) {
                    Log.i("Resp onFailure: ", "" + t.getMessage());
                }
                if (t.getMessage().startsWith("Unable to resolve host")) {
                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                } else if (t.getMessage().startsWith("timeout")) {
                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                } else {
                    onFailed("An unexpected error has occurred.", "Error Failure: " + t.getMessage());


                }
            }
        });

    }

    private void onFailed(String s, String s1) {
        if (vi.hasVibrator()) {
            vi.vibrate(100);
        }
        Toast.makeText(this, "" + s1, Toast.LENGTH_SHORT).show();
    }


    private void VerifyOtp() {
        progressDialog.show();
        ApiInterface retrofitAPIInterface = RetrofitClient.getRetrofitClientSSO(this).create(ApiInterface.class);
        Call<ResponseBody> call = retrofitAPIInterface.SSO_Login(
                "login",
                received_mobile_number,
                "HARSAC",
                "SSO Login OTP"
                , otp,
                "false"

        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.optString("Result");


                            Log.d(TAG, "onResponse: "+jsonObject);

                        if (message.equalsIgnoreCase("Success")) {
                            register_sso_user(
                                    jsonObject.optString("MobileNo"),
                                    jsonObject.optString("DesignationID"),
                                    jsonObject.optString("Designation"),
                                    jsonObject.optString("OfficeID"),
                                    jsonObject.optString("OfficeName"),
                                    jsonObject.optString("userGroupId"),
                                    jsonObject.optString("userid"),
                                    jsonObject.optString("name"),
                                    jsonObject.optString("username"),
                                    jsonObject.optString("emailid")
                            );
                        } else {
                            progressDialog.dismiss();
                            if (vi.hasVibrator()) {
                                vi.vibrate(100);
                            }
                            Toast.makeText(OtpScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                if (BuildConfig.DEBUG) {
                    Log.i("Resp onFailure: ", "" + t.getMessage());
                }
                if (t.getMessage().startsWith("Unable to resolve host")) {
                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                } else if (t.getMessage().startsWith("timeout")) {
                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                } else {
                    onFailed("An unexpected error has occurred.", "Error Failure: " + t.getMessage());


                }
            }
        });

    }

    private void register_sso_user(String MobileNo, String DesignationID, String Designation, String OfficeID, String OfficeName, String userGroupId, String userid, String name, String username, String emailid) {
        ApiInterface retrofitAPIInterface = RetrofitClient.getRetrofitClient(OtpScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = retrofitAPIInterface.Register_SSO_User(
                MobileNo, DesignationID, Designation, OfficeID, OfficeName, userGroupId, userid, name, username, emailid

        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObjectData = new JSONObject(result);
                        String message = jsonObjectData.optString("message");
                        boolean status = jsonObjectData.optBoolean("status");

                        Log.d(TAG, "onResponse: "+jsonObjectData);
                        progressDialog.dismiss();
                        if (status) {

                            JSONObject jsonObject = jsonObjectData.optJSONArray("data").getJSONObject(0);
                            Toast.makeText(OtpScreen.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                            if (!Objects.equals("null", jsonObject.optString("mobile"))) {
                                Sp.write_shared_pref(OtpScreen.this, "user_mobile", jsonObject.optString("mobile"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("DesignationID"))) {
                                Sp.write_shared_pref(OtpScreen.this, "DesignationID", jsonObject.optString("DesignationID"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("Designation"))) {
                                Sp.write_shared_pref(OtpScreen.this, "Designation", jsonObject.optString("designation"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("OfficeID"))) {
                                Sp.write_shared_pref(OtpScreen.this, "OfficeID", jsonObject.optString("OfficeID"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("OfficeName"))) {
                                Sp.write_shared_pref(OtpScreen.this, "OfficeName", jsonObject.optString("OfficeName"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("name"))) {
                                Sp.write_shared_pref(OtpScreen.this, "name", jsonObject.optString("name"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("username"))) {
                                Sp.write_shared_pref(OtpScreen.this, "user_name", jsonObject.optString("username"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("emailid"))) {
                                Sp.write_shared_pref(OtpScreen.this, "emailid", jsonObject.optString("emailid"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("SSOLogOut"))) {
                                Sp.write_shared_pref(OtpScreen.this, "SSOLogOut", jsonObject.optString("SSOLogOut"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("logintime"))) {
                                Sp.write_shared_pref(OtpScreen.this, "logintime", jsonObject.optString("logintime"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("userid"))) {
                                Sp.write_shared_pref(OtpScreen.this, "userid", jsonObject.optString("uid"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("n_d_code"))) {
                                Sp.write_shared_pref(OtpScreen.this, "n_d_code", jsonObject.optString("n_d_code"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("n_d_name"))) {
                                Log.d(TAG, "onResponse:n_d_name "+jsonObject.optString("n_d_name"));
                                Sp.write_shared_pref(OtpScreen.this, "n_d_name", jsonObject.optString("n_d_name"));
                            }
                            if (!Objects.equals("null", jsonObject.optString("roleId"))) {
                                Sp.write_shared_pref(OtpScreen.this, "roleId", jsonObject.optString("roleId"));
                            }

                            save_data_and_login_successfully2();


                        } else {
                            Toast.makeText(OtpScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                if (BuildConfig.DEBUG) {
                    Log.i("Resp onFailure: ", "" + t.getMessage());
                }
                if (t.getMessage().startsWith("Unable to resolve host")) {
                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                } else if (t.getMessage().startsWith("timeout")) {
                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                } else {
                    onFailed("An unexpected error has occurred.", "Error Failure: " + t.getMessage());


                }
            }
        });

    }

    private void showSnackBar(String s) {
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();

    }

    private void save_data_and_login_successfully2() {
        Sp.write_shared_pref(OtpScreen.this, "login_status", "true");
        Sp.write_shared_pref(OtpScreen.this, "processFlowStatus", "Not Seen");

//
//        if (Sp.read_shared_pref(OtpScreen.this, "DesignationID").equalsIgnoreCase("12")) {
//            makeLocationHistory();
//
//        } else

    if (Sp.read_shared_pref(OtpScreen.this, "roleId").equalsIgnoreCase("Admin")) {
            startActivity(new Intent(OtpScreen.this, AdminDashboardScreen.class));
            finish();
        }else{
            makeLocationHistory();
        }





//        else if (Sp.read_shared_pref(OtpScreen.this, "n_d_code").equalsIgnoreCase("99")) {
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(OtpScreen.this);
//            builder.setMessage(getString(R.string.select_one));
//            builder.setCancelable(false);
//            builder.setPositiveButton("User", (dialog, id) -> {
//                        dialog.dismiss();
//                        makeLocationHistory();
//                    }
//            );
//            builder.setNegativeButton("Admin", (dialog, id) -> {
//                        dialog.dismiss();
//                        startActivity(new Intent(OtpScreen.this, AdminDashboardScreen.class));
//                        finish();
//
//                    }
//            );
//            builder.show();
//        }

    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }


    private void makeLocationHistory() {

        //progressDialog.show();
        ApiInterface retrofitAPIInterface = RetrofitClient.getRetrofitClient(OtpScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = retrofitAPIInterface.locationHistory(received_mobile_number, String.valueOf(latitude), String.valueOf(longitude), getDateTime(), "LOGIN");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);

                        boolean status = jsonObject.optBoolean("status");
                        String message = jsonObject.optString("message");
                        progressDialog.dismiss();
                        if (status) {
                            if (message.equalsIgnoreCase("Success")) {

                                Toast.makeText(OtpScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(OtpScreen.this, ChooseAreaScreen.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(OtpScreen.this, message, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: " + message);
                        }


                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                if (BuildConfig.DEBUG) {
                    Log.i("Resp onFailure: ", "" + t.getMessage());
                }
                if (t.getMessage().startsWith("Unable to resolve host")) {
                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                } else if (t.getMessage().startsWith("timeout")) {
                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                } else {
                    onFailed("An unexpected error has occurred.", "Error Failure: " + t.getMessage());


                }
            }
        });

    }


//    private void fetch_user_details_and_show_by_api() {
//        progressDialog.show();
//        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(OtpScreen.this).create(ApiInterface.class);
//        Call<ResponseBody> call = apiInterface.getUserData(received_mobile_number);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    try {
//                        String result = response.body().string();
//                        JSONObject jsonObject = new JSONObject(result);
//                        String message = jsonObject.optString("message");
//
//                        boolean status = jsonObject.optBoolean("status");
//                        progressDialog.dismiss();
//
//                        if (message.equalsIgnoreCase("sucess")) {
//                            if (status) {
//                                JSONObject jsonObject1 = jsonObject.optJSONObject("data");
//                                Sp.write_shared_pref(OtpScreen.this, "user_name", jsonObject1.optString("name"));
//                                Sp.write_shared_pref(OtpScreen.this, "user_mobile", jsonObject1.optString("mobile"));
//                                Sp.write_shared_pref(OtpScreen.this, "roleId", jsonObject1.optString("roleId"));
//                                Sp.write_shared_pref(OtpScreen.this, "processFlowStatus", "Not Seen");
//                                save_data_and_login_successfully2(received_mobile_number);
//
//                            } else {
//                                Toast.makeText(OtpScreen.this, "" + message, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(OtpScreen.this, "" + message, Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    } catch (Exception e) {
//                        progressDialog.dismiss();
//                        e.printStackTrace();
//                        if (BuildConfig.DEBUG) {
//                            Log.i("Resp Exc: ", e.getMessage() + "");
//                        }
//                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
//                    }
//
//
//                } else if (response.code() == 404) {
//                    progressDialog.dismiss();
//                    if (BuildConfig.DEBUG) {
//                        Log.i("Resp Exc: ", "" + response.code());
//                    }
//                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");
//
//
//                } else {
//                    progressDialog.dismiss();
//                    if (BuildConfig.DEBUG) {
//                        Log.i("Resp Exc: ", "" + response.code());
//                    }
//                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");
//
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                progressDialog.dismiss();
//                if (BuildConfig.DEBUG) {
//                    Log.i("Resp onFailure: ", "" + t.getMessage());
//                }
//                if (t.getMessage().startsWith("Unable to resolve host")) {
//                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");
//
//
//                } else if (t.getMessage().startsWith("timeout")) {
//                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");
//
//
//                } else {
//                    onFailed("An unexpected error has occurred.", "Error Failure: " + t.getMessage());
//
//
//                }
//            }
//        });
//
//    }

}
