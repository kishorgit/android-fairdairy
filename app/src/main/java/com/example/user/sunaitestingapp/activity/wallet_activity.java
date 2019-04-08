package com.example.user.sunaitestingapp.activity;

import android.app.ProgressDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.adapter.wallet_adapter;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyDividerItemDecoration;
import com.example.user.sunaitestingapp.common.RecyclerTouchListener;
import com.example.user.sunaitestingapp.model.wallet_model;

import java.util.ArrayList;
import java.util.List;

public class wallet_activity extends AppCompatActivity {
    Toolbar toolbar;
    TextView addmoneylink;
    private RecyclerView recyclerView;
    private wallet_adapter mAdapter;
    private List<wallet_model> empList = new ArrayList<>();
    Handler mHandler = null;
    private Model_TLI_API_Response model_TLI_api_response;

    public ProgressDialog progressDialog = null;
    public ProgressDialog progressDialogDone = null;
    AlertDialog.Builder alertDialog, alertDialog2;
    public String TAG = "wallet_fragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_activity);
        findViewId();
        toolbar.setTitle(Html.fromHtml("<font  >Wallet</font>"+"<font color='#FFFFFF'> </font>"));
        toolbar.setTitleTextAppearance(getApplicationContext(),R.style.titletextStyle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        addmoneylink.setText(">");

        GlobalClass globalvariable = (GlobalClass) getApplicationContext();


        //////////////////////listing product//////////////
        mAdapter = new wallet_adapter(empList, new wallet_adapter.MyAdapterListener() {

            @Override
            public void modifyInfoOnClick(View v, wallet_model wallet_model) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                   /* final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                    wallet_model.setProductQty(wallet_model.getProductQty() + 0.5);
                    mAdapter.notifyDataSetChanged();*/
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void subQuantityOnClick(View v, wallet_model wallet_model) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                  /*  final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                    wallet_model.setProductQty(wallet_model.getProductQty() - 0.5);
                    mAdapter.notifyDataSetChanged();*/
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
                //   recyclerView.getChildAt(position).setSelected(false);
            }

            @Override
            public void onLongClick(View view, final int position) {

            }

        }));

        ////////////////////listing product///////////////////////////////////////


        requestLeaveListData();

        recyclerView.setAdapter(mAdapter);




    }
    private void requestLeaveListData() {

        wallet_model wallet_model = new wallet_model();
        wallet_model.setWalletdate("31-12-2018");
        wallet_model.setWalletdesc("Wallet Deposit");
        wallet_model.setMoney("₹ 200.00");

        empList.add(wallet_model);
        empList.add(wallet_model);
        empList.add(wallet_model);
        empList.add(wallet_model);
        empList.add(wallet_model);
        empList.add(wallet_model);
        empList.add(wallet_model);
        empList.add(wallet_model);
        empList.add(wallet_model);

     /*   String result = "";
        int arrayloop = 0;
        empList.clear();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        Double   totalactualDeliver=0.0;
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
        api_response = AppUtills.callWebGetService(getActivity().getApplicationContext(), "http://ec2-13-233-40-31.ap-south-1.compute.amazonaws.com:8080/fd/api/product/subscribe/"+globalVariable.customerid);
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


                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wallet_model wallet_model= new wallet_model();
                                wallet_model.setProduct_name(product_name);
                                wallet_model.setProduct_id(product_id);
                                wallet_model.setPricePerUnit(pricePerUnit);
                                wallet_model.setProductQty(productQty);
                                empList.add(wallet_model);

                              *//*  wallet_model wallet_model1= new wallet_model();
                                wallet_model1.setProduct_name(product_name);
                                wallet_model1.setProduct_id(product_id);
                                wallet_model1.setPricePerUnit(pricePerUnit);
                                wallet_model1.setProductQty(productQty);
                                empList.add(wallet_model1);

                                wallet_model wallet_model2= new wallet_model();
                                wallet_model2.setProduct_name(product_name);
                                wallet_model2.setProduct_id(product_id);
                                wallet_model2.setPricePerUnit(pricePerUnit);
                                wallet_model2.setProductQty(productQty);
                                empList.add(wallet_model2);
                                wallet_model wallet_model3= new wallet_model();
                                wallet_model3.setProduct_name(product_name);
                                wallet_model3.setProduct_id(product_id);
                                wallet_model3.setPricePerUnit(pricePerUnit);
                                wallet_model3.setProductQty(productQty);
                                empList.add(wallet_model3);
                                wallet_model wallet_model4= new wallet_model();
                                wallet_model4.setProduct_name(product_name);
                                wallet_model4.setProduct_id(product_id);
                                wallet_model4.setPricePerUnit(pricePerUnit);
                                wallet_model4.setProductQty(productQty);
                                empList.add(wallet_model4);*//*


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
                            Toast.makeText(getActivity().getApplicationContext()(), "No Data Found", Toast.LENGTH_LONG).show();

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
                        Toast.makeText(getActivity().getApplicationContext()(), "No Data Found", Toast.LENGTH_LONG).show();

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
*/

    }
    private void findViewId() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addmoneylink = (TextView)findViewById(R.id.addmoneylink);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
}
