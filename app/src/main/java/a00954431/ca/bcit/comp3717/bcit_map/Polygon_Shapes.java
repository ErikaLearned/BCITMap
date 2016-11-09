package a00954431.ca.bcit.comp3717.bcit_map;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 * Polygon_Shapes.java
 * Contains the polygon shapes needed to overlay on all the BCIT Burnaby buildings on a google map.
 *
 * Created by erika on 06/11/16.
 */

public final class Polygon_Shapes {
    // Building colors by location (SE, SW, NE, NW)
    // Colors were chosen base on an existing map for consistency
    private int SE_Color = Color.rgb(255, 218, 185); // peach
    private int SW_Color = Color.rgb(255,215,0); // yellow
    private int NE_Color = Color.rgb(50,205,50); // green
    private int NW_Color = Color.rgb(218,112,214); // purple

    // Buildings
    private static PolygonOptions SE12;

    // Coordinates of SE12
    private static final LatLng SE12_nwCorner = new LatLng(49.250432, -123.001742);
    private static final LatLng SE12_swCorner = new LatLng(49.249406, -123.001731);
    private static final LatLng SE12_seCorner = new LatLng(49.249406, -123.001401);
    private static final LatLng SE12_neCorner = new LatLng(49.249406, -123.001401);

    /*
     * Polygon_Shapes constructor.
     */
    public Polygon_Shapes(){
        SE12 = new PolygonOptions()
                .add(SE12_nwCorner,
                     SE12_swCorner,
                     SE12_seCorner,
                     SE12_neCorner)
                .fillColor(SE_Color);

    }

    /*
     * Getter: SE12
     */
    public PolygonOptions getSE12() {
        return SE12;
    }

    /*
     * Getter: SE color
     */
    public int getSE_Color() {
        return SE_Color;
    }

    /*
     * Getter: SW color
     */
    public int getSW_Color() {
        return SW_Color;
    }

    /*
     * Getter: NE color
     */
    public int getNE_Color() {
        return NE_Color;
    }

    /*
     * Getter: NW color
     */
    public int getNW_Color() {
        return NW_Color;
    }


}
