package com.app.harcdis.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Constants;
import com.app.harcdis.utils.Sp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseAreaScreen extends AppCompatActivity {
    private static final String TAG = "MyTag";
    TextView spinner_dis_name, spinner_tehsil_name, spinner_village_name;
    Button continue_btn;
    private ArrayList<String> village_code_list;
    private String dis_name_code;
    TextView login_user_name;
    TextView login_user_mobile_no;
    TextView login_user_department;
    private ArrayList<String> dist_tehsil_list;
    private ArrayList<String> dist_list;
    private ArrayList<String> dist_tehsil_code_list;
    private ArrayList<String> village_name_list;
    private String dis_name;
    private String teh_name;
    private String village_name;
    private ProgressDialog progressDialog;
    private String dis_code_string;
    private String teh_name_code;
    private String village_name_code;
    private String dis_name_string;
    private String selectedDistCode = "";
    private String selectedDistName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area_screen);
        initViews();
    }

    private void initViews() {
        continue_btn = findViewById(R.id.continue_btn);
        spinner_dis_name = findViewById(R.id.spinner_dis_name);
        spinner_tehsil_name = findViewById(R.id.spinner_tehsil_name);
        spinner_village_name = findViewById(R.id.spinner_village_name);
        login_user_name = findViewById(R.id.login_user_name);
        login_user_mobile_no = findViewById(R.id.login_user_mobile_no);
        login_user_department = findViewById(R.id.login_user_department);
        dist_tehsil_list = new ArrayList<>();
        village_name_list = new ArrayList<>();
        dist_tehsil_code_list = new ArrayList<>();
        village_code_list = new ArrayList<>();
        dist_list = new ArrayList<>();

        progressDialog = new ProgressDialog(ChooseAreaScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);


        login_user_name.setText(Sp.read_shared_pref(ChooseAreaScreen.this, "name"));
        login_user_mobile_no.setText(Sp.read_shared_pref(ChooseAreaScreen.this, "user_mobile"));
        login_user_department.setText(Sp.read_shared_pref(ChooseAreaScreen.this, "Designation"));

        manageUserAreaSelection();


        spinner_dis_name.setOnClickListener(view1 -> {
            SpinnerDialog spinnerDialog = new SpinnerDialog(ChooseAreaScreen.this, dist_list, getString(R.string.select_district_name));
            spinnerDialog.bindOnSpinerListener((s, i) -> {
                selectedDistCode = s.split("-")[1];
                selectedDistName = s.split("-")[0];
                village_name = "";
                village_name_code = null;
                teh_name = "";
                teh_name_code = null;
                spinner_dis_name.setText(selectedDistName);
                spinner_tehsil_name.setText(getString(R.string.select_tehsil_name));
                spinner_village_name.setText(getString(R.string.select_village_name));

                loadDataForTehsil(selectedDistCode);
            });
            spinnerDialog.showSpinerDialog();
        });


        spinner_tehsil_name.setOnClickListener(view1 -> {
            SpinnerDialog spinnerDialog = new SpinnerDialog(ChooseAreaScreen.this, dist_tehsil_list, getString(R.string.select_tehsil_name));
            spinnerDialog.bindOnSpinerListener((s, i) -> {
                teh_name = s;
                village_name = "";
                village_name_code = null;
                spinner_village_name.setText(getString(R.string.tap_here_to_select_village_name));
                spinner_tehsil_name.setText(s);
                teh_name_code = dist_tehsil_code_list.get(i);
                load_village_data(teh_name_code);
            });
            spinnerDialog.showSpinerDialog();
        });
        spinner_village_name.setOnClickListener(view1 -> {
            SpinnerDialog spinnerDialog = new SpinnerDialog(ChooseAreaScreen.this, village_name_list, getString(R.string.select_village_name));
            spinnerDialog.bindOnSpinerListener((s, i) -> {
                village_name = s;
                spinner_village_name.setText(s);
                village_name_code = village_code_list.get(i);

            });
            spinnerDialog.showSpinerDialog();
        });

//        continue_btn.setOnClickListener(view -> {
//            if (selectedDistCode.isEmpty()) {
//                Toast.makeText(this, getString(R.string.select_district_name), Toast.LENGTH_SHORT).show();
//            } else {
//                Sp.write_shared_pref(ChooseAreaScreen.this, "dis_code_store", selectedDistCode);
//                Sp.write_shared_pref(ChooseAreaScreen.this, "dis_code_name", selectedDistName);
//                Sp.write_shared_pref(ChooseAreaScreen.this, "teh_code_store", teh_name_code);
//                Sp.write_shared_pref(ChooseAreaScreen.this, "teh_code_store_name", teh_name);
//                Sp.write_shared_pref(ChooseAreaScreen.this, "village_code_store", village_name_code);
//                Sp.write_shared_pref(ChooseAreaScreen.this, "village_code_store_name", village_name);
//                Intent intent = new Intent(ChooseAreaScreen.this, MapScreen.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//            }
//
//
//        });




        continue_btn.setOnClickListener(view -> {
            // Validate district selection
            if (selectedDistCode == null || selectedDistCode.trim().isEmpty()) {
                Toast.makeText(this, getString(R.string.select_district_name), Toast.LENGTH_SHORT).show();
                return;
            }

            // Use safe defaults to avoid putting null into SharedPreferences
            String safeSelectedDistCode = selectedDistCode != null ? selectedDistCode : "";
            String safeSelectedDistName = selectedDistName != null ? selectedDistName : "";
            String safeTehCode = teh_name_code != null ? teh_name_code : "";
            String safeTehName = teh_name != null ? teh_name : "";
            String safeVillageCode = village_name_code != null ? village_name_code : "";
            String safeVillageName = village_name != null ? village_name : "";

            // Debug log - helps in Logcat to see what values we are about to store
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Continue clicked with: distCode=" + safeSelectedDistCode
                        + " distName=" + safeSelectedDistName
                        + " tehCode=" + safeTehCode
                        + " tehName=" + safeTehName
                        + " villageCode=" + safeVillageCode
                        + " villageName=" + safeVillageName);
            }

            // Write safe values to SharedPreferences
            Sp.write_shared_pref(ChooseAreaScreen.this, "dis_code_store", safeSelectedDistCode);
            Sp.write_shared_pref(ChooseAreaScreen.this, "dis_code_name", safeSelectedDistName);
            Sp.write_shared_pref(ChooseAreaScreen.this, "teh_code_store", safeTehCode);
            Sp.write_shared_pref(ChooseAreaScreen.this, "teh_code_store_name", safeTehName);
            Sp.write_shared_pref(ChooseAreaScreen.this, "village_code_store", safeVillageCode);
            Sp.write_shared_pref(ChooseAreaScreen.this, "village_code_store_name", safeVillageName);

            // Start MapScreen in try/catch to avoid unhandled crash — and log exception for debugging
            try {
                Intent intent = new Intent(ChooseAreaScreen.this, MapScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e(TAG, "Failed to start MapScreen", e);
                Toast.makeText(ChooseAreaScreen.this,
                        "Unable to open Map. Please try again. (" + e.getClass().getSimpleName() + ")", Toast.LENGTH_LONG).show();
            }
        });





    }


    private void load_village_data(String teh_name_code) {
        village_name_list.clear();
        village_code_list.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(ChooseAreaScreen.this).create(ApiInterface.class);
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

                        progressDialog.dismiss();
                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    if (!object.optString("n_v_code").equalsIgnoreCase(" ")) {
                                        village_name_list.add(object.optString("n_v_name"));
                                        village_code_list.add(object.optString("n_v_code"));
                                        spinner_village_name.setEnabled(true);
                                    }
                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ChooseAreaScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ChooseAreaScreen.this, "" + message, Toast.LENGTH_SHORT).show();
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


    private void manageUserAreaSelection() {
        dis_code_string = Sp.read_shared_pref(ChooseAreaScreen.this, "n_d_code");
        dis_name_string = Sp.read_shared_pref(ChooseAreaScreen.this, "n_d_name");
        if (dis_code_string.equalsIgnoreCase("99")) {
            loadAllDistrict();
        } else {
            if (dis_code_string.contains(",")) {
                String[] dis_assign_to_user = dis_code_string.split(",");
                String[] dis_name_assign_to_user = dis_name_string.split(",");

                for (int i = 0; i < dis_assign_to_user.length; i++) {
                    dist_list.add(dis_name_assign_to_user[i] + "-" + dis_assign_to_user[i]);
                }
            } else {
                dist_list.add(dis_name_string + "-" + dis_code_string);
            }
        }
    }








    private void loadAllDistrict() {
        dist_list.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(ChooseAreaScreen.this).create(ApiInterface.class);
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
                        progressDialog.dismiss();
                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    if (!object.optString("n_d_code").equalsIgnoreCase(" ")) {
                                        dist_list.add(object.optString("n_d_name") + "-" + object.optString("n_d_code"));
                                    }
                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ChooseAreaScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ChooseAreaScreen.this, "" + message, Toast.LENGTH_SHORT).show();
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


    private void fetch_user_details_and_show_by_api() {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(ChooseAreaScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getUserData(Sp.read_shared_pref(ChooseAreaScreen.this, Constants.mobile_number));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.optString("message");
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "onResponse: " + result);
                        }
                        boolean status = jsonObject.optBoolean("status");
                        progressDialog.dismiss();

                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                                if (Objects.equals("null", jsonObject1.optString("n_d_code"))) {
                                    dis_code_string = "";
                                } else {
                                    dis_code_string = jsonObject1.optString("n_d_code");
                                }

                                if (Objects.equals("null", jsonObject1.optString("n_d_name"))) {
                                    dis_name_string = "";
                                } else {
                                    dis_name_string = jsonObject1.optString("n_d_name");
                                }
                                Sp.write_shared_pref(ChooseAreaScreen.this, "user_name", jsonObject1.optString("name"));
                                Sp.write_shared_pref(ChooseAreaScreen.this, "user_mobile", jsonObject1.optString("mobile"));
                                Sp.write_shared_pref(ChooseAreaScreen.this, "roleId", jsonObject1.optString("roleId"));
                                if (dis_code_string.contains(",")) {
                                    String[] dis_assign_to_user = dis_code_string.split(",");
                                    String[] dis_name_assign_to_user = dis_name_string.split(",");

                                    for (int i = 0; i < dis_assign_to_user.length; i++) {
                                        dist_list.add(dis_name_assign_to_user[i] + "-" + dis_assign_to_user[i]);
                                    }
                                } else {
                                    dist_list.add(dis_name_string + "-" + dis_code_string);

                                }

                            } else {
                                Toast.makeText(ChooseAreaScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChooseAreaScreen.this, "" + message, Toast.LENGTH_SHORT).show();
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

    private void loadDataForTehsil(String dis_code_string) {

        dist_tehsil_list.clear();
        dist_tehsil_code_list.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(ChooseAreaScreen.this).create(ApiInterface.class);
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

                        progressDialog.dismiss();
                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    if (!object.optString("n_t_code").equalsIgnoreCase(" ")) {
                                        dist_tehsil_list.add(object.optString("n_t_name"));
                                        dist_tehsil_code_list.add(object.optString("n_t_code"));
                                        spinner_tehsil_name.setEnabled(true);
                                    }
                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ChooseAreaScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ChooseAreaScreen.this, "" + message, Toast.LENGTH_SHORT).show();
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

    private String manageDisCode(String dis_code_string) {
        if (Integer.parseInt(dis_code_string) <= 9) {
            return "0" + dis_code_string;
        } else {
            return dis_code_string;
        }
    }
}