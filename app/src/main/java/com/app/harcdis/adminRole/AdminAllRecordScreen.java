package com.app.harcdis.adminRole;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
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

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAllRecordScreen extends AppCompatActivity {
    TickerView all_verified_count;
    RecyclerView all_record_recyclerView;
    LinearLayout no_data_found_layout;
    ArrayList<AdminCardHolderModel> arrayList;
    ProgressDialog progressDialog;
    AdminCardAdapter adminCardAdapter;
    NestedScrollView nested_scroll_view;


    Button apply_filter_button;
    LinearLayout ll_dis_location_selection;

    ImageView arrow_down_image_view;


    private ArrayList<String> tehsil_name_list;
    private ArrayList<String> dist_name_list;
    private ArrayList<String> dist_code_list;
    private ArrayList<String> tehsil_code_list;
    private ArrayList<String> village_name_list;
    private ArrayList<String> village_code_list;
    private String dis_name;
    private String teh_name;
    private String village_name;
    private String teh_name_code;
    private String village_name_code;
    private String dis_name_code;
    Spinner districtSpinner, tehsilSpinner, villageSpinner;
    SearchView searchView;
    int page = 1;
    int total_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_record_screen);
        initViews();
    }

    private void initViews() {
        arrayList = new ArrayList<>();

        searchView = findViewById(R.id.searchView);
        nested_scroll_view = findViewById(R.id.nested_scroll_view);
        districtSpinner = findViewById(R.id.districtSpinner);
        tehsilSpinner = findViewById(R.id.tehsilSpinner);
        villageSpinner = findViewById(R.id.villageSpinner);
        arrow_down_image_view = findViewById(R.id.arrow_down_image_view);
        ll_dis_location_selection = findViewById(R.id.ll_dis_location_selection);
        apply_filter_button = findViewById(R.id.apply_filter_button);
        tehsil_name_list = new ArrayList<>();
        village_name_list = new ArrayList<>();
        tehsil_code_list = new ArrayList<>();
        village_code_list = new ArrayList<>();
        dist_name_list = new ArrayList<>();
        dist_code_list = new ArrayList<>();

        adminCardAdapter = new AdminCardAdapter(AdminAllRecordScreen.this, arrayList);
        all_verified_count = findViewById(R.id.all_verified_count);
        all_record_recyclerView = findViewById(R.id.all_record_recyclerView);
        no_data_found_layout = findViewById(R.id.no_data_found_layout);
        all_record_recyclerView.setLayoutManager(new LinearLayoutManager(AdminAllRecordScreen.this));
        all_record_recyclerView.setAdapter(adminCardAdapter);

        progressDialog = new ProgressDialog(AdminAllRecordScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        fetchDataByApi("", "", "", page);

        loadAllDistrict();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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


        // adding on scroll change listener method for our nested scroll view.
        nested_scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    page++;
                    if (page <= total_page) {
                        fetchDataByApi(dis_name_code, teh_name_code, village_name_code, page);
                    } else {
                        Toast.makeText(AdminAllRecordScreen.this, "All Data Loaded SuccessFully", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dis_name = dist_name_list.get(position);
                dis_name_code = dist_code_list.get(position);

                if (!districtSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {

                    loadDataForTehsil(dis_name_code);
                    fetchDataByApi(dis_name_code, "", "", page);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tehsilSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teh_name = tehsil_name_list.get(position);
                teh_name_code = tehsil_code_list.get(position);

                if (!tehsilSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {
                    load_village_data(teh_name_code);
                    fetchDataByApi(dis_name_code, teh_name_code, "", page);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                village_name = village_name_list.get(position);
                village_name_code = village_code_list.get(position);
                if (!villageSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {

                    fetchDataByApi(dis_name_code, teh_name_code, village_name_code, page);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        arrow_down_image_view.setOnClickListener(v -> {
            if (ll_dis_location_selection.getVisibility() == View.VISIBLE) {
                ll_dis_location_selection.setVisibility(View.GONE);
                arrow_down_image_view.setRotation(0);
            } else if (ll_dis_location_selection.getVisibility() == View.GONE) {
                ll_dis_location_selection.setVisibility(View.VISIBLE);
                arrow_down_image_view.setRotation(180);
            }
        });

        apply_filter_button.setOnClickListener(view -> {
            if (ll_dis_location_selection.getVisibility() == View.VISIBLE) {
                ll_dis_location_selection.setVisibility(View.GONE);
                arrow_down_image_view.setRotation(0);
            } else if (ll_dis_location_selection.getVisibility() == View.GONE) {
                ll_dis_location_selection.setVisibility(View.VISIBLE);
                arrow_down_image_view.setRotation(180);
            }


        });
    }

    private void filter(String text) {

        ArrayList<AdminCardHolderModel> filteredList = new ArrayList<>();
        for (AdminCardHolderModel item : arrayList) {
            if (item.getUID().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            adminCardAdapter.filterList(filteredList);
            no_data_found_layout.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
        } else {
            no_data_found_layout.setVisibility(View.GONE);
            adminCardAdapter.filterList(filteredList);
        }
    }

    private void fetchDataByApi(String dis, String teh, String vil, int page) {
        if (page == 1) {
            arrayList.clear();
        }
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAllRecordList(
                dis, teh, vil, page
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
                        if (BuildConfig.DEBUG) {
                            Log.d("TAG", "onResponse: " + jsonObject);
                        }

                        if (message.equalsIgnoreCase("Success")) {
                            if (status) {
                                total_page = jsonObject.optInt("totalPages");
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


                                progressDialog.dismiss();
                                if (arrayList.size() > 0) {
                                    no_data_found_layout.setVisibility(View.GONE);
                                    adminCardAdapter.notifyDataSetChanged();

                                } else {
                                    no_data_found_layout.setVisibility(View.VISIBLE);
                                }
                                all_verified_count.setText(String.valueOf(jsonObject.optString("totalCount")));

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AdminAllRecordScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AdminAllRecordScreen.this, "" + message, Toast.LENGTH_SHORT).show();

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


    private void load_village_data(String teh_name_code) {
        village_name_list.clear();
        village_code_list.clear();

        village_name_list.add(getString(R.string.select_one));
        village_code_list.add(getString(R.string.select_one));

        //progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(AdminAllRecordScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAllVillage("", teh_name_code);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");

                        //progressDialog.dismiss();
                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    if (!object.optString("n_v_code").equalsIgnoreCase(" ")) {
                                        village_name_list.add(object.optString("n_v_name"));
                                        village_code_list.add(object.optString("n_v_code"));

                                    }
                                    ArrayAdapter adapter = new ArrayAdapter(AdminAllRecordScreen.this, android.R.layout.simple_spinner_item, village_name_list);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    villageSpinner.setAdapter(adapter);
                                }

                            } else {
                                //progressDialog.dismiss();
                                Toast.makeText(AdminAllRecordScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //progressDialog.dismiss();
                            Toast.makeText(AdminAllRecordScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        //  progressDialog.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    //  progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    // progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //progressDialog.dismiss();
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

    private void loadDataForTehsil(String dis_code_string) {

        tehsil_name_list.clear();
        tehsil_code_list.clear();
        village_name_list.clear();
        village_code_list.clear();

        tehsil_name_list.add(getString(R.string.select_one));
        tehsil_code_list.add(getString(R.string.select_one));

        //progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(AdminAllRecordScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAllTehsil(dis_code_string);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");

                        //progressDialog.dismiss();
                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    if (!object.optString("n_t_code").equalsIgnoreCase(" ")) {
                                        tehsil_name_list.add(object.optString("n_t_name"));
                                        tehsil_code_list.add(object.optString("n_t_code"));

                                    }
                                    ArrayAdapter adapter = new ArrayAdapter(AdminAllRecordScreen.this, android.R.layout.simple_spinner_item, tehsil_name_list);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    tehsilSpinner.setAdapter(adapter);
                                }

                            } else {
                                //progressDialog.dismiss();
                                Toast.makeText(AdminAllRecordScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //progressDialog.dismiss();
                            Toast.makeText(AdminAllRecordScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        //progressDialog.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    // progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    // progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // progressDialog.dismiss();
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

    private void loadAllDistrict() {
        dist_name_list.clear();
        dist_code_list.clear();


        dist_name_list.add(getString(R.string.select_one));
        dist_code_list.add(getString(R.string.select_one));

        //progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(AdminAllRecordScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAllDistrict();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");

                        //progressDialog.dismiss();
                        if (message.equalsIgnoreCase("Success")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    if (!object.optString("n_d_code").equalsIgnoreCase(" ")) {
                                        dist_name_list.add(object.optString("n_d_name"));
                                        dist_code_list.add(object.optString("n_d_code"));

                                    }

                                    ArrayAdapter adapter = new ArrayAdapter(AdminAllRecordScreen.this, android.R.layout.simple_spinner_item, dist_name_list);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    districtSpinner.setAdapter(adapter);
                                }

                            } else {
                                //progressDialog.dismiss();
                                Toast.makeText(AdminAllRecordScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //progressDialog.dismiss();
                            Toast.makeText(AdminAllRecordScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        // progressDialog.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    //  progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    // progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // progressDialog.dismiss();
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
}