package com.example.user.sunaitestingapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyServiceListener;
import com.example.user.sunaitestingapp.common.Service_Call;
import com.example.user.sunaitestingapp.model.StringWithTag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class registration_info extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText first_name,last_name,altr_phoneno,address,email;
    TextView mobileno;
    Spinner door_bell;
    Button submit;
    public ProgressDialog progressDialog = null;
    AlertDialog.Builder alertDialog,alertDialog2;
    Handler mHandler = null;
    private Model_TLI_API_Response model_TLI_api_response;
    String TAG = "registration_info";
    List<StringWithTag> doorbelist = new ArrayList<StringWithTag>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_info);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);*/
        findViewId();
        mobileno.setText(globalVariable.registermobileno);
        alertDialog = new AlertDialog.Builder(registration_info.this);
        alertDialog2 = new AlertDialog.Builder(registration_info.this);
        mHandler = new Handler();
        progressDialog = new ProgressDialog(registration_info.this);
        doorbelist.add(new StringWithTag("Select Doorbell", "0"));
        doorbelist.add(new StringWithTag("Doorbell1", "Doorbell1"));
        doorbelist.add(new StringWithTag("Doorbell2", "Doorbell2"));

        final ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(
                this, R.layout.spinner_stylish_item, doorbelist
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_stylish_item);
        door_bell.setAdapter(spinnerArrayAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    if (validateForm())
                    {
                        new MyAsyncTask().execute();

                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void findViewId() {
     
        first_name = (EditText)  findViewById(R.id.first_name);
        last_name = (EditText)  findViewById(R.id.last_name);
        altr_phoneno = (EditText)  findViewById(R.id.altr_phoneno);
        address = (EditText)  findViewById(R.id.address);
        email = (EditText)  findViewById(R.id.email);
        door_bell = (Spinner) findViewById(R.id.door_bell);
        door_bell.setOnItemSelectedListener(this);
        mobileno = (TextView)  findViewById(R.id.mobileno);
        
        submit =  (Button) findViewById(R.id.submit);
    }
    private void registerUser() {


        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        //  Log.d("save_form--", TAG+"--actual_delivery--" +actual_delivery.getText().toString().trim());
        Log.d("save_form--", TAG+"--start_date--" +first_name.getText().toString().trim());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        //////////////////Call Api/////////////////////////////////actualDelivery=1.7

        JSONObject json_object_root = new JSONObject();


        try {
            GlobalClass gv = (GlobalClass) getApplicationContext();

                json_object_root.put("firstName", first_name.getText().toString().trim());
                json_object_root.put("lastName",last_name.getText().toString().trim());
                json_object_root.put("email", email.getText().toString().trim());
                json_object_root.put("address1",address.getText().toString().trim());

                json_object_root.put("city",globalVariable.registercity);
                json_object_root.put("locality",gv.registerlocality);
                json_object_root.put("phone", globalVariable.registermobileno);


              /*  json_object_root.put("firstName", "mks1");
                json_object_root.put("lastName","");
                json_object_root.put("email", "mks@gmail.com");
                json_object_root.put("phone", "9911223355");

                json_object_root.put("city","noida");
                json_object_root.put("locality","sector 51");
                json_object_root.put("address1","c-32, sector1");
*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Service_Call(getApplicationContext(), "/customer/registration","POST", json_object_root, true, new MyServiceListener() {
            @Override
            public void getServiceData(Model_TLI_API_Response api_response) {
                String result = api_response.data;
                model_TLI_api_response = api_response;

                Log.d("test--", TAG+"--result data1 --" + result);
                Log.d("test--", TAG+"--responsecode1" + model_TLI_api_response.response_code);
                Log.d("test--", TAG+"--responsecode1HttpsURLConnection.HTTP_OK" + HttpsURLConnection.HTTP_OK);
                try {
                    if (model_TLI_api_response.response_code == HttpsURLConnection.HTTP_OK) {
                         JSONObject rootObject = new JSONObject(result);

                        Log.d("registerUser--", TAG+"customerid" + rootObject.getInt("id"));


                  /*      alertDialog2.setMessage("Registration Successful");
                        alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                                try {

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/
                                globalVariable.customerid =  String.valueOf(rootObject.getInt("id"));
                                Intent intent = new Intent(getApplicationContext(), calendar_activity.class);
                                startActivity(intent);
                                finish();


                       /*     }
                        });
*/

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
                        alertDialog2.show();
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
                    alertDialog2.show();
                }

            }
        });

        /////////////////////////////////////////////////

    }
    private boolean validateForm(){

        alertDialog = new AlertDialog.Builder(registration_info.this);
        if (first_name.getText().toString().trim().equalsIgnoreCase("")) {
         //   alertDialog.setTitle("VALIDATION");
            alertDialog.setMessage("Please Enter First Name");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    first_name.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        }
        else if(last_name.getText().toString().trim().equalsIgnoreCase("")){

            alertDialog.setMessage("Please Enter Last Name");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    last_name.requestFocus();
                }
            });
            alertDialog.show();
            return false;

        }
        else if(address.getText().toString().trim().equalsIgnoreCase("")){

            alertDialog.setMessage("Please Enter Address");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    address.requestFocus();
                }
            });
            alertDialog.show();
            return false;

        }
        else if(email.getText().toString().trim().equalsIgnoreCase("")){

            alertDialog.setMessage("Please Enter Email Id");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    email.requestFocus();
                }
            });
            alertDialog.show();
            return false;

        }
      /*  else if(door_bell.getText().toString().trim().equalsIgnoreCase("")){
            alertDialog.setTitle("VALIDATION");
            alertDialog.setMessage("Please Enter Doorbell");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    door_bell.requestFocus();
                }
            });
            alertDialog.show();
            return false;

        }*/

        else {
            return true;
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {

            case R.id.door_bell:
                StringWithTag swtfID = (StringWithTag) adapterView.getItemAtPosition(i);
                String cityname = (String) swtfID.tag;

                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class MyAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


            registerUser();
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
