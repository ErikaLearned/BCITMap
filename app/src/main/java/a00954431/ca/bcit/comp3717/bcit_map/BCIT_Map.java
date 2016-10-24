package a00954431.ca.bcit.comp3717.bcit_map;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class BCIT_Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Create a LatLngBounds that includes the BCIT Burnaby campus.
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
        // Move camera to middle of BCIT Burnaby campus
        LatLng BCIT = new LatLng(49.250717, -123.000490);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(BCIT));
        // Constrain the camera target to the BCIT Burnaby campus
        mMap.setLatLngBoundsForCameraTarget(burnabyCampus);
    }
}
