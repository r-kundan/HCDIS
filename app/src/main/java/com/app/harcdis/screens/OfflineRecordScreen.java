package com.app.harcdis.screens;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.R;
import com.app.harcdis.adapter.OfflineNewPointAdapter;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.click_interface.ClickInterfaceNew;
import com.app.harcdis.offline_storage.AppDataBase;
import com.app.harcdis.offline_storage.entites.NewPointRecord;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfflineRecordScreen extends AppCompatActivity implements ClickInterfaceNew {
    private static final String TAG = "MyTag";
    RecyclerView new_offline_point_recycler_view;
    ArrayList<NewPointRecord> arrayList = new ArrayList<NewPointRecord>();
    OfflineNewPointAdapter newPointAdapter;
    ProgressDialog progressDialog3;
    Button upload_data_offline;
    private int uploaded_counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_record_screen2);
        new_offline_point_recycler_view = findViewById(R.id.new_offline_point_recycler_view);
        upload_data_offline = findViewById(R.id.upload_data_offline);
        progressDialog3 = new ProgressDialog(OfflineRecordScreen.this);
        progressDialog3.setTitle("Uploading Data....");
        progressDialog3.setMessage("Please Wait");
        progressDialog3.setCancelable(false);
        upload_data_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayList.size() != 0) {
                    start_uploading_data_for_new();
                } else {
                    Toast.makeText(OfflineRecordScreen.this, "No Offline Data Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        fetchDatabaseNew();
    }


    private void start_uploading_data_for_new() {
        for (int i = 0; i < arrayList.size(); i++) {
            progressDialog3.show();
            byte[] videoBytes = new byte[0];
            try {
                videoBytes = readVideoFile(arrayList.get(i).getVideo());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Video Not Found", Toast.LENGTH_SHORT).show();
            }
            String base64VideoString = encodeVideoToBase64(videoBytes);
            ApiInterface apiInterface = RetrofitClient.getRetrofitClient(OfflineRecordScreen.this).create(ApiInterface.class);
            Call<ResponseBody> call = apiInterface.addNewPointWithBase64V2(
                    arrayList.get(i).getLatitude(),
                    arrayList.get(i).getLongitude(),
                    arrayList.get(i).getCa_name(),
                    arrayList.get(i).getDev_plan(),
                    arrayList.get(i).getN_d_code(),
                    arrayList.get(i).getN_d_name(),
                    arrayList.get(i).getN_t_code(),
                    arrayList.get(i).getN_t_name(),
                    arrayList.get(i).getN_v_code(),
                    arrayList.get(i).getN_v_name(),
                    arrayList.get(i).getN_murr_no(),
                    arrayList.get(i).getN_khas_no(),
                    arrayList.get(i).getYear(),
                    arrayList.get(i).getVerifiedBy(),
                    arrayList.get(i).getUser_name(),
                    arrayList.get(i).getVerified(),
                    arrayList.get(i).getNearByLandMark(),
                    arrayList.get(i).getPointSource(),
                    arrayList.get(i).getCA_Key_GIS(),
                    arrayList.get(i).getAOI_DP(),
                    arrayList.get(i).getAOI_UA(),
                    arrayList.get(i).getAOI_CA(),
                    arrayList.get(i).getImage1(),
                    arrayList.get(i).getImage2(),
                    arrayList.get(i).getImage3(),
                    arrayList.get(i).getImage4(),
                    base64VideoString,
                    arrayList.get(i).getAuth_status(),
                    arrayList.get(i).getFeedback(),
                    arrayList.get(i).getRemarks(),
                    arrayList.get(i).getOwner_name(),
                    arrayList.get(i).getUAKey()
            );
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            progressDialog3.dismiss();
                            String result = response.body().string();
                            JSONObject obj = new JSONObject(result);
                            Log.d("Resp: ", "" + obj);
                            String message = obj.getString("message");
                            boolean status = obj.optBoolean("status");
                            if (status) {
                                uploaded_counter++;
                                if (arrayList.size() == uploaded_counter) {
                                    Log.d(TAG, "onResponse: data inserted");
                                    removeDataFromDB(uploaded_counter);
                                }
                                Toast.makeText(OfflineRecordScreen.this, "Data Insert Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OfflineRecordScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (

                                Exception e) {
                            e.printStackTrace();
                            progressDialog3.dismiss();
                            Log.i("Resp Exc: ", e.getMessage() + "");
                            onFailed("An unexpected error has occured.",
                                    "Error: " + e.getMessage() + "\n" +
                                            "Please Try Again later ");

                        }
                    } else if (response.code() == 401) {
                        progressDialog3.dismiss();
                        Log.i("Resp Exc: ", "" + response.code());
                        onFailed("An unexpected error has occured.",
                                "Error Code: " + response.code() + "\n" +
                                        "Please Try Again later ");


                    } else {
                        progressDialog3.dismiss();
                        Log.i("Resp Exc: ", "" + response.code());
                        onFailed("An unexpected error has occured.",
                                "Error Code: " + response.code() + "\n" +
                                        "Please Try Again later ");

                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i("Resp onFailure: ", "" + t.getMessage());
                    progressDialog3.dismiss();
                    if (t.getMessage().startsWith("Unable to resolve host")) {

                        onFailed("Slow or No Connection!",
                                "Check Your Network Settings & try again.");


                    } else if (t.getMessage().startsWith("timeout")) {

                        onFailed("Slow or No Connection!",
                                "Check Your Network Settings & try again.");


                    } else {

                        onFailed("An unexpected error has occured.",
                                "Error Failure: " + t.getMessage());


                    }
                }
            });
        }
    }

    private void removeDataFromDB(int uploaded_counter) {
        Log.d(TAG, "removeDataFromDB: ");
        AsyncTask.execute(() -> {
            AppDataBase.getDatabase(getApplicationContext()).newPointRecordDao().deleteAll();
            fetchDatabaseNew();
        });

    }


    private void onFailed(String s, String s1) {
        Toast.makeText(this, "" + s1, Toast.LENGTH_SHORT).show();
    }


    public byte[] readVideoFile(String filePath) throws IOException {
        File videoFile = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(videoFile);
        byte[] videoBytes = new byte[(int) videoFile.length()];
        fileInputStream.read(videoBytes);
        fileInputStream.close();
        return videoBytes;
    }


    public String encodeVideoToBase64(byte[] videoBytes) {
        return Base64.encodeToString(videoBytes, Base64.DEFAULT);
    }


    private void fetchDatabaseNew() {
        Log.d("TAG", "fetchDatabase: New ");
        arrayList.clear();
        AsyncTask.execute(() -> {
            List<NewPointRecord> newPointRecordList = AppDataBase.getDatabase(getApplicationContext()).newPointRecordDao().getAllData();
            Log.d(TAG, "fetchDatabase: ");
            for (NewPointRecord pointRecord : newPointRecordList) {
                arrayList.add(
                        new NewPointRecord(
                                pointRecord.uid,
                                pointRecord.latitude,
                                pointRecord.longitude,
                                pointRecord.ca_name,
                                pointRecord.dev_plan,
                                pointRecord.n_d_code,
                                pointRecord.n_d_name,
                                pointRecord.n_t_code,
                                pointRecord.n_t_name,
                                pointRecord.n_v_code,
                                pointRecord.n_v_name,
                                pointRecord.n_murr_no,
                                pointRecord.n_khas_no,
                                pointRecord.year,
                                pointRecord.remarks,
                                pointRecord.verifiedBy,
                                pointRecord.user_name,
                                pointRecord.verified,
                                pointRecord.nearByLandMark,
                                pointRecord.feedback,
                                pointRecord.pointSource,
                                pointRecord.CA_Key_GIS,
                                pointRecord.AOI_DP,
                                pointRecord.AOI_UA,
                                pointRecord.AOI_CA,
                                pointRecord.image1,
                                pointRecord.image2,
                                pointRecord.image3,
                                pointRecord.image4,
                                pointRecord.video,
                                pointRecord.auth_status,
                                pointRecord.owner_name,
                                pointRecord.UAKey
                        ));
                Log.d(TAG, "fetchDatabaseNew: " + pointRecord.getVideo());

            }
            runOnUiThread(() -> {
                newPointAdapter = new OfflineNewPointAdapter(OfflineRecordScreen.this, arrayList, this::onClickHandle);
                LinearLayoutManager layoutManager = new LinearLayoutManager(OfflineRecordScreen.this, RecyclerView.VERTICAL, false);
                new_offline_point_recycler_view.setLayoutManager(layoutManager);
                new_offline_point_recycler_view.setAdapter(newPointAdapter);
                newPointAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onClickHandle(int position) {
        AsyncTask.execute(() -> {
            AppDataBase.getDatabase(getApplicationContext()).newPointRecordDao().delete(arrayList.get(position));
            fetchDatabaseNew();
        });
    }
}