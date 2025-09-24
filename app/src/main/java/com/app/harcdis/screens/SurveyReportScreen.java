package com.app.harcdis.screens;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyReportScreen extends AppCompatActivity {
    TableLayout reportTableLayout;
    Button downloadButton;
    ProgressDialog progressDialog;
    ArrayList<String> dist_code_list;
    ArrayList<String> columnDataList;
    ArrayList<ArrayList<String>> rowDataList;
    ArrayList<ArrayList<String>> bottomHeader;
    //String[] columnDataList;
//String[][] rowDataList;
    StringBuilder csvData;
    String district, totalRecords, verifiedYesCount, verifiedNoCount, demolishNoCount;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_report_screen);
        reportTableLayout = findViewById(R.id.reportTableLayout);
        downloadButton = findViewById(R.id.downloadButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        dist_code_list = new ArrayList<>();
        columnDataList = new ArrayList<>(6);
        rowDataList = new ArrayList<>();
        bottomHeader = new ArrayList<>();
//        columnDataList = new String[6];
//        rowDataList = new String[21][6];
        getAllDistrictDataByOneApi();
        csvData = new StringBuilder();
        downloadButton.setOnClickListener(v -> {
            progressDialog.show();
            try {
                Random random = new Random();

                // Generate a random integer within a specified range (e.g., between 1 and 100)
                int randomNumber = random.nextInt(1000) + 1;

                File outputFile = new File(getExternalFilesDir(null), "survey_report_" + randomNumber + ".csv");
                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(csvData.toString().getBytes());
                fos.close();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", outputFile);
                intent.setDataAndType(uri, "text/csv");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    progressDialog.dismiss();
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void getAllDistrictDataByOneApi() {
        rowDataList.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getSurveyReportDataOfAllDistrict();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");
                        progressDialog.dismiss();
                        if (message.equalsIgnoreCase("Success")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    district = object.optString("distrct_name");
                                    totalRecords = object.getString("total");
                                    verifiedYesCount = object.getString("Verified");
                                    verifiedNoCount = object.getString("UnVerified");
                                    demolishNoCount = object.getString("ReadyToDemolish");
                                    String Demolished = object.getString("Demolished");
                                    ArrayList<String> columnDataList = new ArrayList<>();
                                    columnDataList.add(0, String.valueOf(i));
                                    columnDataList.add(1, district);
                                    columnDataList.add(2, verifiedNoCount);
                                    columnDataList.add(3, verifiedYesCount);
                                    columnDataList.add(4, demolishNoCount);
                                    columnDataList.add(5, Demolished);
                                    columnDataList.add(6, totalRecords);
                                    rowDataList.add(columnDataList);
                                }
                                bottomHeader.clear();
                                ArrayList<String> columnDataList = new ArrayList<>();
                                columnDataList.add(0, "Total");
                                columnDataList.add(1, "--");
                                columnDataList.add(2, jsonObject.optString("verifiedNoCount"));
                                columnDataList.add(3, jsonObject.optString("verifiedYesCount"));
                                columnDataList.add(4, jsonObject.optString("demolishedCount"));
                                columnDataList.add(5, jsonObject.optString("readyDemolishCount"));
                                columnDataList.add(6, jsonObject.optString("totallocations"));
                                bottomHeader.add(columnDataList);


                                Log.d(TAG, "onResponse: " + columnDataList);
                                loadDataInTable(rowDataList);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SurveyReportScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SurveyReportScreen.this, "" + message, Toast.LENGTH_SHORT).show();
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


    private void loadDataInTable(ArrayList<ArrayList<String>> rowDataList2) {
        reportTableLayout.removeAllViews();
        ArrayList<String> tableHeader = new ArrayList<>();
        tableHeader.add("Sr No");
        tableHeader.add("District");
        tableHeader.add("Unverified");
        tableHeader.add("Verified");
        tableHeader.add("Ready To Demolish");
        tableHeader.add("Demolished");
        tableHeader.add("Total");
        // Sample data

        // Loop through the nested ArrayList (rowDataList) and add it to the TableLayout
        TableRow HeaderRow = new TableRow(this);

        for (String cellData : tableHeader) {
            TextView cell = new TextView(this);
            cell.setText(cellData);
            cell.setTextSize(14);
            cell.setTextColor(getResources().getColor(R.color.white));
            cell.setBackgroundColor(Color.parseColor("#A9EEE7E6"));
            cell.setBackgroundResource(R.drawable.table_line);
            cell.setGravity(Gravity.CENTER);
            cell.setPadding(8, 8, 8, 8);
            HeaderRow.addView(cell);
        }
        HeaderRow.setBackgroundColor(getResources().getColor(R.color.appColor));
        HeaderRow.animate();
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
        reportTableLayout.addView(HeaderRow);

        for (ArrayList<String> rowData : rowDataList2) {
            TableRow rowValue = new TableRow(this);
            for (String cellData : rowData) {
                TextView cell = new TextView(this);
                cell.setText(cellData);
                cell.setTypeface(boldTypeface);
                cell.setBackgroundResource(R.drawable.table_line);
                cell.setGravity(Gravity.CENTER);
                cell.setPadding(8, 8, 8, 8);
                rowValue.addView(cell);
            }
            reportTableLayout.addView(rowValue);
        }
        for (ArrayList<String> rowData : bottomHeader) {
            TableRow rowValue = new TableRow(this);
            for (String cellData : rowData) {
                TextView cell = new TextView(this);
                cell.setText(cellData);
                cell.setTypeface(boldTypeface);
                cell.setBackgroundResource(R.drawable.table_line2);
                cell.setGravity(Gravity.CENTER);
                cell.setTextColor(getResources().getColor(R.color.white));
                cell.setPadding(8, 8, 8, 8);
                rowValue.addView(cell);
            }
            reportTableLayout.addView(rowValue);
        }
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, // Width
                TableRow.LayoutParams.WRAP_CONTENT // Height
        );

        if (rowDataList2.size() == dist_code_list.size()) {
            for (String cell : tableHeader) {
                csvData.append(cell).append(",");
            }
            csvData.deleteCharAt(csvData.length() - 1);
            csvData.append("\n");

            for (ArrayList<String> row : rowDataList2) {
                for (String cell : row) {
                    csvData.append(cell).append(",");
                }
                csvData.deleteCharAt(csvData.length() - 1);
                csvData.append("\n"); // Add a new line for each row// Remove trailing comma
            }
        }
    }

    private void onFailed(String s, String s1) {
        Toast.makeText(this, "" + s1, Toast.LENGTH_SHORT).show();
    }
}









