package com.example.user.sunaitestingapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.activity.emp_listing;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyServiceListener;
import com.example.user.sunaitestingapp.common.Service_Call;
import com.example.user.sunaitestingapp.common._db_Super;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class login_activity extends AppCompatActivity {
    Button submit;
    Button new_customer;
    SQLiteDatabase mDB;
    EditText uname, password;
    ContentValues cv;
    String uname_str, password_str;
    private Model_TLI_API_Response model_TLI_api_response;
    String TAG = "login_activity";
    public ProgressDialog progressDialog = null;
    AlertDialog.Builder alertDialog, alertDialog2;
    TextInputLayout til;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        _db_Super dbhelper = _db_Super.getInstance(login_activity.this);
        if (mDB == null)
            mDB = dbhelper.getWritableDatabase();
        findById();
        alertDialog2 = new AlertDialog.Builder(login_activity.this);


      /*  new_customer.setClickable(true);
        //   new_customer.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='test' > New Customer Registration</a>";
        new_customer.setLinkTextColor(Color.rgb(135, 208, 235));
        new_customer.setText(Html.fromHtml(text));*/
        new_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    globalVariable.globalsubs=1;
                Intent intent = new Intent(getApplicationContext(), registration_activity.class);

                   startActivity(intent);
                   //finish();

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    if (validateForm()) {

                        new MyAsyncTask().execute();
                 /*   Intent intent = new Intent(getApplicationContext(), emp_listing.class);
                 startActivity(intent);*/
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void save_form() {
        //////////////////Call Api/////////////////////////////////
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        JSONObject json_object_root = new JSONObject();
        try {
          /*  json_object_root.put("userName", "test");
            json_object_root.put("password", "testf");*/
            json_object_root.put("userName", uname.getText().toString().trim());
            json_object_root.put("password", password.getText().toString().trim());

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
                            globalVariable.username += userObject.get("middleName").toString().trim();
                        }
                        if(userObject.get("lastName").toString().equals(null)|| userObject.get("lastName").toString().trim().equalsIgnoreCase("")|| userObject.get("lastName").toString().trim().equalsIgnoreCase("null")){}
                        else {
                            globalVariable.username += userObject.get("lastName").toString().trim();
                        }


                       /* Toast.makeText(getApplicationContext(),"User Detail"+userObject.toString(),Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),"User Role"+userRoleObject.toString(),Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),"User Address"+userAddrsObject.toString(),Toast.LENGTH_LONG).show();
                      */

                        globalVariable.userid = String.valueOf(userObject.getInt("id")).trim();
                        //   Toast.makeText(getApplicationContext(),"userid"+globalVariable.userid,Toast.LENGTH_LONG).show();
                        globalVariable.getSharedPreferences().edit()
                                .putString("userName",    uname.getText().toString().trim())
                                .putString("password", password.getText().toString().trim())
                                .putBoolean("is_loggedIn", true)
                                .apply();

                        Log.d("test--", TAG + "ROLE" + userRoleObject.getString("roleName"));
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
                                uname.requestFocus();
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
                            uname.requestFocus();
                        }
                    });
                    alertDialog2.show();
                }
            }
        });

    }

    private void findById() {
        progressDialog = new ProgressDialog(login_activity.this);
        til = (TextInputLayout) findViewById(R.id.child_nametxtinput);

        new_customer = (Button) findViewById(R.id.new_customer);
        uname = (EditText) findViewById(R.id.uname);
        password = (EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);
    }

    private boolean validateForm() {
        alertDialog = new AlertDialog.Builder(login_activity.this);
        if (uname.getText().toString().trim().equalsIgnoreCase("")) {

            alertDialog.setMessage("Please Enter Mobile No. or Email");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    uname.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        } else if (password.getText().toString().trim().equalsIgnoreCase("")) {

            alertDialog.setMessage("Please Enter Password");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    password.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        } else {
            return true;
        }


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
