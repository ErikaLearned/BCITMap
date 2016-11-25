package a00954431.ca.bcit.comp3717.bcit_map;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by jacob on 16/11/16.
 */

public class Node {


    public LatLng loc;
    public boolean outside;
    public String building;
    public int floor;
    public String roomNum;
    public String roomName;
    public Integer key;
    ArrayList<Integer> neighbours;

    public boolean visited = false;
    public float distance = 0;

    public Node (LatLng l, String b, int f, String rn, String ro, boolean o, Integer k, ArrayList<Integer> nei) {
        loc = l;
        building = b;
        floor = f;
        roomNum = rn;
        roomName = ro;
        outside = o;
        key = k;
        neighbours = nei;
    }

    /*
    *   Gets distance between nodes based on LatLng positions
    */
    public float getDistanceTo(Node des) {
        float distance=0;
        Location crntLocation=new Location("crntlocation");
        crntLocation.setLatitude(this.loc.latitude);
        crntLocation.setLongitude(this.loc.longitude);

        Location newLocation=new Location("newlocation");
        newLocation.setLatitude(des.loc.latitude);
        newLocation.setLongitude(des.loc.longitude);

        distance = crntLocation.distanceTo(newLocation);  //in meters
        return distance;
    }

    @Override
    public String toString() {
        String name = "";
        if (!this.roomNum.equals("")) {
            name = this.roomNum;
        } else if (!this.roomName.equals("")) {
            name = this.roomName;
        } else {
            name = this.key.toString();
        }
        return this.building + "-" + name;
    }

}

