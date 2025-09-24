package com.app.harcdis.adminRole;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.harcdis.screens.SplashScreen;
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
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;
import com.app.harcdis.utils.Connection_Detector;
import com.app.harcdis.utils.LocationAssistant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewScreen extends AppCompatActivity implements LocationAssistant.Listener {
    private static final String TAG = "MyTag";
    private final int requestCode = 2;
    private final String[] reqPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    FloatingActionButton live_location_fab_button;
    MapView mapView;
    Connection_Detector connection_detector;
    String vector_tile_url = "";
    String feature_service_url_development = "";
    String feature_service_url_area_boundary = "";
    String pointFeatureLayer = "";
    Polygon bufferGeom;
    com.esri.arcgisruntime.geometry.Point callpoint;
    private ArcGISMap arcGISMap;
    private ServiceFeatureTable serviceFeatureTable;
    private FeatureLayer featureLayer;
    private ProgressDialog progressDialog;
    private LocationAssistant assistant;
    private LocationDisplay locationDisplay;
    private double x;
    private double y;
    private LocationManager locationManager;
    private double accuracy;
    private boolean GpsStatus;
    private GraphicsOverlay bufferGraphicsOverlay;
    private FeatureLayer featureLayerBoundaryOne;
    private FeatureLayer featureLayerBoundaryTwo;
    private Geometry geom;
    private FeatureLayer featureLayerPolygonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_screen);

        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 4000, false);
        assistant.setVerbose(true);
        connection_detector = new Connection_Detector(getApplicationContext());

        progressDialog = new ProgressDialog(MapViewScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);



        mapView = findViewById(R.id.arcgis_map_view);
        live_location_fab_button = findViewById(R.id.live_location_fab_button_new);

        bufferGraphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(bufferGraphicsOverlay);

        get_all_layer_by_api("05");

        locationDisplay = mapView.getLocationDisplay();
        datasourcestatuschangedlistener();
        locationchangedmethod();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        mapView.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84()), 2050);
        locationDisplay.startAsync();

        live_location_fab_button.setOnClickListener(v->{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                assert locationManager != null;
                GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (GpsStatus) {
                    centerMapOnLocation();
                } else {
                    Toast.makeText(MapViewScreen.this, getString(R.string.please_one_gps), Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent1);
                }
            }
        });

    }

    private void mapByQuery(String query) {
        featureLayerPolygonService.setDefinitionExpression(query);
        featureLayer.setDefinitionExpression(query);
        arcGISMap.getOperationalLayers().add(featureLayerPolygonService);
        arcGISMap.getOperationalLayers().add(featureLayer);
        mapView.setMap(arcGISMap);
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


    private void centerMapOnLocation() {
        Toast.makeText(this, getString(R.string.your_live_location), Toast.LENGTH_SHORT).show();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        mapView.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84()), 500);
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
                   //     mapView.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84()), 500);
                        CreateBuffer(callpoint);
                        //        Log.d("MyTag", "" + x + " " + y + " " + accuracy);
                        //    Toast.makeText(MapScreen.this, ""+x+","+y+","+","+accuracy, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MapViewScreen.this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
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
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MapViewScreen.this, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MapViewScreen.this, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(MapViewScreen.this, reqPermissions, requestCode);
                } else {
                    // Report other unknown failure types to the user - for example, location services may not
                    // be enabled on the device.
                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                            .getSource().getLocationDataSource().getError().getMessage());

                }
            }
        });
    }



    private void CreateBuffer(final com.esri.arcgisruntime.geometry.Point currentLocationPoint) {
        bufferGraphicsOverlay.getGraphics().clear();
        LinearUnit linearUnit = new LinearUnit(LinearUnitId.METERS);
        bufferGeom = GeometryEngine.bufferGeodetic(currentLocationPoint, 100, linearUnit, 0.0001, GeodeticCurveType.GEODESIC);
        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(50, 239, 188, 69), null);
        simpleFillSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.argb(255, 247, 160, 46), 1));
        Graphic polyGraphics = new Graphic(bufferGeom, simpleFillSymbol);
        if (!bufferGraphicsOverlay.getGraphics().isEmpty()) {
            bufferGraphicsOverlay.getGraphics().clear();
        }
        bufferGraphicsOverlay.getGraphics().add(polyGraphics);
        geom = GeometryEngine.project(bufferGeom, SpatialReference.create(4326));
    }

    private void get_all_layer_by_api(String dist_code) {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(MapViewScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getFeatureLayerByDisCode(dist_code, Sp.read_shared_pref(MapViewScreen.this,"login_with"));
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
                                    pointFeatureLayer = object.getString("feature_service");
                                    feature_service_url_development = object.getString("development_plan_service");
                                    feature_service_url_area_boundary = object.getString("controlled_area_boundary");
                                    String polygon_service = object.getString("polygon_service");

                                    set_up_function(vector_tile_url, pointFeatureLayer, feature_service_url_development, feature_service_url_area_boundary,polygon_service);
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


    private void set_up_function(String vector_tile_url, String pointFeatureLayer, String feature_service_url_development, String feature_service_url_area_boundary, String polygon_service) {

        arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, 28.44775767809802, 77.03304752435946, 16);


        serviceFeatureTable = new ServiceFeatureTable(pointFeatureLayer);
        serviceFeatureTable.loadAsync();

        ServiceFeatureTable boundaryServiceTable = new ServiceFeatureTable(feature_service_url_development);
        boundaryServiceTable.loadAsync();


        ServiceFeatureTable boundaryServiceTableTwo = new ServiceFeatureTable(feature_service_url_area_boundary);
        boundaryServiceTableTwo.loadAsync();


        ServiceFeatureTable boundaryServiceTableThreePolygon = new ServiceFeatureTable(polygon_service);
        boundaryServiceTableThreePolygon.loadAsync();


        ArcGISVectorTiledLayer arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(vector_tile_url);


        featureLayer = new FeatureLayer(serviceFeatureTable);
        featureLayerBoundaryOne = new FeatureLayer(boundaryServiceTable);
        featureLayerBoundaryTwo = new FeatureLayer(boundaryServiceTableTwo);



        featureLayerPolygonService = new FeatureLayer(boundaryServiceTableThreePolygon);


//        SimpleLineSymbol polygonLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, RED, 2);
//        SimpleFillSymbol pointSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, BLUE, polygonLineSymbol);
//
//        UniqueValueRenderer uniqueValRenderer = new UniqueValueRenderer(null, null, null, pointSymbol);
//        uniqueValRenderer.getFieldNames().add("COLOR");
//
//        featureLayerPolygonService.setRenderer(uniqueValRenderer);


        arcGISMap.getOperationalLayers().add(featureLayerBoundaryOne);
        arcGISMap.getOperationalLayers().add(featureLayerBoundaryTwo);
        arcGISMap.getOperationalLayers().add(arcGISVectorTiledLayer);

        String query = getIntent().getStringExtra("query");
        mapByQuery(query);


    }

    private void onFailed(String s, String s1) {
        Toast.makeText(this, ""+s1, Toast.LENGTH_SHORT).show();
    }

    private void show_toast(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }


}