package com.app.harcdis.point_forward_flow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import android.widget.Toast;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;

import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.point_forward_flow.model.ForwardedModel;
import com.app.harcdis.utils.Sp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DTPForwardedPointScreen extends AppCompatActivity {
    RecyclerView forward_recycler_view;

    private ProgressDialog progressDialog;
    ArrayList<ForwardedModel> arrayList;
    ForwardedPointAdapter forwardedPointAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distrcit_verified_record);
        forward_recycler_view = findViewById(R.id.forward_recycler_view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        arrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(DTPForwardedPointScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        forwardedPointAdapter = new ForwardedPointAdapter(DTPForwardedPointScreen.this, arrayList);
        forward_recycler_view.setLayoutManager(new LinearLayoutManager(DTPForwardedPointScreen.this));
        forward_recycler_view.setAdapter(forwardedPointAdapter);
        getVerifiedPointFromDatabase();

    }

    private void getVerifiedPointFromDatabase() {
        arrayList.clear();
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAllForwardedData("manish", Sp.read_shared_pref(DTPForwardedPointScreen.this, "dis_code_store"), "", "", Sp.read_shared_pref(DTPForwardedPointScreen.this, "login_with"));
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
                        if (message.equalsIgnoreCase("All Forwarded Data")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    arrayList.add(new ForwardedModel(object.optString("UID"), object.optString("latitude"), object.optString("longitude"), object.optString("assigner_name"), object.optString("assigner_mobile")));
                                }
                                if (arrayList.size() > 0) {
                                    forward_recycler_view.setVisibility(View.VISIBLE);
                                    forwardedPointAdapter.notifyDataSetChanged();

                                } else {
                                    forward_recycler_view.setVisibility(View.GONE);
                                }


                            } else {
                                Toast.makeText(DTPForwardedPointScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DTPForwardedPointScreen.this, message, Toast.LENGTH_SHORT).show();

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


    private void onFailed(String s, String s1) {
        Toast.makeText(this, s1, Toast.LENGTH_SHORT).show();
    }

}