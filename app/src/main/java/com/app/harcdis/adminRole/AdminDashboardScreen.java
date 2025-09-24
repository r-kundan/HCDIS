package com.app.harcdis.adminRole;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.Fragment.DashboardFragment;
import com.app.harcdis.Fragment.HomeFragment;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Sp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardScreen extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    DashboardFragment dashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard_screen);


        dashboardFragment = new DashboardFragment();
        homeFragment = new HomeFragment();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
        generate_and_save_token();

    }


    private void generate_and_save_token() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String fcm_token = task.getResult();
                    Log.d("token ", "onComplete: " + fcm_token);
                    save_token_in_db(fcm_token);
                }
            }
        });

    }



    private void save_token_in_db(String fcm_token) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(AdminDashboardScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.updateTokenInDb(Sp.read_shared_pref(AdminDashboardScreen.this,"user_mobile"), fcm_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject obj = new JSONObject(result);

                        if(BuildConfig.DEBUG) {
                            Log.d("Resp: ", "" + obj);
                            Log.d("TAG", "onResponse: " + Sp.read_shared_pref(AdminDashboardScreen.this, "user_mobile"));
                        }
                        boolean status = obj.optBoolean("status");
                        String message = obj.optString("message");
                        if (status) {
                            if(message.equalsIgnoreCase("Sucess")){
                                Log.d("TAG", "onResponse:token Saved Successfully ");
                            }
                        } else {
                            Toast.makeText(AdminDashboardScreen.this, ""+message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        if(BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    if(BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    if(BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;
            case R.id.dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, dashboardFragment).commit();
                return true;
        }
        return false;
    }
}