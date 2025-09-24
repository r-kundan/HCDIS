package com.app.harcdis.screens;

import static android.graphics.Color.CYAN;
import static com.app.harcdis.firebase.MyFirebaseNService.TAG;
import static com.app.harcdis.screens.MapScreen.apiKey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedEvent;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedListener;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MapChangeBoundaryScreen extends AppCompatActivity {

    String vector_tile_url, feature_service_url, feature_service_url_development, feature_service_url_area_boundary, polygon_service;
    private String gisId,uid, objectId;
    private FeatureLayer featureLayer;
    private FeatureLayer featureLayerPolygonService;

    String ca_name, dev_plan,ca_key, n_d_code, n_d_name, n_t_code, n_t_name, n_v_code, n_v_name, n_murr_no, n_khas_no;
    private ProgressDialog progressDialog;

    MapView change_boundary_map_view;
    private Point screenPoint;
    SpatialReference wgsSR;
    private com.esri.arcgisruntime.geometry.Point webPoint;
    private String click = "";
    private LocationDisplay locationDisplay;
    private GraphicsOverlay graphicsOverlay;

    private FeatureLayer featureLayerBoundaryOne;
    private FeatureLayer featureLayerBoundaryTwo;

    private double x, y, accuracy;
    private final int requestCode = 2;

    private final String[] reqPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private PictureMarkerSymbol pinSourceSymbol;
    private FloatingActionButton confirmFloatingIcon;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private com.esri.arcgisruntime.geometry.Point callpoint;
    Polygon bufferGeom;
    private Geometry geom;
    ArcGISMap map;

    public static SketchEditor sketchEditorUpdate;
    public static Geometry sketchGeometryUpdate;

    private ImageButton rectangleButton, polygonButton;
    private ImageView clearAllButton, undoButton, redoButton;
    TextView polygon_area, click_on_map_text_view;
    CardView main_ll_layout;
    private boolean exit = false;
    int polygon_check = 0;
    int rectangle_check = 0;


    //Create by Jyoti Choudhary for change boundary of selected point
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_change_boundary_screen);


        ArcGISRuntimeEnvironment.setApiKey(apiKey);
        change_boundary_map_view = findViewById(R.id.change_boundary_map_view);
        confirmFloatingIcon = findViewById(R.id.confirmFloatingIcon);
        click_on_map_text_view = findViewById(R.id.click_on_map_text_view);

        main_ll_layout = findViewById(R.id.main_ll_layout);

        polygon_area = findViewById(R.id.polygon_area);
        redoButton = findViewById(R.id.redoButton);
        undoButton = findViewById(R.id.undoButton);
        clearAllButton = findViewById(R.id.clearAllButton);
        polygonButton = findViewById(R.id.polygonButton);
        rectangleButton = findViewById(R.id.rectangleButton);


        progressDialog = new ProgressDialog(MapChangeBoundaryScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));

        map = new ArcGISMap(Basemap.Type.IMAGERY, 30.375233486156763, 76.78257619714825, 16);
//
//        map.setMinScale(8000);
//        map.setMaxScale(2000);

        wgsSR = SpatialReference.create(4326);
        sketchEditorUpdate = new SketchEditor();
        graphicsOverlay = new GraphicsOverlay();
        vector_tile_url = getIntent().getStringExtra("vector_tile_url");
        feature_service_url = getIntent().getStringExtra("feature_service_url");
        feature_service_url_development = getIntent().getStringExtra("feature_service_url_development");
        feature_service_url_area_boundary = getIntent().getStringExtra("feature_service_url_area_boundary");
        polygon_service = getIntent().getStringExtra("polygon_service");


        latitude = Double.valueOf(getIntent().getStringExtra("latitude"));
        longitude = Double.valueOf(getIntent().getStringExtra("longitude"));

        ca_name = getIntent().getStringExtra("ca_name");
        dev_plan = getIntent().getStringExtra("dev_plan");
        ca_key = getIntent().getStringExtra("ca_key");

        n_d_code = getIntent().getStringExtra("n_d_code");
        n_t_code = getIntent().getStringExtra("n_t_code");
        n_v_code = getIntent().getStringExtra("n_v_code");


        n_d_name = getIntent().getStringExtra("n_d_name");
        n_t_name = getIntent().getStringExtra("n_t_name");
        n_v_name = getIntent().getStringExtra("n_v_name");
        gisId = getIntent().getStringExtra("gisId");
        uid = getIntent().getStringExtra("uid");
        objectId = getIntent().getStringExtra("objectId");
        n_murr_no = getIntent().getStringExtra("murabba");
        n_khas_no = getIntent().getStringExtra("khasra_no");


        if (BuildConfig.DEBUG) {
            Log.d(ContentValues.TAG, "initViews: latitude" + latitude);
            Log.d(ContentValues.TAG, "initViews: longitude" + longitude);
            Log.d(ContentValues.TAG, "initViews: n_d_code" + n_d_code);
            Log.d(ContentValues.TAG, "initViews: n_t_code" + n_t_code);
            Log.d(ContentValues.TAG, "initViews: n_v_code" + n_v_code);

            Log.d(ContentValues.TAG, "initViews: vector_tile_url" + vector_tile_url);
            Log.d(ContentValues.TAG, "initViews: feature_service_url" + feature_service_url);
            Log.d(ContentValues.TAG, "initViews: polygon_service" + polygon_service);
        }
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(feature_service_url);
        serviceFeatureTable.loadAsync();
        ServiceFeatureTable boundaryServiceTableThreePolygon = new ServiceFeatureTable(polygon_service);
        boundaryServiceTableThreePolygon.loadAsync();


        featureLayer = new FeatureLayer(serviceFeatureTable);
        featureLayerPolygonService = new FeatureLayer(boundaryServiceTableThreePolygon);

        SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, CYAN, 25);

        UniqueValueRenderer uniqueValRenderer = new UniqueValueRenderer(null, null, null, simpleMarkerSymbol);
        uniqueValRenderer.getFieldNames().add("COLOR");

        featureLayer.setRenderer(uniqueValRenderer);

        String query = "gisId='" + gisId + "'";
        featureLayerPolygonService.setDefinitionExpression(query);
        featureLayer.setDefinitionExpression(query);

        map.getOperationalLayers().add(featureLayerPolygonService);
        map.getOperationalLayers().add(featureLayer);


        setMap();

        redoButton.setOnClickListener(v -> {
            if (sketchEditorUpdate.canRedo()) {
                sketchEditorUpdate.redo();
            }
        });
        undoButton.setOnClickListener(v -> {
            if (sketchEditorUpdate.canUndo()) {
                sketchEditorUpdate.undo();
            }
        });
        clearAllButton.setOnClickListener(v -> {
            graphicsOverlay.clearSelection();
            sketchEditorUpdate.clearGeometry();
        });


        rectangleButton.setOnClickListener(v -> {
            graphicsOverlay.clearSelection();
            sketchEditorUpdate.stop();
            if (rectangle_check == 0) {
                rectangle_check = 1;
                polygon_check=0;
                polygonButton.setBackgroundColor(getResources().getColor(R.color.white));
                rectangleButton.setBackgroundColor(getResources().getColor(R.color.appColor));
                graphicsOverlay.clearSelection();
                sketchEditorUpdate.start(SketchCreationMode.RECTANGLE);
                Toast.makeText(this, getString(R.string.rectangle_selected), Toast.LENGTH_SHORT).show();
            } else {
                rectangle_check = 0;
                polygon_check=0;
                polygonButton.setBackgroundColor(getResources().getColor(R.color.white));
                rectangleButton.setBackgroundColor(getResources().getColor(R.color.white));
                graphicsOverlay.clearSelection();
                sketchEditorUpdate.stop();
                Toast.makeText(this, getString(R.string.rectangle_unselected), Toast.LENGTH_SHORT).show();
            }
        });
        polygonButton.setOnClickListener(v -> {
            graphicsOverlay.clearSelection();
            sketchEditorUpdate.stop();
            if(polygon_check==0) {
                polygon_check=1;
                rectangle_check = 0;
                polygonButton.setBackgroundColor(getResources().getColor(R.color.appColor));
                rectangleButton.setBackgroundColor(getResources().getColor(R.color.white));
                graphicsOverlay.clearSelection();
                sketchEditorUpdate.start(SketchCreationMode.POLYGON);
                Toast.makeText(this, getString(R.string.polygon_selected), Toast.LENGTH_SHORT).show();
            }else{
                polygon_check=0;
                rectangle_check = 0;
                polygonButton.setBackgroundColor(getResources().getColor(R.color.white));
                rectangleButton.setBackgroundColor(getResources().getColor(R.color.white));
                graphicsOverlay.clearSelection();
                sketchEditorUpdate.stop();
                Toast.makeText(this, getString(R.string.polygon_unselected), Toast.LENGTH_SHORT).show();
            }
        });

        click_on_map_text_view.setVisibility(View.GONE);
        main_ll_layout.setVisibility(View.VISIBLE);

        change_boundary_map_view.setSketchEditor(sketchEditorUpdate);

        change_boundary_map_view.setOnTouchListener(new DefaultMapViewOnTouchListener(this, change_boundary_map_view) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {

                click_on_map_text_view.setVisibility(View.GONE);
                main_ll_layout.setVisibility(View.VISIBLE);

                change_boundary_map_view.setSketchEditor(sketchEditorUpdate);

                return true;
            }
        });

        sketchEditorUpdate.addGeometryChangedListener(new SketchGeometryChangedListener() {
            @Override
            public void geometryChanged(SketchGeometryChangedEvent sketchGeometryChangedEvent) {

                if (sketchEditorUpdate.isSketchValid()) {

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
            sketchGeometryUpdate = sketchEditorUpdate.getGeometry();

            if (sketchEditorUpdate.isSketchValid()) {


                com.esri.arcgisruntime.geometry.Point newPoint = new com.esri.arcgisruntime.geometry.Point(longitude, latitude, wgsSR);

                Geometry geometry = GeometryEngine.project(sketchGeometryUpdate, wgsSR);
                boolean isInBoundary = GeometryEngine.contains(geometry, newPoint);


                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreate: isInBoundary " + isInBoundary);
                    Log.d(TAG, "onCreate: isInBoundary " + newPoint);
                    Log.d(TAG, "onCreate: isInBoundary " + sketchGeometryUpdate.getGeometryType());
                    Log.d(TAG, "onCreate: isInBoundary " + sketchGeometryUpdate.getSpatialReference());
                    Log.d(TAG, "onCreate: isInBoundary " + geometry.getSpatialReference());
                }

                if (isInBoundary) {

                    Intent intent = new Intent(this, SurveyFormScreen.class);
                    intent.putExtra("point", "editPoint");
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

                    intent.putExtra("gisId", gisId);
                    intent.putExtra("uid", uid);
                    intent.putExtra("objectId", objectId);
                    intent.putExtra("murabba_khasra_no", n_murr_no + "//" + n_khas_no);

                    String split1 = polygon_area.getText().toString().split(": ")[1];
                    String split2 = split1.split(" Sq")[0];

                    intent.putExtra("polygon_area", split2);
                    startActivity(intent);


                } else {
                    Toast.makeText(this, getString(R.string.create_boundaries_around_point), Toast.LENGTH_SHORT).show();
                }


            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(MapChangeBoundaryScreen.this);
                builder.setMessage(getString(R.string.do_you_want_draw_boundaries_around_this_point));
                builder.setPositiveButton(getString(R.string.no), (dialog, id) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(this, SurveyFormScreen.class);

                    intent.putExtra("point", "editPoint");
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

                    intent.putExtra("gisId", gisId);
                    intent.putExtra("uid", uid);
                    intent.putExtra("objectId", objectId);
                    intent.putExtra("murabba_khasra_no", n_murr_no + "//" + n_khas_no);

                    intent.putExtra("polygon_area", 0.0);

                    startActivity(intent);
                });
                builder.setNegativeButton(getString(R.string.yes), (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


    }


    private void setMap() {


        ServiceFeatureTable boundaryServiceTable = new ServiceFeatureTable(feature_service_url_development);
        boundaryServiceTable.loadAsync();


        ServiceFeatureTable boundaryServiceTableTwo = new ServiceFeatureTable(feature_service_url_area_boundary);
        boundaryServiceTableTwo.loadAsync();


        //   ArcGISVectorTiledLayer arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(vector_tile_url);

        featureLayerBoundaryOne = new FeatureLayer(boundaryServiceTable);
        featureLayerBoundaryTwo = new FeatureLayer(boundaryServiceTableTwo);

        map.getOperationalLayers().add(featureLayerBoundaryOne);
        map.getOperationalLayers().add(featureLayerBoundaryTwo);
        //  map.getOperationalLayers().add(arcGISVectorTiledLayer);


        change_boundary_map_view.getGraphicsOverlays().add(graphicsOverlay);

        change_boundary_map_view.setMap(map);


        locationDisplay = change_boundary_map_view.getLocationDisplay();

        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        change_boundary_map_view.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(x, y, SpatialReferences.getWgs84()), 2050);
        locationDisplay.startAsync();


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