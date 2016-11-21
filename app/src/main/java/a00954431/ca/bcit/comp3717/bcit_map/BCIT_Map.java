package a00954431.ca.bcit.comp3717.bcit_map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class BCIT_Map extends FragmentActivity implements OnMapReadyCallback,
                                                          GoogleMap.OnCameraMoveListener{
    private String TAG = BCIT_Map.class.getName();
    private GoogleMap mMap;
    private ArrayList<Polygon> buildings;
    private Polygon_Shapes shape;
    private ArrayList<Marker> buildingMarkers;
    private Building_Markers markers;
    private Building_Floorplan building_floorplan;
    private TreeMap<String, GroundOverlayOptions> floorplans;

    float turnOffAtZoom = (float) 17.5;
    float turnOffBuildingLabels = (float) 16.5;

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

        // Init polygon shapes over buildings
        shape = new Polygon_Shapes();
        buildings = new ArrayList<Polygon>();
        initBuildingOverlays(shape.getBuildings(), buildings);

        // Set labels on each building
        markers = new Building_Markers(this, mMap);
        buildingMarkers = markers.getBuildingNameMarkers();

        // Grab all floorplans
        building_floorplan = new Building_Floorplan();
        floorplans = building_floorplan.getFloorPlans();
        boolean contains = floorplans.containsKey("se12f4m");

        // Setup floor spinner
        Spinner spinner = (Spinner) findViewById(R.id.floor_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bcit_floors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                setFloor(position+1);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // Do Nothing
            }
        });

        // Get from and to nodes if they exists
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
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Log.d(TAG, "askPermission()");
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    1
            );
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("X", latLng.toString());

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(this, "Permission denied cant get your location!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onCameraMove() {
        float zoom;
        zoom = mMap.getCameraPosition().zoom;

        if (zoom > turnOffAtZoom) {
            shape.turnOffBuildings(buildings);
            markers.turnOffMarkers(buildingMarkers);
            setBuildingsOverlayVisible(true);
        }  else if (zoom < turnOffBuildingLabels) {
            shape.turnOnBuildings(buildings);
            markers.turnOffMarkers(buildingMarkers);
        } else {
            shape.turnOnBuildings(buildings);
            markers.turnOnMarkers(buildingMarkers);
            setBuildingsOverlayVisible(false);
        }
    }

    /*
     * Sets all building overlays to be visible or hidden
     */
    private void setBuildingsOverlayVisible(boolean show) {
        for (GroundOverlay go : groundOverlaysSE) {
            if (go != null) {
                go.setVisible(show);
            }
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


    /*
     * Sets the floor to view, removes floor plans and draws new ones for floor
     * Redraw direction paths if any
     */
    public void setFloor(int floorNum) {
        GroundOverlayOptions plan;
        for (GroundOverlay go : groundOverlaysSE) {
            if (go != null) {
                go.remove();
            }
        }
        switch (floorNum) {
            case 1: {
                plan = floorplans.get("se12f1m");
                groundOverlaysSE[7] = mMap.addGroundOverlay(plan);
                plan = floorplans.get("se14f1m");
                groundOverlaysSE[8] = mMap.addGroundOverlay(plan);
                if (!paths.isEmpty()) {
                    drawDirections(1);
                }
                break;
            }
            case 2: {
                plan = floorplans.get("se12f2m");
                groundOverlaysSE[7] = mMap.addGroundOverlay(plan);
                plan = floorplans.get("se14f2m");
                groundOverlaysSE[8] = mMap.addGroundOverlay(plan);
                if (!paths.isEmpty()) {
                    drawDirections(2);
                }
                break;
            }
            case 3: {
                plan = floorplans.get("se12f3m");
                groundOverlaysSE[7] = mMap.addGroundOverlay(plan);
                plan = floorplans.get("se14f3m");
                groundOverlaysSE[8] = mMap.addGroundOverlay(plan);
                if (!paths.isEmpty()) {
                    drawDirections(3);
                }
                break;
            }
            case 4: {
                plan = floorplans.get("se12f4m");
                groundOverlaysSE[7] = mMap.addGroundOverlay(plan);

                if (!paths.isEmpty()) {
                    drawDirections(4);
                }
                break;
            }
            case 5: {

                if (!paths.isEmpty()) {
                    drawDirections(5);
                }
                break;
            }
            default: {
                drawDirections(0);
                break;
            }
        }
        if (mMap.getCameraPosition().zoom > turnOffAtZoom) {
            setBuildingsOverlayVisible(true);
        } else {
            setBuildingsOverlayVisible(false);
        }
    }

    /*
    *   Generates the directions for the given nodes
    */
    protected void generateDirections(Node start, Node to) {

        LinkedBlockingQueue<Node> path = findPath(start, to, false);
        if (path != null) {
            IconGenerator icon = new IconGenerator(this);

            // Set markers
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

            // Add direction markers and lines to arrays
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
            Spinner spinner = (Spinner) findViewById(R.id.floor_spinner);
            spinner.setSelection(start.floor-1);

            // Zoom on start position
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start.loc, 18.0f));
        } else {
            // If failed to find path
            paths.clear();
            directionMarkers.clear();
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Direction Error");
            alertDialog.setMessage("We could not find a path to your destination.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    /*
    *   Draws directions for floor
    */
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

    /*
     * Comparision method for A* path finding.
     */
    float heuristic(Node a, Node b) {
        return a.getDistanceTo(b);
    }

    /*
     *  Gets path by A* path finding.
     */
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
        // Loop until exhausted nodes to check, or found end node
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
        if (frontier.isEmpty()) {
            return null; // Failed to find a path
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

}
