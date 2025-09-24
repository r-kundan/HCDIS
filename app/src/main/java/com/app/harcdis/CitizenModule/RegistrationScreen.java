package com.app.harcdis.CitizenModule;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.screens.MapScreen;
import com.app.harcdis.utils.Sp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationScreen extends AppCompatActivity {

    EditText new_full_name, new_mobile, pin_box, confirm_pin_box, new_answer;
    TextView alreadyMember;
    Button createAccountButton;
    Spinner districtSpinner, questionSpinner;
    private ArrayList<String> districtList, districtId;
    private ArrayList<String> questionList, questionId;
    private String district_sending, district_id_sending;
    private String question_sending, question_id_sending;

    Button backToInfoButton, continueButton;
    LinearLayout user_pin_layout, user_info_layout;
    Vibrator vi;
    private ProgressDialog progressDialog;
    String userPin;
    String userMobile;
    String userName;
    String userAnswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        progressDialog = new ProgressDialog(RegistrationScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        districtList = new ArrayList<>();
        districtId = new ArrayList<>();

        questionList = new ArrayList<>();
        questionId = new ArrayList<>();

        districtSpinner = findViewById(R.id.districtSpinner);
        questionSpinner = findViewById(R.id.questionSpinner);


        new_full_name = findViewById(R.id.new_full_name);
        new_mobile = findViewById(R.id.new_mobile);
        pin_box = findViewById(R.id.pin_box);
        confirm_pin_box = findViewById(R.id.confirm_pin_box);
        new_answer = findViewById(R.id.new_answer);
        alreadyMember = findViewById(R.id.alreadyMember);
        createAccountButton = findViewById(R.id.createAccountButton);
        continueButton = findViewById(R.id.continueButton);
        backToInfoButton = findViewById(R.id.backToInfoButton);
        user_info_layout = findViewById(R.id.user_info_layout);
        user_pin_layout = findViewById(R.id.user_pin_layout);

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                district_sending = districtList.get(position);
                district_id_sending = districtId.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        questionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                question_sending = questionList.get(position);
                question_id_sending = questionId.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alreadyMember.setOnClickListener(v -> {
            startActivity(new Intent(this, CitizenLoginScreen.class));
            finish();
        });

        continueButton.setOnClickListener(v -> {
            String userDistrict = districtSpinner.getSelectedItem().toString();
            String userQuestion = questionSpinner.getSelectedItem().toString();

            userName = new_full_name.getText().toString();
            userMobile = new_mobile.getText().toString();
            userAnswer = new_answer.getText().toString();
            if (userDistrict.equalsIgnoreCase(getString(R.string.select_one))) {
                Toast.makeText(this, getString(R.string.select_district), Toast.LENGTH_SHORT).show();
                districtSpinner.requestFocus();
                callVibrator();
            } else if (userName.isEmpty()) {
                new_full_name.setError(getString(R.string.enter_user_full_name));
                new_full_name.requestFocus();
                callVibrator();
            } else if (userMobile.isEmpty()) {
                new_mobile.setError(getString(R.string.enter_mobile_no));
                new_mobile.requestFocus();
                callVibrator();
            } else if (userMobile.length() < 10) {
                Toast.makeText(this, getString(R.string.enter_valid_mobile_no), Toast.LENGTH_SHORT).show();
                new_mobile.requestFocus();
                callVibrator();
            } else if (userQuestion.equalsIgnoreCase(getString(R.string.select_one))) {
                Toast.makeText(this, getString(R.string.select_security_question), Toast.LENGTH_SHORT).show();
                questionSpinner.requestFocus();
                callVibrator();
            } else if (userAnswer.isEmpty()) {
                new_answer.setError(getString(R.string.answer));
                new_answer.requestFocus();
                callVibrator();
            } else {
                user_info_layout.setVisibility(View.GONE);
                user_pin_layout.setVisibility(View.VISIBLE);
            }
        });
        backToInfoButton.setOnClickListener(v -> {
            user_pin_layout.setVisibility(View.GONE);
            user_info_layout.setVisibility(View.VISIBLE);
        });
        createAccountButton.setOnClickListener(v -> {
            userPin = pin_box.getText().toString();

            if (userPin.isEmpty()) {
            pin_box.setError(getString(R.string.enter_pin));
            pin_box.requestFocus();
            callVibrator();
        } else if (userPin.length() < 4) {
            Toast.makeText(this, getString(R.string.enter_valid_pin), Toast.LENGTH_SHORT).show();
            pin_box.requestFocus();
            callVibrator();
        } else if (confirm_pin_box.getText().toString().isEmpty()) {
            confirm_pin_box.setError(getString(R.string.enter_confirm_pin));
            confirm_pin_box.requestFocus();
            callVibrator();
        } else if (!Objects.equals(userPin.trim(), confirm_pin_box.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.pin_or_confirm_pin_not_matched), Toast.LENGTH_SHORT).show();
            confirm_pin_box.requestFocus();
            callVibrator();
        } else{
            citizenRegisterInDatabase(userName, userMobile, userPin, userAnswer);

        }

    });

    getDistrictFromDatabase();

    getSecurityQuestionsFromDatabase();

}

    private void citizenRegisterInDatabase(String userName, String userMobile, String userPin, String userAnswer) {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(RegistrationScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.citizenRegister(district_id_sending, district_sending, userName, userMobile, userPin, question_id_sending, userAnswer, "Citizen", "Citizen");
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
                            if (message.equalsIgnoreCase("Citizen registerd successfully")) {
                                Toast.makeText(RegistrationScreen.this, message, Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegistrationScreen.this);
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage(getString(R.string.new_account_created));
                                alertDialog.setNeutralButton(getString(R.string.ok), (dialog, which) -> {
                                    startActivity(new Intent(RegistrationScreen.this, CitizenLoginScreen.class));
                                    finish();
                                });
                                alertDialog.show();
                            } else {
                                Toast.makeText(RegistrationScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegistrationScreen.this, message, Toast.LENGTH_SHORT).show();
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

    public void callVibrator() {
        if (vi.hasVibrator()) {
            vi.vibrate(100);
        }
    }

    private void getSecurityQuestionsFromDatabase() {
        questionList.clear();
        questionId.clear();
        questionList.add(getString(R.string.select_one));
        questionId.add(getString(R.string.select_one));

        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(RegistrationScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getListOfSecurityQuestions();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");


                        if (message.equalsIgnoreCase("Security Questions:")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);

                                    questionList.add(object.optString("question").trim());
                                    questionId.add(object.optString("question_id").trim());

                                }

                                ArrayAdapter districtAdapter = new ArrayAdapter(RegistrationScreen.this, R.layout.spinner_text_view, questionList);
                                districtAdapter.setDropDownViewResource(R.layout.spinner_text_view);
                                questionSpinner.setAdapter(districtAdapter);


                            } else {
                                Toast.makeText(RegistrationScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegistrationScreen.this, "" + message, Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                        if (BuildConfig.DEBUG)
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    if (BuildConfig.DEBUG)
                        Log.i("Resp Exc: ", "" + response.code());
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "pPlease Try Again later ");


                } else {
                    if (BuildConfig.DEBUG)
                        Log.i("Resp Exc: ", "" + response.code());
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (BuildConfig.DEBUG)
                    Log.i("Resp onFailure: ", "" + t.getMessage());
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

    private void getDistrictFromDatabase() {
        districtList.clear();
        districtId.clear();
        districtList.add(getString(R.string.select_one));
        districtId.add(getString(R.string.select_one));

        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(RegistrationScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getListOfDistricts();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");


                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);

                                    districtList.add(object.optString("n_d_name").trim());
                                    districtId.add(object.optString("n_d_code").trim());

                                }
                                ArrayAdapter districtAdapter = new ArrayAdapter(RegistrationScreen.this, android.R.layout.simple_spinner_item, districtList);
                                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                districtSpinner.setAdapter(districtAdapter);
                            } else {
                                Toast.makeText(RegistrationScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegistrationScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (BuildConfig.DEBUG)

                            Log.i("Resp Exc: ", e.getMessage() + "");
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    if (BuildConfig.DEBUG)

                        Log.i("Resp Exc: ", "" + response.code());
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "pPlease Try Again later ");


                } else {
                    if (BuildConfig.DEBUG)

                        Log.i("Resp Exc: ", "" + response.code());
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (BuildConfig.DEBUG)

                    Log.i("Resp onFailure: ", "" + t.getMessage());
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
        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
    }

}