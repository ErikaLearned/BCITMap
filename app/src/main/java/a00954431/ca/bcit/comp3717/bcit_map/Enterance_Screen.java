package a00954431.ca.bcit.comp3717.bcit_map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Enterance_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterance__screen);
    }

    protected void startMap(View v) {
        Intent intent = new Intent(this, BCIT_Map.class);
        startActivity(intent);
    }
}
