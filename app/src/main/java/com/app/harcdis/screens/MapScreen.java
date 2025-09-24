package com.app.harcdis.screens;

import static android.graphics.Color.BLUE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.CitizenModule.ChangePinScreen;
import com.app.harcdis.R;
import com.app.harcdis.point_forward_flow.AdminReadyToDemolishScreen;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.model.LatLongModels;
import com.app.harcdis.offline_storage.AppDataBase;
import com.app.harcdis.offline_storage.entites.NewPointRecord;
import com.app.harcdis.offline_storage.entites.VerifiedPointRecord;
import com.app.harcdis.point_forward_flow.DTPForwardedPointScreen;
import com.app.harcdis.point_forward_flow.model.ForwardedModel;
import com.app.harcdis.utils.Connection_Detector;
import com.app.harcdis.utils.LocaleHelper;
import com.app.harcdis.utils.LocationAssistant;
import com.app.harcdis.utils.Sp;
import com.app.harcdis.utils.UiHelper;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalUser;
import com.esri.arcgisruntime.portal.PortalUserContent;
import com.esri.arcgisruntime.security.UserCredential;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapScreen extends AppCompatActivity implements View.OnClickListener, LocationAssistant.Listener {

    final static String apiKey = "AAPK434df87ae73945b48160d2f666c044500jKyrEI9chyZsyL7-GEdxBDSn47pK-3Hqk9FtJOo72K2TRJlEhxgTFRWbqmipRV2";
    private static final String TAG = "MyTag";
    private final int requestCode = 2;
    private final String[] reqPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public ArrayList<String> languageList;
    public ArrayList<String> rangeList;
    public ArrayList<String> caList;
    ImageButton search_data_via_date;
    TextView bottom_text, role;
    PictureMarkerSymbol pin_icon;
    PictureMarkerSymbol pin_icon_two;
    //Create by manish for spinner data.
    ArrayList<String> N_MURR_NO;
    ArrayList<String> N_KHAS_NO;
    ArrayList<String> OBJECT_ID;
    ArrayList<String> gisId;
    ArrayList<String> latitude_array_list;
    ArrayList<String> longitude_array_list;
    double x1 = 0.0;
    double y1 = 0.0;
    ServiceFeatureTable serviceFeatureTable;
    Callout mCallOut;
    FloatingActionButton floatingActionButton, live_location_fab_button, on_map_fab_button, add_new_point_fab_button;
    TextView forward_floating_text, live_location_fab_text;
    GraphicsOverlay graphicsOverlay;
    SimpleLineSymbol simpleLineSymbol;
    SimpleMarkerSymbol simpleMarkerSymbol;
    ProgressDialog progressDialog;
    TextView Khsara_number, crop_spinner_session, crop_spinner_commodityC, crop_spinner_commodityN, spinner_extra_crop_name, crop_damage_text;
    Button submit, cancel, save_offline_data;
    Dialog dialogfinal;
    Dialog crop_detail_dialog;
    String session = "", commodityC = "", commodityM = "";
    ProgressDialog progressDialog3;
    com.esri.arcgisruntime.geometry.Point callpoint;
    Polygon bufferGeom;
    Bitmap convertedImage;
    String vector_tile_url = "";
    String feature_service_url = "";
    String feature_service_url_development = "";
    String feature_service_url_area_boundary = "";
    String feature_service_urban_area_boundary = "";
    String feature_service_licensed_colony_boundary = "";
    String regularized_colony_boundary = "";
    String polygon_service = "";
    String habitation = "";
    String hsidc_sites = "";
    String hsvp_haryana = "";
    String mandi_township = "";
    String clu_boundary = "";
    String districtCodeConditions;
    TextView username;
    TextView navUserid;
    TextView userRole;
    Connection_Detector connection_detector;
    Animation shake;
    String coming_dis_name;
    String coming_dis_code;
    String coming_tehsil_code;
    String coming_tehsil_code_name;
    String coming_village_code;
    String coming_village_code_name;
    boolean doubleBackToExitPressedOnce = false;
    EditText start_date, end_date;
    String selectedDisName, selectedTehsilName, selectedVillageName;
    String inside_ca_variable = " AND (AOI_CA ='CA' OR AOI_DP ='DP' OR AOI_UA ='UA')";
    String inside_dp_variable = "";
    String inside_urban_area_variable = "";
    String not_shown_variable = " AND (AOI_NA IS NULL OR AOI_NA ='')";
    String quality_control_variable = "AND qc_status ='YES' AND (point_status = '' or point_status is null)";
    //  String not_shown_variable = "";
    String assigned_ca_variable = "";
    String assigned_polygon_variable = "";
    String date_variable = "";
    String ca_variable = "";
    TextView totalPointView, verifiedPointView, unverifiedPointView;
    TextView resetDateView;
    TextView geofenceRangeView;
    //Create by jyoti for show count of all points in geofence range
    ImageView bufferRangeView;
    LinearLayout bufferRangeLinearLayout;
    String ca_name = "", dev_plan = "", ca_key = "", n_d_code, n_t_code, n_v_code, uid;
    LinearLayout dateLinearLayout;
    LinearLayout layerListTextViewLayout;
    CardView layerListLayout;
    ImageView layerShowImageView;
    CheckBox caCheckBox, devCheckBox, urbanCheckBox, licensedColonyCheckBox, cadCheckBox, pointCheckBox, polygonCheckBox, RegularColonyCheckBox, habitationLayerCheckBox;
    CheckBox mandi_township_check_box, clu_boundary_check_box;
    ArcGISVectorTiledLayer arcGISVectorTiledLayer;
    CheckBox hsvp_haryana_CheckBox, hsidc_sites_CheckBox;
    TextView add_new_point_text;
    //Applying cycle filter in mobile app
    TextView image_filter_text;
    TextView cycle_filter_text;
    LinearLayout linear_layout_start_end_image;
    LinearLayout cycle_start_date_ll_layout;
    //Image Filter Spinner
    Spinner year_spinner;
    Spinner month_spinner;
    Spinner cycle_start_date_spinner;
    Spinner cycle_end_date_spinner;
    ArrayList<String> yearList;
    ArrayList<String> monthArrayList;
    ArrayList<String> monthArrayListCode;
    ArrayList<String> monthArrayDayCount;
    TextView new_built_up_layer_text_view;
    TextView boundary_other_authority_text_view;
    TextView boundary_admin_text_view;
    TextView boundary_tcp_text_view;
    LinearLayout new_built_up_layer_collection_ll_layout;
    LinearLayout boundaries_tcp_ll_layout;
    LinearLayout boundary_other_authority_ll_layout;
    LinearLayout boundary_admin_ll_layout;
    ProgressBar progress_bar_data_loading;
    private LocationManager locationManager;
    private boolean GpsStatus;
    private TextView tvLocation;
    private EditText remarks_edit_text;
    private String img_path_1 = "";
    private MapView mapView;
    private FeatureLayer featureLayer;
    private FeatureLayer featureLayerDevelopmentPlan;
    private FeatureLayer featureLayerhabitation;
    private FeatureLayer featureLayerBoundaryTwo;
    private FeatureLayer RegularColonyFeatureLayer;
    private FeatureLayer featureLayerBoundaryUrban;
    private FeatureLayer featureLayerHSVP;
    private FeatureLayer featureLayerHSIDC;
    private FeatureLayer featureLayerMandiTownShip;
    private FeatureLayer featureLayerCluBoundary;
    private FeatureLayer featureLayerBoundaryLicensedColony;
    private FeatureLayer featureLayerPolygonService;
    private ArcGISMap arcGISMap;
    private ImageView cameraImage;
    private Uri imageUri;
    private File imageFile;
    private byte[] imageInByte;
    private LocationDisplay locationDisplay;
    private double x;
    private double y;
    private double accuracy;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private ConstraintLayout con_layout;
    private GraphicsOverlay bufferGraphicsOverlay;
    private Geometry geom;
    private String user_name, user_id, user_role;
    private final int rq_code_camera = 1;
    private final String extra_crop_name = "";
    private ProgressDialog progressDialog4;
    private ProgressDialog customProgressDialog;
    private LocationAssistant assistant;
    private ArrayList<LatLongModels> array_list_for_lat_long;
    private GraphicsOverlay offline_graphics_overlay;
    private GraphicsOverlay offline_graphics_overlay2;
    private ArrayList<Feature> mSelectedFeatures, totalPointList, verifiedPointList, unverifiedPointList;
    private String languageSelected;
    private String rangeSelected;
    private String caSelected;
    private ArrayList<String> polygonUIDArrayList;
    private String cycleYear = "";
    private String cycleMonth = "";
    private String cycleStartDate = "";
    private String cycleEndDate = "";
    private ArrayList<String> cycleStartDateArrayList;
    private ArrayList<String> cycleEndDateArrayList;


    ExtendedFloatingActionButton add_fab;
    FloatingActionButton forward_point_floating_button;
    private boolean isAllFabsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_screen);
        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 4000, false);
        assistant.setVerbose(true);
        connection_detector = new Connection_Detector(getApplicationContext());

        ArcGISRuntimeEnvironment.setApiKey(apiKey);
        initViews();

        Log.d(TAG, "onCreate: " + Sp.read_shared_pref(this, "user_name"));
        languageList = new ArrayList<>();
        languageList.add("English");
        languageList.add("हिंदी");
        languageList.add("ਪੰਜਾਬੀ");

//ArrayList For Cycle Code
        cycleEndDateArrayList = new ArrayList<>();
        cycleStartDateArrayList = new ArrayList<>();
        yearList = new ArrayList<>();
        monthArrayList = new ArrayList<>();
        monthArrayListCode = new ArrayList<>();
        monthArrayDayCount = new ArrayList<>();


        rangeList = new ArrayList<>();
        rangeList.add("Inside Controlled Area");
        rangeList.add("Outside Controlled Area");
        rangeList.add("Inside Development Plan");
        rangeList.add("Outside Development Plan");
        rangeList.add("Inside Urban Area");
        rangeList.add("Outside Urban Area");
        rangeList.add("All");
        rangeList.add("DEFAULT");

        caList = new ArrayList<>();

        locationDisplay = mapView.getLocationDisplay();

        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        mapView.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84()), 2050);
        locationDisplay.startAsync();

        //callpoint = new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84());


        datasourcestatuschangedlistener();
        locationchangedmethod();


        add_new_point_fab_button.setOnClickListener(v -> {
            if (polygon_service.isEmpty()) {
                Toast.makeText(this, getString(R.string.no_survice_currently_running), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, MapPointSelectionScreen.class);
                intent.putExtra("vector_tile_url", vector_tile_url);
                intent.putExtra("feature_service_url", feature_service_url);
                intent.putExtra("feature_service_url_development", feature_service_url_development);
                intent.putExtra("feature_service_url_area_boundary", feature_service_url_area_boundary);
                intent.putExtra("polygon_service", polygon_service);
                startActivity(intent);
            }
        });


        live_location_fab_button.setOnClickListener(this);
        floatingActionButton.setOnClickListener(view -> {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onCreate: " + selectedDisName);
                Log.d(TAG, "onCreate: " + selectedTehsilName);
                Log.d(TAG, "onCreate: " + selectedVillageName);
            }
            floatingActionButton.setVisibility(View.GONE);

            if (!connection_detector.isConnected()) {
                Toast.makeText(this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MapScreen.this, MapChangeBoundaryScreen.class);
                intent.putExtra("point", "editPoint");
                intent.putExtra("boundary", "noBoundary");
                intent.putExtra("latitude", latitude_array_list.get(0));
                intent.putExtra("longitude", longitude_array_list.get(0));
                intent.putExtra("n_d_name", selectedDisName);
                intent.putExtra("n_t_name", selectedTehsilName);
                intent.putExtra("n_v_name", selectedVillageName);
                intent.putExtra("objectId", OBJECT_ID.get(0));
                intent.putExtra("gisId", gisId.get(0));
                intent.putExtra("uid", uid);
                intent.putExtra("murabba", N_MURR_NO.get(0));
                intent.putExtra("khasra_no", N_KHAS_NO.get(0));
                intent.putExtra("n_d_code", n_d_code);
                intent.putExtra("n_t_code", n_t_code);
                intent.putExtra("n_v_code", n_v_code);
                intent.putExtra("ca_name", ca_name);
                intent.putExtra("dev_plan", dev_plan);
                intent.putExtra("ca_key", ca_key);
                intent.putExtra("vector_tile_url", vector_tile_url);
                intent.putExtra("feature_service_url", feature_service_url);
                intent.putExtra("feature_service_url_development", feature_service_url_development);
                intent.putExtra("feature_service_url_area_boundary", feature_service_url_area_boundary);
                intent.putExtra("polygon_service", polygon_service);

                startActivity(intent);
                clearArrayListAll();
                graphicsOverlay.getGraphics().clear();
            }
        });


    }

    public void showLoginButton() {

        MenuItem pointRange = nv.getMenu().findItem(R.id.pointRange);
        MenuItem controlledArea = nv.getMenu().findItem(R.id.controlledArea);
        MenuItem changeLocation = nv.getMenu().findItem(R.id.changeLocation);
        MenuItem changePin = nv.getMenu().findItem(R.id.changePin);


        changePin.setVisible(true);
        pointRange.setVisible(false);
        controlledArea.setVisible(false);
        //changeLocation.setVisible(false);

    }

    private void getControlledArea() {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(MapScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getControlledArea(coming_dis_code);
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
                            if (message.equalsIgnoreCase("Controlled Area")) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    caList.add(object.optString("ca_name"));
                                }
                                caList.add("All");
                            } else {
                                show_toast(message);
                            }

                        } else {
                            show_toast(message);
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
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "PPlease Try Again later ");


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



    /*-------------------------------------------------------------------------dialog for language-----------------------------------------------------------------------------*/

    private void showDialogForSelectLanguage() {
        ArrayList<Integer> itemSelected = new ArrayList<>();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title)).setCancelable(false).setSingleChoiceItems(languageList.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemSelected.add(which);
            }
        }).setPositiveButton("Done!", (dialog, id) -> {
            if (itemSelected.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_lang), Toast.LENGTH_SHORT).show();
            } else {
                for (int i : itemSelected) {
                    languageSelected = String.valueOf(languageList.get(i));
                }

                updateViewsAccordingLanguage(languageSelected);
            }

        });
        builder.create().show();

    }


    private void updateViewsAccordingLanguage(String languageSelected) {
        if (languageSelected.equals("English")) {
            LocaleHelper.setLocale(this, "en");
        } else if (languageSelected.equals("हिंदी")) {
            LocaleHelper.setLocale(this, "hi");
        } else if (languageSelected.equals("ਪੰਜਾਬੀ")) {
            LocaleHelper.setLocale(this, "pa");
        }
        finish();
        startActivity(getIntent());
    }
    /*-------------------------------------------------------------------------dialog for point range-----------------------------------------------------------------------------*/

    private void showDialogForSelectPointRange() {
        ArrayList<Integer> itemSelected = new ArrayList<>();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title)).setCancelable(false).setSingleChoiceItems(rangeList.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemSelected.add(which);
            }
        }).setPositiveButton("Done!", (dialog, id) -> {
            if (itemSelected.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_point_range), Toast.LENGTH_SHORT).show();
            } else {
                for (int i : itemSelected) {
                    rangeSelected = String.valueOf(rangeList.get(i));
                }
                updateViewsAccordingRange(rangeSelected);
            }

        });
        builder.create().show();

    }


    private void updateViewsAccordingRange(String rangeSelected) {
        arcGISMap.getOperationalLayers().remove(featureLayer);
        //arcGISMap.getOperationalLayers().remove(featureLayerPolygonService);

        switch (rangeSelected) {
            case "Inside Controlled Area":
                inside_ca_variable = " AND AOI_CA ='CA'";
                inside_dp_variable = "";
                inside_urban_area_variable = "";
                break;
            case "Outside Controlled Area":
                inside_ca_variable = " AND AOI_CA <> 'CA' ";
                inside_dp_variable = "";
                inside_urban_area_variable = "";
                break;
            case "Inside Development Plan":
                inside_dp_variable = " AND AOI_DP ='DP'";
                inside_ca_variable = "";
                inside_urban_area_variable = "";
                break;
            case "Outside Development Plan":
                inside_dp_variable = " AND AOI_DP <> 'DP' ";
                inside_ca_variable = "";
                inside_urban_area_variable = "";
                break;
            case "Inside Urban Area":
                inside_urban_area_variable = " AND AOI_UA ='UA' ";
                inside_ca_variable = "";
                inside_dp_variable = "";
                break;
            case "Outside Urban Area":
                inside_urban_area_variable = " AND AOI_UA <> 'UA'";
                inside_ca_variable = "";
                inside_dp_variable = "";
                break;
            case "All":
                inside_ca_variable = "";
                inside_dp_variable = "";
                inside_urban_area_variable = "";
                break;
            case "DEFAULT":
                inside_ca_variable = " AND( AOI_CA ='CA' OR AOI_DP ='DP' OR AOI_UA ='UA') ";
                inside_dp_variable = "";
                inside_urban_area_variable = "";
                break;
        }
        set_up_all_data();

    }
    /*-------------------------------------------------------------------------dialog for ControlledArea-----------------------------------------------------------------------------*/

    private void showDialogForSelectControlledArea() {
        ArrayList<Integer> itemSelected = new ArrayList<>();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title)).setCancelable(false).setSingleChoiceItems(caList.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemSelected.add(which);
            }
        }).setPositiveButton("Done!", (dialog, id) -> {
            if (itemSelected.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_point_range), Toast.LENGTH_SHORT).show();
            } else {
                for (int i : itemSelected) {
                    caSelected = String.valueOf(caList.get(i));
                }
                updateViewsAccordingCA(caSelected);
            }
        });
        builder.create().show();

    }


    private void updateViewsAccordingCA(String caSelected) {

        arcGISMap.getOperationalLayers().remove(featureLayer);
        // arcGISMap.getOperationalLayers().remove(featureLayerPolygonService);

        if (caSelected.equalsIgnoreCase("All")) {
            ca_variable = "";
        } else {
            ca_variable = "AND ca_name ='" + caSelected + "'";
        }
        set_up_all_data();

    }

    /*-------------------------------------------------------------------------base language-----------------------------------------------------------------------------*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

    /*-------------------------------------------------------------------------date time-----------------------------------------------------------------------------*/

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /*-------------------------------------------------------------------------location history-----------------------------------------------------------------------------*/

    private void makeLocationHistory() {
        // progressDialog.show();
        if (BuildConfig.DEBUG)
            Log.d(TAG, "makeLocationHistory: " + Sp.read_shared_pref(MapScreen.this, "user_mobile"));
        ApiInterface retrofitAPIInterface = RetrofitClient.getRetrofitClient(MapScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = retrofitAPIInterface.locationHistory(Sp.read_shared_pref(MapScreen.this, "user_mobile"), String.valueOf(y), String.valueOf(x), getDateTime(), "LOGOUT");

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
                            if (message.equalsIgnoreCase("Success")) {
                                Toast.makeText(MapScreen.this, message, Toast.LENGTH_SHORT).show();
                                Sp.logout(MapScreen.this);
                                finish();
                            }
                        } else {
                            Toast.makeText(MapScreen.this, "SomeThing went wrong", Toast.LENGTH_SHORT).show();
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "onResponse: " + message);
                            }
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

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
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

    private void AllowPermissions() {
        int hasCAMERAPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int hasWRITE_EXTERNAL_STORAGEPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasACCESS_FINE_LOCATION = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasACCESS_COARSE_LOCATION = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> permissions = new ArrayList<String>();
        if (hasCAMERAPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (hasWRITE_EXTERNAL_STORAGEPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

    private void get_all_layer_by_api(String dist_code) {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(MapScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getFeatureLayerByDisCode(dist_code, Sp.read_shared_pref(MapScreen.this, "login_with"));
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
                            if (message.equalsIgnoreCase("Data Inserted")) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                if (result_array.length() > 0) {
                                    JSONObject object = result_array.getJSONObject(0);
                                    vector_tile_url = object.getString("tile_service");
                                    feature_service_url = object.getString("feature_service");
                                    feature_service_url_development = object.getString("development_plan_service");
                                    feature_service_url_area_boundary = object.getString("controlled_area_boundary");
                                    feature_service_urban_area_boundary = object.getString("urban_area_boundary");
                                    feature_service_licensed_colony_boundary = object.getString("licensed_colony_boundary");
                                    regularized_colony_boundary = object.getString("regularized_colony_boundary");
                                    polygon_service = object.getString("polygon_service");
                                    habitation = object.getString("habitation");
                                    hsidc_sites = object.getString("hsidc_sites");
                                    hsvp_haryana = object.getString("hsvp_haryana");
                                    mandi_township = object.getString("mandi_township");
                                    clu_boundary = object.getString("clu_boundary");

                                    Sp.write_shared_pref(getApplicationContext(), "polygon_feature_service", object.getString("polygon_feature_service"));
                                    Sp.write_shared_pref(getApplicationContext(), "polygon_service_history", object.getString("polygon_service_history"));

                                    if (BuildConfig.DEBUG) {
                                        Log.d(TAG, "onResponse: " + vector_tile_url);
                                        Log.d(TAG, "onResponse: " + feature_service_url);
                                        Log.d(TAG, "onResponse: " + feature_service_url_development);
                                        Log.d(TAG, "onResponse: " + feature_service_url_area_boundary);
                                        Log.d(TAG, "onResponse: " + polygon_service);
                                    }
                                    set_up_function(vector_tile_url, feature_service_url, feature_service_url_development, feature_service_url_area_boundary, polygon_service, feature_service_urban_area_boundary, feature_service_licensed_colony_boundary, regularized_colony_boundary, habitation);

                                } else {
                                    show_toast(message);
                                }

                            } else {
                                show_toast(message);
                            }

                        } else {
                            show_toast(message);
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


    private void show_toast(String api_message) {

        Toast.makeText(MapScreen.this, api_message, Toast.LENGTH_SHORT).show();
    }

    private void set_up_function(String vector_u, String feature_u, String feature_service_url_development, String feature_service_url_area_boundary, String polygon_service, String feature_service_urban_area_boundary, String feature_service_licensed_colony_boundary, String regularized_colony_boundary, String habitation) {
        arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, 30.375233486156763, 76.78257619714825, 16);

//https://onemapdepts.gmda.gov.in/server4/rest/services/TCP_Haryana/TCP_encroahment_V1/MapServer/0
        serviceFeatureTable = new ServiceFeatureTable(feature_u);
        serviceFeatureTable.loadAsync();
        Log.d(TAG, "set_up_function: " + serviceFeatureTable);

        ServiceFeatureTable boundaryServiceTableTwo = new ServiceFeatureTable(feature_service_url_area_boundary);
        boundaryServiceTableTwo.loadAsync();

        ServiceFeatureTable boundaryServiceTableLicensedColony = new ServiceFeatureTable(feature_service_licensed_colony_boundary);
        boundaryServiceTableLicensedColony.loadAsync();

        ServiceFeatureTable boundaryServiceTableThreePolygon = new ServiceFeatureTable(polygon_service);
        boundaryServiceTableThreePolygon.loadAsync();

        featureLayer = new FeatureLayer(serviceFeatureTable);
        featureLayerPolygonService = new FeatureLayer(boundaryServiceTableThreePolygon);

        featureLayerBoundaryTwo = new FeatureLayer(boundaryServiceTableTwo);

        featureLayerBoundaryLicensedColony = new FeatureLayer(boundaryServiceTableLicensedColony);

        //Setup base map
        mapView.getGraphicsOverlays().add(bufferGraphicsOverlay);
        mapView.getGraphicsOverlays().add(offline_graphics_overlay);
        mapView.getGraphicsOverlays().add(offline_graphics_overlay2);

        arcGISMap.getOperationalLayers().remove(featureLayer);
        featureLayerBoundaryTwo.setDefinitionExpression(districtCodeConditions);
        featureLayerBoundaryLicensedColony.setDefinitionExpression(districtCodeConditions);
        arcGISMap.getOperationalLayers().add(featureLayerBoundaryTwo);
        arcGISMap.getOperationalLayers().add(featureLayerBoundaryLicensedColony);

//        if (Sp.read_shared_pref(this, "Designation").equalsIgnoreCase("Citizen"))
        String designation = Sp.read_shared_pref(this, "Designation");
        if (designation != null && designation.equalsIgnoreCase("Citizen"))

        {
            String citizenCondition = "AND verifiedBy = '" + user_id + "'";

            if (coming_dis_code != null && coming_tehsil_code != null && coming_village_code != null) {

                String where = "n_v_code = '" + coming_village_code + "'" + citizenCondition;

                featureLayer.setDefinitionExpression(where);
                featureLayerPolygonService.setDefinitionExpression(where);

            } else if (coming_dis_code != null && coming_tehsil_code != null) {
                String where = "n_t_code = '" + coming_tehsil_code + "'" + citizenCondition;

                featureLayer.setDefinitionExpression(where);
                featureLayerPolygonService.setDefinitionExpression(where);
            } else if (coming_dis_code != null) {
                String where = "n_d_code = '" + coming_dis_code + "'" + citizenCondition;

                featureLayer.setDefinitionExpression(where);
                featureLayerPolygonService.setDefinitionExpression(where);
            }
            arcGISMap.getOperationalLayers().add(featureLayer);
        } else {
            set_up_all_data();
        }

        mapView.setMap(arcGISMap);
        mCallOut = mapView.getCallout();
        clearArrayListAll();
        addGraphics();
        loadFeatureMap();
    }

    private void initViews() {

//Managing Layout Collection
        add_fab = findViewById(R.id.add_fab);
        forward_point_floating_button = findViewById(R.id.forward_point_floating_button);
        new_built_up_layer_text_view = findViewById(R.id.new_built_up_layer_text_view);
        progress_bar_data_loading = findViewById(R.id.progress_bar_data_loading);
        boundary_other_authority_text_view = findViewById(R.id.boundary_other_authority_text_view);
        boundary_admin_text_view = findViewById(R.id.boundary_admin_text_view);
        boundary_tcp_text_view = findViewById(R.id.boundary_tcp_text_view);
        new_built_up_layer_collection_ll_layout = findViewById(R.id.new_built_up_layer_collection_ll_layout);
        boundaries_tcp_ll_layout = findViewById(R.id.boundaries_tcp_ll_layout);
        boundary_other_authority_ll_layout = findViewById(R.id.boundary_other_authority_ll_layout);
        boundary_admin_ll_layout = findViewById(R.id.boundary_admin_ll_layout);

        image_filter_text = findViewById(R.id.image_filter_text);
        cycle_filter_text = findViewById(R.id.cycle_filter_text);
        linear_layout_start_end_image = findViewById(R.id.linear_layout_start_end_image);
        cycle_start_date_ll_layout = findViewById(R.id.cycle_start_date_ll_layout);
        year_spinner = findViewById(R.id.year_spinner);
        month_spinner = findViewById(R.id.month_spinner);
        cycle_start_date_spinner = findViewById(R.id.cycle_start_date_spinner);
        cycle_end_date_spinner = findViewById(R.id.cycle_end_date_spinner);

        Drawable drawable_down = getResources().getDrawable(R.drawable.arrow_down_white);
        Drawable drawable_up = getResources().getDrawable(R.drawable.arrow_up_white);
        new_built_up_layer_text_view.setOnClickListener(view -> {
            if (new_built_up_layer_collection_ll_layout.getVisibility() == View.VISIBLE) {
                new_built_up_layer_text_view.setBackgroundColor(getResources().getColor(R.color.appColor));
                new_built_up_layer_collection_ll_layout.setVisibility(View.GONE);
                new_built_up_layer_text_view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable_up, null);
            } else {
                new_built_up_layer_collection_ll_layout.setVisibility(View.VISIBLE);
                new_built_up_layer_text_view.setBackgroundColor(getResources().getColor(R.color.grey));
                new_built_up_layer_text_view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable_down, null);
            }
        });

        boundary_tcp_text_view.setOnClickListener(view -> {
            if (boundaries_tcp_ll_layout.getVisibility() == View.VISIBLE) {
                boundary_tcp_text_view.setBackgroundColor(getResources().getColor(R.color.appColor));
                boundaries_tcp_ll_layout.setVisibility(View.GONE);
                boundary_tcp_text_view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable_up, null);

            } else {
                boundaries_tcp_ll_layout.setVisibility(View.VISIBLE);
                boundary_tcp_text_view.setBackgroundColor(getResources().getColor(R.color.grey));
                boundary_tcp_text_view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable_down, null);
            }
        });

        boundary_other_authority_text_view.setOnClickListener(view -> {
            if (boundary_other_authority_ll_layout.getVisibility() == View.VISIBLE) {
                boundary_other_authority_text_view.setBackgroundColor(getResources().getColor(R.color.appColor));
                boundary_other_authority_ll_layout.setVisibility(View.GONE);
                boundary_other_authority_text_view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable_up, null);

            } else {
                boundary_other_authority_ll_layout.setVisibility(View.VISIBLE);
                boundary_other_authority_text_view.setBackgroundColor(getResources().getColor(R.color.grey));
                boundary_other_authority_text_view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable_down, null);
            }
        });
        boundary_admin_text_view.setOnClickListener(view -> {
            if (boundary_admin_ll_layout.getVisibility() == View.VISIBLE) {
                boundary_admin_text_view.setBackgroundColor(getResources().getColor(R.color.appColor));
                boundary_admin_ll_layout.setVisibility(View.GONE);
                boundary_admin_text_view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable_up, null);

            } else {
                boundary_admin_ll_layout.setVisibility(View.VISIBLE);
                boundary_admin_text_view.setBackgroundColor(getResources().getColor(R.color.grey));
                boundary_admin_text_view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable_down, null);
            }
        });


        image_filter_text.setOnClickListener(view -> {
            image_filter_text.setBackgroundDrawable(getResources().getDrawable(R.drawable.text_background));
            cycle_filter_text.setBackgroundDrawable(getResources().getDrawable(R.drawable.text_background_two));
            linear_layout_start_end_image.setVisibility(View.VISIBLE);
            cycle_start_date_ll_layout.setVisibility(View.GONE);
            resetFunction();
        });


        cycle_filter_text.setOnClickListener(view -> {
            image_filter_text.setBackgroundDrawable(getResources().getDrawable(R.drawable.text_background_two));
            cycle_filter_text.setBackgroundDrawable(getResources().getDrawable(R.drawable.text_background));
            linear_layout_start_end_image.setVisibility(View.GONE);
            cycle_start_date_ll_layout.setVisibility(View.VISIBLE);
            resetFunction();
            setUpYearSpinner();
        });


        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(MapScreen.this, "Select Year ", Toast.LENGTH_SHORT).show();
                } else {
                    cycleYear = yearList.get(i);
                    setUpMonthSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(MapScreen.this, "Select Month ", Toast.LENGTH_SHORT).show();
                } else {
                    cycleMonth = monthArrayListCode.get(i);
                    if (cycleYear.isEmpty()) {
                        Toast.makeText(MapScreen.this, "Select Year", Toast.LENGTH_SHORT).show();
                    } else {
                        hitApiToFetchCycleStartDate(cycleYear, cycleMonth);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cycle_start_date_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (cycleYear.isEmpty()) {
                    Toast.makeText(MapScreen.this, "Select Year", Toast.LENGTH_SHORT).show();
                } else if (cycleMonth.isEmpty()) {
                    Toast.makeText(MapScreen.this, "Select Month", Toast.LENGTH_SHORT).show();
                } else if (i == 0) {
                    Toast.makeText(MapScreen.this, "Select Cycle Start Date", Toast.LENGTH_SHORT).show();
                } else {
                    cycleStartDate = cycleStartDateArrayList.get(i);
                    hitQueryOnLayerByCycleStartDate(cycleStartDate);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cycle_end_date_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(MapScreen.this, "Select Cycle End Date", Toast.LENGTH_SHORT).show();
                } else {
                    cycleEndDate = cycleEndDateArrayList.get(i);
                    set_up_all_data();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        caCheckBox = findViewById(R.id.caCheckBox);
        RegularColonyCheckBox = findViewById(R.id.RegularColonyCheckBox);
        hsidc_sites_CheckBox = findViewById(R.id.hsidc_sites_CheckBox);
        hsvp_haryana_CheckBox = findViewById(R.id.hsvp_haryana_CheckBox);
        habitationLayerCheckBox = findViewById(R.id.habitationLayerCheckBox);
        mandi_township_check_box = findViewById(R.id.mandi_township_check_box);
        clu_boundary_check_box = findViewById(R.id.clu_boundary_check_box);
        devCheckBox = findViewById(R.id.devCheckBox);
        urbanCheckBox = findViewById(R.id.urbanCheckBox);
        licensedColonyCheckBox = findViewById(R.id.licensedColonyCheckBox);
        cadCheckBox = findViewById(R.id.cadCheckBox);
        pointCheckBox = findViewById(R.id.pointCheckBox);
        polygonCheckBox = findViewById(R.id.polygonCheckBox);

        layerListTextViewLayout = findViewById(R.id.layerListTextView);
        layerListLayout = findViewById(R.id.layerListLayout);
        layerShowImageView = findViewById(R.id.layerShowImageView);

        dateLinearLayout = findViewById(R.id.dateLinearLayout);
        geofenceRangeView = findViewById(R.id.geofenceRangeView);
        bufferRangeView = findViewById(R.id.bufferRangeView);
        bufferRangeLinearLayout = findViewById(R.id.bufferRangeLinearLayout);
        totalPointView = findViewById(R.id.totalPointView);
        verifiedPointView = findViewById(R.id.verifiedPointView);
        unverifiedPointView = findViewById(R.id.unverifiedPointView);
        tvLocation = findViewById(R.id.tvLocation);
        mapView = findViewById(R.id.districtMapView);

        bottom_text = findViewById(R.id.bottom_text);
        role = findViewById(R.id.role);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        search_data_via_date = findViewById(R.id.search_data_via_date);
        resetDateView = findViewById(R.id.resetDateView);
        floatingActionButton = findViewById(R.id.fab_data_button);
        live_location_fab_button = findViewById(R.id.live_location_fab_button);
        add_new_point_fab_button = findViewById(R.id.add_new_point_fab_button);
        on_map_fab_button = findViewById(R.id.on_map_fab_button);
        add_new_point_text = findViewById(R.id.add_new_point_text);
        live_location_fab_text = findViewById(R.id.live_location_fab_text);
        forward_floating_text = findViewById(R.id.forward_floating_text);

        con_layout = findViewById(R.id.con_layout);

        N_MURR_NO = new ArrayList<>();
        N_KHAS_NO = new ArrayList<>();
        OBJECT_ID = new ArrayList<>();
        gisId = new ArrayList<>();
        polygonUIDArrayList = new ArrayList<>();

        latitude_array_list = new ArrayList<>();
        longitude_array_list = new ArrayList<>();
        array_list_for_lat_long = new ArrayList<>();

        totalPointList = new ArrayList<>();
        verifiedPointList = new ArrayList<>();
        unverifiedPointList = new ArrayList<>();

        imageInByte = new byte[0];
        progressDialog = new ProgressDialog(MapScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        bufferGraphicsOverlay = new GraphicsOverlay();
        offline_graphics_overlay = new GraphicsOverlay();
        offline_graphics_overlay2 = new GraphicsOverlay();

        shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        clearArrayListAll();
        portal_login_vector_tile_layer();
        portal_login_gmda();
        dl = findViewById(R.id.drawer_layout_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = findViewById(R.id.nv);
        nv.setItemIconTintList(null);
        View headerView = nv.getHeaderView(0);
        user_id = Sp.read_shared_pref(MapScreen.this, "user_mobile");
        user_name = Sp.read_shared_pref(MapScreen.this, "name");
        user_role = Sp.read_shared_pref(MapScreen.this, "Designation");
        districtCodeConditions = "n_d_code = '" + Sp.read_shared_pref(MapScreen.this, "dis_code_store") + "'";
        ArrayList<String> assignedCaArrayList = new ArrayList<>();

        //assigned ca fetching and prepare the query by Manish 29-08-2023

        String assignedCaNameString = Sp.read_shared_pref(MapScreen.this, "assignedCA");
        if (assignedCaNameString != null) {
            if (assignedCaNameString.contains(",")) {
                String[] assigned_ca_array = assignedCaNameString.split(",");
                Collections.addAll(assignedCaArrayList, assigned_ca_array);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < assignedCaArrayList.size(); i++) {
                    if (i == assignedCaArrayList.size() - 1) {
                        stringBuilder.append("CA_Key_GIS = '").append(assignedCaArrayList.get(i)).append("'");
                    } else {
                        stringBuilder.append("CA_Key_GIS = '").append(assignedCaArrayList.get(i)).append("'").append(" OR ");

                    }
                }
                assigned_ca_variable = " AND (" + stringBuilder + ")";


            } else {
                StringBuilder stringBuilder = new StringBuilder();
                assignedCaArrayList.add(assignedCaNameString);
                for (int i = 0; i < assignedCaArrayList.size(); i++) {
                    if (i == assignedCaArrayList.size() - 1) {
                        stringBuilder.append("CA_Key_GIS = '").append(assignedCaArrayList.get(i)).append("'");
                    } else {
                        stringBuilder.append("CA_Key_GIS = '").append(assignedCaArrayList.get(i)).append("'").append(" OR ");

                    }
                }
                assigned_ca_variable = " AND (" + stringBuilder + ")";
            }
            Log.d(TAG, "initViews:assigned_ca_variable " + assigned_ca_variable);
        }


        navUserid = headerView.findViewById(R.id.usermobilenumber_new);
        username = headerView.findViewById(R.id.username_new);
        userRole = headerView.findViewById(R.id.userrrole_new);
        navUserid.setText(user_id);
        username.setText(user_name);
        userRole.setText(user_role);
        nv.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.LogoutM:
                    makeLocationHistory();
                    break;
                case R.id.changeLocation:
                    startActivity(new Intent(MapScreen.this, ChooseAreaScreen.class));
                    break;
                case R.id.changePin:
                    startActivity(new Intent(MapScreen.this, ChangePinScreen.class));
                    break;
                case R.id.survey_report:
                         startActivity(new Intent(MapScreen.this, AdminReportSectionScreen.class));
                  //  startActivity(new Intent(MapScreen.this, DTPForwardedPointScreen.class));
                    break;
                case R.id.offline_new_record:
                    startActivity(new Intent(MapScreen.this, OfflineRecordScreen.class));
                    break;
                    case R.id.forwarded_point:
                    startActivity(new Intent(MapScreen.this, DTPForwardedPointScreen.class));
                    break;
                case R.id.offline_verified_record:
                    startActivity(new Intent(MapScreen.this, VerifiedPointOfflineScreen.class));
                    break;
                case R.id.language:
                    showDialogForSelectLanguage();
                    break;
                case R.id.pointRange:
                    showDialogForSelectPointRange();
                    break;
                case R.id.controlledArea:
                    showDialogForSelectControlledArea();
                    break;
                case R.id.nav_topo:
                    arcGISMap.setBasemap(Basemap.createTopographic());
                    break;
                case R.id.street:
                    arcGISMap.setBasemap(Basemap.createStreets());
                    break;
                case R.id.openstreetmap:
                    arcGISMap.setBasemap(Basemap.createOceans());
                    break;
                case R.id.imagery:
                    arcGISMap.setBasemap(Basemap.createImagery());
                    break;
                case R.id.about_us:
                    startActivity(new Intent(MapScreen.this, SurveyDashboardScreen.class));
                    break;
                case R.id.rate_us:
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(myAppLinkToMarket);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, "unable to find market app", Toast.LENGTH_LONG).show();
                    }
            }
            dl.closeDrawers();
            return true;

        });
        //Show Menu According to Role ID
//        MenuItem survey_report = nv.getMenu().findItem(R.id.survey_report);
//        survey_report.setVisible(Sp.read_shared_pref(this, "userAccess").equalsIgnoreCase("Admin"));


        MenuItem survey_report = nv.getMenu().findItem(R.id.survey_report);
        String userAccessPref = Sp.read_shared_pref(this, "userAccess");

        if (userAccessPref != null && userAccessPref.equalsIgnoreCase("Admin")) {
            survey_report.setVisible(true);
        } else {
            survey_report.setVisible(false);
        }


//        if (Sp.read_shared_pref(this, "roleId").equalsIgnoreCase("Citizen"))
        String roleId = Sp.read_shared_pref(this, "roleId");
        if (roleId != null && roleId.equalsIgnoreCase("Citizen"))
        {
            dateLinearLayout.setVisibility(View.GONE);
            showLoginButton();
        } else {
            dateLinearLayout.setVisibility(View.VISIBLE);
        }

//        if (Sp.read_shared_pref(this, "userAccess").equalsIgnoreCase("Viewer"))
            String userAccess = Sp.read_shared_pref(this, "userAccess");
        if (userAccess != null && userAccess.equalsIgnoreCase("Viewer")) {
            add_new_point_fab_button.setVisibility(View.GONE);
            add_new_point_text.setVisibility(View.GONE);
        } else {
            add_new_point_fab_button.setVisibility(View.VISIBLE);
            add_new_point_text.setVisibility(View.VISIBLE);
        }
        work_on_area();
        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);


        end_date.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(MapScreen.this, (view, year1, month1, dayOfMonth) -> end_date.setText(year1 + "-" + convertDate((month1 + 1)) + "-" + convertDate(dayOfMonth)), year, month, day);
            datePicker.show();

        });

        start_date.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(MapScreen.this, (view, year1, month1, dayOfMonth) -> start_date.setText(year1 + "-" + convertDate((month1 + 1)) + "-" + convertDate(dayOfMonth)), year, month, day);
            datePicker.show();

        });


        resetDateView.setOnClickListener(v -> {
            start_date.getText().clear();
            end_date.getText().clear();

            cycleStartDateArrayList.clear();
            cycleEndDateArrayList.clear();
            cycleEndDate = "";
            cycleStartDate = "";

            date_variable = "";
            //     featureLayer.setDefinitionExpression("1=1");
            arcGISMap.getOperationalLayers().remove(featureLayer);
            set_up_all_data();
        });


        search_data_via_date.setOnClickListener(view -> {
            if (start_date.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.select_start_date), Toast.LENGTH_SHORT).show();
            } else {
                set_up_all_data();
            }
        });


        isAllFabsVisible = false;
        add_fab.shrink();

        live_location_fab_button.hide();
        add_new_point_fab_button.hide();
        forward_point_floating_button.hide();
        add_new_point_text.setVisibility(View.GONE);
        forward_floating_text.setVisibility(View.GONE);
        live_location_fab_text.setVisibility(View.GONE);


        add_fab.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                live_location_fab_button.show();
                add_new_point_fab_button.show();
                forward_point_floating_button.show();
                add_new_point_text.setVisibility(View.VISIBLE);
                forward_floating_text.setVisibility(View.VISIBLE);
                live_location_fab_text.setVisibility(View.VISIBLE);
                add_fab.extend();
                isAllFabsVisible = true;
            } else {
                live_location_fab_button.hide();
                add_new_point_fab_button.hide();
                forward_point_floating_button.hide();
                add_new_point_text.setVisibility(View.GONE);
                forward_floating_text.setVisibility(View.GONE);
                live_location_fab_text.setVisibility(View.GONE);
                add_fab.shrink();
                isAllFabsVisible = false;
            }
        });


        forward_point_floating_button.setOnClickListener(view -> {
            startActivity(new Intent(MapScreen.this, AdminReadyToDemolishScreen.class));


        });

        bufferRangeView.setOnClickListener(v -> {
            if (bufferRangeLinearLayout.getVisibility() == View.VISIBLE) {
                bufferRangeLinearLayout.setVisibility(View.GONE);
                bufferRangeView.setRotation(0);
            } else if (bufferRangeLinearLayout.getVisibility() == View.GONE) {
                bufferRangeLinearLayout.setVisibility(View.VISIBLE);
                bufferRangeView.setRotation(180);
            }
        });

        layerListTextViewLayout.setOnClickListener(v -> {
            if (layerListLayout.getVisibility() == View.VISIBLE) {
                layerListLayout.setVisibility(View.GONE);
                layerShowImageView.setRotation(0);
            } else if (layerListLayout.getVisibility() == View.GONE) {
                layerListLayout.setVisibility(View.VISIBLE);
                layerShowImageView.setRotation(180);
            }
        });
        RegularColonyCheckBox.setOnClickListener(v -> {
            if (RegularColonyCheckBox.isChecked()) {
                ServiceFeatureTable RegularColonyServiceTable = new ServiceFeatureTable(regularized_colony_boundary);
                RegularColonyServiceTable.loadAsync();
                RegularColonyFeatureLayer = new FeatureLayer(RegularColonyServiceTable);
                arcGISMap.getOperationalLayers().add(RegularColonyFeatureLayer);
            } else {
                arcGISMap.getOperationalLayers().remove(RegularColonyFeatureLayer);
            }
        });
        clu_boundary_check_box.setOnClickListener(v -> {
            if (clu_boundary_check_box.isChecked()) {
                ServiceFeatureTable clu_boundary_service_table = new ServiceFeatureTable(clu_boundary);
                clu_boundary_service_table.loadAsync();
                featureLayerCluBoundary = new FeatureLayer(clu_boundary_service_table);
                arcGISMap.getOperationalLayers().add(featureLayerCluBoundary);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerCluBoundary);
            }
        });
        mandi_township_check_box.setOnClickListener(v -> {
            if (mandi_township_check_box.isChecked()) {
                ServiceFeatureTable mandi_township_service_table = new ServiceFeatureTable(mandi_township);
                mandi_township_service_table.loadAsync();
                featureLayerMandiTownShip = new FeatureLayer(mandi_township_service_table);
                arcGISMap.getOperationalLayers().add(featureLayerMandiTownShip);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerMandiTownShip);
            }
        });

        habitationLayerCheckBox.setOnClickListener(v -> {
            if (habitationLayerCheckBox.isChecked()) {
                ServiceFeatureTable habitationServiceTable = new ServiceFeatureTable(habitation);
                habitationServiceTable.loadAsync();
                featureLayerhabitation = new FeatureLayer(habitationServiceTable);
                featureLayerhabitation.setDefinitionExpression(districtCodeConditions);
                arcGISMap.getOperationalLayers().add(featureLayerhabitation);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerhabitation);
            }
        });

        caCheckBox.setOnClickListener(v -> {
            if (caCheckBox.isChecked()) {
                arcGISMap.getOperationalLayers().remove(featureLayerBoundaryTwo);
                featureLayerBoundaryTwo.setDefinitionExpression(districtCodeConditions);
                arcGISMap.getOperationalLayers().add(featureLayerBoundaryTwo);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerBoundaryTwo);
            }
        });


        devCheckBox.setOnClickListener(v -> {
            if (devCheckBox.isChecked()) {
                ServiceFeatureTable developmentPlanBoundaryServiceTable = new ServiceFeatureTable(feature_service_url_development);
                developmentPlanBoundaryServiceTable.loadAsync();
                featureLayerDevelopmentPlan = new FeatureLayer(developmentPlanBoundaryServiceTable);
                arcGISMap.getOperationalLayers().add(featureLayerDevelopmentPlan);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerDevelopmentPlan);
            }
        });

        urbanCheckBox.setOnClickListener(v -> {
            if (urbanCheckBox.isChecked()) {
                ServiceFeatureTable boundaryServiceTableUrban = new ServiceFeatureTable(feature_service_urban_area_boundary);
                boundaryServiceTableUrban.loadAsync();
                featureLayerBoundaryUrban = new FeatureLayer(boundaryServiceTableUrban);
                featureLayerBoundaryUrban.setDefinitionExpression(districtCodeConditions);
                arcGISMap.getOperationalLayers().add(featureLayerBoundaryUrban);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerBoundaryUrban);
            }
        });


        hsvp_haryana_CheckBox.setOnClickListener(v -> {
            if (hsvp_haryana_CheckBox.isChecked()) {
                ServiceFeatureTable hsvpServiceTable = new ServiceFeatureTable(hsvp_haryana);
                hsvpServiceTable.loadAsync();
                featureLayerHSVP = new FeatureLayer(hsvpServiceTable);
                arcGISMap.getOperationalLayers().add(featureLayerHSVP);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerHSVP);
            }
        });

        hsidc_sites_CheckBox.setOnClickListener(v -> {
            if (hsidc_sites_CheckBox.isChecked()) {
                ServiceFeatureTable hsidcServiceTable = new ServiceFeatureTable(hsidc_sites);
                hsidcServiceTable.loadAsync();
                featureLayerHSIDC = new FeatureLayer(hsidcServiceTable);
                arcGISMap.getOperationalLayers().add(featureLayerHSIDC);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerHSIDC);
            }
        });


        licensedColonyCheckBox.setOnClickListener(v -> {
            if (licensedColonyCheckBox.isChecked()) {
                featureLayerBoundaryLicensedColony.setDefinitionExpression(districtCodeConditions);
                arcGISMap.getOperationalLayers().remove(featureLayerBoundaryLicensedColony);
                arcGISMap.getOperationalLayers().add(featureLayerBoundaryLicensedColony);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerBoundaryLicensedColony);
            }
        });


        cadCheckBox.setOnClickListener(v -> {
            if (cadCheckBox.isChecked()) {
                arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(vector_tile_url);
                arcGISMap.getOperationalLayers().add(arcGISVectorTiledLayer);
            } else {
                arcGISMap.getOperationalLayers().remove(arcGISVectorTiledLayer);
            }
        });
        pointCheckBox.setOnClickListener(v -> {
            if (pointCheckBox.isChecked()) {
                arcGISMap.getOperationalLayers().remove(featureLayer);
                arcGISMap.getOperationalLayers().add(featureLayer);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayer);
            }
        });
        polygonCheckBox.setOnClickListener(v -> {
            if (polygonCheckBox.isChecked()) {
                Log.d(TAG, "assigned_polygon_variable: " + assigned_polygon_variable);
                featureLayerPolygonService.setDefinitionExpression(assigned_polygon_variable);
                arcGISMap.getOperationalLayers().add(featureLayerPolygonService);
            } else {
                arcGISMap.getOperationalLayers().remove(featureLayerPolygonService);
            }
        });


        generate_and_save_token();
    }

    private void setUpMonthSpinner() {
        monthArrayList.clear();
        monthArrayListCode.clear();
        monthArrayList.add("Select Month");
        monthArrayList.add("January");
        monthArrayList.add("February");
        monthArrayList.add("March");
        monthArrayList.add("April");
        monthArrayList.add("May");
        monthArrayList.add("June");
        monthArrayList.add("July");
        monthArrayList.add("August");
        monthArrayList.add("September");
        monthArrayList.add("October");
        monthArrayList.add("November");
        monthArrayList.add("December");
        monthArrayListCode.add("00");
        monthArrayListCode.add("01");
        monthArrayListCode.add("02");
        monthArrayListCode.add("03");
        monthArrayListCode.add("04");
        monthArrayListCode.add("05");
        monthArrayListCode.add("06");
        monthArrayListCode.add("07");
        monthArrayListCode.add("08");
        monthArrayListCode.add("09");
        monthArrayListCode.add("10");
        monthArrayListCode.add("11");
        monthArrayListCode.add("12");

        ArrayAdapter monthAdapter = new ArrayAdapter(MapScreen.this, R.layout.spinner_text_view, monthArrayList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month_spinner.setAdapter(monthAdapter);

    }

    public void resetFunction() {
        start_date.getText().clear();
        end_date.getText().clear();
        cycleStartDateArrayList.clear();
        cycleEndDateArrayList.clear();
        yearList.clear();
        monthArrayListCode.clear();
        monthArrayList.clear();
        cycleEndDate = "";
        cycleStartDate = "";
        set_up_all_data();
    }

    private void hitQueryOnLayerByCycleStartDate(String cycleStartDate) {
        cycleEndDateArrayList.clear();
        cycleEndDateArrayList.add("Select Cycle End Date");
        progress_bar_data_loading.setVisibility(View.VISIBLE);
        Log.d(TAG, "hitQueryOnLayerByCycleStartDate: " + cycleStartDate);
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(MapScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getCycleEndDate(cycleStartDate, coming_dis_code);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject obj = new JSONObject(result);
                        Log.d(TAG, "onResponse:cycleStartDate " + obj);
                        boolean status = obj.optBoolean("status");
                        String message = obj.optString("message");
                        if (status) {
                            if (message.equalsIgnoreCase("Success")) {
                                JSONArray jsonArray = obj.optJSONArray("data");
                                progress_bar_data_loading.setVisibility(View.GONE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                                    String dateValue = jsonObject.optString("cycle_end_date");
                                    String[] splitValue = dateValue.split("T");
                                    if (!cycleEndDateArrayList.contains(splitValue[0])) {
                                        cycleEndDateArrayList.add(splitValue[0]);
                                    }
                                    set_up_cycle_end_date_spinner();
                                }

                            }
                        } else {
                            progress_bar_data_loading.setVisibility(View.GONE);
                            Toast.makeText(MapScreen.this, message, Toast.LENGTH_SHORT).show();
                            if (BuildConfig.DEBUG) Log.d(TAG, "onResponse: " + message);

                        }


                    } catch (Exception e) {
                        progress_bar_data_loading.setVisibility(View.GONE);
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage());
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progress_bar_data_loading.setVisibility(View.GONE);
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progress_bar_data_loading.setVisibility(View.GONE);
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress_bar_data_loading.setVisibility(View.GONE);
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

    private void set_up_cycle_end_date_spinner() {
        ArrayAdapter cycleStartEndAdapter = new ArrayAdapter(MapScreen.this, R.layout.spinner_text_view, cycleEndDateArrayList);
        cycleStartEndAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cycle_end_date_spinner.setAdapter(cycleStartEndAdapter);
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }


    private void hitApiToFetchCycleStartDate(String cycleYear, String cycleMonth) {
        cycleStartDateArrayList.clear();
        cycleStartDateArrayList.add("Select Cycle Start Date");
        progress_bar_data_loading.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(MapScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getCycleStartDate(cycleMonth, cycleYear, coming_dis_code);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject obj = new JSONObject(result);

                        boolean status = obj.optBoolean("status");
                        String message = obj.optString("message");
                        if (status) {
                            if (message.equalsIgnoreCase("Success")) {
                                JSONArray jsonArray = obj.optJSONArray("data");
                                progress_bar_data_loading.setVisibility(View.GONE);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                                    String dateValue = jsonObject.optString("cycle_start_date");
                                    String[] splitValue = dateValue.split("T");
                                    if (!cycleStartDateArrayList.contains(splitValue[0])) {
                                        cycleStartDateArrayList.add(splitValue[0]);
                                    }
                                    set_up_cycler_start_date_spinner();
                                }
                            }
                        } else {
                            progress_bar_data_loading.setVisibility(View.GONE);
                            Toast.makeText(MapScreen.this, message, Toast.LENGTH_SHORT).show();
                            if (BuildConfig.DEBUG) Log.d(TAG, "onResponse: " + message);

                        }


                    } catch (Exception e) {
                        progress_bar_data_loading.setVisibility(View.GONE);
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage());
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    progress_bar_data_loading.setVisibility(View.GONE);
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    progress_bar_data_loading.setVisibility(View.GONE);
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress_bar_data_loading.setVisibility(View.GONE);
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

    private void set_up_cycler_start_date_spinner() {
        ArrayAdapter cycleStartDateAdapter = new ArrayAdapter(MapScreen.this, R.layout.spinner_text_view, cycleStartDateArrayList);
        cycleStartDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cycle_start_date_spinner.setAdapter(cycleStartDateAdapter);

    }

    private void setUpYearSpinner() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearList.add("Select Year");

        for (int year = currentYear; year >= currentYear - 25; year--) {
            yearList.add(String.valueOf(year));
        }

        ArrayAdapter adapter = new ArrayAdapter(MapScreen.this, R.layout.spinner_text_view, yearList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(adapter);


    }

    private void work_on_area() {

        role.setText(Sp.read_shared_pref(MapScreen.this, "userAccess"));
        coming_dis_code = Sp.read_shared_pref(MapScreen.this, "dis_code_store");
        coming_dis_name = Sp.read_shared_pref(MapScreen.this, "dis_code_name");
        coming_tehsil_code = Sp.read_shared_pref(MapScreen.this, "teh_code_store");
        coming_village_code = Sp.read_shared_pref(MapScreen.this, "village_code_store");
        coming_tehsil_code_name = Sp.read_shared_pref(MapScreen.this, "teh_code_store_name");
        coming_village_code_name = Sp.read_shared_pref(MapScreen.this, "village_code_store_name");

        if (!coming_tehsil_code_name.equals("")) {
            if (!coming_village_code_name.equals("")) {
                bottom_text.setText(coming_dis_name + "," + coming_tehsil_code_name + "," + coming_village_code_name);
            } else {
                bottom_text.setText(coming_dis_name + "," + coming_tehsil_code_name);
            }
        } else {
            bottom_text.setText(getString(R.string.current_district) + coming_dis_name);
        }

        geofenceRangeView.setText(getString(R.string.in_your_geofence_range) + ": " + Sp.read_shared_pref(this, "buffer_size_needed") + "m");
        get_all_layer_by_api(coming_dis_code);
        getControlledArea();

    }

    public void resetButton() {
        if (start_date.getText().toString().isEmpty() && end_date.getText().toString().isEmpty()) {
            resetDateView.setVisibility(View.GONE);
        } else {
            resetDateView.setVisibility(View.VISIBLE);
        }
    }

    public String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + input;
        }

    }

    private void set_up_all_data() {
        if (start_date.getText().toString().isEmpty() && end_date.getText().toString().isEmpty() && cycleStartDate.isEmpty() && cycleEndDate.isEmpty()) {
            Log.d(TAG, "set_up_all_data: " + start_date.getText().toString());
            Log.d(TAG, "set_up_all_data: " + end_date.getText().toString());
            if (coming_dis_code != null && coming_tehsil_code != null && coming_village_code != null) {
                String where = "n_v_code = '" + coming_village_code + "'" + inside_ca_variable + inside_dp_variable + inside_urban_area_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                featureLayer.setDefinitionExpression(where);
                showPolygon(where);
            } else if (coming_dis_code != null && coming_tehsil_code != null) {
                String where = "n_t_code = '" + coming_tehsil_code + "'" + inside_ca_variable + inside_dp_variable + inside_urban_area_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                Log.d(TAG, "set_up_all_data: " + where);
                featureLayer.setDefinitionExpression(where);
                showPolygon(where);
            } else if (coming_dis_code != null) {
                String where = "n_d_code = '" + coming_dis_code + "'" + inside_ca_variable + inside_dp_variable + inside_urban_area_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                featureLayer.setDefinitionExpression(where);
                Log.d(TAG, "set_up_all_data: " + where);
                showPolygon(where);
            }
        } else if (!start_date.getText().toString().isEmpty() && end_date.getText().toString().isEmpty() && cycleStartDate.isEmpty() && cycleEndDate.isEmpty()) {
            date_variable = " AND startDate >= date '" + start_date.getText().toString() + " 00:00:00'";

            if (coming_dis_code != null && coming_tehsil_code != null && coming_village_code != null) {
                String where = "n_v_code ='" + coming_village_code + "'" + date_variable + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                featureLayer.setDefinitionExpression(where);
                showPolygon("n_v_code ='" + coming_village_code + "'" + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable);
            } else if (coming_dis_code != null && coming_tehsil_code != null) {
                String where = "n_t_code ='" + coming_tehsil_code + "'" + date_variable + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                featureLayer.setDefinitionExpression(where);
                showPolygon("n_t_code ='" + coming_tehsil_code + "'" + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable);
            } else if (coming_dis_code != null) {
                String where = "n_d_code ='" + coming_dis_code + "'" + date_variable + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                featureLayer.setDefinitionExpression(where);
                showPolygon("n_d_code ='" + coming_dis_code + "'" + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable);
            }
        } else if (!start_date.getText().toString().isEmpty() && !end_date.getText().toString().isEmpty() && cycleStartDate.isEmpty() && cycleEndDate.isEmpty()) {

            date_variable = " AND startDate BETWEEN date '" + start_date.getText().toString() + " 00:00:00' AND date '" + end_date.getText().toString() + " 23:59:00" + "'";
            if (coming_dis_code != null && coming_tehsil_code != null && coming_village_code != null) {
                String where = "n_v_code ='" + coming_village_code + "'" + date_variable + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                featureLayer.setDefinitionExpression(where);
                showPolygon("n_v_code ='" + coming_village_code + "'" + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable);
            } else if (coming_dis_code != null && coming_tehsil_code != null) {
                String where = "n_t_code ='" + coming_tehsil_code + "'" + date_variable + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                featureLayer.setDefinitionExpression(where);
                showPolygon("n_t_code ='" + coming_tehsil_code + "'" + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable);
            } else if (coming_dis_code != null) {
                String where = "n_d_code ='" + coming_dis_code + "'" + date_variable + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                showPolygon("n_d_code ='" + coming_dis_code + "'" + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable);
                featureLayer.setDefinitionExpression(where);
            }
        } else if (!cycleStartDate.isEmpty() && !cycleEndDate.isEmpty()) {
            Log.d(TAG, "set_up_all_data:cycleStartDate " + cycleStartDate);
            Log.d(TAG, "set_up_all_data:cycleEndDate " + cycleEndDate);
            date_variable = " AND cycle_start_date =date '" + cycleStartDate + "' AND cycle_end_date =date '" + cycleEndDate + "'";
            if (coming_dis_code != null && coming_tehsil_code != null && coming_village_code != null) {
                String where = "n_v_code ='" + coming_village_code + "'" + date_variable + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                featureLayer.setDefinitionExpression(where);
                showPolygon("n_v_code ='" + coming_village_code + "'" + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable);
            } else if (coming_dis_code != null && coming_tehsil_code != null) {
                String where = "n_t_code ='" + coming_tehsil_code + "'" + date_variable + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                featureLayer.setDefinitionExpression(where);
                Log.d(TAG, "set_up_all_data:where " + where);
                showPolygon("n_t_code ='" + coming_tehsil_code + "'" + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable);
            } else if (coming_dis_code != null) {
                String where = "n_d_code ='" + coming_dis_code + "'" + date_variable + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable;
                Log.d(TAG, "set_up_all_data:where " + where);
                showPolygon("n_d_code ='" + coming_dis_code + "'" + inside_ca_variable + inside_urban_area_variable + inside_dp_variable + not_shown_variable + ca_variable + assigned_ca_variable + quality_control_variable);
                Log.d(TAG, "set_up_all_data:where " + where);
                featureLayer.setDefinitionExpression(where);
            }
        }

        if (start_date.getText().toString().isEmpty() && cycleStartDate.isEmpty()) {
            arcGISMap.getOperationalLayers().remove(featureLayer);
            arcGISMap.getOperationalLayers().add(featureLayer);
        } else if (!start_date.getText().toString().isEmpty() || !end_date.getText().toString().isEmpty() || !cycleStartDate.isEmpty() || !cycleEndDate.isEmpty()) {
            arcGISMap.getOperationalLayers().remove(featureLayer);
            arcGISMap.getOperationalLayers().add(featureLayer);
        }


        datasourcestatuschangedlistener();
        locationchangedmethod();
    }


    private void generate_and_save_token() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String fcm_token = task.getResult();
                    if (BuildConfig.DEBUG) {
                        Log.d("token ", "onComplete: " + fcm_token);
                    }
                    save_token_in_db(fcm_token);
                }
            }
        });

    }


    private void save_token_in_db(String fcm_token) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(MapScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.updateTokenInDb(Sp.read_shared_pref(MapScreen.this, "user_mobile"), fcm_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject obj = new JSONObject(result);

                        boolean status = obj.optBoolean("status");
                        String message = obj.optString("message");
                        if (status) {
                            if (message.equalsIgnoreCase("Sucess")) {
                                Log.d("TAG", "onResponse: token Saved Successfully ");
                            }
                        } else {
                            //  Toast.makeText(MapScreen.this, "SomeThing went wrong", Toast.LENGTH_SHORT).show();
                            if (BuildConfig.DEBUG) Log.d(TAG, "onResponse: " + message);

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

    private void onFailed2(String s, String s1) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    // add a listener to detect taps on the map view

    private void loadFeatureMap() {
        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mapView) {
            @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // remove any existing callouts
                if (mCallOut.isShowing()) {
                    mCallOut.dismiss();
                }
                // get the point that was clicked and convert it to a point in map coordinates
                final Point screenPoint = new Point(Math.round(e.getX()), Math.round(e.getY()));
                SpatialReference wgsSR = SpatialReference.create(4326);

                com.esri.arcgisruntime.geometry.Point webPoint = (com.esri.arcgisruntime.geometry.Point) GeometryEngine.project(mapView.screenToLocation(screenPoint), wgsSR);

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "onSingleTapConfirmed:screenPoint " + screenPoint + " \nwebPoint   " + webPoint + " \nbufferGeom   " + bufferGeom);

                if (bufferGeom == null) {
                    Toast.makeText(MapScreen.this, getString(R.string.wait_until_access_your_live_location), Toast.LENGTH_SHORT).show();
                } else {

                    boolean isInField = GeometryEngine.contains(bufferGeom, webPoint);

                    if (isInField) {

                        x1 = webPoint.getX();
                        y1 = webPoint.getY();

                        on_map_fab_button.setVisibility(View.VISIBLE);
                        on_map_fab_button.startAnimation(shake);
                        on_map_fab_button.setOnClickListener(v -> {
                            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", y1, x1, "Destination");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        });

                        final ListenableFuture<IdentifyLayerResult> identifyLayerResultListenableFuture = mMapView.identifyLayerAsync(featureLayer, screenPoint, 8, false, 1);
                        identifyLayerResultListenableFuture.addDoneListener(() -> {
                            try {
                                IdentifyLayerResult identifyLayerResult = identifyLayerResultListenableFuture.get();
                                for (GeoElement element : identifyLayerResult.getElements()) {
                                    Feature feature = (Feature) element;
                                    Map<String, Object> attr = feature.getAttributes();
                                    Object str = attr.get("verified");

                                    if (Objects.equals(str, "Y") || Objects.equals(str, "verified")) {
                                        customProgressDialog = new ProgressDialog(MapScreen.this);
                                        customProgressDialog.setTitle("Loading");
                                        customProgressDialog.setMessage("Wait While Loading");
                                        customProgressDialog.setCancelable(false);
                                        customProgressDialog.show();

                                        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(MapScreen.this).create(ApiInterface.class);
                                        Call<ResponseBody> call = apiInterface.getUidData(attr.get("UID").toString(), Sp.read_shared_pref(MapScreen.this, "login_with"));
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    try {
                                                        String result = response.body().string();
                                                        JSONObject jsonObject = new JSONObject(result);
                                                        String message = jsonObject.getString("message");
                                                        boolean status = jsonObject.optBoolean("status");

                                                        if (message.equalsIgnoreCase("Success")) {
                                                            if (status) {
                                                                customProgressDialog.dismiss();
                                                                JSONArray result_array = jsonObject.getJSONArray("data");
                                                                if (result_array.length() > 0) {
                                                                    JSONObject jsonObjectNew = result_array.getJSONObject(0);
                                                                    crop_detail_dialog = new Dialog(MapScreen.this);
                                                                    crop_detail_dialog.setContentView(R.layout.show_crop_infromation);
                                                                    TextView gisId_textview = crop_detail_dialog.findViewById(R.id.gisId);
                                                                    TextView n_d_name = crop_detail_dialog.findViewById(R.id.n_d_name);
                                                                    ImageView edit_crop_image = crop_detail_dialog.findViewById(R.id.edit_crop_image);


                                                                    if (Sp.read_shared_pref(MapScreen.this, "Designation").equalsIgnoreCase("Citizen") || Sp.read_shared_pref(MapScreen.this, "userAccess").equalsIgnoreCase("Viewer")) {
                                                                        edit_crop_image.setVisibility(View.GONE);
                                                                    } else {
                                                                        edit_crop_image.setVisibility(View.VISIBLE);
                                                                    }

                                                                    TextView n_t_name = crop_detail_dialog.findViewById(R.id.n_t_name);
                                                                    TextView n_v_code_tv = crop_detail_dialog.findViewById(R.id.n_v_code_tv);
                                                                    TextView khasra_murba_number_tv = crop_detail_dialog.findViewById(R.id.khasra_murba_number_tv);
                                                                    TextView crop_name_tv = crop_detail_dialog.findViewById(R.id.crop_name_tv);
                                                                    TextView crop_user_name = crop_detail_dialog.findViewById(R.id.crop_user_name);
                                                                    TextView point_source = crop_detail_dialog.findViewById(R.id.point_source);
                                                                    ImageView image_view_for_crop = crop_detail_dialog.findViewById(R.id.image_view_for_crop);
                                                                    ImageView image_view_for_crop2 = crop_detail_dialog.findViewById(R.id.image_view_for_crop2);
                                                                    ImageView image_view_for_crop3 = crop_detail_dialog.findViewById(R.id.image_view_for_crop3);
                                                                    ImageView image_view_for_crop4 = crop_detail_dialog.findViewById(R.id.image_view_for_crop4);

                                                                    n_d_name.setText(getString(R.string.district_) + jsonObjectNew.optString("n_d_name"));
                                                                    n_t_name.setText(getString(R.string.tehsil_) + jsonObjectNew.optString("n_t_name"));
                                                                    n_v_code_tv.setText(getString(R.string.village_) + jsonObjectNew.optString("n_v_name"));
                                                                    crop_name_tv.setText(getString(R.string.verified_) + jsonObjectNew.optString("verified"));
                                                                    point_source.setText(getString(R.string.point_source) + jsonObjectNew.optString("pointSource"));
                                                                    khasra_murba_number_tv.setText(getString(R.string.murabba_khasra_) + jsonObjectNew.optString("n_murr_no") + "//" + jsonObjectNew.optString("n_khas_no"));
                                                                    crop_user_name.setText(getString(R.string.verified_by_) + jsonObjectNew.optString("user_name"));

                                                                    String uploadimage = jsonObjectNew.optString("verifyImg1");
                                                                    String uploadimage1 = jsonObjectNew.optString("verifyImg2");
                                                                    String uploadimage2 = jsonObjectNew.optString("verifyImg3");
                                                                    String uploadimage3 = jsonObjectNew.optString("verifyImg4");

                                                                    if (!Objects.equals("null", uploadimage)) {
                                                                        byte[] decodedString = Base64.decode(uploadimage, Base64.DEFAULT);
                                                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                                                        image_view_for_crop.setImageBitmap(decodedByte);
                                                                    }
                                                                    if (!Objects.equals("null", uploadimage1)) {
                                                                        byte[] decodedString2 = Base64.decode(uploadimage1, Base64.DEFAULT);
                                                                        Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
                                                                        image_view_for_crop2.setImageBitmap(decodedByte2);
                                                                    }
                                                                    if (!Objects.equals("null", uploadimage2)) {
                                                                        byte[] decodedString3 = Base64.decode(uploadimage2, Base64.DEFAULT);
                                                                        Bitmap decodedByte3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
                                                                        image_view_for_crop3.setImageBitmap(decodedByte3);
                                                                    }
                                                                    if (!Objects.equals("null", uploadimage3)) {
                                                                        byte[] decodedString4 = Base64.decode(uploadimage3, Base64.DEFAULT);
                                                                        Bitmap decodedByte4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.length);
                                                                        image_view_for_crop4.setImageBitmap(decodedByte4);
                                                                    }
                                                                    image_view_for_crop.setOnClickListener(view -> {
                                                                        Intent intent = new Intent(MapScreen.this, SimpleImageScreen.class);
                                                                        intent.putExtra("image_url", uploadimage);
                                                                        startActivity(intent);
                                                                    });
                                                                    image_view_for_crop2.setOnClickListener(view -> {
                                                                        Intent intent = new Intent(MapScreen.this, SimpleImageScreen.class);
                                                                        intent.putExtra("image_url", uploadimage1);
                                                                        startActivity(intent);
                                                                    });
                                                                    image_view_for_crop3.setOnClickListener(view -> {
                                                                        Intent intent = new Intent(MapScreen.this, SimpleImageScreen.class);
                                                                        intent.putExtra("image_url", uploadimage2);
                                                                        startActivity(intent);
                                                                    });
                                                                    image_view_for_crop4.setOnClickListener(view -> {
                                                                        Intent intent = new Intent(MapScreen.this, SimpleImageScreen.class);
                                                                        intent.putExtra("image_url", uploadimage3);
                                                                        startActivity(intent);
                                                                    });
                                                                    crop_detail_dialog.show();
                                                                    edit_crop_image.setOnClickListener(view -> {
                                                                        crop_detail_dialog.dismiss();
                                                                        N_MURR_NO.add(jsonObjectNew.optString("n_murr_no"));
                                                                        N_KHAS_NO.add(jsonObjectNew.optString("n_khas_no"));
                                                                        OBJECT_ID.add(jsonObjectNew.optString("OBJECTID"));
                                                                        gisId.add(jsonObjectNew.optString("gisId"));
                                                                        latitude_array_list.add(jsonObjectNew.optString("latitude"));
                                                                        longitude_array_list.add(jsonObjectNew.optString("longitude"));
                                                                        selectedDisName = jsonObjectNew.optString("n_d_name");
                                                                        selectedTehsilName = jsonObjectNew.optString("n_t_name");
                                                                        selectedVillageName = jsonObjectNew.optString("n_v_name");
                                                                        if (!Objects.equals("null", jsonObjectNew.optString("ca_name"))) {
                                                                            ca_name = jsonObjectNew.optString("ca_name");
                                                                            ca_key = jsonObjectNew.optString("CA_Key_GIS");
                                                                        }
                                                                        if (!Objects.equals("null", jsonObjectNew.optString("dev_plan"))) {
                                                                            dev_plan = jsonObjectNew.optString("dev_plan");
                                                                        }
                                                                        n_v_code = jsonObjectNew.optString("n_v_code").toString();
                                                                        n_t_code = jsonObjectNew.optString("n_t_code").toString();
                                                                        n_d_code = jsonObjectNew.optString("n_d_code").toString();
                                                                        uid = jsonObjectNew.optString("UID").toString();


                                                                        if (!connection_detector.isConnected()) {
                                                                            Toast.makeText(MapScreen.this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            Intent intent = new Intent(MapScreen.this, MapChangeBoundaryScreen.class);
                                                                            intent.putExtra("point", "editPoint");
                                                                            intent.putExtra("latitude", attr.get("latitude").toString());
                                                                            intent.putExtra("longitude", attr.get("longitude").toString());
                                                                            intent.putExtra("n_d_name", selectedDisName);
                                                                            intent.putExtra("n_t_name", selectedTehsilName);
                                                                            intent.putExtra("n_v_name", selectedVillageName);
                                                                            intent.putExtra("objectId", OBJECT_ID.get(0));
                                                                            intent.putExtra("gisId", gisId.get(0));
                                                                            intent.putExtra("murabba", N_MURR_NO.get(0));
                                                                            intent.putExtra("khasra_no", N_KHAS_NO.get(0));
                                                                            intent.putExtra("uid", uid);
                                                                            intent.putExtra("n_d_code", n_d_code);
                                                                            intent.putExtra("n_t_code", n_t_code);
                                                                            intent.putExtra("n_v_code", n_v_code);
                                                                            intent.putExtra("ca_name", ca_name);
                                                                            intent.putExtra("dev_plan", dev_plan);
                                                                            intent.putExtra("ca_key", ca_key);
                                                                            intent.putExtra("vector_tile_url", vector_tile_url);
                                                                            intent.putExtra("feature_service_url", feature_service_url);
                                                                            intent.putExtra("feature_service_url_development", feature_service_url_development);
                                                                            intent.putExtra("feature_service_url_area_boundary", feature_service_url_area_boundary);
                                                                            intent.putExtra("polygon_service", polygon_service);
                                                                            startActivity(intent);
                                                                            clearArrayListAll();
                                                                            graphicsOverlay.getGraphics().clear();
                                                                            //  show_dialog_and_submit_data();
                                                                        }
                                                                    });
                                                                }
                                                            } else {
                                                                Toast.makeText(MapScreen.this, message, Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(MapScreen.this, message, Toast.LENGTH_SHORT).show();

                                                        }


                                                    } catch (Exception e) {
                                                        customProgressDialog.dismiss();
                                                        e.printStackTrace();
                                                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                                                    }


                                                } else if (response.code() == 404) {
                                                    customProgressDialog.dismiss();
                                                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                                                } else {
                                                    customProgressDialog.dismiss();
                                                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                                                }


                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                customProgressDialog.dismiss();
                                                if (t.getMessage().startsWith("Unable to resolve host")) {
                                                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                                                } else if (t.getMessage().startsWith("timeout")) {
                                                    onFailed("Slow or No Connection!", "Check Your Network Settings & try again.");


                                                } else {
                                                    onFailed("An unexpected error has occurred.", "Error Failure: " + t.getMessage());


                                                }
                                            }
                                        });
                                    } else {
                                        if (Sp.read_shared_pref(MapScreen.this, "userAccess").equalsIgnoreCase("Viewer")) {
                                            Toast.makeText(MapScreen.this, "No Permission to Edit Point", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (OBJECT_ID.contains(attr.get("OBJECTID").toString())) {
                                                int i = OBJECT_ID.indexOf(attr.get("OBJECTID").toString());
                                                OBJECT_ID.remove(i);
                                                N_KHAS_NO.remove(i);
                                                N_MURR_NO.remove(i);
                                                latitude_array_list.remove(i);
                                                longitude_array_list.remove(i);
                                                graphicsOverlay.getGraphics().remove(i);

                                                UiHelper.showSnackBarLong(MapScreen.this, con_layout, attr.get("n_murr_no").toString() + "//" + attr.get("n_khas_no").toString() + " UnSelected", R.color.red);

                                            } else {
                                                if (OBJECT_ID.size() < 1) {
                                                    selectedDisName = attr.get("n_d_name").toString();
                                                    selectedTehsilName = attr.get("n_t_name").toString();
                                                    selectedVillageName = attr.get("n_v_name").toString();
                                                    if (!Objects.equals("null", attr.get("ca_name"))) {
                                                        ca_name = String.valueOf(attr.get("ca_name"));
                                                        ca_key = String.valueOf(attr.get("CA_Key_GIS"));
                                                    }
                                                    if (!Objects.equals("null", attr.get("dev_plan"))) {
                                                        dev_plan = String.valueOf(attr.get("dev_plan"));
                                                    }
                                                    n_v_code = attr.get("n_v_code").toString();
                                                    n_t_code = attr.get("n_t_code").toString();
                                                    n_d_code = attr.get("n_d_code").toString();
                                                    uid = attr.get("UID").toString();
                                                    N_MURR_NO.add(attr.get("n_murr_no").toString());
                                                    N_KHAS_NO.add(attr.get("n_khas_no").toString());
                                                    OBJECT_ID.add(attr.get("OBJECTID").toString());
                                                    gisId.add(attr.get("gisId").toString());
                                                    latitude_array_list.add(attr.get("latitude").toString());
                                                    longitude_array_list.add(attr.get("longitude").toString());
                                                    Graphic pointGraphics = new Graphic(webPoint, simpleMarkerSymbol);
                                                    graphicsOverlay.getGraphics().add(pointGraphics);
                                                    UiHelper.showSnackBarLong(MapScreen.this, con_layout, attr.get("n_murr_no").toString() + "//" + attr.get("n_khas_no").toString() + " Selected", R.color.appColor);


                                                } else {
                                                    Toast.makeText(MapScreen.this, getString(R.string.point_is_already_selected), Toast.LENGTH_SHORT).show();
                                                }


                                            }

                                            if (OBJECT_ID.size() < 1) {
                                                graphicsOverlay.getGraphics().clear();
                                                floatingActionButton.hide();
                                            } else {
                                                floatingActionButton.show();
                                                floatingActionButton.startAnimation(shake);
                                            }

                                        }
                                    }
                                }
                            } catch (Exception e1) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onSingleTapConfirmed:try catch " + e1.getMessage());
                                }
                                Toast.makeText(MapScreen.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {

                        x1 = webPoint.getX();
                        y1 = webPoint.getY();
                        on_map_fab_button.setVisibility(View.VISIBLE);
                        on_map_fab_button.startAnimation(shake);
                        on_map_fab_button.setOnClickListener(v -> {
                            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", y1, x1, "Destination");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        });

                    }
                }


                return super.onSingleTapConfirmed(e);
            }
        });

    }

    private void clearArrayListAll() {
        latitude_array_list.clear();
        longitude_array_list.clear();
        N_MURR_NO.clear();
        N_KHAS_NO.clear();
        OBJECT_ID.clear();
        gisId.clear();
        img_path_1 = "";

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        assistant.onActivityResult(requestCode, resultCode);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
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

                            convertedImage = getResizedBitmap(bmp, 500);
                            convertedImage.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(Sp.read_shared_pref(MapScreen.this, "image_quality")), stream);

                            cameraImage.setImageBitmap(convertedImage);

                            imageFile = new File(getRealPathFromURI(photoUri));
                            img_path_1 = getRealPathFromURI(photoUri);


                            try {
                                imageInByte = stream.toByteArray();

                            } catch (Exception e) {
                                e.printStackTrace();
                                if (BuildConfig.DEBUG)
                                    Log.d(TAG, "onActivityResult: " + e.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (BuildConfig.DEBUG)

                                Log.i("img_stts e", "Image Stts" + e.getMessage());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error reading image file", Toast.LENGTH_LONG).show();
                        if (BuildConfig.DEBUG)

                            Log.i("img_stts error", "Error reading image file");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    if (BuildConfig.DEBUG)

                        Log.i("img_stts e", "Image Stts" + e.getMessage());
                }
            }


        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Request Cancelled.", Toast.LENGTH_SHORT).show();
            if (BuildConfig.DEBUG)

                Log.i("img_stts Cancelled", "Request Cancelled");
        } else {
            getContentResolver().delete(imageUri, null, null);
            Toast.makeText(getApplicationContext(), "Data not getting.", Toast.LENGTH_SHORT).show();
            if (BuildConfig.DEBUG)

                Log.i("img_stts DataNtGetting", "Data not getting" + imageUri);
        }
    }

    private Bitmap timestampItAndSave(Bitmap toEdit) {
        Bitmap dest = Bitmap.createBitmap(toEdit.getWidth(), toEdit.getHeight(), Bitmap.Config.ARGB_8888);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(35);
        tPaint.setColor(BLUE);
        tPaint.setStyle(Paint.Style.FILL);
        float height = tPaint.measureText("yY");
        cs.drawText(dateTime, 20f, height + 15f, tPaint);
        try {
            dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/timestamped")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return dest;
    }

    private void addGraphics() {
        graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, R.color.appColor, 10f);
        simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, R.color.appColor, 10f);
        simpleMarkerSymbol.setOutline(simpleLineSymbol);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
        Log.i(TAG, "onPause, done");
    }

    @Override
    protected void onResume() {
        super.onResume();
        assistant.start();
        mapView.resume();
        fetchDatabase();
        fetchDatabaseVerified();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        assistant.stop();
        mapView.dispose();
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

    private void centermaponLocation() {
        Toast.makeText(this, getString(R.string.your_live_location), Toast.LENGTH_SHORT).show();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        mapView.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84()), 1050);
        locationDisplay.startAsync();
    }

    private void locationchangedmethod() {

        locationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                try {
                    if (locationChangedEvent.getLocation().getPosition() != null) {
                        x = locationChangedEvent.getLocation().getPosition().getX();
                        y = locationChangedEvent.getLocation().getPosition().getY();
                        accuracy = locationChangedEvent.getLocation().getHorizontalAccuracy();
                        callpoint = new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84());
                        CreateBuffer(callpoint);


                    } else {
                        Toast.makeText(MapScreen.this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    //  Toast.makeText(MapScreen.this, "Error in finding you're location.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void datasourcestatuschangedlistener() {

        locationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {
                // If LocationDisplay started OK, then continue.
                if (dataSourceStatusChangedEvent.isStarted()) {
                    return;
                }
                // No error is reported, then continue.
                if (dataSourceStatusChangedEvent.getError() == null) {
                    return;
                }

                // If an error is found, handle the failure to start.
                // Check permissions to see if failure may be due to lack of permissions.
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MapScreen.this, reqPermissions[0]) == PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MapScreen.this, reqPermissions[1]) == PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(MapScreen.this, reqPermissions, requestCode);
                } else {
                    // Report other unknown failure types to the user - for example, location services may not
                    // be enabled on the device.
                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.live_location_fab_button) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                assert locationManager != null;
                GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (GpsStatus) {
                    centermaponLocation();
                } else {
                    Toast.makeText(MapScreen.this, getString(R.string.please_one_gps), Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent1);
                }
            }


        }

    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else if (exit) {
//            finish();
//            super.onBackPressed();
//        } else if (exit == false) {
//            Toast.makeText(this, "Press Back again to Exit.",
//                    Toast.LENGTH_SHORT).show();
//            exit = true;
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    exit = false;
//                }
//            }, 3 * 1000);
//        }
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (t.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    private void CreateBuffer(final com.esri.arcgisruntime.geometry.Point currentLocationPoint) {
        bufferGraphicsOverlay.getGraphics().clear();
        LinearUnit linearUnit = new LinearUnit(LinearUnitId.METERS);
        bufferGeom = GeometryEngine.bufferGeodetic(currentLocationPoint, Double.parseDouble(Sp.read_shared_pref(MapScreen.this, "buffer_size_needed")), linearUnit, 0.0001, GeodeticCurveType.GEODESIC);
        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(50, 239, 188, 69), null);
        simpleFillSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.argb(255, 247, 160, 46), 1));
        Graphic polyGraphics = new Graphic(bufferGeom, simpleFillSymbol);
        if (!bufferGraphicsOverlay.getGraphics().isEmpty()) {
            bufferGraphicsOverlay.getGraphics().clear();
        }
        bufferGraphicsOverlay.getGraphics().add(polyGraphics);
        geom = GeometryEngine.project(bufferGeom, SpatialReference.create(4326));
    }

    private void checkPointInBuffer() {
        QueryParameters query1 = new QueryParameters();
        query1.setGeometry(geom);
        query1.setWhereClause("verified='Y' " + date_variable + inside_ca_variable + inside_dp_variable + not_shown_variable + ca_variable);
        query1.setSpatialRelationship(QueryParameters.SpatialRelationship.CONTAINS);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "checkPointInBuffer__Manish_query: " + query1.getWhereClause());
        }
        serviceFeatureTable.loadAsync();
        serviceFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                    final ListenableFuture<FeatureQueryResult> future = serviceFeatureTable.queryFeaturesAsync(query1, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                    // add done loading listener to fire when the selection returns
                    future.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // call get on the future to get the result
                                FeatureQueryResult result = future.get();
                                verifiedPointList.clear();
                                if (!result.iterator().hasNext()) {
                                    //    Toast.makeText(MapScreen.this, "No nearby volunteers found", Toast.LENGTH_LONG).show();
                                    //return;
                                } else {
                                    for (final Feature ftr : result) {
                                        Log.d(TAG, "run: " + verifiedPointList.size());
                                        verifiedPointList.add(ftr);
                                    }
                                }
                                Log.d(TAG, "run:verifiedPointList " + verifiedPointList.size());
                                verifiedPointView.setText(getString(R.string.verified_point) + verifiedPointList.size());
                            } catch (Exception e) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "run: " + e.getMessage());
                                }
                            }
                        }
                    });
                } else {

                    Toast.makeText(MapScreen.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                }
            }
        });


////////////////////////////////// Unverified Point ////////////////////////////////

        QueryParameters query2 = new QueryParameters();
        query2.setGeometry(geom);
        query2.setWhereClause("verified='N' " + date_variable + inside_ca_variable + inside_dp_variable + not_shown_variable + ca_variable);
        query2.setSpatialRelationship(QueryParameters.SpatialRelationship.CONTAINS);
        Log.d(TAG, "checkPointInBuffer: Manish_query " + query2.getWhereClause());
        serviceFeatureTable.loadAsync();
        serviceFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                    final ListenableFuture<FeatureQueryResult> future = serviceFeatureTable.queryFeaturesAsync(query2, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                    // add done loading listener to fire when the selection returns
                    future.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // call get on the future to get the result
                                FeatureQueryResult result = future.get();
                                unverifiedPointList.clear();
                                if (!result.iterator().hasNext()) {
                                    //Toast.makeText(MapScreen.this, "No nearby volunteers found", Toast.LENGTH_LONG).show();
                                    //return;
                                } else {
                                    for (final Feature ftr : result) {
                                        unverifiedPointList.add(ftr);
                                        Log.d(TAG, "run: " + unverifiedPointList.size());
                                    }
                                }
                                unverifiedPointView.setText(getString(R.string.unverified_point) + unverifiedPointList.size());
                                int total_points = verifiedPointList.size() + unverifiedPointList.size();
                                totalPointView.setText(getString(R.string.total_point) + total_points);
                            } catch (Exception e) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "run: " + e.getMessage());
                                }
                            }
                        }
                    });
                } else {

                    Toast.makeText(MapScreen.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void onFailed(String message, String description) {

        Toast.makeText(MapScreen.this, description, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNeedLocationPermission() {
        Log.d(TAG, "onNeedLocationPermission:dist");
        tvLocation.setText("Need\nPermission");
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assistant.requestLocationPermission();
            }
        });
        assistant.requestAndPossiblyExplainLocationPermission();
    }

    @Override
    public void onExplainLocationPermission() {
        Log.d(TAG, "onExplainLocationPermission:dist ");
        new AlertDialog.Builder(this).setMessage(R.string.permissionExplanation).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                assistant.requestLocationPermission();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                tvLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assistant.requestLocationPermission();
                    }
                });
            }
        }).show();
    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        Log.d(TAG, "onLocationPermissionPermanentlyDeclined: dist");
        new AlertDialog.Builder(this).setMessage(R.string.permissionPermanentlyDeclined).setPositiveButton(R.string.ok, fromDialog).show();
    }

    @Override
    public void onNeedLocationSettingsChange() {
        Log.d(TAG, "onNeedLocationSettingsChange:dist ");
//        new AlertDialog.Builder(this)
//                .setMessage(R.string.switchOnLocationShort)
//                .setCancelable(false)
//                .setPositiveButton(R.string.ok, (dialog, which) -> {
//                    dialog.dismiss();
//                    System.exit(1);
//                })
//                .show();

    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        Log.d(TAG, "onFallBackToSystemSettings: dist");
        new AlertDialog.Builder(this).setMessage(R.string.switchOnLocationLong).setPositiveButton(R.string.ok, fromDialog).show();
    }

    @Override
    public void onNewLocationAvailable(Location location) {
        Log.d(TAG, "onNewLocationAvailable:dist ");
        if (location == null) return;
        x = location.getLatitude();
        y = location.getLongitude();
        //  Log.d(TAG, "onNewLocationAvailable: " + location.getLongitude());
        //  Log.d(TAG, "onNewLocationAvailable: " + location.getLatitude());
        //  callpoint = new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84());
        // CreateBuffer(callpoint);

        tvLocation.setOnClickListener(null);
        tvLocation.setText(location.getLongitude() + "\n" + location.getLatitude());
        tvLocation.setAlpha(1.0f);
        tvLocation.animate().alpha(0.5f).setDuration(400);
    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        Log.d(TAG, "onMockLocationsDetected: dist");
        tvLocation.setText(getString(R.string.mockLocationMessage));
        tvLocation.setOnClickListener(fromView);
//        new AlertDialog.Builder(this)
//                .setMessage(R.string.switchOnLocationShort)
//                .setCancelable(false)
//                .setPositiveButton(R.string.ok, (dialog, which) -> {
//                    dialog.dismiss();
//                    System.exit(1);
//                })
//                .show();
    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {
        Log.d(TAG, "onError: dist");
        tvLocation.setText(getString(R.string.error));
    }

    private void fetchDatabase() {
        Log.d("TAG", "fetchDatabase: ");
        ArrayList<LatLongModels> array_list_for_lat_long = new ArrayList<>();
        AsyncTask.execute(() -> {
            List<NewPointRecord> newPointRecordList = AppDataBase.getDatabase(getApplicationContext()).newPointRecordDao().getAllData();
            Log.d(TAG, "fetchDatabase: ");
            array_list_for_lat_long.clear();
            for (NewPointRecord pointRecord : newPointRecordList) {
                array_list_for_lat_long.add(new LatLongModels(pointRecord.getLatitude(), pointRecord.getLongitude()));
            }

            runOnUiThread(() -> {
                if (!offline_graphics_overlay.getGraphics().isEmpty()) {
                    offline_graphics_overlay.getGraphics().clear();
                }
                initPinDrawable();

                for (int i = 0; i < array_list_for_lat_long.size(); i++) {
                    com.esri.arcgisruntime.geometry.Point busStopPoint = new com.esri.arcgisruntime.geometry.Point(Double.parseDouble(array_list_for_lat_long.get(i).getLongi()), Double.parseDouble(array_list_for_lat_long.get(i).getLati()), SpatialReference.create(4326));
                    if (BuildConfig.DEBUG) Log.d(TAG, "fetchDatabase: " + busStopPoint);
                    Graphic g = new Graphic(busStopPoint, pin_icon);
                    offline_graphics_overlay.getGraphics().add(g);

                }

            });
        });

    }


    private void fetchDatabaseVerified() {
        Log.d("TAG", "fetchDatabase: ");
        ArrayList<LatLongModels> array_list_for_lat_long2 = new ArrayList<>();
        AsyncTask.execute(() -> {
            List<VerifiedPointRecord> verifiedPointRecords = AppDataBase.getDatabase(getApplicationContext()).verifiedPointDao().getAllData();
            Log.d(TAG, "fetchDatabase:verified ");
            array_list_for_lat_long2.clear();
            for (VerifiedPointRecord pointRecord : verifiedPointRecords) {
                array_list_for_lat_long2.add(new LatLongModels(pointRecord.getLatitude(), pointRecord.getLongitude()));
            }
            runOnUiThread(() -> {
                if (!offline_graphics_overlay2.getGraphics().isEmpty()) {
                    offline_graphics_overlay2.getGraphics().clear();
                }
                initPinDrawableVerified();

                for (int i = 0; i < array_list_for_lat_long2.size(); i++) {
                    com.esri.arcgisruntime.geometry.Point busStopPoint = new com.esri.arcgisruntime.geometry.Point(Double.parseDouble(array_list_for_lat_long2.get(i).getLongi()), Double.parseDouble(array_list_for_lat_long2.get(i).getLati()), SpatialReference.create(4326));
                    if (BuildConfig.DEBUG) Log.d(TAG, "fetchDatabase: " + busStopPoint);
                    Graphic g = new Graphic(busStopPoint, pin_icon_two);
                    offline_graphics_overlay2.getGraphics().add(g);

                }

            });
        });

    }


    private void initPinDrawable() {
        BitmapDrawable pinDrawable = (BitmapDrawable) ContextCompat.getDrawable(MapScreen.this, R.drawable.offline_map_marker_black);

        if (pinDrawable != null) {
            try {
                pin_icon = PictureMarkerSymbol.createAsync(pinDrawable).get();
                pin_icon.setWidth(30f);
                pin_icon.setHeight(35f);

            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
    }

    private void initPinDrawableVerified() {
        BitmapDrawable pinDrawable = (BitmapDrawable) ContextCompat.getDrawable(MapScreen.this, R.drawable.offline_map_marker);

        if (pinDrawable != null) {
            try {
                pin_icon_two = PictureMarkerSymbol.createAsync(pinDrawable).get();
                pin_icon_two.setWidth(30f);
                pin_icon_two.setHeight(35f);

            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(this, getString(R.string.back_press_again), Toast.LENGTH_SHORT).show();
            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 3000);
        }

//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, getString(R.string.click_back_again), Toast.LENGTH_SHORT).show();
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.survey_report_menu, menu);


        MenuItem surveyReportItem = menu.findItem(R.id.survey_report);
//        surveyReportItem.setVisible(Sp.read_shared_pref(this, "userAccess").equalsIgnoreCase("Admin"));


        // Read shared preference safely
        String userAccess = Sp.read_shared_pref(this, "userAccess");

        // Null check before equalsIgnoreCase
        if (userAccess != null && userAccess.equalsIgnoreCase("Admin")) {
            surveyReportItem.setVisible(true);
        } else {
            surveyReportItem.setVisible(false);
        }


        surveyReportItem.setOnMenuItemClickListener(v -> {
            startActivity(new Intent(this, AdminReportSectionScreen.class));
            return false;
        });
        return super.onCreateOptionsMenu(menu);

    }

    private void showPolygon2(String where) {
        Log.d(TAG, "showPolygon: " + where);
        final QueryParameters query = new QueryParameters();
        query.setWhereClause(where);
        serviceFeatureTable.loadAsync();
        serviceFeatureTable.addDoneLoadingListener(() -> {
            if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                final ListenableFuture<FeatureQueryResult> future = serviceFeatureTable.queryFeaturesAsync(query, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                future.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = future.get();
                        if (!result.iterator().hasNext()) {
                            Log.d(TAG, "showPolygon:not found ");
                            return;
                        }
                        polygonUIDArrayList.clear();
                        for (final Feature ftr : result) {
                            Log.d(TAG, "showPolygon: " + result.getFields().size());
                            String str = (String) ftr.getAttributes().get("UID");
                            Log.d(TAG, "run Str:polygon count " + str);
                            polygonUIDArrayList.add((String) ftr.getAttributes().get("UID"));
                        }
                        managePolyGonLayer();
                    } catch (Exception e) {
                        Log.d(TAG, "showPolygon: " + e.getMessage());
                        Toast.makeText(MapScreen.this, "Error in Getting PolyGon" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showPolygon(String where) {
        polygonUIDArrayList.clear();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(MapScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAllDataUID(where);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject obj = new JSONObject(result);

                        boolean status = obj.optBoolean("status");
                        String message = obj.optString("message");
                        if (status) {
                            if (message.equalsIgnoreCase("Success")) {
                                JSONArray jsonArray = obj.optJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Log.d(TAG, "onResponse: " + jsonArray.length());
                                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                                    polygonUIDArrayList.add(jsonObject.optString("UID"));
                                }
                                Toast.makeText(MapScreen.this, String.valueOf(jsonArray.length()), Toast.LENGTH_LONG).show();
                                managePolyGonLayer();

                            }
                        } else {

                            Toast.makeText(MapScreen.this, message, Toast.LENGTH_SHORT).show();
                            if (BuildConfig.DEBUG) Log.d(TAG, "onResponse: " + message);

                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage());
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }

                } else if (response.code() == 404) {

                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                } else {
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", String.valueOf(response.code()));
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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

    private void managePolyGonLayer() {
        if (polygonUIDArrayList.isEmpty()) {
            assigned_polygon_variable = "";
        } else {

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < polygonUIDArrayList.size(); i++) {
                if (i == polygonUIDArrayList.size() - 1) {

                    stringBuilder.append("UID = '").append(polygonUIDArrayList.get(i)).append("'");
                } else {
                    stringBuilder.append("UID = '").append(polygonUIDArrayList.get(i)).append("'").append(" OR ");
                }
            }
            assigned_polygon_variable = String.valueOf(stringBuilder);

        }

    }

    private void portal_login_vector_tile_layer() {
        Log.d(TAG, "portal_login: ");
        UserCredential credential = new UserCredential(Sp.read_shared_pref(MapScreen.this, "user_name_hass"), Sp.read_shared_pref(MapScreen.this, "password_hass"));
        final Portal portal = new Portal(Sp.read_shared_pref(MapScreen.this, "hass_portal_login_url"), true);
        portal.setCredential(credential);
        portal.addDoneLoadingListener(() -> {
            if (portal.getLoadStatus() == LoadStatus.LOADED) {
                PortalUser user = portal.getUser();
                final ListenableFuture<PortalUserContent> contentFuture = user.fetchContentAsync();
                contentFuture.addDoneListener(() -> {
                    try {
                        Log.d(TAG, "portal_login_vector_tile_layer: ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } else {
                Toast.makeText(MapScreen.this, getString(R.string.portal_login_failed), Toast.LENGTH_SHORT).show();
            }
        });

        portal.loadAsync();

    }

    private void portal_login_gmda() {
        Log.d(TAG, "portal_login: ");
        UserCredential credential = new UserCredential(Sp.read_shared_pref(MapScreen.this, "user_name_needed"), Sp.read_shared_pref(MapScreen.this, "passowrd_needed"));
        final Portal portal = new Portal(Sp.read_shared_pref(MapScreen.this, "portal_login_url_ggm"), true);
        portal.setCredential(credential);
        portal.addDoneLoadingListener(() -> {
            if (portal.getLoadStatus() == LoadStatus.LOADED) {

                //    Toast.makeText(SplashScreen.this, "Connection Established.", Toast.LENGTH_SHORT).show();
                PortalUser user = portal.getUser();
                final ListenableFuture<PortalUserContent> contentFuture = user.fetchContentAsync();
                contentFuture.addDoneListener(() -> {
                    try {
                        Log.d(TAG, "portal_login: ");
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                });

            } else {
                Toast.makeText(MapScreen.this, getString(R.string.portal_login_failed), Toast.LENGTH_SHORT).show();
            }
        });

        portal.loadAsync();

    }
}



