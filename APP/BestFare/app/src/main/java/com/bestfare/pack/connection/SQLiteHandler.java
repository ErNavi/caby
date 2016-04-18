package com.bestfare.pack.connection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "BestFare";

    // Login table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_ADDRESS = "Address";
    private static final String TABLE_SESSION_STORE = "Session_store";
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_PLOT = "plot";
    private static final String KEY_AREA = "area";
    private static final String KEY_PINCODE = "pin";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_CITY = "city";
    private static final String KEY_USER_ID="id";
    private static final String KEY_SESSION_ID="session_id";
    SQLiteHandler   db;
    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ADDRESS_TABLE = "CREATE TABLE " + TABLE_ADDRESS + "("
                + KEY_USER_ID + " TEXT," + KEY_PLOT + " TEXT,"
                + KEY_AREA + " TEXT ," + KEY_PINCODE + " TEXT,"+KEY_CONTACT + " TEXT,"+KEY_COUNTRY + " TEXT,"+KEY_CITY + " TEXT"
                 + ")";
        db.execSQL(CREATE_ADDRESS_TABLE);

        Log.d(TAG, "Database tables created Address");

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " VARCHAR(10) PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_PASSWORD + " TEXT,"
                + KEY_CREATED_AT + null + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
String CREATE_SESSION_TABLE="create table "+TABLE_SESSION_STORE+"( "+KEY_SESSION_ID+" varchar(150))";
        db.execSQL(CREATE_SESSION_TABLE);
        Log.d(TAG, "Session table created");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
 db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);;
        // Create tables again
        onCreate(db);
    }
 public void updateAddress(String country,String city){
     String UPDATE_ADDRESS="UPDATE "+TABLE_ADDRESS+"set "+KEY_COUNTRY+"="+country+", "+KEY_CITY+"="+city;

     ContentValues values = new ContentValues();
     values.put(KEY_COUNTRY, country);
     values.put(KEY_CITY, city);
     HashMap<String, String> add = db.getUserAddress();

     // Inserting Row

     db.close(); // Closing database connection

     Log.d(TAG, "New user inserted into sqlite: ");

 }
    /**
     * Storing user details in database
     * */
    public void storeSession(String session_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, session_id);
        long id = db.insert(TABLE_SESSION_STORE, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New Session is stored. " + id);

    }
    public void addUser(String user_id,String name, String email,String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user_id);
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PASSWORD, password);
        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }
    public void addAddress(String plot, String area,String pincode,String contact,String country,String city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLOT, plot); // Name
        values.put(KEY_AREA, area); // Email
        values.put(KEY_PINCODE, pincode);
        values.put(KEY_CONTACT, contact);
        values.put(KEY_COUNTRY, country);
        values.put(KEY_CITY, city);
        // Inserting Row
        long id = db.insert(TABLE_ADDRESS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }
    public void addUserLoginDetails(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {

            user.put("user_id",cursor.getString(0));
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("password", cursor.getString(3));
            //user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }
    public HashMap<String, String> getUserAddress() {
        HashMap<String, String> add = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_ADDRESS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            add.put("plot", cursor.getString(1));
            add.put("area", cursor.getString(2));
            add.put("pin", cursor.getString(3));
            add.put("contact", cursor.getString(4));
            add.put("country", cursor.getString(5));
            add.put("city", cursor.getString(6));

            //user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + add.toString());

        return add;
    }
 public HashMap<String,String> getUserName(){

     HashMap<String, String> user = new HashMap<String, String>();
     String selectQuery = "SELECT  name FROM " + TABLE_USER;

     SQLiteDatabase db = this.getReadableDatabase();
     Cursor cursor = db.rawQuery(selectQuery, null);
     cursor.moveToFirst();
     if (cursor.getCount() > 0) {
         user.put("name", cursor.getString(1));
            }
     cursor.close();
     db.close();
     // return user
     Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
return user;
 }
    /**
     * Re crate database Delete all tables and create them again
     * */
    public boolean deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows

        db.execSQL("delete from " + TABLE_USER);
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
return  true;
    }
    public HashMap<String, String> getSessionDetails() {
        HashMap<String, String> add = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_SESSION_STORE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            add.put("session_id", cursor.getString(0));}
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + add.toString());

        return add;
    }
    public boolean deleteSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows

        db.execSQL("delete from " + TABLE_SESSION_STORE);
        db.delete(TABLE_SESSION_STORE, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
        return  true;
    }


}