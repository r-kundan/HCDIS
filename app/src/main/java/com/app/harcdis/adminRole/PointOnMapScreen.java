package com.app.harcdis.adminRole;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.app.harcdis.screens.SplashScreen;
import com.app.harcdis.utils.Sp;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointOnMapScreen extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private MapView mapView;
    private ArcGISMap arcGISMap;
    private PictureMarkerSymbol pinSourceSymbol;
    private FeatureLayer featureLayer;
    private FeatureLayer featureLayerPolygonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_on_map_screen);

        progressDialog = new ProgressDialog(PointOnMapScreen.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        get_all_layer_by_api("05");
        mapView = findViewById(R.id.arcgis_map_view);





    }


    private void get_all_layer_by_api(String dist_code) {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(PointOnMapScreen.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getFeatureLayerByDisCode(dist_code, Sp.read_shared_pref(PointOnMapScreen.this,"login_with"));
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
                                    String pointFeatureLayer = object.getString("feature_service");
                                    String feature_service_url_development = object.getString("development_plan_service");
                                    String feature_service_url_area_boundary = object.getString("controlled_area_boundary");
                                    String polygon_service = object.getString("polygon_service");
                                    set_up_function( pointFeatureLayer, feature_service_url_development, feature_service_url_area_boundary,polygon_service);
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


    private void onFailed(String s, String s1) {
        Toast.makeText(this, ""+s1, Toast.LENGTH_SHORT).show();
    }

    private void show_toast(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    private void set_up_function(String pointFeatureLayer, String feature_service_url_development, String feature_service_url_area_boundary, String polygon_service) {

        double latitude = getIntent().getDoubleExtra("latitude",0);
        double longitude = getIntent().getDoubleExtra("longitude",0);
        Log.d(TAG, "onCreate: Lat & Long"+latitude+","+longitude);
        arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, latitude, longitude, 17);



        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(pointFeatureLayer);
        serviceFeatureTable.loadAsync();

        ServiceFeatureTable boundaryServiceTable = new ServiceFeatureTable(feature_service_url_development);
        boundaryServiceTable.loadAsync();


        ServiceFeatureTable boundaryServiceTableTwo = new ServiceFeatureTable(feature_service_url_area_boundary);
        boundaryServiceTableTwo.loadAsync();

        ServiceFeatureTable boundaryServiceTableThreePolygon = new ServiceFeatureTable(polygon_service);
        boundaryServiceTableThreePolygon.loadAsync();


        featureLayer = new FeatureLayer(serviceFeatureTable);
        FeatureLayer featureLayerBoundaryOne = new FeatureLayer(boundaryServiceTable);
        FeatureLayer featureLayerBoundaryTwo = new FeatureLayer(boundaryServiceTableTwo);


        featureLayerPolygonService = new FeatureLayer(boundaryServiceTableThreePolygon);

//
//        SimpleLineSymbol polygonLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, RED, 2);
//        SimpleFillSymbol pointSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, BLUE, polygonLineSymbol);
//
//        UniqueValueRenderer uniqueValRenderer = new UniqueValueRenderer(null, null, null, pointSymbol);
//        uniqueValRenderer.getFieldNames().add("COLOR");
//
//        featureLayerPolygonService.setRenderer(uniqueValRenderer);




        arcGISMap.getOperationalLayers().add(featureLayerBoundaryOne);
        arcGISMap.getOperationalLayers().add(featureLayerBoundaryTwo);


        String query = getIntent().getStringExtra("query");
        mapByQuery(query);


    }


    private void mapByQuery(String query) {
        featureLayerPolygonService.setDefinitionExpression(query);
        featureLayer.setDefinitionExpression(query);
        arcGISMap.getOperationalLayers().add(featureLayerPolygonService);
        arcGISMap.getOperationalLayers().add(featureLayer);
        mapView.setMap(arcGISMap);
    }
}