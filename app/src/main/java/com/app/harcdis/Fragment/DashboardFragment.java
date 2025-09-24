package com.app.harcdis.Fragment;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.app.harcdis.screens.SplashScreen;
import com.app.harcdis.utils.Sp;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.api.ApiInterface;
import com.app.harcdis.api.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    Spinner districtSpinner;
    LinearLayout pointLinearLayout;
    MapView showPointOnMap;
    TextView all_point, verified_point, unverified_point;
    private ProgressDialog progressDialog;
    private ArrayList<String> dist_list_name;
    private ArrayList<String> dist_list_code;
    private String dist_code, dist_name;
    private ArcGISMap arcGISMap;

    private FeatureLayer featureLayer;
    private FeatureLayer featureLayerPolygonService;
    private String query;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        districtSpinner = view.findViewById(R.id.districtSpinner);
        showPointOnMap = view.findViewById(R.id.showPointOnMap);
        all_point = view.findViewById(R.id.all_point);
        verified_point = view.findViewById(R.id.verified_point);
        unverified_point = view.findViewById(R.id.unverified_point);
        pointLinearLayout = view.findViewById(R.id.pointLinearLayout);


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));

        dist_list_name = new ArrayList<>();
        dist_list_code = new ArrayList<>();

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dist_name = dist_list_name.get(position);
                dist_code = dist_list_code.get(position);


                if (!districtSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_one))) {
                    pointLinearLayout.setVisibility(View.VISIBLE);
                    all_point.setTextColor(getResources().getColor(R.color.white));
                    all_point.setBackgroundResource(R.drawable.btn_background);
                    verified_point.setTextColor(getResources().getColor(R.color.purple_700));
                    verified_point.setBackgroundResource(R.drawable.btn_light_background);
                    unverified_point.setBackgroundResource(R.drawable.btn_light_background);
                    unverified_point.setTextColor(getResources().getColor(R.color.purple_700));
                    getPointOfSelectedDistrict(dist_code);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadAllDistrict();

        all_point.setOnClickListener(v->{
            all_point.setTextColor(getResources().getColor(R.color.white));
            all_point.setBackgroundResource(R.drawable.btn_background);
            verified_point.setTextColor(getResources().getColor(R.color.purple_700));
            verified_point.setBackgroundResource(R.drawable.btn_light_background);
            unverified_point.setBackgroundResource(R.drawable.btn_light_background);
            unverified_point.setTextColor(getResources().getColor(R.color.purple_700));

            query="n_d_code = '"+ dist_code +"'";
            mapByQuery(query);
        });
        verified_point.setOnClickListener(v->{
            verified_point.setTextColor(getResources().getColor(R.color.white));
            verified_point.setBackgroundResource(R.drawable.btn_background);
            all_point.setTextColor(getResources().getColor(R.color.purple_700));
            all_point.setBackgroundResource(R.drawable.btn_light_background);
            unverified_point.setBackgroundResource(R.drawable.btn_light_background);
            unverified_point.setTextColor(getResources().getColor(R.color.purple_700));

            query="n_d_code = '"+ dist_code +"' AND verified='Y'";
            mapByQuery(query);
        });
        unverified_point.setOnClickListener(v->{
            unverified_point.setTextColor(getResources().getColor(R.color.white));
            unverified_point.setBackgroundResource(R.drawable.btn_background);
            verified_point.setTextColor(getResources().getColor(R.color.purple_700));
            verified_point.setBackgroundResource(R.drawable.btn_light_background);
            all_point.setBackgroundResource(R.drawable.btn_light_background);
            all_point.setTextColor(getResources().getColor(R.color.purple_700));

            query="n_d_code = '"+ dist_code +"' AND verified='N'";
            mapByQuery(query);
        });


        return view;
    }



    private void getPointOfSelectedDistrict(String dist_code) {
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(getContext()).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getFeatureLayerByDisCode(dist_code, Sp.read_shared_pref(getContext(),"login_with"));
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
                                    String vector_tile_url = object.getString("tile_service");
                                    String  pointFeatureLayer = object.getString("feature_service");
                                    String feature_service_url_development = object.getString("development_plan_service");
                                    String feature_service_url_area_boundary = object.getString("controlled_area_boundary");
                                    String polygon_service = object.getString("polygon_service");

                                    set_up_function(vector_tile_url, pointFeatureLayer, feature_service_url_development, feature_service_url_area_boundary,dist_code,polygon_service);
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


    private void set_up_function(String vector_tile_url, String pointFeatureLayer, String feature_service_url_development, String feature_service_url_area_boundary, String dist_code, String polygon_service) {

        arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, 29.324756107617237, 76.33718321624832, 7);

        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(pointFeatureLayer);
        serviceFeatureTable.loadAsync();

        ServiceFeatureTable boundaryServiceTable = new ServiceFeatureTable(feature_service_url_development);
        boundaryServiceTable.loadAsync();


        ServiceFeatureTable boundaryServiceTableTwo = new ServiceFeatureTable(feature_service_url_area_boundary);
        boundaryServiceTableTwo.loadAsync();


        ServiceFeatureTable boundaryServiceTableThreePolygon = new ServiceFeatureTable(polygon_service);
        boundaryServiceTableThreePolygon.loadAsync();

        ArcGISVectorTiledLayer arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(vector_tile_url);


        featureLayer = new FeatureLayer(serviceFeatureTable);
        FeatureLayer featureLayerBoundaryOne = new FeatureLayer(boundaryServiceTable);
        FeatureLayer featureLayerBoundaryTwo = new FeatureLayer(boundaryServiceTableTwo);

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

        arcGISMap.getOperationalLayers().add(featureLayerPolygonService);
        arcGISMap.getOperationalLayers().add(featureLayer);
        showPointOnMap.setMap(arcGISMap);


        query="n_d_code = '"+ dist_code +"'";

        mapByQuery(query);

    }

    private void mapByQuery(String query) {
        featureLayerPolygonService  .setDefinitionExpression(query);
        featureLayer.setDefinitionExpression(query);
    }


    private void show_toast(String message) {
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
    }

    private void loadAllDistrict() {
        dist_list_name.clear();
        dist_list_code.clear();

        dist_list_name.add(getString(R.string.select_one));
        dist_list_code.add(getString(R.string.select_one));

        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitClient(getContext()).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAllDistrict();
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
                        if (message.equalsIgnoreCase("Success")) {
                            if (status) {
                                JSONArray result_array = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject object = result_array.getJSONObject(i);
                                    if (!object.optString("n_d_code").equalsIgnoreCase(" ")) {
                                        dist_list_name.add(object.optString("n_d_name"));
                                        dist_list_code.add(object.optString("n_d_code"));

                                    }
                                }
                                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, dist_list_name);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                districtSpinner.setAdapter(adapter);


                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
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


    private void onFailed(String s, String s1) {
        Toast.makeText(getContext(), "" + s1, Toast.LENGTH_SHORT).show();
    }

}