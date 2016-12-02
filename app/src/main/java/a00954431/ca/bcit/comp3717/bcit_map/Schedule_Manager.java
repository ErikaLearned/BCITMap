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
    private int maxTries = 5;
    private boolean createdFile;
    private String fileContents;
    private File file;
    private File directory;
    private Context context;
    private FileOutputStream outputStream;
    private FileInputStream inputStream;

    public Schedule_Manager() {
        int count = 0;
        createdFile = true; // default
        context = getApplicationContext();
        directory = context.getFilesDir();
        file = new File(directory, fileName);

        while (!createFile() && count < maxTries) {
            count ++;
            if (count == maxTries) {
                createdFile = false;
            }
        }
    }

    private boolean createFile() {
        boolean created = true; // file assumed to exist
        if (!file.exists()) {
            try {
                created = file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Failed to create " + fileName
                        + " in " + getClass().getEnclosingMethod().getName());
                created = false;
            }
        }
        return created;
    }

    public boolean fileExists() {
        return createdFile;
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
        int c;
        fileContents = "";

        try {
            inputStream = openFileInput(fileName);
        } catch (IOException e) {
            Log.e(TAG, "Failed to open " + fileName
                    + " in " + getClass().getEnclosingMethod().getName());
            return false;
        }

        String temp="";
        try {
            while ((c = inputStream.read()) != -1) {
                fileContents = fileContents + Character.toString((char) c);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read from file " + fileName
                    + " in " + getClass().getEnclosingMethod().getName());
            return false;
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to close file " + fileName + " after reading in "
                    + getClass().getEnclosingMethod().getName());
            return false;
        }

        return true;
    }
}
