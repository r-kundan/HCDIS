package com.app.harcdis.adminRole;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adapter.AdminCardAdapter;
import com.app.harcdis.adminRole.model.AdminCardHolderModel;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAllRecordAccordingToDateScreen extends AppCompatActivity {
    EditText start_date;
    EditText end_date;
    ImageButton search_data_via_date;

    RecyclerView unverified_record_recyclerView;
    LinearLayout no_data_found_layout;
    ArrayList<AdminCardHolderModel> arrayList;
    ProgressDialog progressDialog;
    AdminCardAdapter adminCardAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_record_according_to_date_screen);
        initViews();
    }

    private void initViews() {
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        search_data_via_date = findViewById(R.id.search_data_via_date);

        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);

        start_date.setOnClickListener(view -> {
            DatePickerDialog datePicker = new DatePickerDialog(AdminAllRecordAccordingToDateScreen.this, (view1, year1, month1, dayOfMonth) -> start_date.setText(year1 + "-" + convertDate((month1 + 1)) + "-" + convertDate(dayOfMonth)), year, month, day);
            datePicker.show();
        });

        end_date.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(AdminAllRecordAccordingToDateScreen.this, (view, year1, month1, dayOfMonth) -> end_date.setText(year1 + "-" + convertDate((month1 + 1)) + "-" + convertDate(dayOfMonth)), year, month, day);
            datePicker.show();
        });
        search_data_via_date.setOnClickListener(view -> {
            if (start_date.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.select_start_date), Toast.LENGTH_SHORT).show();
            }else if(end_date.getText().toString().isEmpty()){
                Toast.makeText(this, getString(R.string.select_end_date), Toast.LENGTH_SHORT).show();
            } else {
                fetchDataByApi();
            }
        });
        arrayList = new ArrayList<>();
        adminCardAdapter = new AdminCardAdapter(AdminAllRecordAccordingToDateScreen.this,arrayList);
        unverified_record_recyclerView = findViewById(R.id.date_record_recyclerView);
        no_data_found_layout = findViewById(R.id.no_data_found_layout);
        unverified_record_recyclerView.setLayoutManager(new LinearLayoutManager(AdminAllRecordAccordingToDateScreen.this));
        unverified_record_recyclerView.setAdapter(adminCardAdapter);

        progressDialog = new ProgressDialog(AdminAllRecordAccordingToDateScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        fetchDataByApi();
    }

    public String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }




    private void fetchDataByApi() {
        arrayList.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(getApplicationContext()).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getRecordByDate(
                start_date.getText().toString(),end_date.getText().toString()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");

                        if(BuildConfig.DEBUG) {
                            Log.d("TAG", "onResponse: " + jsonObject);
                        }
                        progressDialog.dismiss();
                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    arrayList.add(new AdminCardHolderModel(
                                            object.optString("n_d_name"),
                                            object.optString("n_t_name"),
                                            object.optString("n_v_name"),
                                            object.optString("n_murr_no"),
                                            object.optString("n_khas_no"),
                                            object.optString("ca_name"),
                                            object.optString("dev_plan"),
                                            object.optString("verificationDate"),
                                            object.optString("verifiedBy"),
                                            object.optString("verified"),
                                            object.optString("uploadimage"),
                                            object.optString("uploadimage1"),
                                            object.optString("uploadimage2"),
                                            object.optString("uploadimage3"),
                                            object.optDouble("latitude"),
                                            object.optDouble("longitude"),
                                            object.optString("OBJECTID"),
                                            object.optString("gisId"),
                                            object.optString("nearByLandMark"),
                                            object.optString("feedback"),
                                            object.optString("user_name"),
                                            object.optString("UID")

                                    ));


                                }
                                if (arrayList.size() > 0) {
                                    unverified_record_recyclerView.setVisibility(View.VISIBLE);
                                    no_data_found_layout.setVisibility(View.GONE);
                                    adminCardAdapter.notifyDataSetChanged();

                                } else {
                                    no_data_found_layout.setVisibility(View.VISIBLE);
                                    unverified_record_recyclerView.setVisibility(View.GONE);
                                }


                            } else {
                                Toast.makeText(AdminAllRecordAccordingToDateScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminAllRecordAccordingToDateScreen.this, "" + message, Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        if(BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog.dismiss();
                    if(BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog.dismiss();
                    if(BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                if(BuildConfig.DEBUG) {
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