package a00954431.ca.bcit.comp3717.bcit_map;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Schedule extends AppCompatActivity {
    private ListView listView;
    private String[] display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Get ListView object and populate it ((DUMMY DATA ATM))
        listView = (ListView) findViewById(R.id.schedule_list);

        display = new String[] { "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.schedule_textview, display);

        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);
                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
            }
        });

    }
}
