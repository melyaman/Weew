package preferences.android.eurecom.fr.weew3.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by aabdelli on 02/01/2017.
 */

public class SQLiteEventHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteEventHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_EVENT = "events";

    // Login Table Columns names
    //($email, $event_date, $event_type, $loc_lat, $loc_long, $picture, $time_begin, $time_end, $description)
    private static final String KEY_ID = "id";
    private static final String KEY_EVID = "evid";
    private static final String KEY_EVENT_DATE = "event_date";
    private static final String KEY_TIME_BEGIN = "time_begin";
    private static final String KEY_TIME_END = "time_end";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_EVENT_TYPE = "event_type";
    private static final String KEY_LOC_LONG = "loc_long";
    private static final String KEY_LOC_LAT = "loc_lat";
    private static final String KEY_PICTURE = "picture";



   // private static final String KEY_CREATED_AT = "created_at";

    public SQLiteEventHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EVID + " TEXT UNIQUE,"
                + KEY_EVENT_DATE + " DATE," + KEY_TIME_BEGIN + " TIME," + KEY_TIME_END +
                " TIME," + KEY_DESCRIPTION + " TEXT," + KEY_EMAIL + " TEXT," + KEY_EVENT_TYPE + " TEXT," +
                KEY_LOC_LONG + " FLOAT," + KEY_LOC_LAT + " FLOAT," + KEY_PICTURE + " TEXT" + ")";
        db.execSQL(CREATE_EVENT_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing event details in database
     * */
    public void addEvent(String evid ,String email, String event_date, String event_type, float loc_lat,
                         float loc_long, String picture, String time_begin, String time_end, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVID, evid);
        values.put(KEY_EMAIL, email);
        values.put(KEY_EVENT_DATE, event_date);
        values.put(KEY_TIME_BEGIN, time_begin);
        values.put(KEY_TIME_END, time_end );
        values.put(KEY_DESCRIPTION, description  );
        values.put(KEY_EVENT_TYPE, event_type);
        values.put(KEY_LOC_LONG, loc_long  );
        values.put(KEY_LOC_LAT, loc_lat  );
        values.put(KEY_PICTURE, picture );

        // Inserting Row
        long id = db.insert(TABLE_EVENT, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New event inserted into event table: " + id);
    }

    /**
     * Getting event data from database
     * */
    public HashMap<String, String> getEventDetails() {
        HashMap<String, String> event = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            event.put("evid", cursor.getString(1));
            event.put("email", cursor.getString(2));
            event.put("event_date", cursor.getString(3));
            event.put("event_type", cursor.getString(4));
            event.put("loc_lat", cursor.getString(5));
            event.put("loc_long", cursor.getString(6));
            event.put("picture", cursor.getString(7));
            event.put("event_type", cursor.getString(8));
            event.put("time_begin", cursor.getString(9));
            event.put("time_end", cursor.getString(10));
            event.put("description", cursor.getString(11));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching event from Sqlite: " + event.toString());

        return event;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteEvent() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_EVENT, null, null);
        db.close();

        Log.d(TAG, "Deleted all events info from sqlite");
    }
}
