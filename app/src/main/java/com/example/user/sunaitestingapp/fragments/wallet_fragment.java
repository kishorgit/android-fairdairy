package com.example.user.sunaitestingapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class wallet_fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
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
    public wallet_fragment() {
        // Required empty public constructor
    }

    public static wallet_fragment newInstance(String param1, String param2) {
        wallet_fragment fragment = new wallet_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet_fragment, container, false);
        findViewId(view);
     
        addmoneylink.setText(">");

        GlobalClass globalvariable = (GlobalClass) getActivity().getApplicationContext();
       
      
        //////////////////////listing product//////////////
        mAdapter = new wallet_adapter(empList, new wallet_adapter.MyAdapterListener() {

            @Override
            public void modifyInfoOnClick(View v, wallet_model wallet_model) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {
                   /* final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                    wallet_model.setProductQty(wallet_model.getProductQty() + 0.5);
                    mAdapter.notifyDataSetChanged();*/
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void subQuantityOnClick(View v, wallet_model wallet_model) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {
                  /*  final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                    wallet_model.setProductQty(wallet_model.getProductQty() - 0.5);
                    mAdapter.notifyDataSetChanged();*/
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }
            }

        }

        );

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 21));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
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






        return view;
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


    public void findViewId(View view) {
        addmoneylink = (TextView)view.findViewById(R.id.addmoneylink);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
