package com.example.user.sunaitestingapp.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.user.sunaitestingapp.model.emp_listing_model;

import java.util.List;

public class GlobalClass extends Application {

    public static SQLiteDatabase mDB;
    public static String username="",customername="",customeraddrs="";
    public static String password="";
    public static String userid="";
    public static String customerid="";
    public static int productid=0;
    public static Double scheduledelivery=0.0;
    public static int createdBy=0;
    public static List<emp_listing_model> globalempList;
    public static   emp_listing_model globalemp_listing_model;
    public static SharedPreferences sharedPref_respond;
    public static String current_date="";
    public static String   scheduleChangeDate="";

    public static String registercity="";
    public static String registerlocality="";
    public static String registermobileno="";
    public static String  product_name="";
    public static String  pricePerUnit="";


    public static String globalstartdate="";
    public static String globalenddate="";
    public static String  vacationid="";
    public static int  globalsubs=0;
    public static int globalsubscriptflag=0;
    @Override
    public void onCreate() {
        super.onCreate();
        initSharedPreferences();
    }

    private void initSharedPreferences() {
        try {
            Log.d("test--","--enter in initSharedPreferences() method--");

            sharedPref_respond = getBaseContext().getSharedPreferences("fairdairy_shared", Context.MODE_PRIVATE);

        } catch (Exception e) {
            Log.d("test--","--initSharedPreferences()--"+e);
            e.printStackTrace();
        }

    }
    public static synchronized SharedPreferences getSharedPreferences() {
        return sharedPref_respond;
    }

}
