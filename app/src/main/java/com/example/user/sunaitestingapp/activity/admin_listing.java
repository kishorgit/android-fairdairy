package com.example.user.sunaitestingapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.adapter.admin_listing_adapter;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyDividerItemDecoration;
import com.example.user.sunaitestingapp.common.RecyclerTouchListener;
import com.example.user.sunaitestingapp.model.admin_listing_model;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class admin_listing extends AppCompatActivity {
    private RecyclerView recyclerView;
    private admin_listing_adapter mAdapter;
    private List<admin_listing_model> empList = new ArrayList<>();
    private Model_TLI_API_Response model_TLI_api_response;
    String TAG = "admin_listing";
    public ProgressDialog progressDialog = null;
    AlertDialog.Builder alertDialog;
    Handler mHandler = null;
    Toolbar toolbar;
    EditText cust_name;
    Button search_btn;
    RelativeLayout movie_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_listing);
        findById();
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentdate = sdf.format(c.getTime());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.ic_drawer);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);

        alertDialog = new AlertDialog.Builder(admin_listing.this);
        mHandler = new Handler();
        progressDialog = new ProgressDialog(admin_listing.this);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_btn.getWindowToken(), 0);
                    new MyAsyncTask().execute();
                    recyclerView.setAdapter(mAdapter);
                }
            }
        });

        mAdapter = new admin_listing_adapter(empList, new admin_listing_adapter.MyAdapterListener() {

            @Override
            public void modifyInfoOnClick(View v, admin_listing_model admin_listing_model) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {

                    globalVariable.customerid = admin_listing_model.getCustomerid();
                    globalVariable.customername = admin_listing_model.getCust_name();
                    globalVariable.customeraddrs = admin_listing_model.getAddrs();


                    Intent intent = new Intent(getApplicationContext(), change_schedule.class);
                    startActivity(intent);
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
    }
    @Override
    public void onResume() {
        recyclerView.setAdapter(mAdapter);
        super.onResume();

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
        api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/search/"+cust_name.getText().toString().trim());
        try {
            Log.d("requestLeaveListData", TAG + "api_response.response_code" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK) {
                result = api_response.data;
                JSONArray dailyscheduleArray = new JSONArray(result);
                Log.d("requestLeaveListData", TAG + "dailyscheduleArrayString" + dailyscheduleArray.toString());
                Log.d("requestLeaveListData", TAG + "dailyscheduleArray.length()" + dailyscheduleArray.length());
                if (dailyscheduleArray.length() > 0) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            movie_list.setVisibility(View.VISIBLE);
                        }
                    });

                    for (arrayloop = 0; arrayloop < dailyscheduleArray.length(); arrayloop++) {

                        JSONObject dailyscheduleObject = dailyscheduleArray.getJSONObject(arrayloop);

                        JSONObject dailyscheduleAddrsObject =null;
                      

                        if(dailyscheduleObject.isNull("address")==false) {
                            dailyscheduleAddrsObject = dailyscheduleObject.getJSONObject("address");
                            Log.d("requestLeaveListData", TAG + "dailyscheduleObject" + dailyscheduleAddrsObject.toString());

                        }

                        String firstName = "", middleName = "", lastName = "", addressLine1 = "", city = "", routeName = "";


                        final  String customerid = String.valueOf(dailyscheduleObject.getInt("id"));


                            if (dailyscheduleObject.getString("firstName").equals(null) || dailyscheduleObject.getString("firstName").equalsIgnoreCase("") || dailyscheduleObject.getString("firstName").equalsIgnoreCase("null")) {

                                firstName = "";
                            } else {
                                firstName = dailyscheduleObject.getString("firstName");
                            }
                            if (dailyscheduleObject.getString("middleName").equals(null) || dailyscheduleObject.getString("middleName").equalsIgnoreCase("") || dailyscheduleObject.getString("middleName").equalsIgnoreCase("null")) {

                                middleName = "";
                            } else {
                                middleName = dailyscheduleObject.getString("middleName");
                            }
                            if (dailyscheduleObject.getString("lastName").equals(null) || dailyscheduleObject.getString("lastName").equalsIgnoreCase("") || dailyscheduleObject.getString("lastName").equalsIgnoreCase("null")) {

                                lastName = "";
                            } else {
                                lastName = dailyscheduleObject.getString("lastName");
                            }

                      

                        final String cust_name = firstName + " " + middleName + " " + lastName;
                        if(dailyscheduleObject.isNull("address")==false) {
                            if (dailyscheduleAddrsObject.getString("addressLine2").equals(null) || dailyscheduleAddrsObject.getString("addressLine2").equalsIgnoreCase("") || dailyscheduleAddrsObject.getString("addressLine2").equalsIgnoreCase("null")) {

                                addressLine1 = "";
                            } else {
                                addressLine1 = dailyscheduleAddrsObject.getString("addressLine2");
                            }
                            if (dailyscheduleAddrsObject.getString("city").equals(null) || dailyscheduleAddrsObject.getString("city").equalsIgnoreCase("") || dailyscheduleAddrsObject.getString("city").equalsIgnoreCase("null")) {

                                city = "";
                            } else {
                                if (addressLine1.equalsIgnoreCase("")) {
                                    city = dailyscheduleAddrsObject.getString("city");
                                } else {
                                   // city = "," + dailyscheduleAddrsObject.getString("city");
                                    city = dailyscheduleAddrsObject.getString("city");
                                }

                            }



                        }
                        if (dailyscheduleObject.getString("routeName").equals(null) || dailyscheduleObject.getString("routeName").equalsIgnoreCase("") || dailyscheduleObject.getString("routeName").equalsIgnoreCase("null")) {

                            routeName = "";
                        } else {
                            routeName = dailyscheduleObject.getString("routeName");
                        }

                        final String cust_address =addressLine1;
                        final String routeName_address = city+" ["+routeName+"]";


                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                admin_listing_model admin_listing_model= new admin_listing_model();
                                admin_listing_model.setCust_name(cust_name);
                                admin_listing_model.setAddrs(cust_address);
                                admin_listing_model.setRouteName(routeName_address);
                                admin_listing_model.setCustomerid(customerid);
                                empList.add(admin_listing_model);
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
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    movie_list.setVisibility(View.GONE);
                                }
                            });

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
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                movie_list.setVisibility(View.GONE);
                            }
                        });
                        Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();

                    }
                });

            }
        } catch (Exception ex) {
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            movie_list.setVisibility(View.GONE);
                        }
                    });
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

    private void   findById(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        movie_list = (RelativeLayout) findViewById(R.id.movie_list);
        cust_name = (EditText)findViewById(R.id.cust_name);
        search_btn =(Button)findViewById(R.id.search_btn);

    }

    private boolean validateForm() {
        alertDialog = new AlertDialog.Builder(admin_listing.this);
        if (cust_name.getText().toString().trim().equalsIgnoreCase("")) {
            alertDialog.setTitle("VALIDATION");
            alertDialog.setMessage("Please Enter Customer Name");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    cust_name.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        }else {
            return true;
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item1:
                // Toast.makeText(getApplicationContext(), "Item 1 Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.item2:
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                globalVariable.getSharedPreferences().edit()
                        .putString("userName",    "")
                        .putString("password","")
                        .putBoolean("is_loggedIn", false)
                        .apply();
                Intent intent = new Intent(getApplicationContext(), login_activity.class);
                startActivity(intent);
                finish();
                // Toast.makeText(getApplicationContext(), "Item 2 Selected", Toast.LENGTH_LONG).show();
                return true;
           /* case R.id.item3:
                Toast.makeText(getApplicationContext(), "Item 3 Selected", Toast.LENGTH_LONG).show();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
