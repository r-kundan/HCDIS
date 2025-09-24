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
import com.app.harcdis.adapter.OfflineVerifiedPointAdapter;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.click_interface.ClickInterfaceVerified;
import com.app.harcdis.offline_storage.AppDataBase;
import com.app.harcdis.offline_storage.entites.VerifiedPointRecord;

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

public class VerifiedPointOfflineScreen extends AppCompatActivity implements ClickInterfaceVerified {
    private static final String TAG = "MyTag";
    RecyclerView verified_point_recyclerview;
    ArrayList<VerifiedPointRecord> arrayList = new ArrayList<VerifiedPointRecord>();
    OfflineVerifiedPointAdapter  offlineVerifiedPointAdapter;
    Button upload_data_verified;
    ProgressDialog progressDialog3;
    private int uploaded_counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified_point_offline_screen);
        initViews();
    }

    private void initViews() {
        verified_point_recyclerview = findViewById(R.id.verified_point_recyclerview);
        upload_data_verified = findViewById(R.id.upload_data_verified);
        progressDialog3 = new ProgressDialog(VerifiedPointOfflineScreen.this);
        progressDialog3.setTitle("Uploading Data....");
        progressDialog3.setMessage("Please Wait");
        progressDialog3.setCancelable(false);
        upload_data_verified= findViewById(R.id.upload_data_verified);

        upload_data_verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_uploading_data_for_new();
            }
        });

    }



    private void start_uploading_data_for_new() {
        for(int i =0;i<arrayList.size();i++){
            progressDialog3.show();

            byte[] videoBytes = new byte[0];
            try {
                videoBytes = readVideoFile(arrayList.get(i).getVideo());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Video Not Found", Toast.LENGTH_SHORT).show();
            }
            String base64VideoString = encodeVideoToBase64(videoBytes);


            ApiInterface apiInterface = RetrofitClient.getRetrofitClient(VerifiedPointOfflineScreen.this).create(ApiInterface.class);
            Call<ResponseBody> call = apiInterface.updatePointBase64V2(
                    arrayList.get(i).getOBJECTID(),
                    arrayList.get(i).getYear(),
                    arrayList.get(i).getVerifiedBy(),
                    arrayList.get(i).getVerificationDate(),
                    arrayList.get(i).getUser_name(),
                    arrayList.get(i).getVerified(),
                    arrayList.get(i).getNearByLandMark(),
                    arrayList.get(i).getGisId(),
                    arrayList.get(i).getImage1(),
                    arrayList.get(i).getImage2(),
                    arrayList.get(i).getImage3(),
                    arrayList.get(i).getImage4(),
                    base64VideoString,
                    arrayList.get(i).getAuth_status(),
                    arrayList.get(i).getRemarks(),
                    arrayList.get(i).getFeedback(),
                    arrayList.get(i).getOwner_name()
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

                                Toast.makeText(VerifiedPointOfflineScreen.this, "Data Insert Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(VerifiedPointOfflineScreen.this, "" + message, Toast.LENGTH_SHORT).show();
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
            AppDataBase.getDatabase(getApplicationContext()).verifiedPointDao().deleteAll();
            fetchDatabaseNew();
        });

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


    private void onFailed(String s, String s1) {
        Toast.makeText(this, "" + s1, Toast.LENGTH_SHORT).show();
    }


    public void onResume() {
        super.onResume();
        fetchDatabaseNew();
    }

    private void fetchDatabaseNew() {
        Log.d("TAG", "fetchDatabase: New ");
        arrayList.clear();
        AsyncTask.execute(() -> {
            List<VerifiedPointRecord> verifiedPointRecords = AppDataBase.getDatabase(getApplicationContext()).verifiedPointDao().getAllData();
            Log.d(TAG, "fetchDatabase: ");
            for (VerifiedPointRecord pointRecord : verifiedPointRecords) {
                arrayList.add(
                        new VerifiedPointRecord(
                                pointRecord.getUid(),
                                pointRecord.getOBJECTID(),
                                pointRecord.getYear(),
                                pointRecord.getRemarks(),
                                pointRecord.getVerifiedBy(),
                                pointRecord.getVerificationDate(),
                                pointRecord.getUser_name(),
                                pointRecord.getVerified(),
                                pointRecord.getNearByLandMark(),
                                pointRecord.getFeedback(),
                                pointRecord.getGisId(),
                                pointRecord.getImage1(),
                                pointRecord.getImage2(),
                                pointRecord.getImage3(),
                                pointRecord.getImage4(),
                                pointRecord.getVideo(),
                                pointRecord.getN_d_name(),
                                pointRecord.getN_t_name(),
                                pointRecord.getN_v_name(),
                                pointRecord.getN_murra_no(),
                                pointRecord.getN_khas_no(),
                                pointRecord.getLatitude(),
                                pointRecord.getLongitude(),
                                pointRecord.getAuth_status(),
                                pointRecord.getOwner_name()
                        ));


            }
            runOnUiThread(() -> {
                offlineVerifiedPointAdapter = new OfflineVerifiedPointAdapter(VerifiedPointOfflineScreen.this, arrayList,this::handleClick);
                LinearLayoutManager layoutManager = new LinearLayoutManager(VerifiedPointOfflineScreen.this, RecyclerView.VERTICAL, false);
                verified_point_recyclerview.setLayoutManager(layoutManager);
                verified_point_recyclerview.setAdapter(offlineVerifiedPointAdapter);
                offlineVerifiedPointAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void handleClick(int position) {
        AsyncTask.execute(() -> {
            AppDataBase.getDatabase(getApplicationContext()).verifiedPointDao().delete(arrayList.get(position));
            fetchDatabaseNew();
        });
    }
}