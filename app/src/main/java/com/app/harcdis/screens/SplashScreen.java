package com.app.harcdis.screens;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Connection_Detector;
import com.app.harcdis.utils.Sp;
import com.app.harcdis.utils.TypeWriter;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalUser;
import com.esri.arcgisruntime.portal.PortalUserContent;
import com.esri.arcgisruntime.security.UserCredential;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "MyTag";

    ProgressDialog progressDialog;
    ImageView splash_image;
    private LocationManager locationManager;
    private boolean GpsStatus;
    private int version = 0;
    private TextView splash_text;
    private ProgressBar splash_progress_bar;
    Connection_Detector connection_detector;
    TypeWriter app_mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        app_mode = findViewById(R.id.app_mode);

        splash_image = findViewById(R.id.splash_image);
        splash_progress_bar = findViewById(R.id.splash_progress_bar);
        splash_text = findViewById(R.id.splash_text);
        progressDialog = new ProgressDialog(SplashScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        connection_detector = new Connection_Detector(getApplicationContext());

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();



    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CheckGpsStatus();
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void CheckGpsStatus() {

        splash_text.setText(getString(R.string.gps_status_));
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (GpsStatus) {
            if (connection_detector.isConnected()) {

                check_app_version();
            } else {
                Toast.makeText(this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent1);

            }
        } else {
            Toast.makeText(SplashScreen.this, getString(R.string.on_your_mobile_gps), Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
        }
    }

    private void portal_login() {

        Log.d(TAG, "portal_login: ");
        UserCredential credential = new UserCredential(Sp.read_shared_pref(SplashScreen.this, "user_name_needed"),
                Sp.read_shared_pref(SplashScreen.this, "passowrd_needed"));
        final Portal portal = new Portal(Sp.read_shared_pref(SplashScreen.this, "portal_login_url_ggm"), true);
        portal.setCredential(credential);
        portal.addDoneLoadingListener(() -> {
            if (portal.getLoadStatus() == LoadStatus.LOADED) {
                splash_text.setText(getString(R.string.connection_established));
                //    Toast.makeText(SplashScreen.this, "Connection Established.", Toast.LENGTH_SHORT).show();
                PortalUser user = portal.getUser();
                final ListenableFuture<PortalUserContent> contentFuture = user.fetchContentAsync();
                contentFuture.addDoneListener(() -> {
                    try {
                        splash_progress_bar.setProgress(100, true);
                        startActivity(new Intent(SplashScreen.this, HCDISLoginScreen.class));
                        finish();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                });

            } else {
                Toast.makeText(SplashScreen.this, getString(R.string.portal_login_failed), Toast.LENGTH_SHORT).show();
            }
        });

        portal.loadAsync();

    }


    private void portal_login_vector_tile_layer() {


        Log.d(TAG, "portal_login: ");
        UserCredential credential = new UserCredential(Sp.read_shared_pref(SplashScreen.this, "user_name_hass"), Sp.read_shared_pref(SplashScreen.this, "password_hass"));
        final Portal portal = new Portal(Sp.read_shared_pref(SplashScreen.this, "hass_portal_login_url"), true);
        portal.setCredential(credential);
        portal.addDoneLoadingListener(() -> {
            if (portal.getLoadStatus() == LoadStatus.LOADED) {

                //    Toast.makeText(SplashScreen.this, "Connection Established.", Toast.LENGTH_SHORT).show();
                PortalUser user = portal.getUser();
                final ListenableFuture<PortalUserContent> contentFuture = user.fetchContentAsync();
                contentFuture.addDoneListener(() -> {
                    try {
                        splash_progress_bar.setProgress(100, true);
                        startActivity(new Intent(SplashScreen.this, HCDISLoginScreen.class));
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } else {
                Toast.makeText(SplashScreen.this, getString(R.string.portal_login_failed), Toast.LENGTH_SHORT).show();
            }
        });

        portal.loadAsync();

    }


    private void check_app_version() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ApiInterface apiInterface = RetrofitClient.getRetrofitClientForOTP(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.checkVersion("tcp_nba_app");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        splash_progress_bar.setProgress(25, true);
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String api_response_msg = jsonObject.getString("responseType");

                        JSONObject json_object_inner = jsonObject.getJSONObject("responsemsg");
                        String data_msg = json_object_inner.getString("Msg");

                        JSONArray jsonArray = jsonObject.getJSONArray("responseData");
                        if (api_response_msg.equals("Success")) {
                            if (data_msg.equals("Data Fetched")) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                String app_host_url = jsonObject1.getString("app_host_url");
                                String latest_app_version = jsonObject1.getString("priority_min_version");
                                String under_maintenance = jsonObject1.getString("under_maintainence");
                                String under_maintenance_text = jsonObject1.getString("under_maintainence_text");


                                Sp.write_shared_pref(SplashScreen.this, "live_base_url", jsonObject1.getString("base_url"));
                                Sp.write_shared_pref(SplashScreen.this, "staging_url", jsonObject1.getString("staging_url"));
                                Sp.write_shared_pref(SplashScreen.this, "active_mode", String.valueOf(jsonObject1.getInt("active_mode")));
                                Sp.write_shared_pref(SplashScreen.this, "login_with", jsonObject1.getString("login_with"));
                                Log.d(TAG, "onResponse: "+jsonObject1.getString("login_with"));

                                if(Sp.read_shared_pref(SplashScreen.this,"active_mode").equalsIgnoreCase("1")){
                                    app_mode.setCharacterDelay(20);
                                    app_mode.animateText("V.C"+version+" LIVE...........");
                                }else{
                                    app_mode.setCharacterDelay(20);
                                    app_mode.animateText("V.C"+version+" STAGING..........");
                                }

                                match_version_and_show_dialog(under_maintenance, under_maintenance_text, app_host_url, latest_app_version);

                            } else {
                                Toast.makeText(SplashScreen.this, "" + data_msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SplashScreen.this, "" + data_msg, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {

                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {

                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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

    private void match_version_and_show_dialog(String under_maintenance, String under_maintenance_text, String app_host_url, String latest_app_version) {
        Log.d(TAG, "match_version_and_show_dialog: " + version + latest_app_version);
        if (version >= Integer.parseInt(latest_app_version)) {

            //   Toast.makeText(SplashScreen.this, "Your Application is Updated", Toast.LENGTH_SHORT).show();
            if (under_maintenance.equals("YES")) {
                Toast.makeText(SplashScreen.this, "" + under_maintenance_text, Toast.LENGTH_SHORT).show();
            } else {
                fetchNeededDetailsAndSave();

            }
        } else {
            OpenNewVersion(app_host_url, app_host_url);
        }
    }


    private void onFailed(String s, String s1) {
        Toast.makeText(this, "" + s1, Toast.LENGTH_SHORT).show();
    }

    private void OpenNewVersion(String google_play_url, String apk_url) {

        LinearLayout app_store_image;
        Dialog updateBox = new Dialog(SplashScreen.this);
        updateBox.setContentView(R.layout.update_app_layout);
        updateBox.setCancelable(false);

        app_store_image = updateBox.findViewById(R.id.ll_Doupdate);
        app_store_image.setOnClickListener(v -> {
            if (apk_url.length() > 5) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(apk_url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent
                        .FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                updateBox.dismiss();
                finish();
            }
        });
        updateBox.show();
    }


    private void fetchNeededDetailsAndSave() {
        Log.d(TAG, "fetchNeededDetailsAndSave: ");
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAllNeededData(Sp.read_shared_pref(SplashScreen.this,"login_with"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.optString("message");
                        boolean status = jsonObject.optBoolean("status");
                        splash_progress_bar.setProgress(50, true);

                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                                Sp.write_shared_pref(SplashScreen.this, "user_name_needed", jsonObject1.getString("user_name"));
                                Sp.write_shared_pref(SplashScreen.this, "passowrd_needed", jsonObject1.getString("passowrd"));
                                Sp.write_shared_pref(SplashScreen.this, "app_mode_needed", jsonObject1.getString("app_mode"));
                                Sp.write_shared_pref(SplashScreen.this, "offline_point_count_needed", jsonObject1.getString("offline_point_count"));
                                Sp.write_shared_pref(SplashScreen.this, "buffer_size_needed", jsonObject1.getString("buffer_size"));
                                Sp.write_shared_pref(SplashScreen.this, "portal_login_url_ggm", jsonObject1.getString("portal_login_url_ggm"));
                                Sp.write_shared_pref(SplashScreen.this, "user_name_hass", jsonObject1.getString("user_name_hass"));
                                Sp.write_shared_pref(SplashScreen.this, "password_hass", jsonObject1.getString("password_hass"));
                                Sp.write_shared_pref(SplashScreen.this, "hass_portal_login_url", jsonObject1.getString("hass_portal_login_url"));
                                Sp.write_shared_pref(SplashScreen.this, "image_quality", jsonObject1.getString("image_quality"));
                                Sp.write_shared_pref(SplashScreen.this, "sso_login_url", jsonObject1.getString("sso_login_url"));
                                Sp.write_shared_pref(SplashScreen.this, "sso_login_url_staging", jsonObject1.getString("sso_login_url_staging"));
                                Sp.write_shared_pref(SplashScreen.this, "video_width", jsonObject1.getString("video_width"));
                                Sp.write_shared_pref(SplashScreen.this, "video_height", jsonObject1.getString("video_height"));
                                splash_progress_bar.setProgress(100, true);
                                startActivity(new Intent(SplashScreen.this, HCDISLoginScreen.class));
                                finish();
                            } else {
                                Toast.makeText(SplashScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SplashScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "PPlease Try Again later ");


                } else {

                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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
