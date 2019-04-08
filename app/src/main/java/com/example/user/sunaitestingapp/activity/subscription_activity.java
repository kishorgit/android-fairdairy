package com.example.user.sunaitestingapp.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

public class subscription_activity extends AppCompatActivity {
    Toolbar toolbar;
    Button submit;
    RadioButton daily, alternate_days1, week_days, select_days;
    TextView start_date,end_date,product_name,price_per_unit;
    ImageView img_sun,img_mon,img_tue,img_wed,img_thu,img_fri,img_sat,product_image;
    LinearLayout daysContainer,linear_sun,linear_mon,linear_tue,linear_wed,linear_thu,linear_fri,linear_sat;
    CircleImageView add_btn,sub_btn;
    TextView product_quantity,save,qty_sun,qty_mon,qty_tue,qty_wed,qty_thu,qty_fri,qty_sat,
            txt_sun,txt_mon,txt_tue,txt_wed,txt_thu,txt_fri,txt_sat;
    Spinner spinproduct_info;
    String TAG = "subscription_activity";
    public ProgressDialog progressDialog = null;
    AlertDialog.Builder alertDialog;
    Handler mHandler = null;
    private Model_TLI_API_Response model_TLI_api_response;

    JSONArray jsonArray = new JSONArray();
    String  product_id;
    String frequency="";
    private int mYear, mMonth, mDay;
    int dayflag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_activity);
        findViewId();
        toolbar.setTitle(Html.fromHtml("<font  >Subscriptions</font>"+"<font color='#FFFFFF'> </font>"));
        toolbar.setTitleTextAppearance(getApplicationContext(),R.style.titletextStyle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        /////////////////
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        start_date.setText(currentdate);
        alertDialog = new AlertDialog.Builder(this);
        mHandler = new Handler();
        progressDialog = new ProgressDialog(subscription_activity.this);

        Log.d("group", TAG + "onCreateproductid" +globalVariable.productid);

        new MyAsyncTaskProd(String.valueOf(globalVariable.productid)).execute();


        product_name.setText(globalVariable.product_name);
        price_per_unit.setText("₹ "+globalVariable.pricePerUnit);


        /*     new MyAsyncTask().execute();*/
       /* save.setClickable(true);
        //   new_customer.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='test' > Add More Product</a>";
        // save.setLinkTextColor(Color.rgb(135, 208, 235));
        save.setLinkTextColor(Color.rgb(0, 208, 0));
        save.setText(Html.fromHtml(text));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    save_form();
                    Log.d(TAG, "save_form: jsonArray" + jsonArray.toString());
                }
            }
        });*/
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    if (validateForm())
                    {
                        new MyAsyncTaskForm().execute();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"No Internet Connectivity",Toast.LENGTH_LONG).show();
                }



            }
        });

        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayflag=0;
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
                dayflag=0;
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
                dayflag=0;
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
                dayflag=8;
                product_quantity.setText("0.0");
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
                if(dayflag==0){
                    product_quantity.setText(String.valueOf(Double.valueOf(product_quantity.getText().toString())+0.5));
                }
                else if(dayflag==1) {
                    qty_sun.setText(String.valueOf(Double.valueOf(qty_sun.getText().toString())+0.5));
                }
                else if(dayflag==2) {
                    qty_mon.setText(String.valueOf(Double.valueOf(qty_mon.getText().toString())+0.5));
                }
                else if(dayflag==3) {
                    qty_tue.setText(String.valueOf(Double.valueOf(qty_tue.getText().toString())+0.5));
                }
                else if(dayflag==4) {
                    qty_wed.setText(String.valueOf(Double.valueOf(qty_wed.getText().toString())+0.5));
                }
                else if(dayflag==5) {
                    qty_thu.setText(String.valueOf(Double.valueOf(qty_thu.getText().toString())+0.5));
                }
                else if(dayflag==6) {
                    qty_fri.setText(String.valueOf(Double.valueOf(qty_fri.getText().toString())+0.5));
                }
                else if(dayflag==7) {
                    qty_sat.setText(String.valueOf(Double.valueOf(qty_sat.getText().toString())+0.5));
                }

            }
        });
        sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dayflag==0){
                    if((Double.valueOf(product_quantity.getText().toString())-0.5) > 0.0) {
                        product_quantity.setText(String.valueOf(Double.valueOf(product_quantity.getText().toString()) - 0.5));
                    }
                    else {
                        product_quantity.setText("0.0");

                    }
                }
                else if(dayflag==1) {

                    if((Double.valueOf(qty_sun.getText().toString())-0.5) > 0.0) {
                        qty_sun.setText(String.valueOf(Double.valueOf(qty_sun.getText().toString()) - 0.5));
                    }
                    else {
                        qty_sun.setText("0.0");

                    }


                }
                else if(dayflag==2) {

                    if((Double.valueOf(qty_mon.getText().toString())-0.5) > 0.0) {
                        qty_mon.setText(String.valueOf(Double.valueOf(qty_mon.getText().toString()) - 0.5));
                    }
                    else {
                        qty_mon.setText("0.0");

                    }
                }
                else if(dayflag==3) {

                    if((Double.valueOf(qty_tue.getText().toString())-0.5) > 0.0) {
                        qty_tue.setText(String.valueOf(Double.valueOf(qty_tue.getText().toString()) - 0.5));
                    }
                    else {
                        qty_tue.setText("0.0");

                    }

                }
                else if(dayflag==4) {


                    if((Double.valueOf(qty_wed.getText().toString())-0.5) > 0.0) {
                        qty_wed.setText(String.valueOf(Double.valueOf(qty_wed.getText().toString()) - 0.5));
                    }
                    else {
                        qty_wed.setText("0.0");

                    }
                }
                else if(dayflag==5) {

                    if((Double.valueOf(qty_thu.getText().toString())-0.5) > 0.0) {
                        qty_thu.setText(String.valueOf(Double.valueOf(qty_thu.getText().toString()) - 0.5));
                    }
                    else {
                        qty_thu.setText("0.0");

                    }
                }
                else if(dayflag==6) {
                    qty_fri.setText(String.valueOf(Double.valueOf(qty_fri.getText().toString())+0.5));


                    if((Double.valueOf(qty_fri.getText().toString())-0.5) > 0.0) {
                        qty_fri.setText(String.valueOf(Double.valueOf(qty_fri.getText().toString()) - 0.5));
                    }
                    else {
                        qty_fri.setText("0.0");

                    }
                }
                else if(dayflag==7) {
                    if((Double.valueOf(qty_sat.getText().toString())-0.5) > 0.0) {
                        qty_sat.setText(String.valueOf(Double.valueOf(qty_sat.getText().toString()) - 0.5));
                    }
                    else {
                        qty_sat.setText("0.0");

                    }
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(subscription_activity.this,R.style.DialogTheme,
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
                               // start_date.setTextSize(12);
                                start_date.setGravity(Gravity.CENTER_HORIZONTAL);
                              //  start_date.setTextColor(getResources().getColor(R.color.black_color));

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


                DatePickerDialog datePickerDialog = new DatePickerDialog(subscription_activity.this,R.style.DialogTheme,
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
                            //    end_date.setTextSize(12);
                                end_date.setGravity(Gravity.CENTER_HORIZONTAL);
                            //    end_date.setTextColor(getResources().getColor(R.color.black_color));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //////////////////////Select Days/////////////////////////////////////
        linear_sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayflag=1;


                img_sun.setBackgroundResource(R.drawable.circle_delivered);
                img_mon.setBackgroundResource(R.drawable.circlegrey_day);
                img_tue.setBackgroundResource(R.drawable.circlegrey_day);
                img_wed.setBackgroundResource(R.drawable.circlegrey_day);
                img_thu.setBackgroundResource(R.drawable.circlegrey_day);
                img_fri.setBackgroundResource(R.drawable.circlegrey_day);
                img_sat.setBackgroundResource(R.drawable.circlegrey_day);

                //////////////////////////////////////
                qty_sun.setTextColor(getResources().getColor(R.color.green));
                qty_mon.setTextColor(getResources().getColor(R.color.black_color));
                qty_tue.setTextColor(getResources().getColor(R.color.black_color));
                qty_wed.setTextColor(getResources().getColor(R.color.black_color));
                qty_thu.setTextColor(getResources().getColor(R.color.black_color));
                qty_fri.setTextColor(getResources().getColor(R.color.black_color));
                qty_sat.setTextColor(getResources().getColor(R.color.black_color));

                //////////////////////////////////////////////////

                txt_sun.setTextColor(getResources().getColor(R.color.green));
                txt_mon.setTextColor(getResources().getColor(R.color.black_color));
                txt_tue.setTextColor(getResources().getColor(R.color.black_color));
                txt_wed.setTextColor(getResources().getColor(R.color.black_color));
                txt_thu.setTextColor(getResources().getColor(R.color.black_color));
                txt_fri.setTextColor(getResources().getColor(R.color.black_color));
                txt_sat.setTextColor(getResources().getColor(R.color.black_color));

            }
        });
        linear_mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dayflag=2;
                img_sun.setBackgroundResource(R.drawable.circlegrey_day);
                img_mon.setBackgroundResource(R.drawable.circle_delivered);
                img_tue.setBackgroundResource(R.drawable.circlegrey_day);
                img_wed.setBackgroundResource(R.drawable.circlegrey_day);
                img_thu.setBackgroundResource(R.drawable.circlegrey_day);
                img_fri.setBackgroundResource(R.drawable.circlegrey_day);
                img_sat.setBackgroundResource(R.drawable.circlegrey_day);

                //////////////////////////////////////
                qty_sun.setTextColor(getResources().getColor(R.color.black_color));
                qty_mon.setTextColor(getResources().getColor(R.color.green));
                qty_tue.setTextColor(getResources().getColor(R.color.black_color));
                qty_wed.setTextColor(getResources().getColor(R.color.black_color));
                qty_thu.setTextColor(getResources().getColor(R.color.black_color));
                qty_fri.setTextColor(getResources().getColor(R.color.black_color));
                qty_sat.setTextColor(getResources().getColor(R.color.black_color));

                //////////////////////////////////////////////////

                txt_sun.setTextColor(getResources().getColor(R.color.black_color));
                txt_mon.setTextColor(getResources().getColor(R.color.green));
                txt_tue.setTextColor(getResources().getColor(R.color.black_color));
                txt_wed.setTextColor(getResources().getColor(R.color.black_color));
                txt_thu.setTextColor(getResources().getColor(R.color.black_color));
                txt_fri.setTextColor(getResources().getColor(R.color.black_color));
                txt_sat.setTextColor(getResources().getColor(R.color.black_color));

            }
        });
        linear_tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayflag=3;
                img_sun.setBackgroundResource(R.drawable.circlegrey_day);
                img_mon.setBackgroundResource(R.drawable.circlegrey_day);
                img_tue.setBackgroundResource(R.drawable.circle_delivered);
                img_wed.setBackgroundResource(R.drawable.circlegrey_day);
                img_thu.setBackgroundResource(R.drawable.circlegrey_day);
                img_fri.setBackgroundResource(R.drawable.circlegrey_day);
                img_sat.setBackgroundResource(R.drawable.circlegrey_day);
                //////////////////////////////////////
                qty_sun.setTextColor(getResources().getColor(R.color.black_color));
                qty_mon.setTextColor(getResources().getColor(R.color.black_color));
                qty_tue.setTextColor(getResources().getColor(R.color.green));
                qty_wed.setTextColor(getResources().getColor(R.color.black_color));
                qty_thu.setTextColor(getResources().getColor(R.color.black_color));
                qty_fri.setTextColor(getResources().getColor(R.color.black_color));
                qty_sat.setTextColor(getResources().getColor(R.color.black_color));

                //////////////////////////////////////////////////

                txt_sun.setTextColor(getResources().getColor(R.color.black_color));
                txt_mon.setTextColor(getResources().getColor(R.color.black_color));
                txt_tue.setTextColor(getResources().getColor(R.color.green));
                txt_wed.setTextColor(getResources().getColor(R.color.black_color));
                txt_thu.setTextColor(getResources().getColor(R.color.black_color));
                txt_fri.setTextColor(getResources().getColor(R.color.black_color));
                txt_sat.setTextColor(getResources().getColor(R.color.black_color));


            }
        });

        linear_wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayflag=4;
                img_sun.setBackgroundResource(R.drawable.circlegrey_day);
                img_mon.setBackgroundResource(R.drawable.circlegrey_day);
                img_tue.setBackgroundResource(R.drawable.circlegrey_day);
                img_wed.setBackgroundResource(R.drawable.circle_delivered);
                img_thu.setBackgroundResource(R.drawable.circlegrey_day);
                img_fri.setBackgroundResource(R.drawable.circlegrey_day);
                img_sat.setBackgroundResource(R.drawable.circlegrey_day);
                //////////////////////////////////////
                qty_sun.setTextColor(getResources().getColor(R.color.black_color));
                qty_mon.setTextColor(getResources().getColor(R.color.black_color));
                qty_tue.setTextColor(getResources().getColor(R.color.black_color));
                qty_wed.setTextColor(getResources().getColor(R.color.green));
                qty_thu.setTextColor(getResources().getColor(R.color.black_color));
                qty_fri.setTextColor(getResources().getColor(R.color.black_color));
                qty_sat.setTextColor(getResources().getColor(R.color.black_color));

                //////////////////////////////////////////////////

                txt_sun.setTextColor(getResources().getColor(R.color.black_color));
                txt_mon.setTextColor(getResources().getColor(R.color.black_color));
                txt_tue.setTextColor(getResources().getColor(R.color.black_color));
                txt_wed.setTextColor(getResources().getColor(R.color.green));
                txt_thu.setTextColor(getResources().getColor(R.color.black_color));
                txt_fri.setTextColor(getResources().getColor(R.color.black_color));
                txt_sat.setTextColor(getResources().getColor(R.color.black_color));

            }
        });

        linear_thu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayflag=5;
                img_sun.setBackgroundResource(R.drawable.circlegrey_day);
                img_mon.setBackgroundResource(R.drawable.circlegrey_day);
                img_tue.setBackgroundResource(R.drawable.circlegrey_day);
                img_wed.setBackgroundResource(R.drawable.circlegrey_day);
                img_thu.setBackgroundResource(R.drawable.circle_delivered);
                img_fri.setBackgroundResource(R.drawable.circlegrey_day);
                img_sat.setBackgroundResource(R.drawable.circlegrey_day);
                //////////////////////////////////////
                qty_sun.setTextColor(getResources().getColor(R.color.black_color));
                qty_mon.setTextColor(getResources().getColor(R.color.black_color));
                qty_tue.setTextColor(getResources().getColor(R.color.black_color));
                qty_wed.setTextColor(getResources().getColor(R.color.black_color));
                qty_thu.setTextColor(getResources().getColor(R.color.green));
                qty_fri.setTextColor(getResources().getColor(R.color.black_color));
                qty_sat.setTextColor(getResources().getColor(R.color.black_color));

                //////////////////////////////////////////////////

                txt_sun.setTextColor(getResources().getColor(R.color.black_color));
                txt_mon.setTextColor(getResources().getColor(R.color.black_color));
                txt_tue.setTextColor(getResources().getColor(R.color.black_color));
                txt_wed.setTextColor(getResources().getColor(R.color.black_color));
                txt_thu.setTextColor(getResources().getColor(R.color.green));
                txt_fri.setTextColor(getResources().getColor(R.color.black_color));
                txt_sat.setTextColor(getResources().getColor(R.color.black_color));


            }
        });
        linear_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayflag=6;
                img_sun.setBackgroundResource(R.drawable.circlegrey_day);
                img_mon.setBackgroundResource(R.drawable.circlegrey_day);
                img_tue.setBackgroundResource(R.drawable.circlegrey_day);
                img_wed.setBackgroundResource(R.drawable.circlegrey_day);
                img_thu.setBackgroundResource(R.drawable.circlegrey_day);
                img_fri.setBackgroundResource(R.drawable.circle_delivered);
                img_sat.setBackgroundResource(R.drawable.circlegrey_day);
                //////////////////////////////////////
                qty_sun.setTextColor(getResources().getColor(R.color.black_color));
                qty_mon.setTextColor(getResources().getColor(R.color.black_color));
                qty_tue.setTextColor(getResources().getColor(R.color.black_color));
                qty_wed.setTextColor(getResources().getColor(R.color.black_color));
                qty_thu.setTextColor(getResources().getColor(R.color.black_color));
                qty_fri.setTextColor(getResources().getColor(R.color.green));
                qty_sat.setTextColor(getResources().getColor(R.color.black_color));

                //////////////////////////////////////////////////

                txt_sun.setTextColor(getResources().getColor(R.color.black_color));
                txt_mon.setTextColor(getResources().getColor(R.color.black_color));
                txt_tue.setTextColor(getResources().getColor(R.color.black_color));
                txt_wed.setTextColor(getResources().getColor(R.color.black_color));
                txt_thu.setTextColor(getResources().getColor(R.color.black_color));
                txt_fri.setTextColor(getResources().getColor(R.color.green));
                txt_sat.setTextColor(getResources().getColor(R.color.black_color));


            }
        });

        linear_sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dayflag=7;
                img_sun.setBackgroundResource(R.drawable.circlegrey_day);
                img_mon.setBackgroundResource(R.drawable.circlegrey_day);
                img_tue.setBackgroundResource(R.drawable.circlegrey_day);
                img_wed.setBackgroundResource(R.drawable.circlegrey_day);
                img_thu.setBackgroundResource(R.drawable.circlegrey_day);
                img_fri.setBackgroundResource(R.drawable.circlegrey_day);
                img_sat.setBackgroundResource(R.drawable.circle_delivered);
                //////////////////////////////////////
                qty_sun.setTextColor(getResources().getColor(R.color.black_color));
                qty_mon.setTextColor(getResources().getColor(R.color.black_color));
                qty_tue.setTextColor(getResources().getColor(R.color.black_color));
                qty_wed.setTextColor(getResources().getColor(R.color.black_color));
                qty_thu.setTextColor(getResources().getColor(R.color.black_color));
                qty_fri.setTextColor(getResources().getColor(R.color.black_color));
                qty_sat.setTextColor(getResources().getColor(R.color.green));

                //////////////////////////////////////////////////

                txt_sun.setTextColor(getResources().getColor(R.color.black_color));
                txt_mon.setTextColor(getResources().getColor(R.color.black_color));
                txt_tue.setTextColor(getResources().getColor(R.color.black_color));
                txt_wed.setTextColor(getResources().getColor(R.color.black_color));
                txt_thu.setTextColor(getResources().getColor(R.color.black_color));
                txt_fri.setTextColor(getResources().getColor(R.color.black_color));
                txt_sat.setTextColor(getResources().getColor(R.color.green));


            }
        });
        ////////////////////////elect Days///////////////////////////////////

    }

    private void findViewId() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
/*        save = (TextView) findViewById(R.id.save);*/
        product_name= (TextView) findViewById(R.id.product_name);
        price_per_unit= (TextView) findViewById(R.id.price_per_unit);



        submit  = (Button) findViewById(R.id.submit);
        daily = (RadioButton)findViewById(R.id.daily);
        alternate_days1 = (RadioButton) findViewById(R.id.alternate_days1);
        week_days = (RadioButton) findViewById(R.id.week_days);
        select_days = (RadioButton) findViewById(R.id.select_days);
        daysContainer = (LinearLayout)findViewById(R.id.daysContainer);

        start_date = (TextView) findViewById(R.id.start_date);
        end_date = (TextView) findViewById(R.id.end_date);
        add_btn = (CircleImageView)findViewById(R.id.add_btn);
        sub_btn = (CircleImageView) findViewById(R.id.sub_btn);
        product_quantity = (TextView) findViewById(R.id.product_quantity);
        img_sun = (ImageView) findViewById(R.id.img_sun);
        img_mon = (ImageView) findViewById(R.id.img_mon);
        img_tue = (ImageView) findViewById(R.id.img_tue);
        img_wed = (ImageView) findViewById(R.id.img_wed);
        img_thu = (ImageView) findViewById(R.id.img_thu);
        img_fri = (ImageView) findViewById(R.id.img_fri);
        img_sat = (ImageView) findViewById(R.id.img_sat);
        product_image = (ImageView) findViewById(R.id.product_image);
        qty_sun= (TextView) findViewById(R.id.qty_sun);
        qty_mon= (TextView) findViewById(R.id.qty_mon);
        qty_tue= (TextView) findViewById(R.id.qty_tue);
        qty_wed= (TextView) findViewById(R.id.qty_wed);
        qty_thu= (TextView) findViewById(R.id.qty_thu);
        qty_fri= (TextView) findViewById(R.id.qty_fri);
        qty_sat= (TextView) findViewById(R.id.qty_sat);


        txt_sun= (TextView) findViewById(R.id.txt_sun);
        txt_mon= (TextView) findViewById(R.id.txt_mon);
        txt_tue= (TextView) findViewById(R.id.txt_tue);
        txt_wed= (TextView) findViewById(R.id.txt_wed);
        txt_thu= (TextView) findViewById(R.id.txt_thu);
        txt_fri= (TextView) findViewById(R.id.txt_fri);
        txt_sat= (TextView) findViewById(R.id.txt_sat);


        linear_sun= (LinearLayout) findViewById(R.id.linear_sun);
        linear_mon= (LinearLayout) findViewById(R.id.linear_mon);
        linear_tue= (LinearLayout) findViewById(R.id.linear_tue);
        linear_wed= (LinearLayout) findViewById(R.id.linear_wed);
        linear_thu= (LinearLayout) findViewById(R.id.linear_thu);
        linear_fri= (LinearLayout) findViewById(R.id.linear_fri);
        linear_sat= (LinearLayout) findViewById(R.id.linear_sat);

    }




    public  void  send_to_server(){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Log.d(TAG, "send_to_server: jsonArray"+jsonArray.toString());
        JSONObject jsonObjectRoot = null;
      /*  if(jsonArray.length()>0) {
            jsonObjectRoot = new JSONObject();
            try {
                jsonObjectRoot.put("root_array", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
      //  else {
            JSONArray jsonArrayone = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if(dayflag==0) {
                    jsonObject.put("productQty", product_quantity.getText().toString().trim());
                }
                jsonObject.put("productId",globalVariable.productid);
                jsonObject.put("frequency",frequency);
                jsonObject.put("sundayQty",qty_sun.getText().toString().trim());
                jsonObject.put("mondayQty",qty_mon.getText().toString().trim());
                jsonObject.put("tuesdayQty",qty_tue.getText().toString().trim());
                jsonObject.put("wednesdayQty",qty_wed.getText().toString().trim());
                jsonObject.put("thursdayQty",qty_thu.getText().toString().trim());
                jsonObject.put("fridayQty",qty_fri.getText().toString().trim());
                jsonObject.put("saturdayQty",qty_sat.getText().toString().trim());

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

            jsonArrayone.put(jsonObject);
            jsonObjectRoot = new JSONObject();
            try {
                jsonObjectRoot.put("root_array", jsonArrayone);
            } catch (JSONException e) {
                e.printStackTrace();
            }


     //   }

        new Service_Call(getApplicationContext(), "/customer/"+globalVariable.customerid+"/subscription","PUT", jsonObjectRoot, true, new MyServiceListener() {
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

                                Intent intent = new Intent(getApplicationContext(),backhome_activity.class);
                                startActivity(intent);
                                finish();

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

        alertDialog = new AlertDialog.Builder(this);
        if (frequency.equalsIgnoreCase("")) {

            alertDialog.setMessage("PLEASE  SELECT Days");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
            return false;
        } else if ((start_date.getText().toString().trim().equalsIgnoreCase(""))||(start_date.getText().toString().trim().equalsIgnoreCase(">"))) {

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

    private class MyAsyncTaskProd extends AsyncTask {

        String pid;

        public MyAsyncTaskProd(String pid) {
            this.pid = pid;

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            requestLeaveListData(Integer.parseInt(pid));

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

    private void requestLeaveListData(int pid) {

        String result = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Double   totalactualDeliver=0.0;
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
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            product_name.setText(pname);
                            price_per_unit.setText("₹ "+pprice);

                            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

                            if (startDate.equals(null)) {
                            } else {
                                Date d1 = null;
                                try {
                                    d1 = sdformat.parse(startDate);
                                    start_date.setText(sdformat.format(d1));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }

                            if (frqncy.equalsIgnoreCase("DAILY")) {
                                dayflag = 0;
                                product_quantity.setText(String.valueOf(productQty));
                                daily.setChecked(true);
                                alternate_days1.setChecked(false);
                                select_days.setChecked(false);
                                week_days.setChecked(false);
                                daysContainer.setVisibility(View.GONE);
                                frequency = "DAILY";
                            } else if (frqncy.equalsIgnoreCase("ALTERNATE_DAYS")) {
                                dayflag = 0;
                                product_quantity.setText(String.valueOf(productQty));
                                daily.setChecked(false);
                                alternate_days1.setChecked(true);
                                select_days.setChecked(false);
                                week_days.setChecked(false);
                                daysContainer.setVisibility(View.GONE);
                                frequency = "ALTERNATE_DAYS";

                            } else if (frqncy.equalsIgnoreCase("week_days")) {
                                dayflag = 0;
                                product_quantity.setText(String.valueOf(productQty));
                                daily.setChecked(false);
                                alternate_days1.setChecked(false);
                                week_days.setChecked(true);
                                select_days.setChecked(false);
                                daysContainer.setVisibility(View.GONE);
                                frequency = "week_days";

                            } else if (frqncy.equalsIgnoreCase("select_days")) {
                                daily.setChecked(false);
                                alternate_days1.setChecked(false);
                                week_days.setChecked(false);
                                select_days.setChecked(true);
                                daysContainer.setVisibility(View.VISIBLE);
                                qty_sun.setText(String.valueOf(sundayQty));
                                qty_mon.setText(String.valueOf(mondayQty));
                                qty_tue.setText(String.valueOf(tuesdayQty));
                                qty_wed.setText(String.valueOf(wednesdayQty));
                                qty_thu.setText(String.valueOf(thursdayQty));
                                qty_fri.setText(String.valueOf(fridayQty));
                                qty_sat.setText(String.valueOf(saturdayQty));
                                frequency = "select_days";
                            } else {

                            }
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
                        ///////Refresh Form////////////////
                        daily.setChecked(false);
                        alternate_days1.setChecked(false);
                        select_days.setChecked(false);
                        week_days.setChecked(false);
                        daysContainer.setVisibility(View.GONE);
                        product_quantity.setText("0.0");
                        product_quantity.setText("0.0");
                        qty_sun.setText("0.0");
                        qty_mon.setText("0.0");
                        qty_tue.setText("0.0");
                        qty_wed.setText("0.0");
                        qty_thu.setText("0.0");
                        qty_fri.setText("0.0");
                        qty_sat.setText("0.0");
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String currentdate = sdf.format(c.getTime());
                        start_date.setText(currentdate);
                        end_date.setText("");
                        ///////Refresh Form////////////////
                    }
                });

            }
        } catch (Exception ex) {
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    ///////Refresh Form////////////////
                    daily.setChecked(false);
                    alternate_days1.setChecked(false);
                    select_days.setChecked(false);
                    week_days.setChecked(false);
                    daysContainer.setVisibility(View.GONE);
                    product_quantity.setText("0.0");
                    product_quantity.setText("0.0");
                    qty_sun.setText("0.0");
                    qty_mon.setText("0.0");
                    qty_tue.setText("0.0");
                    qty_wed.setText("0.0");
                    qty_thu.setText("0.0");
                    qty_fri.setText("0.0");
                    qty_sat.setText("0.0");

                    start_date.setText(currentdate);
                    end_date.setText("");
                    ///////Refresh Form////////////////
                }
            } catch (Exception e) {
                Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + e);
            }
            //   Toast.makeText(getActivity().getApplicationContext()(), "Something goes wrong please try again!", Toast.LENGTH_LONG).show();
            Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
        }
        /////////////////////////////////////////////////////////

    }

    @Override
    protected void onResume() {
        super.onResume();
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Log.d("group", TAG + "onResumeproductid" +globalVariable.productid);
        new MyAsyncTaskProd(String.valueOf(globalVariable.productid)).execute();
    }
    @Override
    public void onBackPressed() {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Intent intent =null;
      if( globalVariable.globalsubscriptflag==1) {
          intent = new Intent(getApplicationContext(), productsublist_activity.class);
      }
      else {
          intent = new Intent(getApplicationContext(), productlist_activity.class);

      }

        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
