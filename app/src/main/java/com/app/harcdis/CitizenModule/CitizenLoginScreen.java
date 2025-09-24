package com.app.harcdis.CitizenModule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.screens.ChooseAreaScreen;
import com.app.harcdis.screens.HCDISLoginScreen;
import com.app.harcdis.utils.Sp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitizenLoginScreen extends AppCompatActivity {

    TextView registerAsCitizen, forgot_pin_textview;
    Button continueAsUser, loginCitizen;
    EditText id_box_citizen, pin_box_citizen;

    Vibrator vi;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_login_screen);

        // Hide the action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize vibrator
        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Progress dialog
        progressDialog = new ProgressDialog(CitizenLoginScreen.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        // Bind views
        registerAsCitizen = findViewById(R.id.registerAsCitizen);
        continueAsUser = findViewById(R.id.continueAsUser);
        loginCitizen = findViewById(R.id.loginCitizen);
        id_box_citizen = findViewById(R.id.id_box_citizen);
        pin_box_citizen = findViewById(R.id.pin_box_citizen);
        forgot_pin_textview = findViewById(R.id.forgot_pin_textview);

        // Forgot PIN screen
        forgot_pin_textview.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPinScreen.class));
        });

        // Registration screen
        registerAsCitizen.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistrationScreen.class));
        });

        // Continue as user (redirect to HCDIS login)
        continueAsUser.setOnClickListener(v -> {
            startActivity(new Intent(this, HCDISLoginScreen.class));
            finishAffinity();
        });

        // Login as citizen
        loginCitizen.setOnClickListener(v -> {
            if (id_box_citizen.getText().toString().isEmpty()) {
                id_box_citizen.setError(getString(R.string.enter_mobile_no));
                callVibrator();
            } else if (pin_box_citizen.getText().toString().isEmpty()) {
                pin_box_citizen.setError(getString(R.string.enter_pin));
                callVibrator();
            } else {
                // Call API
                citizenLoginInApp(id_box_citizen.getText().toString(),
                        pin_box_citizen.getText().toString());
            }
        });
    }

    /**
     * API Call for Citizen Login
     */
    private void citizenLoginInApp(String mobile, String pin) {
        progressDialog.show();

        ApiInterface apiInterface = RetrofitClient
                .getRetrofitClient(CitizenLoginScreen.this)
                .create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.citizenLogin(mobile, pin);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        Log.i("LoginResp", result); // Log the raw response

                        JSONObject jsonObjectResult = new JSONObject(result);
                        boolean status = jsonObjectResult.optBoolean("status");
                        String message = jsonObjectResult.optString("message");

                        if (status) {
                            // Relaxed condition: check only if message contains "logged in successfully"
                            if (message.toLowerCase().contains("logged in successfully")) {
                                JSONArray result_array = jsonObjectResult.getJSONArray("data");
                                if (result_array.length() > 0) {
                                    JSONObject jsonObject = result_array.getJSONObject(0);

                                    // Save response values into SharedPreferences
                                    if (!Objects.equals("null", jsonObject.optString("mobile"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "user_mobile", jsonObject.optString("mobile"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("DesignationID"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "DesignationID", jsonObject.optString("DesignationID"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("designation"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "Designation", jsonObject.optString("designation"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("OfficeID"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "OfficeID", jsonObject.optString("OfficeID"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("OfficeName"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "OfficeName", jsonObject.optString("OfficeName"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("name"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "name", jsonObject.optString("name"));
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "user_name", jsonObject.optString("name"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("email"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "emailid", jsonObject.optString("email"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("OBJECTID"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "userid", jsonObject.optString("OBJECTID"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("n_d_code"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "n_d_code", jsonObject.optString("n_d_code"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("n_d_name"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "n_d_name", jsonObject.optString("n_d_name"));
                                    }
                                    if (!Objects.equals("null", jsonObject.optString("roleId"))) {
                                        Sp.write_shared_pref(CitizenLoginScreen.this,
                                                "roleId", jsonObject.optString("roleId"));
                                    }

                                    // Set login flags
                                    Sp.write_shared_pref(CitizenLoginScreen.this,
                                            "login_status", "true");
                                    Sp.write_shared_pref(CitizenLoginScreen.this,
                                            "processFlowStatus", "Not Seen");

                                    // Redirect to ChooseAreaScreen
                                    startActivity(new Intent(CitizenLoginScreen.this,
                                            ChooseAreaScreen.class));
                                    finish();

                                } else {
                                    Toast.makeText(CitizenLoginScreen.this,
                                            "No user data received", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(CitizenLoginScreen.this,
                                        message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CitizenLoginScreen.this,
                                    message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed(" An unexpected error has occurred",
                                "Error: " + e.getMessage() + "\nPlease Try Again later");
                    }

                } else {
                    // Handle API error codes
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("API Error",
                            "Error Code: " + response.code() + "\nPlease Try Again later");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                if (BuildConfig.DEBUG) {
                    Log.i("Resp onFailure: ", "" + t.getMessage());
                }

                if (t.getMessage() != null &&
                        (t.getMessage().startsWith("Unable to resolve host") ||
                                t.getMessage().startsWith("timeout"))) {
                    onFailed("Slow or No Connection!",
                            "Check Your Network Settings & try again.");
                } else {
                    onFailed("Network Failure",
                            "Error Failure: " + t.getMessage());
                }
            }
        });
    }

    /**
     * Trigger phone vibration when error happens
     */
    public void callVibrator() {
        if (vi != null && vi.hasVibrator()) {
            vi.vibrate(100);
        }
    }

    /**
     * Common method to handle API failure
     */
    public void onFailed(String message, String description) {
        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
    }
}




//
//
//package com.app.harcdis.CitizenModule;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Vibrator;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.app.harcdis.BuildConfig;
//import com.app.harcdis.R;
//import com.app.harcdis.api.ApiInterface;
//import com.app.harcdis.api.RetrofitClient;
//import com.app.harcdis.screens.ChooseAreaScreen;
//import com.app.harcdis.screens.HCDISLoginScreen;
//import com.app.harcdis.screens.LoginScreen;
//import com.app.harcdis.utils.Sp;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.Objects;
//
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class CitizenLoginScreen extends AppCompatActivity {
//    TextView registerAsCitizen;
//    Button continueAsUser;
//    Button loginCitizen;
//    EditText id_box_citizen, pin_box_citizen;
//    TextView forgot_pin_textview;
//
//    Vibrator vi;
//    private ProgressDialog progressDialog;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_citizen_login_screen);
//
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }
//
//        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
//        progressDialog = new ProgressDialog(CitizenLoginScreen.this);
//        progressDialog.setMessage(getString(R.string.loading));
//        progressDialog.setMessage(getString(R.string.please_wait));
//        progressDialog.setCancelable(false);
//
//        registerAsCitizen = findViewById(R.id.registerAsCitizen);
//        continueAsUser = findViewById(R.id.continueAsUser);
//        loginCitizen = findViewById(R.id.loginCitizen);
//        id_box_citizen = findViewById(R.id.id_box_citizen);
//        pin_box_citizen = findViewById(R.id.pin_box_citizen);
//        forgot_pin_textview = findViewById(R.id.forgot_pin_textview);
//
//        forgot_pin_textview.setOnClickListener(v -> {
//            startActivity(new Intent(this, ForgotPinScreen.class));
//        });
//        registerAsCitizen.setOnClickListener(v -> {
//            startActivity(new Intent(this, RegistrationScreen.class));
//        });
//        continueAsUser.setOnClickListener(v -> {
//            startActivity(new Intent(this, HCDISLoginScreen.class));
//            finishAffinity();
//        });
//
//        loginCitizen.setOnClickListener(v -> {
//            if (id_box_citizen.getText().toString().isEmpty()) {
//                id_box_citizen.setError(getString(R.string.enter_mobile_no));
//                callVibrator();
//            } else if (pin_box_citizen.getText().toString().isEmpty()) {
//                pin_box_citizen.setError(getString(R.string.enter_pin));
//                callVibrator();
//            } else {
//                citizenLoginInApp(id_box_citizen.getText().toString(), pin_box_citizen.getText().toString());
//            }
//        });
//
//
//    }
//
//    private void citizenLoginInApp(String mobile, String pin) {
//
//        progressDialog.show();
//        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(CitizenLoginScreen.this).create(ApiInterface.class);
//        Call<ResponseBody> call = apiInterface.citizenLogin(mobile, pin);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    try {
//                        String result = response.body().string();
//                        JSONObject jsonObjectResult = new JSONObject(result);
//                        boolean status = jsonObjectResult.optBoolean("status");
//                        String message = jsonObjectResult.optString("message");
//
//                        progressDialog.dismiss();
//                        if (status) {
//                            if (message.equalsIgnoreCase("Citizen Logged In Successfully")) {
//                                JSONArray result_array = jsonObjectResult.getJSONArray("data");
//                                if (result_array.length() > 0) {
//                                    JSONObject jsonObject = result_array.getJSONObject(0);
//
//                                    Toast.makeText(CitizenLoginScreen.this, message, Toast.LENGTH_SHORT).show();
//                                    if (!Objects.equals("null", jsonObject.optString("mobile"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "user_mobile", jsonObject.optString("mobile"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("DesignationID"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "DesignationID", jsonObject.optString("DesignationID"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("Designation"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "Designation", jsonObject.optString("designation"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("OfficeID"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "OfficeID", jsonObject.optString("OfficeID"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("OfficeName"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "OfficeName", jsonObject.optString("OfficeName"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("name"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "name", jsonObject.optString("name"));
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "user_name", jsonObject.optString("name"));
//                                    }
////                                    if (!Objects.equals("null", jsonObject.optString("username"))) {
////                                        Sp.write_shared_pref(CitizenLoginScreen.this, "user_name", jsonObject.optString("username"));
////                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("emailid"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "emailid", jsonObject.optString("emailid"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("SSOLogOut"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "SSOLogOut", jsonObject.optString("SSOLogOut"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("logintime"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "logintime", jsonObject.optString("logintime"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("userid"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "userid", jsonObject.optString("uid"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("n_d_code"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "n_d_code", jsonObject.optString("n_d_code"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("n_d_name"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "n_d_name", jsonObject.optString("n_d_name"));
//                                    }
//                                    if (!Objects.equals("null", jsonObject.optString("roleId"))) {
//                                        Sp.write_shared_pref(CitizenLoginScreen.this, "roleId", jsonObject.optString("roleId"));
//                                    }
//
//                                    Sp.write_shared_pref(CitizenLoginScreen.this, "login_status", "true");
//                                    Sp.write_shared_pref(CitizenLoginScreen.this, "processFlowStatus", "Not Seen");
//
//                                    startActivity(new Intent(CitizenLoginScreen.this, ChooseAreaScreen.class));
//                                    finish();
//
//                                } else {
//                                    Toast.makeText(CitizenLoginScreen.this, message, Toast.LENGTH_SHORT).show();
//                                }
//
//                            } else {
//                                Toast.makeText(CitizenLoginScreen.this, message, Toast.LENGTH_SHORT).show();
//                            }
//
//                        } else {
//                            Toast.makeText(CitizenLoginScreen.this, message, Toast.LENGTH_SHORT).show();
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
//    }
//
//    public void callVibrator() {
//        if (vi.hasVibrator()) {
//            vi.vibrate(100);
//        }
//    }
//
//    public void onFailed(String message, String description) {
//        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
//    }
//}