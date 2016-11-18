package a00954431.ca.bcit.comp3717.bcit_map;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

/**
 * Polygon_Shapes.java
 * Contains the polygon shapes needed to overlay on all the BCIT Burnaby buildings on a google map.
 *
 * Created by erika on 06/11/16.
 */

public final class Polygon_Shapes {
    // List containing all buildings
    private ArrayList<PolygonOptions> buildings;

    // Building colors by location (SE, SW, NE, NW)
    // Colors were chosen base on an existing map for consistency
    private int SE_Color = Color.rgb(255, 218, 185); // peach
    private int SW_Color = Color.rgb(255,215,0);     // yellow
    private int NE_Color = Color.rgb(50,205,50);     // green
    private int NW_Color = Color.rgb(218,112,214);   // purple

    // Universal configurations for each building
    private int strokeW = 0;

    // Buildings
        // SE
    private static PolygonOptions SE12;
    private static PolygonOptions SE14;
        // SW
    private static PolygonOptions SW9;
        // NW
    private static PolygonOptions NW3;
        // NE
    private static PolygonOptions NE9;


    /* TODO if there is time, adjust coordinates to encompass buildings more fully // buildings that are done: none */

    // Coordinates of NW3
    private static final LatLng NW3_swCorner = new LatLng(49.253090, -123.002794);
    private static final LatLng NW3_seCorner = new LatLng(49.253083, -123.002275);
    private static final LatLng NW3_neCorner = new LatLng(49.253405, -123.002276);
    private static final LatLng NW3_nwCorner = new LatLng(49.253398, -123.002791);
        // Long box to the left/west of NW3
    private static final LatLng NW_neCornerBox = new LatLng(49.253431, -123.002802);
    private static final LatLng NW_nwCornerBox = new LatLng(49.253426, -123.002958);
    private static final LatLng NW_swCornerBox = new LatLng(49.253056, -123.002955);
    private static final LatLng NW_seCornerBox = new LatLng(49.253067, -123.002794);


    // Coordinates of SW9
    private static final LatLng SW9_seCorner = new LatLng(49.248297, -123.002026);
    private static final LatLng SW9_neCorner = new LatLng(49.248835, -123.002031);
    private static final LatLng SW9_nwCorner = new LatLng(49.248840, -123.003509);
    private static final LatLng SW9_swCorner = new LatLng(49.248308, -123.003509);
        // Jut in at the bottom of SW9
    private static final LatLng SW9_swCornerBox = new LatLng(49.248310, -123.003101);
    private static final LatLng SW9_nwCornerBox = new LatLng(49.248350, -123.003099);
    private static final LatLng SW9_neCornerBox = new LatLng(49.248354, -123.002891);
    private static final LatLng SW9_seCornerBox = new LatLng(49.248300, -123.002887);


    // Coordinates of NE9
    private static final LatLng NE9_neCorner = new LatLng(49.254341, -122.998319);
    private static final LatLng NE9_nwCorner = new LatLng(49.254341, -122.998914);
    private static final LatLng NE9_swCorner = new LatLng(49.253776, -122.998906);
    private static final LatLng NE9_seCorner = new LatLng(49.253772, -122.998321);
        // Jut in on the east side of NE9
    private static final LatLng NE9_seCornerBox = new LatLng(49.253958, -122.998326);
    private static final LatLng NE9_swCornerBox = new LatLng(49.253962, -122.998514);
    private static final LatLng NE9_nwCornerBox = new LatLng(49.254209, -122.998503);
    private static final LatLng NE9_neCornerBox = new LatLng(49.254212, -122.998322);


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
        buildings = new ArrayList<PolygonOptions>();
        NE9 = new PolygonOptions()
                .add(NE9_neCorner,
                        NE9_nwCorner,
                        NE9_swCorner,
                        NE9_seCorner,
                        NE9_seCornerBox,
                        NE9_swCornerBox,
                        NE9_nwCornerBox,
                        NE9_neCornerBox)
                .fillColor(NE_Color)
                .strokeWidth(strokeW);
        buildings.add(NE9);

        SE12 = new PolygonOptions()
                .add(SE12_nwCorner,
                     SE12_swCorner,
                     SE12_seCorner,
                     SE12_neCorner)
                .fillColor(SE_Color)
                .strokeWidth(strokeW);
        buildings.add(SE12);

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
        buildings.add(SE14);

        SW9 = new PolygonOptions()
                .add(SW9_seCorner,
                        SW9_neCorner,
                        SW9_nwCorner,
                        SW9_swCorner,
                        SW9_swCornerBox,
                        SW9_nwCornerBox,
                        SW9_neCornerBox,
                        SW9_seCornerBox)
                .fillColor(SW_Color)
                .strokeWidth(strokeW);
        buildings.add(SW9);

        NW3 = new PolygonOptions()
                .add(NW3_swCorner,
                        NW3_seCorner,
                        NW3_neCorner,
                        NW3_nwCorner,
                        NW_neCornerBox,
                        NW_nwCornerBox,
                        NW_swCornerBox,
                        NW_seCornerBox)
                .fillColor(NW_Color)
                .strokeWidth(strokeW)
                ;
        buildings.add(NW3);
    }

    /*
     * Make all the building polygon overlays invisible.
     */
    public void turnOffBuildings(ArrayList<Polygon> poly) {
        for (Polygon p: poly) {
            p.setVisible(false);
        }
    }

    /*
     * Make all the building polygon overlays visible.
     */
    public void turnOnBuildings(ArrayList<Polygon> poly) {
        for (Polygon p: poly) {
            p.setVisible(true);
        }
    }

    /*
     * Make all the building polygon overlays visible.
     */
    public ArrayList<PolygonOptions> getBuildings() {
        return buildings;
    }

    /*
     * Getter: Nw3
     */
    public PolygonOptions getNW3() {
        return NW3;
    }

    /*
     * Getter: NE9
     */
    public PolygonOptions getNE9() {
        return NE9;
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
     * Getter: SW9
     */
    public PolygonOptions getSW9() {
        return SW9;
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
