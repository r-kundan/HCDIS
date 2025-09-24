package com.app.harcdis.adminRole;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adapter.SurveyHistoryAdapter;
import com.app.harcdis.adminRole.model.SurveyHistoryModel;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.robinhood.ticker.TickerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSurveyHistoryScreen extends AppCompatActivity {
    TickerView total_verified_count;

    RecyclerView history_record_recyclerview;
    LinearLayout no_data_found_layout;
    ArrayList<SurveyHistoryModel> arrayList;
    ProgressDialog progressDialog;
    com.app.harcdis.adapter.SurveyHistoryAdapter SurveyHistoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_survey_history_screen);

        history_record_recyclerview = findViewById(R.id.history_record_recyclerview);
        no_data_found_layout = findViewById(R.id.no_data_found_layout);
        total_verified_count = findViewById(R.id.total_verified_count);
        arrayList = new ArrayList<>();
        history_record_recyclerview.setLayoutManager(new LinearLayoutManager(AdminSurveyHistoryScreen.this));
        SurveyHistoryAdapter = new SurveyHistoryAdapter(AdminSurveyHistoryScreen.this, arrayList);
        history_record_recyclerview.setAdapter(SurveyHistoryAdapter);


        progressDialog = new ProgressDialog(AdminSurveyHistoryScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));

        String gisId = getIntent().getStringExtra("gisId");
        String mobile = getIntent().getStringExtra("mobile");

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate: gisId:  " + gisId);
            Log.d(TAG, "onCreate: mobile:  " + mobile);
        }
        if (gisId != null) {
            fetchHistoryByApi(gisId);
        } else if (mobile != null) {
            fetchHistoryOfUserByApi(mobile);
        }


    }


    private void fetchHistoryByApi(String gisId) {
        arrayList.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getHistory(gisId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");
                        if (BuildConfig.DEBUG) {
                            Log.d("TAG", "onResponse: " + jsonObject);
                        }
                        progressDialog.dismiss();
                        if (message.equalsIgnoreCase("Sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    arrayList.add(new SurveyHistoryModel(
                                            object.optString("verificationDate"),
                                            object.optString("verifiedBy"),
                                            object.optString("userName"),
                                            object.optString("year"),
                                            object.optString("verifyImg1"),
                                            object.optString("verifyImg2"),
                                            object.optString("verifyImg3"),
                                            object.optString("verifyImg4"),
                                            object.optString("verifyVideo"),
                                            object.optString("feedback"),
                                            object.optString("nearByLandMark"),
                                            object.optString("remarks")

                                    ));


                                }
                                if (arrayList.size() > 0) {
                                    history_record_recyclerview.setVisibility(View.VISIBLE);
                                    no_data_found_layout.setVisibility(View.GONE);
                                    SurveyHistoryAdapter.notifyDataSetChanged();

                                } else {
                                    no_data_found_layout.setVisibility(View.VISIBLE);
                                    history_record_recyclerview.setVisibility(View.GONE);
                                }
                                total_verified_count.setText(String.valueOf(arrayList.size()));

                            } else {
                                Toast.makeText(AdminSurveyHistoryScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminSurveyHistoryScreen.this, "" + message, Toast.LENGTH_SHORT).show();

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

    private void fetchHistoryOfUserByApi(String mobile) {
        arrayList.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getUserHistory(mobile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");

                        if (BuildConfig.DEBUG) {
                            Log.d("TAG", "onResponse: " + jsonObject);
                        }
                        progressDialog.dismiss();
                        if (message.equalsIgnoreCase("Sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    arrayList.add(new SurveyHistoryModel(
                                            object.optString("verificationDate"),
                                            object.optString("verifiedBy"),
                                            object.optString("userName"),
                                            object.optString("year"),
                                            object.optString("uploadImage1"),
                                            object.optString("uploadImage2"),
                                            object.optString("uploadImage3"),
                                            object.optString("uploadImage4"),
                                            object.optString("uploadVideo"),
                                            object.optString("feedback"),
                                            object.optString("nearByLandMark"),
                                            object.optString("remarks")

                                    ));


                                }
                                if (arrayList.size() > 0) {
                                    history_record_recyclerview.setVisibility(View.VISIBLE);
                                    no_data_found_layout.setVisibility(View.GONE);
                                    SurveyHistoryAdapter.notifyDataSetChanged();

                                } else {
                                    no_data_found_layout.setVisibility(View.VISIBLE);
                                    history_record_recyclerview.setVisibility(View.GONE);
                                }
                                total_verified_count.setText(String.valueOf(arrayList.size()));

                            } else {
                                Toast.makeText(AdminSurveyHistoryScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminSurveyHistoryScreen.this, "" + message, Toast.LENGTH_SHORT).show();

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

    private void onFailed(String s, String s1) {
        Toast.makeText(this, "" + s1, Toast.LENGTH_SHORT).show();
    }

}