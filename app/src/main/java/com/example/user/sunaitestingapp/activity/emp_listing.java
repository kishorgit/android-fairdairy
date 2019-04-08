package com.example.user.sunaitestingapp.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyDividerItemDecoration;
import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.common.MyServiceListener;
import com.example.user.sunaitestingapp.common.RecyclerTouchListener;
import com.example.user.sunaitestingapp.adapter.emp_listing_adapter;
import com.example.user.sunaitestingapp.common.Service_Call;
import com.example.user.sunaitestingapp.model.emp_listing_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class emp_listing extends AppCompatActivity {
    private RecyclerView recyclerView;
    private emp_listing_adapter mAdapter;
    private List<emp_listing_model> empList = new ArrayList<>();
    private Model_TLI_API_Response model_TLI_api_response;
    String TAG = "emp_listing";
    public ProgressDialog progressDialog = null;
    public ProgressDialog progressDialogDone = null;
    AlertDialog.Builder alertDialog;
    Handler mHandler = null;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    Toolbar toolbar;
    TextView tv_toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_listing);
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
        tv_toolbar = (TextView) findViewById(R.id.tv_toolbar);

        alertDialog = new AlertDialog.Builder(emp_listing.this);
        mHandler = new Handler();
        progressDialog = new ProgressDialog(emp_listing.this);
        progressDialogDone = new ProgressDialog(emp_listing.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        actionModeCallback = new ActionModeCallback();
        mAdapter = new emp_listing_adapter(empList, new emp_listing_adapter.MyAdapterListener() {

            @Override
            public void modifyInfoOnClick(View v, emp_listing_model emp_listing_model) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    globalVariable.productid = emp_listing_model.getProductid();
                    globalVariable.customerid = emp_listing_model.getCustomerid();
                    globalVariable.customername = emp_listing_model.getCust_name();
                    globalVariable.customeraddrs = emp_listing_model.getAddrs();
                    globalVariable.scheduledelivery = emp_listing_model.getScheduleDelivery();
                  /*  globalVariable.globalempList=empList;
                    globalVariable.globalemp_listing_model=emp_listing_model;
                    globalVariable.globalempList.remove(  globalVariable.globalemp_listing_model);
                    mAdapter.notifyDataSetChanged();*/
                    Intent intent = new Intent(getApplicationContext(), delivery_confirmation.class);
                   startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void okbtnViewOnClick(View v, emp_listing_model emp_listing_model, int position) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {

                new MyAsyncTaskDone(position).execute();
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
       //     recyclerView.getChildAt(position).setSelected(true);
            enableActionMode(position);


              /*  PopupMenu popup = new PopupMenu(emp_listing.this, recyclerView);
                //Inflating the Popup using xml file
             popup.getMenuInflater().inflate(R.menu.menu_context, popup.getMenu());
            //    popup.inflate(R.menu.menu_context);
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(getApplicationContext(), record_payment.class);
                        startActivity(intent);

                        return true;
                    }
                });

                popup.show();//showing popup menu*/
            }

        }));

    }

    private void send_to_server(int position) {

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        final emp_listing_model emp_listing_model = empList.get(position);
        Log.d("save_form--", TAG+"--productid--" + emp_listing_model.getProductid());
        Log.d("save_form--", TAG+"--Customerid--" + emp_listing_model.getCustomerid());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
      //  Log.d("save_form--", TAG+"--actual_delivery--" +actual_delivery.getText().toString().trim());
        //////////////////Call Api/////////////////////////////////actualDelivery=1.7
        JSONArray jsonArray_root  = new JSONArray();
        JSONObject json_object_root  = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            json_object_root.put("productId", emp_listing_model.getProductid());
            json_object_root.put("actualDelivery",emp_listing_model.getActualDelivery());
            jsonArray_root.put(json_object_root);
            jsonObject.put("root_array",jsonArray_root);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Service_Call(getApplicationContext(), "/schedule/"+globalVariable.userid+"/"+currentdate+"/"+emp_listing_model.getCustomerid(),"PUT", jsonObject, true, new MyServiceListener() {
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

                        alertDialog.setMessage("Delivery recorded successfully.");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                              //  empList.remove(emp_listing_model);
                               emp_listing_model.setDonestatus("DELIVERED");
                                mAdapter.notifyDataSetChanged();
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

                        alertDialog.setMessage("Something goes wrong.Please try agian!");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

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
                 //   Toast.makeText(getApplicationContext(), "Something goes wrong please try again!", Toast.LENGTH_LONG).show();
                    Log.d("test--",TAG+"--exception in Service_Call1--"+ex);
                    alertDialog.setMessage("Something goes wrong.Please try agian!");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
                alertDialog.show();
            }
        });

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
        api_response = AppUtills.callWebGetService(getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/schedule/" + globalVariable.userid + "/"+currentdate);
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

                        JSONObject dailyscheduleUserObject =null;
                        JSONObject dailyscheduleProductObject =null;
                        JSONObject dailyscheduleRoleObject =null;
                        JSONObject dailyscheduleAddrsObject =null;
                        Log.d("requestLeaveListData", TAG + "MydailyscheduleObject" + dailyscheduleObject.toString());


                        Log.d("requestLeaveListData", TAG + "customerid" + globalVariable.customerid);
                        if(dailyscheduleObject.isNull("user")==false) {
                             dailyscheduleUserObject = dailyscheduleObject.getJSONObject("user");
                            Log.d("requestLeaveListData", TAG + "dailyscheduleUserObject" + dailyscheduleUserObject.toString());

                        }
                        final  String customerid = String.valueOf(dailyscheduleUserObject.getInt("id"));
                        if(dailyscheduleObject.isNull("product")==false) {
                             dailyscheduleProductObject = dailyscheduleObject.getJSONObject("product");
                            Log.d("requestLeaveListData", TAG + "dailyscheduleUserObject" + dailyscheduleProductObject.toString());

                        }

                        final int productid = dailyscheduleProductObject.getInt("id");

                        if(dailyscheduleUserObject.isNull("role")==false) {
                             dailyscheduleRoleObject = dailyscheduleUserObject.getJSONObject("role");
                            Log.d("requestLeaveListData", TAG + "dailyscheduleUserObject" + dailyscheduleRoleObject.toString());

                        }

                        if(dailyscheduleUserObject.isNull("address")==false) {
                             dailyscheduleAddrsObject = dailyscheduleUserObject.getJSONObject("address");
                            Log.d("requestLeaveListData", TAG + "dailyscheduleUserObject" + dailyscheduleAddrsObject.toString());

                        }

                        String firstName = "", middleName = "", lastName = "", addressLine1 = "", city = "";
                        Double quantity = 0.0, actualDeliver = 0.0;

                        if(dailyscheduleObject.isNull("user")==false) {
                            if (dailyscheduleUserObject.getString("firstName").equals(null) || dailyscheduleUserObject.getString("firstName").equalsIgnoreCase("") || dailyscheduleUserObject.getString("firstName").equalsIgnoreCase("null")) {

                                firstName = "";
                            } else {
                                firstName = dailyscheduleUserObject.getString("firstName");
                            }
                            if (dailyscheduleUserObject.getString("middleName").equals(null) || dailyscheduleUserObject.getString("middleName").equalsIgnoreCase("") || dailyscheduleUserObject.getString("middleName").equalsIgnoreCase("null")) {

                                middleName = "";
                            } else {
                                middleName = dailyscheduleUserObject.getString("middleName");
                            }
                            if (dailyscheduleUserObject.getString("lastName").equals(null) || dailyscheduleUserObject.getString("lastName").equalsIgnoreCase("") || dailyscheduleUserObject.getString("lastName").equalsIgnoreCase("null")) {

                                lastName = "";
                            } else {
                                lastName = dailyscheduleUserObject.getString("lastName");
                            }

                        }

                        final String cust_name = firstName + " " + middleName + " " + lastName;
                        if(dailyscheduleUserObject.isNull("address")==false) {
                            if (dailyscheduleAddrsObject.getString("addressLine1").equals(null) || dailyscheduleAddrsObject.getString("addressLine1").equalsIgnoreCase("") || dailyscheduleAddrsObject.getString("addressLine1").equalsIgnoreCase("null")) {

                                addressLine1 = "";
                            } else {
                                addressLine1 = dailyscheduleAddrsObject.getString("addressLine1");
                            }
                            if (dailyscheduleAddrsObject.getString("city").equals(null) || dailyscheduleAddrsObject.getString("city").equalsIgnoreCase("") || dailyscheduleAddrsObject.getString("city").equalsIgnoreCase("null")) {

                                city = "";
                            } else {
                                if (addressLine1.equalsIgnoreCase("")) {
                                    city = dailyscheduleAddrsObject.getString("city");
                                } else {
                                    city = "," + dailyscheduleAddrsObject.getString("city");
                                }

                            }
                        }
                        final String cust_address = addressLine1 + city;
                        final String deliveryInstructions = dailyscheduleObject.getString("deliveryInstructions");
                        final String specialInstructions = dailyscheduleObject.getString("specialInstructions");
                        Log.d("requestLeaveListData", TAG + "deliveryInstructions" + deliveryInstructions);
                        Log.d("requestLeaveListData", TAG + "specialInstructions" + specialInstructions);
                        quantity = dailyscheduleObject.getDouble("quantity");
                        actualDeliver = dailyscheduleObject.getDouble("actualDelivery");

                        final Double actualDelivery =  dailyscheduleObject.getDouble("actualDelivery");
                        totalactualDeliver = totalactualDeliver +actualDelivery;
                        Log.d("requestLeaveListData", TAG + "totalactualDeliver" + totalactualDeliver);
                        final String totalQty = String.valueOf(quantity) + "/" + String.valueOf(actualDeliver);
                        final Double scheduleDelivery = dailyscheduleObject.getDouble("quantity");
                         final int       createdBy = dailyscheduleUserObject.getInt("createdBy");
                        final String deliveryStatus =dailyscheduleObject.getString("deliveryStatus");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                emp_listing_model emp_listing_model = new emp_listing_model();
                                emp_listing_model.setCust_name(cust_name);
                                emp_listing_model.setAddrs(cust_address);
                                emp_listing_model.setDeliveryinstruction(deliveryInstructions);
                                emp_listing_model.setSpecialinstruction(specialInstructions);
                                emp_listing_model.setQty(totalQty);
                                emp_listing_model.setCustomerid(customerid);
                                emp_listing_model.setProductid(productid);
                                emp_listing_model.setActualDelivery(actualDelivery);
                                emp_listing_model.setScheduleDelivery(scheduleDelivery);
                                emp_listing_model.setCreatedBy(createdBy);
                                emp_listing_model.setDonestatus(deliveryStatus);
                             //   if((Double.compare(emp_listing_model.getActualDelivery(),0.0) == 1)&&(Double.compare(emp_listing_model.getActualDelivery(),0.0) == 1))
                                {
                                    empList.add(emp_listing_model);
                                }


                             /*   emp_listing_model emp_listing_model1 = new emp_listing_model();
                                emp_listing_model1.setCust_name(cust_name);
                                emp_listing_model1.setAddrs(cust_address);
                                emp_listing_model1.setInstruction("Instruction: " + cust_instruct);
                                emp_listing_model1.setQty(totalQty);

                                emp_listing_model1.setCustomerid(customerid);
                                emp_listing_model1.setProductid(productid);
                                emp_listing_model1.setActualDelivery(2.5);
                                emp_listing_model1.setScheduleDelivery(2.1);
                                emp_listing_model1.setCreatedBy(createdBy);*/

                              //  empList.add(emp_listing_model1);
                             /*   empList.add(emp_listing_model);
                                empList.add(emp_listing_model);
                                empList.add(emp_listing_model);
                                empList.add(emp_listing_model);
                                empList.add(emp_listing_model);
                                empList.add(emp_listing_model);
                                empList.add(emp_listing_model);*/
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    tv_toolbar.setText("Delivery Schedule- "+currentdate+"   ["+totalactualDeliver+"]");
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
    private class MyAsyncTaskDone extends AsyncTask {
        int position;
        public MyAsyncTaskDone(int position) {
            this.position = position;

        }

        @Override
        protected Object doInBackground(Object[] objects) {
               send_to_server(position);


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

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            Log.d("API123", "onCreateActionMode");

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.d("API123", "here");
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            switch (item.getItemId()) {


                case R.id.makepayment:
                    if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                        Log.d("API123","toolbaritemposition"+ toolbar.getTitle().toString().trim());
                    emp_listing_model emp_listing_model = empList.get(Integer.parseInt(toolbar.getTitle().toString().trim()));
                    globalVariable.productid = emp_listing_model.getProductid();
                    globalVariable.createdBy = emp_listing_model.getCreatedBy();
                    globalVariable.customerid  = emp_listing_model.getCustomerid();
                    globalVariable.customername =  emp_listing_model.getCust_name();
                    globalVariable.customeraddrs = emp_listing_model.getAddrs();

                    Intent intent = new Intent(getApplicationContext(), record_payment.class);
                    startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                    }

                    mode.finish();
                    return true;

                /*    case R.id.action_color:
                    updateColoredRows();
                    mode.finish();
                    return true;

                case R.id.action_select_all:
                    selectAll();
                    return true;

                case R.id.action_refresh:
                    populateDataAndSetAdapter();
                    mode.finish();
                    return true;*/

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            toolbar.setTitle("");
            toolbar.setVisibility(View.VISIBLE);tv_toolbar.setVisibility(View.VISIBLE);
            actionMode = null;
        }
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        Log.d("API123", "toggleSelectionposition"+position);
//        recyclerView.getChildAt(position+1).setBackgroundResource(R.color.view_light_gray);
        int count = mAdapter.getSelectedItemCount();


        Log.d("API123", "toggleSelection");
        if (count == 0) {
    //  recyclerView.getChildAt(position).setBackgroundResource(R.color.rowColor);
            toolbar.setTitle("");
            toolbar.setVisibility(View.VISIBLE);tv_toolbar.setVisibility(View.VISIBLE);
            actionMode.finish();
            actionMode = null;
        } else {
           // actionMode.setTitle(String.valueOf(position));
            toolbar.setTitle(String.valueOf(position));
            actionMode.invalidate();

            Log.d("API123", "actionModeelse" + String.valueOf(position));
            toolbar.setVisibility(View.GONE);tv_toolbar.setVisibility(View.GONE);
           //  recyclerView.getChildAt(position).setBackgroundResource(R.color.view_gray_color);
        }
    }

}
