package com.example.user.sunaitestingapp.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

public class _db_Super extends SQLiteOpenHelper {
    public static final int DB_VERSION = 6;
    public static final String DB_NAME = "marvel_database.db";
    public static String TAG = "_db_Super";
    private static _db_Super db;
    public static _db_Super getInstance(Context context) {
        if (db == null)
            db = new _db_Super(context);


        db.CreateTableNotExist("device_detail");
        db.CreateTableNotExist("login_activity");
        db.CreateTableNotExist("delivery_confirmation");
        db.CreateTableNotExist("record_payment");

        return (db);
    }

    public _db_Super(Context context) {
       super(context, DB_NAME, null, DB_VERSION);
      //  super(context, Environment.getExternalStorageDirectory() + File.separator + DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
    public void CreateTableNotExist(String TN) {
        String SQL;
        if (GlobalClass.mDB == null) {
            GlobalClass.mDB = db.getWritableDatabase();
        }
        SQLiteDatabase mDB = GlobalClass.mDB;
        //mDB.execSQL("DROP TABLE IF EXISTS " + TN);
        //mDB.execSQL("update user_login set user_password='123'");

        if (TN.compareToIgnoreCase("device_detail") == 0) {
            //mDB.execSQL("DROP TABLE IF EXISTS " + TN);
            SQL = "create table if not exists device_detail (person_id  integer  PRIMARY KEY  AUTOINCREMENT NOT NULL , person_name String  NOT NULL,person_email  String   , " +
                    "person_mobile  String   NOT NULL ,device_id  INTEGER , brg_id INTEGER, logedin INTEGER, datareceived INTEGER,  mac_id1  INTEGER  NOT NULL,  mac_id2  INTEGER  NOT NULL,mac_id3  INTEGER  NOT NULL, mac_id4  INTEGER  NOT NULL,mac_id5  INTEGER  NOT NULL, mac_id6  INTEGER  NOT NULL, imei_no  INTEGER, app_version FLOAT)";
            mDB.execSQL(SQL);
        }
        if (TN.compareToIgnoreCase("login_activity") == 0) {
            //mDB.execSQL("DROP TABLE IF EXISTS " + TN);
            SQL = "create table if not exists login_activity (device_id  INTEGER, " +
                    "login_id INTEGER  PRIMARY KEY AUTOINCREMENT, uname TEXT ,password TEXT )";
            mDB.execSQL(SQL);
        }
        if (TN.compareToIgnoreCase("delivery_confirmation") == 0) {
            //mDB.execSQL("DROP TABLE IF EXISTS " + TN);
            SQL = "create table if not exists delivery_confirmation (device_id  INTEGER, " +
                    "delivery_login_id INTEGER , actual_delivery TEXT)";
            mDB.execSQL(SQL);
        }
        if (TN.compareToIgnoreCase("record_payment") == 0) {
            //mDB.execSQL("DROP TABLE IF EXISTS " + TN);
            SQL = "create table if not exists record_payment (device_id  INTEGER, " +
                    "payment_login_id INTEGER , payment_type INTEGER,payment_amt TEXT)";
            mDB.execSQL(SQL);
        }

    }
}
