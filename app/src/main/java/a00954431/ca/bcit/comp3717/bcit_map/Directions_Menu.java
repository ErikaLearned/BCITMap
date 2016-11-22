package a00954431.ca.bcit.comp3717.bcit_map;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Directions_Menu extends AppCompatActivity {

    ArrayList<String> roomList = new ArrayList<String>();
    ArrayList<String> roomList_FilteredTo = new ArrayList<String>();
    ArrayList<String> roomList_FilteredFrom = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Button dirButt = (Button) findViewById(R.id.getDirections);
        dirButt.setEnabled(false);
        final CheckBox outdoor = (CheckBox) findViewById(R.id.checkBoxOutdoors);
        final CheckBox locationcheck = (CheckBox) findViewById(R.id.checkboxlocation);
        final EditText fromText = (EditText)findViewById(R.id.fromSearch);
        final EditText toText = (EditText)findViewById(R.id.toSearch);
        final LinearLayout searchlayout = (LinearLayout)findViewById(R.id.searchlayout);
        ListView listViewSearch = (ListView)findViewById(R.id.listViewSearch);

        locationcheck.setVisibility(View.GONE);
        searchlayout.setVisibility(View.GONE);

        ArrayList<Node> dbList = NodeDir.mapDB.getAllNodes();

        for(Node node : dbList) {
            if (!node.roomNum.equals("")) {
                roomList.add(node.building + "-" + node.roomNum);
            } else if (!node.roomName.equals("")) {
                roomList.add(node.building + "-" + node.roomName);
            }
        }

        listViewSearch.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roomList));

        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View v = getCurrentFocus();
                if (v.equals(findViewById(R.id.fromSearch))) {
                    fromText.setText((String)parent.getItemAtPosition(position));
                    fromText.clearFocus();
                } else if (v.equals(findViewById(R.id.toSearch))) {
                    toText.setText((String)parent.getItemAtPosition(position));
                    toText.clearFocus();
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        locationcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                // TODO
                Toast.makeText(Directions_Menu.this, "BOX = " + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        fromText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ListView mListView1 = (ListView)findViewById(R.id.listViewSearch);
                EditText fromText = (EditText)findViewById(R.id.fromSearch);
                String search = fromText.getText().toString().toLowerCase();
                if(!search.equals("") ) {
                    roomList_FilteredFrom.clear();
                    for(String room : roomList) {
                        if(room.toLowerCase().contains(search)) {
                            roomList_FilteredFrom.add(room);
                        }
                    }
                    mListView1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, roomList_FilteredFrom));

                } else {
                    roomList_FilteredFrom.clear();
                    mListView1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, roomList));
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
                    searchlayout.setVisibility(View.VISIBLE);
                    locationcheck.setVisibility(View.VISIBLE);
                    ListView mListView1 = (ListView)findViewById(R.id.listViewSearch);
                    roomList_FilteredFrom.clear();
                    mListView1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, roomList));

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
                    searchlayout.setVisibility(View.GONE);
                    locationcheck.setVisibility(View.GONE);

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
                    roomList_FilteredTo.clear();
                    for(String room : roomList) {
                        if(room.toLowerCase().contains(search)) {
                            roomList_FilteredTo.add(room);
                        }
                    }
                    mListView1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, roomList_FilteredTo));
                } else {
                    roomList_FilteredTo.clear();
                    mListView1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, roomList));
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
                    searchlayout.setVisibility(View.VISIBLE);
                    ListView mListView1 = (ListView)findViewById(R.id.listViewSearch);
                    roomList_FilteredTo.clear();
                    mListView1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, roomList));

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
            finish();
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
        if (!checkIfValidDirection(searchFrom)) {
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
        } else if (!checkIfValidDirection(searchTo)) {
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
        } else {
            CheckBox outdoor = (CheckBox) findViewById(R.id.checkBoxOutdoors);
            Intent intent = new Intent(this, BCIT_Map.class);
            intent.putExtra("fromLocation", searchFrom);
            intent.putExtra("toLocation", searchTo);
            intent.putExtra("outdoors", outdoor.isChecked());
            startActivity(intent);
        }
    }

    protected boolean checkIfValidDirection(String location){
        for (String room : roomList) {
            if (location.equals(room.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}
