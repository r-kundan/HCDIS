package com.app.harcdis.api;

import static com.app.harcdis.firebase.MyFirebaseNService.TAG;

import android.content.Context;
import android.util.Log;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.utils.Sp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL_FEATURE = "https://onemapdepts.gmda.gov.in/";
    private static final String BASE_URL_SSO_LOGIN = "https://tcpharyana.gov.in/EODB/api/mobileapp/";
    //private static final String BASE_URL_SSO_LOGIN = "http://182.79.97.53:81/api/mobileapp/";
    private static final String BASE_URL_STAGING = "https://onemapdepts.gmda.gov.in/API_tcp_encroachment_v1.0";

    private static Retrofit retrofitObject3 = null;
    private static Retrofit retrofitObjectSSO = null;
    private static Retrofit retrofitObject2 = null;


    private RetrofitClient() {

    }


    public static Retrofit getRetrofitClientForOTP(Context context) {
        if (retrofitObject3 == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .build();

            retrofitObject3 = new Retrofit.Builder().
                    baseUrl(BASE_URL_FEATURE)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofitObject3;
    }


    public static Retrofit getRetrofitClient(Context context) {


        String currentlyUsedUrl = "";

        if (Sp.read_shared_pref(context, "active_mode").equalsIgnoreCase("1")) {
            currentlyUsedUrl = Sp.read_shared_pref(context, "live_base_url");
        } else {
            currentlyUsedUrl = Sp.read_shared_pref(context, "staging_url");
        }

        //String currentlyUsedUrl = BASE_URL_STAGING;
        Sp.write_shared_pref(context, "app_base_url_needed", currentlyUsedUrl);

        if (BuildConfig.DEBUG)
            Log.d(TAG, "getRetrofitClient: " + currentlyUsedUrl);

        if (retrofitObject2 == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .addInterceptor(interceptor)
                    .build();

            retrofitObject2 = new Retrofit.Builder().
                    baseUrl(currentlyUsedUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofitObject2;
    }

    public static Retrofit getRetrofitClientSSO(Context context) {


        String currentlyUsedUrl = "";

        if (Sp.read_shared_pref(context, "active_mode").equalsIgnoreCase("1")) {
            currentlyUsedUrl = Sp.read_shared_pref(context, "sso_login_url");
        } else {
            currentlyUsedUrl = Sp.read_shared_pref(context, "sso_login_url_staging");
        }


        if (BuildConfig.DEBUG)
            Log.d("Tag", currentlyUsedUrl);
        if (retrofitObjectSSO == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .build();

            retrofitObjectSSO = new Retrofit.Builder().
                    baseUrl(currentlyUsedUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofitObjectSSO;
    }

}
