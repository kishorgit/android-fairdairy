package com.example.user.sunaitestingapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.adapter.navproductlist_adapter;
import com.example.user.sunaitestingapp.adapter.navproductlist_adapter;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyDividerItemDecoration;
import com.example.user.sunaitestingapp.common.RecyclerTouchListener;
import com.example.user.sunaitestingapp.model.change_schedule_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class productlist_activity extends AppCompatActivity {

    public String TAG = "productlist_activity";
    TextView currnetdate;
    private RecyclerView recyclerView;
    private navproductlist_adapter mAdapter;
    private List<change_schedule_model> empList = new ArrayList<>();
    Handler mHandler = null;
    private Model_TLI_API_Response model_TLI_api_response;
    public ProgressDialog progressDialog = null;
    AlertDialog.Builder alertDialog;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist_activity);
        findViewId();
        toolbar.setTitle(Html.fromHtml("<font  >Products</font>"+"<font color='#FFFFFF'> </font>"));
        toolbar.setTitleTextAppearance(getApplicationContext(),R.style.titletextStyle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAdapter = new navproductlist_adapter(empList,getApplicationContext(), new navproductlist_adapter.MyAdapterListener() {

            @Override
            public void modifyInfoOnClick(View v, change_schedule_model change_schedule_model) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    final GlobalClass globalVariable = (GlobalClass)getApplicationContext();

                    if(change_schedule_model.getSubscriptable()==false)
                    {   /* globalVariable.productid=change_schedule_model.getProduct_id();
                        globalVariable.product_name=change_schedule_model.getProduct_name();
                        globalVariable.pricePerUnit= String.valueOf(change_schedule_model.getPricePerUnit());

                        Intent intent = new Intent(getApplicationContext(),subscription_activity.class);
                        startActivity(intent);
                        finish();*/
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void subQuantityOnClick(View v, change_schedule_model change_schedule_model) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    final GlobalClass globalVariable = (GlobalClass)getApplicationContext();

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
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
                final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
                change_schedule_model change_schedule_model = empList.get(position);
                if(change_schedule_model.getSubscriptable()==true){
                    globalVariable.productid=change_schedule_model.getProduct_id();
                    globalVariable.product_name=change_schedule_model.getProduct_name();
                    globalVariable.pricePerUnit= String.valueOf(change_schedule_model.getPricePerUnit());
                    globalVariable.globalsubscriptflag=0;
                    Intent intent = new Intent(getApplicationContext(),subscription_activity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onLongClick(View view, final int position) {

            }

        }));

        ////////////////////listing product///////////////////////////////////////


        new MyAsyncTask().execute();

        recyclerView.setAdapter(mAdapter);

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
        Model_TLI_API_Response api_subsresponse = new Model_TLI_API_Response();
     // api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/"+globalVariable.customerid+"/subscription");
        api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/products");

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

                        final int        product_id = dailyscheduleObject.getInt("id");
                        final      String product_name =dailyscheduleObject.getString("name");
                        final  Double pricePerUnit =  dailyscheduleObject.getDouble("pricePerUnit");
                        final  boolean subscriptable =      dailyscheduleObject.getBoolean("subscriptable");
                        final      String image =dailyscheduleObject.getString("image");
                        final  Double prodqty =  getproductqty(product_id);



                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                change_schedule_model change_schedule_model= new change_schedule_model();
                                change_schedule_model.setProduct_name(product_name);
                                change_schedule_model.setProduct_id(product_id);
                                change_schedule_model.setPricePerUnit(pricePerUnit);
                                change_schedule_model.setImageurl(image);
                                change_schedule_model.setProductQty(prodqty);
                                change_schedule_model.setSubscriptable(subscriptable);


                                empList.add(change_schedule_model);
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
            //   Toast.makeText(getActivity().getApplicationContext()(), "Something goes wrong please try again!", Toast.LENGTH_LONG).show();
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
    private void findViewId() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        alertDialog = new AlertDialog.Builder(productlist_activity.this);
        mHandler = new Handler();
        progressDialog = new ProgressDialog(productlist_activity.this);
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
    private Double getproductqty(int pid) {

        String result = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Double   totalqty =0.0;
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
        api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/"+globalVariable.customerid+"/subscription/product/"+pid);

        try {
            Log.d("requestLeaveListData", TAG + "subscribeddata" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK) {
                result = api_response.data;

                Log.d("requestLeaveListData", TAG + "resultString" +result);

                JSONObject dailyscheduleObject = new JSONObject(result);
                if(dailyscheduleObject.length()>0) {
                    JSONObject productObject = dailyscheduleObject.getJSONObject("product");
                    final int product_id = productObject.getInt("id");
                    final Double productQty = dailyscheduleObject.getDouble("productQty");
                    final Double sundayQty = dailyscheduleObject.getDouble("sundayQty");
                    final Double mondayQty = dailyscheduleObject.getDouble("mondayQty");
                    final Double tuesdayQty = dailyscheduleObject.getDouble("tuesdayQty");
                    final Double wednesdayQty = dailyscheduleObject.getDouble("wednesdayQty");
                    final Double thursdayQty = dailyscheduleObject.getDouble("thursdayQty");
                    final Double fridayQty = dailyscheduleObject.getDouble("fridayQty");
                    final Double saturdayQty = dailyscheduleObject.getDouble("saturdayQty");
                    final String frqncy = dailyscheduleObject.getString("frequency");
                    final String startDate = dailyscheduleObject.getString("startDate");
                    final  String pname =productObject.getString("name");
                    final  String pprice =productObject.getString("pricePerUnit");
                    Log.d("group", TAG + "price_per_unit" + productObject.getString("pricePerUnit"));
                    Log.d("group", TAG + "name" + productObject.getString("name"));
                    if(frqncy.equalsIgnoreCase("SELECT_DAYS")){
                        totalqty =   sundayQty+mondayQty+tuesdayQty+wednesdayQty+thursdayQty+fridayQty+saturdayQty;
                    }
                    else {
                        totalqty =productQty;
                    }


                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception ex) {
                        Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                    }
                }
                else {

                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception ex) {
                        Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                    }

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
                        //    Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();

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
            //   Toast.makeText(getActivity().getApplicationContext()(), "Something goes wrong please try again!", Toast.LENGTH_LONG).show();
            Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
        }
        /////////////////////////////////////////////////////////
        return totalqty;
    }
}
