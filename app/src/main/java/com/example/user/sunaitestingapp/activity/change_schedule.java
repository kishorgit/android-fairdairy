package com.example.user.sunaitestingapp.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.adapter.change_schedule_adapter;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyDividerItemDecoration;
import com.example.user.sunaitestingapp.common.MyServiceListener;
import com.example.user.sunaitestingapp.common.RecyclerTouchListener;
import com.example.user.sunaitestingapp.common.Service_Call;
import com.example.user.sunaitestingapp.model.change_schedule_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class change_schedule extends AppCompatActivity {
    Button submit;
    TextView admin_name,admin_addrs,schedule_delivery,start_date,end_date;
    EditText actual_delivery;
    ContentValues cv;
    String actual_delivery_str;
    SQLiteDatabase mDB;
    private Model_TLI_API_Response model_TLI_api_response;
    String TAG = "change_schedule";
    public ProgressDialog progressDialog =null;
    public ProgressDialog progressDialogDone = null;
    AlertDialog.Builder alertDialog,alertDialog2;
    Toolbar toolbar;
    private int mYear, mMonth, mDay;
    Handler mHandler = null;
    private RecyclerView recyclerView;
    private change_schedule_adapter mAdapter;
    private List<change_schedule_model> empList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_schedule);
        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        alertDialog2 = new AlertDialog.Builder(change_schedule.this);
        mHandler = new Handler();
        findById();
        setFormValue();
        //  setActionBar();
        toolbar.setTitle(Html.fromHtml("<font  >Change Schedule</font>"+"<font color='#FFFFFF'> </font>"));
        toolbar.setTitleTextAppearance(getApplicationContext(),R.style.titletextStyle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDB = GlobalClass.mDB;
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(change_schedule.this,R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

                                Date d1 = null;
                                try {
                                    d1 = sdformat.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                start_date.setText(sdformat.format(d1));
                                start_date.setTextSize(12);
                                start_date.setGravity(Gravity.CENTER_HORIZONTAL);
                                start_date.setTextColor(getResources().getColor(R.color.black_color));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(change_schedule.this,R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

                                Date d1 = null;
                                try {
                                    d1 = sdformat.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                end_date.setText(sdformat.format(d1));
                                end_date.setTextSize(12);
                                end_date.setGravity(Gravity.CENTER_HORIZONTAL);
                                end_date.setTextColor(getResources().getColor(R.color.black_color));

                            //    end_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        ////////////////////////////////////Listing Product////////////////////

        mAdapter = new change_schedule_adapter(empList, new change_schedule_adapter.MyAdapterListener() {

            @Override
            public void modifyInfoOnClick(View v, change_schedule_model change_schedule_model) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    change_schedule_model.setProductQty(change_schedule_model.getProductQty()+0.5);
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void subQuantityOnClick(View v, change_schedule_model change_schedule_model) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    if((change_schedule_model.getProductQty()-0.5)>0.0){

                        change_schedule_model.setProductQty(change_schedule_model.getProductQty()-0.5);
                    }
                    else {
                        change_schedule_model.setProductQty(0.0);
                    }



                    mAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }
            }

        }

        );

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL, 21));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //   recyclerView.getChildAt(position).setSelected(false);
            }

            @Override
            public void onLongClick(View view, final int position) {

            }

        }));
        ////////////////////////////////////Listing Product////////////////////
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
               if (validateForm())
                    {
               new MyAsyncTaskSaveForm().execute();

                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }
            }
        });



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

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            }
        });

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

      //  Log.d("save_form--", TAG+"--actual_delivery--" +actual_delivery.getText().toString().trim());
        Log.d("save_form--", TAG+"--start_date--" +start_date.getText().toString().trim());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        //////////////////Call Api/////////////////////////////////actualDelivery=1.7
        JSONArray jsonArray_root  = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        try {
            for(int i=0;i<empList.size();i++) {
                change_schedule_model change_schedule_model=empList.get(i);
                        JSONObject json_object_root = new JSONObject();
                json_object_root.put("productId", change_schedule_model.getProduct_id());
                json_object_root.put("changeQuantity",change_schedule_model.getProductQty());
                if(start_date.getText().toString().trim().equalsIgnoreCase("null")||start_date.getText().toString().trim().equalsIgnoreCase(">")|| start_date.getText().toString().trim().equals(null)){}
               else{
                    json_object_root.put("scheduleChangeDate", start_date.getText().toString());
                }
                if(end_date.getText().toString().trim().equalsIgnoreCase("null")||end_date.getText().toString().trim().equalsIgnoreCase(">")|| end_date.getText().toString().trim().equals(null)){}
                else{
                    json_object_root.put("endDate", end_date.getText().toString());
                }


          /*  json_object_root.put("productId",1);
            json_object_root.put("changeQuantity",2.7);
            json_object_root.put("startDate", "2018-12-24");*/

                jsonArray_root.put(json_object_root);
            }


            jsonObject.put("root_array",jsonArray_root);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Service_Call(getApplicationContext(), "/customer/"+globalVariable.customerid+"/changeschedule","PUT", jsonObject, true, new MyServiceListener() {
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
                        JSONObject jsonObject1 = new JSONObject(result);
                        alertDialog2.setMessage(jsonObject1.getString("responseMsg"));
                        alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                              /*  final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                                globalVariable.globalempList.remove(globalVariable.globalemp_listing_model);*/

                                finish();
                                // submit.requestFocus();
                            }
                        });


                        try{
                            if (progressDialogDone != null && progressDialogDone.isShowing()) {
                                progressDialogDone.dismiss();
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
                            if (progressDialogDone == null ||  progressDialogDone.isShowing()) {
                                progressDialogDone.dismiss();
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

                                progressDialogDone.dismiss();
                            }
                        });

                    }


                }
                catch (Exception ex)
                {
                    try{
                        if (progressDialogDone != null && progressDialogDone.isShowing()) {
                            progressDialogDone.dismiss();
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

                            progressDialogDone.dismiss();
                        }
                    });
                }
                alertDialog2.show();
            }
        });

        /////////////////////////////////////////////////

    }
    private void findById() {

        progressDialog = new ProgressDialog(change_schedule.this);
        progressDialogDone = new ProgressDialog(change_schedule.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        start_date= (TextView) findViewById(R.id.start_date);
        end_date= (TextView) findViewById(R.id.end_date);
        admin_name = (TextView) findViewById(R.id.admin_name);
        admin_addrs = (TextView) findViewById(R.id.admin_addrs);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        submit =  (Button) findViewById(R.id.submit);
    }
    private void setFormValue() {
        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        admin_name.setText(globalVariable.customername);
        admin_addrs.setText(globalVariable.customeraddrs);

    }
    private boolean validateForm(){

        alertDialog = new AlertDialog.Builder(change_schedule.this);
        if (start_date.getText().toString().trim().equalsIgnoreCase("")|| start_date.getText().toString().trim().equalsIgnoreCase(">")) {
            alertDialog.setTitle("VALIDATION");
            alertDialog.setMessage("Please Enter Start Date");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    start_date.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        }

        else {
            return true;
        }



    }


    private void requestLeaveListData() {
        String result = "";
        int arrayloop = 0;
        empList.clear();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Double   totalactualDeliver=0.0;
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
     //   api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/product/subscribe/"+globalVariable.customerid);
        api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/"+globalVariable.customerid+"/subscription");
        try {
            Log.d("requestLeaveListData", TAG + "api_response.response_code" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK) {
                result = api_response.data;
                JSONArray dailyscheduleArray = new JSONArray(result);
                Log.d("requestLeaveListData", TAG + "dailyscheduleArrayString" + dailyscheduleArray.toString());
                Log.d("requestLeaveListData", TAG + "dailyscheduleArray.length()" + dailyscheduleArray.length());
                if (dailyscheduleArray.length() > 0) {
                    for (arrayloop = 0; arrayloop < dailyscheduleArray.length(); arrayloop++) {

                        JSONObject dailyscheduleObject = dailyscheduleArray.getJSONObject(arrayloop);

                        JSONObject dailyscheduleproductObject =null;

                        if(dailyscheduleObject.isNull("product")==false) {
                            dailyscheduleproductObject = dailyscheduleObject.getJSONObject("product");
                            Log.d("requestLeaveListData", TAG + "dailyscheduleproductObject" + dailyscheduleproductObject.toString());

                        }
          final int        product_id = dailyscheduleproductObject.getInt("id");
                        Log.d("requestLeaveListData", TAG + "groupproduct_id" + product_id);

                        final      String product_name =dailyscheduleproductObject.getString("name");
                        final  Double pricePerUnit =  dailyscheduleproductObject.getDouble("pricePerUnit");
                        final   Double productQty =dailyscheduleObject.getDouble("productQty");
                        Log.d("requestLeaveListData", TAG + "productQty" + productQty);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                change_schedule_model change_schedule_model= new change_schedule_model();
                                change_schedule_model.setProduct_name(product_name);
                                change_schedule_model.setProduct_id(product_id);
                                change_schedule_model.setPricePerUnit(pricePerUnit);
                                change_schedule_model.setProductQty(productQty);
                                empList.add(change_schedule_model);

                              /*  change_schedule_model change_schedule_model1= new change_schedule_model();
                                change_schedule_model1.setProduct_name(product_name);
                                change_schedule_model1.setProduct_id(product_id);
                                change_schedule_model1.setPricePerUnit(pricePerUnit);
                                change_schedule_model1.setProductQty(productQty);
                                empList.add(change_schedule_model1);

                                change_schedule_model change_schedule_model2= new change_schedule_model();
                                change_schedule_model2.setProduct_name(product_name);
                                change_schedule_model2.setProduct_id(product_id);
                                change_schedule_model2.setPricePerUnit(pricePerUnit);
                                change_schedule_model2.setProductQty(productQty);
                                empList.add(change_schedule_model2);
                                change_schedule_model change_schedule_model3= new change_schedule_model();
                                change_schedule_model3.setProduct_name(product_name);
                                change_schedule_model3.setProduct_id(product_id);
                                change_schedule_model3.setPricePerUnit(pricePerUnit);
                                change_schedule_model3.setProductQty(productQty);
                                empList.add(change_schedule_model3);
                                change_schedule_model change_schedule_model4= new change_schedule_model();
                                change_schedule_model4.setProduct_name(product_name);
                                change_schedule_model4.setProduct_id(product_id);
                                change_schedule_model4.setPricePerUnit(pricePerUnit);
                                change_schedule_model4.setProductQty(productQty);
                                empList.add(change_schedule_model4);*/


                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }

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
                            Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();

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
                        Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();

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
            Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
        }
        /////////////////////////////////////////////////////////


    }


    private class MyAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


            requestLeaveListData();
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
    private class MyAsyncTaskSaveForm extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {
         save_form();


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

    @Override
    public void onResume() {
        super.onResume();
        new MyAsyncTask().execute();
        recyclerView.setAdapter(mAdapter);
    }
}
