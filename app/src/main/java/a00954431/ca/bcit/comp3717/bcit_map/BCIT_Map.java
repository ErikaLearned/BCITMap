package a00954431.ca.bcit.comp3717.bcit_map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

public class BCIT_Map extends FragmentActivity implements OnMapReadyCallback,
                                                          GoogleMap.OnCameraMoveListener {
    private String TAG = BCIT_Map.class.getName();
    private GoogleMap mMap;
    private int currentFloor;
    private ArrayList<Polygon> buildings;
    private Polygon_Shapes shape;
    private float zoom;
    private float turnOffAtZoom = (float)17.5;
    private ArrayList<Marker> buildingMarkers;
    private Building_Markers markers;

    GroundOverlay groundOverlaysSE[] = new GroundOverlay[14];

    GroundOverlay se12Overlay;
    GroundOverlay se14Overlay;

    // Create a LatLngBounds that includes the BCIT Burnaby campus.
    // TODO update burnabyCampus boundaries to better encompass the campus.
    private LatLngBounds burnabyCampus = new LatLngBounds(
            new LatLng(49.2432,-123.006932), new LatLng(49.253,-122.99998));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcit__map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
        }

        mMap.setOnCameraMoveListener(this);

        // Move camera to middle of BCIT Burnaby campus
        LatLng BCIT = new LatLng(49.250899, -123.001488);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(BCIT));
		
        // Constrain the camera target to the BCIT Burnaby campus
        mMap.setLatLngBoundsForCameraTarget(burnabyCampus);

        // Set labels on each building
        markers = new Building_Markers(this, mMap);
        buildingMarkers = markers.getBuildingNameMarkers();

        currentFloor = R.id.floor1;
        setFloor(findViewById(R.id.floor1));

        // Init polygon shapes over buildings
        shape = new Polygon_Shapes();
        buildings = new ArrayList<Polygon>();
        initBuildingOverlays(shape.getBuildings(), buildings);
        // TODO Make building overlays disappear when at a closer zoom level or when accessed by directions menu
    }

    @Override
    public void onCameraMove() {
        zoom = mMap.getCameraPosition().zoom;

        if(zoom > turnOffAtZoom) {
            shape.turnOffBuildings(buildings);
            markers.turnOffMarkers(buildingMarkers);
        } else {
            shape.turnOnBuildings(buildings);
            markers.turnOnMarkers(buildingMarkers);
        }
    }


    /*
     * Adds polygon options to the map.
     */
    private void initBuildingOverlays(ArrayList<PolygonOptions> list,
                                      ArrayList<Polygon> buildings) {
        for(int i = 0; i < list.size(); i++) {
            Polygon poly = mMap.addPolygon(list.get(i));
            buildings.add(poly);
        }
    }


    public void setFloor(View v) {
        int floorNum = v.getId();
        Button fc = (Button) findViewById(currentFloor);
        fc.getBackground().setColorFilter(Color.LTGRAY,PorterDuff.Mode.MULTIPLY);
        v.getBackground().setColorFilter(Color.BLUE,PorterDuff.Mode.MULTIPLY);
        currentFloor = floorNum;
        for(GroundOverlay go : groundOverlaysSE) {
            if (go != null) {
                go.remove();
            }
        }
        switch(currentFloor) {
            case R.id.floor1: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f1m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249360, -123.002164),     // South west corner
                                new LatLng(49.250528, -123.001075)      // North east corner
                        ));
                groundOverlaysSE[7] =  mMap.addGroundOverlay(se12OverlayOption);
                break;
            }
            case R.id.floor2: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f2m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249360, -123.002164),     // South west corner
                                new LatLng(49.250528, -123.001075)      // North east corner
                        ));
                groundOverlaysSE[7] =  mMap.addGroundOverlay(se12OverlayOption);
                break;
            }
            case R.id.floor3: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f3m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249377, -123.001834),     // South west corner
                                new LatLng(49.250505, -123.001316)      // North east corner
                        ));
                groundOverlaysSE[7] =  mMap.addGroundOverlay(se12OverlayOption);
                break;
            }
            case R.id.floor4: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f4m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249377, -123.001834),     // South west corner
                                new LatLng(49.250505, -123.001316)      // North east corner
                        ));
                groundOverlaysSE[7] =  mMap.addGroundOverlay(se12OverlayOption);
                break;
            }
            default: {
                // Nothing
                break;
            }
        }
    }

}