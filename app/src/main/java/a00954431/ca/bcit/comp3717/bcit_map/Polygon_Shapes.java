package a00954431.ca.bcit.comp3717.bcit_map;

import android.graphics.Color;
import android.util.Log;

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
    private int SW_Color = Color.rgb(255,215,0);     // yellow
    private int NE_Color = Color.rgb(50,205,50);     // green
    private int NW_Color = Color.rgb(218,112,214);   // purple

    // Universal configurations for each building
    private int strokeW = 0;

    // Buildings
    private static PolygonOptions SE12;
    private static PolygonOptions SE14;


    // TODO if there is time, adjust coordinates to encompass buildings more fully
    // TODO do buildings: SE12, SE14

    // Coordinates of SE12
    private static final LatLng SE12_nwCorner = new LatLng(49.250432, -123.001742);
    private static final LatLng SE12_swCorner = new LatLng(49.249406, -123.001731);
    private static final LatLng SE12_seCorner = new LatLng(49.249406, -123.001401);
    private static final LatLng SE12_neCorner = new LatLng(49.250440, -123.001412);

    // Coordinates of SE14
        // SE12 - SE14 connector
    private static final LatLng SE14_neCornerConnectJoin = new LatLng(49.249585, -123.000935);
    private static final LatLng SE14_neCornerConnect = new LatLng(49.249676, -123.000927);
    private static final LatLng SE14_nwCornerConnect = new LatLng(49.249685, -123.001367);
    private static final LatLng SE14_swCornerConnect = new LatLng(49.249508, -123.001364);
    private static final LatLng SE14_swCornerConnectJoin = new LatLng(49.249510, -123.001260);
        // Jut out at the bottom of SE14
    private static final LatLng SE14_swCornerBox = new LatLng(49.249274, -123.001265);
    private static final LatLng SE14_seCornerBox = new LatLng(49.249274, -123.001117);
    private static final LatLng SE14_neCornerBoxJoin = new LatLng(49.249304, -123.001126);
        // East end of SE14
    private static final LatLng SE14_seCorner = new LatLng(49.249300, -123.000213);
    private static final LatLng SE14_neCorner = new LatLng(49.249588, -123.000195);

    /*
     * Polygon_Shapes constructor.
     */
    public Polygon_Shapes(){
        SE12 = new PolygonOptions()
                .add(SE12_nwCorner,
                     SE12_swCorner,
                     SE12_seCorner,
                     SE12_neCorner)
                .fillColor(SE_Color)
                .strokeWidth(strokeW);

        SE14 = new PolygonOptions()
                .add(SE14_neCornerConnectJoin,
                        SE14_neCornerConnect,
                        SE14_nwCornerConnect,
                        SE14_swCornerConnect,
                        SE14_swCornerConnectJoin,
                        SE14_swCornerBox,
                        SE14_seCornerBox,
                        SE14_neCornerBoxJoin,
                        SE14_seCorner,
                        SE14_neCorner)
                .fillColor(SE_Color)
                .strokeWidth(strokeW);
    }

    /*
     * Getter: SE12
     */
    public PolygonOptions getSE12() {
        return SE12;
    }

    /*
     * Getter: SE14
     */
    public PolygonOptions getSE14() {
        return SE14;
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
