package a00954431.ca.bcit.comp3717.bcit_map;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

/**
 * Created by erika on 17/11/16.
 */

public class Building_Markers {
    private Context applyToContext;
    private GoogleMap map;
    private IconGenerator icon;
    private Bitmap iconBitmap;
    private Marker marker;
    private ArrayList<Marker> buildingNameMarkers;

    public Building_Markers(Context context, GoogleMap m) {
        map = m;
        applyToContext = context;
        icon = new IconGenerator(applyToContext);
        buildingNameMarkers = new ArrayList<Marker>();

        // SE
        makeBuildingNameMarker(new LatLng(49.251029, -122.999057), "SE1");
        makeBuildingNameMarker(new LatLng(49.251337, -123.001294), "SE2\nGreat Hall");
        makeBuildingNameMarker(new LatLng(49.251242, -123.000092), "SE4");
        makeBuildingNameMarker(new LatLng(49.250836, -123.000462), "SE6");
        makeBuildingNameMarker(new LatLng(49.250692, -123.001412), "SE8");
        makeBuildingNameMarker(new LatLng(49.249782, -123.000645), "SE10");
        makeBuildingNameMarker(new LatLng(49.249926, -123.001573), "SE12");
        makeBuildingNameMarker(new LatLng(49.249379, -123.000811), "SE14\nLibrary");
        makeBuildingNameMarker(new LatLng(49.248665, -123.000978), "SE16\nRecreation Center");
        makeBuildingNameMarker(new LatLng(49.248882, -122.998591), "SE19");
        makeBuildingNameMarker(new LatLng(49.246131, -122.998702), "SE30");
        makeBuildingNameMarker(new LatLng(49.243363, -122.998737), "SE40");
        makeBuildingNameMarker(new LatLng(49.243741, -122.999338), "SE41");
        makeBuildingNameMarker(new LatLng(49.243275, -122.999536), "SE42");
        makeBuildingNameMarker(new LatLng(49.242520, -122.998954), "SE50");
    }

    private void makeBuildingNameMarker(LatLng ll, String label) {
        iconBitmap = icon.makeIcon(label);
        marker = map.addMarker(new MarkerOptions().position(ll));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
        buildingNameMarkers.add(marker);
    }

    public void turnOnMarkers(ArrayList<Marker> list) {
        for (Marker mark: list) {
            mark.setVisible(true);
        }
    }

    public void turnOffMarkers(ArrayList<Marker> list) {
        for (Marker mark: list) {
            mark.setVisible(false);
        }
    }

    public ArrayList<Marker> getBuildingNameMarkers() {
        return buildingNameMarkers;
    }
}
