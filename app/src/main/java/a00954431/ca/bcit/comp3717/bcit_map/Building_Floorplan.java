package a00954431.ca.bcit.comp3717.bcit_map;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.TreeMap;

/**
 * Created by erika on 18/11/16.
 */

public class Building_Floorplan {
    private TreeMap<String, GroundOverlayOptions> plans;

    public Building_Floorplan() {
        plans = new TreeMap<String, GroundOverlayOptions>();

        // SE12 - floors 1 to 4
        buildFloorplan(new LatLng(49.249457, -123.002164),
                       new LatLng(49.250539, -123.001253),
                       R.drawable.se12f1m,
                       "se12f1m");
        buildFloorplan(new LatLng(49.249457, -123.002164),
                       new LatLng(49.250528, -123.001075),
                       R.drawable.se12f2m,
                       "se12f2m");
        buildFloorplan(new LatLng(49.249457, -123.001834),
                       new LatLng(49.250505, -123.001316),
                       R.drawable.se12f3m,
                       "se12f3m");
        buildFloorplan(new LatLng(49.249457, -123.001834),
                       new LatLng(49.250505, -123.001316),
                       R.drawable.se12f4m,
                       "se12f4m");

        // SE14 (library) - floors 1 - 3
        buildFloorplan(new LatLng(49.249255, -123.001415),
                       new LatLng(49.249782, -123.000137),
                       R.drawable.se14f1m,
                       "se14f1m");
        buildFloorplan(new LatLng(49.249249, -123.001412),
                       new LatLng(49.249782, -123.000137),
                       R.drawable.se14f2m,
                       "se14f2m");
        buildFloorplan(new LatLng(49.249249, -123.001412),
                       new LatLng(49.249740, -123.000137),
                       R.drawable.se14f3m,
                       "se14f3m");
    }

    /*
     * @param resource R.drawable.(floorplan to draw)
     */
    private void buildFloorplan(LatLng SW, LatLng NE, int resource, String key) {
        GroundOverlayOptions overlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(resource))
                .positionFromBounds(new LatLngBounds(
                        SW,     // South west corner
                        NE      // North east corner
                ));
        plans.put(key, overlay);
    }

    public TreeMap<String, GroundOverlayOptions> getFloorPlans() {
        return plans;
    }
}
