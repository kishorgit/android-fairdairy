package com.example.user.sunaitestingapp.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyServiceListener;
import com.example.user.sunaitestingapp.common.Service_Call;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import android.support.v7.widget.Toolbar;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class delivery_confirmation extends AppCompatActivity {
    Button submit;
    TextView tv_custname,tv_custaddrs,schedule_delivery;
    EditText actual_delivery;
    ContentValues cv;
    String actual_delivery_str;
    SQLiteDatabase mDB;
    private Model_TLI_API_Response model_TLI_api_response;
    String TAG = "delivery_confirmation";
    public ProgressDialog progressDialog =null;
    AlertDialog.Builder alertDialog,alertDialog2;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_confirmation);
         GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        alertDialog2 = new AlertDialog.Builder(delivery_confirmation.this);
        findById();
         setFormValue();
      //  setActionBar();
        toolbar.setTitle(Html.fromHtml("<font  >Delivery Confirmation</font>"+"<font color='#FFFFFF'> </font>"));
        toolbar.setTitleTextAppearance(getApplicationContext(),R.style.titletextStyle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
         mDB = GlobalClass.mDB;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    if (validateForm()) {
                        new MyAsyncTask().execute();
                        // finish();
                    }
                }
         else {
             Toast.makeText(getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
         }
            }
        });


    }



    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(2.0f);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#87CEEB")));
            actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Delivery Confirmation</font>"+"<font color='#FFFFFF'> </font>"));

        }
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;


        }

        return super.onOptionsItemSelected(item);
    }
    private void save_form() {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Log.d("save_form--", TAG+"--productid--" + globalVariable.productid);
        Log.d("save_form--", TAG+"--actual_delivery--" +actual_delivery.getText().toString().trim());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        //////////////////Call Api/////////////////////////////////actualDelivery=1.7
      JSONArray jsonArray_root  = new JSONArray();
        JSONObject json_object_root  = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            json_object_root.put("productId", globalVariable.productid);
            json_object_root.put("actualDelivery", Double.parseDouble(actual_delivery.getText().toString().trim()));
            jsonArray_root.put(json_object_root);
            jsonObject.put("root_array",jsonArray_root);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Service_Call(getApplicationContext(), "/schedule/"+globalVariable.userid+"/"+currentdate+"/"+globalVariable.customerid,"PUT", jsonObject, true, new MyServiceListener() {
            @Override
            public void getServiceData(Model_TLI_API_Response api_response) {
                String result = api_response.data;
                model_TLI_api_response = api_response;

                Log.d("test--", TAG+"--result data1 --" + result);
                Log.d("test--", TAG+"--responsecode1" + model_TLI_api_response.response_code);
                Log.d("test--", TAG+"--responsecode1HttpsURLConnection.HTTP_OK" + HttpsURLConnection.HTTP_OK);
                try {
                    if (model_TLI_api_response.response_code == HttpsURLConnection.HTTP_OK) {
                       // Toast.makeText(getApplicationContext(),""+result,Toast.LENGTH_LONG).show();
                        alertDialog2.setMessage(result);
                        alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                              /*  final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                                globalVariable.globalempList.remove(globalVariable.globalemp_listing_model);*/

                              finish();
                               // submit.requestFocus();
                            }
                        });


                        try{
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }
                            catch (Exception ex)
                            {
                                Log.d("test--",TAG+" exception in progressDialog.dismiss()--"+ex);
                            }



                    }
                    else
                    {
                        try{
                            if (progressDialog == null ||  progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        catch (Exception ex)
                        {
                            Log.d("test--",TAG+" exception in progressDialog.dismiss()--"+ex);
                        }
                      //  Toast.makeText(getApplicationContext(), "Something goes wrong.Please try agian!", Toast.LENGTH_LONG).show();
                        alertDialog2.setMessage("Something goes wrong.Please try agian!");
                        alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                progressDialog.dismiss();
                            }
                        });

                    }


                }
                catch (Exception ex)
                {
                    try{
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("test--",TAG+" exception in progressDialog.dismiss()--"+e);
                    }
                  //  Toast.makeText(getApplicationContext(), "Something goes wrong please try again!", Toast.LENGTH_LONG).show();
                    Log.d("test--",TAG+"--exception in Service_Call1--"+ex);
                    alertDialog2.setMessage("Something goes wrong.Please try agian!");
                    alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            progressDialog.dismiss();
                        }
                    });
                }
                alertDialog2.show();
            }
        });

        /////////////////////////////////////////////////

    }
    private void findById() {

        progressDialog = new ProgressDialog(delivery_confirmation.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_custname = (TextView) findViewById(R.id.tv_custname);
        tv_custaddrs = (TextView) findViewById(R.id.tv_custaddrs);
        schedule_delivery = (TextView) findViewById(R.id.schedule_delivery);
        actual_delivery = (EditText) findViewById(R.id.actual_delivery);
        submit =  (Button) findViewById(R.id.submit);
    }
    private void setFormValue() {
        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        tv_custname.setText(globalVariable.customername);
        tv_custaddrs.setText(globalVariable.customeraddrs);
        schedule_delivery.setText(String.valueOf(globalVariable.scheduledelivery));

    }
    private boolean validateForm(){

        alertDialog = new AlertDialog.Builder(delivery_confirmation.this);
        if (actual_delivery.getText().toString().trim().equalsIgnoreCase("")) {
            alertDialog.setTitle("VALIDATION");
            alertDialog.setMessage("Please Enter Actual Delivery");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    actual_delivery.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        }

        else {
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
