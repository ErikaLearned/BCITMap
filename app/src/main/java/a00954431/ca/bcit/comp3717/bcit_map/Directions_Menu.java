package a00954431.ca.bcit.comp3717.bcit_map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_directions_menu);

        Button dirButt = (Button) findViewById(R.id.getDirections);
        dirButt.setEnabled(false);

        ArrayList<Node> dbList = NodeDir.mapDB.getAllNodes();

        for(Node node : dbList) {
            if (!node.roomNum.equals("")) {
                roomList.add(node.building + "-" + node.roomNum);
            } else if (!node.roomName.equals("")) {
                roomList.add(node.building + "-" + node.roomName);
            }
        }

        ListView listViewFrom = (ListView)findViewById(R.id.listViewFrom);
        ListView listViewTo = (ListView)findViewById(R.id.listViewTo);

        listViewFrom.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roomList));
        listViewTo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roomList));


        listViewFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditText fromText = (EditText)findViewById(R.id.fromSearch);
                fromText.setText((String)parent.getItemAtPosition(position));
            }
        });

        listViewTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditText toText = (EditText)findViewById(R.id.toSearch);
                toText.setText((String)parent.getItemAtPosition(position));
            }
        });

        EditText fromText = (EditText)findViewById(R.id.fromSearch);
        fromText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ListView mListView1 = (ListView)findViewById(R.id.listViewFrom);
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
                // Do Nothing
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Do Nothing
            }
        });

        EditText toText = (EditText)findViewById(R.id.toSearch);
        toText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ListView mListView1 = (ListView)findViewById(R.id.listViewTo);
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
            Intent intent = new Intent(this, BCIT_Map.class);
            intent.putExtra("fromLocation", searchFrom);
            intent.putExtra("toLocation", searchTo);
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
