package a00954431.ca.bcit.comp3717.bcit_map;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by erika on 02/12/16.
 */

public class Schedule_Manager extends Activity {
    private String TAG = this.getClass().getName();
    private String fileName = "schedule.txt";
    private File file;
    private File directory;
    private Context context;
    private FileOutputStream outputStream;
    private FileInputStream inputStream;

    public Schedule_Manager() {
        context = getApplicationContext();
        directory = context.getFilesDir();
        file = new File(directory, fileName);
    }

    public boolean writeSchedule(String message) {
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(message.getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to write to file " + fileName);
            return false;
        }
        return true;
    }

    public boolean readSchedule() {
        return true;
    }
}
