package com.example.mapbox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "your_database_name.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "your_table_name";
    private static final String COLUMN_ID = "id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TEXT = "text";

    public static final String TABLE_PROFILE = "user_profile";
    public static final String COLUMN_PROFILE_ID = "profile_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_LEVEL = "level";

    // Singleton instance
    private static AppDatabase instance;

    // Private constructor to prevent multiple instances
    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Public method to provide a single instance of AppDatabase
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabase(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LATITUDE + " REAL," +
                COLUMN_LONGITUDE + " REAL," +
                COLUMN_TEXT + " TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);

        String CREATE_TABLE_PROFILE = "CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_POINTS + " INTEGER," +
                COLUMN_LEVEL + " TEXT" +
                ")";
        db.execSQL(CREATE_TABLE_PROFILE);

        Log.d("Database", "Tables created: " + CREATE_TABLE + " and " + CREATE_TABLE_PROFILE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }

    public void insertData(double latitude, double longitude, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_TEXT, text);
        long newRowId = db.insert(TABLE_NAME, null, values);
        if (newRowId == -1) {
            Log.e("Database Error", "Failed to insert row");
        } else {
            Log.d("Database", "Row inserted with ID: " + newRowId);
        }
        db.close();
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_TEXT}, null, null, null, null, null);
    }

    public void saveUserProfile(String name, int points, String level) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_POINTS, points);
        values.put(COLUMN_LEVEL, level);

        int rowsUpdated = db.update(TABLE_PROFILE, values, COLUMN_PROFILE_ID + "= 1", null);

        if (rowsUpdated == 0) {
            values.put(COLUMN_PROFILE_ID, 1); // ID для першого запису профілю
            db.insert(TABLE_PROFILE, null, values);
            Log.d("Database", "User profile inserted.");
        } else {
            Log.d("Database", "User profile updated.");
        }
        db.close();
    }

    public Cursor getUserProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PROFILE, new String[]{COLUMN_NAME, COLUMN_POINTS, COLUMN_LEVEL}, null, null, null, null, null);
    }
}


