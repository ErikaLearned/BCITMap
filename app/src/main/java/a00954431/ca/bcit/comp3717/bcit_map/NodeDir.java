package a00954431.ca.bcit.comp3717.bcit_map;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class NodeDir {
    public static MapHelper mapDB;

    public static void initMapDB(Context ctx) {
        mapDB = new MapHelper(ctx);
        mapDB.killDatabase(ctx); // TEMP REMOVE LATER
        Log.d("X", "COUNT: " + Long.toString(mapDB.getNumberOfNodes()));
        if (mapDB.getNumberOfNodes() <= 0) {
            Log.d("X", "INIT DB");
            NodeDir.initNodeBase(ctx);
        }
    }

    public static Scanner initFile(String fileName, Context ctx) throws IOException {
        DataInputStream textFileStream = new DataInputStream(ctx.getAssets().open(String.format(fileName)));
        return new Scanner(textFileStream);
    }

    public static void initNodeBase(Context ctx) {
        try {
            Scanner file = initFile("nodelist", ctx);

            while(file.hasNext()) {
                String line = file.nextLine();
                if (line.contains("//")) {
                    continue;
                }
                String nodeData[] = line.split(",");
                ArrayList<Integer> nei = new ArrayList<Integer>();
                for (int i = 8; i < nodeData.length; ++i) {
                    nei.add(Integer.parseInt(nodeData[i]));
                }
                Node node = new Node(new LatLng(Double.valueOf(nodeData[0]), Double.valueOf(nodeData[1])), nodeData[2], Integer.parseInt(nodeData[3]), nodeData[4], nodeData[5], Boolean.getBoolean(nodeData[6]), Integer.parseInt(nodeData[7]), nei);

                mapDB.insertNode(node);
            }
            file.close();

        } catch (IOException e) {
            Log.d("X", "FILE FAILED");
        }
    }


}
