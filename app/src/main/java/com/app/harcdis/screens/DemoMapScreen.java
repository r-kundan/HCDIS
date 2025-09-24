package com.app.harcdis.screens;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.harcdis.R;
import com.app.harcdis.model.LatLongModels;
import com.app.harcdis.utils.Connection_Detector;
import com.app.harcdis.utils.LocationAssistant;
import com.app.harcdis.utils.Sp;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class DemoMapScreen extends AppCompatActivity implements LocationAssistant.Listener {
    private static final String TAG = "MyTag";
    private final int requestCode = 2;
    private final String[] reqPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    ImageButton search_data_via_date;
    TextView bottom_text;
    PictureMarkerSymbol pin_icon;
    //Create by manish for spinner data.
    ArrayList<String> N_MURR_NO;
    ArrayList<String> N_KHAS_NO;
    ArrayList<String> OBJECT_ID;
    ArrayList<String> latitude_array_list;
    ArrayList<String> longitude_array_list;
    double x1 = 0.0;
    double y1 = 0.0;
    ServiceFeatureTable serviceFeatureTable;
    Callout mCallOut;
    FloatingActionButton floatingActionButton, live_location_fab_button;
    GraphicsOverlay graphicsOverlay;
    SimpleLineSymbol simpleLineSymbol;
    SimpleMarkerSymbol simpleMarkerSymbol;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;
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
    TextView username;
    TextView navUserid;
    Connection_Detector connection_detector;
    Animation shake;
    String coming_dis_code;
    String coming_tehsil_code;
    String coming_tehsil_code_name;
    String coming_village_code;
    String coming_village_code_name;
    boolean doubleBackToExitPressedOnce = false;
    EditText start_date, end_date;
    private LocationManager locationManager;
    private boolean GpsStatus;
    private TextView tvLocation;
    private EditText remarks_edit_text;
    private String img_path_1 = "";
    private MapView mapView;
    private FeatureLayer featureLayer;
    private FeatureLayer featureLayerBoundaryOne;
    private FeatureLayer featureLayerBoundaryTwo;
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
    private String user_name, user_id;
    private int rq_code_camera = 1;
    private String extra_crop_name = "";
    private ProgressDialog progressDialog4;
    private LocationAssistant assistant;
    private ArrayList<LatLongModels> array_list_for_lat_long;
    private String n_d_code = "";
    private GraphicsOverlay offline_graphics_overlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_map_screen);

        connection_detector = new Connection_Detector(getApplicationContext());
        mapView= findViewById(R.id.map_view_demo_screen);
        arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, 30.375233486156763, 76.78257619714825, 16);
        mapView.setMap(arcGISMap);
        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 4000, false);
        assistant.setVerbose(true);
      //  initViews();
    }

    public  Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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

    @Override
    public void onNeedLocationPermission() {

    }

    @Override
    public void onExplainLocationPermission() {

    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNeedLocationSettingsChange() {

    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNewLocationAvailable(Location location) {

    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }

    private void initViews() {
        bottom_text = findViewById(R.id.bottom_text);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        search_data_via_date = findViewById(R.id.search_data_via_date);
        floatingActionButton = findViewById(R.id.fab_data_button);
        live_location_fab_button = findViewById(R.id.live_location_fab_button);
        con_layout = findViewById(R.id.con_layout);

        N_MURR_NO = new ArrayList<>();
        N_KHAS_NO = new ArrayList<>();
        OBJECT_ID = new ArrayList<>();

        latitude_array_list = new ArrayList<>();
        longitude_array_list = new ArrayList<>();
        array_list_for_lat_long = new ArrayList<>();


        imageInByte = new byte[0];
        progressDialog = new ProgressDialog(DemoMapScreen.this);
        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("Please Wait ... ");
        progressDialog.setCancelable(false);

        progressDialog1 = new ProgressDialog(DemoMapScreen.this);
        progressDialog1.setTitle("Loading...");
        progressDialog1.setMessage("Please wait");
        progressDialog1.setCancelable(false);

        bufferGraphicsOverlay = new GraphicsOverlay();
        offline_graphics_overlay = new GraphicsOverlay();
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        clearArrayListAll();

        coming_dis_code = Sp.read_shared_pref(DemoMapScreen.this, "dis_code_store");
        coming_tehsil_code = Sp.read_shared_pref(DemoMapScreen.this, "teh_code_store");
        coming_village_code = Sp.read_shared_pref(DemoMapScreen.this, "village_code_store");
        coming_tehsil_code_name = Sp.read_shared_pref(DemoMapScreen.this, "teh_code_store_name");
        coming_village_code_name = Sp.read_shared_pref(DemoMapScreen.this, "village_code_store_name");

        if (coming_tehsil_code_name != null) {
            if (coming_village_code_name != null) {
                bottom_text.setText(coming_dis_code + "," + coming_tehsil_code_name + "," + coming_village_code_name);
            } else {
                bottom_text.setText(coming_dis_code + "," + coming_tehsil_code_name);
            }
        } else {
            bottom_text.setText("Current District Code " + coming_dis_code);
        }

        //    get_all_layer_by_api(coming_dis_code);

        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);

        end_date.setOnClickListener(v -> {
            Log.d(TAG, "initViews: Calender ONe");

            DatePickerDialog datePicker = new DatePickerDialog(DemoMapScreen.this, (view, year1, month1, dayOfMonth) -> end_date.setText(year1 + "-" + convertDate((month1 + 1)) + "-" + convertDate(dayOfMonth)), year, month, day);
            datePicker.show();
        });

        start_date.setOnClickListener(v -> {
            Log.d(TAG, "initViews: Calender Button Two");
            DatePickerDialog datePicker = new DatePickerDialog(DemoMapScreen.this, (view, year1, month1, dayOfMonth) -> start_date.setText(year1 + "-" + convertDate((month1 + 1)) + "-" + convertDate(dayOfMonth)), year, month, day);
            datePicker.show();
        });


        search_data_via_date.setOnClickListener(view -> {
            Log.d(TAG, "initViews: button Called Search");
            if (start_date.getText().toString().isEmpty()) {
                Toast.makeText(this, "Select Start Date", Toast.LENGTH_SHORT).show();
            } else {
              //  set_up_all_data("button");
            }
        });


    }

    private void clearArrayListAll() {
        Log.d(TAG, "clearArrayListAll: array list clear method called");
        latitude_array_list.clear();
        longitude_array_list.clear();
        N_MURR_NO.clear();
        N_KHAS_NO.clear();
        OBJECT_ID.clear();
        img_path_1 = "";

    }


    public String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        assistant.start();
        mapView.resume();



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
        Toast.makeText(this, "Your live location", Toast.LENGTH_SHORT).show();
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
                        //    mapView.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84()), 1050);
                        CreateBuffer(callpoint);
                        //        Log.d("MyTag", "" + x + " " + y + " " + accuracy);
                        //    Toast.makeText(MapScreen.this, ""+x+","+y+","+","+accuracy, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DemoMapScreen.this, "Wait", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    //  Toast.makeText(MapScreen.this, "Error in finding you're location.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void CreateBuffer(final com.esri.arcgisruntime.geometry.Point currentLocationPoint) {
        bufferGraphicsOverlay.getGraphics().clear();
        LinearUnit linearUnit = new LinearUnit(LinearUnitId.METERS);
        bufferGeom = GeometryEngine.bufferGeodetic(currentLocationPoint, Double.parseDouble(Sp.read_shared_pref(DemoMapScreen.this, "buffer_size_needed")), linearUnit, 0.0001, GeodeticCurveType.GEODESIC);
        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(50, 239, 188, 69), null);
        simpleFillSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.argb(255, 247, 160, 46), 1));
        Graphic polyGraphics = new Graphic(bufferGeom, simpleFillSymbol);
        if (!bufferGraphicsOverlay.getGraphics().isEmpty()) {
            bufferGraphicsOverlay.getGraphics().clear();
        }
        bufferGraphicsOverlay.getGraphics().add(polyGraphics);
        geom = GeometryEngine.project(bufferGeom, SpatialReference.create(4326));
    }

    public void onFailed(String message, String description) {

        Toast.makeText(DemoMapScreen.this, "" + description, Toast.LENGTH_SHORT).show();

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
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(DemoMapScreen.this, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(DemoMapScreen.this, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(DemoMapScreen.this, reqPermissions, requestCode);
                } else {
                    // Report other unknown failure types to the user - for example, location services may not
                    // be enabled on the device.
                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                            .getSource().getLocationDataSource().getError().getMessage());

                }
            }
        });
    }
}