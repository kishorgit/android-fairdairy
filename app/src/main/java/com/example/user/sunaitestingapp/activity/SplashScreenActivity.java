package com.example.user.sunaitestingapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyServiceListener;
import com.example.user.sunaitestingapp.common.Service_Call;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class SplashScreenActivity extends AppCompatActivity {
    Handler mHandler = null;
    String TAG = "SplashScreenActivity";
    private Model_TLI_API_Response model_TLI_api_response;
    public ProgressDialog progressDialog = null;
    AlertDialog.Builder  alertDialog2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        progressDialog = new ProgressDialog(SplashScreenActivity.this);
        alertDialog2 = new AlertDialog.Builder(SplashScreenActivity.this);
       /* TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        String moblileno = telephonyManager.getLine1Number();
        Log.d("test--", TAG + "--moblileno--" +moblileno);*/
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("test--", TAG + "--shared_is_loggedIn --" + globalVariable.getSharedPreferences().getBoolean("is_loggedIn", false));
                if(globalVariable.getSharedPreferences().getBoolean("is_loggedIn", false))
                {

                    new MyAsyncTask().execute();
                }
                else
                {
                    Log.d("test--",TAG+"--if--");
                    Intent i = new Intent(SplashScreenActivity.this, login_activity.class);
                    startActivity(i);
                    finish();
                }
                // close this activity



            }
        },1000);

    }
    private void save_form() {
        //////////////////Call Api/////////////////////////////////
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Log.d("test--", TAG + "--shared_userName --" + globalVariable.getSharedPreferences().getString("userName", ""));
        Log.d("test--", TAG + "--shared_password --" + globalVariable.getSharedPreferences().getString("password", ""));
        JSONObject json_object_root = new JSONObject();
        try {
          /*  json_object_root.put("userName", "test");
            json_object_root.put("password", "testf");*/
            json_object_root.put("userName",globalVariable.getSharedPreferences().getString("userName", ""));
            json_object_root.put("password",globalVariable.getSharedPreferences().getString("password", ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Service_Call(getApplicationContext(), "/login", "POST", json_object_root, true, new MyServiceListener() {
            @Override
            public void getServiceData(Model_TLI_API_Response api_response) {
                String result = api_response.data;
                model_TLI_api_response = api_response;

                Log.d("test--", TAG + "--result data1 --" + result);
                Log.d("test--", TAG + "--responsecode1" + model_TLI_api_response.response_code);
                Log.d("test--", TAG + "--responsecode1HttpsURLConnection.HTTP_OK" + HttpsURLConnection.HTTP_OK);
                try {
                    if (model_TLI_api_response.response_code == HttpsURLConnection.HTTP_OK) {


                        JSONObject userRoleObject = null;
                        JSONObject userAddrsObject = null;
                        JSONObject userObject = new JSONObject(result);
                        //  Log.d("test--",TAG+"--address--"+userObject.getJSONObject("address"));
                        if (userObject.isNull("role") == false) {
                            Log.d("test--", TAG + "--role--" + userObject.getJSONObject("role"));
                            userRoleObject = userObject.getJSONObject("role");
                        }
                        if (userObject.isNull("address") == false) {
                            Log.d("test--", TAG + "--addressif--" + userObject.getJSONObject("address"));
                            userAddrsObject = userObject.getJSONObject("address");
                        }

                        if(userObject.get("firstName").toString().equals(null)|| userObject.get("firstName").toString().trim().equalsIgnoreCase("")|| userObject.get("firstName").toString().trim().equalsIgnoreCase("null")){}
                        else {
                            globalVariable.username = userObject.get("firstName").toString().trim();
                        }

                        if(userObject.get("middleName").toString().equals(null)|| userObject.get("middleName").toString().trim().equalsIgnoreCase("")|| userObject.get("middleName").toString().trim().equalsIgnoreCase("null")){}
                        else {
                            globalVariable.username += " "+userObject.get("middleName").toString().trim();
                        }
                        if(userObject.get("lastName").toString().equals(null)|| userObject.get("lastName").toString().trim().equalsIgnoreCase("")|| userObject.get("lastName").toString().trim().equalsIgnoreCase("null")){}
                        else {
                            globalVariable.username += " "+userObject.get("lastName").toString().trim();
                        }


                       /* Toast.makeText(getApplicationContext(),"User Detail"+userObject.toString(),Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),"User Role"+userRoleObject.toString(),Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),"User Address"+userAddrsObject.toString(),Toast.LENGTH_LONG).show();
                      */

                        globalVariable.userid = String.valueOf(userObject.getInt("id")).trim();
                        //   Toast.makeText(getApplicationContext(),"userid"+globalVariable.userid,Toast.LENGTH_LONG).show();
                       /* globalVariable.getSharedPreferences().edit()
                                .putString("userName",    uname.getText().toString().trim())
                                .putString("password", password.getText().toString().trim())
                                .putBoolean("is_loggedIn", false)
                                .apply();
*/

                        if (userRoleObject.getString("roleName").equalsIgnoreCase("admin")) {
                            globalVariable.globalsubs=0;
                            Intent intent = new Intent(getApplicationContext(), admin_listing.class);

                            startActivity(intent);
                            finish();
                        }
                        else if (userRoleObject.getString("roleName").equalsIgnoreCase("CUSTR")) {
                            globalVariable.globalsubs=0;
                            globalVariable.customerid =  String.valueOf(userObject.getInt("id"));
                            Log.d("test--", TAG + "logincustomerid" +   globalVariable.customerid);
                            Intent intent = new Intent(getApplicationContext(), calendar_activity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            globalVariable.globalsubs=0;
                            Intent intent = new Intent(getApplicationContext(), emp_listing.class);
                            startActivity(intent);
                            finish();
                        }


                        try {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception ex) {
                            Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                        }


                        //  }
                    } else {
                        try {
                            if (progressDialog == null || progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception ex) {
                            Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                        }
                        // Toast.makeText(getApplicationContext(),"Username or Password is Wrong.Please Try Again...",Toast.LENGTH_LONG).show();
                    /*    uname.setError("User Not Found");
                        password.setError("User Not Found");*/
                        alertDialog2.setMessage("User Not Found");
                        alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog2.show();
                    }


                } catch (Exception ex) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception e) {
                        Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + e);
                    }
                    //  Toast.makeText(getApplicationContext(), "Something goes wrong please try again!", Toast.LENGTH_LONG).show();
                    Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
                    alertDialog2.setMessage("Something goes wrong please try again!");
                    alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog2.show();
                }
            }
        });

    }

    private class MyAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            save_form();
            return true;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait.......");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {


            super.onPostExecute(o);
        }
    }

}
