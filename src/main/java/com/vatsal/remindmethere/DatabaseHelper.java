package com.vatsal.remindmethere;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geofences";
    private static final String TABLE_GEOFENCE = "geofences";
    private static final String KEY_ID = "id";
  //  private static final String KEY_LAT = "Lat";
 //   private static final String KEY_LNG = "Lng";
    private static final String KEY_LOCATION = "Location";
    private static final String KEY_RADIUS = "radius";
    private static final String KEY_TOGGLE = "Toggle";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GEOFENCE_TABLE = "CREATE TABLE " + TABLE_GEOFENCE + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_LOCATION + " TEXT,"
                + KEY_RADIUS + " INTEGER,"
                + KEY_TOGGLE + " INTEGER" + ")";
        db.execSQL(CREATE_GEOFENCE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEOFENCE);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    void addGeofence(String id,String location, int radius, int toggle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_LOCATION, location);
        values.put(KEY_RADIUS, radius);
        values.put(KEY_TOGGLE, toggle);



        // Inserting Row
        db.insert(TABLE_GEOFENCE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
//    geofences getGeofence(String id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_GEOFENCE, new String[] { KEY_ID,
//                        KEY_LOCATION, KEY_RADIUS, KEY_TOGGLE }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//        geofences g = null;
//        geofences geofence;
//        geofence = new geofences(
//                cursor.getString(cursor.getColumnIndex(g.id)),
//                cursor.getString(cursor.getColumnIndex(g.location)),
//                cursor.getFloat(cursor.getColumnIndex(String.valueOf(g.radius))),
//        cursor.getString(cursor.getColumnIndex(String.valueOf(g.toggle))));
//        // return contact
//        return geofence;
//    }

    // code to get all contacts in a list view
    public List<geofences> getAllGeofences() {
        List<geofences > geofenceList = new ArrayList<>();
        String location;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GEOFENCE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                geofences geofence = new geofences();
                geofence.setID(cursor.getString(0));
                geofence.setLocation(cursor.getString(1));
               // location=cursor.getString(1);
                geofence.setRadius(Integer.parseInt(cursor.getString(2)));
                geofence.setToggle(Integer.parseInt((cursor.getString(3))));
               // geofence.setToggle(true);
                // Adding contact to list
                
                geofenceList.add(geofence);
            } while (cursor.moveToNext());
        }

        // return contact list
        return geofenceList;
    }

    // code to update the single contact
    public int updateGeofenceToggle(String id, int toggle ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TOGGLE, toggle);


        // updating row
        return db.update(TABLE_GEOFENCE, values, KEY_ID + " = ?",
                new String[] { id });

    }

    public int updateGeofenceRadius(String id, int radius ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RADIUS, radius);


        // updating row
        return db.update(TABLE_GEOFENCE, values, KEY_ID + " = ?",
                new String[] { id });
    }

    // Deleting single contact
     void deleteGeofence(String id) {
         Log.d(TAG, "database");
        SQLiteDatabase db = this.getWritableDatabase();
      db.delete(TABLE_GEOFENCE, KEY_ID + " = ?",
               new String[] { id });
      //  db.execSQL("DELETE FROM " + TABLE_GEOFENCE+ " WHERE "+KEY_ID+"='"+id+"'");
        db.close();
    }


    // Getting contacts Count
    public int getGeofenceCount() {
        String countQuery = "SELECT  * FROM " + TABLE_GEOFENCE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
