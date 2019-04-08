package com.example.user.sunaitestingapp.fragments;

import android.app.DatePickerDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.activity.registration_info;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyServiceListener;
import com.example.user.sunaitestingapp.common.Service_Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class addvacation_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText start_date,end_date;
    Button donevacation,endvacation;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private OnFragmentInteractionListener mListener;
    public ProgressDialog progressDialog = null;
    public ProgressDialog progressDialogDone = null;
    AlertDialog.Builder alertDialog,alertDialog2;
    Handler mHandler = null;
    String TAG = "addvacation_fragment";
    private Model_TLI_API_Response model_TLI_api_response;
    private int mYear, mMonth, mDay;
    public addvacation_fragment() {
        // Required empty public constructor
    }


    public static addvacation_fragment newInstance(String param1, String param2) {
        addvacation_fragment fragment = new addvacation_fragment();
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
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_addvacation_fragment, container, false);
        donevacation = (Button)view.findViewById(R.id.donevacation);
        endvacation = (Button)view.findViewById(R.id.endvacation);
        start_date = (EditText)view.findViewById(R.id.start_date);
        end_date = (EditText)view.findViewById(R.id.end_date);
        alertDialog = new AlertDialog.Builder(getContext());
        alertDialog2 = new AlertDialog.Builder(getContext());
        mHandler = new Handler();
        progressDialog = new ProgressDialog(getContext());
        progressDialogDone = new ProgressDialog(getContext());
        start_date.setText(globalVariable.globalstartdate);
        end_date.setText(globalVariable.globalenddate);
        if(globalVariable.vacationid.equalsIgnoreCase("")){
            endvacation.setVisibility(View.GONE);
        }

        donevacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {
                    if (validateForm())
                    {
                        new MyAsyncTask().execute();
                    }
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }





            }
        });

        endvacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {
                    if (validateForm())
                    {
                        new MyAsyncTaskDone().execute();
                    }
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }





            }
        });


        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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

                                }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        return view;
    }


    public void add_vacation(){



        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();

        //  Log.d("save_form--", TAG+"--actual_delivery--" +actual_delivery.getText().toString().trim());
        Log.d("save_form--", TAG+"--start_date--" +start_date.getText().toString().trim());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        //////////////////Call Api/////////////////////////////////actualDelivery=1.7

        JSONObject json_object_root = new JSONObject();


        try {
            GlobalClass gv = (GlobalClass)getActivity().getApplicationContext();
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d1 = sdformat.parse(start_date.getText().toString().trim());
                Date d2 = sdformat.parse(end_date.getText().toString().trim());
                Log.d("add_vacation--", TAG+"--start_date--" +sdformat.format(d1));
                Log.d("add_vacation--", TAG+"--endDate--" +sdformat.format(d2));

                json_object_root.put("startDate", sdformat.format(d1));
                json_object_root.put("endDate",sdformat.format(d2));
            } catch (ParseException e) {
                e.printStackTrace();
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Service_Call(getActivity().getApplicationContext(), "/customer/"+globalVariable.customerid+"/vacation","POST", json_object_root, true, new MyServiceListener() {
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
                      /*  alertDialog2.setMessage("Vacation Added Successfully.");
                        alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                       */         fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container,new vacation_fragment());
                                fragmentTransaction.commit();
                      /*      }
                        });
*/

                        try{
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
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
                            if (progressDialog == null ||  progressDialog.isShowing()) {
                                progressDialog.dismiss();
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

                                progressDialog.dismiss();
                            }
                        });
                        alertDialog2.show();
                    }


                }
                catch (Exception ex)
                {
                    try{
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
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

                            progressDialog.dismiss();
                        }
                    });
                    alertDialog2.show();
                }

            }
        });



    }

    public void  end_vacation(){

        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        //////////////////Call Api/////////////////////////////////actualDelivery=1.7

        JSONArray jsonArray_root  = new JSONArray();
        JSONObject json_object_root  = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            json_object_root.put("vacationId",globalVariable.vacationid);

            jsonArray_root.put(json_object_root);
            jsonObject.put("root_array",jsonArray_root);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Service_Call(getActivity().getApplicationContext(), "/customer/"+globalVariable.customerid+"/vacation/"+globalVariable.vacationid,"PUT", jsonObject, true, new MyServiceListener() {
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
                     /*   alertDialog2.setMessage("Vacation Ended Successfully.");
                        alertDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {*/

                        fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,new vacation_fragment());
                        fragmentTransaction.commit();
                       /*     }
                        });
                        alertDialog2.show();*/
                        try{
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
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
                            if (progressDialog == null ||  progressDialog.isShowing()) {
                                progressDialog.dismiss();
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

                                progressDialog.dismiss();
                            }
                        });
                        alertDialog2.show();
                    }


                }
                catch (Exception ex)
                {
                    try{
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
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

                            progressDialog.dismiss();
                        }
                    });
                    alertDialog2.show();
                }

            }
        });






    }

    private boolean validateForm(){

        alertDialog = new AlertDialog.Builder(getActivity());
        if (start_date.getText().toString().trim().equalsIgnoreCase("")) {
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



    private class MyAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


            add_vacation();
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

        public MyAsyncTaskDone() {


        }

        @Override
        protected Object doInBackground(Object[] objects) {
          end_vacation();


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
