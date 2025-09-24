package com.app.harcdis.screens;

import static android.content.ContentValues.TAG;
import static com.app.harcdis.screens.MapScreen.apiKey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Sp;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
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
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedEvent;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedListener;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPointSelectionScreen extends AppCompatActivity {
    public static SketchEditor sketchEditor;
    public static Geometry sketchGeometry;
    private final int requestCode = 2;
    private final String[] reqPermissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    MapView first_location_map_view;
    SpatialReference wgsSR;
    Polygon bufferGeom;
    ArcGISMap map;
    ImageButton undoButton, redoButton, polygonButton, rectangleButton, clearAllButton;
    String ca_name = "", dev_plan = "",
            n_d_code, n_d_name, n_t_code,
            n_t_name, n_v_code, n_v_name, n_murr_no, n_khas_no;
    TextView polygon_area;
    CardView processFlowCardView;
    Button okButton;
    TextView click_on_map_text_view;
    String vector_tile_url, feature_service_url, feature_service_url_development, feature_service_url_area_boundary, polygon_service;
    CardView main_ll_layout;
    int polygon_check = 0;
    int rectangle_check = 0;
    private Point screenPoint;
    private com.esri.arcgisruntime.geometry.Point webPoint;
    private String click = "";
    private LocationDisplay locationDisplay;
    private GraphicsOverlay bufferGraphicsOverlay, graphicsOverlay, pointGraphicsOverlay;
    private FeatureLayer featureLayerBoundaryOne;
    private FeatureLayer featureLayerBoundaryTwo;
    private double x, y, accuracy;
    private PictureMarkerSymbol pinSourceSymbol;
    private FloatingActionButton confirmFloatingIcon;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private com.esri.arcgisruntime.geometry.Point callpoint;
    private Geometry geom;
    private ProgressDialog progressDialog;
    private ServiceFeatureTable serviceFeatureTable;
    private Feature feature;
    private ArcGISFeature selectedFeature;
    private Graphic graphic;
    private String gisId;
    private FeatureLayer featureLayer;
    private FeatureLayer featureLayerPolygonService;
    private String ca_key;
    private String code_bnd_controlled_area = "";
    private String code_bnd_dev_plan = "";
    private String urban_area_code_bnd = "";
    private String UAKey = "";

    //Create by Jyoti Choudhary for add new point on map

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_point_selection_screen);
        getExtras();

        ArcGISRuntimeEnvironment.setApiKey(apiKey);
        click_on_map_text_view = findViewById(R.id.click_on_map_text_view);
        main_ll_layout = findViewById(R.id.main_ll_layout);
        first_location_map_view = findViewById(R.id.first_location_map_view);
        confirmFloatingIcon = findViewById(R.id.confirmFloatingIcon);
        polygon_area = findViewById(R.id.polygon_area);
        redoButton = findViewById(R.id.redoButton);
        undoButton = findViewById(R.id.undoButton);
        clearAllButton = findViewById(R.id.clearAllButton);
        polygonButton = findViewById(R.id.polygonButton);
        rectangleButton = findViewById(R.id.rectangleButton);
        processFlowCardView = findViewById(R.id.processFlowCardView);
        okButton = findViewById(R.id.okButton);

        sketchEditor = new SketchEditor();


        initPinDrawable();
        progressDialog = new ProgressDialog(MapPointSelectionScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));


        if (!Sp.read_shared_pref(this, "processFlowStatus").equals("Seen")) {
            processFlowCardView.setVisibility(View.VISIBLE);

        } else {
            processFlowCardView.setVisibility(View.GONE);

        }

        okButton.setOnClickListener(v -> {
            processFlowCardView.setVisibility(View.GONE);
            Sp.write_shared_pref(this, "processFlowStatus", "Seen");
        });


        bufferGraphicsOverlay = new GraphicsOverlay();
        graphicsOverlay = new GraphicsOverlay();
        pointGraphicsOverlay = new GraphicsOverlay();

        map = new ArcGISMap(Basemap.Type.IMAGERY, 30.375233486156763, 76.78257619714825, 16);

//        map.setMinScale(8000);
//        map.setMaxScale(2000);


        vector_tile_url = getIntent().getStringExtra("vector_tile_url");
        feature_service_url = getIntent().getStringExtra("feature_service_url");
        feature_service_url_development = getIntent().getStringExtra("feature_service_url_development");
        feature_service_url_area_boundary = getIntent().getStringExtra("feature_service_url_area_boundary");
        polygon_service = getIntent().getStringExtra("polygon_service");


        setMap();


        redoButton.setOnClickListener(v -> {
            if (sketchEditor.canRedo()) {
                sketchEditor.redo();
            }
        });
        undoButton.setOnClickListener(v -> {
            if (sketchEditor.canUndo()) {
                sketchEditor.undo();
            }
        });
        clearAllButton.setOnClickListener(v -> {
            graphicsOverlay.clearSelection();
            sketchEditor.clearGeometry();
        });


        rectangleButton.setOnClickListener(v -> {
            graphicsOverlay.clearSelection();
            sketchEditor.stop();
            if (pointGraphicsOverlay.getGraphics().isEmpty()) {
                Toast.makeText(this, getString(R.string.first_mark_location), Toast.LENGTH_SHORT).show();
            } else {

                if (rectangle_check == 0) {
                    rectangle_check = 1;
                    polygon_check = 0;
                    polygonButton.setBackgroundColor(getResources().getColor(R.color.white));
                    rectangleButton.setBackgroundColor(getResources().getColor(R.color.appColor));
                    graphicsOverlay.clearSelection();
                    sketchEditor.start(SketchCreationMode.RECTANGLE);
                    Toast.makeText(this, getString(R.string.rectangle_selected), Toast.LENGTH_SHORT).show();
                } else {
                    rectangle_check = 0;
                    polygon_check = 0;
                    polygonButton.setBackgroundColor(getResources().getColor(R.color.white));
                    rectangleButton.setBackgroundColor(getResources().getColor(R.color.white));
                    graphicsOverlay.clearSelection();
                    sketchEditor.stop();
                    Toast.makeText(this, getString(R.string.rectangle_unselected), Toast.LENGTH_SHORT).show();
                }

            }
        });
        polygonButton.setOnClickListener(v -> {
            graphicsOverlay.clearSelection();
            sketchEditor.stop();
            if (pointGraphicsOverlay.getGraphics().isEmpty()) {
                Toast.makeText(this, getString(R.string.first_mark_location), Toast.LENGTH_SHORT).show();
            } else {

                if (polygon_check == 0) {
                    polygon_check = 1;
                    rectangle_check = 0;
                    polygonButton.setBackgroundColor(getResources().getColor(R.color.appColor));
                    rectangleButton.setBackgroundColor(getResources().getColor(R.color.white));
                    graphicsOverlay.clearSelection();
                    sketchEditor.start(SketchCreationMode.POLYGON);
                    Toast.makeText(this, getString(R.string.polygon_selected), Toast.LENGTH_SHORT).show();
                } else {
                    polygon_check = 0;
                    rectangle_check = 0;
                    polygonButton.setBackgroundColor(getResources().getColor(R.color.white));
                    rectangleButton.setBackgroundColor(getResources().getColor(R.color.white));
                    graphicsOverlay.clearSelection();
                    sketchEditor.stop();
                    Toast.makeText(this, getString(R.string.polygon_unselected), Toast.LENGTH_SHORT).show();
                }
            }
        });


        sketchEditor.addGeometryChangedListener(new SketchGeometryChangedListener() {
            @Override
            public void geometryChanged(SketchGeometryChangedEvent sketchGeometryChangedEvent) {


                if (sketchEditor.isSketchValid()) {

                    Envelope envelope = sketchGeometryChangedEvent.getGeometry().getExtent();
                    double area = GeometryEngine.area(envelope);

                    double sq_mtr = GeometryEngine.areaGeodetic(envelope, null, GeodeticCurveType.SHAPE_PRESERVING);

                    polygon_area.setText("Area: " + area + " Sq. Mtr");
                    polygon_area.setVisibility(View.VISIBLE);

                } else {
                    polygon_area.setVisibility(View.GONE);
                }
            }

        });


        confirmFloatingIcon.setOnClickListener(v -> {
            sketchGeometry = sketchEditor.getGeometry();
            if (sketchEditor.isSketchValid()) {
                com.esri.arcgisruntime.geometry.Point newPoint = new com.esri.arcgisruntime.geometry.Point(longitude, latitude, wgsSR);
                Geometry geometry = GeometryEngine.project(sketchGeometry, wgsSR);
                boolean isInBoundary = GeometryEngine.contains(geometry, newPoint);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreate: isInBoundary " + isInBoundary);
                    Log.d(TAG, "onCreate: isInBoundary " + newPoint);
                    Log.d(TAG, "onCreate: isInBoundary " + screenPoint);
                    Log.d(TAG, "onCreate: isInBoundary " + webPoint);
                    Log.d(TAG, "onCreate: isInBoundary " + sketchGeometry.getGeometryType());
                    Log.d(TAG, "onCreate: isInBoundary " + sketchGeometry.getSpatialReference());
                    Log.d(TAG, "onCreate: isInBoundary " + geometry.getSpatialReference());

                }
                if (isInBoundary) {

                    Intent intent = new Intent(this, SurveyFormScreen.class);
                    intent.putExtra("point", "newPoint");
                    intent.putExtra("boundary", "newBoundary");

                    intent.putExtra("latitude", String.valueOf(latitude));
                    intent.putExtra("longitude", String.valueOf(longitude));

                    intent.putExtra("ca_name", ca_name);
                    intent.putExtra("dev_plan", dev_plan);
                    intent.putExtra("ca_key", ca_key);
                    intent.putExtra("n_d_code", n_d_code);
                    intent.putExtra("n_t_code", n_t_code);
                    intent.putExtra("n_v_code", n_v_code);
                    intent.putExtra("n_murr_no", n_murr_no);
                    intent.putExtra("n_khas_no", n_khas_no);
                    intent.putExtra("n_d_name", n_d_name);
                    intent.putExtra("n_t_name", n_t_name);
                    intent.putExtra("n_v_name", n_v_name);
                    intent.putExtra("code_bnd_controlled_area", code_bnd_controlled_area);
                    intent.putExtra("code_bnd_dev_plan", code_bnd_dev_plan);
                    intent.putExtra("urban_area_code_bnd", urban_area_code_bnd);
                    intent.putExtra("UAKey", UAKey);

                    intent.putExtra("murabba_khasra_no", n_murr_no + "//" + n_khas_no);

                    String split1 = polygon_area.getText().toString().split(": ")[1];
                    String split2 = split1.split(" Sq")[0];

                    intent.putExtra("polygon_area", split2);
                    startActivity(intent);


                } else {
                    Toast.makeText(this, getString(R.string.create_boundaries_around_point), Toast.LENGTH_SHORT).show();
                }


            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(MapPointSelectionScreen.this);
                builder.setMessage(getString(R.string.do_you_want_draw_boundaries_around_this_point));
                builder.setPositiveButton(getString(R.string.no),
                        (dialog, id) -> {
                            dialog.dismiss();

                            if (n_d_code.isEmpty()) {
                                Toast.makeText(this, getString(R.string.data_not_getting_for_that_point), Toast.LENGTH_SHORT).show();

                            } else {
                                Intent intent = new Intent(this, SurveyFormScreen.class);
                                intent.putExtra("point", "newPoint");
                                intent.putExtra("boundary", "noBoundary");

                                intent.putExtra("latitude", String.valueOf(latitude));
                                intent.putExtra("longitude", String.valueOf(longitude));

                                intent.putExtra("ca_name", ca_name);
                                intent.putExtra("dev_plan", dev_plan);
                                intent.putExtra("ca_key", ca_key);
                                intent.putExtra("n_d_code", n_d_code);
                                intent.putExtra("n_t_code", n_t_code);
                                intent.putExtra("n_v_code", n_v_code);
                                intent.putExtra("n_murr_no", n_murr_no);
                                intent.putExtra("n_khas_no", n_khas_no);
                                intent.putExtra("n_d_name", n_d_name);
                                intent.putExtra("n_t_name", n_t_name);
                                intent.putExtra("n_v_name", n_v_name);
                                intent.putExtra("murabba_khasra_no", n_murr_no + "//" + n_khas_no);
                                intent.putExtra("code_bnd_controlled_area", code_bnd_controlled_area);
                                intent.putExtra("code_bnd_dev_plan", code_bnd_dev_plan);
                                intent.putExtra("urban_area_code_bnd", urban_area_code_bnd);
                                intent.putExtra("UAKey", UAKey);
                                intent.putExtra("polygon_area", 0.0);
                                startActivity(intent);
                            }
                        });
                builder.setNegativeButton(getString(R.string.yes), (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


        first_location_map_view.setOnTouchListener(new DefaultMapViewOnTouchListener(this, first_location_map_view) {
            private Graphic resultLocGraphic;

            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                screenPoint = new Point(Math.round(motionEvent.getX()), Math.round(motionEvent.getY()));
                wgsSR = SpatialReference.create(4326);

                webPoint = (com.esri.arcgisruntime.geometry.Point) GeometryEngine.project(first_location_map_view.screenToLocation(screenPoint), wgsSR);

                boolean isInField = GeometryEngine.contains(bufferGeom, webPoint);
                if (isInField) {


                    pointGraphicsOverlay.getGraphics().clear();

                    latitude = webPoint.getY();
                    longitude = webPoint.getX();

                    getNewPointInformation(latitude, longitude);
                    resultLocGraphic = new Graphic(webPoint, pinSourceSymbol);
                    pointGraphicsOverlay.getGraphics().add(resultLocGraphic);


                    first_location_map_view.setSketchEditor(sketchEditor);
                    //sketchEditor.start(SketchCreationMode.POLYGON);

                } else {
                    Toast.makeText(MapPointSelectionScreen.this, getString(R.string.add_new_point_in_buffer_range), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }

    private void setMap() {


        ServiceFeatureTable boundaryServiceTable = new ServiceFeatureTable(feature_service_url_development);
        boundaryServiceTable.loadAsync();


        ServiceFeatureTable boundaryServiceTableTwo = new ServiceFeatureTable(feature_service_url_area_boundary);
        boundaryServiceTableTwo.loadAsync();


        //  ArcGISVectorTiledLayer arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(vector_tile_url);
        String districtCodeConditions = "n_d_code = '" + Sp.read_shared_pref(MapPointSelectionScreen.this, "dis_code_store") + "'";
        featureLayerBoundaryOne = new FeatureLayer(boundaryServiceTable);
        featureLayerBoundaryOne.setDefinitionExpression(districtCodeConditions);

        featureLayerBoundaryTwo = new FeatureLayer(boundaryServiceTableTwo);
        featureLayerBoundaryTwo.setDefinitionExpression(districtCodeConditions);

        map.getOperationalLayers().add(featureLayerBoundaryOne);
        map.getOperationalLayers().add(featureLayerBoundaryTwo);
        // map.getOperationalLayers().add(arcGISVectorTiledLayer);


        first_location_map_view.getGraphicsOverlays().add(graphicsOverlay);
        first_location_map_view.getGraphicsOverlays().add(bufferGraphicsOverlay);
        first_location_map_view.getGraphicsOverlays().add(pointGraphicsOverlay);
        first_location_map_view.setMap(map);


        locationDisplay = first_location_map_view.getLocationDisplay();
        datasourcestatuschangedlistener();
        locationchangedmethod();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        first_location_map_view.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84()), 2050);
        locationDisplay.startAsync();


    }

    private void getNewPointInformation(Double latitude, Double longitude) {
        Log.d(TAG, "getNewPointInformation: latitude "+latitude);
        Log.d(TAG, "getNewPointInformation:longitude "+longitude);
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getNewPointInformationApp(String.valueOf(latitude), String.valueOf(longitude), Sp.read_shared_pref(this, "dis_code_store"));
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
                        if (message.equalsIgnoreCase("sucess")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");

                                JSONObject jsonObjectControlledArea = result_array.getJSONObject(0);
                                JSONObject jsonObjectDevPlan = result_array.getJSONObject(1);
                                JSONObject jsonObjectAdditional = result_array.getJSONObject(2);
                                JSONObject jsonObjectUrbanArea = result_array.getJSONObject(3);


                                Log.d(TAG, "onResponse: " + jsonObjectControlledArea);
                                Log.d(TAG, "onResponse: " + jsonObjectDevPlan);
                                Log.d(TAG, "onResponse: " + jsonObjectAdditional);
                                Log.d(TAG, "onResponse: " + jsonObjectUrbanArea);
                                ca_name = jsonObjectControlledArea.optString("Name");
                                ca_key = jsonObjectControlledArea.optString("CA_Key_GIS");
                                code_bnd_controlled_area = jsonObjectControlledArea.optString("code_bnd");

                                dev_plan = jsonObjectDevPlan.optString("DevPlan_Na");
                                code_bnd_dev_plan = jsonObjectDevPlan.optString("code_bnd");

                                n_d_code = jsonObjectAdditional.optString("n_d_code");
                                n_t_code = jsonObjectAdditional.optString("n_t_code");
                                n_v_code = jsonObjectAdditional.optString("n_v_code");
                                n_t_name = jsonObjectAdditional.optString("n_t_name");
                                n_d_name = jsonObjectAdditional.optString("n_d_name");
                                n_v_name = jsonObjectAdditional.optString("n_v_name");
                                n_murr_no = jsonObjectAdditional.optString("n_murr_no");
                                n_khas_no = jsonObjectAdditional.optString("n_khas_no");
                                urban_area_code_bnd = jsonObjectUrbanArea.optString("code_bnd");
                                UAKey = jsonObjectUrbanArea.optString("UAKey");


                                click_on_map_text_view.setVisibility(View.GONE);
                                confirmFloatingIcon.setVisibility(View.VISIBLE);
                                main_ll_layout.setVisibility(View.VISIBLE);

                            } else {
                                confirmFloatingIcon.setVisibility(View.GONE);
                                main_ll_layout.setVisibility(View.GONE);
                                Toast.makeText(MapPointSelectionScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            confirmFloatingIcon.setVisibility(View.GONE);
                            main_ll_layout.setVisibility(View.GONE);
                            Toast.makeText(MapPointSelectionScreen.this, message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        confirmFloatingIcon.setVisibility(View.GONE);
                        main_ll_layout.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        e.printStackTrace();
                        if (BuildConfig.DEBUG) {
                            Log.i("Resp Exc: ", e.getMessage() + "");
                        }
                        onFailed("An unexpected error has occurred.", "Error: " + e.getMessage() + "\n" + "Please Try Again later ");
                    }


                } else if (response.code() == 404) {
                    confirmFloatingIcon.setVisibility(View.GONE);
                    main_ll_layout.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");


                } else {
                    confirmFloatingIcon.setVisibility(View.GONE);
                    main_ll_layout.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    if (BuildConfig.DEBUG) {
                        Log.i("Resp Exc: ", "" + response.code());
                    }
                    onFailed("An unexpected error has occurred.", "Error Code: " + response.code() + "\n" + "Please Try Again later ");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                confirmFloatingIcon.setVisibility(View.GONE);
                main_ll_layout.setVisibility(View.GONE);
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

    private void onFailed(String s, String s1) {
        Toast.makeText(this, "" + s1, Toast.LENGTH_SHORT).show();
    }


    private void getExtras() {
        Intent intent = getIntent();
        if (intent.hasExtra("click")) {
            click = getIntent().getExtras().getString("click");

        }
    }

    private void initPinDrawable() {
        BitmapDrawable pinDrawable1 = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.pin);
        try {
            pinSourceSymbol = PictureMarkerSymbol.createAsync(pinDrawable1).get();
        } catch (InterruptedException | ExecutionException e) {
            String error = "Error creating PictureMarkerSymbol: " + e.getMessage();
            if (BuildConfig.DEBUG) {
                Log.e(TAG, error);
            }
            Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();

        }

    }

    /*------------------------------------------------------Location Change--------------------------------------------------------------------------*/
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
                        //first_location_map_view.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84()), 1050);
                        CreateBuffer(callpoint);
                    } else {
                        Toast.makeText(MapPointSelectionScreen.this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    //  Toast.makeText(MapPointSelectionScreen.this, getString(R.string.error_in_finding_your_location), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void CreateBuffer(final com.esri.arcgisruntime.geometry.Point currentLocationPoint) {
        bufferGraphicsOverlay.getGraphics().clear();
        LinearUnit linearUnit = new LinearUnit(LinearUnitId.METERS);
        bufferGeom = GeometryEngine.bufferGeodetic(currentLocationPoint, Double.parseDouble(Sp.read_shared_pref(MapPointSelectionScreen.this, "buffer_size_needed")), linearUnit, 0.0001, GeodeticCurveType.GEODESIC);
        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(50, 239, 188, 69), null);
        simpleFillSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.argb(255, 247, 160, 46), 1));
        Graphic polyGraphics = new Graphic(bufferGeom, simpleFillSymbol);
        if (!bufferGraphicsOverlay.getGraphics().isEmpty()) {
            bufferGraphicsOverlay.getGraphics().clear();
        }
        bufferGraphicsOverlay.getGraphics().add(polyGraphics);
        geom = GeometryEngine.project(bufferGeom, SpatialReference.create(4326));


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
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MapPointSelectionScreen.this, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MapPointSelectionScreen.this, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(MapPointSelectionScreen.this, reqPermissions, requestCode);
                } else {
                    // Report other unknown failure types to the user - for example, location services may not
                    // be enabled on the device.
                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                            .getSource().getLocationDataSource().getError().getMessage());

                    Toast.makeText(MapPointSelectionScreen.this, message, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.topographic_item:
                map.setBasemap(Basemap.createTopographic());
                break;
            case R.id.street_item:
                map.setBasemap(Basemap.createStreets());
                break;
            case R.id.openstreetmap_item:
                map.setBasemap(Basemap.createOceans());
                break;
            case R.id.imagery_item:
                map.setBasemap(Basemap.createImagery());
                break;
        }
        return true;
    }
}



