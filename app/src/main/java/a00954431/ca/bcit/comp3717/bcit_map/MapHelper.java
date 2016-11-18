package a00954431.ca.bcit.comp3717.bcit_map;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.DatabaseUtils;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;
        import android.widget.Toast;

        import com.google.android.gms.maps.model.LatLng;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;

public final class MapHelper
        extends SQLiteOpenHelper
{
    private static final String TAG = MapHelper.class.getName();
    private static final int SCHEMA_VERSION = 1;
    private static final String DB_NAME = "nodes.db";
    private static final String NODE_TABLE_NAME = "MAP_NODES";
    private static final String ID_COLUMN_NAME = "_id";
    private static final String LAT_COLUMN_NAME = "_lat";
    private static final String LNG_COLUMN_NAME = "_lng";
    private static final String OUTSIDE_COLUMN_NAME = "_outside";
    private static final String BUILDING_COLUMN_NAME = "_building";
    private static final String FLOOR_COLUMN_NAME = "_floor";
    private static final String ROOM_NUM_COLUMN_NAME = "_room_num";
    private static final String ROOM_NAME_COLUMN_NAME = "_room_name";
    private static final String KEY_COLUMN_NAME = "_key";
    private static final String NEI_COLUMN_NAME = "_nei";
    private static MapHelper instance;

    public MapHelper(final Context ctx)
    {
        super(ctx, DB_NAME, null, SCHEMA_VERSION);
    }

    public void killDatabase(final Context ctx) {
        ctx.deleteDatabase(DB_NAME);
    }

    public synchronized static MapHelper getInstance(final Context context)
    {
        if(instance == null)
        {
            instance = new MapHelper(context.getApplicationContext());
        }

        return instance;
    }

    @Override
    public void onConfigure(final SQLiteDatabase db)
    {
        super.onConfigure(db);

        setWriteAheadLoggingEnabled(true);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(final SQLiteDatabase db)
    {
        final String CREATE_NAME_TABLE;

        CREATE_NAME_TABLE = "CREATE TABLE IF NOT EXISTS "  + NODE_TABLE_NAME + " ( " +
                ID_COLUMN_NAME   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LAT_COLUMN_NAME + " REAL NOT NULL, " +
                LNG_COLUMN_NAME + " REAL NOT NULL, " +
                OUTSIDE_COLUMN_NAME + " INTEGER NOT NULL, " +
                BUILDING_COLUMN_NAME + " TEXT NOT NULL, " +
                FLOOR_COLUMN_NAME + " INTEGER NOT NULL, " +
                ROOM_NUM_COLUMN_NAME + " TEXT NOT NULL, " +
                ROOM_NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                KEY_COLUMN_NAME + " INTEGER NOT NULL, " +
                NEI_COLUMN_NAME + " TEXT NOT NULL " + ")";
        db.execSQL(CREATE_NAME_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db,
                          final int oldVersion,
                          final int newVersion)
    {
    }

    public long getNumberOfNodes()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        final long numEntries;

        numEntries = DatabaseUtils.queryNumEntries(db, NODE_TABLE_NAME);

        return (numEntries);
    }

    public void insertNode(Node node)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues contentValues;
        String neiList;
        try {
            JSONObject json = new JSONObject();
            json.put("nei", new JSONArray(node.neighbours));
            neiList = json.toString();
        } catch (JSONException e) {
            neiList = "";

        }

        contentValues = new ContentValues();
        contentValues.put(LAT_COLUMN_NAME, node.loc.latitude);
        contentValues.put(LNG_COLUMN_NAME, node.loc.longitude);
        contentValues.put(OUTSIDE_COLUMN_NAME, node.outside);
        contentValues.put(BUILDING_COLUMN_NAME, node.building);
        contentValues.put(FLOOR_COLUMN_NAME, node.floor);
        contentValues.put(ROOM_NUM_COLUMN_NAME, node.roomNum);
        contentValues.put(ROOM_NAME_COLUMN_NAME, node.roomName);
        contentValues.put(KEY_COLUMN_NAME, node.key);
        contentValues.put(NEI_COLUMN_NAME, neiList);
        db.insert(NODE_TABLE_NAME, null, contentValues);
    }

    public ArrayList<Node> getAllNodes()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor;
        ArrayList<Node> nodes = new ArrayList<Node>();

        cursor = db.query(NODE_TABLE_NAME,
                null,
                null,     // selection, null = *
                null,     // selection args (String[])
                null,     // group by
                null,     // having
                null,     // order by
                null);    // limit

        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {

            ArrayList<Integer> neiDB = new ArrayList<Integer>();

            try {
                JSONObject jsonObj = new JSONObject(cursor.getString(cursor.getColumnIndex(NEI_COLUMN_NAME)));
                JSONArray jArray = jsonObj.getJSONArray("nei");
                if (jArray != null) {
                    for (int i=0;i<jArray.length();i++){
                        neiDB.add(Integer.parseInt(jArray.get(i).toString()));
                    }
                }

            } catch (JSONException e) {
                neiDB.clear();
            }

           // Log.d("X",  cursor.getString(cursor.getColumnIndex(KEY_COLUMN_NAME)));

            nodes.add(new Node(new LatLng(cursor.getDouble(cursor.getColumnIndex(LAT_COLUMN_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(LNG_COLUMN_NAME))),
                    cursor.getString(cursor.getColumnIndex(BUILDING_COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndex(FLOOR_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NUM_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NAME_COLUMN_NAME)),
                    (cursor.getInt(cursor.getColumnIndex(OUTSIDE_COLUMN_NAME)) == 1),
                    cursor.getInt(cursor.getColumnIndex(KEY_COLUMN_NAME)),
                    neiDB
                    ));
            cursor.moveToNext();
        }
        cursor.close();
        return nodes;
    }

    public Node getNodeById(final int _id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor;
        ArrayList<Node> nodes = new ArrayList<Node>();

        cursor = db.query(NODE_TABLE_NAME,
                null,
                ID_COLUMN_NAME + " = ?",
                new String[]
                        {
                                String.valueOf(_id),
                        },
                null,     // group by
                null,     // having
                null,     // order by
                String.valueOf(1));

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            ArrayList<Integer> neiDB = new ArrayList<Integer>();

            try {
                JSONObject json = new JSONObject(cursor.getString(cursor.getColumnIndex(NEI_COLUMN_NAME)));
                JSONArray jArray = json.optJSONArray("nei");
                if (jArray != null) {
                    for (int i=0;i<jArray.length();i++){
                        neiDB.add(Integer.parseInt(jArray.get(i).toString()));
                    }
                }

            } catch (JSONException e) {
                Log.d("X", "JSON ERROR");
                neiDB.clear();
            }

            nodes.add(new Node(new LatLng(cursor.getDouble(cursor.getColumnIndex(LAT_COLUMN_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(LNG_COLUMN_NAME))),
                    cursor.getString(cursor.getColumnIndex(BUILDING_COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndex(FLOOR_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NUM_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NAME_COLUMN_NAME)),
                    (cursor.getInt(cursor.getColumnIndex(OUTSIDE_COLUMN_NAME)) == 1),
                    cursor.getInt(cursor.getColumnIndex(KEY_COLUMN_NAME)),
                    neiDB
            ));
            cursor.moveToNext();
        }
        cursor.close();
        if (nodes.isEmpty()) {
            return null;
        } else {
            return nodes.get(0);
        }
    }

    public Node getNodeByKey(final int key)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor;
        ArrayList<Node> nodes = new ArrayList<Node>();

        cursor = db.query(NODE_TABLE_NAME,
                null,
                KEY_COLUMN_NAME + " = ?",
                new String[]
                        {
                                String.valueOf(key),
                        },
                null,     // group by
                null,     // having
                null,     // order by
                String.valueOf(1));

        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {

            ArrayList<Integer> neiDB = new ArrayList<Integer>();

            try {
                JSONObject jsonObj = new JSONObject(cursor.getString(cursor.getColumnIndex(NEI_COLUMN_NAME)));
                JSONArray jArray = jsonObj.getJSONArray("nei");
                if (jArray != null) {
                    for (int i=0;i<jArray.length();i++){
                        neiDB.add(Integer.parseInt(jArray.get(i).toString()));
                    }
                }

            } catch (JSONException e) {
                neiDB.clear();
            }

           // Log.d("X",  cursor.getString(cursor.getColumnIndex(KEY_COLUMN_NAME)));

            nodes.add(new Node(new LatLng(cursor.getDouble(cursor.getColumnIndex(LAT_COLUMN_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(LNG_COLUMN_NAME))),
                    cursor.getString(cursor.getColumnIndex(BUILDING_COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndex(FLOOR_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NUM_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NAME_COLUMN_NAME)),
                    (cursor.getInt(cursor.getColumnIndex(OUTSIDE_COLUMN_NAME)) == 1),
                    cursor.getInt(cursor.getColumnIndex(KEY_COLUMN_NAME)),
                    neiDB
            ));
            cursor.moveToNext();
        }
        cursor.close();
        if (nodes.isEmpty()) {
            return null;
        } else {
            return nodes.get(0);
        }
    }

    public Node getNodeByRoom(String building, String room)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor;
        ArrayList<Node> nodes = new ArrayList<Node>();

        cursor = db.query(NODE_TABLE_NAME,
                null,
                BUILDING_COLUMN_NAME + " = ? AND " + ROOM_NUM_COLUMN_NAME + " = ? ",
                new String[]
                        {
                                building.toUpperCase(),
                                room
                        },
                null,     // group by
                null,     // having
                null,     // order by
                String.valueOf(1));

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            ArrayList<Integer> neiDB = new ArrayList<Integer>();

            try {
                JSONObject jsonObj = new JSONObject(cursor.getString(cursor.getColumnIndex(NEI_COLUMN_NAME)));
                JSONArray jArray = jsonObj.getJSONArray("nei");
                if (jArray != null) {
                    for (int i=0;i<jArray.length();i++){
                        neiDB.add(Integer.parseInt(jArray.get(i).toString()));
                    }
                }

            } catch (JSONException e) {
                neiDB.clear();
            }

            //Log.d("X",  cursor.getString(cursor.getColumnIndex(KEY_COLUMN_NAME)));

            nodes.add(new Node(new LatLng(cursor.getDouble(cursor.getColumnIndex(LAT_COLUMN_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(LNG_COLUMN_NAME))),
                    cursor.getString(cursor.getColumnIndex(BUILDING_COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndex(FLOOR_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NUM_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NAME_COLUMN_NAME)),
                    (cursor.getInt(cursor.getColumnIndex(OUTSIDE_COLUMN_NAME)) == 1),
                    cursor.getInt(cursor.getColumnIndex(KEY_COLUMN_NAME)),
                    neiDB
            ));
            cursor.moveToNext();
        }
        cursor.close();
        if (nodes.isEmpty()) {
            return null;
        } else {
            return nodes.get(0);
        }
    }

    public Node getNodeByName(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor;
        ArrayList<Node> nodes = new ArrayList<Node>();

        cursor = db.query(NODE_TABLE_NAME,
                null,
                ROOM_NAME_COLUMN_NAME + " = ? ",
                new String[]
                        {
                                name.toUpperCase()
                        },
                null,     // group by
                null,     // having
                null,     // order by
                String.valueOf(1));

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            ArrayList<Integer> neiDB = new ArrayList<Integer>();

            try {
                JSONObject jsonObj = new JSONObject(cursor.getString(cursor.getColumnIndex(NEI_COLUMN_NAME)));
                JSONArray jArray = jsonObj.getJSONArray("nei");
                if (jArray != null) {
                    for (int i=0;i<jArray.length();i++){
                        neiDB.add(Integer.parseInt(jArray.get(i).toString()));
                    }
                }

            } catch (JSONException e) {
                neiDB.clear();
            }

            //Log.d("X",  cursor.getString(cursor.getColumnIndex(KEY_COLUMN_NAME)));

            nodes.add(new Node(new LatLng(cursor.getDouble(cursor.getColumnIndex(LAT_COLUMN_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(LNG_COLUMN_NAME))),
                    cursor.getString(cursor.getColumnIndex(BUILDING_COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndex(FLOOR_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NUM_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ROOM_NAME_COLUMN_NAME)),
                    (cursor.getInt(cursor.getColumnIndex(OUTSIDE_COLUMN_NAME)) == 1),
                    cursor.getInt(cursor.getColumnIndex(KEY_COLUMN_NAME)),
                    neiDB
            ));
            cursor.moveToNext();
        }
        cursor.close();
        if (nodes.isEmpty()) {
            return null;
        } else {
            return nodes.get(0);
        }
    }

}

