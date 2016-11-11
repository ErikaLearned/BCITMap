package a00954431.ca.bcit.comp3717.bcit_map;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class BCIT_Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int currentFloor;
    private ArrayList<PolygonOptions> buildingShapes;
    private Polygon_Shapes shape;

    GroundOverlay groundOverlaysSE[] = new GroundOverlay[14];

    GroundOverlay se12Overlay;
    GroundOverlay se14Overlay;

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

        // Move camera to middle of BCIT Burnaby campus
        LatLng BCIT = new LatLng(49.250899, -123.001488);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(BCIT));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( 16.5f ));

        currentFloor = R.id.floor1;
        setFloor(findViewById(R.id.floor1));

        // Init polygon shapes over buildings
        buildingShapes = new ArrayList<PolygonOptions>();
        shape = new Polygon_Shapes();
        addBuildingOverlays(buildingShapes, shape);
        initBuildingOverlays(buildingShapes);
    }

    /*
     * Adds polygon options to the arraylist
     */
    private void addBuildingOverlays(ArrayList<PolygonOptions> list, Polygon_Shapes shape) {
        // SE
        list.add(shape.getSE12());
        list.add(shape.getSE14());
        // SW
        list.add(shape.getSW9());
        // NW
        list.add(shape.getNW3());
        // NE
        list.add(shape.getNE9());
    }

    /*
     * Adds polygon options to the map and make them visible.
     */
    private void initBuildingOverlays(ArrayList<PolygonOptions> list) {
        for(int i = 0; i < list.size(); i++) {
            mMap.addPolygon(list.get(i).visible(true));
        }
    }

    /*
     * Makes polygon options visible.
     */
    private void visiblePolyOptions(ArrayList<PolygonOptions> list) {
        for(int i = 0; i < list.size(); i++) {
            list.get(i).visible(true);
        }
    }

    /*
     * Makes polygon options invisible.
     */
    private void invisiblePolyOptions(ArrayList<PolygonOptions> list) {
        for(int i = 0; i < list.size(); i++) {
            list.get(i).visible(false);
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

                break;
            }
            case R.id.floor2: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f2m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249367, -123.001782),       // South west corner
                                new LatLng(49.250463, -123.001364)      // North east corner
                        ));
                groundOverlaysSE[7] =  mMap.addGroundOverlay(se12OverlayOption);
                break;
            }
            case R.id.floor3: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f3m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249367, -123.001782),       // South west corner
                                new LatLng(49.250463, -123.001364)      // North east corner
                        ));
                groundOverlaysSE[7] =  mMap.addGroundOverlay(se12OverlayOption);
                break;
            }
            case R.id.floor4: {

                break;
            }
            default: {
                // Nothing
                break;
            }
        }
    }

}