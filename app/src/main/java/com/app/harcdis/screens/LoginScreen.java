package com.app.harcdis.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.CitizenModule.CitizenLoginScreen;
import com.app.harcdis.R;
import com.app.harcdis.adminRole.AdminDashboardScreen;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Constants;
import com.app.harcdis.utils.Sp;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {
    private static final String TAG = "MyTag";

    String user_login_type_save = "";
    private EditText phone_number;
    private Button get_otp_button;
    private RelativeLayout relativeLayout;
    private boolean is_user_found = false;
    private ProgressDialog progressDialog;
    private String number;
    private String username = ""; // expecting mobile number here

    private TextView intro_text;
    Vibrator vi;
    Button continueAsCitizen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String is_user_login_already = Sp.read_shared_pref(LoginScreen.this, "login_status");
        String dis_code_already_save = Sp.read_shared_pref(LoginScreen.this, "dis_code_store");


        if (Sp.read_shared_pref(LoginScreen.this, "roleId") != null) {
            user_login_type_save = Sp.read_shared_pref(LoginScreen.this, "roleId");
        } else {
            user_login_type_save = "";
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate:is_user_login_already " + is_user_login_already);
            Log.d(TAG, "onCreate:dis_code_already_save " + dis_code_already_save);
            Log.d(TAG, "onCreate:is_user_login_already " + user_login_type_save);

        }
        if (is_user_login_already != null) {
            if (user_login_type_save.equalsIgnoreCase("Admin")) {
                startActivity(new Intent(LoginScreen.this, AdminDashboardScreen.class));
                finish();
//            } else if(user_login_type_save.equalsIgnoreCase("Citizen")){
//
//                startActivity(new Intent(LoginScreen.this, MapScreen.class));
//                finish();
            } else {
                if (dis_code_already_save != null) {
                    startActivity(new Intent(LoginScreen.this, MapScreen.class));
                    finish();
                } else {
                    startActivity(new Intent(LoginScreen.this, ChooseAreaScreen.class));
                    finish();
                }
            }

        }

        setContentView(R.layout.activity_login_screen);
        initViews();


        get_otp_button.setOnClickListener(v -> {
            checkValidation_CheckTable_AndSendOtp();
        });

        continueAsCitizen.setOnClickListener(v -> {
            //Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CitizenLoginScreen.class));
        });

        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }


    private void checkValidation_CheckTable_AndSendOtp() {
        number = phone_number.getText().toString().trim();
        if (number.isEmpty()) {
            phone_number.setError(getString(R.string.enter_username));
            if (vi.hasVibrator()) {
                vi.vibrate(100);
            }
        } else {
            ValidateOkSendOtp();
        }
    }


    // Change by Manish Aggarwal for SSO Login

    private void check_user_exit(String number) {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClientSSO(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.SSO_Login(
                "sms",
                number,
                "HARSAC",
                "SSO Login OTP"
                , "0",
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

                        Log.d(TAG, "onResponse: " + result);
                        progressDialog.dismiss();
                        if (message.equalsIgnoreCase("Success")) {
                            Toast.makeText(LoginScreen.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginScreen.this, OtpScreen.class);
                            intent.putExtra(Constants.mobile_number, number);
                            startActivity(intent);
                            finish();
                        } else {

                            if (vi.hasVibrator()) {
                                vi.vibrate(100);
                            }
                            Toast.makeText(LoginScreen.this, "" + message, Toast.LENGTH_SHORT).show();
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


    private void ValidateOkSendOtp() {
        progressDialog.show();
        ApiInterface retrofitAPIInterface = RetrofitClient.getRetrofitClientForOTP(LoginScreen.this).create(ApiInterface.class);
//        Call<ResponseBody> call = retrofitAPIInterface.sendOtpToUser(number);
        Call<ResponseBody> call = retrofitAPIInterface.sendOtpToUser(username, "harsac", "harsac");
//        Call<ResponseBody> call = retrofitAPIInterface.sendOtpToUser(number, "harsac", "harsac");


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.optString("responseType");
                        JSONObject responseMsg = jsonObject.optJSONObject("responseMsg");
                        progressDialog.dismiss();
                        String msg = responseMsg.optString("msg");

                        if (message.equalsIgnoreCase("success")) {
                            if (msg.equalsIgnoreCase("OTP sent!")) {
                                Toast.makeText(LoginScreen.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginScreen.this, OtpScreen.class);
                                intent.putExtra(Constants.mobile_number, number);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginScreen.this, "" + msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginScreen.this, "" + msg, Toast.LENGTH_SHORT).show();
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


    public void onFailed(String message, String description) {
        if (vi.hasVibrator()) {
            vi.vibrate(100);
        }
        Toast.makeText(this, "" + description, Toast.LENGTH_SHORT).show();
    }


    private void initViews() {
        continueAsCitizen = findViewById(R.id.continueAsCitizen);
        relativeLayout = findViewById(R.id.parentRelative);
        phone_number = findViewById(R.id.phone_number_box);
        get_otp_button = findViewById(R.id.get_otp_button);
        intro_text = findViewById(R.id.intro_text);
        intro_text.setSelected(true);
        progressDialog = new ProgressDialog(LoginScreen.this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.please_wait));

    }


}