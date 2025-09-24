package com.app.harcdis.point_forward_flow;

import static android.content.ContentValues.TAG;

import static com.app.harcdis.utils.UiHelper.getResizedBitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adminRole.model.AdminCardHolderModel;
import com.app.harcdis.adminRole.model.OffcialMemberModel;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.point_forward_flow.model.ForwardedModel;
import com.app.harcdis.point_forward_flow.model.MasterModel;
import com.app.harcdis.screens.SurveyFormScreen;
import com.app.harcdis.utils.Sp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForwardPointFormScreen extends AppCompatActivity {

    String uid;
    private ProgressDialog progressDialog;
    String forward_id;
    String before_img1;
    String before_img2;
    String after_img1;
    String after_img2;
    private static final int REQUEST_VIDEO_CAPTURE = 1990;
    ImageView before_image_one, before_image_two;
    private int rq_code_camera;
    private Uri imageUri;
    private File imageFile;
    private byte[] imageInByte1;
    private String img_path_1 = "";
    private String image1Base64 = "";
    //second image
    private File imageFile2;
    private byte[] imageInByte2;
    private String img_path_2 = "";
    private String image2Base64 = "";
    //Create by jyoti for maintain record of boundaries
    //third image
    private File imageFile3;
    private byte[] imageInByte3;
    private String img_path_3 = "";
    private String image3Base64 = "";
    //third image
    private File imageFile4;
    private byte[] imageInByte4;
    private String img_path_4 = "";
    private String image4Base64 = "";

    private ArrayList<MasterModel> masterModelArrayList;
    Spinner master_spinner;
    Spinner master_spinner_after_demolish;
    Spinner imp_spinner_two;
    Spinner imp_spinner_one;
    private String selected_code_one;
    private String selected_code_two;
    ImageView after_image_two, after_image_one;
    TextView uid_after;
    TextView uid_before;
    ArrayList<String> demoliationImplementArrayList;
    CardView after_card_layout, before_card_layout;

    Button before_submit_btn;
    Button after_submit_btn;
    private String status_spinner_value_one;
    private String status_spinner_value_two;
    EditText remarks_edit_text;
    EditText remarks_edit_text_after_demolish;
    private Call<ResponseBody> call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_point_form_screen);
        initViewsAndIntent();

    }

    private void initViewsAndIntent() {
        progressDialog = new ProgressDialog(ForwardPointFormScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);


        before_image_one = findViewById(R.id.before_image_one);
        before_image_two = findViewById(R.id.before_image_two);
        after_image_one = findViewById(R.id.after_image_one);
        after_image_two = findViewById(R.id.after_image_two);
        master_spinner = findViewById(R.id.master_spinner);
        uid_before = findViewById(R.id.uid_before);
        uid_after = findViewById(R.id.uid_after);
        imp_spinner_one = findViewById(R.id.imp_spinner_one);
        imp_spinner_two = findViewById(R.id.imp_spinner_two);
        after_card_layout = findViewById(R.id.after_card_layout);
        before_card_layout = findViewById(R.id.before_card_layout);
        before_submit_btn = findViewById(R.id.before_submit_btn);
        remarks_edit_text = findViewById(R.id.remarks_edit_text);
        master_spinner_after_demolish = findViewById(R.id.master_spinner_after_demolish);
        remarks_edit_text_after_demolish = findViewById(R.id.remarks_edit_text_after_demolish);
        after_submit_btn = findViewById(R.id.after_submit_btn);
        masterModelArrayList = new ArrayList<>();
        demoliationImplementArrayList = new ArrayList<>();
        demoliationImplementArrayList.add("Select Demolition Status");
        demoliationImplementArrayList.add("Implemented");
        demoliationImplementArrayList.add("Not Implemented");
        Intent intent = getIntent();
        if (intent != null) {
            uid = intent.getStringExtra("uid_key");
            fetchUidData();
            getMasterSpinnerData();
            uid_before.setText("UID - " + uid);
            uid_before.setText("UID - " + uid);
        }


        before_submit_btn.setOnClickListener(view -> {
            if (image1Base64.isEmpty()) {
                Toast.makeText(this, "Capture Before First Image", Toast.LENGTH_SHORT).show();
            } else if (image2Base64.isEmpty()) {
                Toast.makeText(this, "Capture Before 2nd Image", Toast.LENGTH_SHORT).show();
            } else if (selected_code_one.isEmpty()) {
                Toast.makeText(this, "Select Work Status", Toast.LENGTH_SHORT).show();
            } else if (status_spinner_value_one.isEmpty()) {
                Toast.makeText(this, "Select Demolition Status", Toast.LENGTH_SHORT).show();
            } else {
                hit_api_save_before_data("BEFORE");
            }
        });

        after_submit_btn.setOnClickListener(view -> {
            if (image1Base64.isEmpty()) {
                Toast.makeText(this, "Capture Before First Image", Toast.LENGTH_SHORT).show();
            } else if (image2Base64.isEmpty()) {
                Toast.makeText(this, "Capture Before 2nd Image", Toast.LENGTH_SHORT).show();
            } else if (selected_code_one.isEmpty()) {
                Toast.makeText(this, "Select Work Status", Toast.LENGTH_SHORT).show();
            } else if (status_spinner_value_one.isEmpty()) {
                Toast.makeText(this, "Select Demolition Status", Toast.LENGTH_SHORT).show();
            } else {
                hit_api_save_before_data("AFTER");
            }
        });


        before_image_one.setOnClickListener(view -> {
            rq_code_camera = 1;
            if (Build.VERSION.SDK_INT >= 23) {
                AllowPermissions();
            } else {
                camera_intent(rq_code_camera);
            }
        });


        before_image_two.setOnClickListener(view -> {
            rq_code_camera = 2;
            if (Build.VERSION.SDK_INT >= 23) {
                AllowPermissions();
            } else {
                camera_intent(rq_code_camera);
            }

        });

        after_image_one.setOnClickListener(view -> {
            rq_code_camera = 3;
            if (Build.VERSION.SDK_INT >= 23) {
                AllowPermissions();
            } else {
                camera_intent(rq_code_camera);
            }

        });

        after_image_two.setOnClickListener(view -> {
            rq_code_camera = 4;
            if (Build.VERSION.SDK_INT >= 23) {
                AllowPermissions();
            } else {
                camera_intent(rq_code_camera);
            }

        });


        master_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MasterModel offcialMemberModel = (MasterModel) adapterView.getItemAtPosition(i);
                selected_code_one = offcialMemberModel.getStatus_code();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(ForwardPointFormScreen.this, "", Toast.LENGTH_SHORT).show();
            }
        });

        master_spinner_after_demolish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MasterModel offcialMemberModel = (MasterModel) adapterView.getItemAtPosition(i);
                selected_code_two = offcialMemberModel.getStatus_code();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(ForwardPointFormScreen.this, "", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(ForwardPointFormScreen.this, android.R.layout.simple_spinner_item, demoliationImplementArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imp_spinner_one.setAdapter(adapter);
        imp_spinner_two.setAdapter(adapter);


        imp_spinner_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(ForwardPointFormScreen.this, "Select Demolition Status", Toast.LENGTH_SHORT).show();
                } else {
                    status_spinner_value_one = demoliationImplementArrayList.get(i);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(ForwardPointFormScreen.this, "", Toast.LENGTH_SHORT).show();
            }
        });

        imp_spinner_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(ForwardPointFormScreen.this, "Select Demolition Status", Toast.LENGTH_SHORT).show();
                } else {
                    status_spinner_value_two = demoliationImplementArrayList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(ForwardPointFormScreen.this, "", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void hit_api_save_before_data(String type) {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);


        if (type.equals("AFTER")) {
            call = apiInterface.updateStausToDemolish(
                    uid,
                    Sp.read_shared_pref(ForwardPointFormScreen.this, "login_with"),
                    "AFTER",
                    selected_code_two,
                    "",
                    status_spinner_value_two,
                    Sp.read_shared_pref(ForwardPointFormScreen.this, "user_name"),
                    image3Base64,
                    image4Base64, remarks_edit_text.getText().toString()
            );


        } else if (type.equals("BEFORE")) {
            call = apiInterface.updateStausToDemolish(
                    uid,
                    Sp.read_shared_pref(ForwardPointFormScreen.this, "login_with"),
                    "BEFORE",
                    selected_code_one,
                    "",
                    status_spinner_value_one,
                    Sp.read_shared_pref(ForwardPointFormScreen.this, "user_name"),
                    image1Base64,
                    image2Base64, remarks_edit_text_after_demolish.getText().toString()
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
                        if (message.equalsIgnoreCase("Status Updated")) {
                            if (status) {
                                Toast.makeText(ForwardPointFormScreen.this, "Data Update Successfully", Toast.LENGTH_SHORT).show();
                                fetchUidData();
                            } else {
                                Toast.makeText(ForwardPointFormScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ForwardPointFormScreen.this, message, Toast.LENGTH_SHORT).show();

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


    private void getMasterSpinnerData() {
        masterModelArrayList.clear();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getStatusMaster();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");
                        if (message.equalsIgnoreCase("Demolish Status")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    masterModelArrayList.add(new MasterModel(object.optString("status_code"), object.optString("status_name")));
                                }

                                ArrayAdapter adapter = new ArrayAdapter(ForwardPointFormScreen.this, android.R.layout.simple_spinner_item, masterModelArrayList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                master_spinner.setAdapter(adapter);
                                master_spinner_after_demolish.setAdapter(adapter);


                            } else {
                                Toast.makeText(ForwardPointFormScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ForwardPointFormScreen.this, message, Toast.LENGTH_SHORT).show();

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


    private void camera_intent(int rq_code_camera) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, rq_code_camera);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 101) {

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (BuildConfig.DEBUG) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    }
                    camera_intent(rq_code_camera);
                    return;
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (BuildConfig.DEBUG) {
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_CAPTURE) {

            } else {
                try {
                    Uri photoUri = imageUri;
                    if (photoUri != null) {
                        try {
                            InputStream imageStream = null;
                            try {
                                imageStream = getContentResolver().openInputStream(photoUri);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();

                            Bitmap convertedImage = getResizedBitmap(bmp, 500);
                            convertedImage.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(Sp.read_shared_pref(ForwardPointFormScreen.this, "image_quality")), stream);

                            Log.d(TAG, "onActivityResult: " + requestCode);
                            if (requestCode == 1) {
                                before_image_one.setImageBitmap(convertedImage);
                                imageFile = new File(getRealPathFromURI(photoUri));
                                img_path_1 = getRealPathFromURI(photoUri);
                                try {
                                    imageInByte1 = stream.toByteArray();
                                    image1Base64 = getEncoded64ImageStringFromBitmap(convertedImage);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (requestCode == 2) {
                                before_image_two.setImageBitmap(convertedImage);
                                imageFile2 = new File(getRealPathFromURI(photoUri));
                                img_path_2 = getRealPathFromURI(photoUri);
                                try {
                                    imageInByte2 = stream.toByteArray();
                                    image2Base64 = getEncoded64ImageStringFromBitmap(convertedImage);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (requestCode == 3) {
                                after_image_one.setImageBitmap(convertedImage);
                                imageFile3 = new File(getRealPathFromURI(photoUri));
                                img_path_3 = getRealPathFromURI(photoUri);

                                try {
                                    imageInByte3 = stream.toByteArray();
                                    image3Base64 = getEncoded64ImageStringFromBitmap(convertedImage);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (requestCode == 4) {
                                after_image_two.setImageBitmap(convertedImage);
                                imageFile4 = new File(getRealPathFromURI(photoUri));
                                img_path_4 = getRealPathFromURI(photoUri);
                                try {
                                    imageInByte4 = stream.toByteArray();
                                    image4Base64 = getEncoded64ImageStringFromBitmap(convertedImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_reading_image_file), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.no_image_selected) + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), getString(R.string.request_cancelled), Toast.LENGTH_SHORT).show();
        } else {
            getContentResolver().delete(imageUri, null, null);
            Toast.makeText(getApplicationContext(), getString(R.string.data_not_getting_for_that_point), Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri photoUri) {
        String result;
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(photoUri, projection, null, null, null);
        if (cursor == null) {
            result = photoUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(projection[0]);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        Log.d(TAG, "getEncoded64ImageStringFromBitmap: ");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(Sp.read_shared_pref(ForwardPointFormScreen.this, "image_quality")), stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
    }

    private void AllowPermissions() {

        String readImagePermission2, readAudioPermission2, readVideoPermission2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readImagePermission2 = Manifest.permission.READ_MEDIA_IMAGES;
            readAudioPermission2 = Manifest.permission.READ_MEDIA_AUDIO;
            readVideoPermission2 = Manifest.permission.READ_MEDIA_VIDEO;
        } else {
            readImagePermission2 = Manifest.permission.READ_EXTERNAL_STORAGE;
            readAudioPermission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            readVideoPermission2 = Manifest.permission.READ_EXTERNAL_STORAGE;
        }


        int hasCAMERAPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int hasREAD_IMAGE_STORAGEPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), readImagePermission2);
        int hasREAD_AUDIO_STORAGEPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), readAudioPermission2);
        int hasREAD_VIDEO_STORAGEPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), readVideoPermission2);
        int hasACCESS_FINE_LOCATION = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasACCESS_COARSE_LOCATION = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> permissions = new ArrayList<String>();
        if (hasCAMERAPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (hasREAD_IMAGE_STORAGEPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(readImagePermission2);
        }
        if (hasREAD_AUDIO_STORAGEPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(readAudioPermission2);
        }
        if (hasREAD_VIDEO_STORAGEPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(readVideoPermission2);
        }
        if (hasACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (hasACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 101);
        } else {
            camera_intent(rq_code_camera);
        }
    }


    private void fetchUidData() {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getBeforeDemolishData(Sp.read_shared_pref(ForwardPointFormScreen.this, "user_name"), "GCD/GN/3063", Sp.read_shared_pref(ForwardPointFormScreen.this, "login_with"));
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
                        if (message.equalsIgnoreCase("Before Demolish Data")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                if (result_array.length() > 0) {
                                    before_card_layout.setVisibility(View.VISIBLE);
                                    after_card_layout.setVisibility(View.VISIBLE);
                                    JSONObject jsonObject1 = result_array.optJSONObject(0);
                                    before_img1 = jsonObject1.optString("before_img1");
                                    before_img2 = jsonObject1.optString("before_img2");
                                    after_img1 = jsonObject1.optString("after_img1");
                                    after_img2 = jsonObject1.optString("after_img2");
                                    forward_id = jsonObject1.optString("forward_id");
                                    Log.d(TAG, "onResponse: jsonObject1" + jsonObject1);
                                    if (!Objects.equals("null", before_img1)) {
                                        byte[] decodedString = Base64.decode(before_img1, Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        before_image_one.setImageBitmap(decodedByte);

                                    }
                                    if (!Objects.equals("null", before_img2)) {
                                        byte[] decodedString2 = Base64.decode(before_img1, Base64.DEFAULT);
                                        Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
                                        before_image_two.setImageBitmap(decodedByte2);
                                    }
                                    if (!Objects.equals("null", after_img1)) {
                                        byte[] decodedString2 = Base64.decode(after_img1, Base64.DEFAULT);
                                        Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
                                        after_image_one.setImageBitmap(decodedByte2);
                                    }
                                    if (!Objects.equals("null", after_img2)) {
                                        byte[] decodedString2 = Base64.decode(after_img2, Base64.DEFAULT);
                                        Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
                                        after_image_two.setImageBitmap(decodedByte2);
                                    }

                                } else {
                                    Toast.makeText(ForwardPointFormScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                before_card_layout.setVisibility(View.VISIBLE);
                                after_card_layout.setVisibility(View.GONE);
                                Toast.makeText(ForwardPointFormScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            before_card_layout.setVisibility(View.VISIBLE);
                            after_card_layout.setVisibility(View.GONE);
                            Toast.makeText(ForwardPointFormScreen.this, message, Toast.LENGTH_SHORT).show();

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
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();
    }
}