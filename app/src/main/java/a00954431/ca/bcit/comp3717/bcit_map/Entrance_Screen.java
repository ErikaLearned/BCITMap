package a00954431.ca.bcit.comp3717.bcit_map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Entrance_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance__screen);
        NodeDir.initMapDB(this);
    }

    protected void startMap(View v) {
        Intent intent = new Intent(this, BCIT_Map.class);
        startActivity(intent);
    }

    protected void startDirections(View v) {
        Intent intent = new Intent(this, Directions_Menu.class);
        startActivity(intent);
    }

    protected void startSchedule(View v) {
        Intent intent = new Intent(this, Schedule.class);
        startActivity(intent);
    }
}
