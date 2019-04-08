package com.example.user.sunaitestingapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.nfc.TagLostException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.activity.change_schedule;
import com.example.user.sunaitestingapp.adapter.change_schedule_adapter;
import com.example.user.sunaitestingapp.adapter.delivery_change_adapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class delivery_change_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public String TAG = "delivery_change_fragment";

    private OnFragmentInteractionListener mListener;
    Button updatebtn;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView currnetdate;
    private RecyclerView recyclerView;
    private delivery_change_adapter mAdapter;
    private List<change_schedule_model> empList = new ArrayList<>();
    private List<change_schedule_model> subsprodList = new ArrayList<>();
    Handler mHandler = null;
    private Model_TLI_API_Response model_TLI_api_response;

    public ProgressDialog progressDialog = null;
    public ProgressDialog progressDialogDone = null;
    AlertDialog.Builder alertDialog, alertDialog2;

    public delivery_change_fragment() {
        // Required empty public constructor
    }


    public static delivery_change_fragment newInstance(String param1, String param2) {
        delivery_change_fragment fragment = new delivery_change_fragment();
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
        View view = inflater.inflate(R.layout.fragment_delivery_change_fragment, container, false);
        GlobalClass globalvariable = (GlobalClass) getActivity().getApplicationContext();
        findViewId(view);
        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog2 = new AlertDialog.Builder(getActivity());
        mHandler = new Handler();
        progressDialog = new ProgressDialog(getActivity());
        progressDialogDone = new ProgressDialog(getActivity());
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {


                        new MyAsyncTaskSaveForm().execute();


                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }

            }
        });
        currnetdate.setText(globalvariable.current_date);


        //////////////////////listing product//////////////
        mAdapter = new delivery_change_adapter(empList, getActivity().getApplicationContext(),new delivery_change_adapter.MyAdapterListener() {

            @Override
            public void modifyInfoOnClick(View v, change_schedule_model change_schedule_model) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {
                    final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                    change_schedule_model.setProductQty(change_schedule_model.getProductQty() + 0.5);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void subQuantityOnClick(View v, change_schedule_model change_schedule_model) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {
                    final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                    if((change_schedule_model.getProductQty() - 0.5)>0.0) {
                        change_schedule_model.setProductQty(change_schedule_model.getProductQty() - 0.5);
                    }
                    else {
                        change_schedule_model.setProductQty(0.0);


                    }
                    mAdapter.notifyDataSetChanged();
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


       new MyAsyncTask().execute();

        recyclerView.setAdapter(mAdapter);


        return view;
    }


    private void requestLeaveListData() {

        String result = "";
        int arrayloop = 0;
        empList.clear();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        Double   totalactualDeliver=0.0;
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
        api_response = AppUtills.callWebGetService(getActivity().getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/"+globalVariable.customerid+"/subscription");
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
                        JSONObject productObject = dailyscheduleObject.getJSONObject("product");

                        final int        product_id = productObject.getInt("id");
                        final      String product_name =productObject.getString("name");
                        final  Double pricePerUnit =  productObject.getDouble("pricePerUnit");
                        final   Double productQty =dailyscheduleObject.getDouble("productQty");
                        Log.d("group", TAG + "groupproduct_id" + product_id);
                        Log.d("group", TAG + "groupproductQty" + productQty);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                change_schedule_model change_schedule_model= new change_schedule_model();
                                change_schedule_model.setProduct_name(product_name);
                                change_schedule_model.setProduct_id(product_id);
                                change_schedule_model.setPricePerUnit(pricePerUnit);
                                change_schedule_model.setProductQty(productQty);
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
                            Toast.makeText(getActivity().getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();

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
                        Toast.makeText(getActivity().getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();

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
  /*  private Double getProductQty(int product_id) {

        String result = "";
        int arrayloop = 0;
        subsprodList.clear();

        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();

        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
        api_response = AppUtills.callWebGetService(getActivity().getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/"+globalVariable.customerid+"/subscription");
        try {
            Log.d("getProductQty", TAG + "api_response.response_code" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK) {
                result = api_response.data;
                JSONArray dailyscheduleArray = new JSONArray(result);
                Log.d("getProductQty", TAG + "dailyscheduleArrayString" + dailyscheduleArray.toString());
                Log.d("getProductQty", TAG + "dailyscheduleArray.length()" + dailyscheduleArray.length());
                if (dailyscheduleArray.length() > 0) {
                    for (arrayloop = 0; arrayloop < dailyscheduleArray.length(); arrayloop++) {

                        JSONObject dailyscheduleObject = dailyscheduleArray.getJSONObject(arrayloop);
                        JSONObject dailysproductObject = dailyscheduleObject.getJSONObject("product");
                        Log.d("local", TAG + "localproduct_id" + product_id);
                        Log.d("local", TAG + "localobjectproduct_id" + dailysproductObject.getInt("id"));
                        if(product_id ==dailysproductObject.getInt("id"))

                        {
                        Double productQty =dailyscheduleObject.getDouble("productQty");
                        return productQty;
                        }


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


                        }
                    });
                    return 0.0;
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
        return 0.0;
    }*/

    public void findViewId(View view) {
        updatebtn = (Button) view.findViewById(R.id.updatebtn);
        currnetdate = (TextView) view.findViewById(R.id.currnetdate);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }


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
        void onFragmentInteraction(Uri uri);
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

    private void save_form() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            }
        });

        final GlobalClass globalVariable = (GlobalClass)getActivity(). getApplicationContext();

        //  Log.d("save_form--", TAG+"--actual_delivery--" +actual_delivery.getText().toString().trim());

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
                json_object_root.put("scheduleChangeDate",globalVariable.scheduleChangeDate);

                jsonArray_root.put(json_object_root);
            }


            jsonObject.put("root_array",jsonArray_root);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Service_Call(getActivity().getApplicationContext(), "/customer/"+globalVariable.customerid+"/changeschedule","PUT", jsonObject, true, new MyServiceListener() {
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
                                fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, new backhome_fragment());
                                fragmentTransaction.commit();
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


   /* private class MyAsyncTaskProductId extends AsyncTask {
        int product_id;

        public MyAsyncTaskProductId( int product_id) {
            this.product_id = product_id;

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            getProductQty(product_id);


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
    }*/
}
