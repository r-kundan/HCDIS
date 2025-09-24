package com.app.harcdis.screens;

import static android.content.ContentValues.TAG;
import static com.app.harcdis.screens.MapPointSelectionScreen.sketchEditor;
import static com.app.harcdis.screens.MapPointSelectionScreen.sketchGeometry;
import static com.app.harcdis.utils.UiHelper.getResizedBitmap;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.model.CameraImageModel;
import com.app.harcdis.offline_storage.AppDataBase;
import com.app.harcdis.offline_storage.entites.NewPointRecord;
import com.app.harcdis.offline_storage.entites.VerifiedPointRecord;
import com.app.harcdis.utils.Connection_Detector;
import com.app.harcdis.utils.Sp;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.google.android.gms.common.images.Size;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyFormScreen extends AppCompatActivity {
    private static final int REQUEST_VIDEO_CAPTURE = 1990;
    public static ArrayList<CameraImageModel> camera_image_array_list;
    Button update_record_btn, add_record_btn;
    String selectedDisName, selectedTehsilName, selectedVillageName, murabba_khasra_no, objectID, UAKey;
    String gisId, uid;
    TextView survey_uid, history_n_d_name, survey_n_t_name;
    TextView survey_n_v_name, survey_khasra_murba_number;
    ImageView image_one, image_two, image_three, image_four;
    String videoPath = "";
    EditText remarks_edit_text;
    String user_id, user_name;
    TextView select_feedback_spinner;
    ArrayList<String> feedbackList;
    ArrayList<String> feedbackId;
    EditText landmarks_edit_text;
    String latitude, longitude;
    String ca_name = "", dev_plan = "", ca_key = "", n_d_code, n_t_code, n_v_code, n_murr_no, n_khas_no;
    String urban_area_code_bnd = "";
    String code_bnd_dev_plan = "";
    String code_bnd_controlled_area = "";
    Spinner floorSpinner, structureType, landType;
    EditText verifiedArea;
    LinearLayout floorLayout;
    String boundary;
    FeatureLayer polygonServiceFeatureLayer, historyPolygonServiceFeatureLayer;
    int year;
    String point;
    Button save_new_point_offline;
    Button verify_point_offline;
    LinearLayout update_point_ll_layout;
    LinearLayout add_new_point_ll_layout;
    String base64VideoString = "";
    RadioGroup auth_unauth_radio_group;
    RadioButton auth_status_new;
    RadioButton unauth_status_new;
    private int rq_code_camera;
    private Uri imageUri;
    private VideoView video_view;
    //first image
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
    private ProgressDialog progressDialog3;
    private String feedback_value = "";
    private EditText feedbackOthersEditText;
    private ServiceFeatureTable polygonServiceFeatureTable, historyPolygonServiceFeatureTable;
    private Feature polygonFeature, historyPolygonFeature;
    private ArcGISFeature polygonSelectedFeature, historyPolygonSelectedFeature;
    private ArrayList<String> floorValueList, floorIdList;
    private ArrayList<String> structureValueList, structureIdList;
    private ArrayList<String> landValueList, landIdList;
    private String floor_value, floor_id;
    private String structure_value, structure_id;
    //Offline Module for saving new point
    private String land_value, land_id;
    private Connection_Detector connection_detector;
    private RadioButton selected_auth_status_by_radio_btn;
    private TextView select_reason_auth_unauth_tv;
    private String send_auth_status = "-";
    private LinearLayout auth_unauth_reason_ll_layout;

    private EditText owner_name_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_form_screen);

        connection_detector = new Connection_Detector(getApplicationContext());
        polygonServiceFeatureTable = new ServiceFeatureTable(Sp.read_shared_pref(this, "polygon_feature_service"));
        historyPolygonServiceFeatureTable = new ServiceFeatureTable(Sp.read_shared_pref(this, "polygon_service_history"));

        polygonServiceFeatureLayer = new FeatureLayer(polygonServiceFeatureTable);
        historyPolygonServiceFeatureLayer = new FeatureLayer(historyPolygonServiceFeatureTable);

        camera_image_array_list = new ArrayList<>();

        save_new_point_offline = findViewById(R.id.save_new_point_offline);
        verify_point_offline = findViewById(R.id.verify_point_offline);
        verifiedArea = findViewById(R.id.verifiedArea);
        floorSpinner = findViewById(R.id.floorSpinner);
        structureType = findViewById(R.id.structureType);
        landType = findViewById(R.id.landType);
        floorLayout = findViewById(R.id.floorLayout);
        update_record_btn = findViewById(R.id.update_record_btn);
        add_record_btn = findViewById(R.id.add_record_btn);
        update_point_ll_layout = findViewById(R.id.update_point_ll_layout);
        add_new_point_ll_layout = findViewById(R.id.add_new_point_ll_layout);
        unauth_status_new = findViewById(R.id.unauth_status_new);
        auth_status_new = findViewById(R.id.auth_status_new);
        auth_unauth_radio_group = findViewById(R.id.auth_unauth_radio_group);
        select_reason_auth_unauth_tv = findViewById(R.id.select_reason_auth_unauth_tv);
        auth_unauth_reason_ll_layout = findViewById(R.id.auth_unauth_reason_ll_layout);
        owner_name_edit_text = findViewById(R.id.owner_name_edit_text);


        Intent intent = getIntent();

        if (intent != null) {

            point = intent.getStringExtra("point");
            if (point.equalsIgnoreCase("newPoint")) {
                //  survey_uid.setVisibility(View.GONE);
                update_point_ll_layout.setVisibility(View.GONE);
                add_new_point_ll_layout.setVisibility(View.VISIBLE);


                boundary = intent.getStringExtra("boundary");
                if (boundary.equalsIgnoreCase("newBoundary")) {
                    save_new_point_offline.setVisibility(View.GONE);
                    floorLayout.setVisibility(View.VISIBLE);
                } else {
                    floorLayout.setVisibility(View.GONE);
                    save_new_point_offline.setVisibility(View.VISIBLE);
                }

                latitude = intent.getStringExtra("latitude");
                longitude = intent.getStringExtra("longitude");

                ca_name = intent.getStringExtra("ca_name");
                dev_plan = intent.getStringExtra("dev_plan");
                ca_key = intent.getStringExtra("ca_key");
                n_d_code = intent.getStringExtra("n_d_code");
                n_t_code = intent.getStringExtra("n_t_code");
                n_v_code = intent.getStringExtra("n_v_code");
                n_murr_no = intent.getStringExtra("n_murr_no");
                n_khas_no = intent.getStringExtra("n_khas_no");
                code_bnd_controlled_area = intent.getStringExtra("code_bnd_controlled_area");
                code_bnd_dev_plan = intent.getStringExtra("code_bnd_dev_plan");
                urban_area_code_bnd = intent.getStringExtra("urban_area_code_bnd");
                Log.d(TAG, "addDataToLayer: " + code_bnd_controlled_area);
                Log.d(TAG, "addDataToLayer: " + code_bnd_dev_plan);
                Log.d(TAG, "addDataToLayer: " + urban_area_code_bnd);
                selectedDisName = intent.getStringExtra("n_d_name");
                selectedTehsilName = intent.getStringExtra("n_t_name");
                selectedVillageName = intent.getStringExtra("n_v_name");
                murabba_khasra_no = intent.getStringExtra("murabba_khasra_no");
                UAKey = intent.getStringExtra("UAKey");

                verifiedArea.setText(intent.getStringExtra("polygon_area"));

            } else if (point.equalsIgnoreCase("editPoint")) {
                //  survey_uid.setVisibility(View.VISIBLE);
                update_point_ll_layout.setVisibility(View.VISIBLE);
                add_new_point_ll_layout.setVisibility(View.GONE);

                floorLayout.setVisibility(View.GONE);
                boundary = intent.getStringExtra("boundary");
                if (boundary.equalsIgnoreCase("newBoundary")) {
                    floorLayout.setVisibility(View.VISIBLE);
                    verify_point_offline.setVisibility(View.GONE);
                } else {
                    floorLayout.setVisibility(View.GONE);
                    verify_point_offline.setVisibility(View.VISIBLE);

                }

                latitude = intent.getStringExtra("latitude");
                longitude = intent.getStringExtra("longitude");

                ca_name = intent.getStringExtra("ca_name");
                dev_plan = intent.getStringExtra("dev_plan");
                ca_key = intent.getStringExtra("ca_key");


                n_d_code = intent.getStringExtra("n_d_code");
                n_t_code = intent.getStringExtra("n_t_code");
                n_v_code = intent.getStringExtra("n_v_code");
                n_murr_no = intent.getStringExtra("n_murr_no");
                n_khas_no = intent.getStringExtra("n_khas_no");

                selectedDisName = intent.getStringExtra("n_d_name");
                selectedTehsilName = intent.getStringExtra("n_t_name");
                selectedVillageName = intent.getStringExtra("n_v_name");
                objectID = intent.getStringExtra("objectId");
                murabba_khasra_no = intent.getStringExtra("murabba_khasra_no");
                gisId = intent.getStringExtra("gisId");
                uid = intent.getStringExtra("uid");
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "onCreate: " + uid);
                verifiedArea.setText(intent.getStringExtra("polygon_area"));

            }

            initViews();
        }

        user_id = Sp.read_shared_pref(SurveyFormScreen.this, "user_mobile");
        user_name = Sp.read_shared_pref(SurveyFormScreen.this, "user_name");

    }

    private void initViews() {

        Date d = new Date();
        year = d.getYear();

        progressDialog3 = new ProgressDialog(SurveyFormScreen.this);
        progressDialog3.setTitle(getString(R.string.please_wait));
        progressDialog3.setCancelable(false);
        progressDialog3.show();


        //  survey_uid = findViewById(R.id.survey_uid);
        history_n_d_name = findViewById(R.id.survey_n_d_name);
        landmarks_edit_text = findViewById(R.id.landmarks_edit_text);
        survey_n_t_name = findViewById(R.id.survey_n_t_name);
        survey_n_v_name = findViewById(R.id.survey_n_v_name);
        survey_khasra_murba_number = findViewById(R.id.survey_khasra_murba_number);

        remarks_edit_text = findViewById(R.id.remarks_edit_text);
        image_one = findViewById(R.id.image_one);
        image_two = findViewById(R.id.image_two);
        image_three = findViewById(R.id.image_three);
        image_four = findViewById(R.id.image_four);
        select_feedback_spinner = findViewById(R.id.select_feedback_spinner);
        feedbackOthersEditText = findViewById(R.id.feedbackOthersEditText);
        video_view = findViewById(R.id.video_view);


        //   survey_uid.setText("TCP ID: "+uid);
        history_n_d_name.setText(getString(R.string.district_) + selectedDisName);
        survey_n_t_name.setText(getString(R.string.tehsil_) + selectedTehsilName);
        survey_n_v_name.setText(getString(R.string.village_) + selectedVillageName);
        survey_khasra_murba_number.setText(getString(R.string.murabba_khasra_) + murabba_khasra_no);


        floorValueList = new ArrayList<>();
        floorIdList = new ArrayList<>();

        structureValueList = new ArrayList<>();
        structureIdList = new ArrayList<>();

        landValueList = new ArrayList<>();
        landIdList = new ArrayList<>();

        feedbackList = new ArrayList<>();
        feedbackId = new ArrayList<>();

        getFloorFromDatabase();
        getStructureFromDatabase();
        getLandFromDatabase();


        auth_unauth_radio_group.clearCheck();
        auth_unauth_radio_group.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    selected_auth_status_by_radio_btn = (RadioButton) group.findViewById(checkedId);
                    switch (checkedId) {
                        case R.id.auth_status_new:
                            select_reason_auth_unauth_tv.setText("Select Authorize Reason");
                            auth_unauth_reason_ll_layout.setVisibility(View.VISIBLE);
                            send_auth_status = "01";
                            getReportFromApi(send_auth_status);
                            break;
                        case R.id.unauth_status_new:
                            select_reason_auth_unauth_tv.setText("Select UnAuthorize Reason");
                            auth_unauth_reason_ll_layout.setVisibility(View.VISIBLE);
                            send_auth_status = "02";
                            getReportFromApi(send_auth_status);
                            break;

                        // Add more cases for additional radio buttons
                    }
                });


        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                floor_value = floorValueList.get(position);
                floor_id = floorIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        structureType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                structure_value = structureValueList.get(position);
                structure_id = structureIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        landType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                land_value = landValueList.get(position);
                land_id = landIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        image_one.setOnClickListener(view -> {
            rq_code_camera = 1;
            Log.d(TAG, "initViews: " + Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT >= 23) {
                AllowPermissions();
            } else {
                camera_intent(rq_code_camera);
            }
        });

        image_two.setOnClickListener(view -> {
            rq_code_camera = 2;
            if (Build.VERSION.SDK_INT >= 23) {
                AllowPermissions();
            } else {
                camera_intent(rq_code_camera);
            }
        });

        image_three.setOnClickListener(view -> {
            rq_code_camera = 3;
            if (Build.VERSION.SDK_INT >= 23) {
                AllowPermissions();
            } else {
                camera_intent(rq_code_camera);
            }
        });

        image_four.setOnClickListener(view -> {
            rq_code_camera = 4;
            if (Build.VERSION.SDK_INT >= 23) {
                AllowPermissions();
            } else {
                camera_intent(rq_code_camera);
            }
        });

        video_view.setOnClickListener(view -> {


            String readImagePermission2, readAudioPermission2, readVideoPermission2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                readImagePermission2 = Manifest.permission.READ_MEDIA_IMAGES;
                readAudioPermission2 = Manifest.permission.READ_MEDIA_AUDIO;
                readVideoPermission2 = Manifest.permission.READ_MEDIA_VIDEO;
            } else {
                readImagePermission2 = Manifest.permission.READ_EXTERNAL_STORAGE;
                readAudioPermission2 = Manifest.permission.READ_EXTERNAL_STORAGE;
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
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Size videoSize = new Size(Integer.parseInt(Sp.read_shared_pref(SurveyFormScreen.this, "video_width")), Integer.parseInt(Sp.read_shared_pref(SurveyFormScreen.this, "video_height")));
                // Specify the video quality (optional)
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                // 1 for high quality, 0 for low quality

                // Set the video size using the Intent's extra parameters
                takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, videoSize.getWidth() * videoSize.getHeight());
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10); //15Seconds
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }

        });

        verify_point_offline.setOnClickListener(view -> {
            if (boundary.equalsIgnoreCase("newBoundary")) {
                Toast.makeText(this, getResources().getString(R.string.you_can_not_save_boundary_details_offline), Toast.LENGTH_SHORT).show();
            } else {
                conditionsForUpdateData("offline");
            }
        });
        update_record_btn.setOnClickListener(view -> {
            if (!connection_detector.isConnected()) {
                Toast.makeText(this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
            } else {
                if (boundary.equalsIgnoreCase("newBoundary")) {
                    if (floorSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {
                        Toast.makeText(this, getString(R.string.floors_details), Toast.LENGTH_SHORT).show();
                    } else if (structureType.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {
                        Toast.makeText(this, getString(R.string.enter_structure_type), Toast.LENGTH_SHORT).show();
                    } else if (landType.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {
                        Toast.makeText(this, getString(R.string.land_type), Toast.LENGTH_SHORT).show();
                    } else {
                        conditionsForUpdateData("online");
                    }
                } else {
                    conditionsForUpdateData("online");
                }
            }
        });

        save_new_point_offline.setOnClickListener(view -> {
            if (boundary.equalsIgnoreCase("newBoundary")) {
                Toast.makeText(this, getResources().getString(R.string.you_can_not_save_boundary_details_offline), Toast.LENGTH_SHORT).show();
            } else {
                conditionsForAddData("offline");
            }
        });


        add_record_btn.setOnClickListener(view -> {
            if (!connection_detector.isConnected()) {
                Toast.makeText(this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
            } else {
                if (boundary.equalsIgnoreCase("newBoundary")) {
                    if (floorSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {
                        Toast.makeText(this, getString(R.string.floors_details), Toast.LENGTH_SHORT).show();
                    } else if (structureType.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {
                        Toast.makeText(this, getString(R.string.enter_structure_type), Toast.LENGTH_SHORT).show();
                    } else if (landType.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {
                        Toast.makeText(this, getString(R.string.land_type), Toast.LENGTH_SHORT).show();
                    } else {
                        conditionsForAddData("online");
                    }
                } else {
                    conditionsForAddData("online");
                }

            }
        });


        select_feedback_spinner.setOnClickListener(v -> {
            SpinnerDialog spinnerDialog4 = new SpinnerDialog(SurveyFormScreen.this, feedbackList, getString(R.string.select_feedback));
            spinnerDialog4.bindOnSpinerListener((s, i1) -> {
                select_feedback_spinner.setText(s);
                Log.d(TAG, "initViews:Manish " + i1);
                feedback_value = feedbackId.get(i1);
                Log.d(TAG, "initViews: " + feedback_value);
            });
            spinnerDialog4.showSpinerDialog();
        });


    }

    private void conditionsForUpdateData(String appMode) {
        if (img_path_1.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_first_image), Toast.LENGTH_SHORT).show();
        } else if (img_path_2.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_second_image), Toast.LENGTH_SHORT).show();
        } else if (img_path_3.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_third_image), Toast.LENGTH_SHORT).show();
        } else if (img_path_4.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_fourth_image), Toast.LENGTH_SHORT).show();
        } else if (videoPath.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_video), Toast.LENGTH_SHORT).show();
        } else if (landmarks_edit_text.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.nearby_landmarks), Toast.LENGTH_SHORT).show();
        } else if (send_auth_status.equals("-")) {
            Toast.makeText(this, getString(R.string.select_auth_unauth_type), Toast.LENGTH_SHORT).show();
        } else if (feedback_value.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_reason), Toast.LENGTH_SHORT).show();
        } else {
            if (appMode.equalsIgnoreCase("offline")) {
                verifyPointOffline(murabba_khasra_no, feedback_value);
            } else {
                uploadDataToLayer(murabba_khasra_no, feedback_value);
            }
        }

    }

    private void conditionsForAddData(String appMode) {
        if (img_path_1.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_first_image), Toast.LENGTH_SHORT).show();
        } else if (img_path_2.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_second_image), Toast.LENGTH_SHORT).show();
        } else if (img_path_3.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_third_image), Toast.LENGTH_SHORT).show();
        } else if (img_path_4.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_fourth_image), Toast.LENGTH_SHORT).show();
        } else if (videoPath.isEmpty()) {
            Toast.makeText(this, getString(R.string.capture_video), Toast.LENGTH_SHORT).show();
        } else if (landmarks_edit_text.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.nearby_landmarks), Toast.LENGTH_SHORT).show();
        } else if (send_auth_status.equals("-")) {
            Toast.makeText(this, getString(R.string.select_auth_unauth_type), Toast.LENGTH_SHORT).show();
        } else if (feedback_value.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_reason), Toast.LENGTH_SHORT).show();
        } else {
            if (appMode.equalsIgnoreCase("offline")) {
                savePointOffline(murabba_khasra_no, feedback_value, "Field");
            } else {
                if (Sp.read_shared_pref(this, "Designation").equalsIgnoreCase("Citizen")) {
                    addDataToLayer(murabba_khasra_no, feedback_value, "Public");
                } else {
                    addDataToLayer(murabba_khasra_no, feedback_value, "Field");
                }
            }

        }

    }


    private void getLandFromDatabase() {
        landIdList.clear();
        landValueList.clear();
        landIdList.add(getString(R.string.select_one));
        landValueList.add(getString(R.string.select_one));

        progressDialog3.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getLand();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");


                        progressDialog3.dismiss();
                        if (message.equalsIgnoreCase("Land List")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    landValueList.add(object.optString("land_type").trim());
                                    landIdList.add(object.optString("land_id").trim());

                                }

                                ArrayAdapter adapter = new ArrayAdapter(SurveyFormScreen.this, android.R.layout.simple_spinner_item, landValueList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                landType.setAdapter(adapter);


                            } else {
                                Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {
                        progressDialog3.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog3.dismiss();
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

    private void getStructureFromDatabase() {
        structureIdList.clear();
        structureValueList.clear();
        structureIdList.add(getString(R.string.select_one));
        structureValueList.add(getString(R.string.select_one));

        progressDialog3.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getStructure();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");

                        progressDialog3.dismiss();
                        if (message.equalsIgnoreCase("Structure List")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    structureValueList.add(object.optString("structure_type").trim());
                                    structureIdList.add(object.optString("structure_id").trim());

                                }

                                ArrayAdapter adapter = new ArrayAdapter(SurveyFormScreen.this, android.R.layout.simple_spinner_item, structureValueList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                structureType.setAdapter(adapter);


                            } else {
                                Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();

                        }

                    } catch (Exception e) {
                        progressDialog3.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog3.dismiss();
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


    private void getFloorFromDatabase() {
        floorIdList.clear();
        floorValueList.clear();
        floorIdList.add(getString(R.string.select_one));
        floorValueList.add(getString(R.string.select_one));

        progressDialog3.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getFloor();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");

                        progressDialog3.dismiss();
                        if (message.equalsIgnoreCase("Floor List")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    floorValueList.add(object.optString("floor_value").trim());
                                    floorIdList.add(object.optString("floor_id").trim());

                                }
                                ArrayAdapter adapter = new ArrayAdapter(SurveyFormScreen.this, android.R.layout.simple_spinner_item, floorValueList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                floorSpinner.setAdapter(adapter);


                            } else {
                                Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {
                        progressDialog3.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog3.dismiss();
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


    private void getReportFromApi(String send_auth_status) {
        progressDialog3.show();
        feedbackList.clear();
        feedbackId.clear();
        select_feedback_spinner.setText("Select Reason");
        feedback_value = "";


        ApiInterface retrofitAPIInterface = RetrofitClient.getRetrofitClient(SurveyFormScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = retrofitAPIInterface.getMasterAuthUnauth(send_auth_status, Sp.read_shared_pref(SurveyFormScreen.this, "login_with"));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);

                        boolean status = jsonObject.optBoolean("status");
                        String message = jsonObject.optString("message");

                        progressDialog3.dismiss();
                        if (status) {
                            JSONArray result_array = jsonObject.getJSONArray("data");
                            for (int i = 0; i < result_array.length(); i++) {
                                JSONObject object = result_array.getJSONObject(i);
                                feedbackList.add(object.optString("Name").trim());
                                feedbackId.add(object.optString("code").trim());
                            }
                        } else {
                            Toast.makeText(SurveyFormScreen.this, message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        progressDialog3.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog3.dismiss();
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
                Uri videoUri = data.getData();

                if (videoUri != null) {
                    video_view.setVideoURI(videoUri);
                    video_view.start();
                    videoPath = getRealPathFromURI(videoUri);
                    Log.d(TAG, "onActivityResult: " + videoPath);
                    try {

                        byte[] videoBytes = readVideoFile(videoPath);
                        base64VideoString = encodeVideoToBase64(videoBytes);
                        // Now, base64VideoString contains your video in Base64 format.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Record Video", Toast.LENGTH_SHORT).show();
                }

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
                            convertedImage.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(Sp.read_shared_pref(SurveyFormScreen.this, "image_quality")), stream);

                            Log.d(TAG, "onActivityResult: " + requestCode);
                            if (requestCode == 1) {
                                image_one.setImageBitmap(convertedImage);
                                imageFile = new File(getRealPathFromURI(photoUri));
                                img_path_1 = getRealPathFromURI(photoUri);
                                try {
                                    imageInByte1 = stream.toByteArray();
                                    image1Base64 = getEncoded64ImageStringFromBitmap(convertedImage);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (requestCode == 2) {
                                image_two.setImageBitmap(convertedImage);
                                imageFile2 = new File(getRealPathFromURI(photoUri));
                                img_path_2 = getRealPathFromURI(photoUri);
                                try {
                                    imageInByte2 = stream.toByteArray();
                                    image2Base64 = getEncoded64ImageStringFromBitmap(convertedImage);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (requestCode == 3) {
                                image_three.setImageBitmap(convertedImage);
                                imageFile3 = new File(getRealPathFromURI(photoUri));
                                img_path_3 = getRealPathFromURI(photoUri);

                                try {
                                    imageInByte3 = stream.toByteArray();
                                    image3Base64 = getEncoded64ImageStringFromBitmap(convertedImage);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (requestCode == 4) {
                                image_four.setImageBitmap(convertedImage);
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


    private void uploadDataToLayer(String KhsaraMuraba, String feedback_value) {

        progressDialog3.show();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        MultipartBody.Part request_image = null;
        MultipartBody.Part request_image2 = null;
        MultipartBody.Part request_image3 = null;
        MultipartBody.Part request_image4 = null;
        MultipartBody.Part video = null;

        List<MultipartBody.Part> parts = new ArrayList<>();
        if (!img_path_1.isEmpty()) {
            File file_photo = new File(img_path_1);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageInByte1);
            //request_image = MultipartBody.Part.createFormData("image", "TCP_" + KhsaraMuraba + "Verification_Image" + file_photo.getName(), requestFile);
            request_image = MultipartBody.Part.createFormData("image", user_id + "_TCP_" + KhsaraMuraba + "_Verification_Image_" + file_photo.getName(), requestFile);
            parts.add(request_image);
        }
        if (!img_path_2.isEmpty()) {
            File file_photo2 = new File(img_path_2);
            RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), imageInByte2);
            //request_image2 = MultipartBody.Part.createFormData("image", "TCP_" + KhsaraMuraba + "Verification_Image" + file_photo2.getName(), requestFile2);
            request_image2 = MultipartBody.Part.createFormData("image", user_id + "_TCP_" + KhsaraMuraba + "_Verification_Image_" + file_photo2.getName(), requestFile2);
            parts.add(request_image2);
        }

        if (!img_path_3.isEmpty()) {
            File file_photo3 = new File(img_path_3);
            RequestBody requestFile3 = RequestBody.create(MediaType.parse("multipart/form-data"), imageInByte3);
            //request_image3 = MultipartBody.Part.createFormData("image", "TCP_" + KhsaraMuraba + "Verification_Image" + file_photo3.getName(), requestFile3);
            request_image3 = MultipartBody.Part.createFormData("image", user_id + "_TCP_" + KhsaraMuraba + "_Verification_Image_" + file_photo3.getName(), requestFile3);
            parts.add(request_image3);
        }
        if (!img_path_4.isEmpty()) {
            File file_photo4 = new File(img_path_4);
            RequestBody requestFile4 = RequestBody.create(MediaType.parse("multipart/form-data"), imageInByte4);
            //request_image4 = MultipartBody.Part.createFormData("image", "TCP_" + KhsaraMuraba + "Verification_Image" + file_photo4.getName(), requestFile4);
            request_image4 = MultipartBody.Part.createFormData("image", user_id + "_TCP_" + KhsaraMuraba + "_Verification_Image_" + file_photo4.getName(), requestFile4);
            parts.add(request_image4);

        }
        if (videoPath != null) {
            File file_photo5 = new File(videoPath);
            RequestBody requestFile5 = RequestBody.create(MediaType.parse("multipart/form-data"), file_photo5);
            //video = MultipartBody.Part.createFormData("image", "TCP_" + KhsaraMuraba + "Verification_Video" + file_photo5.getName(), requestFile5);
            video = MultipartBody.Part.createFormData("image", user_id + "_TCP_" + KhsaraMuraba + "_Verification_Video_" + file_photo5.getName(), requestFile5);
            parts.add(video);
        }
        RequestBody user_mobile_multi = RequestBody.create(MediaType.parse("multipart/form-data"), user_id);
        RequestBody gis_id_multi = RequestBody.create(MediaType.parse("multipart/form-data"), gisId);
        RequestBody u_status_multi = RequestBody.create(MediaType.parse("multipart/form-data"), "Y");
        RequestBody entry_date_multi = RequestBody.create(MediaType.parse("multipart/form-data"), strDate);
        RequestBody user_name_multi = RequestBody.create(MediaType.parse("multipart/form-data"), user_name);
        RequestBody id_multi = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(objectID));
        RequestBody nearByLandMark = RequestBody.create(MediaType.parse("multipart/form-data"), landmarks_edit_text.getText().toString());
        RequestBody feedback = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(feedback_value));
        RequestBody year_multi_multi = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(year + 1900));
        RequestBody remarks_multi = RequestBody.create(MediaType.parse("multipart/form-data"), remarks_edit_text.getText().toString());

        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(SurveyFormScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.updatePointBase64V2(String.valueOf(objectID), String.valueOf(year + 1900), user_id,
                strDate, user_name, "Y", landmarks_edit_text.getText().toString(), gisId, image1Base64, image2Base64, image3Base64, image4Base64, base64VideoString,
                send_auth_status, String.valueOf(feedback_value), remarks_edit_text.getText().toString(),
                owner_name_edit_text.getText().toString()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        progressDialog3.dismiss();
                        String result = response.body().string();
                        JSONObject obj = new JSONObject(result);

                        String message = obj.getString("message");
                        boolean status = obj.optBoolean("status");
                        if (status) {
                            if (message.equalsIgnoreCase("Data  Inserted")) {

                                if (MapChangeBoundaryScreen.sketchEditorUpdate.isSketchValid()) {
                                    progressDialog3.show();
                                    Toast.makeText(SurveyFormScreen.this, getString(R.string.point_updated), Toast.LENGTH_SHORT).show();
                                    updateGISIDInPolygonLayer(gisId, uid);

                                } else {
                                    Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFormScreen.this);
                                    builder.setMessage(getString(R.string.point_is_verified_successfully));
                                    builder.setCancelable(false);
                                    builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                                        dialog.dismiss();
                                        Intent refresh = new Intent(SurveyFormScreen.this, MapScreen.class);
                                        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(refresh);
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }

                            } else {
                                Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        progressDialog3.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog3.dismiss();
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

    private void updateGISIDInPolygonLayer(String gis_id, String uid) {

        polygonServiceFeatureTable.loadAsync();
        final QueryParameters parameters = new QueryParameters();
        parameters.setWhereClause("gisId='" + gis_id + "'");

        if (polygonServiceFeatureTable != null) {
            final ListenableFuture<FeatureQueryResult> future = polygonServiceFeatureTable.queryFeaturesAsync(parameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();

                    if (result.iterator().hasNext()) {
                        for (final Feature ftr : result) {
                            ArcGISFeature updateFeature = (ArcGISFeature) ftr;
                            updationOfBoundary(updateFeature);
                        }
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "updateGISIDInPolygonLayer: " + Sp.read_shared_pref(this, "Designation"));
                        if (Sp.read_shared_pref(this, "Designation").equalsIgnoreCase("Citizen")) {
                            addGISIDInPolygonLayer(gis_id, uid, MapChangeBoundaryScreen.sketchGeometryUpdate, "Public");
                        } else {
                            addGISIDInPolygonLayer(gis_id, uid, MapChangeBoundaryScreen.sketchGeometryUpdate, "Field");
                        }
                    }

                } catch (Exception e) {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "updateGISIDInPolygonLayer: " + e.getMessage());
                    }
                    Toast.makeText(this, "Try Again " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    private void updationOfBoundary(ArcGISFeature updateFeature) {

        updateFeature.loadAsync();
        updateFeature.addDoneLoadingListener(() -> {
            if (updateFeature.getLoadStatus() == LoadStatus.LOADED) {

                Map<String, Object> attributes = new HashMap<>();

                attributes.put("year", String.valueOf(year + 1900));
                attributes.put("pointSource", "Field");
                attributes.put("user_name", user_name);
                attributes.put("verificationDate", new GregorianCalendar());
                attributes.put("verifiedBy", user_id);
                attributes.put("remarks1", remarks_edit_text.getText().toString());
                attributes.put("floor", floor_value);
                attributes.put("construction_type", structure_value);
                attributes.put("landType", land_value);
                attributes.put("tcp_area_verified", Double.valueOf(verifiedArea.getText().toString()));

                updateFeature.getAttributes().putAll(attributes);


                if (updateFeature.canUpdateGeometry()) {
                    updateFeature.setGeometry(MapChangeBoundaryScreen.sketchGeometryUpdate);

                    if (polygonServiceFeatureTable != null) {
                        final ListenableFuture<Void> updateFeatureFuture = polygonServiceFeatureTable.updateFeatureAsync(updateFeature);
                        updateFeatureFuture.addDoneListener(() -> {
                            try {
                                updateFeatureFuture.get();

                                final ListenableFuture<List<FeatureEditResult>> applyEditsFuture = polygonServiceFeatureTable.applyEditsAsync();
                                applyEditsFuture.addDoneListener(() -> {
                                    try {

                                        List<FeatureEditResult> edits = applyEditsFuture.get();
                                        // check if the server edit was successful
                                        if (edits != null && edits.size() > 0) {
                                            FeatureEditResult featureEditResult = edits.get(0);
                                            if (!featureEditResult.hasCompletedWithErrors()) {
                                                Toast.makeText(this, getString(R.string.boundaries_updated), Toast.LENGTH_SHORT).show();
                                                polygonServiceFeatureLayer.clearSelection();
                                                createHistoryShape(gisId, uid, MapChangeBoundaryScreen.sketchGeometryUpdate);
                                            } else {
                                                throw featureEditResult.getError();
                                            }
                                        }

                                    } catch (Exception e) {

                                        progressDialog3.dismiss();
                                        Toast.makeText(this, getString(R.string.error_while_uploading_data_to_server), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                });
                            } catch (InterruptedException | ExecutionException e) {
                                progressDialog3.dismiss();

                                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            }
        });


    }


    private void addDataToLayer(String KhsaraMuraba, String feedback_value, String pointSource) {

        progressDialog3.show();


        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(SurveyFormScreen.this).create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.addNewPointWithBase64V2(latitude, longitude, ca_name, dev_plan, n_d_code, selectedDisName,
                n_t_code, selectedTehsilName, n_v_code, selectedVillageName, n_murr_no, n_khas_no, String.valueOf(year + 1900),
                user_id, user_name, "Y", landmarks_edit_text.getText().toString(), pointSource, ca_key, code_bnd_dev_plan, urban_area_code_bnd, code_bnd_controlled_area, image1Base64, image2Base64, image3Base64, image4Base64, base64VideoString,
                send_auth_status, feedback_value, remarks_edit_text.getText().toString(),
                owner_name_edit_text.getText().toString(), UAKey
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject obj = new JSONObject(result);

                        String message = obj.getString("message");
                        boolean status = obj.optBoolean("status");
                        if (status) {
                            if (message.equalsIgnoreCase("Data  Inserted")) {
                                if (sketchEditor.isSketchValid()) {
                                    progressDialog3.show();

                                    JSONObject jsonObject1 = obj.optJSONObject("data");
                                    String gis_id = jsonObject1.optString("gisId");
                                    String uid = jsonObject1.optString("UID");
                                    Toast.makeText(SurveyFormScreen.this, getString(R.string.point_added), Toast.LENGTH_SHORT).show();

                                    if (Sp.read_shared_pref(SurveyFormScreen.this, "Designation").equalsIgnoreCase("Citizen")) {
                                        addGISIDInPolygonLayer(gis_id, uid, sketchGeometry, "Public");
                                    } else {
                                        addGISIDInPolygonLayer(gis_id, uid, sketchGeometry, "Field");
                                    }


                                } else {

                                    progressDialog3.dismiss();
                                    Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFormScreen.this);
                                    builder.setMessage(getString(R.string.new_point_added));
                                    builder.setCancelable(false);
                                    builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                                        dialog.dismiss();
                                        Intent refresh = new Intent(SurveyFormScreen.this, MapScreen.class);
                                        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(refresh);
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }


                            } else {
                                progressDialog3.dismiss();

                                Toast.makeText(SurveyFormScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog3.dismiss();
                            Toast.makeText(SurveyFormScreen.this, message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        progressDialog3.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog3.dismiss();
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


    private void onFailed(String s, String s1) {
        Toast.makeText(this, "" + s1, Toast.LENGTH_SHORT).show();
    }

    private void addGISIDInPolygonLayer(String gis_id, String uid, Geometry sketchGeometry, String pointSource) {

        Map<String, Object> polygonAttributes = new HashMap<>();
        polygonAttributes.put("latitude", Double.valueOf(latitude));
        polygonAttributes.put("longitude", Double.valueOf(longitude));

        polygonAttributes.put("ca_name", ca_name);
        polygonAttributes.put("dev_plan", dev_plan);
        polygonAttributes.put("n_d_code", n_d_code);
        polygonAttributes.put("n_d_name", selectedDisName);
        polygonAttributes.put("n_t_code", n_t_code);
        polygonAttributes.put("n_t_name", selectedTehsilName);
        polygonAttributes.put("n_v_code", n_v_code);
        polygonAttributes.put("n_v_name", selectedVillageName);
        polygonAttributes.put("n_murr_no", n_murr_no);
        polygonAttributes.put("n_khas_no", n_khas_no);
        polygonAttributes.put("landType", land_value);
        polygonAttributes.put("floor", floor_value);
        polygonAttributes.put("construction_type", structure_value);
        polygonAttributes.put("tcp_area_verified", Double.valueOf(verifiedArea.getText().toString()));


        polygonAttributes.put("year", String.valueOf(year + 1900));
        polygonAttributes.put("verified", "Y");
        polygonAttributes.put("verifiedBy", user_id);
        polygonAttributes.put("user_name", user_name);
        polygonAttributes.put("verificationDate", new GregorianCalendar());
        polygonAttributes.put("startDate", new GregorianCalendar());
        polygonAttributes.put("remarks1", remarks_edit_text.getText().toString());
        polygonAttributes.put("gisId", gis_id);
        polygonAttributes.put("pointSource", pointSource);
        polygonAttributes.put("UID", uid);

        polygonServiceFeatureLayer.loadAsync();
        polygonServiceFeatureLayer.addDoneLoadingListener(() -> {
            polygonFeature = polygonServiceFeatureTable.createFeature(polygonAttributes, sketchGeometry);
            polygonSelectedFeature = (ArcGISFeature) polygonFeature;
            addPolygon(polygonServiceFeatureTable, gis_id, uid);

        });

    }

    private void addPolygon(ServiceFeatureTable polygonServiceFeatureTable, String gis_id, String uid) {

        polygonSelectedFeature.addDoneLoadingListener(() -> {

            if (polygonServiceFeatureTable != null) {
                final ListenableFuture<Void> addFeatureFuture = polygonServiceFeatureTable.addFeatureAsync(polygonSelectedFeature);
                addFeatureFuture.addDoneListener(() -> {
                    try {
                        addFeatureFuture.get();

                        final ListenableFuture<List<FeatureEditResult>> applyEditsFuture = polygonServiceFeatureTable.applyEditsAsync();
                        applyEditsFuture.addDoneListener(() -> {
                            try {
                                applyEditsFuture.get();

                                Toast.makeText(SurveyFormScreen.this, getString(R.string.boundaries_added), Toast.LENGTH_SHORT).show();

                                createHistoryShape(gis_id, uid, sketchGeometry);


                            } catch (Exception e) {
                                progressDialog3.dismiss();
                                Toast.makeText(this, getString(R.string.error_while_uploading_data_to_server), Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                            }
                        });
                    } catch (InterruptedException | ExecutionException e) {
                        progressDialog3.dismiss();
                        Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    private void createHistoryShape(String gis_id, String uid, Geometry sketchGeometry) {


        Map<String, Object> attributes = new HashMap<>();

        attributes.put("tcp_area_verified", Double.valueOf(verifiedArea.getText().toString()));
        attributes.put("gisId", gis_id);
        attributes.put("UID", uid);

        historyPolygonServiceFeatureLayer.loadAsync();
        historyPolygonServiceFeatureLayer.addDoneLoadingListener(() -> {
            historyPolygonFeature = historyPolygonServiceFeatureTable.createFeature(attributes, sketchGeometry);
            historyPolygonSelectedFeature = (ArcGISFeature) historyPolygonFeature;
            addPolygonInHistory(historyPolygonServiceFeatureTable);

        });


    }

    private void addPolygonInHistory(ServiceFeatureTable historyPolygonServiceFeatureTable) {

        historyPolygonSelectedFeature.addDoneLoadingListener(() -> {

            if (historyPolygonServiceFeatureTable != null) {
                final ListenableFuture<Void> addFeatureFuture = historyPolygonServiceFeatureTable.addFeatureAsync(historyPolygonSelectedFeature);
                addFeatureFuture.addDoneListener(() -> {
                    try {
                        addFeatureFuture.get();
                        final ListenableFuture<List<FeatureEditResult>> applyEditsFuture = historyPolygonServiceFeatureTable.applyEditsAsync();
                        applyEditsFuture.addDoneListener(() -> {
                            try {
                                applyEditsFuture.get();

                                Log.d(TAG, "addPolygon: Boundary added history");

                                if (point.equalsIgnoreCase("newPoint")) {

                                    progressDialog3.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFormScreen.this);
                                    builder.setMessage(getString(R.string.point_and_boundary_added));
                                    builder.setCancelable(false);
                                    builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                                        dialog.dismiss();

                                        Intent refresh = new Intent(SurveyFormScreen.this, MapScreen.class);
                                        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(refresh);

                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                } else {

                                    progressDialog3.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFormScreen.this);
                                    builder.setMessage(getString(R.string.point_and_boundary_verified));
                                    builder.setCancelable(false);
                                    builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                                        dialog.dismiss();

                                        Intent refresh = new Intent(SurveyFormScreen.this, MapScreen.class);
                                        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(refresh);

                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }


                            } catch (Exception e) {

                                progressDialog3.dismiss();
                                Toast.makeText(this, getString(R.string.error_while_uploading_data_to_server), Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                            }
                        });
                    } catch (InterruptedException | ExecutionException e) {
                        progressDialog3.dismiss();
                        Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void createHistoryInTable(String gis_id, String uid) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(SurveyFormScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.built_up_area_history(gis_id, String.valueOf(year + 1900), remarks_edit_text.getText().toString(), user_id, user_name, "Y", "Field", structure_value, floor_value, Double.valueOf(verifiedArea.getText().toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        boolean status = jsonObject.optBoolean("status");

                        if (message.equalsIgnoreCase("Data  Inserted")) {
                            if (status) {

                                if (point.equalsIgnoreCase("newPoint")) {

                                    progressDialog3.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFormScreen.this);
                                    builder.setMessage(getString(R.string.point_and_boundary_added));
                                    builder.setCancelable(false);
                                    builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                                        dialog.dismiss();

                                        Intent refresh = new Intent(SurveyFormScreen.this, MapScreen.class);
                                        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(refresh);

                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                } else {

                                    progressDialog3.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFormScreen.this);
                                    builder.setMessage(getString(R.string.point_and_boundary_verified));
                                    builder.setCancelable(false);
                                    builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                                        dialog.dismiss();
                                        Intent refresh = new Intent(SurveyFormScreen.this, MapScreen.class);
                                        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(refresh);

                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }

                            } else {
                                progressDialog3.dismiss();
                                Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog3.dismiss();
                            Toast.makeText(SurveyFormScreen.this, "" + message, Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {
                        progressDialog3.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progressDialog3.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog3.dismiss();
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

    public Bitmap drawTextToBitmap(Bitmap bitmap, String mText) {
        try {
            Resources resources = SurveyFormScreen.this.getResources();
            float scale = resources.getDisplayMetrics().density;

            Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.BLACK);
            // text size in pixels
            paint.setTextSize((int) (20 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);

            canvas.drawText(mText, 30f, bitmap.getHeight() - 50f, paint);

            return bitmap;
        } catch (Exception e) {
            Log.d(TAG, "drawTextToBitmap: bitmap time draw error");
            return bitmap;
        }

    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.exit));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), (dialog, id) -> SurveyFormScreen.super.onBackPressed());
        builder.setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void savePointOffline(String KhsaraMuraba, String feedback_value, String pointSource) {
        AsyncTask.execute(() -> {
            Log.d("TAG", "save_data_into_room_db: ");
            NewPointRecord newPointRecord = new NewPointRecord(
                    latitude,
                    longitude,
                    ca_name,
                    dev_plan,
                    n_d_code,
                    selectedDisName,
                    n_t_code,
                    selectedTehsilName,
                    n_v_code,
                    selectedVillageName,
                    n_murr_no,
                    n_khas_no,
                    String.valueOf(year + 1900),
                    remarks_edit_text.getText().toString(),
                    user_id, user_name, "Y", landmarks_edit_text.getText().toString(),
                    String.valueOf(feedback_value),
                    pointSource,
                    ca_key,
                    code_bnd_dev_plan,
                    urban_area_code_bnd,
                    code_bnd_controlled_area,
                    image1Base64,
                    image2Base64,
                    image3Base64,
                    image4Base64,
                    videoPath,
                    send_auth_status, owner_name_edit_text.getText().toString(), UAKey
            );
            AppDataBase.getDatabase(getApplicationContext()).newPointRecordDao().insert(newPointRecord);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFormScreen.this);
                    builder.setMessage("Save New Point Offline");
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                        dialog.dismiss();
                        Intent refresh = new Intent(SurveyFormScreen.this, MapScreen.class);
                        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(refresh);

                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        });

    }


    public void verifyPointOffline(String KhsaraMuraba, String feedback_value) {
        Log.d(TAG, "verifyPointOffline: " + feedback_value);
        AsyncTask.execute(() -> {
            Log.d("TAG", "save_data_into_room_db: ");
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            String strDate = sdf.format(c.getTime());

            VerifiedPointRecord verifiedPointRecord = new VerifiedPointRecord(
                    String.valueOf(objectID),
                    String.valueOf(year + 1900),
                    remarks_edit_text.getText().toString(),
                    user_id,
                    strDate, user_name, "Y", landmarks_edit_text.getText().toString(), String.valueOf(feedback_value), gisId,
                    image1Base64,
                    image2Base64,
                    image3Base64,
                    image4Base64,
                    videoPath,
                    selectedDisName, selectedTehsilName, selectedVillageName, n_murr_no, n_khas_no
                    , latitude, longitude, send_auth_status, owner_name_edit_text.getText().toString()
            );
            AppDataBase.getDatabase(getApplicationContext()).verifiedPointDao().insert(verifiedPointRecord);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyFormScreen.this);
                    builder.setMessage(getString(R.string.save_new_point_offline));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                        dialog.dismiss();
                        Intent refresh = new Intent(SurveyFormScreen.this, MapScreen.class);
                        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(refresh);
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            });
        });

    }


    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        Log.d(TAG, "getEncoded64ImageStringFromBitmap: ");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(Sp.read_shared_pref(SurveyFormScreen.this, "image_quality")), stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
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

}