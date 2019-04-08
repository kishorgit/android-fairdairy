package com.example.user.sunaitestingapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.adapter.vacation_adapter;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyDividerItemDecoration;
import com.example.user.sunaitestingapp.common.RecyclerTouchListener;
import com.example.user.sunaitestingapp.model.StringWithTag;
import com.example.user.sunaitestingapp.model.vacation_model;

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

public class vacation_fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ImageView addvacation;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView servetxt;
 
    public ProgressDialog progressDialog = null;
    public ProgressDialog progressDialogDone = null;
    AlertDialog.Builder alertDialog, alertDialog2;
    Handler mHandler = null;
    String TAG = "vacation_fragment";
    private Model_TLI_API_Response model_TLI_api_response;

    private RecyclerView recyclerView;
    private vacation_adapter mAdapter;
    private List<vacation_model> empList = new ArrayList<>();



    public vacation_fragment() {
        // Required empty public constructor
    }


    public static vacation_fragment newInstance(String param1, String param2) {
        vacation_fragment fragment = new vacation_fragment();
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

        View view = inflater.inflate(R.layout.fragment_vacation_fragment, container, false);
        addvacation = (ImageView) view.findViewById(R.id.addvacation);
        servetxt = (TextView) view.findViewById(R.id.servetxt);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        alertDialog2 = new AlertDialog.Builder(getActivity());
        mHandler = new Handler();
        progressDialog = new ProgressDialog(getActivity());
      
        addvacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                globalVariable.globalstartdate ="";
                globalVariable.globalenddate ="";
                globalVariable.vacationid ="";

                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new addvacation_fragment());
                fragmentTransaction.commit();
            }
        });
        
        ///////////////////////listing///////////////

        mAdapter = new vacation_adapter(empList, new vacation_adapter.MyAdapterListener() {

            @Override
            public void modifyInfoOnClick(View v, vacation_model vacation_model) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {
                   /* final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                    vacation_model.setProductQty(vacation_model.getProductQty() + 0.5);
                    mAdapter.notifyDataSetChanged();*/
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void subQuantityOnClick(View v, vacation_model vacation_model) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {
                  /*  final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                    vacation_model.setProductQty(vacation_model.getProductQty() - 0.5);
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
                final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                final vacation_model vacation_model = empList.get(position);
                globalVariable.globalstartdate =vacation_model.getStartdate();
                globalVariable.globalenddate =vacation_model.getEnddate();
                globalVariable.vacationid =vacation_model.getVacationid();

                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new addvacation_fragment());
                fragmentTransaction.commit();
            }

            @Override
            public void onLongClick(View view, final int position) {

            }

        }));

        ////////////////////listing product///////////////////////////////////////

        new MyAsyncTask().execute();
        //   getVacation();
        recyclerView.setAdapter(mAdapter);
        Log.d("add_vacation--", TAG+"--empListSize--" +empList.size());
        if(empList.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            servetxt.setVisibility(View.INVISIBLE);
        }
        else {
            recyclerView.setVisibility(View.INVISIBLE);
            servetxt.setVisibility(View.VISIBLE);
        }



        return view;
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

    public void getVacation() {

        String result = "";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
        api_response = AppUtills.callWebGetService(getActivity().getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/" + globalVariable.customerid + "/vacation");

        try {
            Log.d("requestLeaveListData", TAG + "api_response.response_code" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK)

            {
                result = api_response.data;
                Log.d("getVacation", TAG + "result" + result);
                JSONArray jsonArray = new JSONArray(result);
                if(jsonArray.length()>0){

                    for (int arrayloop = 0; arrayloop < jsonArray.length(); arrayloop++)
                    {

                        final  JSONObject json_object_root =jsonArray.getJSONObject(arrayloop);

                        Log.d("getVacation", TAG + "start date" + json_object_root.getString("startDate"));
                        Log.d("getVacation", TAG + "end date" + json_object_root.getString("endDate"));


                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                                vacation_model vacation_model = new vacation_model();

                                try {
                                    if (json_object_root.getString("startDate").equals(null)) {

                                    } else {

                                        Date d1 = null;
                                        try {
                                            d1 = sdformat.parse(json_object_root.getString("startDate"));

                                            vacation_model.setStartdate(sdformat.format(d1));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (json_object_root.getString("endDate").equals(null)) {

                                    } else {
                                        Date d2 = null;
                                        try {
                                            d2 = sdformat.parse(json_object_root.getString("endDate"));
                                            vacation_model.setEnddate(sdformat.format(d2));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        Log.d("add_vacation--", TAG+"--endDate--" +sdformat.format(d2));


                                    }

                                    empList.add(vacation_model);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                try {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                } catch (Exception ex) {
                                    Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                                }

                            }
                        });

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

                        //  Toast.makeText(getActivity().getApplicationContext(), "No Record Found From Server", Toast.LENGTH_LONG).show();
                        alertDialog2.setMessage("No Response From Server");
                        alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }
                });

            }
        } catch (Exception ex) {
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
                    //  Toast.makeText(getActivity().getApplicationContext(), "No Record Found From Server", Toast.LENGTH_LONG).show();

                }
            });
            Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
        }


    }

    private class MyAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


            getVacation();
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
