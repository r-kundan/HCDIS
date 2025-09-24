package com.app.harcdis.CitizenModule;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPinScreen extends AppCompatActivity {

    LinearLayout security_ques_layout, new_pin_layout;
    Button backButton,sendButton, setButton;
    EditText linked_mobile, linkedAnswer;
    Spinner linkedQuestionsSpinner;
    EditText new_pin_box, confirm_pin_box;
    private ArrayList<String> questionList, questionId;
    private String question_sending, question_id_sending;

    Vibrator vi;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin_screen);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        progressDialog = new ProgressDialog(ForgotPinScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        questionList = new ArrayList<>();
        questionId = new ArrayList<>();

        security_ques_layout = findViewById(R.id.security_ques_layout);
        new_pin_layout = findViewById(R.id.new_pin_layout);
        backButton = findViewById(R.id.backButton);
        sendButton = findViewById(R.id.sendButton);
        setButton = findViewById(R.id.setButton);
        new_pin_box = findViewById(R.id.new_pin_box);
        confirm_pin_box = findViewById(R.id.confirm_pin_box);
        linked_mobile = findViewById(R.id.linked_mobile);
        linkedQuestionsSpinner = findViewById(R.id.linkedQuestionsSpinner);
        linkedAnswer = findViewById(R.id.linkedAnswer);

        linkedQuestionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                question_sending = questionList.get(position);
                question_id_sending = questionId.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backButton.setOnClickListener(v->{
            security_ques_layout.setVisibility(View.VISIBLE);
            new_pin_layout.setVisibility(View.GONE);
        });
        sendButton.setOnClickListener(v -> {

            if (linked_mobile.getText().toString().isEmpty()) {
                linked_mobile.setError(getString(R.string.enter_mobile_no));
                callVibrator();
            } else if (linkedQuestionsSpinner.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.select_one))) {
                Toast.makeText(this, "Select Security Question", Toast.LENGTH_SHORT).show();
                callVibrator();
            } else if (linkedAnswer.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter Answer of selected security question", Toast.LENGTH_SHORT).show();
                callVibrator();
            } else {
                security_ques_layout.setVisibility(View.GONE);
                new_pin_layout.setVisibility(View.VISIBLE);
            }

        });

        setButton.setOnClickListener(v -> {

            String user_new_pin = new_pin_box.getText().toString().trim();
            if (user_new_pin.isEmpty()) {
                new_pin_box.setError(getString(R.string.pin));
                callVibrator();
            } else if (user_new_pin.length() < 4) {
                Toast.makeText(this, "Enter Valid Pin", Toast.LENGTH_SHORT).show();
                callVibrator();
            } else if (confirm_pin_box.getText().toString().isEmpty()) {
                confirm_pin_box.setError(getString(R.string.confirm_pin));
                callVibrator();
            } else if (!Objects.equals(user_new_pin, confirm_pin_box.getText().toString().trim())) {
                Toast.makeText(this, "Pin and Confirm Pin are not matched.", Toast.LENGTH_SHORT).show();
                callVibrator();
            } else {

                forgotPinAPI(linked_mobile.getText().toString(), question_id_sending, linkedAnswer.getText().toString(), new_pin_box.getText().toString());

            }

        });

        getSecurityQuestionsFromDatabase();
    }

    private void forgotPinAPI(String mobile, String question_id_sending, String linkedAnswer, String pin) {


        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(ForgotPinScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.forgotPin(mobile, pin, question_id_sending, linkedAnswer);
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
                            if (message.equalsIgnoreCase("User Pin Updated Successfully")) {
                                Toast.makeText(ForgotPinScreen.this, message, Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgotPinScreen.this);
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage(getString(R.string.pin_updated));
                                alertDialog.setNeutralButton(getString(R.string.ok), (dialog, which) -> {
                                    startActivity(new Intent(ForgotPinScreen.this, CitizenLoginScreen.class));
                                    finish();
                                });
                                alertDialog.show();
                            } else {
                                Toast.makeText(ForgotPinScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ForgotPinScreen.this, message, Toast.LENGTH_SHORT).show();
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

        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(ForgotPinScreen.this).create(ApiInterface.class);
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

                                ArrayAdapter districtAdapter = new ArrayAdapter(ForgotPinScreen.this, R.layout.spinner_text_view, questionList);
                                districtAdapter.setDropDownViewResource(R.layout.spinner_text_view);
                                linkedQuestionsSpinner.setAdapter(districtAdapter);


                            } else {
                                Toast.makeText(ForgotPinScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ForgotPinScreen.this, "" + message, Toast.LENGTH_SHORT).show();

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