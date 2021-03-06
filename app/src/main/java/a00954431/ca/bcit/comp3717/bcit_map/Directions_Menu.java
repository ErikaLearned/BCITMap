package a00954431.ca.bcit.comp3717.bcit_map;

import android.*;
import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Directions_Menu extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    ArrayList<Node> roomList = new ArrayList<Node>();
    ArrayList<Node> roomList_Filtered = new ArrayList<Node>();
    ArrayList<Node> dbList;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng currentLocation;

    private Integer startKey = null;
    private Integer endKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        final Button dirButt = (Button) findViewById(R.id.getDirections);
        dirButt.setEnabled(false);
        final CheckBox outdoor = (CheckBox) findViewById(R.id.checkBoxOutdoors);
        final CheckBox locationcheck = (CheckBox) findViewById(R.id.checkboxlocation);
        final Spinner areaSpinner = (Spinner) findViewById(R.id.detectspinner);
        final EditText fromText = (EditText)findViewById(R.id.fromSearch);
        final EditText toText = (EditText)findViewById(R.id.toSearch);
        final LinearLayout searchlayout = (LinearLayout)findViewById(R.id.searchlayout);
        final ListView listViewSearch = (ListView)findViewById(R.id.listViewSearch);


        searchlayout.setVisibility(View.GONE);
        areaSpinner.setVisibility(View.GONE);

        dbList = NodeDir.mapDB.getAllNodes();

        for (Node node : dbList) {
            if (!node.roomNum.equals("") || !node.roomName.equals("")) {
              roomList.add(node);
            }
        }

        listViewSearch.setAdapter(new ArrayAdapter<Node>(this, android.R.layout.simple_list_item_1, roomList));

        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Node n = (Node)parent.getItemAtPosition(position);
                String item = n.toString();
                View v = getCurrentFocus();
                if (v == null) {
                    listViewSearch.setSelection(0);
                    Toast.makeText(Directions_Menu.this, "Focus Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (v.equals(findViewById(R.id.fromSearch))) {
                    startKey = n.key;
                    fromText.setText(item);
                    fromText.clearFocus();
                } else if (v.equals(findViewById(R.id.toSearch))) {
                    endKey = n.key;
                    toText.setText(item);
                    toText.clearFocus();
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });


        locationcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    fromText.setEnabled(false);
                    fromText.setText("");
                    fromText.setHint("Set floor");
                    areaSpinner.setSelection(0);
                   runLocation();
                    areaSpinner.setVisibility(View.VISIBLE);
                } else {
                    areaSpinner.setVisibility(View.GONE);
                    fromText.setEnabled(true);
                    fromText.setText("");
                    fromText.setHint("From");
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.detect_areas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(adapter);
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Node bestNode = null;
                    double bestSum = Double.POSITIVE_INFINITY;
                    runLocation();
                    LatLng local = currentLocation;

                    if (local == null) {
                        fromText.setText("");
                        fromText.setHint("Can't get location");
                        areaSpinner.setSelection(0);
                        return;
                    }
                    for(Node node : dbList) {
                        if (position < 6) {
                            if (node.floor != position) {
                                continue;
                            }
                        } else if (position == 6 && !node.outside) {
                            continue;
                        }
                        double sum =  Math.abs(node.loc.latitude - local.latitude) + Math.abs(node.loc.longitude - local.longitude);
                        if (sum < bestSum) {
                            bestSum = sum;
                            bestNode = node;
                        }
                    }
                    if (bestNode == null) {
                        fromText.setText("");
                        fromText.setHint("Can't get location");
                    } else {
                        Log.d("X", bestNode.toString());
                        fromText.setHint("From");
                        fromText.setText("Your location");
                        startKey = bestNode.key;
                        setStartButton();
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // Do Nothing
            }
        });

        fromText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ListView mListView1 = (ListView)findViewById(R.id.listViewSearch);
                EditText fromText = (EditText)findViewById(R.id.fromSearch);
                String search = fromText.getText().toString().toLowerCase();
                if(!search.equals("") ) {
                   roomList_Filtered.clear();
                    for(Node room : roomList) {
                        if(room.toString().toLowerCase().contains(search)) {
                            roomList_Filtered.add(room);
                        }
                    }
                   mListView1.setAdapter(new ArrayAdapter<Node>(getBaseContext(), android.R.layout.simple_list_item_1, roomList_Filtered));

                } else {
                    startKey = null;
                    roomList_Filtered.clear();
                    mListView1.setAdapter(new ArrayAdapter<Node>(getBaseContext(), android.R.layout.simple_list_item_1, roomList));
                }
                setStartButton();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Do Nothing
            }

        });

        fromText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    toText.setVisibility(View.GONE);
                    dirButt.setVisibility(View.GONE);
                    outdoor.setVisibility(View.GONE);
                    locationcheck.setVisibility(View.GONE);
                    areaSpinner.setVisibility(View.GONE);
                    searchlayout.setVisibility(View.VISIBLE);
                    ListView mListView1 = (ListView)findViewById(R.id.listViewSearch);
                    roomList_Filtered.clear();
                    mListView1.setAdapter(new ArrayAdapter<Node>(getBaseContext(), android.R.layout.simple_list_item_1, roomList));

                    int dpValue = 5; // margin in dips
                    float d = getApplicationContext().getResources().getDisplayMetrics().density;
                    int margin = (int)(dpValue * d); // margin in pixels

                    EditText ev = (EditText)findViewById(R.id.fromSearch);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ev.getLayoutParams();
                    params.setMargins(0, margin, 0, 0); //substitute parameters for left, top, right, bottom
                    ev.setLayoutParams(params);

                }else {
                    toText.setVisibility(View.VISIBLE);
                    dirButt.setVisibility(View.VISIBLE);
                    outdoor.setVisibility(View.VISIBLE);
                    locationcheck.setVisibility(View.VISIBLE);
                    if (locationcheck.isChecked()) {
                        areaSpinner.setVisibility(View.VISIBLE);
                    }
                    searchlayout.setVisibility(View.GONE);

                    int dpValue = 76; // margin in dips
                    float d = getApplicationContext().getResources().getDisplayMetrics().density;
                    int margin = (int)(dpValue * d); // margin in pixels

                    EditText ev = (EditText)findViewById(R.id.fromSearch);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ev.getLayoutParams();
                    params.setMargins(0, margin, 0, 0); //substitute parameters for left, top, right, bottom
                    ev.setLayoutParams(params);
                }
            }
        });

        fromText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    fromText.clearFocus();

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(fromText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        toText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               ListView mListView1 = (ListView)findViewById(R.id.listViewSearch);
                EditText fromText = (EditText)findViewById(R.id.toSearch);
                String search = fromText.getText().toString().toLowerCase();
                if(!search.equals("") ) {
                    roomList_Filtered.clear();
                    for(Node room : roomList) {
                        if(room.toString().toLowerCase().contains(search)) {
                            roomList_Filtered.add(room);
                        }
                    }
                    mListView1.setAdapter(new ArrayAdapter<Node>(getBaseContext(), android.R.layout.simple_list_item_1, roomList_Filtered));

                } else {
                    endKey = null;
                    roomList_Filtered.clear();
                    mListView1.setAdapter(new ArrayAdapter<Node>(getBaseContext(), android.R.layout.simple_list_item_1, roomList));
                }
                setStartButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do Nothing
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Do Nothing
            }
        });

        toText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    fromText.setVisibility(View.GONE);
                    dirButt.setVisibility(View.GONE);
                    outdoor.setVisibility(View.GONE);
                    locationcheck.setVisibility(View.GONE);
                    areaSpinner.setVisibility(View.GONE);
                    searchlayout.setVisibility(View.VISIBLE);
                    ListView mListView1 = (ListView)findViewById(R.id.listViewSearch);
                    roomList_Filtered.clear();
                    mListView1.setAdapter(new ArrayAdapter<Node>(getBaseContext(), android.R.layout.simple_list_item_1, roomList));

                    int dpValue = 5; // margin in dips
                    float d = getApplicationContext().getResources().getDisplayMetrics().density;
                    int margin = (int)(dpValue * d); // margin in pixels

                    EditText ev = (EditText)findViewById(R.id.toSearch);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ev.getLayoutParams();
                    params.setMargins(0, margin, 0, 0); //substitute parameters for left, top, right, bottom
                    ev.setLayoutParams(params);

                }else {
                    fromText.setVisibility(View.VISIBLE);
                    dirButt.setVisibility(View.VISIBLE);
                    outdoor.setVisibility(View.VISIBLE);
                    locationcheck.setVisibility(View.VISIBLE);
                    if (locationcheck.isChecked()) {
                        areaSpinner.setVisibility(View.VISIBLE);
                    }
                    searchlayout.setVisibility(View.GONE);

                    int dpValue = 146; // margin in dips
                    float d = getApplicationContext().getResources().getDisplayMetrics().density;
                    int margin = (int)(dpValue * d); // margin in pixels

                    EditText ev = (EditText)findViewById(R.id.toSearch);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ev.getLayoutParams();
                    params.setMargins(0, 0, 0, margin); //substitute parameters for left, top, right, bottom
                    ev.setLayoutParams(params);
                }
            }
        });

        toText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    toText.clearFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(toText.getWindowToken(), 0);
                    return  true;
                }
                return false;
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        View v = getCurrentFocus();
        if (v != null && (v.equals(findViewById(R.id.fromSearch)) || v.equals(findViewById(R.id.toSearch)))) {
            v.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return false;
        } else {
            Intent myIntent = new Intent(getApplicationContext(), Entrance_Screen.class);
            startActivityForResult(myIntent, 0);
            return  true;
        }
    }

    protected  void setStartButton() {
        EditText fromText = (EditText)findViewById(R.id.fromSearch);
        String searchFrom = fromText.getText().toString();
        EditText toText = (EditText)findViewById(R.id.toSearch);
        String searchTo = toText.getText().toString();
        Button dirButt = (Button) findViewById(R.id.getDirections);
        if (!searchFrom.equals("") && !searchTo.equals("")) {
            dirButt.setEnabled(true);
        } else {
            dirButt.setEnabled(false);
        }
    }

    protected void startDirections(View v) {
        EditText fromText = (EditText)findViewById(R.id.fromSearch);
        String searchFrom = fromText.getText().toString().toLowerCase();
        EditText toText = (EditText)findViewById(R.id.toSearch);
        String searchTo = toText.getText().toString().toLowerCase();
        if (startKey == null) {
            startKey = getValidDirection(searchFrom);
            if (startKey == null) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Direction Error");
                alertDialog.setMessage("Location " + searchFrom + " cannot be found.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return;
            }
        }
        if (endKey == null) {
            endKey = getValidDirection(searchTo);
            if (endKey == null) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Direction Error");
                alertDialog.setMessage("Location " + searchTo + " cannot be found.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return;
            }
        }
        CheckBox outdoor = (CheckBox) findViewById(R.id.checkBoxOutdoors);
        Intent intent = new Intent(this, BCIT_Map.class);
        intent.putExtra("fromLocation", startKey);
        intent.putExtra("toLocation", endKey);
        intent.putExtra("outdoors", outdoor.isChecked());
        startActivity(intent);
    }

    protected Integer getValidDirection(String location){
        for (Node node : roomList) {
            if (node.toString().equalsIgnoreCase(location)) {
                return node.key;
            }
        }
        for (Node node : roomList) {
            if (node.toString().toLowerCase().contains(location.toLowerCase())) {
                return node.key;
            }
        }
        return null;
    }

    public void runLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (location == null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                } else {
                    //If everything went fine lets get latitude and longitude
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();

                    currentLocation = new LatLng(currentLatitude, currentLongitude);
                }
            } else {
                currentLocation = null;
            }
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    1
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        CheckBox locationcheck = (CheckBox) findViewById(R.id.checkboxlocation);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationcheck.setChecked(true);
                    if (!mGoogleApiClient.isConnected()) {
                        currentLocation = null;
                        return;
                    }

                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (location == null) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                    } else {
                        //If everything went fine lets get latitude and longitude
                        double currentLatitude = location.getLatitude();
                        double currentLongitude = location.getLongitude();

                        currentLocation = new LatLng(currentLatitude, currentLongitude);
                    }
                } else {
                    Toast.makeText(this, "Permission denied cant get your location!", Toast.LENGTH_SHORT).show();
                    Spinner areaSpinner = (Spinner) findViewById(R.id.detectspinner);
                    EditText fromText = (EditText) findViewById(R.id.fromSearch);
                    locationcheck.setChecked(false);
                    areaSpinner.setVisibility(View.GONE);
                    fromText.setEnabled(true);
                    fromText.setText("");
                    fromText.setHint("From");
                }
                return;
            }
        }
    }

    /**
     * If connected get lat and long
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } else {
                //If everything went fine lets get latitude and longitude
                double currentLatitude = location.getLatitude();
                double currentLongitude = location.getLongitude();

                currentLocation = new LatLng(currentLatitude, currentLongitude);
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        currentLocation = new LatLng(currentLatitude, currentLongitude);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }







}
