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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class BCIT_Map extends FragmentActivity implements OnMapReadyCallback,
                                                          GoogleMap.OnCameraMoveListener {
    private String TAG = BCIT_Map.class.getName();
    private GoogleMap mMap;
    private int currentFloor;
    private ArrayList<Polygon> buildings;
    private Polygon_Shapes shape;
    private float zoom;
    private float turnOffAtZoom = (float) 17.5;
    private ArrayList<Marker> buildingMarkers;
    private Building_Markers markers;

    GroundOverlay groundOverlaysSE[] = new GroundOverlay[14];

    ArrayList<ArrayList<Node>> paths = new ArrayList<ArrayList<Node>>();
    ArrayList<Polyline> lines = new ArrayList<Polyline>();
    ArrayList<Marker> directionMarkers = new ArrayList<Marker>();

    // Create a LatLngBounds that includes the BCIT Burnaby campus.
    // TODO update burnabyCampus boundaries to better encompass the campus.
    private LatLngBounds burnabyCampus = new LatLngBounds(
            new LatLng(49.2432, -123.006932), new LatLng(49.253, -122.99998));


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


        currentFloor = R.id.floor1;
        setFloor(findViewById(R.id.floor1));

        // Init polygon shapes over buildings
        shape = new Polygon_Shapes();
        buildings = new ArrayList<Polygon>();
        initBuildingOverlays(shape.getBuildings(), buildings);

        // Set labels on each building
        markers = new Building_Markers(this, mMap);
        buildingMarkers = markers.getBuildingNameMarkers();

        paths.clear();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String[] from = extras.getString("fromLocation").split("-");
            String[] to = extras.getString("toLocation").split("-");
            Node nFrom = NodeDir.mapDB.getNodeByRoom(from[0], from[1]);
            if (nFrom == null) {
                nFrom = NodeDir.mapDB.getNodeByName(from[1]);
            }
            Node nTo = NodeDir.mapDB.getNodeByRoom(to[0], to[1]);
            if (nTo == null) {
                nTo = NodeDir.mapDB.getNodeByName(to[1]);
            }
            generateDirections(nFrom, nTo);
        } else {
            setMarkers();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("X", latLng.toString());

            }
        });

    }

    @Override
    public void onCameraMove() {
        zoom = mMap.getCameraPosition().zoom;

        if (zoom > turnOffAtZoom) {
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
        for (int i = 0; i < list.size(); i++) {
            Polygon poly = mMap.addPolygon(list.get(i));
            buildings.add(poly);
        }
    }

    public void setFloor(View v) {
        int floorNum = v.getId();
        Button fc = (Button) findViewById(currentFloor);
        fc.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        v.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        currentFloor = floorNum;
        for (GroundOverlay go : groundOverlaysSE) {
            if (go != null) {
                go.remove();
            }
        }
        switch (currentFloor) {
            case R.id.floor1: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f1m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249360, -123.002164),     // South west corner
                                new LatLng(49.250528, -123.001075)      // North east corner
                        ));
                groundOverlaysSE[7] = mMap.addGroundOverlay(se12OverlayOption);

                if (!paths.isEmpty()) {
                    drawDirections(1);
                }
                break;
            }
            case R.id.floor2: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f2m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249365086234455, -123.00208780914544),       // South west corner
                                new LatLng(49.2505254554666, -123.00113193690777)      // North east corner
                                //testpost
                        ));
                groundOverlaysSE[7] = mMap.addGroundOverlay(se12OverlayOption);
                if (!paths.isEmpty()) {
                    drawDirections(2);
                }
                break;
            }
            case R.id.floor3: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f3m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249368, -123.001789),       // South west corner
                                new LatLng(49.250480, -123.001330)      // North east corner
                        ));
                groundOverlaysSE[7] = mMap.addGroundOverlay(se12OverlayOption);
                if (!paths.isEmpty()) {
                    drawDirections(3);
                }
                break;
            }
            case R.id.floor4: {
                GroundOverlayOptions se12OverlayOption = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.se12f4m))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(49.249377, -123.001834),     // South west corner
                                new LatLng(49.250505, -123.001316)      // North east corner
                        ));
                groundOverlaysSE[7] = mMap.addGroundOverlay(se12OverlayOption);

                if (!paths.isEmpty()) {
                    drawDirections(4);
                }
                break;
            }
            default: {
                // Nothing
                break;
            }
        }
    }

    protected void generateDirections(Node start, Node to) {

        LinkedBlockingQueue<Node> path = findPath(start, to, false);

        IconGenerator icon = new IconGenerator(this);

        Bitmap iconBitmap = icon.makeIcon("CURRENT");
        mMap.addMarker(new MarkerOptions().position(start.loc))
                .setIcon(BitmapDescriptorFactory.fromBitmap(iconBitmap));

        String iconTag;
        if (!to.roomNum.equals("") && !to.building.equals("")) {
            iconTag = to.building + "-" + to.roomNum;
        } else {
            iconTag = to.roomName;
        }
        iconBitmap = icon.makeIcon(iconTag);
        mMap.addMarker(new MarkerOptions().position(to.loc))
                .setIcon(BitmapDescriptorFactory.fromBitmap(iconBitmap));

        paths.clear();
        directionMarkers.clear();
        int floor = 0;
        int pathNum = -1;
        for (Node node : path) {
            if (node.floor != floor) {

                paths.add(new ArrayList<Node>());

                if (node.floor > floor) {
                    iconBitmap = icon.makeIcon("Down to floor " + floor);
                } else {
                    iconBitmap = icon.makeIcon("Up to floor " + floor);
                }

                Marker mark = mMap.addMarker(new MarkerOptions().position(node.loc));
                mark.setIcon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
                mark.setVisible(false);
                mark.setTag(node.floor);
                directionMarkers.add(mark);
                floor = node.floor;
                pathNum++;
            }
            paths.get(pathNum).add(node);
        }
        directionMarkers.remove(0);
        switch (start.floor) {
            case 1:
                setFloor(findViewById(R.id.floor1));
                break;
            case 2:
                setFloor(findViewById(R.id.floor2));
                break;
            case 3:
                setFloor(findViewById(R.id.floor3));
                break;
            case 4:
                setFloor(findViewById(R.id.floor4));
                break;
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start.loc, 18.0f));

    }

    public void drawDirections(int floor) {
        for (Polyline line : lines) {
            line.remove();
        }
        lines.clear();
        for (ArrayList<Node> path : paths) {
            if (path.get(0).floor == floor) {
                PolylineOptions options = new PolylineOptions().width(5).color(Color.RED);
                for (Node node : path) {
                    options.add(node.loc);
                }
                Polyline line = mMap.addPolyline(options);
                lines.add(line);
            }
        }
        for (Marker mark : directionMarkers) {
            if ((int) mark.getTag() == floor) {
                mark.setVisible(true);
            } else {
                mark.setVisible(false);
            }
        }
    }

    // Comparision method for A* path finding.
    float heuristic(Node a, Node b) {
        return a.getDistanceTo(b);
    }

    // Gets unit path by A* path finding.
    public LinkedBlockingQueue<Node> findPath(Node start, Node end, boolean noOutside) {
        Comparator<Node> comparator = new NodeLengthComparator();
        LinkedBlockingQueue<Node> path = new LinkedBlockingQueue<Node>();
        PriorityQueue<Node> frontier = new PriorityQueue<Node>(11, comparator);
        Set<Integer> visited = new HashSet<Integer>();
        frontier.add(start);
        HashMap<Node, Node> cameFrom = new HashMap<Node, Node>();
        HashMap<Node, Integer> cost = new HashMap<Node, Integer>();
        cameFrom.put(start, null);
        cost.put(start, 0);
        while (!frontier.isEmpty()) {
            Node current = frontier.peek();
            //Log.d("X", current.key.toString());
            if (current.key == end.key) {
                break;
            }
            frontier.poll();
            for (Integer nei : current.neighbours) {
                Node neiNode = NodeDir.mapDB.getNodeByKey(nei);
                if ((!noOutside || !neiNode.outside) && !visited.contains(neiNode.key)) {
                    int newCost = cost.get(current) + 1;
                    if (!cost.containsKey(neiNode) || newCost < cost.get(neiNode)) {
                        cost.put(neiNode, newCost);
                        neiNode.distance = newCost + heuristic(end, neiNode);
                        cameFrom.put(neiNode, current);
                        visited.add(neiNode.key);
                        frontier.add(neiNode);
                    }
                }
            }
        }
        // Converts path to deque for use
        Node current = frontier.peek();
        while (current != start) {
            path.add(current);
            Node nep = cameFrom.get(current);
            current = nep;
        }
        path.add(start);
        return path;
    }


    /*
     * Sets bitmap markers to each building.
     */
    private void setMarkers() {
        //TODO Why do you call setMarkers() in the if else statement above.
        //If this is empty it works, if it's not it doesn't. WTF pls educate, Jacob!
    }
}