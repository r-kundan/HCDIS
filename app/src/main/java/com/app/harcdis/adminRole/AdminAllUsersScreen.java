package com.app.harcdis.adminRole;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adapter.AdminUserAdapter;
import com.app.harcdis.adminRole.model.AdminUserModel;
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

public class AdminAllUsersScreen extends AppCompatActivity {
    TickerView total_user;
    RecyclerView unverified_record_recyclerView;
    LinearLayout no_data_found_layout;
    ArrayList<AdminUserModel> arrayList;
    ProgressDialog progressDialog;
    AdminUserAdapter adminUserAdapter;
    SearchView userSearchView;
    private TextView no_user_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_users_screen);
        initViews();
    }

    private void initViews() {
        arrayList = new ArrayList<>();
        adminUserAdapter = new AdminUserAdapter(AdminAllUsersScreen.this, arrayList);
        userSearchView = findViewById(R.id.userSearchView);
        no_user_found = findViewById(R.id.no_user_found);
        total_user = findViewById(R.id.total_user);
        unverified_record_recyclerView = findViewById(R.id.total_user_recyclerview);
        no_data_found_layout = findViewById(R.id.no_data_found_layout);
        unverified_record_recyclerView.setLayoutManager(new LinearLayoutManager(AdminAllUsersScreen.this));
        unverified_record_recyclerView.setAdapter(adminUserAdapter);

        progressDialog = new ProgressDialog(AdminAllUsersScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));


        userSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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

        fetchDataByApi();
    }


    private void filter(String text) {

        ArrayList<AdminUserModel> filteredList = new ArrayList<>();
        for (AdminUserModel item : arrayList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getMobile().toLowerCase().contains(text.toLowerCase())|| item.getUser_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            adminUserAdapter.filterList(filteredList);
            no_user_found.setVisibility(View.VISIBLE);
            Toast.makeText(this,getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();

        } else {
            no_user_found.setVisibility(View.GONE);
            adminUserAdapter.filterList(filteredList);
        }
    }

    private void fetchDataByApi() {
        arrayList.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAdminUserList(
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
                                    arrayList.add(new AdminUserModel(
                                            object.optString("name"),
                                            object.optString("mobile"),
                                            object.optString("n_d_name"),
                                            object.optString("roleId"),
                                            object.optString("username"),
                                            object.optString("designation")

                                    ));

                                }
                                if (arrayList.size() > 0) {
                                    unverified_record_recyclerView.setVisibility(View.VISIBLE);
                                    no_data_found_layout.setVisibility(View.GONE);
                                    adminUserAdapter.notifyDataSetChanged();

                                } else {
                                    no_data_found_layout.setVisibility(View.VISIBLE);
                                    unverified_record_recyclerView.setVisibility(View.GONE);
                                }
                                total_user.setText(String.valueOf(arrayList.size()));

                            } else {
                                Toast.makeText(AdminAllUsersScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminAllUsersScreen.this, "" + message, Toast.LENGTH_SHORT).show();

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