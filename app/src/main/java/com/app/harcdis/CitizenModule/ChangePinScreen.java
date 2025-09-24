package com.app.harcdis.CitizenModule;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.screens.SplashScreen;
import com.app.harcdis.utils.Sp;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePinScreen extends AppCompatActivity {
    EditText saved_mobile, old_pin_box, new_pin_box;
    Button changeButton;
    Vibrator vi;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        progressDialog = new ProgressDialog(ChangePinScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);


        saved_mobile = findViewById(R.id.saved_mobile);
        old_pin_box = findViewById(R.id.old_pin_box);
        new_pin_box = findViewById(R.id.new_pin_box);
        changeButton = findViewById(R.id.changeButton);

        saved_mobile.setText(Sp.read_shared_pref(this, "user_mobile"));
        changeButton.setOnClickListener(v -> {
            if (old_pin_box.getText().toString().isEmpty()) {
                callVibrator();
                old_pin_box.setError("Enter Your Old Pin");
            } else if (new_pin_box.getText().toString().isEmpty()) {
                callVibrator();
                new_pin_box.setError("Enter New Pin");
            } else {
                changePinAPI(old_pin_box.getText().toString(),new_pin_box.getText().toString());

            }
        });
    }

    private void changePinAPI(String oldPin, String newPin) {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(ChangePinScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.changePin(saved_mobile.getText().toString(), oldPin, newPin);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        String message = jsonObject.optString("message");

                        progressDialog.dismiss();
                        if (status) {
                            if (message.equalsIgnoreCase("User Pin Changed Successfully")) {
                                Toast.makeText(ChangePinScreen.this, message, Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePinScreen.this);
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage(getString(R.string.pin_changed));
                                alertDialog.setNeutralButton(getString(R.string.ok), (dialog, which) -> {
                                    Sp.logout(ChangePinScreen.this);

                                });
                                alertDialog.show();
                            } else {
                                Toast.makeText(ChangePinScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChangePinScreen.this, message, Toast.LENGTH_SHORT).show();
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

    public void callVibrator(){
        if (vi.hasVibrator()) {
            vi.vibrate(100);
        }
    }


    public void onFailed(String message, String description) {
        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
    }
}