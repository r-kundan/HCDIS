package com.app.harcdis.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adminRole.AdminAllRecordAccordingToDateScreen;
import com.app.harcdis.adminRole.AdminAllRecordScreen;
import com.app.harcdis.adminRole.AdminAllUsersScreen;
import com.app.harcdis.adminRole.AdminNewPointScreen;
import com.app.harcdis.point_forward_flow.AdminReadyToDemolishScreen;
import com.app.harcdis.adminRole.AdminUnVerifiedRecordScreen;
import com.app.harcdis.adminRole.AdminVerifiedRecordScreen;
import com.app.harcdis.adminRole.MapViewScreen;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Sp;
import com.robinhood.ticker.TickerView;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    ProgressDialog progressDialog;
    TickerView total_builtup_count;
    TickerView total_verified_record;
    TickerView total_unverified_record;
    TickerView total_user_count;
    ProgressBar progress_bar_loading;
    RelativeLayout card_for_total_verified_records;
    RelativeLayout card_two_unverified_record;
    LinearLayout card_zero_total_point;
    CardView card_logout;
    CardView card_date_view;
    CardView card_total_user;
    CardView report_card_view_click;
    CardView update_status_card_view_click;

    RelativeLayout total_record_on_map_layout,verified_record_on_map_layout,unverified_record_on_map_layout;

    int totalBuiltUpPoint;
    int totalVerifiedPoint;
    int totalUnverifiedPoint;
    int totalUser;

    String query;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
  
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        total_builtup_count = view.findViewById(R.id.total_builtup_count);
        total_verified_record = view.findViewById(R.id.total_verified_record);
        total_unverified_record = view.findViewById(R.id.total_unverified_record);
        total_user_count = view.findViewById(R.id.total_user_count);
        progress_bar_loading = view.findViewById(R.id.progress_bar_loading);
        card_for_total_verified_records = view.findViewById(R.id.card_for_total_verified_records);
        card_two_unverified_record = view.findViewById(R.id.card_two_unverified_record);
        card_zero_total_point = view.findViewById(R.id.card_zero_total_point);
        card_date_view = view.findViewById(R.id.card_date_view);
        card_total_user = view.findViewById(R.id.card_total_user);
        card_logout = view.findViewById(R.id.card_logout);
        total_record_on_map_layout = view.findViewById(R.id.total_record_on_map_layout);
        verified_record_on_map_layout = view.findViewById(R.id.verified_record_on_map_layout);
        unverified_record_on_map_layout = view.findViewById(R.id.unverified_record_on_map_layout);
        report_card_view_click = view.findViewById(R.id.report_card_view_click);
        update_status_card_view_click = view.findViewById(R.id.update_status_card_view_click);

        initViews();
        
        return view;
    }

    private void initViews() {



        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        fetchDashboardDetails();

        card_for_total_verified_records.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AdminVerifiedRecordScreen.class));
        });

        card_two_unverified_record.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AdminUnVerifiedRecordScreen.class));
        });

        card_zero_total_point.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AdminAllRecordScreen.class));
        });

        card_date_view.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AdminAllRecordAccordingToDateScreen.class));
        });
        card_total_user.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AdminAllUsersScreen.class));
        });

        card_logout.setOnClickListener(view -> {
            Sp.logout(getContext());
            getActivity().finish();
        });



        total_record_on_map_layout.setOnClickListener(v->{
            query="1=1";
            Intent intent= new Intent(getContext(), MapViewScreen.class);
            intent.putExtra("query",query);
            startActivity(intent);
        });
        verified_record_on_map_layout.setOnClickListener(v->{
            query="verified='Y'";
            Intent intent= new Intent(getContext(), MapViewScreen.class);
            intent.putExtra("query",query);
            startActivity(intent);
        });
        unverified_record_on_map_layout.setOnClickListener(v->{
            query="verified='N'";
            Intent intent= new Intent(getContext(), MapViewScreen.class);
            intent.putExtra("query",query);
            startActivity(intent);
        });

        report_card_view_click.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AdminNewPointScreen.class));
        });
        update_status_card_view_click.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AdminReadyToDemolishScreen.class));
        });

    }



    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }


//    private void makeLocationHistory() {
//
//        progressDialog.show();
//        ApiInterface retrofitAPIInterface = RetrofitClient.getRetrofitClient(getContext()).create(ApiInterface.class);
//        Call<ResponseBody> call = retrofitAPIInterface.locationHistory(Sp.read_shared_pref(getContext(),"user_mobile"),"27.05","72.45",getDateTime(),"LOGOUT");
//        Log.d("TAG", "makeLocationHistory: "+Sp.read_shared_pref(getContext(),"user_mobile")+"27.05"+"72.45"+ getDateTime()+"LOGOUT");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    try {
//                        String result = response.body().string();
//                        JSONObject jsonObject = new JSONObject(result);
//                        Log.d("Resp:Location:  ", "" + jsonObject);
//                        boolean status = jsonObject.optBoolean("status");
//                        String message = jsonObject.optString("message");
//                        progressDialog.dismiss();
//                        if (status) {
//                            if (message.equalsIgnoreCase("Sucess")) {
//
//                                Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "SomeThing went wrong", Toast.LENGTH_SHORT).show();
//                            Log.d("TAG", "onResponse: "+message);
//                        }
//
//
//
//                    } catch (Exception e) {
//                        progressDialog.dismiss();
//                        e.printStackTrace();
//                        Log.i("Resp Exc: ", e.getMessage() + "");
//                        onFailed("An unexpected error has occured.",
//                                "Error: " + e.getMessage() + "\n" +
//                                        "Please Try Again later ");
//                    }
//
//
//                } else if (response.code() == 404) {
//                    progressDialog.dismiss();
//                    Log.i("Resp Exc: ", "" + response.code());
//                    onFailed("An unexpected error has occured.",
//                            "Error Code: " + response.code() + "\n" +
//                                    "Please Try Again later ");
//
//
//                } else {
//                    progressDialog.dismiss();
//                    Log.i("Resp Exc: ", "" + response.code());
//                    onFailed("An unexpected error has occured.",
//                            "Error Code: " + response.code() + "\n" +
//                                    "Please Try Again later ");
//
//                }
//
//
//            }
//
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.i("Resp onFailure: ", "" + t.getMessage());
//                progressDialog.dismiss();
//                if (t.getMessage().startsWith("Unable to resolve host")) {
//                    onFailed("Slow or No Connection!",
//                            "Check Your Network Settings & try again.");
//
//
//                } else if (t.getMessage().startsWith("timeout")) {
//                    onFailed("Slow or No Connection!",
//                            "Check Your Network Settings & try again.");
//
//
//                } else {
//                    onFailed("An unexpected error has occured.",
//                            "Error Failure: " + t.getMessage());
//
//
//                }
//            }
//        });
//
//    }
//

    private void fetchDashboardDetails() {
        progress_bar_loading.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(getContext()).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getMainDashboardData("");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.optString("message");
                        boolean status = jsonObject.optBoolean("status");
                        progress_bar_loading.setVisibility(View.GONE);

                        if (message.equalsIgnoreCase("Success")) {
                            if (status) {
                                JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                                JSONObject counttotaldata = jsonObject1.optJSONObject("counttotaldata");
                                totalBuiltUpPoint = counttotaldata.optInt("totaldata");

                                JSONObject counttotalverifield = jsonObject1.optJSONObject("counttotalverifield");

                                totalVerifiedPoint = counttotalverifield.optInt("totaldata");

                                JSONObject counttotalnonverifield = jsonObject1.optJSONObject("counttotalnonverifield");

                                totalUnverifiedPoint = counttotalnonverifield.optInt("totaldata");


                                JSONObject totaluser = jsonObject1.optJSONObject("totaluser");

                                totalUser = totaluser.optInt("totalusercount");


                                updateTickerView();


                            } else {
                                Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
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

    private void updateTickerView() {
        total_builtup_count.setText(String.valueOf(totalBuiltUpPoint));
        total_verified_record.setText(String.valueOf(totalVerifiedPoint));
        total_unverified_record.setText(String.valueOf(totalUnverifiedPoint));
        total_user_count.setText(String.valueOf(totalUser));
    }

    private void onFailed(String s, String s1) {
        Toast.makeText(getContext(), "" + s1, Toast.LENGTH_SHORT).show();
    }
}