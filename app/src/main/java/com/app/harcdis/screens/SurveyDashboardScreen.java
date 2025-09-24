package com.app.harcdis.screens;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adapter.RecordAdapter;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.model.SurveyModel;
import com.app.harcdis.utils.Sp;
import com.robinhood.ticker.TickerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyDashboardScreen extends AppCompatActivity {
    RecyclerView record_recycler_view;
    LinearLayout no_data_found_linear_layout;
    ArrayList<SurveyModel> surveyModelArrayList;
    RecordAdapter recordAdapter;
    ProgressDialog progressDialog;
    TickerView history_total_record;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_dashboard_screen);
        initViews();
    }

    private void initViews() {
        searchView = findViewById(R.id.searchView);
        record_recycler_view = findViewById(R.id.record_recycler_view);
        no_data_found_linear_layout = findViewById(R.id.no_data_found_linear_layout);
        history_total_record = findViewById(R.id.history_total_record);
        surveyModelArrayList = new ArrayList<>();
        recordAdapter = new RecordAdapter(SurveyDashboardScreen.this, surveyModelArrayList);
        record_recycler_view.setLayoutManager(new LinearLayoutManager(SurveyDashboardScreen.this));
        record_recycler_view.setAdapter(recordAdapter);
        progressDialog = new ProgressDialog(SurveyDashboardScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        fetchDataByApi();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String text) {

        ArrayList<SurveyModel> filteredList = new ArrayList<>();
        for (SurveyModel item : surveyModelArrayList) {
            if (item.getGisId().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            recordAdapter.filterList(filteredList);
            no_data_found_linear_layout.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
        } else {
            no_data_found_linear_layout.setVisibility(View.GONE);
            recordAdapter.filterList(filteredList);
        }
    }
    private void fetchDataByApi() {
        surveyModelArrayList.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getDashboardDetails(
                Sp.read_shared_pref(SurveyDashboardScreen.this, "user_mobile"),
                Sp.read_shared_pref(SurveyDashboardScreen.this, "dis_code_store")
        );
        Log.d("TAG", "fetchDataByApi: "+Sp.read_shared_pref(SurveyDashboardScreen.this, "user_mobile"));
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
                                    surveyModelArrayList.add(new SurveyModel(
                                            object.optString("remarks"),
                                            object.optString("n_d_name"),
                                            object.optString("n_t_name"),
                                            object.optString("n_v_name"),
                                            object.optString("n_murr_no"),
                                            object.optString("n_khas_no"),
                                            object.optString("verifyImg1"),
                                            object.optString("verifyImg2"),
                                            object.optString("verifyImg3"),
                                            object.optString("verifyImg4"),
                                            object.optString("verifiedBy"),
                                            object.optString("user_name"),
                                            object.optString("verified"),
                                            object.optString("verificationDate"),
                                            object.optString("UID")

                                    ));

                                }
//                                Collections.sort(surveyModelArrayList, new Comparator<SurveyModel>() {
//                                    DateFormat f = new SimpleDateFormat("dd/MM/yyyy '@'hh:mm a");
//                                    @Override
//                                    public int compare(SurveyModel lhs, SurveyModel rhs) {
//                                        try {
//                                            return f.parse(lhs.getVerificationDate()).compareTo(f.parse(rhs.getVerificationDate()));
//                                        } catch (ParseException e) {
//                                            throw new IllegalArgumentException(e);
//                                        }
//                                    }
//                                });

                                if (surveyModelArrayList.size() > 0) {
                                    no_data_found_linear_layout.setVisibility(View.GONE);
                                    record_recycler_view.setVisibility(View.VISIBLE);

                                    recordAdapter.notifyDataSetChanged();

                                } else {
                                    record_recycler_view.setVisibility(View.GONE);
                                    no_data_found_linear_layout.setVisibility(View.VISIBLE);
                                }
                                history_total_record.setText(String.valueOf(surveyModelArrayList.size()));

                            } else {
                                Toast.makeText(SurveyDashboardScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SurveyDashboardScreen.this, "" + message, Toast.LENGTH_SHORT).show();

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_record_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_date);


        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);

        searchItem.setOnMenuItemClickListener(v -> {

            DatePickerDialog datePicker = new DatePickerDialog(SurveyDashboardScreen.this, (view, year1, month1, dayOfMonth) ->
                    filterList(year1 + "-" + checkZero((month1 + 1)) + "-" + checkZero(dayOfMonth)),

                    year, month, day);
            datePicker.show();
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }

    private String checkZero(int i) {
        if (i < 9) {
            return "0" + i;
        } else {
            return "" + i;
        }
    }

    private void filterList(String text) {
        ArrayList<SurveyModel> filteredList = new ArrayList<>();
        for (SurveyModel item : surveyModelArrayList) {
            if (item.getVerificationDate().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            recordAdapter.filterList(filteredList);
            history_total_record.setText(String.valueOf(filteredList.size()));
            no_data_found_linear_layout.setVisibility(View.VISIBLE);
            record_recycler_view.setVisibility(View.GONE);
        } else {
            recordAdapter.filterList(filteredList);
            history_total_record.setText(String.valueOf(filteredList.size()));
            no_data_found_linear_layout.setVisibility(View.GONE);
            record_recycler_view.setVisibility(View.VISIBLE);
        }
    }


}