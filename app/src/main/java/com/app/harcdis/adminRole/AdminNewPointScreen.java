package com.app.harcdis.adminRole;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.robinhood.ticker.TickerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminNewPointScreen extends AppCompatActivity {
    Spinner spinner;
    String[] month_name = {"Any Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    String[] month_code = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    TextView daily_point, weekly_point, monthly_point;
    TickerView total_new_point;
    RecyclerView admin_new_point_recyclerview;
    LinearLayout admin_no_data_found_layout;
    ArrayList<AdminCardHolderModel> arrayList;
    ProgressDialog progressDialog;
    AdminCardAdapter adminCardAdapter;
    final Calendar myCalendar= Calendar.getInstance();
    SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_point_screen);
        initViews();
    }

    private void initViews() {
        spinner = findViewById(R.id.any_month_spinner);
        daily_point = findViewById(R.id.daily_point);
        weekly_point = findViewById(R.id.weekly_point);

        arrayList = new ArrayList<>();
        monthly_point = findViewById(R.id.monthly_point);
        total_new_point = findViewById(R.id.total_new_point);
        admin_new_point_recyclerview = findViewById(R.id.admin_new_point_recyclerview);
        admin_no_data_found_layout = findViewById(R.id.admin_no_data_found_layout);
        admin_new_point_recyclerview.setLayoutManager(new LinearLayoutManager(AdminNewPointScreen.this));
        adminCardAdapter  =new AdminCardAdapter(AdminNewPointScreen.this,arrayList);
        admin_new_point_recyclerview.setAdapter(adminCardAdapter);

        progressDialog = new ProgressDialog(AdminNewPointScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        ArrayAdapter aa = new ArrayAdapter(AdminNewPointScreen.this, R.layout.spinner_text_view, month_name);
        aa.setDropDownViewResource(R.layout.spinner_text_view);
        spinner.setAdapter(aa);


        dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        fetchDataByApi(dateFormat.format(myCalendar.getTime()), "", "");




        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);

                updateLabel();
            }
        };


        daily_point.setOnClickListener(v -> {
            daily_point.setTextColor(getResources().getColor(R.color.white));
            daily_point.setBackgroundResource(R.drawable.btn_background);
            weekly_point.setTextColor(getResources().getColor(R.color.purple_700));
            weekly_point.setBackgroundResource(R.drawable.btn_light_background);
            monthly_point.setBackgroundResource(R.drawable.btn_light_background);
            monthly_point.setTextColor(getResources().getColor(R.color.purple_700));

            new DatePickerDialog(AdminNewPointScreen.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        });

        weekly_point.setOnClickListener(v -> {
            weekly_point.setTextColor(getResources().getColor(R.color.white));
            weekly_point.setBackgroundResource(R.drawable.btn_background);
            daily_point.setTextColor(getResources().getColor(R.color.purple_700));
            daily_point.setBackgroundResource(R.drawable.btn_light_background);
            monthly_point.setBackgroundResource(R.drawable.btn_light_background);
            monthly_point.setTextColor(getResources().getColor(R.color.purple_700));
            fetchDataByApi("", "weeksearch", "");
        });

        monthly_point.setOnClickListener(v -> {
            monthly_point.setTextColor(getResources().getColor(R.color.white));
            monthly_point.setBackgroundResource(R.drawable.btn_background);
            daily_point.setTextColor(getResources().getColor(R.color.purple_700));
            daily_point.setBackgroundResource(R.drawable.btn_light_background);
            weekly_point.setBackgroundResource(R.drawable.btn_light_background);
            weekly_point.setTextColor(getResources().getColor(R.color.purple_700));
            fetchDataByApi("", "", "monthsearch");
        });


    }


    private void updateLabel(){
        fetchDataByApi(dateFormat.format(myCalendar.getTime()),"","");
        if(BuildConfig.DEBUG) {
            Log.d("TAG", "initViews: Date:  " + dateFormat.format(myCalendar.getTime()));
        }
    }


    private void fetchDataByApi(String dateSearch, String weekSearch, String monthSearch) {
        arrayList.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.reportByDate(dateSearch,weekSearch,monthSearch);
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
                        if (message.equalsIgnoreCase("Success")) {
                            if (status) {
                                JSONObject result_array = jsonObject.optJSONObject("data");

                                JSONArray today_array = result_array.optJSONArray("Today");
                                for (int i = 0; i < today_array.length(); i++) {
                                    JSONObject object = today_array.getJSONObject(i);
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
                                            object.optString("verifyImg1"),
                                            object.optString("verifyImg2"),
                                            object.optString("verifyImg3"),
                                            object.optString("verifyImg4"),
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
                                    admin_new_point_recyclerview.setVisibility(View.VISIBLE);
                                    admin_no_data_found_layout.setVisibility(View.GONE);
                                    adminCardAdapter.notifyDataSetChanged();

                                } else {
                                    admin_no_data_found_layout.setVisibility(View.VISIBLE);
                                    admin_new_point_recyclerview.setVisibility(View.GONE);
                                }
                                total_new_point .setText(String.valueOf(arrayList.size()));

                            } else {
                                Toast.makeText(AdminNewPointScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminNewPointScreen.this, "" + message, Toast.LENGTH_SHORT).show();

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