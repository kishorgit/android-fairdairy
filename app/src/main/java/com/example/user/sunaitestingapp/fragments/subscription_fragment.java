package com.example.user.sunaitestingapp.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.adapter.spinnercustomadapter;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.common.MyServiceListener;
import com.example.user.sunaitestingapp.common.Service_Call;
import com.example.user.sunaitestingapp.model.StringWithTag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;


public class subscription_fragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button save,submit;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private OnFragmentInteractionListener mListener;
    RadioButton daily, alternate_days1, week_days, select_days;
    TextView start_date,end_date;
    LinearLayout daysContainer;
    CircleImageView add_btn,sub_btn;
    TextView product_quantity;
    /*  String[] productNames={"Cow Milk","Cow Milk","Cow Milk","Cow Milk","Cow Milk","Cow Milk"};
      int productimages[] = {R.drawable.circle_buff, R.drawable.circle_buff, R.drawable.circle_buff, R.drawable.circle_buff, R.drawable.circle_buff, R.drawable.circle_buff};
   */

    Spinner spinproduct_info;
    String TAG = "subscription_fragment";
    public ProgressDialog progressDialog = null;
    AlertDialog.Builder alertDialog;
    Handler mHandler = null;
    private Model_TLI_API_Response model_TLI_api_response;
    ArrayList<String> namelist = new ArrayList<String>();
    ArrayList<String> imagelist = new ArrayList<String>();
    ArrayList<String> pricelist = new ArrayList<String>();
    ArrayList<String> idlist = new ArrayList<String>();
    JSONArray jsonArray = new JSONArray();
    String product_name,product_id;
    String frequency="";
    private int mYear, mMonth, mDay;
    public subscription_fragment() {
        // Required empty public constructor
    }

    public static subscription_fragment newInstance(String param1, String param2) {
        subscription_fragment fragment = new subscription_fragment();
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
        View view = inflater.inflate(R.layout.fragment_subscription_fragment, container, false);
        findViewId(view);
        alertDialog = new AlertDialog.Builder(getActivity());
        mHandler = new Handler();
        progressDialog = new ProgressDialog(getActivity());
        new MyAsyncTask().execute();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    save_form();
                    Log.d(TAG, "save_form: jsonArray" + jsonArray.toString());
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getActivity().getApplicationContext())) {
                    if (validateForm())
                    {
                        new MyAsyncTaskForm().execute();
                    }
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }



            }
        });

        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alternate_days1.setChecked(false);
                select_days.setChecked(false);
                week_days.setChecked(false);
                daysContainer.setVisibility(View.GONE);
                frequency = "DAILY";
            }
        });
        alternate_days1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daily.setChecked(false);
                select_days.setChecked(false);
                week_days.setChecked(false);
                daysContainer.setVisibility(View.GONE);
                frequency = "ALTERNATE_DAYS";
            }
        });
        week_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daily.setChecked(false);
                select_days.setChecked(false);
                alternate_days1.setChecked(false);
                daysContainer.setVisibility(View.GONE);
                frequency = "WEEK_DAYS";
            }
        });
        select_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daily.setChecked(false);
                week_days.setChecked(false);
                alternate_days1.setChecked(false);
                daysContainer.setVisibility(View.VISIBLE);
                frequency = "SELECT_DAYS";
            }
        });
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_quantity.setText(String.valueOf(Double.valueOf(product_quantity.getText().toString())+0.5));
            }
        });
        sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((Double.valueOf(product_quantity.getText().toString())-0.5) > 0.0) {
                    product_quantity.setText(String.valueOf(Double.valueOf(product_quantity.getText().toString()) - 0.5));
                }
                else {
                    product_quantity.setText("0.0");

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
                                end_date.setTextSize(12);
                                end_date.setGravity(Gravity.CENTER_HORIZONTAL);
                                end_date.setTextColor(getResources().getColor(R.color.black_color));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });



        return view;
    }

    private void findViewId(View view) {
        save = (Button) view.findViewById(R.id.save);
        submit  = (Button) view.findViewById(R.id.submit);
        daily = (RadioButton) view.findViewById(R.id.daily);
        alternate_days1 = (RadioButton) view.findViewById(R.id.alternate_days1);
        week_days = (RadioButton) view.findViewById(R.id.week_days);
        select_days = (RadioButton) view.findViewById(R.id.select_days);
        daysContainer = (LinearLayout) view.findViewById(R.id.daysContainer);
        spinproduct_info = (Spinner) view.findViewById(R.id.spinproduct_info);
        spinproduct_info.setOnItemSelectedListener(this);
        start_date = (TextView) view.findViewById(R.id.start_date);
        end_date = (TextView) view.findViewById(R.id.end_date);
        add_btn = (CircleImageView) view.findViewById(R.id.add_btn);
        sub_btn = (CircleImageView) view.findViewById(R.id.sub_btn);
        product_quantity = (TextView) view.findViewById(R.id.product_quantity);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {

            case R.id.spinproduct_info:
                 product_name = namelist.get(i);
                 product_id = idlist.get(i);

         //      Toast.makeText(getActivity(), "" + product_id, Toast.LENGTH_LONG).show();
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getProductList() {


        String result = "";
        int arrayloop = 0;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
        api_response = AppUtills.callWebGetService(getActivity().getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/products");
        try {
            Log.d("requestLeaveListData", TAG + "api_response.response_code" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK) {
                result = api_response.data;
                JSONArray dailyscheduleArray = new JSONArray(result);
                Log.d("requestLeaveListData", TAG + "dailyscheduleArrayString" + dailyscheduleArray.toString());
                Log.d("requestLeaveListData", TAG + "dailyscheduleArray.length()" + dailyscheduleArray.length());

                if (dailyscheduleArray.length() > 0) {
                    for (arrayloop = 0; arrayloop < dailyscheduleArray.length(); arrayloop++) {
                        final JSONObject productobject = dailyscheduleArray.getJSONObject(arrayloop);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    namelist.add(productobject.getString("name"));
                                    imagelist.add("R.drawable.circle_buff");
                                    pricelist.add(productobject.getString("pricePerUnit"));
                                    idlist.add(productobject.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }

                    final spinnercustomadapter customAdapter = new spinnercustomadapter(getActivity().getApplicationContext(), imagelist, namelist, pricelist);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            spinproduct_info.setAdapter(customAdapter);

                        }
                    });
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
                            Toast.makeText(getActivity().getApplicationContext(), "No Product Found From Server", Toast.LENGTH_LONG).show();

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
                        Toast.makeText(getActivity().getApplicationContext(), "No Product Found From Server", Toast.LENGTH_LONG).show();

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

            Toast.makeText(getActivity().getApplicationContext(), "No Product Found From Server", Toast.LENGTH_LONG).show();

            Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
        }


    }


    public void  save_form(){

            JSONObject jsonObject = new JSONObject();
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            jsonObject.put("productQty",product_quantity.getText().toString().trim());
            jsonObject.put("startDate","2019-01-22");
            jsonObject.put("productId",product_id);
            jsonObject.put("frequency",frequency);
            Date d1 = null;
            try {
                d1 = sdformat.parse(start_date.getText().toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //   Date d2 = sdformat.parse(end_date.getText().toString().trim());


            jsonObject.put("startDate", sdformat.format(d1));
          //  jsonObject.put("endDate",sdformat.format(d2));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(jsonObject);
        Toast.makeText(getActivity().getApplicationContext(),"Saved Successfully.",Toast.LENGTH_LONG).show();

    }
public  void  send_to_server(){
    final GlobalClass globalVariable = (GlobalClass)getActivity(). getApplicationContext();
    Log.d(TAG, "send_to_server: jsonArray"+jsonArray.toString());
    JSONObject jsonObjectRoot = new JSONObject();
    try {
        jsonObjectRoot.put("root_array",jsonArray);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    new Service_Call(getActivity().getApplicationContext(), "/customer/"+globalVariable.customerid+"/subscription","PUT", jsonObjectRoot, true, new MyServiceListener() {
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
                    alertDialog.setMessage("Product subscription recorded successfully.");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, new backhome_fragment());
                            fragmentTransaction.commit();

                        }
                    });


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
                    alertDialog.setMessage("Something goes wrong.Please try agian!");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            progressDialog.dismiss();
                        }
                    });

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
                alertDialog.setMessage("Something goes wrong.Please try agian!");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        progressDialog.dismiss();
                    }
                });
            }
            alertDialog.show();
        }
    });


}
    private class MyAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


            getProductList();
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

    private class MyAsyncTaskForm extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


            send_to_server();
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
    private boolean validateForm() {

        alertDialog = new AlertDialog.Builder(getActivity());
        if (frequency.equalsIgnoreCase("")) {

            alertDialog.setMessage("PLEASE  SELECT Days");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
            return false;
        } else if (start_date.getText().toString().trim().equalsIgnoreCase("")) {

            alertDialog.setMessage("PLEASE  SELECT Start Date");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    start_date.requestFocus();
                }
            });
            alertDialog.show();
            return false;
        }else {
            return true;
        }


    }
}
