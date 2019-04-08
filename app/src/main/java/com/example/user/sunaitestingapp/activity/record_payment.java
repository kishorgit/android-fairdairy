package com.example.user.sunaitestingapp.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyServiceListener;
import com.example.user.sunaitestingapp.common.Service_Call;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class record_payment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] payment_type = {"SELECT", "CASH", "CHEQUE"};
    Spinner spinner_payment_type;
    TextView tv_custname, tv_custaddrs;
    EditText payment_amt,cheque_num,bank_name;
    LinearLayout linear_cheque_num,linear_bank_name;
    Button submit;
    String payment_type_val= "";
    String payment_amt_str;
    ContentValues cv;
    SQLiteDatabase mDB;
    private Model_TLI_API_Response model_TLI_api_response;
    String TAG = "record_payment";
    public ProgressDialog progressDialog = null;
    AlertDialog.Builder alertDialog,alertDialog2;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_payment);
        mDB = GlobalClass.mDB;
        alertDialog2 =  new AlertDialog.Builder(record_payment.this);
        findById();
         setFormValue();
        toolbar.setTitle(Html.fromHtml("<font >Record Payments</font>" + "<font color='#FFFFFF'> </font>"));
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.titletextStyle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //  setActionBar();
        // tv_toolbar.setText("Record Payments");
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_stylish_item, payment_type
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_stylish_item);
        spinner_payment_type.setAdapter(spinnerArrayAdapter);
        spinner_payment_type.setOnItemSelectedListener(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                if(validateForm())
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

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(2.0f);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#87CEEB")));
            actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Record Payments</font>" + "<font color='#FFFFFF'> </font>"));

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
        Log.d("test--", TAG + "--cheque_num --" + cheque_num.getText().toString().trim());
        Log.d("test--", TAG + "--bank_name --" + bank_name.getText().toString().trim());
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        JSONObject json_object_root = new JSONObject();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        try {

            json_object_root.put("amount",Double.parseDouble(payment_amt.getText().toString().trim()) );
            json_object_root.put("paymentDate", currentdate);
            json_object_root.put("createdBy", globalVariable.userid);
            json_object_root.put("paymentMethod", payment_type_val);
            json_object_root.put("chequeNumber", cheque_num.getText().toString().trim());
            json_object_root.put("chequeDate",currentdate);
            json_object_root.put("bankName", bank_name.getText().toString().trim());
            json_object_root.put("customerId", globalVariable.customerid);
            json_object_root.put("status", "PENDING");
            json_object_root.put("invoiceId", 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Service_Call(getApplicationContext(), "/customer/"+globalVariable.customerid+"/payment", "POST", json_object_root, false, new MyServiceListener() {
            @Override
            public void getServiceData(Model_TLI_API_Response api_response) {
                String result = api_response.data;
                model_TLI_api_response = api_response;

                Log.d("test--", TAG + "--result data1 --" + result);
                Log.d("test--", TAG + "--responsecode1" + model_TLI_api_response.response_code);
                Log.d("test--", TAG + "--responsecode1HttpsURLConnection.HTTP_OK" + HttpsURLConnection.HTTP_OK);
                try {
                    if (model_TLI_api_response.response_code == HttpsURLConnection.HTTP_OK) {

                        {
                           // Toast.makeText(getApplicationContext(), "Payment Added Successfully.", Toast.LENGTH_LONG).show();
                            alertDialog2.setMessage("Payment recorded successfully.");
                            alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();
                                 //   submit.requestFocus();
                                }
                            });


                            try {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            } catch (Exception ex) {
                                Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                            }


                        }
                    } else {
                        try {
                            if (progressDialog == null || progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception ex) {
                            Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                        }

                       // Toast.makeText(getApplicationContext(), "Payment Not Added.Please Try Again.", Toast.LENGTH_LONG).show();
                        alertDialog2.setMessage("Payment Not Added.Please Try Again.");
                        alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                submit.requestFocus();
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
                  //  Toast.makeText(getApplicationContext(), "Something goes wrong please try again!", Toast.LENGTH_LONG).show();
                    Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
                    alertDialog2.setMessage("Something goes wrong please try again!");
                    alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            submit.requestFocus();
                        }
                    });
                }
                alertDialog2.show();
            }
        });
        ///////////////////////////////////////

    }

    private void findById() {
        progressDialog = new ProgressDialog(record_payment.this);
        /*  tv_toolbar = (TextView) findViewById(R.id.tv_toolbar);*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_custname = (TextView) findViewById(R.id.tv_custname);
        tv_custaddrs = (TextView) findViewById(R.id.tv_custaddrs);
        payment_amt = (EditText) findViewById(R.id.payment_amt);
        cheque_num= (EditText) findViewById(R.id.cheque_num);
        bank_name= (EditText) findViewById(R.id.bank_name);
        spinner_payment_type = (Spinner) findViewById(R.id.spinner_payment_type);
        linear_cheque_num = (LinearLayout) findViewById(R.id.linear_cheque_num);
        linear_bank_name = (LinearLayout) findViewById(R.id.linear_bank_name);
        submit = (Button) findViewById(R.id.submit);
    }

    private void setFormValue() {
        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        tv_custname.setText(globalVariable.customername);
        tv_custaddrs.setText(globalVariable.customeraddrs);


    }

    private boolean validateForm() {

        alertDialog = new AlertDialog.Builder(record_payment.this);
        if (spinner_payment_type.getSelectedItemPosition() == 0) {
            alertDialog.setTitle("VALIDATION");
            alertDialog.setMessage("PLEASE  SELECT Payment Type");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    spinner_payment_type.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        } else if (payment_amt.getText().toString().trim().equalsIgnoreCase("")) {
            alertDialog.setTitle("VALIDATION");
            alertDialog.setMessage("Please Enter Amount");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    payment_amt.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        } else {
            return true;
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_payment_type:
                if(i==1){
                    payment_type_val = "CASH";
                    linear_cheque_num.setVisibility(View.GONE);
                    linear_bank_name.setVisibility(View.GONE);
                }
                else if(i==2){
                    payment_type_val = "CHEQUE";
                    linear_cheque_num.setVisibility(View.VISIBLE);
                    linear_bank_name.setVisibility(View.VISIBLE);
                }
                else {
                    linear_cheque_num.setVisibility(View.GONE);
                    linear_bank_name.setVisibility(View.GONE);
                }

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
