package com.app.harcdis.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Sp;
import com.robinhood.ticker.TickerView;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReportSectionScreen extends AppCompatActivity {
    CardView card_download_report;
    TickerView total_builtup_count_dis;
    TickerView total_verified_record_dis;
    TickerView total_unverified_record_dis;
    int totalBuiltUpPoint;
    int totalVerifiedPoint;
    int totalUnverifiedPoint;
    private ProgressDialog progressDialog;
    LinearLayout card_zero_total_point_dis;
    RelativeLayout card_for_total_verified_records;
    RelativeLayout card_two_unverified_record;
    TextView dis_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report_section_screen);
        initViews();
    }

    private void initViews() {
        progressDialog = new ProgressDialog(AdminReportSectionScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);


        card_download_report = findViewById(R.id.card_download_report);
        dis_text_view = findViewById(R.id.dis_text_view);
        total_unverified_record_dis = findViewById(R.id.total_unverified_record_dis);
        total_verified_record_dis = findViewById(R.id.total_verified_record_dis);
        total_builtup_count_dis = findViewById(R.id.total_builtup_count_dis);
        card_zero_total_point_dis = findViewById(R.id.card_zero_total_point_dis);
        card_for_total_verified_records = findViewById(R.id.card_for_total_verified_records);
        card_two_unverified_record = findViewById(R.id.card_two_unverified_record);


        card_download_report.setOnClickListener(view -> {
            startActivity(new Intent(AdminReportSectionScreen.this, SurveyReportScreen.class));
        });
        fetchDashboardDetails();
        dis_text_view.setText(Sp.read_shared_pref(AdminReportSectionScreen.this,"dis_code_name")+" Dashboard");
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }




    private void fetchDashboardDetails() {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(AdminReportSectionScreen.this).create(ApiInterface.class);
        Log.d("TAG", "fetchDashboardDetails: "+Sp.read_shared_pref(AdminReportSectionScreen.this,"dis_code_store"));
        Call<ResponseBody> call = apiInterface.getMainDashboardData(Sp.read_shared_pref(AdminReportSectionScreen.this,"dis_code_store"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.optString("message");
                        boolean status = jsonObject.optBoolean("status");
                        progressDialog.dismiss();

                        if (message.equalsIgnoreCase("Success")) {
                            if (status) {
                                JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                                JSONObject counttotaldata = jsonObject1.optJSONObject("counttotaldata");
                                totalBuiltUpPoint = counttotaldata.optInt("totaldata");
                                JSONObject counttotalverifield = jsonObject1.optJSONObject("counttotalverifield");
                                totalVerifiedPoint = counttotalverifield.optInt("totaldata");
                                JSONObject counttotalnonverifield = jsonObject1.optJSONObject("counttotalnonverifield");
                                totalUnverifiedPoint = counttotalnonverifield.optInt("totaldata");
                                updateTickerView();

                            } else {
                                Toast.makeText(AdminReportSectionScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminReportSectionScreen.this, "" + message, Toast.LENGTH_SHORT).show();
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

    private void updateTickerView() {
        total_builtup_count_dis.setText(String.valueOf(totalBuiltUpPoint));
        total_verified_record_dis.setText(String.valueOf(totalVerifiedPoint));
        total_unverified_record_dis.setText(String.valueOf(totalUnverifiedPoint));
    }

    private void onFailed(String s, String s1) {
        Toast.makeText(AdminReportSectionScreen.this, "" + s1, Toast.LENGTH_SHORT).show();

    }
}