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
import android.view.LayoutInflater;
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
import com.example.user.sunaitestingapp.model.StringWithTag;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class registration_activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner_city_type, spinner_locality;
    List<StringWithTag> citylist = new ArrayList<StringWithTag>();
    List<StringWithTag> localitylist = new ArrayList<StringWithTag>();
    String TAG = "registration_activity";
    public ProgressDialog progressDialog = null;
    public ProgressDialog progressDialogDone = null;
    public ProgressDialog progressDialogPhone = null;
    AlertDialog.Builder alertDialog, alertDialog2;
    Handler mHandler = null;
    EditText mobile_no;
    TextView msg;
    Button submit;
    int flag =0;
    android.support.v7.app.AlertDialog.Builder alert;
    android.support.v7.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_activity);

    /*    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);*/
        alertDialog = new AlertDialog.Builder(registration_activity.this);
        alertDialog2 = new AlertDialog.Builder(registration_activity.this);
        mHandler = new Handler();
        progressDialog = new ProgressDialog(registration_activity.this);
        progressDialogDone = new ProgressDialog(registration_activity.this);
        progressDialogPhone = new ProgressDialog(registration_activity.this);
        findById();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    if (validateForm()) {

                        new MyAsyncTaskPhone(mobile_no.getText().toString().trim()).execute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getAllCity() {


        String result = "";
        int arrayloop = 0;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                citylist.clear();
                citylist.add(new StringWithTag("Select Your City", "0"));
                localitylist.clear();
                localitylist.add(new StringWithTag("Select Your Locality", "0"));
            }
        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
        api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/operations/city");
        try {
            Log.d("requestLeaveListData", TAG + "api_response.response_code" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK) {
                result = api_response.data;
                JSONArray dailyscheduleArray = new JSONArray(result);
                Log.d("requestLeaveListData", TAG + "dailyscheduleArrayString" + dailyscheduleArray.toString());
                Log.d("requestLeaveListData", TAG + "dailyscheduleArray.length()" + dailyscheduleArray.length());

                if (dailyscheduleArray.length() > 0) {
                    for (arrayloop = 0; arrayloop < dailyscheduleArray.length(); arrayloop++) {
                        final String cityname = dailyscheduleArray.getString(arrayloop);
                        final int key = arrayloop + 1;
                        Log.d("getAllCity", TAG + "cityname" + cityname);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // citylist.add(cityname);

                                citylist.add(new StringWithTag(cityname, cityname));

                            }
                        });
                    }
                    final ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(
                            this, R.layout.spinner_stylish_item, citylist
                    );
                    final ArrayAdapter<StringWithTag> spinnerlocalitylistAdapter = new ArrayAdapter<StringWithTag>(
                            this, R.layout.spinner_stylish_item, localitylist
                    );
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            spinnerlocalitylistAdapter.setDropDownViewResource(R.layout.spinner_stylish_item);
                            spinner_locality.setAdapter(spinnerlocalitylistAdapter);

                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_stylish_item);
                            spinner_city_type.setAdapter(spinnerArrayAdapter);
if(spinner_city_type.getSelectedItemPosition()>0) {
    flag = 1;
}
                        }
                    });
                    //
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception ex) {
                        Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                    }
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (progressDialog == null || progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            } catch (Exception ex) {
                                Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                            }
                         //   Toast.makeText(getApplicationContext(), "No City Found From Server", Toast.LENGTH_LONG).show();
                          msg.setVisibility(View.VISIBLE);
                            flag =0;
                        }
                    });
                }
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (progressDialog == null || progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception ex) {
                            Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                        }
                    //    Toast.makeText(getApplicationContext(), "No City Found From Server", Toast.LENGTH_LONG).show();
                        msg.setVisibility(View.VISIBLE);
                        flag =0;
                    }
                });

            }
        } catch (Exception ex) {
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
                Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + e);
            }
            //   Toast.makeText(getApplicationContext(), "Something goes wrong please try again!", Toast.LENGTH_LONG).show();
            msg.setVisibility(View.VISIBLE);
            flag =0;
            Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
        }


    }

    private void findById() {
        spinner_city_type = (Spinner) findViewById(R.id.spinner_city_type);
        spinner_city_type.setOnItemSelectedListener(this);
        spinner_locality = (Spinner) findViewById(R.id.spinner_locality);
        spinner_locality.setOnItemSelectedListener(this);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        msg  = (TextView) findViewById(R.id.msg);
        submit = (Button) findViewById(R.id.submit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new MyAsyncTask().execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (adapterView.getId()) {

            case R.id.spinner_city_type:
                StringWithTag swtfID = (StringWithTag) adapterView.getItemAtPosition(i);
                String cityname = (String) swtfID.tag;
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                globalVariable.registercity = cityname;
                Log.d(TAG, "onItemSelected: cityname" + cityname);
                new MyAsyncTaskDone(cityname).execute();

                break;
            case R.id.spinner_locality:
                StringWithTag swtfID1 = (StringWithTag) adapterView.getItemAtPosition(i);
                String localityname = (String) swtfID1.tag;
                GlobalClass gv = (GlobalClass) getApplicationContext();
                gv.registerlocality = localityname;
                Log.d(TAG, "onItemSelected: localityname" + localityname);

                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void getAllLocality(String cityname) {

        String result = "";
        int arrayloop = 0;


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
        api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/operations/locality/" + cityname);
        try {
            Log.d("requestLeaveListData", TAG + "api_response.response_code" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK) {
                result = api_response.data;
                JSONArray dailyscheduleArray = new JSONArray(result);
                Log.d("requestLeaveListData", TAG + "dailyscheduleArrayString" + dailyscheduleArray.toString());
                Log.d("requestLeaveListData", TAG + "dailyscheduleArray.length()" + dailyscheduleArray.length());

                if (dailyscheduleArray.length() > 0) {
                    for (arrayloop = 0; arrayloop < dailyscheduleArray.length(); arrayloop++) {
                        JSONObject localityobject = dailyscheduleArray.getJSONObject(arrayloop);
                        final String localityname = localityobject.getString("locality");

                        Log.d("getAllLocality", TAG + "localityname" + localityname);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                localitylist.add(new StringWithTag(localityname, localityname));
                            }
                        });
                    }
                    final ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(
                            this, R.layout.spinner_stylish_item, localitylist
                    );

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_stylish_item);
                            spinner_locality.setAdapter(spinnerArrayAdapter);


                        }
                    });
                    //
                    try {
                        if (progressDialogDone != null && progressDialogDone.isShowing()) {
                            progressDialogDone.dismiss();
                        }
                    } catch (Exception ex) {
                        Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                    }
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (progressDialogDone == null || progressDialogDone.isShowing()) {
                                    progressDialogDone.dismiss();
                                }
                            } catch (Exception ex) {
                                Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                            }
                         //   Toast.makeText(getApplicationContext(), "No Locality Found From Server", Toast.LENGTH_LONG).show();
                           if(flag==1) {
                               msg.setVisibility(View.VISIBLE);
                           }
                        }
                    });
                }
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (progressDialogDone == null || progressDialogDone.isShowing()) {
                                progressDialogDone.dismiss();
                            }
                        } catch (Exception ex) {
                            Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                        }
                       // Toast.makeText(getApplicationContext(), "No Locality Found From Server", Toast.LENGTH_LONG).show();
                        if(flag==1) {
                            msg.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        } catch (Exception ex) {
            try {
                if (progressDialogDone != null && progressDialogDone.isShowing()) {
                    progressDialogDone.dismiss();
                }
            } catch (Exception e) {
                Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + e);
            }
            if(flag==1) {
                msg.setVisibility(View.VISIBLE);
            }
            //   Toast.makeText(getApplicationContext(), "Something goes wrong please try again!", Toast.LENGTH_LONG).show();
            Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
        }

    }

    private void checkPhoneValidity(final String mobileno) {
        String result = "";
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
        api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/checkphone/" + mobileno);
        try {
            Log.d("requestLeaveListData", TAG + "api_response.response_code" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK) {
                result = api_response.data;
                JSONObject jsonObject = new JSONObject(result);
                Log.d("requestLeaveListData", TAG + "result" + result);
             /*   alertDialog2.setMessage(jsonObject.getString("responseMsg"));
                alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {*/

                        globalVariable.registermobileno = mobileno;
                        Intent intent = new Intent(getApplicationContext(), registration_info.class);
                        startActivity(intent);

/*
                    }
                });*/

                try {
                    if (progressDialogPhone != null && progressDialogPhone.isShowing()) {
                        progressDialogPhone.dismiss();
                    }
                } catch (Exception ex) {
                    Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                }


            } else {
                mHandler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      ////////////////////////
                                      LayoutInflater inflater = getLayoutInflater();
                                      View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
                                      Button   loginscreen = (Button) alertLayout.findViewById(R.id.loginscreen);
                                      alert = new android.support.v7.app.AlertDialog.Builder(registration_activity.this);
                                      alert.setTitle("Already Registered.");
                                      alert.setView(alertLayout);
                                      alert.setCancelable(false);
                                      loginscreen.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              Intent intent = new Intent(getApplicationContext(), login_activity.class);
                                              startActivity(intent);
                                              finish();
                                          }
                                      });
                                     /* alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                                          }
                                      });*/

                                   /*   alert.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {



                                          }
                                      });*/
                                      dialog = alert.create();
                                      dialog.show();


                                      ///////////////////////////////////////////////


                                      try {
                                          if (progressDialogPhone == null || progressDialogPhone.isShowing()) {
                                              progressDialogPhone.dismiss();
                                          }
                                      } catch (Exception ex) {
                                          Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                                      }



                    }
                });

            }
        } catch (Exception ex) {
            try {
                if (progressDialogPhone != null && progressDialogPhone.isShowing()) {
                    progressDialogPhone.dismiss();
                }
            } catch (Exception e) {
                Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + e);
            }
            Toast.makeText(getApplicationContext(), "Phone is not Valid", Toast.LENGTH_LONG).show();
            Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
              //  alertDialog2.show();

            }
        });
    }

    private class MyAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


            getAllCity();
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


    private class MyAsyncTaskDone extends AsyncTask {
        String cityname;

        public MyAsyncTaskDone(String cityname) {
            this.cityname = cityname;

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            getAllLocality(cityname);


            return true;
        }

        @Override
        protected void onPreExecute() {
            progressDialogDone.setMessage("Please wait.......");
            progressDialogDone.setCanceledOnTouchOutside(false);
            progressDialogDone.setCancelable(false);
            progressDialogDone.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {


            super.onPostExecute(o);
        }
    }


    private class MyAsyncTaskPhone extends AsyncTask {
        String mobileno;

        public MyAsyncTaskPhone(String mobileno) {
            this.mobileno = mobileno;

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            checkPhoneValidity(mobileno);


            return true;
        }

        @Override
        protected void onPreExecute() {
            progressDialogPhone.setMessage("Please wait.......");
            progressDialogPhone.setCanceledOnTouchOutside(false);
            progressDialogPhone.setCancelable(false);
            progressDialogPhone.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {


            super.onPostExecute(o);
        }
    }

    private boolean validateForm() {

        alertDialog = new AlertDialog.Builder(registration_activity.this);
        if (spinner_city_type.getSelectedItemPosition() == 0) {

            alertDialog.setMessage("PLEASE  SELECT City");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    spinner_city_type.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        } else if (spinner_locality.getSelectedItemPosition() == 0) {

            alertDialog.setMessage("PLEASE  SELECT Locality");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    spinner_locality.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        } else if (mobile_no.getText().toString().trim().equalsIgnoreCase("")) {

            alertDialog.setMessage("Please Enter Mobile No.");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mobile_no.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        } else if (mobile_no.getText().toString().trim().length()<10) {

            alertDialog.setMessage("Mobile No. should have 10 digits.");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mobile_no.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        }
        else {
            return true;
        }


    }

}
