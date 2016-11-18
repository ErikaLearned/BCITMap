package a00954431.ca.bcit.comp3717.bcit_map;

import java.util.Comparator;

/**
 * Created by jacob on 17/11/16.
 */

public class NodeLengthComparator implements Comparator<Node>
{
    @Override
    public int compare(Node x, Node y)
    {


        if (x.distance < y.distance)
        {
            return -1;
        }
        if (x.distance > y.distance)
        {
            return 1;
        }
        return 0;
    }
}
