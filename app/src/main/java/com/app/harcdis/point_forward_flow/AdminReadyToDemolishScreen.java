package com.app.harcdis.point_forward_flow;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adapter.SelectVerifiedAdapter;
import com.app.harcdis.adminRole.model.AdminCardHolderModel;
import com.app.harcdis.adminRole.model.OffcialMemberModel;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.click_interface.AdminForwardClickHandle;
import com.app.harcdis.utils.Sp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReadyToDemolishScreen extends AppCompatActivity implements AdminForwardClickHandle {

    RecyclerView verifiedPointRecyclerView;
    SearchView itemSearchView;
    ImageView no_item_found;
    private ProgressDialog progressDialog;

    ArrayList<AdminCardHolderModel> arrayList;
    ArrayList<OffcialMemberModel> memberArrayList;

    SelectVerifiedAdapter selectVerifiedAdapter;
    private Dialog dialogfinal;
    String member_name;
    String member_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ready_to_demolish_screen);


        itemSearchView = findViewById(R.id.itemSearchView);
        no_item_found = findViewById(R.id.no_item_found);
        verifiedPointRecyclerView = findViewById(R.id.verifiedPointRecyclerView);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        arrayList = new ArrayList<>();
        memberArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(AdminReadyToDemolishScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        selectVerifiedAdapter = new SelectVerifiedAdapter(AdminReadyToDemolishScreen.this, arrayList, this::ClickHandleForAdmin);
        verifiedPointRecyclerView.setLayoutManager(new LinearLayoutManager(AdminReadyToDemolishScreen.this));
        verifiedPointRecyclerView.setAdapter(selectVerifiedAdapter);


        itemSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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
        getVerifiedPointFromDatabase();

    }


    private void updateStatusInDatabase(String gisId, String action) {
        Toast.makeText(this, "" + gisId, Toast.LENGTH_SHORT).show();
        Log.d("TAG", "updateStatusInDatabase: " + Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "user_name"));
        Log.d("TAG", "updateStatusInDatabase: " + Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "user_mobile"));
        progressDialog.show();


        Call<ResponseBody> call;

        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        if (action.equals("view")) {
            call = apiInterface.updateStatusByAdmin("APPROVED", gisId, Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "user_name"),
                    Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "user_mobile"), "", "",
                    Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "login_with")

            );
        } else {
            call = apiInterface.updateStatusByAdmin("READY TO DEMOLISH", gisId, Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "user_name"),
                    Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "user_mobile"), member_name, member_mobile, Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "login_with")

            );
        }

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
                        if (message.equalsIgnoreCase("Update Status By Admin")) {
                            if (status) {
                                Toast.makeText(AdminReadyToDemolishScreen.this, getString(R.string.point_status_is_updated), Toast.LENGTH_SHORT).show();
                                getVerifiedPointFromDatabase();
                            } else {
                                Toast.makeText(AdminReadyToDemolishScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminReadyToDemolishScreen.this, message, Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage());
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                if (BuildConfig.DEBUG) {
                    Log.i("Resp onFailure: ", t.getMessage());
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

    private void getVerifiedPointFromDatabase() {
        arrayList.clear();
        progressDialog.show();
        Log.d("TAG", "getVerifiedPointFromDatabase: " + Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "dis_code_store"));
        Log.d("TAG", "getVerifiedPointFromDatabase: " + Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "login_with"));
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.listAccordingStatus("Y", Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "dis_code_store"), "", "", Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "login_with"));
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
                        if (message.equalsIgnoreCase("Success")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    arrayList.add(new AdminCardHolderModel(object.optString("n_d_name"), object.optString("n_t_name"), object.optString("n_v_name"), object.optString("n_murr_no"), object.optString("n_khas_no"), object.optString("entry_date"), object.optString("verifiedBy"), object.optString("verified"), object.optString("gisId"), object.optString("UID"), object.optString("auth_status")));
                                }
                                if (arrayList.size() > 0) {
                                    verifiedPointRecyclerView.setVisibility(View.VISIBLE);
                                    no_item_found.setVisibility(View.GONE);
                                    selectVerifiedAdapter.notifyDataSetChanged();

                                } else {
                                    no_item_found.setVisibility(View.VISIBLE);
                                    verifiedPointRecyclerView.setVisibility(View.GONE);
                                }


                            } else {
                                Toast.makeText(AdminReadyToDemolishScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminReadyToDemolishScreen.this, message, Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage());
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                if (BuildConfig.DEBUG) {
                    Log.i("Resp onFailure: ", t.getMessage());
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


    private void getOfficalUserList(String uid) {
        memberArrayList.clear();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getOfficialsList(Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "dis_code_store"), Sp.read_shared_pref(AdminReadyToDemolishScreen.this, "login_with"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");
                        if (message.equalsIgnoreCase("Officials List")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    memberArrayList.add(new OffcialMemberModel(object.optString("name"), object.optString("mobile"), object.optString("username")));
                                }
                                if (memberArrayList.isEmpty()) {
                                    Toast.makeText(AdminReadyToDemolishScreen.this, "No User Found", Toast.LENGTH_SHORT).show();
                                } else {
                                    showSpinnerDialog(uid);
                                }


                            } else {
                                Toast.makeText(AdminReadyToDemolishScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminReadyToDemolishScreen.this, message, Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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

    private void showSpinnerDialog(String uid) {
        dialogfinal = new Dialog(AdminReadyToDemolishScreen.this);
        dialogfinal.setContentView(R.layout.forward_admin_user_layout);
        Button cancel = dialogfinal.findViewById(R.id.cancal_button_dialog);
        Button submit = dialogfinal.findViewById(R.id.submit_button_dialog);
        Spinner spinner = dialogfinal.findViewById(R.id.member_spinner);
        ArrayAdapter adapter = new ArrayAdapter(AdminReadyToDemolishScreen.this, android.R.layout.simple_spinner_item, memberArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        dialogfinal.setCancelable(false);
        dialogfinal.show();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                OffcialMemberModel offcialMemberModel = (OffcialMemberModel) adapterView.getItemAtPosition(i);
                member_name = offcialMemberModel.getUsername();
                member_mobile = offcialMemberModel.getMobile();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AdminReadyToDemolishScreen.this, "", Toast.LENGTH_SHORT).show();
            }
        });
        submit.setOnClickListener(view15 -> {
            if (member_mobile.isEmpty() && member_name.isEmpty()) {
                Toast.makeText(this, "Select Official Name", Toast.LENGTH_SHORT).show();
            } else {
                dialogfinal.dismiss();
                updateStatusInDatabase(uid, "forward");
            }
        });

        cancel.setOnClickListener(view16 -> {
            dialogfinal.dismiss();
        });


    }

    private void UpdateAdminPointStatus() {

    }


    private void onFailed(String s, String s1) {
        Toast.makeText(this, s1, Toast.LENGTH_SHORT).show();
    }


    private void filter(String text) {

        ArrayList<AdminCardHolderModel> filteredList = new ArrayList<>();
        for (AdminCardHolderModel item : arrayList) {
            if (item.getUID().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            selectVerifiedAdapter.filterList(filteredList);
            no_item_found.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            no_item_found.setVisibility(View.GONE);
            selectVerifiedAdapter.filterList(filteredList);
        }
    }


    @Override
    public void ClickHandleForAdmin(String uid, String action) {
        if (action.equals("view")) {
            updateStatusInDatabase(uid, "view");
        } else if (action.equals("forward")) {
            getOfficalUserList(uid);
        }
    }
}